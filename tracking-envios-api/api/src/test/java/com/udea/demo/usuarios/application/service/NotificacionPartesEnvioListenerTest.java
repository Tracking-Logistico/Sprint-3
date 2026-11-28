package com.udea.demo.usuarios.application.service;

import com.udea.demo.pedidos.domain.event.DevolucionCreadaEvent;
import com.udea.demo.pedidos.domain.event.EnvioGestionEvent;
import com.udea.demo.pedidos.domain.model.EstadoPedido;
import com.udea.demo.usuarios.interfaces.services.EmailServiceI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificacionPartesEnvioListenerTest {

    @Mock private EmailServiceI emailService;
    @Mock private JdbcTemplate jdbc;
    @InjectMocks private NotificacionPartesEnvioListener listener;

    // ---------- notificar(EnvioGestionEvent) ----------

    @Test
    void envioGestion_conCorreo_enviaEmailYRegistraConductor() {
        EnvioGestionEvent e = EnvioGestionEvent.de(1L, "TRK-1", 100L, EstadoPedido.ENTREGADO, "ok", "dest@x.com", 7L);
        listener.notificar(e);
        verify(emailService).enviarNotificacionCambioEstado(eq("dest@x.com"), anyString(), anyString());
        verify(jdbc).update(anyString(), eq(7L), eq(1L), anyString(), anyString());
    }

    @Test
    void envioGestion_sinCorreo_noEnviaEmailPeroRegistraConductor() {
        EnvioGestionEvent e = EnvioGestionEvent.de(1L, "TRK-1", 100L, EstadoPedido.PAUSADO, null, "  ", 7L);
        listener.notificar(e);
        verify(emailService, never()).enviarNotificacionCambioEstado(anyString(), anyString(), anyString());
        verify(jdbc).update(anyString(), eq(7L), eq(1L), anyString(), anyString());
    }

    @Test
    void envioGestion_sinConductor_noRegistraConductor() {
        EnvioGestionEvent e = EnvioGestionEvent.de(1L, "TRK-1", 100L, EstadoPedido.CANCELADO, null, "dest@x.com", null);
        listener.notificar(e);
        verify(jdbc, never()).update(anyString(), any(), any(), anyString(), anyString());
    }

    @Test
    void envioGestion_falloEmail_noPropaga() {
        EnvioGestionEvent e = EnvioGestionEvent.de(1L, "TRK-1", 100L, EstadoPedido.ENTREGA_FALLIDA, null, "dest@x.com", null);
        doThrow(new RuntimeException("mail error")).when(emailService).enviarNotificacionCambioEstado(anyString(), anyString(), anyString());
        assertThatCode(() -> listener.notificar(e)).doesNotThrowAnyException();
    }

    @Test
    void conductor_falloInsercion_duplicadoYGenerico_noPropaga() {
        EnvioGestionEvent dup = EnvioGestionEvent.de(1L, "TRK-1", 100L, EstadoPedido.ENTREGADO, null, null, 7L);
        org.mockito.Mockito.doThrow(new DataIntegrityViolationException("dup")).when(jdbc).update(anyString(), any(), any(), anyString(), anyString());
        assertThatCode(() -> listener.notificar(dup)).doesNotThrowAnyException();

        org.mockito.Mockito.reset(jdbc);
        EnvioGestionEvent err = EnvioGestionEvent.de(1L, "TRK-1", 100L, EstadoPedido.ENTREGADO, null, null, 7L);
        org.mockito.Mockito.doThrow(new RuntimeException("db")).when(jdbc).update(anyString(), any(), any(), anyString(), anyString());
        assertThatCode(() -> listener.notificar(err)).doesNotThrowAnyException();
    }

    // ---------- notificar(DevolucionCreadaEvent) ----------

    @Test
    void devolucionCreada_registraConductor() {
        DevolucionCreadaEvent e = DevolucionCreadaEvent.de(10L, "TRK-DEV", 100L, 5L, "TRK-ORIG", "x@x.com", 7L, "msg");
        listener.notificar(e);
        verify(jdbc).update(anyString(), eq(7L), eq(5L), anyString(), anyString());
        verifyNoInteractions(emailService);
    }

    @Test
    void devolucionCreada_sinConductor_noRegistra() {
        DevolucionCreadaEvent e = DevolucionCreadaEvent.de(10L, "TRK-DEV", 100L, 5L, "TRK-ORIG", "x@x.com", null, "msg");
        listener.notificar(e);
        verify(jdbc, never()).update(anyString(), any(), any(), anyString(), anyString());
    }
}
