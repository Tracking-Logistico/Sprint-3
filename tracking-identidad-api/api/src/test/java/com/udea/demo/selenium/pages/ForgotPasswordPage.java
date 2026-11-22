package com.udea.demo.selenium.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model para la página de Recuperación de Contraseña (/recuperar-password).
 */
public class ForgotPasswordPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String url;

    // Localizadores basados en ForgotPasswordPage.tsx
    private final By headingTitle = By.xpath("//h1[contains(text(),'Recuperar contraseña')]");
    private final By emailInput = By.name("email");
    private final By submitButton = By.cssSelector("button[type='submit']");
    private final By statusMessage = By.cssSelector("[role='status']");
    private final By errorAlert = By.cssSelector("[role='alert']");
    private final By backToLoginLink = By.linkText("Volver al inicio de sesión");

    public ForgotPasswordPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.url = baseUrl.endsWith("/") ? baseUrl + "recuperar-password" : baseUrl + "/recuperar-password";
    }

    public ForgotPasswordPage open() {
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(headingTitle));
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

    public boolean isEmailInputPresent() {
        return !driver.findElements(emailInput).isEmpty();
    }

    public boolean isEmailInputRequired() {
        WebElement element = driver.findElement(emailInput);
        return Boolean.parseBoolean(element.getAttribute("required")) || element.getAttribute("required") != null;
    }

    public String getEmailInputType() {
        return driver.findElement(emailInput).getAttribute("type");
    }

    public void enterEmail(String email) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        element.clear();
        element.sendKeys(email);
    }

    public void clickSubmit() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        button.click();
    }

    public void solicitarRecuperacion(String email) {
        enterEmail(email);
        clickSubmit();
    }

    public boolean isStatusMessageDisplayed() {
        try {
            WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(statusMessage));
            return msg.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getStatusMessageText() {
        try {
            WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(statusMessage));
            return msg.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isErrorDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert));
            return alert.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessageText() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert));
            return alert.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public void clickBackToLogin() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(backToLoginLink));
        link.click();
    }
}
