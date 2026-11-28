package com.udea.demo.rutas.application.service;

import com.udea.demo.pedidos.domain.event.EnvioGestionEvent;
import com.udea.demo.pedidos.domain.model.EstadoPedido;
import com.udea.demo.rutas.domain.model.EstadoParada;
import com.udea.demo.rutas.domain.model.Ruta;
import com.udea.demo.rutas.interfaces.persistence.ParadaRutaRepository;
import com.udea.demo.rutas.interfaces.persistence.RutaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.udea.demo.rutas.domain.model.ParadaRuta;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Prueba nueva (no modifica la existente) para el listener {@code alPausarOCancelar},
 * que quedaba sin cobertura. Cubre ramas PAUSADO / CANCELADO / otro / sin parada.
 */
@ExtendWith(MockitoExtension.class)
class CierreParadaPorDevolucionListenerAlPausarOCancelarTest {

    @Mock private ParadaRutaRepository paradas;
    @Mock private RutaRepository rutas;
    @InjectMocks private CierreParadaPorDevolucionListener listener;

    private EnvioGestionEvent evento(EstadoPedido estado) {
        return EnvioGestionEvent.de(7L, "TRK-7", 100L, estado, null, null, null);
    }

    @Test
    void estadoNoPausadoNiCancelado_noHaceNada() {
        listener.alPausarOCancelar(evento(EstadoPedido.EN_TRANSITO));
        verifyNoInteractions(paradas, rutas);
    }

    @Test
    void pausado_conParadaPendiente_guardaRutaSinCancelar() {
        ParadaRuta parada = org.mockito.Mockito.mock(ParadaRuta.class);
        Ruta ruta = org.mockito.Mockito.mock(Ruta.class);
        when(paradas.findByPedidoIdAndEstado(7L, EstadoParada.PENDIENTE)).thenReturn(Optional.of(parada));
        when(parada.getRuta()).thenReturn(ruta);

        listener.alPausarOCancelar(evento(EstadoPedido.PAUSADO));

        verify(ruta, never()).cancelarParada(any());
        verify(rutas).save(ruta);
    }

    @Test
    void cancelado_conParadaPendiente_cancelaParadaYGuarda() {
        ParadaRuta parada = org.mockito.Mockito.mock(ParadaRuta.class);
        Ruta ruta = org.mockito.Mockito.mock(Ruta.class);
        when(paradas.findByPedidoIdAndEstado(7L, EstadoParada.PENDIENTE)).thenReturn(Optional.of(parada));
        when(parada.getRuta()).thenReturn(ruta);

        listener.alPausarOCancelar(evento(EstadoPedido.CANCELADO));

        verify(ruta).cancelarParada(7L);
        verify(rutas).save(ruta);
    }

    @Test
    void pausadoOCancelado_sinParadaPendiente_noGuarda() {
        when(paradas.findByPedidoIdAndEstado(7L, EstadoParada.PENDIENTE)).thenReturn(Optional.empty());
        listener.alPausarOCancelar(evento(EstadoPedido.CANCELADO));
        verify(rutas, never()).save(any());
    }
}
