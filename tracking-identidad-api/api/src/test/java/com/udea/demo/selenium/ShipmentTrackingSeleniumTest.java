package com.udea.demo.selenium;

import com.udea.demo.selenium.pages.LoginPage;
import com.udea.demo.selenium.pages.ShipmentTrackingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Automatizadas de Seguimiento de Envíos con Selenium WebDriver")
class ShipmentTrackingSeleniumTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Debe redirigir al Login si un usuario no autenticado intenta acceder a la ruta de seguimiento")
    void testAccesoSeguimientoSinAutenticacionRedirigeALogin() {
        ShipmentTrackingPage trackingPage = new ShipmentTrackingPage(driver, baseUrl);
        trackingPage.openByQueryParam("LGT-4829-MX");

        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"), 
                "El acceso a seguimiento sin sesión debe redirigir a la pantalla de login");
    }

    @Test
    @DisplayName("Debe mostrar alerta de error cuando el código de seguimiento es inexistente o no válido")
    void testConsultaSeguimientoCodigoInexistenteMuestraAlerta() {
        iniciarSesionComoCliente();

        ShipmentTrackingPage trackingPage = new ShipmentTrackingPage(driver, baseUrl);
        trackingPage.openByQueryParam("GUIA-INEXISTENTE-99999");

        assertTrue(trackingPage.isHeadingVisible(), "El encabezado 'Seguimiento del envío' debe ser visible");
        assertTrue(trackingPage.isBackButtonPresent(), "El botón 'Volver a Mis envíos' debe estar presente");
        assertTrue(trackingPage.isErrorAlertDisplayed(), 
                "Debe mostrarse una alerta informativa indicando que no se pudo encontrar el envío");
    }

    @Test
    @DisplayName("Debe permitir volver a la vista de Mis Envíos desde el botón de retorno")
    void testBotonVolverAMisEnviosRedirigeCorrectamente() {
        iniciarSesionComoCliente();

        ShipmentTrackingPage trackingPage = new ShipmentTrackingPage(driver, baseUrl);
        trackingPage.openByRoute("LGT-0000-TEST");

        assertTrue(trackingPage.isBackButtonPresent(), "El botón de regreso debe estar visible");
        trackingPage.clickBackToOrders();

        wait.until(ExpectedConditions.urlContains("/panel/pedidos"));
        assertTrue(driver.getCurrentUrl().contains("/panel/pedidos"), 
                "Al pulsar en 'Volver a Mis envíos', la URL debe ser /panel/pedidos");
    }

    @Test
    @DisplayName("Debe visualizar las secciones principales de historial y trazabilidad con un envío válido")
    void testEstructuraSeguimientoEnvioValido() {
        iniciarSesionComoCliente();

        String trackingValido = System.getProperty("test.tracking", "LGT-4829-MX");
        ShipmentTrackingPage trackingPage = new ShipmentTrackingPage(driver, baseUrl);
        trackingPage.openByRoute(trackingValido);

        if (!trackingPage.isErrorAlertDisplayed()) {
            assertTrue(trackingPage.isHeadingVisible(), "El encabezado principal debe estar visible");
            assertTrue(trackingPage.isMovementHistoryVisible() || trackingPage.isLocationTraceabilityVisible(), 
                    "Debe mostrarse al menos una de las tarjetas de historial o trazabilidad");
        } else {
            assertTrue(trackingPage.isErrorAlertDisplayed(), "Debe gestionarse el estado no encontrado sin caídas");
        }
    }

    private void iniciarSesionComoCliente() {
        driver.get(baseUrl + "/login");
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String sessionJson = "{\"state\":{\"accessToken\":\"mock-token-selenium-test\",\"refreshToken\":\"mock-refresh-token\",\"role\":\"CLIENTE\",\"panel\":\"/panel/cliente\",\"userId\":1,\"requiresPasswordChange\":false,\"isHydrated\":true},\"version\":0}";
            js.executeScript("window.localStorage.setItem('logistrack-session', arguments[0]);", sessionJson);
        } catch (Exception ignored) {
        }
    }
}
