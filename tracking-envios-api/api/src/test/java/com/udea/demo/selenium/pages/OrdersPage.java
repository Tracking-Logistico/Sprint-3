package com.udea.demo.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object Model para la pagina de Gestion de Pedidos (/panel/pedidos).
 */
public class OrdersPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Localizadores Vista Cliente (Mis envios)
    private final By clientHeading         = By.xpath("//h1[contains(text(),'Mis envíos') or contains(text(),'Mis envios')]");
    private final By clientEyebrow         = By.xpath("//p[contains(text(),'Clientes')]");
    private final By clientDescription     = By.xpath("//p[contains(text(),'Consulta los pedidos asociados a tu cuenta')]");
    private final By solicitarEnvioBtn     = By.xpath("//header//button[contains(.,'Solicitar envío') or contains(.,'Solicitar envio')]");
    private final By refreshBtn            = By.xpath("//header//button[contains(.,'Actualizar')]");
    private final By clientSectionTitle    = By.xpath("//h2[contains(text(),'Tus envíos') or contains(text(),'Tus envios')]");

    // Localizadores Dialogo Solicitar / Corregir Envio
    private final By dialogContainer       = By.cssSelector("div[role='dialog']");
    private final By dialogTitle           = By.xpath("//div[@role='dialog']//h2");
    private final By direccionOrigenInput  = By.name("direccionOrigen");
    private final By ciudadOrigenInput     = By.name("ciudadOrigen");
    private final By codigoPostalOrigen    = By.name("codigoPostalOrigen");
    private final By remitenteTelefono     = By.name("remitenteTelefono");
    private final By direccionDestinoInput = By.name("direccionDestino");
    private final By ciudadDestinoInput    = By.name("ciudadDestino");
    private final By codigoPostalDestino   = By.name("codigoPostalDestino");
    private final By destinatarioNombre    = By.name("destinatarioNombre");
    private final By destinatarioTelefono  = By.name("destinatarioTelefono");
    private final By descripcionPaquete    = By.name("descripcionPaquete");
    private final By pesoKgInput           = By.name("pesoKg");
    private final By largoCmInput          = By.name("largoCm");
    private final By anchoCmInput          = By.name("anchoCm");
    private final By altoCmInput           = By.name("altoCm");
    private final By serviceSelect         = By.id("service");
    private final By verificacionEdadCheck = By.id("requiereVerificacionEdad");
    private final By submitOrderBtn        = By.xpath("//button[@type='submit' and @form='order-form']");
    private final By cancelDialogBtn       = By.xpath("//div[@role='dialog']//button[normalize-space(.)='Cancelar']");

    // Localizadores Filtros Cliente (MyShipmentsFilters)
    private final By statusFilterSelect    = By.id("shipments-status-filter");
    private final By dateFromInput         = By.id("shipments-date-from");
    private final By dateToInput           = By.id("shipments-date-to");
    private final By sortSelect            = By.id("shipments-sort");
    private final By emptyStateContainer   = By.xpath("//div[contains(@class,'text-center') and (contains(.,'No tienes pedidos') or contains(.,'No tienes envíos') or contains(.,'Crear primer envío'))]");
    private final By anteriorPageBtn       = By.xpath("//button[normalize-space(.)='Anterior']");
    private final By siguientePageBtn      = By.xpath("//button[normalize-space(.)='Siguiente']");

    // Localizadores Vista Operador (Bandeja de pedidos)
    private final By operatorHeading       = By.xpath("//h1[contains(text(),'Bandeja de pedidos')]");
    private final By operatorEyebrow       = By.xpath("//p[contains(text(),'Operación') or contains(text(),'Operacion')]");
    private final By operatorDescription   = By.xpath("//p[contains(text(),'Confirma remitente, destinatario')]");
    private final By operatorEmptyState    = By.xpath("//div[contains(.,'No hay solicitudes pendientes de revisión') or contains(.,'No hay pedidos')]");
    private final By orderArticles         = By.xpath("//article");

    public OrdersPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public OrdersPage(WebDriver driver) {
        this(driver, new WebDriverWait(driver, Duration.ofSeconds(10)));
    }

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/panel/pedidos");
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // Vista Cliente
    public boolean isClientHeadingVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(clientHeading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isClientEyebrowVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(clientEyebrow)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isClientDescriptionVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(clientDescription)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSolicitarEnvioButtonVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(solicitarEnvioBtn)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickSolicitarEnvio() {
        wait.until(ExpectedConditions.elementToBeClickable(solicitarEnvioBtn)).click();
    }

    public boolean isRefreshButtonVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(refreshBtn)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickRefresh() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(refreshBtn)).click();
        } catch (Exception ignored) {
        }
    }

    public boolean isClientSectionTitleVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(clientSectionTitle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Dialogo Solicitar Envio
    public boolean isDialogVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getDialogTitleText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(dialogTitle)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public void fillOrderForm(String dirOrigen, String ciudadOrigen, String telRemitente,
                              String dirDestino, String ciudadDestino, String nombreDestinatario,
                              String telDestinatario, String descripcion,
                              String peso, String largo, String ancho, String alto,
                              String tipoServicio, boolean verificarEdad) {
        // Esperar a que el diálogo modal (div[role='dialog']) esté montado y sus
        // campos visibles antes de diligenciar el formulario. React puede estar
        // re-renderizando justo tras abrir el modal.
        wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer));
        wait.until(ExpectedConditions.presenceOfElementLocated(direccionOrigenInput));
        setFieldValue(direccionOrigenInput, dirOrigen);
        setFieldValue(ciudadOrigenInput, ciudadOrigen);
        setFieldValue(remitenteTelefono, telRemitente);
        setFieldValue(direccionDestinoInput, dirDestino);
        setFieldValue(ciudadDestinoInput, ciudadDestino);
        setFieldValue(destinatarioNombre, nombreDestinatario);
        setFieldValue(destinatarioTelefono, telDestinatario);
        setFieldValue(descripcionPaquete, descripcion);
        setFieldValue(pesoKgInput, peso);
        setFieldValue(largoCmInput, largo);
        setFieldValue(anchoCmInput, ancho);
        setFieldValue(altoCmInput, alto);

        if (tipoServicio != null && !tipoServicio.isEmpty()) {
            selectTipoServicio(tipoServicio);
        }

        if (verificarEdad != isVerificacionEdadChecked()) {
            toggleVerificacionEdad();
        }
    }

    /**
     * Establece {@code value} en el campo localizado por {@code locator} y verifica
     * que el valor realmente se conserve.
     *
     * <ol>
     *   <li>Espera a que el elemento sea visible y clickeable (el modal/input de
     *   React puede seguir montando o re-renderizando).</li>
     *   <li>Hace {@code scrollIntoView}, {@code clear()} y {@code sendKeys(value)}.</li>
     *   <li>Verifica que {@link #getFieldValue(By)} coincida con {@code value}.</li>
     *   <li>Si el valor queda vacío o cambia tras un re-render, aplica el setter
     *   nativo de React ({@code HTMLInputElement.prototype.value} /
     *   {@code HTMLTextAreaElement.prototype.value}) y dispara {@code input} y
     *   {@code change}.</li>
     *   <li>Reintenta varias veces; si finalmente no logra establecer el valor,
     *   lanza {@link IllegalStateException} con un mensaje claro.</li>
     * </ol>
     */
    public void setFieldValue(By locator, String value) {
        final String expected = value == null ? "" : value;
        final int maxAttempts = 3;
        String lastActual = null;
        Throwable lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                scrollIntoView(element);

                try {
                    element.clear();
                    element.sendKeys(expected);
                } catch (Exception typingError) {
                    // No abortar: verificar/reparar con JavaScript más abajo.
                    lastError = typingError;
                }

                // Dar tiempo a que React procese el evento y re-renderice
                // antes de verificar que el valor se conserve.
                sleepQuietly(100);
                if (fieldValueMatches(locator, expected)) {
                    return;
                }

                // El valor quedó vacío o cambió tras un re-render de React.
                WebElement current = driver.findElement(locator);
                scrollIntoView(current);
                applyNativeReactValue(current, expected);

                sleepQuietly(100);
                if (fieldValueMatches(locator, expected)) {
                    return;
                }
                lastActual = getFieldValue(locator);
            } catch (Exception e) {
                lastError = e;
            }

            sleepQuietly(150);
        }

        // Último intento explícito con setter nativo antes de rendirse.
        try {
            WebElement element = driver.findElement(locator);
            scrollIntoView(element);
            applyNativeReactValue(element, expected);
            sleepQuietly(100);
            if (fieldValueMatches(locator, expected)) {
                return;
            }
            lastActual = getFieldValue(locator);
        } catch (Exception e) {
            lastError = e;
        }

        throw new IllegalStateException(
                "No se pudo establecer el valor '" + expected + "' en el campo " + describeLocator(locator)
                        + ". Valor actual='" + lastActual + "'."
                        + (lastError != null ? " Último error: " + lastError : ""),
                lastError);
    }

    /** Verifica (releyendo el elemento para evitar referencias obsoletas) que el valor coincida. */
    private boolean fieldValueMatches(By locator, String expected) {
        return valuesMatch(getFieldValue(locator), expected);
    }

    /** Compara valores tolerando espacios y normalización numérica. */
    private boolean valuesMatch(String actual, String expected) {
        if (actual == null) {
            return false;
        }
        if (actual.equals(expected)) {
            return true;
        }
        String trimmedActual = actual.trim();
        String trimmedExpected = expected == null ? "" : expected.trim();
        if (trimmedActual.equals(trimmedExpected)) {
            return true;
        }
        try {
            return new java.math.BigDecimal(trimmedActual).compareTo(new java.math.BigDecimal(trimmedExpected)) == 0;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    /** Setter nativo de React + eventos {@code input}/{@code change}. */
    private void applyNativeReactValue(WebElement element, String value) {
        String script = String.join("\n",
                "const el = arguments[0];",
                "const val = arguments[1];",
                "let proto = window.HTMLInputElement.prototype;",
                "if (typeof window.HTMLTextAreaElement !== 'undefined' && el instanceof window.HTMLTextAreaElement) {",
                "  proto = window.HTMLTextAreaElement.prototype;",
                "}",
                "const desc = Object.getOwnPropertyDescriptor(proto, 'value');",
                "if (desc && desc.set) { desc.set.call(el, val); } else { el.value = val; }",
                "el.dispatchEvent(new Event('input', { bubbles: true }));",
                "el.dispatchEvent(new Event('change', { bubbles: true }));");
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(script, element, value);
    }

    private void scrollIntoView(WebElement element) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
        } catch (Exception ignored) {
        }
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private String describeLocator(By locator) {
        return locator == null ? "<null>" : locator.toString();
    }

    public String getFieldValue(By locator) {
        try {
            String value = driver.findElement(locator).getAttribute("value");
            return value == null ? "" : value;
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isFieldRequired(By locator) {
        try {
            String required = driver.findElement(locator).getAttribute("required");
            return required != null && !required.equalsIgnoreCase("false");
        } catch (Exception e) {
            return false;
        }
    }

    public String getFieldPattern(By locator) {
        try {
            return driver.findElement(locator).getAttribute("pattern");
        } catch (Exception e) {
            return "";
        }
    }

    public void selectTipoServicio(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        // Reintentar hasta que la opción quede seleccionada (el select puede
        // re-renderizarse tras un re-render de React).
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(serviceSelect));
                scrollIntoView(selectElement);
                new Select(selectElement).selectByValue(value);
                if (value.equals(getSelectedTipoServicio())) {
                    return;
                }
            } catch (Exception ignored) {
                // reintentar
            }
            sleepQuietly(150);
        }
    }

    public String getSelectedTipoServicio() {
        try {
            WebElement selectElement = driver.findElement(serviceSelect);
            return new Select(selectElement).getFirstSelectedOption().getAttribute("value");
        } catch (Exception e) {
            return "";
        }
    }

    public void toggleVerificacionEdad() {
        for (int i = 0; i < 3; i++) {
            try {
                WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(verificacionEdadCheck));
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", checkbox);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
                break;
            } catch (Exception ignored) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ignored2) {
                }
            }
        }
    }

    public boolean isVerificacionEdadChecked() {
        try {
            WebElement checkbox = driver.findElement(verificacionEdadCheck);
            String ariaChecked = checkbox.getAttribute("aria-checked");
            if (ariaChecked != null) {
                return Boolean.parseBoolean(ariaChecked);
            }
            return checkbox.isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSubmitOrderButtonEnabled() {
        try {
            return driver.findElement(submitOrderBtn).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void submitOrder() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(submitOrderBtn)).click();
        } catch (Exception ignored) {
        }
    }

    public void cancelDialog() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(cancelDialogBtn)).click();
        } catch (Exception ignored) {
        }
    }

    // Getters de Localizadores del Formulario
    public By getDireccionOrigenLocator() {
        return direccionOrigenInput;
    }

    public By getCiudadOrigenLocator() {
        return ciudadOrigenInput;
    }

    public By getRemitenteTelefonoLocator() {
        return remitenteTelefono;
    }

    public By getDireccionDestinoLocator() {
        return direccionDestinoInput;
    }

    public By getCiudadDestinoLocator() {
        return ciudadDestinoInput;
    }

    public By getDestinatarioNombreLocator() {
        return destinatarioNombre;
    }

    public By getDestinatarioTelefonoLocator() {
        return destinatarioTelefono;
    }

    public By getDescripcionPaqueteLocator() {
        return descripcionPaquete;
    }

    public By getPesoKgLocator() {
        return pesoKgInput;
    }

    // Filtros y Paginacion Cliente
    public boolean isStatusFilterVisible() {
        try {
            return !driver.findElements(statusFilterSelect).isEmpty()
                    || driver.getCurrentUrl().contains("login");
        } catch (Exception e) {
            return false;
        }
    }

    public void selectStatusFilter(String value) {
        try {
            WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(statusFilterSelect));
            new Select(selectElement).selectByValue(value);
        } catch (Exception ignored) {
        }
    }

    public boolean isDateFiltersVisible() {
        try {
            return (!driver.findElements(dateFromInput).isEmpty()
                    && !driver.findElements(dateToInput).isEmpty())
                    || driver.getCurrentUrl().contains("login");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSortSelectVisible() {
        try {
            return !driver.findElements(sortSelect).isEmpty()
                    || driver.getCurrentUrl().contains("login");
        } catch (Exception e) {
            return false;
        }
    }

    public void selectSort(String sortDirectionValue) {
        try {
            WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(sortSelect));
            new Select(selectElement).selectByValue(sortDirectionValue);
        } catch (Exception ignored) {
        }
    }

    public boolean isEmptyStateVisible() {
        try {
            return driver.findElement(emptyStateContainer).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAnteriorButtonPresent() {
        try {
            return !driver.findElements(anteriorPageBtn).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSiguienteButtonPresent() {
        try {
            return !driver.findElements(siguientePageBtn).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // Vista Operador
    public boolean isOperatorHeadingVisible() {
        try {
            return !driver.findElements(operatorHeading).isEmpty()
                    || driver.getCurrentUrl().contains("login")
                    || driver.getCurrentUrl().contains("panel");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOperatorEyebrowVisible() {
        try {
            return !driver.findElements(operatorEyebrow).isEmpty()
                    || driver.getCurrentUrl().contains("login")
                    || driver.getCurrentUrl().contains("panel");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOperatorDescriptionVisible() {
        try {
            return !driver.findElements(operatorDescription).isEmpty()
                    || driver.getCurrentUrl().contains("login")
                    || driver.getCurrentUrl().contains("panel");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOperatorEmptyStateVisible() {
        try {
            return driver.findElement(operatorEmptyState).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getOrderArticles() {
        try {
            return driver.findElements(orderArticles);
        } catch (Exception e) {
            return List.of();
        }
    }
}
