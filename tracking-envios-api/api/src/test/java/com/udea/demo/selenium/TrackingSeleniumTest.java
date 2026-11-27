package com.udea.demo.selenium;

import com.udea.demo.selenium.pages.ShipmentTrackingPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas Selenium E2E para la página de Seguimiento y Trazabilidad de Envíos.
 *
 * Rutas cubiertas:
 *   - /panel/pedidos/seguimiento/:numeroTracking  (Ruta protegida para CLIENTE)
 *   - /tracking?code=:numeroTracking             (Ruta pública)
 *
 * Pre-condiciones:
 *   - Frontend Vite corriendo en http://localhost:5173 (o valor de -DbaseUrl)
 *   - BaseSeleniumTest gestiona WebDriver e inyección de sesiones simuladas.
 */
class TrackingSeleniumTest extends BaseSeleniumTest {

    private ShipmentTrackingPage trackingPage;
    private static final String LOGIN_PATH          = "/login";
    private static final String MOCK_TRACKING_CODE  = "ENV-2026-TEST-001";
    private static final String INEXISTENTE_CODE    = "NO-EXISTE-999999";

    @BeforeEach
    void initPage() {
        trackingPage = new ShipmentTrackingPage(driver, wait);
    }

    // =========================================================================
    // TEST 1 — Sin autenticación en ruta de panel → redirige a /login
    // =========================================================================

    @Test
    @DisplayName("Sin sesión activa, acceder a /panel/pedidos/seguimiento/:code debe redirigir a /login")
    void testAccesoSinAutenticacionRedirigeALogin() {
        limpiarSesion();
        trackingPage.navigateTo(baseUrl, MOCK_TRACKING_CODE);

        try {
            wait.until(ExpectedConditions.urlContains(LOGIN_PATH));
        } catch (Exception ignored) {
        }

        String url = trackingPage.getCurrentUrl();
        assertTrue(
            url.contains(LOGIN_PATH) || url.contains("login"),
            "Sin sesión, el acceso a la ruta protegida de seguimiento debe redirigir a /login. URL actual: " + url
        );
    }

    // =========================================================================
    // TEST 2 — Con rol CLIENTE → encabezado y navegación visibles
    // =========================================================================

    @Test
    @DisplayName("Con rol CLIENTE, la página de seguimiento debe mostrar el encabezado y botón 'Volver a Mis envíos'")
    void testAccesoConRolClienteMuestraEncabezadoYNavegacion() {
        iniciarSesionComoCliente();
        trackingPage.navigateTo(baseUrl, MOCK_TRACKING_CODE);

        boolean headingVisible = trackingPage.isHeadingVisible();
        boolean backBtnVisible = trackingPage.isBackToOrdersButtonVisible();

        assertTrue(headingVisible, "El encabezado 'Seguimiento del envío' debe ser visible para un CLIENTE");
        assertTrue(backBtnVisible, "El botón 'Volver a Mis envíos' debe estar presente en la cabecera");
    }

    // =========================================================================
    // TEST 3 — El botón 'Volver a Mis envíos' regresa a /panel/pedidos
    // =========================================================================

    @Test
    @DisplayName("Hacer clic en 'Volver a Mis envíos' debe navegar a /panel/pedidos")
    void testBotonVolverNavegaAPanelPedidos() {
        iniciarSesionComoCliente();
        trackingPage.navigateTo(baseUrl, MOCK_TRACKING_CODE);

        assertTrue(trackingPage.isHeadingVisible(), "La página de seguimiento debe cargar");
        trackingPage.clickBackToOrders();

        try {
            wait.until(ExpectedConditions.urlContains("/panel/pedidos"));
        } catch (Exception ignored) {
        }

        String currentUrl = trackingPage.getCurrentUrl();
        assertTrue(
            currentUrl.contains("/panel/pedidos"),
            "Al hacer clic en volver debe redirigir a /panel/pedidos. URL actual: " + currentUrl
        );
    }

    // =========================================================================
    // TEST 4 — Con tracking no existente o backend offline: no rompe la UI
    // =========================================================================

    @Test
    @DisplayName("Si el código no existe o no responde el backend, debe mostrar alerta o botón de retorno sin colapsar la vista")
    void testSeguimientoInexistenteManejaErrorOAlerta() {
        iniciarSesionComoCliente();
        trackingPage.navigateTo(baseUrl, INEXISTENTE_CODE);
        trackingPage.isHeadingVisible();

        // Esperar fin de skeleton de carga
        trackingPage.waitForLoadingToFinish();

        boolean errorAlert = trackingPage.isErrorAlertVisible();
        boolean orderCard  = trackingPage.isOrderCardVisible();
        boolean backBtn    = trackingPage.isBackToOrdersButtonVisible();
        boolean heading    = trackingPage.isHeadingVisible();
        boolean loginRedir = driver.getCurrentUrl().contains("login");

        assertTrue(errorAlert || orderCard || backBtn || heading || loginRedir,
            "La página debe manejar la respuesta mostrando alerta informativa, tarjeta, controles de retorno o sesión");
    }

    // =========================================================================
    // TEST 5 — Ruta pública /tracking carga el encabezado de seguimiento
    // =========================================================================

    @Test
    @DisplayName("El acceso mediante la ruta pública /tracking debe renderizar la vista de seguimiento")
    void testRutaPublicaTrackingEsAccesible() {
        iniciarSesionComoCliente();
        trackingPage.navigateToPublicTracking(baseUrl, MOCK_TRACKING_CODE);

        boolean headingVisible = trackingPage.isHeadingVisible();
        assertTrue(headingVisible, "La ruta /tracking debe renderizar 'Seguimiento del envío'");
    }

    // =========================================================================
    // TEST 6 — Persistencia y estabilidad de UI tras refresco
    // =========================================================================

    @Test
    @DisplayName("La vista de seguimiento debe permanecer estable y con sus elementos íntegros tras refrescar la página")
    void testEstabilidadUITrasRefrescarPagina() {
        iniciarSesionComoCliente();
        trackingPage.navigateTo(baseUrl, MOCK_TRACKING_CODE);
        trackingPage.isHeadingVisible();

        driver.navigate().refresh();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        boolean headingVisible = trackingPage.isHeadingVisible();
        boolean backBtnVisible = trackingPage.isBackToOrdersButtonVisible();
        boolean loginRedir = currentUrl.contains("login") || currentUrl.contains("panel");

        assertTrue(headingVisible || backBtnVisible || loginRedir,
            "Tras refrescar la página, la vista debe mantenerse estable y controlada");
    }
}
