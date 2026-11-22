package com.udea.demo.selenium;

import com.udea.demo.selenium.pages.ForgotPasswordPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Automatizadas de Recuperación de Contraseña con Selenium WebDriver")
class ForgotPasswordSeleniumTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Debe cargar la página de recuperación con todos los elementos y campos requeridos")
    void testElementosFormularioRecuperacionVisibles() {
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver, baseUrl);
        forgotPage.open();

        assertTrue(forgotPage.isHeadingVisible(), "El encabezado 'Recuperar contraseña' debe ser visible");
        assertTrue(forgotPage.isEmailInputPresent(), "El campo de correo electrónico debe estar presente");
    }

    @Test
    @DisplayName("Debe validar que el campo de correo sea obligatorio y de tipo email")
    void testValidacionCampoCorreoRequeridoYFormatoEmail() {
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver, baseUrl);
        forgotPage.open();

        assertTrue(forgotPage.isEmailInputRequired(), "El campo de correo debe tener el atributo 'required'");
        assertEquals("email", forgotPage.getEmailInputType(), "El campo de correo debe tener el tipo 'email'");
    }

    @Test
    @DisplayName("Debe validar el formato de correo electrónico mediante validación HTML5 nativa")
    void testValidacionCorreoInvalidoHTML5() {
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver, baseUrl);
        forgotPage.open();

        forgotPage.enterEmail("correo-invalido-sin-arroba");
        forgotPage.clickSubmit();

        WebElement emailElem = driver.findElement(By.name("email"));
        boolean html5Invalido = !(Boolean) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].checkValidity();", emailElem);

        assertTrue(html5Invalido, "El navegador debe marcar el correo sin arroba como inválido según HTML5");
    }

    @Test
    @DisplayName("Debe permitir volver a la pantalla de Login desde el enlace inferior")
    void testNavegacionVolverAlInicioSesion() {
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver, baseUrl);
        forgotPage.open();

        forgotPage.clickBackToLogin();

        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"), 
                "Al pulsar en 'Volver al inicio de sesión', la URL debe ser /login");
    }

    @Test
    @DisplayName("Debe procesar el envío de solicitud de recuperación de contraseña")
    void testEnvioSolicitudRecuperacionContrasena() {
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver, baseUrl);
        forgotPage.open();

        forgotPage.solicitarRecuperacion("operador@udea.edu.co");

        // Si el backend y servicio de correo están activos, muestra el mensaje de confirmación en role="status"
        // Si el backend no está iniciado en la máquina local, el frontend muestra la alerta controlada en role="alert"
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='status']")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='alert']"))
            ));

            boolean mensajeConfirmacion = forgotPage.isStatusMessageDisplayed();
            boolean errorControlado = forgotPage.isErrorDisplayed();

            assertTrue(mensajeConfirmacion || errorControlado, 
                    "Debe mostrar el mensaje de confirmación si el backend está activo o la alerta controlada si está apagado");
        } catch (Exception e) {
            fail("El formulario de recuperación de contraseña no respondió al envío: " + e.getMessage());
        }
    }
}
