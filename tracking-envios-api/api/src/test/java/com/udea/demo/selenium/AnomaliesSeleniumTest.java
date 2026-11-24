package com.udea.demo.selenium;

import com.udea.demo.selenium.pages.AnomaliesPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas Selenium E2E para la página de Alertas Operativas (HU-18).
 *
 * Ruta: /panel/alertas — requiere rol OPERADOR
 *
 * Pre-condiciones:
 * - Frontend Vite corriendo en http://localhost:5173 (o valor de -DbaseUrl)
 * - Backend opcional: si no está disponible, los tests inyectan sesión vía
 * localStorage y verifican la estructura de UI solamente.
 */
class AnomaliesSeleniumTest extends BaseSeleniumTest {

    private AnomaliesPage anomaliesPage;
    private static final String LOGIN_PATH = "/login";
    private static final String ALERTAS_PATH = "/panel/alertas";

    @BeforeEach
    void initPage() {
        anomaliesPage = new AnomaliesPage(driver, wait);
    }

    // =========================================================================
    // TEST 1 — Sin autenticación → redirige a /login
    // =========================================================================

    @Test
    void testAccesoSinAutenticacionRedigeALogin() {
        // Limpiar sesión
        driver.get(baseUrl);
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");

        anomaliesPage.navigateTo(baseUrl);

        // Esperar redirección a login
        try {
            wait.until(ExpectedConditions.urlContains(LOGIN_PATH));
        } catch (Exception ignored) {
        }

        String url = anomaliesPage.getCurrentUrl();
        assertTrue(
                url.contains(LOGIN_PATH) || url.contains("login"),
                "Sin sesion, debe redirigir a /login. URL actual: " + url);
    }

    // =========================================================================
    // TEST 2 — Con rol CLIENTE → debe denegar acceso (redirige o muestra error)
    // =========================================================================

    @Test
    void testAccesoConRolClienteEsDenegado() {
        // Inyectar sesión de CLIENTE
        driver.get(baseUrl);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String clienteSession = "{\"state\":{" +
                "\"accessToken\":\"mock-cliente-token\"," +
                "\"refreshToken\":\"mock-refresh\"," +
                "\"role\":\"CLIENTE\"," +
                "\"panel\":\"/panel/cliente\"," +
                "\"userId\":1," +
                "\"requiresPasswordChange\":false," +
                "\"isHydrated\":true" +
                "},\"version\":0}";
        js.executeScript("localStorage.setItem(arguments[0], arguments[1]);",
                "logistrack-session", clienteSession);

        anomaliesPage.navigateTo(baseUrl);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }

        String url = anomaliesPage.getCurrentUrl();
        boolean headingPresent = anomaliesPage.isHeadingVisible();

        // Un CLIENTE NO debería ver la página de alertas operativas
        // Puede redirigir a su panel o a login — lo importante es que NO muestra la
        // página
        boolean accesoDenegado = url.contains(LOGIN_PATH)
                || url.contains("/panel/cliente")
                || url.contains("/panel/pedidos")
                || !headingPresent;

        assertTrue(accesoDenegado,
                "Un CLIENTE no deberia tener acceso a /panel/alertas. URL: " + url);
    }

    // =========================================================================
    // TEST 3 — Con rol OPERADOR → página accesible, heading visible
    // =========================================================================

    @Test
    void testPaginaAlertasEsAccesibleConRolOperador() {
        iniciarSesionComoOperador();
        anomaliesPage.navigateTo(baseUrl);

        boolean visible = anomaliesPage.isHeadingVisible();
        assertTrue(visible, "Con rol OPERADOR, el heading 'Alertas operativas' debe ser visible");
    }

    // =========================================================================
    // TEST 4 — Filtros de Estado y Tipo son visibles
    // =========================================================================

    @Test
    void testFiltrosDeEstadoYTipoSonVisibles() {
        iniciarSesionComoOperador();
        anomaliesPage.navigateTo(baseUrl);

        // Esperar que la página cargue
        anomaliesPage.isHeadingVisible();

        assertTrue(anomaliesPage.isEstadoSelectVisible(),
                "El selector de Estado debe ser visible");
        assertTrue(anomaliesPage.isTipoSelectVisible(),
                "El selector de Tipo debe ser visible");
        assertTrue(anomaliesPage.isActualizarButtonVisible(),
                "El boton Actualizar debe ser visible");
    }

    // =========================================================================
    // TEST 5 — Los filtros tienen las opciones correctas
    // =========================================================================

    @Test
    void testOpcionesDeEstadoSonCorrectas() {
        iniciarSesionComoOperador();
        anomaliesPage.navigateTo(baseUrl);
        anomaliesPage.isHeadingVisible();

        // Verificar que se puede seleccionar cada estado
        // (no lanza excepción si el valor existe en el <select>)
        assertDoesNotThrow(() -> anomaliesPage.selectEstado("ACTIVA"),
                "Debe existir la opcion ACTIVA");
        assertDoesNotThrow(() -> anomaliesPage.selectEstado("RESUELTA"),
                "Debe existir la opcion RESUELTA");
        assertDoesNotThrow(() -> anomaliesPage.selectEstado("DESCARTADA"),
                "Debe existir la opcion DESCARTADA");
    }

    // =========================================================================
    // TEST 6 — Sección de Anomalías está presente en el DOM
    // =========================================================================

    @Test
    void testEstructuraSectionAnomaliasEsVisible() {
        iniciarSesionComoOperador();
        anomaliesPage.navigateTo(baseUrl);
        anomaliesPage.isHeadingVisible();

        assertTrue(anomaliesPage.isSectionAnomaliasVisible(),
                "La seccion aria-label='Anomalias' debe estar presente");
    }

    // =========================================================================
    // TEST 7 — Sección de Recepciones Pendientes está presente
    // =========================================================================

    @Test
    void testSectionRecepcionesPendientesEsVisible() {
        iniciarSesionComoOperador();
        anomaliesPage.navigateTo(baseUrl);
        anomaliesPage.isHeadingVisible();

        assertTrue(anomaliesPage.isSectionRecepcionesVisible(),
                "La seccion 'Recepciones pendientes' debe estar presente");
    }

    // =========================================================================
    // TEST 8 — Botones de paginación están presentes
    // =========================================================================

    @Test
    void testBotonesDePaginacionEstanPresentes() {
        iniciarSesionComoOperador();
        anomaliesPage.navigateTo(baseUrl);
        anomaliesPage.isHeadingVisible();
        anomaliesPage.waitForLoadingToFinish();

        assertTrue(anomaliesPage.isAnteriorButtonPresent(),
                "El boton 'Anterior' debe estar presente en la pagina");
        assertTrue(anomaliesPage.isSiguienteButtonPresent(),
                "El boton 'Siguiente' debe estar presente en la pagina");
    }

    // =========================================================================
    // TEST 9 — Con backend activo: lista o estado vacío visible tras carga
    // =========================================================================

    @Test
    void testConBackendActivoMuestraListaOEstadoVacio() {
        iniciarSesionComoOperador();
        anomaliesPage.navigateTo(baseUrl);
        anomaliesPage.isHeadingVisible();

        // Esperar a que termine de cargar
        anomaliesPage.waitForLoadingToFinish();

        boolean hayArticulos = !anomaliesPage.getAnomalyArticles().isEmpty();
        boolean hayMensajeVacio = anomaliesPage.isEmptyMessageVisible();
        boolean hayError = anomaliesPage.isErrorDisplayed();

        // Si el backend está activo: muestra artículos o mensaje vacío
        // Si el backend está offline: muestra un error — ambos casos son válidos
        assertTrue(hayArticulos || hayMensajeVacio || hayError,
                "Debe mostrar articulos de anomalia, mensaje 'No hay alertas' o error de conexion");
    }

    // =========================================================================
    // TEST 10 — Cambiar filtro y hacer clic en Actualizar no rompe la UI
    // =========================================================================

    @Test
    void testCambiarFiltroYActualizarNoRompeUI() {
        iniciarSesionComoOperador();
        anomaliesPage.navigateTo(baseUrl);
        anomaliesPage.isHeadingVisible();

        // Cambiar estado a RESUELTA
        anomaliesPage.selectEstado("RESUELTA");
        // Cambiar tipo a PERMANENCIA_EXCESIVA
        anomaliesPage.selectTipo("PERMANENCIA_EXCESIVA");
        // Hacer clic en Actualizar
        anomaliesPage.clickActualizar();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        // La página sigue mostrando el heading (UI no se rompió)
        assertTrue(anomaliesPage.isHeadingVisible(),
                "Tras cambiar filtros y actualizar, el heading debe seguir visible");
    }
}
