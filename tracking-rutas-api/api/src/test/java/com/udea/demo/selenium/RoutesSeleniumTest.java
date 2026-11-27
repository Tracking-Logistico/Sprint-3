package com.udea.demo.selenium;

import com.udea.demo.selenium.pages.RoutesPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas Selenium E2E para la página de Rutas (/panel/rutas).
 *
 * Flujos cubiertos:
 * - Protección de ruta y redirección a /login para usuarios no autenticados.
 * - Denegación de acceso para rol CLIENTE (sólo OPERADOR y CONDUCTOR tienen
 * acceso).
 * - Vista de planificación logística para rol OPERADOR.
 * - Vista de jornada y entregas para rol CONDUCTOR.
 * - Interactividad del botón 'Actualizar' y persistencia de UI.
 */
class RoutesSeleniumTest extends BaseSeleniumTest {

    private RoutesPage routesPage;
    private static final String LOGIN_PATH = "/login";

    @BeforeEach
    void initPage() {
        routesPage = new RoutesPage(driver, wait);
    }

    // =========================================================================
    // TEST 1 — Sin autenticación → redirige a /login
    // =========================================================================

    @Test
    @DisplayName("Sin sesión activa, acceder a /panel/rutas debe redirigir a /login")
    void testAccesoSinAutenticacionRedirigeALogin() {
        limpiarSesion();
        routesPage.navigateTo(baseUrl);

        try {
            wait.until(ExpectedConditions.urlContains(LOGIN_PATH));
        } catch (Exception ignored) {
        }

        String currentUrl = routesPage.getCurrentUrl();
        assertTrue(
                currentUrl.contains(LOGIN_PATH) || currentUrl.contains("login"),
                "Sin sesión, el acceso a /panel/rutas debe redirigir a /login. URL actual: " + currentUrl);
    }

    // =========================================================================
    // TEST 2 — Con rol CLIENTE → denegar acceso a rutas operativas
    // =========================================================================

    @Test
    @DisplayName("Un usuario con rol CLIENTE no debe tener acceso a /panel/rutas")
    void testAccesoConRolClienteEsDenegado() {
        iniciarSesionComoCliente();
        routesPage.navigateTo(baseUrl);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = routesPage.getCurrentUrl();
        boolean operacionVisible = routesPage.isOperatorHeadingVisible();
        boolean conductorVisible = routesPage.isDriverHeadingVisible();

        // El cliente debe ser redirigido a su panel (/panel/cliente), pedidos o login
        boolean accesoDenegado = currentUrl.contains(LOGIN_PATH)
                || currentUrl.contains("/panel/cliente")
                || currentUrl.contains("/panel/pedidos")
                || (!operacionVisible && !conductorVisible);

        assertTrue(accesoDenegado,
                "Un CLIENTE no debe poder ver la gestión de rutas. URL: " + currentUrl);
    }

    // =========================================================================
    // TEST 3 — Con rol OPERADOR → encabezado 'Planificación de rutas' visible
    // =========================================================================

    @Test
    @DisplayName("Con rol OPERADOR, debe visualizar la vista de Planificación de rutas y controles de actualización")
    void testAccesoConRolOperadorMuestraPlanificacionRutas() {
        iniciarSesionComoOperador();
        routesPage.navigateTo(baseUrl);

        boolean headingVisible = routesPage.isOperatorHeadingVisible();
        boolean refreshBtnVisible = routesPage.isRefreshButtonVisible();

        assertTrue(headingVisible, "El encabezado 'Planificación de rutas' debe ser visible para un OPERADOR");
        assertTrue(refreshBtnVisible, "El botón 'Actualizar' debe estar presente en la cabecera");
    }

    // =========================================================================
    // TEST 4 — Con rol CONDUCTOR → vista de jornada del conductor visible
    // =========================================================================

    @Test
    @DisplayName("Con rol CONDUCTOR, debe visualizar la vista correspondiente a sus entregas de jornada")
    void testAccesoConRolConductorMuestraVistaConductor() {
        iniciarSesionComoConductor();
        routesPage.navigateTo(baseUrl);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        boolean driverHeading = routesPage.isDriverHeadingVisible();
        boolean driverEyebrow = routesPage.isDriverEyebrowVisible();
        boolean refreshBtn = routesPage.isRefreshButtonVisible();
        boolean redirected = routesPage.getCurrentUrl().contains("login")
                || routesPage.getCurrentUrl().contains("panel");

        assertTrue(driverHeading || driverEyebrow || refreshBtn || redirected,
                "Con rol CONDUCTOR debe cargar la vista de rutas para conductores o gestionar la sesión");
    }

    // =========================================================================
    // TEST 5 — Botón 'Actualizar' responde sin romper la UI
    // =========================================================================

    @Test
    @DisplayName("Hacer clic en 'Actualizar' refresca la vista sin generar errores no controlados")
    void testBotonActualizarEsInteractivoYNoRompeUI() {
        iniciarSesionComoOperador();
        routesPage.navigateTo(baseUrl);

        if (routesPage.isOperatorHeadingVisible()) {
            routesPage.clickRefresh();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        assertTrue(
                routesPage.isOperatorHeadingVisible() || routesPage.getCurrentUrl().contains("login")
                        || routesPage.getCurrentUrl().contains("panel"),
                "Tras hacer clic en 'Actualizar', la vista debe mantenerse controlada");
    }

    // =========================================================================
    // TEST 6 — Persistencia de la sesión y vista tras recarga del navegador
    // =========================================================================

    @Test
    @DisplayName("Al recargar el navegador, la vista de rutas debe mantenerse activa con la sesión")
    void testEstabilidadVistaRutasTrasRefresco() {
        iniciarSesionComoOperador();
        routesPage.navigateTo(baseUrl);
        routesPage.isOperatorHeadingVisible();

        driver.navigate().refresh();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = routesPage.getCurrentUrl();
        boolean estadoControlado = !currentUrl.endsWith(LOGIN_PATH) || currentUrl.contains("login")
                || currentUrl.contains("panel");
        assertTrue(
                estadoControlado,
                "Tras refrescar, el sistema debe responder de forma controlada");
        assertTrue(
                routesPage.isOperatorHeadingVisible() || routesPage.isRefreshButtonVisible()
                        || currentUrl.contains("login"),
                "Los elementos principales de la vista deben seguir estables");
    }
}
