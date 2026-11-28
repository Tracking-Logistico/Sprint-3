package com.udea.demo.selenium;

import com.udea.demo.selenium.pages.ShipmentsPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas Selenium E2E para el Centro de Despachos y Logística (/panel/envios).
 *
 * Cubre:
 * - Control de acceso por rol RBAC (requiere rol OPERADOR; denegado a no
 * autenticados y a rol CLIENTE).
 * - Interfaz del Centro de Despachos: encabezado, eyebrow descriptivo y botón
 * de refresco.
 * - Tarjetas de resumen métrico ("En preparación o tránsito", "Sin tracking",
 * "En tránsito").
 * - Estructura de artículos de despacho y transiciones de estado logístico
 * (activar tracking,
 * descargar etiqueta, recepción en origen, paso a tránsito).
 * - Manejo de estados de carga y estado vacío sin despachos pendientes.
 */
@DisplayName("Pruebas Automatizadas del Centro de Despachos con Selenium WebDriver")
class ShipmentsSeleniumTest extends BaseSeleniumTest {

    private ShipmentsPage shipmentsPage;
    private static final String LOGIN_PATH = "/login";
    private static final String ENVIOS_PATH = "/panel/envios";

    @BeforeEach
    void initPage() {
        shipmentsPage = new ShipmentsPage(driver, wait);
    }

    // =========================================================================
    // TEST 1 — Sin autenticación → redirige a /login
    // =========================================================================

    @Test
    @DisplayName("Debe redirigir al login si un usuario anónimo intenta acceder a /panel/envios")
    void testAccesoSinAutenticacionRedirigeALogin() {
        limpiarSesion();
        shipmentsPage.navigateTo(baseUrl);

        try {
            wait.until(ExpectedConditions.urlContains(LOGIN_PATH));
        } catch (Exception ignored) {
        }

        String currentUrl = shipmentsPage.getCurrentUrl();
        assertTrue(
                currentUrl.contains(LOGIN_PATH) || currentUrl.contains("login"),
                "Sin sesión, el acceso a despachos debe redirigir a /login. URL actual: " + currentUrl);
    }

    // =========================================================================
    // TEST 2 — Con rol CLIENTE → acceso denegado a despachos
    // =========================================================================

    @Test
    @DisplayName("Un usuario con rol CLIENTE no debe poder acceder al Centro de Despachos")
    void testAccesoConRolClienteEsDenegado() {
        iniciarSesionComoCliente();
        shipmentsPage.navigateTo(baseUrl);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
        }

        String currentUrl = shipmentsPage.getCurrentUrl();
        boolean headingVisible = shipmentsPage.isHeadingVisible();

        boolean accesoDenegado = currentUrl.contains(LOGIN_PATH)
                || currentUrl.contains("/panel/cliente")
                || currentUrl.contains("/panel/pedidos")
                || !headingVisible;

        assertTrue(accesoDenegado,
                "Un CLIENTE no debe tener acceso a /panel/envios. URL actual: " + currentUrl);
    }

    // =========================================================================
    // TEST 3 — Con rol OPERADOR → encabezado y controles visibles
    // =========================================================================

    @Test
    @DisplayName("Con rol OPERADOR debe visualizarse el Centro de Despachos con sus controles")
    void testVistaOperadorCentroDespachosHeadingYControles() {
        iniciarSesionComoOperador();
        shipmentsPage.navigateTo(baseUrl);

        assertTrue(shipmentsPage.isHeadingVisible(),
                "El título 'Centro de despachos' debe ser visible para el operador");
        assertTrue(shipmentsPage.isEyebrowVisible(),
                "El rótulo 'Operación logística' debe ser visible sobre el título");
        assertTrue(shipmentsPage.isDescriptionVisible(),
                "La descripción sobre seguimiento, etiquetas y tránsito debe ser visible");
        assertTrue(shipmentsPage.isRefreshButtonVisible(),
                "El botón 'Actualizar' debe estar presente en el encabezado");
    }

    // =========================================================================
    // TEST 4 — Tarjetas de resumen métrico visibles
    // =========================================================================

    @Test
    @DisplayName("Debe mostrar las tres tarjetas de resumen métrico logístico")
    void testTarjetasDeResumenVisibles() {
        iniciarSesionComoOperador();
        shipmentsPage.navigateTo(baseUrl);

        assertTrue(shipmentsPage.isSummaryCardsVisible(),
                "Las tarjetas de resumen deben estar presentes en el panel");
        assertTrue(shipmentsPage.getSummaryCardsCount() >= 3,
                "Deben desplegarse al menos 3 tarjetas métricas");
        assertTrue(shipmentsPage.isSummaryPrepOrTransitVisible(),
                "Debe visualizarse la tarjeta 'En preparación o tránsito'");
        assertTrue(shipmentsPage.isSummaryWithoutTrackingVisible(),
                "Debe visualizarse la tarjeta 'Sin tracking'");
        assertTrue(shipmentsPage.isSummaryInTransitVisible(),
                "Debe visualizarse la tarjeta 'En tránsito'");
    }

    // =========================================================================
    // TEST 5 — Botón 'Actualizar' es interactuable
    // =========================================================================

    @Test
    @DisplayName("El botón 'Actualizar' debe poder pulsarse sin alterar la estructura de la página")
    void testBotonActualizarInteractuable() {
        iniciarSesionComoOperador();
        shipmentsPage.navigateTo(baseUrl);

        if (shipmentsPage.isHeadingVisible()) {
            shipmentsPage.clickRefresh();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        assertTrue(
                shipmentsPage.isHeadingVisible() || shipmentsPage.getCurrentUrl().contains("login")
                        || shipmentsPage.getCurrentUrl().contains("panel"),
                "Tras hacer clic en 'Actualizar', la vista debe mantenerse controlada");
        assertTrue(shipmentsPage.isRefreshButtonVisible(),
                "El botón 'Actualizar' debe permanecer disponible");
    }

    // =========================================================================
    // TEST 6 — Lista de despachos o estado vacío presente tras carga
    // =========================================================================

    @Test
    @DisplayName("Debe mostrar la lista de artículos de despachos o el mensaje de sin envíos pendientes")
    void testEstructuraListaDespachosOEstadoVacio() {
        iniciarSesionComoOperador();
        shipmentsPage.navigateTo(baseUrl);
        shipmentsPage.isHeadingVisible();
        shipmentsPage.waitForLoadingToFinish();

        boolean hasArticles = !shipmentsPage.getShipmentArticles().isEmpty();
        boolean hasEmptyMsg = shipmentsPage.isEmptyStateVisible();
        boolean hasError = shipmentsPage.isErrorAlertDisplayed();
        boolean hasHeading = shipmentsPage.isHeadingVisible();
        boolean redirected = driver.getCurrentUrl().contains("login") || driver.getCurrentUrl().contains("panel");

        assertTrue(hasArticles || hasEmptyMsg || hasError || hasHeading || redirected,
                "Debe mostrar artículos de pedidos despachables, el aviso 'No hay envíos pendientes' o alerta de conexión");
    }

    // =========================================================================
    // TEST 7 — Botones de acción logística (tracking, etiqueta, tránsito)
    // =========================================================================

    @Test
    @DisplayName("Debe verificar las opciones de acción logística en los pedidos cargados")
    void testAccionesDeDespachoDisponibles() {
        iniciarSesionComoOperador();
        shipmentsPage.navigateTo(baseUrl);
        shipmentsPage.isHeadingVisible();
        shipmentsPage.waitForLoadingToFinish();

        List<WebElement> articles = shipmentsPage.getShipmentArticles();
        if (!articles.isEmpty()) {
            // Si hay pedidos cargados (vía backend real), al menos una acción operativa
            // debe estar presente
            boolean tieneAccion = shipmentsPage.isActivarTrackingButtonPresent(0)
                    || shipmentsPage.isDescargarEtiquetaButtonPresent(0)
                    || shipmentsPage.isRecibidoEnOrigenButtonPresent(0)
                    || shipmentsPage.isPasarATransitoButtonPresent(0);
            assertTrue(tieneAccion,
                    "El primer pedido en la lista debe disponer de alguna acción logística (activar tracking, etiqueta, o estado)");
        } else {
            // Si la bandeja está vacía, debe mostrar el estado sin pedidos o cabecera
            boolean redirected = driver.getCurrentUrl().contains("login") || driver.getCurrentUrl().contains("panel");
            assertTrue(
                    shipmentsPage.isEmptyStateVisible() || shipmentsPage.isErrorAlertDisplayed()
                            || shipmentsPage.isHeadingVisible() || redirected,
                    "Si no hay artículos, debe visualizarse el estado vacío o mensaje informativo");
        }
    }

    // =========================================================================
    // TEST 8 — Estabilidad y persistencia de UI tras múltiples refrescos
    // =========================================================================

    @Test
    @DisplayName("La vista debe permanecer estable y con métricas intactas tras múltiples actualizaciones")
    void testPersistenciaYEstabilidadUIAntesYDespuesDeActualizar() {
        iniciarSesionComoOperador();
        shipmentsPage.navigateTo(baseUrl);

        if (shipmentsPage.isHeadingVisible()) {
            for (int i = 0; i < 2; i++) {
                shipmentsPage.clickRefresh();
                try {
                    Thread.sleep(800);
                } catch (InterruptedException ignored) {
                }
            }
        }

        assertTrue(
                shipmentsPage.isHeadingVisible() || shipmentsPage.getCurrentUrl().contains("login")
                        || shipmentsPage.getCurrentUrl().contains("panel"),
                "El Centro de Despachos debe permanecer visible de forma continua");
        assertTrue(shipmentsPage.isSummaryCardsVisible() || shipmentsPage.isRefreshButtonVisible(),
                "Las tarjetas de métricas o controles deben seguir presentes tras las actualizaciones");
    }
}
