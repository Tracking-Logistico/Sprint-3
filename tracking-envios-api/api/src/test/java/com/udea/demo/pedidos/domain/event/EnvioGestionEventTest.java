package com.udea.demo.pedidos.domain.event;

import com.udea.demo.pedidos.domain.model.EstadoPedido;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EnvioGestionEventTest {

    @Test
    void constructor_y_accessors() {
        UUID id = UUID.randomUUID();
        LocalDateTime t = LocalDateTime.of(2026, 11, 27, 10, 0);
        EnvioGestionEvent e = new EnvioGestionEvent(id, "TIPO", 1, t, 1L, "TRK", 2L, EstadoPedido.PAUSADO, "m", "c@x.com", 3L);
        assertEquals(id, e.eventId());
        assertEquals("TIPO", e.tipo());
        assertEquals(1, e.version());
        assertEquals(t, e.ocurridoEn());
        assertEquals(1L, e.pedidoId());
        assertEquals("TRK", e.numeroTracking());
        assertEquals(2L, e.clienteId());
        assertEquals(EstadoPedido.PAUSADO, e.estado());
        assertEquals("m", e.motivo());
        assertEquals("c@x.com", e.correoDestinatario());
        assertEquals(3L, e.conductorUsuarioId());
    }

    @Test
    void factory_de() {
        EnvioGestionEvent e = EnvioGestionEvent.de(1L, "TRK", 2L, EstadoPedido.CANCELADO, "m", "c@x.com", 3L);
        assertNotNull(e.eventId());
        assertEquals(EnvioGestionEvent.TIPO, e.tipo());
        assertEquals(1, e.version());
        assertNotNull(e.ocurridoEn());
        assertEquals(1L, e.pedidoId());
        assertEquals("TRK", e.numeroTracking());
        assertEquals(2L, e.clienteId());
        assertEquals(EstadoPedido.CANCELADO, e.estado());
        assertEquals("m", e.motivo());
        assertEquals("c@x.com", e.correoDestinatario());
        assertEquals(3L, e.conductorUsuarioId());
    }

    @Test
    void constants_y_routingKey() {
        assertEquals("ENVIO_GESTIONADO", EnvioGestionEvent.TIPO);
        assertEquals("pedido.gestionado", EnvioGestionEvent.ROUTING_KEY);
        EnvioGestionEvent e = EnvioGestionEvent.de(1L, "T", 1L, EstadoPedido.ENTREGADO, null, null, null);
        assertEquals(EnvioGestionEvent.ROUTING_KEY, e.routingKey());
    }
}
