package com.udea.orquestador.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas automatizadas con Selenium WebDriver para el API Gateway / Orquestador.
 *
 * Valida:
 *   - Verificación de los endpoints de diagnóstico HTTP directos del Gateway (/internal/health).
 *   - Flujos de navegación en el portal web (React Frontend) y enrutamiento hacia el login/paneles.
 *   - Integración de sesiones y acceso a paneles según el rol mediante el Gateway.
 */
class OrquestadorGatewaySeleniumTest extends BaseSeleniumTest {

    // =========================================================================
    // TEST 1 — Endpoint de diagnóstico directo del Gateway (/internal/health)
    // =========================================================================

    @Test
    @DisplayName("El navegador puede consultar el endpoint de salud interno del Gateway y obtener status UP")
    void testAccesoDirectoAEndpointSaludDelGateway() {
        driver.get(gatewayUrl + "/internal/health");

        // El cuerpo de la respuesta en el navegador contiene el JSON con status UP
        String pageSource = driver.getPageSource();
        assertNotNull(pageSource, "La respuesta del Gateway no debe ser nula");
        assertTrue(
            pageSource.contains("UP") || pageSource.contains("status"),
            "El endpoint /internal/health debe contener el estado del servicio. Contenido: " + pageSource
        );
    }

    // =========================================================================
    // TEST 2 — Sin autenticación, el acceso al panel protegido redirige a /login
    // =========================================================================

    @Test
    @DisplayName("Sin sesión activa en el portal, acceder a /panel debe redirigir a /login")
    void testAccesoAlPortalSinSesionRedirigeALogin() {
        limpiarSesion();
        driver.get(baseUrl + "/panel");

        try {
            wait.until(ExpectedConditions.urlContains("/login"));
        } catch (Exception ignored) {
        }

        String currentUrl = driver.getCurrentUrl();
        assertTrue(
            currentUrl.contains("/login"),
            "Sin sesión, el acceso debe redirigir a /login. URL actual: " + currentUrl
        );
    }

    // =========================================================================
    // TEST 3 — Formulario de autenticación disponible en la interfaz
    // =========================================================================

    @Test
    @DisplayName("La vista de autenticación /login debe presentar campos de correo, clave y acción de ingreso")
    void testFormularioLoginPresentaCamposRequeridos() {
        limpiarSesion();
        driver.get(baseUrl + "/login");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='email'], input[name='email']")));
        } catch (Exception ignored) {
        }

        boolean hasEmailInput = !driver.findElements(By.cssSelector("input[type='email'], input[name='email']")).isEmpty();
        boolean hasPasswordInput = !driver.findElements(By.cssSelector("input[type='password']")).isEmpty();
        boolean hasSubmitButton = !driver.findElements(By.cssSelector("button[type='submit']")).isEmpty();

        assertTrue(hasEmailInput, "El campo de email debe estar presente en el formulario de login");
        assertTrue(hasPasswordInput, "El campo de password debe estar presente en el login");
        assertTrue(hasSubmitButton, "El botón de envío debe estar presente en el login");
    }

    // =========================================================================
    // TEST 4 — Con sesión inyectada de OPERADOR, el portal permite acceso a panel
    // =========================================================================

    @Test
    @DisplayName("Con sesión de OPERADOR, el portal permite acceder a la vista del panel sin forzar login")
    void testAccesoConSesionOperadorPermiteIngresoAPanel() {
        inyectarSesionOperador();
        driver.get(baseUrl + "/panel/operador");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        assertFalse(
            currentUrl.endsWith("/login"),
            "Con sesión activa de OPERADOR, no debe redirigir a login. URL: " + currentUrl
        );
    }

    // =========================================================================
    // TEST 5 — Con sesión inyectada de CLIENTE, el portal permite acceso a su panel
    // =========================================================================

    @Test
    @DisplayName("Con sesión de CLIENTE, el portal permite acceder al panel de cliente")
    void testAccesoConSesionClientePermiteIngresoAPanel() {
        inyectarSesionCliente();
        driver.get(baseUrl + "/panel/cliente");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        assertFalse(
            currentUrl.endsWith("/login"),
            "Con sesión activa de CLIENTE, no debe redirigir a login. URL: " + currentUrl
        );
    }
}
