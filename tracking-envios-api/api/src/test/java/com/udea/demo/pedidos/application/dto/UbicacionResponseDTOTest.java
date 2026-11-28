package com.udea.demo.pedidos.application.dto;

import com.udea.demo.pedidos.domain.model.EstadoPedido;
import com.udea.demo.pedidos.domain.model.EtapaUbicacion;
import com.udea.demo.pedidos.domain.model.OrigenUbicacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UbicacionResponseDTOTest {

    @Test
    void constructor_y_accessors() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 27, 10, 0);
        UbicacionResponseDTO dto = new UbicacionResponseDTO(1L, 2L, "TRK", EtapaUbicacion.EN_BODEGA_ORIGEN,
                EstadoPedido.RECIBIDO_EN_ORIGEN, 3L, "Medellin", 4L, "Oficina", 5L, "Punto", "Calle", "VEH",
                6L, OrigenUbicacion.WEB, t, t.plusMinutes(1), 7L);
        assertEquals(1L, dto.id());
        assertEquals(2L, dto.pedidoId());
        assertEquals("TRK", dto.numeroTracking());
        assertEquals(EtapaUbicacion.EN_BODEGA_ORIGEN, dto.etapa());
        assertEquals(EstadoPedido.RECIBIDO_EN_ORIGEN, dto.estadoPedido());
        assertEquals(3L, dto.ciudadId());
        assertEquals("Medellin", dto.ciudad());
        assertEquals(4L, dto.oficinaId());
        assertEquals("Oficina", dto.oficina());
        assertEquals(5L, dto.puntoEntregaId());
        assertEquals("Punto", dto.puntoEntrega());
        assertEquals("Calle", dto.direccion());
        assertEquals("VEH", dto.vehiculoId());
        assertEquals(6L, dto.usuarioId());
        assertEquals(OrigenUbicacion.WEB, dto.origen());
        assertEquals(t, dto.fechaEvento());
        assertEquals(t.plusMinutes(1), dto.fechaRegistro());
        assertEquals(7L, dto.corrigeEventoId());
    }

    @Test
    void nulos() {
        UbicacionResponseDTO dto = new UbicacionResponseDTO(null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null);
        assertNull(dto.id());
        assertNull(dto.origen());
        assertNull(dto.corrigeEventoId());
    }
}
