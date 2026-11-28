package com.udea.demo.geografia.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PuntoEntregaResponseDTOTest {

    @Test
    void constructor_y_accessors() {
        PuntoEntregaResponseDTO dto = new PuntoEntregaResponseDTO(1L, "Punto A", 2L, "Medellin", 3L, "Oficina", "Calle 2", "ref", true);
        assertEquals(1L, dto.id());
        assertEquals("Punto A", dto.nombre());
        assertEquals(2L, dto.ciudadId());
        assertEquals("Medellin", dto.ciudad());
        assertEquals(3L, dto.oficinaId());
        assertEquals("Oficina", dto.oficina());
        assertEquals("Calle 2", dto.direccion());
        assertEquals("ref", dto.descripcion());
        assertTrue(dto.activo());
    }

    @Test
    void nulosEInactivo() {
        PuntoEntregaResponseDTO dto = new PuntoEntregaResponseDTO(null, null, null, null, null, null, null, null, false);
        assertNull(dto.id());
        assertNull(dto.oficinaId());
        assertFalse(dto.activo());
    }
}
