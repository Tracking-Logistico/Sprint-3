package com.udea.demo.pedidos.application.dto;

import com.udea.demo.pedidos.domain.model.EtapaUbicacion;
import com.udea.demo.pedidos.domain.model.OrigenUbicacion;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RegistrarUbicacionRequestDTOTest {

    @Test
    void constructor_y_accessors() {
        OffsetDateTime fecha = OffsetDateTime.parse("2026-11-27T10:00:00Z");
        RegistrarUbicacionRequestDTO dto = new RegistrarUbicacionRequestDTO(
                EtapaUbicacion.EN_TRANSITO_VEHICULO, OrigenUbicacion.APP_MOVIL,
                "11111111-1111-1111-1111-111111111111", "qr", 1L, null, null, "VEH-1", fecha, 9L);
        assertEquals(EtapaUbicacion.EN_TRANSITO_VEHICULO, dto.etapa());
        assertEquals(OrigenUbicacion.APP_MOVIL, dto.origen());
        assertEquals("11111111-1111-1111-1111-111111111111", dto.idEvento());
        assertEquals("qr", dto.codigoQr());
        assertEquals(1L, dto.ciudadId());
        assertNull(dto.oficinaId());
        assertNull(dto.puntoEntregaId());
        assertEquals("VEH-1", dto.vehiculoId());
        assertEquals(fecha, dto.fechaEvento());
        assertEquals(9L, dto.corrigeEventoId());
    }

    @Test
    void camposOpcionalesNull() {
        RegistrarUbicacionRequestDTO dto = new RegistrarUbicacionRequestDTO(
                EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, "id", null, 1L, 2L, null, null, OffsetDateTime.now(), null);
        assertNull(dto.codigoQr());
        assertNull(dto.vehiculoId());
        assertNull(dto.corrigeEventoId());
        assertEquals(2L, dto.oficinaId());
    }
}
