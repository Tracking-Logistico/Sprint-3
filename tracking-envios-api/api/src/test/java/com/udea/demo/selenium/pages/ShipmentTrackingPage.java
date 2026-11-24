package com.udea.demo.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Page Object Model para la página de Seguimiento / Trazabilidad de Envíos.
 * Soporta la ruta autenticada (/panel/pedidos/seguimiento/:numeroTracking)
 * y la ruta pública (/tracking?code=:numeroTracking).
 */
public class ShipmentTrackingPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Localizadores Encabezado y Navegación ────────────────────────────────
    private final By heading              = By.xpath("//h1[contains(text(),'Seguimiento del envío')]");
    private final By subtitle             = By.xpath("//p[contains(text(),'Consulta el estado actual')]");
    private final By backToOrdersBtn      = By.xpath("//a[contains(.,'Volver a Mis envíos')] | //button[contains(.,'Volver a Mis envíos')]");

    // ── Localizadores Estados de Carga y Error ──────────────────────────────
    private final By loadingSkeleton      = By.cssSelector("div[role='status'][aria-label='Cargando seguimiento']");
    private final By errorAlert           = By.cssSelector("div[role='alert']");
    private final By errorTitle           = By.cssSelector("div[role='alert'] h5, div[role='alert'] [class*='title']");
    private final By errorDescription     = By.cssSelector("div[role='alert'] [class*='description'], div[role='alert'] p");

    // ── Localizadores Tarjeta Principal del Envío ───────────────────────────
    private final By orderCardTitle       = By.xpath("//div[contains(@class,'card')]//span[contains(text(),'Pedido')]");
    private final By statusBadge          = By.xpath("//div[contains(@class,'card')]//*[contains(@class,'badge')]");
    private final By trackingCodeText     = By.cssSelector("p.font-mono");
    private final By statusDescription    = By.xpath("//div[contains(@class,'card')]//p[contains(@class,'font-medium') and contains(@class,'text-foreground')]");
    private final By estimatedDelivery    = By.xpath("//p[contains(text(),'Entrega estimada')]");
    private final By rescheduledDelivery  = By.xpath("//p[contains(text(),'Nueva fecha de entrega')]");

    // ── Localizadores Historial y Trazabilidad ──────────────────────────────
    private final By movementsCardTitle   = By.xpath("//*[contains(text(),'Historial de movimientos')]");
    private final By emptyMovementsMsg    = By.xpath("//p[contains(text(),'Aún no hay movimientos registrados')]");
    private final By locationsCardTitle   = By.xpath("//*[contains(text(),'Trazabilidad de ubicación')]");
    private final By emptyLocationsMsg    = By.xpath("//p[contains(text(),'Aún no hay eventos de ubicación registrados')]");
    private final By locationItems        = By.xpath("//ol/li");

    // ── Localizadores Alertas Operativas (HU-18) ─────────────────────────────
    private final By anomalyAlert         = By.xpath("//div[contains(@aria-live,'polite')]//div[role='alert'] | //div[contains(.,'Estamos revisando tu envío')]");
    private final By callResponsibleOffice = By.xpath("//a[contains(.,'Llamar a la oficina responsable')]");

    public ShipmentTrackingPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait   = wait;
    }

    // ── Navegación ──────────────────────────────────────────────────────────

    public void navigateTo(String baseUrl, String trackingNumber) {
        driver.get(baseUrl + "/panel/pedidos/seguimiento/" + trackingNumber);
    }

    public void navigateToPublicTracking(String baseUrl, String trackingNumber) {
        driver.get(baseUrl + "/tracking?code=" + trackingNumber);
    }

    // ── Encabezado y Navegación ──────────────────────────────────────────────

    public boolean isHeadingVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(heading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSubtitleVisible() {
        try {
            return driver.findElement(subtitle).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isBackToOrdersButtonVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(backToOrdersBtn)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickBackToOrders() {
        wait.until(ExpectedConditions.elementToBeClickable(backToOrdersBtn)).click();
    }

    // ── Estados de Carga y Error ─────────────────────────────────────────────

    public boolean isLoadingVisible() {
        try {
            return driver.findElement(loadingSkeleton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForLoadingToFinish() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingSkeleton));
        } catch (Exception ignored) {
        }
    }

    public boolean isErrorAlertVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorAlertText() {
        try {
            return driver.findElement(errorAlert).getText();
        } catch (Exception e) {
            return "";
        }
    }

    // ── Tarjeta Principal de Pedido ──────────────────────────────────────────

    public boolean isOrderCardVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(orderCardTitle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getOrderTitleText() {
        try {
            return driver.findElement(orderCardTitle).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isStatusBadgeVisible() {
        try {
            return driver.findElement(statusBadge).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getStatusBadgeText() {
        try {
            return driver.findElement(statusBadge).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public String getTrackingCodeText() {
        try {
            return driver.findElement(trackingCodeText).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isEstimatedDeliveryVisible() {
        try {
            return driver.findElement(estimatedDelivery).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Secciones de Historial y Ubicación ────────────────────────────────────

    public boolean isMovementsCardVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(movementsCardTitle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmptyMovementsMessageVisible() {
        try {
            return driver.findElement(emptyMovementsMsg).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLocationsCardVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locationsCardTitle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmptyLocationsMessageVisible() {
        try {
            return driver.findElement(emptyLocationsMsg).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getLocationEvents() {
        try {
            return driver.findElements(locationItems);
        } catch (Exception e) {
            return List.of();
        }
    }

    // ── Alertas Operativas (HU-18) ───────────────────────────────────────────

    public boolean isAnomalyAlertVisible() {
        try {
            return driver.findElement(anomalyAlert).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCallOfficeButtonVisible() {
        try {
            return driver.findElement(callResponsibleOffice).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── URL Actual ──────────────────────────────────────────────────────────

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
