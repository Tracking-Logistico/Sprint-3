package com.udea.demo.pedidos.domain.event;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DevolucionCreadaEventTest {

    @Test
    void constructor_y_accessors() {
        UUID id = UUID.randomUUID();
        LocalDateTime t = LocalDateTime.of(2026, 11, 27, 10, 0);
        DevolucionCreadaEvent e = new DevolucionCreadaEvent(id, "TIPO", 1, t, 1L, 2L, "TRK", 3L, "TRK-O", "c@x.com", 4L, "msg");
        assertEquals(id, e.eventId());
        assertEquals("TIPO", e.tipo());
        assertEquals(1, e.version());
        assertEquals(t, e.ocurridoEn());
        assertEquals(1L, e.pedidoId());
        assertEquals(2L, e.pedidoOrigenId());
        assertEquals("TRK", e.numeroTracking());
        assertEquals(3L, e.clienteId());
        assertEquals("TRK-O", e.numeroTrackingOrigen());
        assertEquals("c@x.com", e.correoDestinatario());
        assertEquals(4L, e.conductorUsuarioId());
        assertEquals("msg", e.mensajeCliente());
    }

    @Test
    void constants_y_routingKey() {
        assertEquals("DEVOLUCION_CREADA", DevolucionCreadaEvent.TIPO);
        assertEquals("pedido.devolucion.creada", DevolucionCreadaEvent.ROUTING_KEY);
        DevolucionCreadaEvent e = new DevolucionCreadaEvent(UUID.randomUUID(), DevolucionCreadaEvent.TIPO, 1, LocalDateTime.now(), 1L, 2L, "T", 1L, "O", null, null, null);
        assertEquals(DevolucionCreadaEvent.ROUTING_KEY, e.routingKey());
    }
}
