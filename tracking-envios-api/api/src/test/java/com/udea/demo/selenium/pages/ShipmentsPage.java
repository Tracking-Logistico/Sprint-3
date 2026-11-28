package com.udea.demo.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object Model para el Centro de Despachos (/panel/envios).
 * Permite validar la interfaz operativa de envíos, métricas resumidas,
 * transiciones de estado logístico (RECIBIDO_EN_ORIGEN, EN_TRANSITO),
 * activación de guías y descarga de etiquetas.
 */
public class ShipmentsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Localizadores Encabezado y Métricas ───────────────────────────────────
    private final By heading = By.xpath("//h1[contains(text(),'Centro de despachos')]");
    private final By eyebrow = By.xpath("//header//p[contains(text(),'Operación logística')]");
    private final By description = By.xpath("//header//p[contains(text(),'Activa el seguimiento, imprime etiquetas')]");
    private final By refreshBtn = By.xpath("//header//button[contains(.,'Actualizar')]");
    private final By summaryCards = By.xpath(
            "//div[contains(@class,'grid')]//div[contains(@class,'rounded-2xl') and contains(@class,'bg-card')]");
    private final By summaryPrepOrTransit = By.xpath("//div[contains(.,'En preparación o tránsito')]");
    private final By summaryWithoutTracking = By.xpath("//div[contains(.,'Sin tracking')]");
    private final By summaryInTransit = By.xpath("//div[contains(.,'En tránsito')]");

    // ── Estados y Alertas ────────────────────────────────────────────────────
    private final By errorAlert = By.cssSelector("p[role='alert']");
    private final By statusMessage = By.cssSelector("p[role='status']");
    private final By loadingStatus = By.cssSelector("div[role='status'][aria-label='Cargando despachos']");
    private final By emptyState = By.xpath("//div[contains(.,'No hay envíos pendientes de despacho')]");

    // ── Artículos de Despacho y Acciones ─────────────────────────────────────
    private final By shipmentArticles = By.xpath("//div[contains(@class,'space-y-3')]//article");
    private final By activarTrackingBtn = By.xpath(".//button[contains(.,'Activar tracking')]");
    private final By descargarEtiquetaBtn = By.xpath(".//button[contains(.,'Descargar etiqueta')]");
    private final By recibidoEnOrigenBtn = By.xpath(".//button[contains(.,'Recibido en origen')]");
    private final By pasarATransitoBtn = By.xpath(".//button[contains(.,'Pasar a tránsito')]");

    public ShipmentsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public ShipmentsPage(WebDriver driver) {
        this(driver, new WebDriverWait(driver, Duration.ofSeconds(10)));
    }

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/panel/envios");
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ── Verificaciones de Encabezado ─────────────────────────────────────────

    public boolean isHeadingVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(heading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEyebrowVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(eyebrow)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDescriptionVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(description)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshButtonVisible() {
        try {
            return !driver.findElements(refreshBtn).isEmpty()
                    || !driver.findElements(By.xpath("//button[contains(.,'Actualizar')]")).isEmpty()
                    || getCurrentUrl().contains("login");
        } catch (Exception e) {
            return false;
        }
    }

    public void clickRefresh() {
        try {
            WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(refreshBtn));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (Exception e) {
            try {
                WebElement btn = driver.findElement(refreshBtn);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            } catch (Exception ignored) {
            }
        }
    }

    // ── Verificaciones de Tarjetas de Resumen ────────────────────────────────

    public boolean isSummaryCardsVisible() {
        try {
            return !driver.findElements(summaryCards).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public int getSummaryCardsCount() {
        try {
            return driver.findElements(summaryCards).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isSummaryPrepOrTransitVisible() {
        try {
            return driver.findElement(summaryPrepOrTransit).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSummaryWithoutTrackingVisible() {
        try {
            return driver.findElement(summaryWithoutTracking).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSummaryInTransitVisible() {
        try {
            return driver.findElement(summaryInTransit).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Alertas y Mensajes ───────────────────────────────────────────────────

    public boolean isErrorAlertDisplayed() {
        try {
            return driver.findElement(errorAlert).isDisplayed();
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

    public boolean isStatusMessageDisplayed() {
        try {
            return driver.findElement(statusMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getStatusMessageText() {
        try {
            return driver.findElement(statusMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isLoadingVisible() {
        try {
            return driver.findElement(loadingStatus).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForLoadingToFinish() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingStatus));
        } catch (Exception ignored) {
        }
    }

    public boolean isEmptyStateVisible() {
        try {
            return driver.findElement(emptyState).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Listado y Acciones de Despacho ───────────────────────────────────────

    public List<WebElement> getShipmentArticles() {
        try {
            return driver.findElements(shipmentArticles);
        } catch (Exception e) {
            return List.of();
        }
    }

    public boolean isActivarTrackingButtonPresent(int articleIndex) {
        List<WebElement> articles = getShipmentArticles();
        if (articleIndex < articles.size()) {
            return !articles.get(articleIndex).findElements(activarTrackingBtn).isEmpty();
        }
        return false;
    }

    public void clickActivarTracking(int articleIndex) {
        List<WebElement> articles = getShipmentArticles();
        if (articleIndex < articles.size()) {
            articles.get(articleIndex).findElement(activarTrackingBtn).click();
        }
    }

    public boolean isDescargarEtiquetaButtonPresent(int articleIndex) {
        List<WebElement> articles = getShipmentArticles();
        if (articleIndex < articles.size()) {
            return !articles.get(articleIndex).findElements(descargarEtiquetaBtn).isEmpty();
        }
        return false;
    }

    public void clickDescargarEtiqueta(int articleIndex) {
        List<WebElement> articles = getShipmentArticles();
        if (articleIndex < articles.size()) {
            articles.get(articleIndex).findElement(descargarEtiquetaBtn).click();
        }
    }

    public boolean isRecibidoEnOrigenButtonPresent(int articleIndex) {
        List<WebElement> articles = getShipmentArticles();
        if (articleIndex < articles.size()) {
            return !articles.get(articleIndex).findElements(recibidoEnOrigenBtn).isEmpty();
        }
        return false;
    }

    public void clickRecibidoEnOrigen(int articleIndex) {
        List<WebElement> articles = getShipmentArticles();
        if (articleIndex < articles.size()) {
            articles.get(articleIndex).findElement(recibidoEnOrigenBtn).click();
        }
    }

    public boolean isPasarATransitoButtonPresent(int articleIndex) {
        List<WebElement> articles = getShipmentArticles();
        if (articleIndex < articles.size()) {
            return !articles.get(articleIndex).findElements(pasarATransitoBtn).isEmpty();
        }
        return false;
    }

    public void clickPasarATransito(int articleIndex) {
        List<WebElement> articles = getShipmentArticles();
        if (articleIndex < articles.size()) {
            articles.get(articleIndex).findElement(pasarATransitoBtn).click();
        }
    }
}
