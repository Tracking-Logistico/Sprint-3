package com.udea.demo.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class AnomaliesPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By heading = By.xpath("//h1[contains(text(),'Alertas operativas')]");
    private final By errorAlert = By.cssSelector("p[role='alert']");
    private final By loadingStatus = By.cssSelector("p[role='status']");
    private final By estadoSelect = By.xpath("//select[contains(@aria-label,'Estado') or @id='estado-filter']");
    private final By tipoSelect = By.xpath("//select[contains(@aria-label,'Tipo') or @id='tipo-filter']");
    private final By actualizarBtn = By.xpath("//button[contains(.,'Actualizar')]");
    private final By sectionAnomalias = By.xpath("//section[contains(@aria-label,'Anomal') or contains(@aria-label,'anomal')]");
    private final By sectionRecepciones = By.xpath("//section[contains(@aria-label,'Recepcion') or contains(@aria-label,'recepcion')]");
    private final By anomalyArticles = By.xpath("//section[@aria-label='Anomal\u00edas']//article");
    private final By emptyAnomaliesMsg = By.xpath("//p[contains(text(),'No hay alertas')]");
    private final By anteriorBtn = By.xpath("//button[normalize-space(.)='Anterior']");
    private final By siguienteBtn = By.xpath("//button[normalize-space(.)='Siguiente']");
    private final By detailAside = By.cssSelector("aside[aria-label='Detalle de alerta']");
    private final By cerrarDetalleBtn = By.xpath("//button[normalize-space(.)='Cerrar detalle']");

    public AnomaliesPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // ── Navigation ──────────────────────────────────────────────────────────

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/panel/alertas");
    }

    // ── Heading ──────────────────────────────────────────────────────────────

    public boolean isHeadingVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(heading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Error ─────────────────────────────────────────────────────────────────

    public boolean isErrorDisplayed() {
        try {
            return driver.findElement(errorAlert).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorText() {
        try {
            return driver.findElement(errorAlert).getText();
        } catch (Exception e) {
            return "";
        }
    }

    // ── Loading ───────────────────────────────────────────────────────────────

    public boolean isLoadingVisible() {
        try {
            return driver.findElement(loadingStatus).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Filters ───────────────────────────────────────────────────────────────

    public boolean isEstadoSelectVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(estadoSelect)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTipoSelectVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(tipoSelect)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isActualizarButtonVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(actualizarBtn)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void selectEstado(String value) {
        WebElement select = wait.until(ExpectedConditions.visibilityOfElementLocated(estadoSelect));
        new Select(select).selectByValue(value);
    }

    public void selectTipo(String value) {
        WebElement select = wait.until(ExpectedConditions.visibilityOfElementLocated(tipoSelect));
        new Select(select).selectByValue(value);
    }

    public void clickActualizar() {
        try {
            WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(actualizarBtn));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (Exception e) {
            try {
                WebElement btn = driver.findElement(actualizarBtn);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            } catch (Exception ignored) {
            }
        }
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    public boolean isSectionAnomaliasVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(sectionAnomalias)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSectionRecepcionesVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(sectionRecepciones)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Anomaly List ──────────────────────────────────────────────────────────

    public List<WebElement> getAnomalyArticles() {
        try {
            return driver.findElements(anomalyArticles);
        } catch (Exception e) {
            return List.of();
        }
    }

    public boolean isEmptyMessageVisible() {
        try {
            return driver.findElement(emptyAnomaliesMsg).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Pagination ────────────────────────────────────────────────────────────

    public boolean isAnteriorButtonPresent() {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(anteriorBtn)) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSiguienteButtonPresent() {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(siguienteBtn)) != null;
        } catch (Exception e) {
            return false;
        }
    }

    // ── Detail Aside ──────────────────────────────────────────────────────────

    public boolean isDetailAsideVisible() {
        try {
            return driver.findElement(detailAside).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickVerDetalle(int articleIndex) {
        List<WebElement> articles = getAnomalyArticles();
        if (articleIndex < articles.size()) {
            WebElement btn = articles.get(articleIndex)
                    .findElement(By.xpath(".//button[contains(.,'Ver detalle')]"));
            btn.click();
        }
    }

    public void clickCerrarDetalle() {
        wait.until(ExpectedConditions.elementToBeClickable(cerrarDetalleBtn)).click();
    }

    // ── Current URL ──────────────────────────────────────────────────────────

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ── Wait helpers ─────────────────────────────────────────────────────────

    /**
     * Waits until loading spinner disappears (or timeout — it's fine if it never
     * showed).
     */
    public void waitForLoadingToFinish() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingStatus));
        } catch (Exception ignored) {
        }
    }
}
