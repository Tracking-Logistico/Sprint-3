package com.udea.demo.geografia.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CiudadResponseDTOTest {

    @Test
    void constructor_y_accessors() {
        CiudadResponseDTO dto = new CiudadResponseDTO(1L, "Medellin", "Antioquia", true);
        assertEquals(1L, dto.id());
        assertEquals("Medellin", dto.nombre());
        assertEquals("Antioquia", dto.departamento());
        assertTrue(dto.activa());
    }

    @Test
    void valoresNulosYFlagInactiva() {
        CiudadResponseDTO dto = new CiudadResponseDTO(null, null, null, false);
        assertNull(dto.id());
        assertNull(dto.nombre());
        assertNull(dto.departamento());
        assertFalse(dto.activa());
    }

    @Test
    void record_equals_hashCode() {
        assertEquals(new CiudadResponseDTO(1L, "x", "y", true), new CiudadResponseDTO(1L, "x", "y", true));
        assertNotEquals(new CiudadResponseDTO(1L, "x", "y", true), new CiudadResponseDTO(1L, "x", "y", false));
    }
}
