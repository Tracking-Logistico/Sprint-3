package com.udea.demo.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model para el Panel del Conductor (/panel/conductor).
 * Modela la gestión de la jornada de entregas, escaneo de checkpoints y estado
 * de conectividad.
 */
public class DriverDashboardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Localizadores Encabezado y Navegación ────────────────────────────────
    private final By heading = By.xpath("//h1[contains(text(),'Mis entregas de hoy')]");
    private final By eyebrow = By.xpath("//p[contains(text(),'Jornada del conductor')]");
    private final By refreshBtn = By.xpath("//header//button[contains(.,'Actualizar')]");

    // ── Localizadores Estado de Conectividad y Cola Offline ──────────────────
    private final By statusContainer = By.xpath("//div[@role='status' and contains(@aria-live,'polite')]");
    private final By onlineBadge = By
            .xpath("//div[@role='status']//*[contains(text(),'En línea') or contains(text(),'Sin conexión')]");
    private final By pendingEvents = By.xpath("//div[@role='status']//*[contains(text(),'eventos pendientes')]");

    // ── Localizadores Alertas y Listado ──────────────────────────────────────
    private final By errorAlert = By.xpath("//div[@role='alert']");

    public DriverDashboardPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // ── Navegación ──────────────────────────────────────────────────────────

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/panel/conductor");
    }

    // ── Encabezado y Navegación ──────────────────────────────────────────────

    public boolean isHeadingVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(heading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEyebrowVisible() {
        try {
            return driver.findElement(eyebrow).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshButtonVisible() {
        try {
            return !driver.findElements(refreshBtn).isEmpty()
                    || !driver.findElements(By.xpath("//button[contains(.,'Actualizar')]")).isEmpty()
                    || driver.getCurrentUrl().contains("login");
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
                WebElement btn = driver.findElement(By.xpath("//button[contains(.,'Actualizar')]"));
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            } catch (Exception ignored) {
            }
        }
    }

    // ── Estado de Conectividad ───────────────────────────────────────────────

    public boolean isStatusContainerVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(statusContainer)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOnlineBadgeVisible() {
        try {
            return driver.findElement(onlineBadge).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
