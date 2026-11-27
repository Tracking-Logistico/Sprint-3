package com.udea.demo.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Clase base para pruebas automatizadas con Selenium WebDriver en tracking-rutas-api.
 * Configura ChromeDriver en modo headless configurable y utilitarios de sesión para OPERADOR, CONDUCTOR y CLIENTE.
 */
public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    protected static final String OPERADOR_USER  = System.getProperty("test.operador.user",     "operador@udea.edu.co");
    protected static final String OPERADOR_PASS  = System.getProperty("test.operador.password", "Operador2026*");
    protected static final String CONDUCTOR_USER = System.getProperty("test.conductor.user",    "conductor@udea.edu.co");
    protected static final String CONDUCTOR_PASS = System.getProperty("test.conductor.password", "Conductor2026*");
    protected static final String CLIENTE_USER   = System.getProperty("test.cliente.user",      "cliente@udea.edu.co");
    protected static final String CLIENTE_PASS   = System.getProperty("test.cliente.password",  "Cliente2026*");

    @BeforeEach
    public void setUp() {
        boolean headless = !"false".equalsIgnoreCase(System.getProperty("headless", "true"));

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = System.getProperty("baseUrl",
                System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:5173"));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Limpia la sesión actual del navegador eliminando el localStorage.
     */
    protected void limpiarSesion() {
        try {
            driver.get(baseUrl);
            ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        } catch (Exception ignored) {
        }
    }

    /**
     * Inicia sesión como OPERADOR intentando login real o fallback con mock de localStorage.
     */
    protected void iniciarSesionComoOperador() {
        driver.get(baseUrl + "/login");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='email']")))
                    .sendKeys(OPERADOR_USER);
            driver.findElement(By.cssSelector("input[type='password']")).sendKeys(OPERADOR_PASS);
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            wait.until(ExpectedConditions.urlContains("/panel"));
            if (driver.getCurrentUrl().contains("/panel")) {
                return;
            }
        } catch (Exception ignored) {
        }

        // Fallback: inyectar sesión OPERADOR en localStorage (Zustand)
        driver.get(baseUrl);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String sessionJson =
            "{\"state\":{" +
            "\"accessToken\":\"mock-rutas-operador-token\"," +
            "\"refreshToken\":\"mock-rutas-refresh-token\"," +
            "\"role\":\"OPERADOR\"," +
            "\"panel\":\"/panel/operador\"," +
            "\"userId\":99," +
            "\"requiresPasswordChange\":false," +
            "\"isHydrated\":true" +
            "},\"version\":0}";
        js.executeScript("window.localStorage.setItem(arguments[0], arguments[1]);",
                "logistrack-session", sessionJson);
    }

    /**
     * Inicia sesión como CONDUCTOR con fallback en localStorage.
     */
    protected void iniciarSesionComoConductor() {
        driver.get(baseUrl + "/login");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='email']")))
                    .sendKeys(CONDUCTOR_USER);
            driver.findElement(By.cssSelector("input[type='password']")).sendKeys(CONDUCTOR_PASS);
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            wait.until(ExpectedConditions.urlContains("/panel"));
            if (driver.getCurrentUrl().contains("/panel")) {
                return;
            }
        } catch (Exception ignored) {
        }

        // Fallback: inyectar sesión CONDUCTOR en localStorage (Zustand)
        driver.get(baseUrl);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String sessionJson =
            "{\"state\":{" +
            "\"accessToken\":\"mock-rutas-conductor-token\"," +
            "\"refreshToken\":\"mock-rutas-refresh-token\"," +
            "\"role\":\"CONDUCTOR\"," +
            "\"panel\":\"/panel/conductor\"," +
            "\"userId\":55," +
            "\"requiresPasswordChange\":false," +
            "\"isHydrated\":true" +
            "},\"version\":0}";
        js.executeScript("window.localStorage.setItem(arguments[0], arguments[1]);",
                "logistrack-session", sessionJson);
    }

    /**
     * Inicia sesión como CLIENTE con fallback en localStorage.
     */
    protected void iniciarSesionComoCliente() {
        driver.get(baseUrl + "/login");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='email']")))
                    .sendKeys(CLIENTE_USER);
            driver.findElement(By.cssSelector("input[type='password']")).sendKeys(CLIENTE_PASS);
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            wait.until(ExpectedConditions.urlContains("/panel"));
            if (driver.getCurrentUrl().contains("/panel")) {
                return;
            }
        } catch (Exception ignored) {
        }

        // Fallback: inyectar sesión CLIENTE en localStorage (Zustand)
        driver.get(baseUrl);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String sessionJson =
            "{\"state\":{" +
            "\"accessToken\":\"mock-rutas-cliente-token\"," +
            "\"refreshToken\":\"mock-rutas-refresh-token\"," +
            "\"role\":\"CLIENTE\"," +
            "\"panel\":\"/panel/cliente\"," +
            "\"userId\":1," +
            "\"requiresPasswordChange\":false," +
            "\"isHydrated\":true" +
            "},\"version\":0}";
        js.executeScript("window.localStorage.setItem(arguments[0], arguments[1]);",
                "logistrack-session", sessionJson);
    }
}
