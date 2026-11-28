package com.udea.demo.pedidos.application.dto;

import com.udea.demo.pedidos.domain.model.EstadoPedido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GestionEnvioResponseDTOTest {

    @Test
    void constructor_y_accessors() {
        GestionEnvioResponseDTO dto = new GestionEnvioResponseDTO(1L, "TRK-1", EstadoPedido.EN_TRANSITO, "TRK-DEV");
        assertEquals(1L, dto.pedidoId());
        assertEquals("TRK-1", dto.numeroTracking());
        assertEquals(EstadoPedido.EN_TRANSITO, dto.estado());
        assertEquals("TRK-DEV", dto.numeroTrackingDevolucion());
    }

    @Test
    void nulosYSinDevolucion() {
        GestionEnvioResponseDTO dto = new GestionEnvioResponseDTO(null, null, null, null);
        assertNull(dto.pedidoId());
        assertNull(dto.numeroTracking());
        assertNull(dto.estado());
        assertNull(dto.numeroTrackingDevolucion());
    }

    @Test
    void record_equals() {
        assertEquals(new GestionEnvioResponseDTO(1L, "a", EstadoPedido.CREADO, null), new GestionEnvioResponseDTO(1L, "a", EstadoPedido.CREADO, null));
        assertNotEquals(new GestionEnvioResponseDTO(1L, "a", EstadoPedido.CREADO, null), new GestionEnvioResponseDTO(2L, "a", EstadoPedido.CREADO, null));
    }
}
