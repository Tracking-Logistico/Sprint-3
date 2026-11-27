package com.udea.demo.selenium.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model para la página de Registro de Cliente (/registro).
 */
public class RegisterPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String url;

    // Localizadores basados en RegisterForm.tsx
    private final By headingTitle = By.xpath("//*[contains(text(),'Crea tu cuenta de cliente')]");
    private final By nombreInput = By.name("nombre");
    private final By emailInput = By.name("email");
    private final By telefonoInput = By.name("telefono");
    private final By ciudadInput = By.name("ciudad");
    private final By direccionInput = By.name("direccion");
    private final By cedulaInput = By.name("numeroCedula");
    private final By fechaNacimientoInput = By.name("fechaNacimiento");
    private final By passwordInput = By.name("password");
    private final By confirmarPasswordInput = By.name("confirmarPassword");
    private final By terminosCheckbox = By.id("terminos");
    private final By politicaDatosCheckbox = By.id("politicaDatos");
    private final By submitButton = By.cssSelector("button[type='submit']");
    private final By errorAlert = By.xpath("//div[@role='alert'] | //div[contains(@class,'bg-rose-50')]");
    private final By loginLink = By.linkText("Inicia sesión");
    private final By togglePasswordButtons = By.cssSelector("button[aria-label='Mostrar contraseña'], button[aria-label='Ocultar contraseña']");
    private final By strengthLabel = By.xpath("//*[contains(@class,'text-xs') and (contains(text(),'Débil') or contains(text(),'Media') or contains(text(),'Fuerte') or contains(text(),'Sin definir'))]");

    public RegisterPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.url = baseUrl.endsWith("/") ? baseUrl + "registro" : baseUrl + "/registro";
    }

    public RegisterPage open() {
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

    public void enterNombre(String nombre) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(nombreInput));
        element.clear();
        element.sendKeys(nombre);
    }

    public void enterEmail(String email) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        element.clear();
        element.sendKeys(email);
    }

    public void enterTelefono(String tel) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(telefonoInput));
        element.clear();
        element.sendKeys(tel);
    }

    public void enterCiudad(String ciudad) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(ciudadInput));
        element.clear();
        element.sendKeys(ciudad);
    }

    public void enterDireccion(String direccion) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(direccionInput));
        element.clear();
        element.sendKeys(direccion);
    }

    public void enterCedula(String cedula) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(cedulaInput));
        element.clear();
        element.sendKeys(cedula);
    }

    public void enterFechaNacimiento(String fecha) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(fechaNacimientoInput));
        element.clear();
        element.sendKeys(fecha);
    }

    public void enterPassword(String password) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        element.clear();
        element.sendKeys(password);
    }

    public void enterConfirmarPassword(String confirmPassword) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(confirmarPasswordInput));
        element.clear();
        element.sendKeys(confirmPassword);
    }

    public void checkTerminos() {
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(terminosCheckbox));
        if (!checkbox.isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
        }
    }

    public void checkPoliticaDatos() {
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(politicaDatosCheckbox));
        if (!checkbox.isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
        }
    }

    public void clickSubmit() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        button.click();
    }

    public boolean isErrorDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert));
            return alert.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert));
            String text = alert.getText();
            if (text == null || text.trim().isEmpty()) {
                for (WebElement desc : driver.findElements(By.xpath("//div[contains(@class,'text-rose-700')] | //*[contains(@class,'cn-toast')] | //div[@role='alert']"))) {
                    if (desc.isDisplayed() && !desc.getText().trim().isEmpty()) {
                        return desc.getText().trim();
                    }
                }
            }
            return text != null ? text.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    public String getPasswordInputType() {
        WebElement element = driver.findElement(passwordInput);
        return element.getAttribute("type");
    }

    public void togglePasswordVisibility() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(togglePasswordButtons));
        button.click();
    }

    public String getPasswordStrengthText() {
        try {
            WebElement label = wait.until(ExpectedConditions.visibilityOfElementLocated(strengthLabel));
            return label.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public void clickLoginLink() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(loginLink));
        link.click();
    }

    public boolean isNombreFieldPresent() {
        return !driver.findElements(nombreInput).isEmpty();
    }

    public boolean isEmailFieldPresent() {
        return !driver.findElements(emailInput).isEmpty();
    }

    public boolean isCedulaFieldPresent() {
        return !driver.findElements(cedulaInput).isEmpty();
    }

    public void llenarDatosBasicos() {
        enterNombre("Carlos Vanegas");
        enterEmail("carlos.vanegas@udea.edu.co");
        enterTelefono("+573001234567");
        enterCiudad("Medellín");
        enterDireccion("Calle 67 # 53 - 108");
        enterCedula("1027886019");
        enterFechaNacimiento("1995-05-15");
    }
}
