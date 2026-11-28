package com.udea.demo.pedidos.application.dto;

import com.udea.demo.pedidos.domain.model.TipoServicio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecibirPedidoRequestDTOTest {

    @Test
    void constructorCompleto_17Args() {
        RecibirPedidoRequestDTO dto = new RecibirPedidoRequestDTO(
                "Calle 67 # 53-108", "Medellin", "050001", "Carrera 7 # 32-16", "Bogota", "110111",
                "Paquete", 3.5, 25.0, 15.0, 10.0, TipoServicio.EXPRESS, "Carlos Gomez", "+573109876543",
                "c@x.com", "+573001234567", Boolean.TRUE);
        assertEquals("Calle 67 # 53-108", dto.direccionOrigen());
        assertEquals("Medellin", dto.ciudadOrigen());
        assertEquals("050001", dto.codigoPostalOrigen());
        assertEquals("Carrera 7 # 32-16", dto.direccionDestino());
        assertEquals("Bogota", dto.ciudadDestino());
        assertEquals("110111", dto.codigoPostalDestino());
        assertEquals("Paquete", dto.descripcionPaquete());
        assertEquals(3.5, dto.pesoKg());
        assertEquals(25.0, dto.largoCm());
        assertEquals(15.0, dto.anchoCm());
        assertEquals(10.0, dto.altoCm());
        assertEquals(TipoServicio.EXPRESS, dto.tipoServicio());
        assertEquals("Carlos Gomez", dto.destinatarioNombre());
        assertEquals("+573109876543", dto.destinatarioTelefono());
        assertEquals("c@x.com", dto.destinatarioEmail());
        assertEquals("+573001234567", dto.remitenteTelefono());
        assertEquals(Boolean.TRUE, dto.requiereVerificacionEdad());
    }

    @Test
    void constructorAnteriorHU13_16Args_requiereVerificacionEdadNull() {
        RecibirPedidoRequestDTO dto = new RecibirPedidoRequestDTO(
                "Calle 67 # 53-108", "Medellin", "050001", "Carrera 7 # 32-16", "Bogota", "110111",
                "Paquete", 3.5, 25.0, 15.0, 10.0, TipoServicio.ESTANDAR, "Carlos Gomez", "+573109876543",
                "c@x.com", "+573001234567");
        assertNull(dto.requiereVerificacionEdad());
        assertEquals("c@x.com", dto.destinatarioEmail());
    }

    @Test
    void constructorCorto_15Args_emailDestinoNull_y_verificacionNull() {
        RecibirPedidoRequestDTO dto = new RecibirPedidoRequestDTO(
                "Calle 67 # 53-108", "Medellin", "050001", "Carrera 7 # 32-16", "Bogota", "110111",
                "Paquete", 3.5, 25.0, 15.0, 10.0, TipoServicio.PROGRAMADO, "Carlos Gomez", "+573109876543",
                "+573001234567");
        assertNull(dto.destinatarioEmail());
        assertNull(dto.requiereVerificacionEdad());
        assertEquals(TipoServicio.PROGRAMADO, dto.tipoServicio());
    }
}
