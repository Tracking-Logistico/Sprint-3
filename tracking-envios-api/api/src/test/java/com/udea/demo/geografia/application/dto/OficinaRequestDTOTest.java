package com.udea.demo.geografia.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OficinaRequestDTOTest {

    @Test
    void constructor_y_accessors() {
        OficinaRequestDTO dto = new OficinaRequestDTO("Centro", 1L, "Calle 1", "+57 300 123 4567", "o@x.com");
        assertEquals("Centro", dto.nombre());
        assertEquals(1L, dto.ciudadId());
        assertEquals("Calle 1", dto.direccion());
        assertEquals("+57 300 123 4567", dto.telefono());
        assertEquals("o@x.com", dto.email());
    }

    @Test
    void emailYCiudadPuedenSerNull_salvo_validacion_externa() {
        OficinaRequestDTO dto = new OficinaRequestDTO("Centro", null, "Calle 1", "+573001234567", null);
        assertNull(dto.ciudadId());
        assertNull(dto.email());
    }

    @Test
    void record_equals() {
        assertEquals(new OficinaRequestDTO("a", 1L, "d", "t", "e"), new OficinaRequestDTO("a", 1L, "d", "t", "e"));
        assertNotEquals(new OficinaRequestDTO("a", 1L, "d", "t", "e"), new OficinaRequestDTO("a", 2L, "d", "t", "e"));
    }
}
