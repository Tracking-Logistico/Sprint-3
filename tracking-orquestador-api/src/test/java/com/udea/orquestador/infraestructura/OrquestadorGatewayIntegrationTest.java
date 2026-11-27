package com.udea.orquestador.infraestructura;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CorsFilter;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para el API Gateway / Orquestador.
 *
 * Valida el comportamiento de punta a punta a través del pipeline HTTP de Spring Boot:
 *   - Verificación de salud interna del proceso (/internal/health).
 *   - Verificación del estado de servicios upstream (/health).
 *   - Política de CORS unificada para el frontend (pre-flight OPTIONS y headers).
 *   - Enrutamiento y respuesta 502/504 en aislamiento cuando el backend destino no está activo.
 *   - Descarte y retorno 404 para rutas no mapeadas.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "upstreams.identidad=http://127.0.0.1:18081",
        "upstreams.envios=http://127.0.0.1:18082",
        "upstreams.rutas=http://127.0.0.1:18083",
        "app.cors.allowed-origin=http://localhost:5173",
        "gateway.connect-timeout-ms=500",
        "gateway.request-timeout-ms=1000",
        "server.port=0"
})
class OrquestadorGatewayIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CorsFilter corsFilter;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(corsFilter)
                .build();
    }

    // =========================================================================
    // 1. Diagnóstico y Salud del Proceso
    // =========================================================================

    @Test
    @DisplayName("GET /internal/health debe responder 200 con status UP")
    void testInternalHealthRespondeUp() throws Exception {
        mockMvc.perform(get("/internal/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    @DisplayName("GET /health debe retornar el mapa de servicios con estado estructurado")
    void testHealthRetornaDiagnosticoDeServicios() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // 200 si los servicios están arriba o 503 si están caídos/aislados en prueba
                    org.junit.jupiter.api.Assertions.assertTrue(
                            status == 200 || status == 503,
                            "El endpoint /health debe responder 200 (OK) o 503 (DEGRADED)"
                    );
                })
                .andExpect(jsonPath("$.estado", notNullValue()))
                .andExpect(jsonPath("$.servicios", notNullValue()));
    }

    // =========================================================================
    // 2. Política de CORS (Pre-flight OPTIONS)
    // =========================================================================

    @Test
    @DisplayName("OPTIONS /api/v1/pedidos con Origin del frontend debe responder 200 con headers CORS")
    void testPreflightCorsHaciaPedidos() throws Exception {
        mockMvc.perform(options("/api/v1/pedidos")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("GET")))
                .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_MAX_AGE));
    }

    @Test
    @DisplayName("OPTIONS /api/v1/auth/login con Origin del frontend debe permitir POST")
    void testPreflightCorsHaciaAuth() throws Exception {
        mockMvc.perform(options("/api/v1/auth/login")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("POST")));
    }

    // =========================================================================
    // 3. Manejo de Rutas Desconocidas (404)
    // =========================================================================

    @Test
    @DisplayName("Rutas no registradas deben responder 404 Not Found")
    void testRutasDesconocidasResponden404() throws Exception {
        mockMvc.perform(get("/api/v1/modulo-inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Rutas con prefijos engañosos (/api/v1/authentication) deben responder 404")
    void testPrefijosParecidosNoSonAceptados() throws Exception {
        mockMvc.perform(post("/api/v1/authentication"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Acceso directo a la raíz de la API /api/v1 sin recurso debe responder 404")
    void testApiSinRecursoResponde404() throws Exception {
        mockMvc.perform(get("/api/v1"))
                .andExpect(status().isNotFound());
    }

    // =========================================================================
    // 4. Enrutamiento y Tolerancia a Fallos (502 cuando upstream no responde)
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/pedidos debe enrutar hacia envíos y retornar 502 al no estar disponible")
    void testRutaPedidosResponde502AlEstarServicioOffline() throws Exception {
        mockMvc.perform(get("/api/v1/pedidos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is("SERVICIO_NO_DISPONIBLE")));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login debe enrutar hacia identidad y responder 502 si está offline")
    void testRutaAuthResponde502AlEstarServicioOffline() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@udea.edu.co\",\"password\":\"123456\"}"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is("SERVICIO_NO_DISPONIBLE")));
    }

    @Test
    @DisplayName("GET /api/v1/rutas debe enrutar hacia rutas y responder 502 si está offline")
    void testRutaRutasResponde502AlEstarServicioOffline() throws Exception {
        mockMvc.perform(get("/api/v1/rutas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer mock-driver-token"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is("SERVICIO_NO_DISPONIBLE")));
    }
}
