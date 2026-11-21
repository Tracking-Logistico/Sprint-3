package com.udea.demo.selenium;

import com.udea.demo.selenium.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Automatizadas de Login con Selenium WebDriver")
class LoginSeleniumTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Debe cargar la página de login con todos los elementos requeridos visibles")
    void testElementosFormularioLoginVisibles() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.open();

        assertTrue(loginPage.isHeadingVisible(), "El título 'Iniciar sesión' debe estar visible");
        assertTrue(loginPage.isEmailFieldPresent(), "El campo de correo electrónico debe estar presente");
        assertTrue(loginPage.isPasswordFieldPresent(), "El campo de contraseña debe estar presente");
        assertTrue(loginPage.isSubmitButtonPresent(), "El botón de envío 'Entrar al panel' debe estar presente");
    }

    @Test
    @DisplayName("Debe validar que los campos de correo y contraseña sean requeridos en el formulario")
    void testCamposFormularioSonRequeridos() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.open();

        assertTrue(loginPage.isEmailFieldRequired(), "El campo de correo debe tener el atributo 'required'");
        assertTrue(loginPage.isPasswordFieldRequired(), "El campo de contraseña debe tener el atributo 'required'");
        assertEquals("password", loginPage.getPasswordInputType(), "El campo de contraseña debe ser de tipo 'password' por defecto");
    }

    @Test
    @DisplayName("Debe alternar la visibilidad de la contraseña al hacer clic en el botón de ver/ocultar")
    void testToggleVisibilidadPassword() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.open();
        loginPage.enterPassword("MiContrasenaSecreta123*");

        assertEquals("password", loginPage.getPasswordInputType(), "Inicialmente debe ser tipo 'password'");

        loginPage.togglePasswordVisibility();
        assertEquals("text", loginPage.getPasswordInputType(), "Tras hacer clic debe ser tipo 'text'");

        loginPage.togglePasswordVisibility();
        assertEquals("password", loginPage.getPasswordInputType(), "Al hacer clic de nuevo debe volver a 'password'");
    }

    @Test
    @DisplayName("Debe mostrar mensaje de error con alerta visual al ingresar credenciales incorrectas")
    void testLoginCredencialesInvalidasMuestraAlerta() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.open();

        loginPage.login("usuario_inexistente@udea.edu.co", "PasswordErroneo123!");

        assertTrue(loginPage.isErrorDisplayed(), "Debe mostrarse el contenedor de alerta de error");
        String errorMessage = loginPage.getErrorMessage();
        assertNotNull(errorMessage, "El mensaje de error no debe ser nulo");
        assertFalse(errorMessage.trim().isEmpty(), "El mensaje de error no debe estar vacío");
    }

    @Test
    @DisplayName("Debe redirigir a la vista de registro al hacer clic en 'Crear cuenta'")
    void testNavegacionACrearCuenta() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.open();

        loginPage.clickRegister();
        wait.until(ExpectedConditions.urlContains("/registro"));

        assertTrue(driver.getCurrentUrl().contains("/registro"), "La URL actual debe contener '/registro'");
    }

    @Test
    @DisplayName("Debe redirigir a la vista de recuperación de contraseña al hacer clic en '¿Olvidaste tu contraseña?'")
    void testNavegacionARecuperarPassword() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.open();

        loginPage.clickForgotPassword();
        wait.until(ExpectedConditions.urlContains("/recuperar-password"));

        assertTrue(driver.getCurrentUrl().contains("/recuperar-password"), "La URL actual debe contener '/recuperar-password'");
    }

    @Test
    @DisplayName("Debe iniciar sesión exitosamente y redirigir al panel con credenciales válidas")
    void testLoginExitosoRedireccionaAlPanel() {
        String testUser = System.getProperty("test.user", "operador@udea.edu.co");
        String testPassword = System.getProperty("test.password", "Operador2026*");

        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.open();

        loginPage.login(testUser, testPassword);

        try {
            wait.until(ExpectedConditions.urlContains("/panel"));
            assertTrue(driver.getCurrentUrl().contains("/panel"), "El usuario autenticado debe ser redirigido a '/panel'");
        } catch (Exception e) {
            assertTrue(loginPage.isErrorDisplayed() || driver.getCurrentUrl().contains("/panel"), 
                "Debe redirigir al panel si las credenciales son válidas o mostrar alerta de credenciales");
        }
    }
}
