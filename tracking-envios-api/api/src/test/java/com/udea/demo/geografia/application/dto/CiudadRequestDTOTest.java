package com.udea.demo.geografia.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CiudadRequestDTOTest {

    @Test
    void constructor_y_accessors() {
        CiudadRequestDTO dto = new CiudadRequestDTO("Medellin", "Antioquia");
        assertEquals("Medellin", dto.nombre());
        assertEquals("Antioquia", dto.departamento());
    }

    @Test
    void record_equals_hashCode_toString() {
        CiudadRequestDTO a = new CiudadRequestDTO("Medellin", "Antioquia");
        CiudadRequestDTO b = new CiudadRequestDTO("Medellin", "Antioquia");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.toString().contains("Medellin"));
        assertNotEquals(a, new CiudadRequestDTO("Bogota", "Cundinamarca"));
    }
}
