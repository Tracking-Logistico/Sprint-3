package com.udea.demo.pedidos.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GestionEnvioRequestDTOTest {

    @Test
    void constructor_y_accessors() {
        GestionEnvioRequestDTO dto = new GestionEnvioRequestDTO("motivo x", true);
        assertEquals("motivo x", dto.motivo());
        assertEquals(Boolean.TRUE, dto.confirmar());
    }

    @Test
    void confirmarNullOFalse() {
        assertNull(new GestionEnvioRequestDTO("x", null).confirmar());
        assertEquals(Boolean.FALSE, new GestionEnvioRequestDTO("x", false).confirmar());
    }

    @Test
    void record_equals() {
        assertEquals(new GestionEnvioRequestDTO("a", true), new GestionEnvioRequestDTO("a", true));
        assertNotEquals(new GestionEnvioRequestDTO("a", true), new GestionEnvioRequestDTO("a", false));
    }
}
