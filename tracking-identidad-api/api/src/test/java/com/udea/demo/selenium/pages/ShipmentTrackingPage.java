package com.udea.demo.selenium.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model para la página de Seguimiento de Envíos (/tracking y /panel/pedidos/seguimiento/:numeroTracking).
 */
public class ShipmentTrackingPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    // Localizadores basados en ShipmentTrackingPage.tsx y ShipmentTrackingView.tsx
    private final By headingTitle = By.xpath("//h1[contains(text(),'Seguimiento del envío')]");
    private final By backToOrdersButton = By.xpath("//button[contains(.,'Volver a Mis envíos')] | //a[contains(.,'Volver a Mis envíos')]");
    private final By alertDestructive = By.xpath("//div[@role='alert'] | //div[contains(@class,'border-destructive')]");
    private final By alertTitle = By.xpath("//h5[contains(@class,'font-medium')] | //div[@role='alert']//div[contains(@class,'font-medium')]");
    private final By alertDescription = By.xpath("//div[contains(@class,'text-muted-foreground')] | //div[@role='alert']//div[contains(@class,'text-sm')]");
    private final By trackingCodeDisplay = By.cssSelector("p.font-mono");
    private final By movementHistoryTitle = By.xpath("//*[contains(text(),'Historial de movimientos')]");
    private final By locationTraceabilityTitle = By.xpath("//*[contains(text(),'Trazabilidad de ubicación')]");
    private final By statusBadge = By.xpath("//div[contains(@class,'badge')] | //span[contains(@class,'badge')] | //header//..//div[contains(text(),'Pedido')]//span");

    public ShipmentTrackingPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public ShipmentTrackingPage openByRoute(String trackingNumber) {
        String fullUrl = baseUrl + "/panel/pedidos/seguimiento/" + trackingNumber;
        driver.get(fullUrl);
        return this;
    }

    public ShipmentTrackingPage openByQueryParam(String trackingCode) {
        String fullUrl = baseUrl + "/tracking?code=" + trackingCode;
        driver.get(fullUrl);
        return this;
    }

    public boolean isHeadingVisible() {
        try {
            WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(headingTitle));
            return heading.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isBackButtonPresent() {
        try {
            WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(backToOrdersButton));
            return button.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickBackToOrders() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(backToOrdersButton));
        button.click();
    }

    public boolean isErrorAlertDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertDestructive));
            return alert.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getAlertTitleText() {
        try {
            WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(alertTitle));
            return title.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public String getAlertDescriptionText() {
        try {
            WebElement desc = wait.until(ExpectedConditions.visibilityOfElementLocated(alertDescription));
            return desc.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isMovementHistoryVisible() {
        try {
            return driver.findElement(movementHistoryTitle).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLocationTraceabilityVisible() {
        try {
            return driver.findElement(locationTraceabilityTitle).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getDisplayedTrackingCode() {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(trackingCodeDisplay));
            return element.getText();
        } catch (Exception e) {
            return "";
        }
    }
}
