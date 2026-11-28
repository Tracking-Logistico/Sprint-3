package com.udea.demo.pedidos.application.dto;

import com.udea.demo.pedidos.domain.model.EstadoPedido;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PedidoClienteResponseDTOTest {

    private final LocalDateTime t = LocalDateTime.of(2026, 1, 1, 10, 0);

    @Test
    void constructorCompleto_9Args() {
        PedidoClienteResponseDTO dto = new PedidoClienteResponseDTO(1L, "P", "TRK", "Ana", "Beto", EstadoPedido.EN_TRANSITO, t, t.plusDays(1), Boolean.TRUE);
        assertEquals(1L, dto.id());
        assertEquals("P", dto.numeroPedido());
        assertEquals("TRK", dto.numeroTracking());
        assertEquals("Ana", dto.remitenteNombre());
        assertEquals("Beto", dto.destinatarioNombre());
        assertEquals(EstadoPedido.EN_TRANSITO, dto.estado());
        assertEquals(t, dto.fechaCreacion());
        assertEquals(t.plusDays(1), dto.fechaEstimadaEntrega());
        assertEquals(Boolean.TRUE, dto.requiereVerificacionEdad());
    }

    @Test
    void constructorAnteriorHU13_8Args_defaultFalse() {
        PedidoClienteResponseDTO dto = new PedidoClienteResponseDTO(1L, "P", "TRK", "Ana", "Beto", EstadoPedido.CREADO, t, t.plusDays(1));
        assertEquals(Boolean.FALSE, dto.requiereVerificacionEdad());
    }
}
