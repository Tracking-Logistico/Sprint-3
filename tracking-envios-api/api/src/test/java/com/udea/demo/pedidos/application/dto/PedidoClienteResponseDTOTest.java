package com.udea.demo.pedidos.application.dto;

import com.udea.demo.pedidos.domain.model.EstadoPedido;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PedidoClienteResponseDTOTest {

    private final LocalDateTime t = LocalDateTime.of(2026, 1, 1, 10, 0);

    @Test
    void constructorCompleto_11Args() {
        PedidoClienteResponseDTO dto = new PedidoClienteResponseDTO(1L, "P-1", "TRK-1", "Ana", "Beto",
                EstadoPedido.EN_TRANSITO, t, t.plusDays(1), "TRK-DEV", "REF", Boolean.TRUE);
        assertEquals(1L, dto.id());
        assertEquals("P-1", dto.numeroPedido());
        assertEquals("TRK-1", dto.numeroTracking());
        assertEquals("Ana", dto.remitenteNombre());
        assertEquals("Beto", dto.destinatarioNombre());
        assertEquals(EstadoPedido.EN_TRANSITO, dto.estado());
        assertEquals(t, dto.fechaCreacion());
        assertEquals(t.plusDays(1), dto.fechaEstimadaEntrega());
        assertEquals("TRK-DEV", dto.numeroTrackingOrigenDevolucion());
        assertEquals("REF", dto.referenciaDevolucion());
        assertEquals(Boolean.TRUE, dto.requiereVerificacionEdad());
    }

    @Test
    void constructorAnteriorHU13_10Args_defaultRequiereVerificacionEdadFalse() {
        PedidoClienteResponseDTO dto = new PedidoClienteResponseDTO(1L, "P-1", "TRK-1", "Ana", "Beto",
                EstadoPedido.CREADO, t, t.plusDays(1), "TRK-DEV", "REF");
        assertEquals("TRK-DEV", dto.numeroTrackingOrigenDevolucion());
        assertEquals("REF", dto.referenciaDevolucion());
        assertEquals(Boolean.FALSE, dto.requiereVerificacionEdad());
    }

    @Test
    void constructorCorto_8Args_defaults() {
        PedidoClienteResponseDTO dto = new PedidoClienteResponseDTO(1L, "P-1", "TRK-1", "Ana", "Beto",
                EstadoPedido.SOLICITADO, t, t.plusDays(1));
        assertNull(dto.numeroTrackingOrigenDevolucion());
        assertNull(dto.referenciaDevolucion());
        assertEquals(Boolean.FALSE, dto.requiereVerificacionEdad());
    }
}
