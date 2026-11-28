package com.udea.demo.geografia.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PuntoEntregaRequestDTOTest {

    @Test
    void constructor_y_accessors() {
        PuntoEntregaRequestDTO dto = new PuntoEntregaRequestDTO("Punto A", 1L, 2L, "Calle 2", "ref");
        assertEquals("Punto A", dto.nombre());
        assertEquals(1L, dto.ciudadId());
        assertEquals(2L, dto.oficinaId());
        assertEquals("Calle 2", dto.direccion());
        assertEquals("ref", dto.descripcion());
    }

    @Test
    void oficinaIdYdescripcionOpcionales() {
        PuntoEntregaRequestDTO dto = new PuntoEntregaRequestDTO("Punto A", 1L, null, "Calle 2", null);
        assertNull(dto.oficinaId());
        assertNull(dto.descripcion());
    }
}
