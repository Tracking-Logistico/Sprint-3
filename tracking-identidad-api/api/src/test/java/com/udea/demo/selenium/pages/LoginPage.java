package com.udea.demo.selenium.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model para la página de inicio de sesión (/login) de LogisTrack.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String url;

    // Localizadores basados en LoginPage.tsx
    private final By emailInput = By.name("email");
    private final By passwordInput = By.name("password");
    private final By togglePasswordButton = By.cssSelector("button[aria-label='Mostrar contraseña'], button[aria-label='Ocultar contraseña']");
    private final By submitButton = By.cssSelector("button[type='submit']");
    private final By errorAlert = By.cssSelector("[role='alert']");
    private final By sessionExpiredNotice = By.cssSelector("[role='status']");
    private final By forgotPasswordLink = By.linkText("¿Olvidaste tu contraseña?");
    private final By registerLink = By.linkText("Crear cuenta");
    private final By headingTitle = By.xpath("//h1[contains(text(),'Iniciar sesión')]");

    public LoginPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.url = baseUrl.endsWith("/") ? baseUrl + "login" : baseUrl + "/login";
    }

    public LoginPage open() {
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(headingTitle));
        return this;
    }

    public void enterEmail(String email) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        element.clear();
        element.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        element.clear();
        element.sendKeys(password);
    }

    public void clickSubmit() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        button.click();
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSubmit();
    }

    public String getErrorMessage() {
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert));
        return alert.getText();
    }

    public boolean isErrorDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert));
            return alert.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void togglePasswordVisibility() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(togglePasswordButton));
        button.click();
    }

    public String getPasswordInputType() {
        WebElement element = driver.findElement(passwordInput);
        return element.getAttribute("type");
    }

    public boolean isHeadingVisible() {
        try {
            return driver.findElement(headingTitle).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmailFieldPresent() {
        return !driver.findElements(emailInput).isEmpty();
    }

    public boolean isPasswordFieldPresent() {
        return !driver.findElements(passwordInput).isEmpty();
    }

    public boolean isSubmitButtonPresent() {
        return !driver.findElements(submitButton).isEmpty();
    }

    public boolean isEmailFieldRequired() {
        WebElement element = driver.findElement(emailInput);
        return Boolean.parseBoolean(element.getAttribute("required")) || element.getAttribute("required") != null;
    }

    public boolean isPasswordFieldRequired() {
        WebElement element = driver.findElement(passwordInput);
        return Boolean.parseBoolean(element.getAttribute("required")) || element.getAttribute("required") != null;
    }

    public void clickRegister() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(registerLink));
        link.click();
    }

    public void clickForgotPassword() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(forgotPasswordLink));
        link.click();
    }
}
