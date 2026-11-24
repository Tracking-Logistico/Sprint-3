package com.udea.demo.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model para la página de Configuración y Perfil de Usuario (/panel/configuracion).
 * Modela tanto la configuración estándar (perfil, preferencias y cambio voluntario de clave)
 * como el flujo de activación de cuenta obligatorio (requiresPasswordChange = true).
 */
public class SettingsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Localizadores Encabezado y Navegación ────────────────────────────────
    private final By heading               = By.xpath("//h1[contains(text(),'Configuración') or contains(text(),'Activa tu cuenta')]");
    private final By backToDashboardLink   = By.xpath("//a[contains(.,'Volver al resumen')]");
    private final By sessionRoleText       = By.xpath("//p[contains(text(),'Sesión activa con rol') or contains(text(),'Por seguridad debes cambiar')]");

    // ── Localizadores Perfil y Preferencias ──────────────────────────────────
    private final By profileCard           = By.xpath("//div[contains(@class,'card')]//*[contains(text(),'Tu perfil') or contains(text(),'Sin teléfono')]");
    private final By editProfileBtn        = By.xpath("//button[contains(.,'Editar datos')]");
    private final By notificationCard      = By.xpath("//*[contains(text(),'Notificaciones') or contains(text(),'Preferencias')]");
    private final By deactivateAccountBtn  = By.xpath("//button[contains(.,'Desactivar')]");

    // ── Localizadores Tarjeta de Seguridad ──────────────────────────────────
    private final By securityCardTitle     = By.xpath("//*[contains(text(),'Seguridad')]");
    private final By openPasswordDialogBtn = By.xpath("//button[normalize-space(.)='Cambiar contraseña']");

    // ── Localizadores Diálogo Cambiar Contraseña ─────────────────────────────
    private final By dialogContainer       = By.cssSelector("div[role='dialog']");
    private final By dialogTitle           = By.xpath("//div[@role='dialog']//*[contains(text(),'Cambiar contraseña')]");
    private final By currentPasswordInput  = By.id("actual");
    private final By newPasswordInput      = By.id("nueva");
    private final By confirmPasswordInput  = By.id("confirmacion");
    private final By submitPasswordBtn     = By.xpath("//div[@role='dialog']//button[@type='submit' and contains(.,'Actualizar contraseña')]");
    private final By closeDialogBtn        = By.xpath("//div[@role='dialog']//button[contains(@class,'close')]");

    public SettingsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait   = wait;
    }

    // ── Navegación ──────────────────────────────────────────────────────────

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/panel/configuracion");
    }

    // ── Encabezado y Navegación ──────────────────────────────────────────────

    public boolean isHeadingVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(heading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getHeadingText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(heading)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isBackToDashboardLinkVisible() {
        try {
            return driver.findElement(backToDashboardLink).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickBackToDashboard() {
        wait.until(ExpectedConditions.elementToBeClickable(backToDashboardLink)).click();
    }

    public boolean isSessionRoleTextVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(sessionRoleText)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Perfil y Seguridad ──────────────────────────────────────────────────

    public boolean isProfileCardVisible() {
        try {
            return driver.findElement(profileCard).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSecurityCardVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(securityCardTitle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isChangePasswordButtonVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(openPasswordDialogBtn)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void openChangePasswordDialog() {
        wait.until(ExpectedConditions.elementToBeClickable(openPasswordDialogBtn)).click();
    }

    // ── Diálogo Cambiar Contraseña ───────────────────────────────────────────

    public boolean isChangePasswordDialogVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void fillPasswordForm(String actual, String nueva, String confirmacion) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(currentPasswordInput)).clear();
        driver.findElement(currentPasswordInput).sendKeys(actual);

        driver.findElement(newPasswordInput).clear();
        driver.findElement(newPasswordInput).sendKeys(nueva);

        driver.findElement(confirmPasswordInput).clear();
        driver.findElement(confirmPasswordInput).sendKeys(confirmacion);
    }

    public void submitPasswordForm() {
        wait.until(ExpectedConditions.elementToBeClickable(submitPasswordBtn)).click();
    }

    public boolean isDeactivateAccountVisible() {
        try {
            return driver.findElement(deactivateAccountBtn).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── URL Actual ──────────────────────────────────────────────────────────

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
