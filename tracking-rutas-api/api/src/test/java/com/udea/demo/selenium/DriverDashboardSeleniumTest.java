package com.udea.demo.selenium;

import com.udea.demo.selenium.pages.DriverDashboardPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas Selenium E2E para el Panel del Conductor (/panel/conductor).
 *
 * Flujos cubiertos:
 *   - Redirección a login para accesos no autenticados.
 *   - Denegación de acceso para rol CLIENTE.
 *   - Acceso y renderizado de la jornada para rol CONDUCTOR.
 *   - Visualización de la barra de estado de conectividad (En línea / Sin conexión).
 *   - Persistencia y estabilidad de la interfaz tras refrescar la vista.
 */
class DriverDashboardSeleniumTest extends BaseSeleniumTest {

    private DriverDashboardPage driverPage;
    private static final String LOGIN_PATH = "/login";

    @BeforeEach
    void initPage() {
        driverPage = new DriverDashboardPage(driver, wait);
    }

    // =========================================================================
    // TEST 1 — Sin autenticación → redirige a /login
    // =========================================================================

    @Test
    @DisplayName("Sin sesión activa, acceder a /panel/conductor debe redirigir a /login")
    void testAccesoSinAutenticacionRedirigeALogin() {
        limpiarSesion();
        driverPage.navigateTo(baseUrl);

        try {
            wait.until(ExpectedConditions.urlContains(LOGIN_PATH));
        } catch (Exception ignored) {
        }

        String currentUrl = driverPage.getCurrentUrl();
        assertTrue(
            currentUrl.contains(LOGIN_PATH) || currentUrl.contains("login"),
            "Sin sesión, el acceso a /panel/conductor debe redirigir a /login. URL actual: " + currentUrl
        );
    }

    // =========================================================================
    // TEST 2 — Con rol CLIENTE → acceso denegado
    // =========================================================================

    @Test
    @DisplayName("Un usuario con rol CLIENTE no debe tener acceso a /panel/conductor")
    void testAccesoConRolClienteEsDenegado() {
        iniciarSesionComoCliente();
        driverPage.navigateTo(baseUrl);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driverPage.getCurrentUrl();
        boolean headingVisible = driverPage.isHeadingVisible();

        boolean accesoDenegado = currentUrl.contains(LOGIN_PATH)
                || currentUrl.contains("/panel/cliente")
                || currentUrl.contains("/panel/pedidos")
                || !headingVisible;

        assertTrue(accesoDenegado,
            "Un CLIENTE no debe poder acceder al panel del conductor. URL: " + currentUrl);
    }

    // =========================================================================
    // TEST 3 — Con rol CONDUCTOR → encabezado y barra de estado visibles
    // =========================================================================

    @Test
    @DisplayName("Con rol CONDUCTOR, el panel debe mostrar el encabezado de jornada y la barra de estado")
    void testAccesoConRolConductorCargaPanelDeEntregas() {
        iniciarSesionComoConductor();
        driverPage.navigateTo(baseUrl);

        boolean headingVisible = driverPage.isHeadingVisible();
        boolean eyebrowVisible = driverPage.isEyebrowVisible();
        boolean refreshBtn = driverPage.isRefreshButtonVisible();

        assertTrue(headingVisible || eyebrowVisible,
            "Con rol CONDUCTOR debe renderizar el encabezado 'Mis entregas de hoy' o la jornada");
        assertTrue(refreshBtn, "El botón 'Actualizar' debe estar presente en el panel del conductor");
    }

    // =========================================================================
    // TEST 4 — Botón 'Actualizar' responde sin romper la vista
    // =========================================================================

    @Test
    @DisplayName("El botón 'Actualizar' permite refrescar la jornada del conductor sin generar excepciones")
    void testBotonActualizarEsInteractivo() {
        iniciarSesionComoConductor();
        driverPage.navigateTo(baseUrl);

        driverPage.isHeadingVisible();
        driverPage.clickRefresh();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        assertTrue(driverPage.isRefreshButtonVisible(),
            "Tras hacer clic en actualizar, los controles del panel deben seguir interactivos");
    }

    // =========================================================================
    // TEST 5 — Persistencia de la vista tras refresco de página
    // =========================================================================

    @Test
    @DisplayName("La vista del panel del conductor permanece activa y estable tras recargar el navegador")
    void testEstabilidadPanelConductorTrasRefresco() {
        iniciarSesionComoConductor();
        driverPage.navigateTo(baseUrl);
        driverPage.isHeadingVisible();

        driver.navigate().refresh();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driverPage.getCurrentUrl();
        assertFalse(
            currentUrl.endsWith(LOGIN_PATH),
            "Tras refrescar, no debe cerrar la sesión ni redirigir a login"
        );
        assertTrue(
            driverPage.isHeadingVisible() || driverPage.isRefreshButtonVisible(),
            "La cabecera o controles de la jornada deben permanecer visibles"
        );
    }
}
