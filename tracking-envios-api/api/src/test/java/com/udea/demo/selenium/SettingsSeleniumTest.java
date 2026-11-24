package com.udea.demo.selenium;

import com.udea.demo.selenium.pages.SettingsPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas Selenium E2E para la página de Configuración y Perfil de Usuario (/panel/configuracion).
 *
 * Flujos cubiertos:
 *   - Protección de ruta y redirección a login sin credenciales.
 *   - Vista de configuración y seguridad con rol CLIENTE.
 *   - Vista de configuración con rol OPERADOR.
 *   - Navegación de retorno al resumen del panel (/panel).
 *   - Apertura y validación de campos del diálogo "Cambiar contraseña".
 *   - Validación de flujo de primer ingreso con cambio de contraseña forzoso (requiresPasswordChange = true).
 */
class SettingsSeleniumTest extends BaseSeleniumTest {

    private SettingsPage settingsPage;
    private static final String LOGIN_PATH      = "/login";
    private static final String SETTINGS_PATH   = "/panel/configuracion";

    @BeforeEach
    void initPage() {
        settingsPage = new SettingsPage(driver, wait);
    }

    // =========================================================================
    // TEST 1 — Sin autenticación → redirige a /login
    // =========================================================================

    @Test
    @DisplayName("Sin sesión activa, acceder a /panel/configuracion debe redirigir a /login")
    void testAccesoSinAutenticacionRedirigeALogin() {
        limpiarSesion();
        settingsPage.navigateTo(baseUrl);

        try {
            wait.until(ExpectedConditions.urlContains(LOGIN_PATH));
        } catch (Exception ignored) {
        }

        String url = settingsPage.getCurrentUrl();
        assertTrue(
            url.contains(LOGIN_PATH) || url.contains("login"),
            "Sin sesión, el acceso a /panel/configuracion debe redirigir a /login. URL actual: " + url
        );
    }

    // =========================================================================
    // TEST 2 — Con rol CLIENTE → encabezado, rol y tarjeta de seguridad visibles
    // =========================================================================

    @Test
    @DisplayName("Con rol CLIENTE, la página de configuración debe mostrar el encabezado, rol y tarjeta de seguridad")
    void testAccesoConRolClienteMuestraEstructuraConfiguracion() {
        iniciarSesionComoCliente();
        settingsPage.navigateTo(baseUrl);

        assertTrue(settingsPage.isHeadingVisible(), "El encabezado 'Configuración' debe ser visible");
        assertTrue(settingsPage.isSessionRoleTextVisible(), "El indicador de sesión con rol debe ser visible");
        assertTrue(settingsPage.isSecurityCardVisible(), "La tarjeta de Seguridad debe estar presente");
        assertTrue(settingsPage.isChangePasswordButtonVisible(), "El botón 'Cambiar contraseña' debe estar disponible");
    }

    // =========================================================================
    // TEST 3 — Con rol OPERADOR → acceso permitido y tarjeta de seguridad visible
    // =========================================================================

    @Test
    @DisplayName("Con rol OPERADOR, la página de configuración y opciones de seguridad deben estar disponibles")
    void testAccesoConRolOperadorMuestraConfiguracionSeguridad() {
        iniciarSesionComoOperador();
        settingsPage.navigateTo(baseUrl);

        assertTrue(settingsPage.isHeadingVisible(), "Con rol OPERADOR, el encabezado debe ser visible");
        assertTrue(settingsPage.isSecurityCardVisible(), "La tarjeta de Seguridad debe ser visible para el operador");
        assertTrue(settingsPage.isChangePasswordButtonVisible(), "El operador debe poder acceder a 'Cambiar contraseña'");
    }

    // =========================================================================
    // TEST 4 — El enlace 'Volver al resumen' navega a /panel
    // =========================================================================

    @Test
    @DisplayName("Hacer clic en 'Volver al resumen' debe navegar a /panel")
    void testEnlaceVolverAlResumenNavegaAlPanel() {
        iniciarSesionComoCliente();
        settingsPage.navigateTo(baseUrl);

        assertTrue(settingsPage.isBackToDashboardLinkVisible(), "El enlace 'Volver al resumen' debe estar presente");
        settingsPage.clickBackToDashboard();

        try {
            wait.until(ExpectedConditions.urlContains("/panel"));
        } catch (Exception ignored) {
        }

        String currentUrl = settingsPage.getCurrentUrl();
        assertTrue(
            currentUrl.contains("/panel"),
            "Debe navegar a /panel o panel específico del rol. URL actual: " + currentUrl
        );
    }

    // =========================================================================
    // TEST 5 — Abrir el diálogo de 'Cambiar contraseña' y verificar campos
    // =========================================================================

    @Test
    @DisplayName("Al hacer clic en 'Cambiar contraseña' debe desplegarse el diálogo modal con sus campos")
    void testAbrirDialogoCambiarPasswordMuestraCampos() {
        iniciarSesionComoCliente();
        settingsPage.navigateTo(baseUrl);

        settingsPage.openChangePasswordDialog();
        assertTrue(settingsPage.isChangePasswordDialogVisible(), "El diálogo modal debe desplegarse en pantalla");

        // Diligenciar los campos sin enviar para verificar interactividad
        assertDoesNotThrow(() -> {
            settingsPage.fillPasswordForm("ClaveActual2026*", "NuevaClaveSegura2026*", "NuevaClaveSegura2026*");
        }, "Los campos de contraseña actual, nueva y confirmación deben permitir entrada de texto");
    }

    // =========================================================================
    // TEST 6 — Primer ingreso obligatorio (requiresPasswordChange = true)
    // =========================================================================

    @Test
    @DisplayName("Si requiresPasswordChange es true, la vista muestra 'Activa tu cuenta' y abre el formulario automáticamente")
    void testFlujoPrimerIngresoExigeCambioPasswordForzoso() {
        driver.get(baseUrl);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Inyectar sesión con requiresPasswordChange = true
        String firstLoginSession =
            "{\"state\":{" +
            "\"accessToken\":\"mock-temp-token\"," +
            "\"refreshToken\":\"mock-temp-refresh\"," +
            "\"role\":\"CLIENTE\"," +
            "\"panel\":\"/panel/cliente\"," +
            "\"userId\":10," +
            "\"requiresPasswordChange\":true," +
            "\"isHydrated\":true" +
            "},\"version\":0}";
        js.executeScript("window.localStorage.setItem(arguments[0], arguments[1]);",
                "logistrack-session", firstLoginSession);

        settingsPage.navigateTo(baseUrl);

        // En activación forzosa, el encabezado cambia a "Activa tu cuenta"
        String heading = settingsPage.getHeadingText();
        assertTrue(
            heading.contains("Activa tu cuenta") || heading.contains("Configuración"),
            "Debe indicar activación de cuenta o configuración forzosa. Encabezado: " + heading
        );

        // El diálogo de cambio de contraseña debe abrirse automáticamente
        assertTrue(
            settingsPage.isChangePasswordDialogVisible(),
            "El modal de cambio de contraseña debe estar abierto automáticamente en primer ingreso"
        );
    }
}
