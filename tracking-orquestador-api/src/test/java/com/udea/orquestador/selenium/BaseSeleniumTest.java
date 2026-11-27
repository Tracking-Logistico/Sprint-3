package com.udea.orquestador.selenium;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Clase base para pruebas automatizadas con Selenium WebDriver en tracking-orquestador-api.
 * Configura ChromeDriver en modo headless configurable y resolución de URLs para el frontend y el gateway.
 */
public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;
    protected String gatewayUrl;

    @BeforeEach
    public void setUp() {
        this.baseUrl = System.getProperty("baseUrl",
                System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:5173"));
        this.gatewayUrl = System.getProperty("gatewayUrl",
                System.getenv().getOrDefault("GATEWAY_URL", "http://localhost:8080"));

        ChromeOptions options = new ChromeOptions();
        boolean headless = !"false".equalsIgnoreCase(System.getProperty("headless", "true"));
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (this.driver != null) {
            try {
                this.driver.quit();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Limpia la sesión actual del navegador en el almacenamiento local.
     */
    protected void limpiarSesion() {
        try {
            driver.get(baseUrl);
            ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        } catch (Exception ignored) {
        }
    }

    /**
     * Inyecta una sesión mock con rol OPERADOR en localStorage (Zustand).
     */
    protected void inyectarSesionOperador() {
        try {
            driver.get(baseUrl);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String sessionJson =
                "{\"state\":{" +
                "\"accessToken\":\"mock-gateway-operador-token\"," +
                "\"refreshToken\":\"mock-gateway-refresh-token\"," +
                "\"role\":\"OPERADOR\"," +
                "\"panel\":\"/panel/operador\"," +
                "\"userId\":99," +
                "\"requiresPasswordChange\":false," +
                "\"isHydrated\":true" +
                "},\"version\":0}";
            js.executeScript("window.localStorage.setItem(arguments[0], arguments[1]);",
                    "logistrack-session", sessionJson);
        } catch (Exception ignored) {
        }
    }

    /**
     * Inyecta una sesión mock con rol CLIENTE en localStorage (Zustand).
     */
    protected void inyectarSesionCliente() {
        try {
            driver.get(baseUrl);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String sessionJson =
                "{\"state\":{" +
                "\"accessToken\":\"mock-gateway-cliente-token\"," +
                "\"refreshToken\":\"mock-gateway-refresh-token\"," +
                "\"role\":\"CLIENTE\"," +
                "\"panel\":\"/panel/cliente\"," +
                "\"userId\":1," +
                "\"requiresPasswordChange\":false," +
                "\"isHydrated\":true" +
                "},\"version\":0}";
            js.executeScript("window.localStorage.setItem(arguments[0], arguments[1]);",
                    "logistrack-session", sessionJson);
        } catch (Exception ignored) {
        }
    }
}
