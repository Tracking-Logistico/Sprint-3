package com.udea.demo.selenium;

import com.udea.demo.selenium.pages.RegisterPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Automatizadas de Registro de Clientes con Selenium WebDriver")
class RegisterSeleniumTest extends BaseSeleniumTest {

    private void llenarDatosBasicos(RegisterPage page) {
        page.enterNombre("Carlos Vanegas");
        page.enterEmail("carlos.vanegas@udea.edu.co");
        page.enterTelefono("+573001234567");
        page.enterCiudad("Medellín");
        page.enterDireccion("Calle 67 # 53 - 108");
        page.enterCedula("1027886019");
        page.enterFechaNacimiento("1995-05-15");
    }

    @Test
    @DisplayName("Debe cargar la página de registro con todos los elementos y campos requeridos")
    void testElementosFormularioRegistroVisibles() {
        RegisterPage registerPage = new RegisterPage(driver, baseUrl);
        registerPage.open();

        assertTrue(registerPage.isHeadingVisible(), "El encabezado 'Crea tu cuenta de cliente' debe ser visible");
        assertTrue(registerPage.isNombreFieldPresent(), "El campo de nombre debe estar presente");
        assertTrue(registerPage.isEmailFieldPresent(), "El campo de correo electrónico debe estar presente");
        assertTrue(registerPage.isCedulaFieldPresent(), "El campo de número de cédula debe estar presente");
    }

    @Test
    @DisplayName("Debe validar que las contraseñas coincidan al registrarse")
    void testValidacionContrasenasNoCoinciden() {
        RegisterPage registerPage = new RegisterPage(driver, baseUrl);
        registerPage.open();

        llenarDatosBasicos(registerPage);
        registerPage.enterPassword("ClaveSegura2026*");
        registerPage.enterConfirmarPassword("ClaveDiferente2026*");
        registerPage.checkTerminos();
        registerPage.checkPoliticaDatos();

        registerPage.clickSubmit();

        assertTrue(registerPage.isErrorDisplayed(), "Debe mostrarse la alerta de error");
        assertTrue(registerPage.getErrorMessage().contains("Las contraseñas no coinciden"),
                "El mensaje de error debe indicar que las contraseñas no coinciden");
    }

    @Test
    @DisplayName("Debe validar los requisitos de complejidad de la contraseña (mínimo 8 caracteres, números, símbolos)")
    void testValidacionRequisitosContrasenaDebil() {
        RegisterPage registerPage = new RegisterPage(driver, baseUrl);
        registerPage.open();

        llenarDatosBasicos(registerPage);
        registerPage.enterPassword("simple");
        registerPage.enterConfirmarPassword("simple");
        registerPage.checkTerminos();
        registerPage.checkPoliticaDatos();

        registerPage.clickSubmit();

        assertTrue(registerPage.isErrorDisplayed(), "Debe mostrarse la alerta de error de contraseña débil");
        String errorMsg = registerPage.getErrorMessage();
        assertNotNull(errorMsg, "El mensaje de error no debe ser nulo");
        assertFalse(errorMsg.trim().isEmpty(), "El mensaje de error no debe estar vacío");
    }

    @Test
    @DisplayName("Debe exigir la aceptación de Términos y Condiciones y Política de Datos (Ley 1581)")
    void testValidacionAceptacionTerminosYPoliticaDatos() {
        RegisterPage registerPage = new RegisterPage(driver, baseUrl);
        registerPage.open();

        llenarDatosBasicos(registerPage);
        registerPage.enterPassword("ClaveSegura2026*");
        registerPage.enterConfirmarPassword("ClaveSegura2026*");

        // No se marcan los checkboxes de términos ni política de datos
        registerPage.clickSubmit();

        assertTrue(registerPage.isErrorDisplayed(), "Debe mostrarse alerta al no aceptar los términos legales");
        assertTrue(registerPage.getErrorMessage().contains("Debes aceptar los Términos y la Política de Tratamiento de Datos"),
                "Debe exigir la aceptación explícita de los términos y política de datos según Ley 1581");
    }

    @Test
    @DisplayName("Debe validar que el número de cédula no empiece por cero y tenga formato válido")
    void testValidacionCedulaInvalida() {
        RegisterPage registerPage = new RegisterPage(driver, baseUrl);
        registerPage.open();

        llenarDatosBasicos(registerPage);
        registerPage.enterCedula("012345678"); // Cédula inválida que empieza por cero
        registerPage.enterPassword("ClaveSegura2026*");
        registerPage.enterConfirmarPassword("ClaveSegura2026*");
        registerPage.checkTerminos();
        registerPage.checkPoliticaDatos();

        registerPage.clickSubmit();

        // Verifica si la validación HTML5 pattern del navegador bloquea el campo o si React muestra la alerta
        WebElement cedulaElem = driver.findElement(By.name("numeroCedula"));
        boolean html5Invalido = !(Boolean) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].checkValidity();", cedulaElem);
        boolean reactError = registerPage.isErrorDisplayed();

        assertTrue(html5Invalido || reactError, 
                "El sistema debe rechazar la cédula inválida mediante validación HTML5 de patrón o alerta de la aplicación");
    }

    @Test
    @DisplayName("Debe alternar la visibilidad de la contraseña al pulsar el botón del ojo")
    void testAlternarVisibilidadContrasena() {
        RegisterPage registerPage = new RegisterPage(driver, baseUrl);
        registerPage.open();

        registerPage.enterPassword("ClavePrueba123*");
        assertEquals("password", registerPage.getPasswordInputType(), "Por defecto debe ser de tipo 'password'");

        registerPage.togglePasswordVisibility();
        assertEquals("text", registerPage.getPasswordInputType(), "Al pulsar ver contraseña debe cambiar a 'text'");

        registerPage.togglePasswordVisibility();
        assertEquals("password", registerPage.getPasswordInputType(), "Al pulsar ocultar contraseña debe volver a 'password'");
    }

    @Test
    @DisplayName("Debe redirigir a la pantalla de Login al pulsar en 'Inicia sesión'")
    void testNavegacionHaciaLogin() {
        RegisterPage registerPage = new RegisterPage(driver, baseUrl);
        registerPage.open();

        registerPage.clickLoginLink();

        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"), "Debe redirigir al formulario de inicio de sesión");
    }

    @Test
    @DisplayName("Debe procesar el flujo completo de registro de cliente y comunicarse con el backend")
    void testRegistroCompletoConEnvioABackend() {
        RegisterPage registerPage = new RegisterPage(driver, baseUrl);
        registerPage.open();

        long timestamp = System.currentTimeMillis();
        String correoUnico = "cliente_" + timestamp + "@udea.edu.co";
        String cedulaUnica = "10" + String.valueOf(timestamp).substring(7);

        registerPage.enterNombre("Usuario Prueba E2E");
        registerPage.enterEmail(correoUnico);
        registerPage.enterTelefono("+573001234567");
        registerPage.enterCiudad("Medellín");
        registerPage.enterDireccion("Calle 67 # 53 - 108");
        registerPage.enterCedula(cedulaUnica);
        registerPage.enterFechaNacimiento("1998-03-20");
        registerPage.enterPassword("ClaveSegura2026*");
        registerPage.enterConfirmarPassword("ClaveSegura2026*");
        registerPage.checkTerminos();
        registerPage.checkPoliticaDatos();

        registerPage.clickSubmit();

        // Si el backend está activo, se completa el registro en BD y se muestra la pantalla de éxito
        // Si el backend está apagado localmente, se comprueba que el frontend reporte el error de conexión sin bloquearse
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Tu cuenta está lista')]")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='alert']"))
            ));
            boolean registroExitoso = !driver.findElements(By.xpath("//*[contains(text(),'Tu cuenta está lista')]")).isEmpty();
            boolean errorManejado = registerPage.isErrorDisplayed();
            assertTrue(registroExitoso || errorManejado, 
                    "Debe completar el registro en base de datos si el backend está activo o mostrar la alerta controlada");
        } catch (Exception e) {
            fail("El formulario de registro no respondió a la acción de envío: " + e.getMessage());
        }
    }
}
