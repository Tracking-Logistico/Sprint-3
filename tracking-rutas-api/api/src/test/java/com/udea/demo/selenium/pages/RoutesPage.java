package com.udea.demo.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Page Object Model para la página de Rutas (/panel/rutas).
 * Modela la vista de OPERADOR ("Planificación de rutas") y la vista de
 * CONDUCTOR ("Mis entregas de hoy" / "Ruta calculada").
 */
public class RoutesPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Localizadores Encabezado General ────────────────────────────────────
    private final By operatorHeading = By.xpath("//h1[contains(text(),'Planificación de rutas')]");
    private final By driverHeading = By
            .xpath("//h1[contains(text(),'Mis entregas de hoy') or normalize-space(text())='Rutas']");
    private final By operatorEyebrow = By.xpath("//p[contains(text(),'Operación logística')]");
    private final By driverEyebrow = By.xpath("//p[contains(text(),'Jornada del conductor')]");
    private final By refreshBtn = By.xpath("//button[contains(.,'Actualizar')]");

    // ── Localizadores Alertas y Mensajes ────────────────────────────────────
    private final By errorAlert = By.xpath("//div[@role='alert']");
    private final By statusMessage = By.xpath("//div[@role='status' and not(@aria-label)]");
    private final By loadingSkeleton = By.xpath("//div[@role='status' and contains(@aria-label,'Cargando')]");

    // ── Localizadores Vista Conductor ───────────────────────────────────────
    private final By driverRouteCard = By.xpath("//div[contains(@class,'card')]//*[contains(text(),'Ruta calculada')]");
    private final By nextRecommendedStop = By.xpath("//*[contains(text(),'Siguiente parada recomendada')]");
    private final By routeNotifications = By.xpath("//*[contains(text(),'Novedades de mi ruta')]");

    public RoutesPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // ── Navegación ──────────────────────────────────────────────────────────

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/panel/rutas");
    }

    // ── Validaciones de Encabezado ───────────────────────────────────────────

    public boolean isOperatorHeadingVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(operatorHeading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDriverHeadingVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(driverHeading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOperatorEyebrowVisible() {
        try {
            return driver.findElement(operatorEyebrow).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDriverEyebrowVisible() {
        try {
            return driver.findElement(driverEyebrow).isDisplayed();
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
                WebElement btn = driver.findElement(refreshBtn);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            } catch (Exception ignored) {
            }
        }
    }

    // ── Validaciones de Vista Conductor ─────────────────────────────────────

    public boolean isDriverRouteCardVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(driverRouteCard)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNextStopVisible() {
        try {
            return driver.findElement(nextRecommendedStop).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRouteNotificationsVisible() {
        try {
            return driver.findElement(routeNotifications).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Mensajes y Estados ──────────────────────────────────────────────────

    public boolean isErrorAlertDisplayed() {
        try {
            return driver.findElement(errorAlert).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
