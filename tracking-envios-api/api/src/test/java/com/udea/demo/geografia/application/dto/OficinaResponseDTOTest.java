package com.udea.demo.geografia.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OficinaResponseDTOTest {

    @Test
    void constructor_y_accessors() {
        OficinaResponseDTO dto = new OficinaResponseDTO(1L, "Centro", 2L, "Medellin", "Antioquia", "Calle 1", "300", "o@x.com", true);
        assertEquals(1L, dto.id());
        assertEquals("Centro", dto.nombre());
        assertEquals(2L, dto.ciudadId());
        assertEquals("Medellin", dto.ciudad());
        assertEquals("Antioquia", dto.departamento());
        assertEquals("Calle 1", dto.direccion());
        assertEquals("300", dto.telefono());
        assertEquals("o@x.com", dto.email());
        assertTrue(dto.activa());
    }

    @Test
    void nulosEInactiva() {
        OficinaResponseDTO dto = new OficinaResponseDTO(null, null, null, null, null, null, null, null, false);
        assertNull(dto.id());
        assertNull(dto.email());
        assertFalse(dto.activa());
    }
}
