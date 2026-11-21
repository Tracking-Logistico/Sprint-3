package com.udea.demo.selenium;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Clase base para pruebas automatizadas con Selenium WebDriver.
 * Configura ChromeDriver en modo headless o con interfaz gráfica y maneja tiempos de espera explícitos.
 */
public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    @BeforeEach
    public void setUp() {
        this.baseUrl = System.getProperty("baseUrl", 
                System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:5173"));

        ChromeOptions options = new ChromeOptions();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

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
}
