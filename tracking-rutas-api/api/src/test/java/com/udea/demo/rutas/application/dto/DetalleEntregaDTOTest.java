package com.udea.demo.rutas.application.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DetalleEntregaDTOTest {

    @Test
    void constructorCompleto_y_accessors() {
        DetalleEntregaDTO.EventoHistorialDTO ev = new DetalleEntregaDTO.EventoHistorialDTO("t", "d", LocalDateTime.of(2026, 1, 1, 8, 0));
        DetalleEntregaDTO dto = new DetalleEntregaDTO(1L, 2L, 3, "P", "TRK", "dir", "ciudad", "cp", "nombre", "tel",
                "acc", "desc", 1.0, 2.0, 3.0, 4.0, "ALTA", "PENDIENTE", "EN_TRANSITO", "obs",
                LocalDate.of(2026, 2, 1), List.of(ev), Boolean.TRUE, "CUMPLE");
        assertEquals(1L, dto.idParada());
        assertEquals(2L, dto.pedidoId());
        assertEquals(3, dto.orden());
        assertEquals("P", dto.numeroPedido());
        assertEquals("TRK", dto.numeroTracking());
        assertEquals("dir", dto.direccionDestino());
        assertEquals("ciudad", dto.ciudadDestino());
        assertEquals("cp", dto.codigoPostalDestino());
        assertEquals("nombre", dto.destinatarioNombre());
        assertEquals("tel", dto.destinatarioTelefono());
        assertEquals("acc", dto.indicacionesAcceso());
        assertEquals("desc", dto.descripcionPaquete());
        assertEquals(1.0, dto.pesoKg());
        assertEquals(2.0, dto.largoCm());
        assertEquals(3.0, dto.anchoCm());
        assertEquals(4.0, dto.altoCm());
        assertEquals("ALTA", dto.prioridad());
        assertEquals("PENDIENTE", dto.estadoParada());
        assertEquals("EN_TRANSITO", dto.estadoPedido());
        assertEquals("obs", dto.observacionesValidacion());
        assertEquals(LocalDate.of(2026, 2, 1), dto.fechaEntregaReprogramada());
        assertEquals(List.of(ev), dto.ultimosEventos());
        assertEquals(Boolean.TRUE, dto.requiereVerificacionEdad());
        assertEquals("CUMPLE", dto.resultadoVerificacionEdad());
    }

    @Test
    void constructorAnteriorHU13_22Args_defaults() {
        DetalleEntregaDTO dto = new DetalleEntregaDTO(1L, 2L, 3, "P", "TRK", "dir", "ciudad", "cp", "nombre", "tel",
                "acc", "desc", 1.0, 2.0, 3.0, 4.0, "ALTA", "PENDIENTE", "EN_TRANSITO", "obs",
                LocalDate.of(2026, 2, 1), List.of());
        assertEquals(Boolean.FALSE, dto.requiereVerificacionEdad());
        assertNull(dto.resultadoVerificacionEdad());
    }

    @Test
    void eventoHistorialDTO_record() {
        DetalleEntregaDTO.EventoHistorialDTO ev = new DetalleEntregaDTO.EventoHistorialDTO("t", "d", LocalDateTime.of(2026, 1, 1, 8, 0));
        assertEquals("t", ev.tipoEvento());
        assertEquals("d", ev.detalle());
        assertEquals(LocalDateTime.of(2026, 1, 1, 8, 0), ev.fecha());
        assertEquals(new DetalleEntregaDTO.EventoHistorialDTO("t", "d", LocalDateTime.of(2026, 1, 1, 8, 0)), ev);
    }
}
