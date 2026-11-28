package com.udea.demo.pedidos.application.service;

import com.udea.demo.geografia.application.dto.CiudadResponseDTO;
import com.udea.demo.pedidos.application.dto.RegistrarUbicacionRequestDTO;
import com.udea.demo.pedidos.application.dto.UbicacionResponseDTO;
import com.udea.demo.pedidos.domain.exception.CodigoQrInvalidoException;
import com.udea.demo.pedidos.domain.exception.CodigoQrOtroEnvioException;
import com.udea.demo.pedidos.domain.exception.EnvioFinalizadoException;
import com.udea.demo.pedidos.domain.exception.PedidoNoEncontradoException;
import com.udea.demo.pedidos.domain.exception.TransicionEstadoInvalidaException;
import com.udea.demo.pedidos.domain.model.CodigoQrEnvio;
import com.udea.demo.pedidos.domain.model.EstadoPedido;
import com.udea.demo.pedidos.domain.model.EtapaUbicacion;
import com.udea.demo.pedidos.domain.model.EventoUbicacionPedido;
import com.udea.demo.pedidos.domain.model.OrigenUbicacion;
import com.udea.demo.pedidos.domain.model.Pedido;
import com.udea.demo.pedidos.interfaces.persistence.EventoUbicacionPedidoRepository;
import com.udea.demo.pedidos.interfaces.persistence.PedidoRepository;
import com.udea.demo.pedidos.interfaces.services.AccesoPedidoConductorI;
import com.udea.demo.usuarios.application.service.ActorAuthorizationService;
import com.udea.demo.usuarios.domain.model.Rol;
import com.udea.demo.usuarios.domain.model.Usuario;
import com.udea.demo.usuarios.interfaces.persistence.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class TrazabilidadUbicacionServiceTest {

    @Mock private PedidoRepository pedidos;
    @Mock private EventoUbicacionPedidoRepository eventos;
    @Mock private JdbcTemplate jdbc;
    @Mock private ActorAuthorizationService actores;
    @Mock private ClienteRepository clientes;
    @Mock private AccesoPedidoConductorI accesoConductor;
    @Mock private RegistroHistorialPedido historial;
    @Mock private AccesoClientePedido accesoCliente;

    @Mock private Pedido pedido;
    @Mock private Usuario actor;

    private TrazabilidadUbicacionService service;

    private static final Long PEDIDO_ID = 1L;
    private static final Long USUARIO_ID = 7L;
    private static final String ID_EVENTO = "11111111-1111-1111-1111-111111111111";
    private static final String TRACKING = "TRK-1";

    @BeforeEach
    void setUp() throws Exception {
        service = new TrazabilidadUbicacionService(pedidos, eventos, jdbc, actores, clientes, accesoConductor, historial, accesoCliente);
        SecurityContextHolder.clearContext();
    }

    // ---------- registrar: flujo feliz ----------

    @Test
    void registrar_operador_enBodegaOrigen_persisteEventoYAvanzaEstado() throws Exception {
        stubActor(Rol.OPERADOR);
        stubPedido(EstadoPedido.CREADO);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        when(eventos.findByIdEvento(ID_EVENTO)).thenReturn(Optional.empty());
        when(eventos.saveAndFlush(any(EventoUbicacionPedido.class))).thenAnswer(inv -> inv.getArgument(0));
        when(pedido.puedeCambiarEstadoLogisticoA(any(EstadoPedido.class))).thenReturn(true);
        stubJdbc();

        RegistrarUbicacionRequestDTO dto = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, null, 1L, 10L, null, null, null);

        UbicacionResponseDTO r = service.registrar(PEDIDO_ID, dto);

        assertNotNull(r);
        assertEquals(EstadoPedido.RECIBIDO_EN_ORIGEN, r.estadoPedido());
        assertEquals(EtapaUbicacion.EN_BODEGA_ORIGEN, r.etapa());
        assertEquals("Medellin", r.ciudad());
        assertEquals("Oficina Centro", r.oficina());
        assertEquals("Calle 10", r.direccion());
        assertNull(r.puntoEntrega());
        verify(pedido).cambiarEstadoLogistico(EstadoPedido.RECIBIDO_EN_ORIGEN);
        verify(pedidos).save(pedido);
        verify(eventos).saveAndFlush(any(EventoUbicacionPedido.class));
        verify(historial).registrar(eq(PEDIDO_ID), eq(USUARIO_ID), eq(RegistroHistorialPedido.ESTADO_LOGISTICO), eq("UBICACION_" + OrigenUbicacion.WEB.name()), eq(EtapaUbicacion.EN_BODEGA_ORIGEN.name()), any(LocalDateTime.class));
    }

    @Test
    void registrar_operador_enTransitoVehiculo_requiereVehiculoYEstado() throws Exception {
        stubActor(Rol.OPERADOR);
        stubPedido(EstadoPedido.RECIBIDO_EN_ORIGEN);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        when(eventos.findByIdEvento(ID_EVENTO)).thenReturn(Optional.empty());
        when(eventos.saveAndFlush(any(EventoUbicacionPedido.class))).thenAnswer(inv -> inv.getArgument(0));
        when(pedido.puedeCambiarEstadoLogisticoA(any(EstadoPedido.class))).thenReturn(true);
        stubJdbc();

        RegistrarUbicacionRequestDTO dto = dto(EtapaUbicacion.EN_TRANSITO_VEHICULO, OrigenUbicacion.APP_MOVIL, null, 1L, null, null, "  ", null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.registrar(PEDIDO_ID, dto));
        assertTrue(ex.getMessage().contains("vehículo"));

        RegistrarUbicacionRequestDTO ok = dto(EtapaUbicacion.EN_TRANSITO_VEHICULO, OrigenUbicacion.APP_MOVIL, null, 1L, null, null, "  VEH-9  ", null);
        UbicacionResponseDTO r = service.registrar(PEDIDO_ID, ok);
        assertEquals("VEH-9", r.vehiculoId()); // normalizar
        assertNull(r.oficina());
    }

    // ---------- registrar: control de acceso ----------

    @Test
    void registrar_clienteNoPuedeRegistrar_lanzaAccessDenied() {
        stubActor(Rol.CLIENTE); // CLIENTE no puede registrar ubicación
        stubPedido(EstadoPedido.CREADO);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido)); // se consulta antes de rechazar rol
        assertThrows(AccessDeniedException.class, () -> service.registrar(PEDIDO_ID, dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, null, 1L, 10L, null, null, null)));
    }

    @Test
    void registrar_conductorSinAsignacion_lanzaAccessDenied() {
        stubActor(Rol.CONDUCTOR);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        stubPedido(EstadoPedido.RECIBIDO_EN_ORIGEN);
        when(accesoConductor.tieneAsignacionActiva(PEDIDO_ID, USUARIO_ID)).thenReturn(false);

        RegistrarUbicacionRequestDTO dto = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, null, 1L, 10L, null, null, null);
        assertThrows(AccessDeniedException.class, () -> service.registrar(PEDIDO_ID, dto));
    }

    @Test
    void registrar_pedidoNoEncontrado_lanza() {
        stubActor(Rol.OPERADOR);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.empty());
        assertThrows(PedidoNoEncontradoException.class, () -> service.registrar(PEDIDO_ID, dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, null, 1L, 10L, null, null, null)));
    }

    // ---------- registrar: QR ----------

    @Test
    void registrar_qrValidoOk_otroEnvioYchecksumInvalido_lanzan() throws Exception {
        stubActor(Rol.OPERADOR);
        stubPedido(EstadoPedido.CREADO);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        when(eventos.findByIdEvento(anyString())).thenReturn(Optional.empty());
        when(eventos.saveAndFlush(any(EventoUbicacionPedido.class))).thenAnswer(inv -> inv.getArgument(0));
        when(pedido.puedeCambiarEstadoLogisticoA(any(EstadoPedido.class))).thenReturn(true);
        stubJdbc();

        // QR válido para este envío
        String qr = CodigoQrEnvio.para(TRACKING).contenido();
        RegistrarUbicacionRequestDTO okQr = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.QR, qr, 1L, 10L, null, null, null);
        assertDoesNotThrow(() -> service.registrar(PEDIDO_ID, okQr));

        // QR de otro envío
        String qrOtro = CodigoQrEnvio.para("TRK-OTRO").contenido();
        RegistrarUbicacionRequestDTO otro = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.QR, qrOtro, 1L, 10L, null, null, null);
        assertThrows(CodigoQrOtroEnvioException.class, () -> service.registrar(PEDIDO_ID, otro));

        // QR malformado
        RegistrarUbicacionRequestDTO malformed = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.QR, "no-es-qr", 1L, 10L, null, null, null);
        assertThrows(CodigoQrInvalidoException.class, () -> service.registrar(PEDIDO_ID, malformed));

        // origen no-QR con código no vacío
        RegistrarUbicacionRequestDTO noQrConCodigo = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, "x", 1L, 10L, null, null, null);
        assertThrows(CodigoQrInvalidoException.class, () -> service.registrar(PEDIDO_ID, noQrConCodigo));
    }

    // ---------- registrar: idempotencia ----------

    @Test
    void registrar_eventoYaExistente_mismoRegistroDevuelveSinGuardar() {
        stubActor(Rol.OPERADOR);
        stubPedido(EstadoPedido.CREADO);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        RegistrarUbicacionRequestDTO dto = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, null, 1L, 10L, null, null, null);

        EventoUbicacionPedido previo = evento(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, 1L, 10L, null, null, null);
        when(eventos.findByIdEvento(ID_EVENTO)).thenReturn(Optional.of(previo));

        UbicacionResponseDTO r = service.registrar(PEDIDO_ID, dto);
        assertNotNull(r);
        verify(eventos, never()).saveAndFlush(any());
        verify(pedidos, never()).save(any());
    }

    @Test
    void registrar_eventoYaExistente_conflicto_lanza() {
        stubActor(Rol.OPERADOR);
        stubPedido(EstadoPedido.CREADO);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        RegistrarUbicacionRequestDTO dto = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, null, 1L, 10L, null, null, null);

        // mismo idEvento pero otro pedido
        EventoUbicacionPedido previoOtroPedido = evento(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, 1L, 10L, null, null, null);
        previoOtroPedido = new EventoUbicacionPedido(ID_EVENTO, 999L, TRACKING, EtapaUbicacion.EN_BODEGA_ORIGEN, EstadoPedido.RECIBIDO_EN_ORIGEN, 1L, "Medellin", 10L, "Oficina Centro", null, null, "Calle 10", null, USUARIO_ID, OrigenUbicacion.WEB, LocalDateTime.now(), null);
        when(eventos.findByIdEvento(ID_EVENTO)).thenReturn(Optional.of(previoOtroPedido));
        assertThrows(IllegalArgumentException.class, () -> service.registrar(PEDIDO_ID, dto));
    }

    // ---------- registrar: transiciones ----------

    @Test
    void registrar_estadoFinal_lanzaEnvioFinalizado() {
        stubActor(Rol.OPERADOR);
        stubPedido(EstadoPedido.ENTREGADO);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        when(eventos.findByIdEvento(ID_EVENTO)).thenReturn(Optional.empty());
        when(pedido.getEstado()).thenReturn(EstadoPedido.ENTREGADO); // esFinal real
        // EstadoPedido.ENTREGADO.esFinal()==true
        RegistrarUbicacionRequestDTO dto = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, null, 1L, 10L, null, null, null);
        assertThrows(EnvioFinalizadoException.class, () -> service.registrar(PEDIDO_ID, dto));
    }

    @Test
    void registrar_transicionInvalida_lanza() {
        stubActor(Rol.OPERADOR);
        stubPedido(EstadoPedido.SOLICITADO); // EN_BODEGA_ORIGEN exige CREADO/RECIBIDO_EN_ORIGEN
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        when(eventos.findByIdEvento(ID_EVENTO)).thenReturn(Optional.empty());
        RegistrarUbicacionRequestDTO dto = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, null, 1L, 10L, null, null, null);
        assertThrows(TransicionEstadoInvalidaException.class, () -> service.registrar(PEDIDO_ID, dto));
    }

    @Test
    void registrar_fechaFutura_lanza() {
        stubActor(Rol.OPERADOR);
        stubPedido(EstadoPedido.CREADO);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        when(eventos.findByIdEvento(ID_EVENTO)).thenReturn(Optional.empty());
        RegistrarUbicacionRequestDTO dto = new RegistrarUbicacionRequestDTO(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, ID_EVENTO, null, 1L, 10L, null, null, OffsetDateTime.now().plusDays(2), null);
        assertThrows(IllegalArgumentException.class, () -> service.registrar(PEDIDO_ID, dto));
    }

    // ---------- registrar: ciudad / oficina / punto ----------

    @Test
    void registrar_ciudadInexistente_lanza() {
        stubActor(Rol.OPERADOR);
        stubPedido(EstadoPedido.CREADO);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        when(eventos.findByIdEvento(ID_EVENTO)).thenReturn(Optional.empty());
        when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(List.of());
        RegistrarUbicacionRequestDTO dto = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, null, 1L, 10L, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> service.registrar(PEDIDO_ID, dto));
    }

    @Test
    void registrar_enBodegaOrigenSinOficina_lanza() throws Exception {
        stubActor(Rol.OPERADOR);
        stubPedido(EstadoPedido.CREADO);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        when(eventos.findByIdEvento(ID_EVENTO)).thenReturn(Optional.empty());
        stubJdbc();
        RegistrarUbicacionRequestDTO dto = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, null, 1L, null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> service.registrar(PEDIDO_ID, dto));
    }

    @Test
    void registrar_oficinaNoPerteneceCiudad_lanza() throws Exception {
        stubActor(Rol.OPERADOR);
        stubPedido(EstadoPedido.CREADO);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        when(eventos.findByIdEvento(ID_EVENTO)).thenReturn(Optional.empty());
        // ciudad ok, oficina vacía, punto vacío
        when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class))).thenAnswer(inv -> {
            String sql = inv.getArgument(0);
            if (sql.contains("ciudades_logisticas")) return List.of(new CiudadResponseDTO(1L, "Medellin", "Antioquia", true));
            return List.of();
        });
        RegistrarUbicacionRequestDTO dto = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, null, 1L, 10L, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> service.registrar(PEDIDO_ID, dto));
    }

    // ---------- registrar: corrección ----------

    @Test
    void registrar_corrigeEventoId_inexistente_lanza() throws Exception {
        stubActor(Rol.OPERADOR);
        stubPedido(EstadoPedido.CREADO);
        when(pedidos.findByIdForUpdate(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        when(eventos.findByIdEvento(anyString())).thenReturn(Optional.empty());
        when(eventos.findById(555L)).thenReturn(Optional.empty());
        stubJdbc();
        RegistrarUbicacionRequestDTO dto = dto(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, null, 1L, 10L, null, null, 555L);
        assertThrows(IllegalArgumentException.class, () -> service.registrar(PEDIDO_ID, dto));
    }

    // ---------- consultar / consultarCliente ----------

    @Test
    void consultar_operador_devuelveEventos() {
        SecurityContextHolder.setContext(new org.springframework.security.core.context.SecurityContextImpl(
                new UsernamePasswordAuthenticationToken("op", "n/a", List.of(new SimpleGrantedAuthority("ROLE_OPERADOR")))));
        stubActor(Rol.OPERADOR);
        when(pedidos.findById(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        when(pedido.getNumeroTracking()).thenReturn(TRACKING);
        EventoUbicacionPedido e = evento(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, 1L, 10L, null, null, null);
        when(eventos.findByPedidoIdOrderByFechaEventoAscIdAsc(PEDIDO_ID)).thenReturn(List.of(e));

        List<UbicacionResponseDTO> r = service.consultar(PEDIDO_ID);
        assertEquals(1, r.size());
        verify(actores).operadorActualId();
    }

    @Test
    void consultar_pedidoNoEncontrado_lanza() {
        when(pedidos.findById(PEDIDO_ID)).thenReturn(Optional.empty());
        assertThrows(PedidoNoEncontradoException.class, () -> service.consultar(PEDIDO_ID));
    }

    @Test
    void consultarCliente_devuelveEventosDelCliente() {
        when(accesoCliente.pedidoDelClienteActual(TRACKING)).thenReturn(pedido);
        when(pedido.getId()).thenReturn(PEDIDO_ID);
        EventoUbicacionPedido e = evento(EtapaUbicacion.EN_BODEGA_ORIGEN, OrigenUbicacion.WEB, 1L, 10L, null, null, null);
        when(eventos.findByPedidoIdOrderByFechaEventoAscIdAsc(PEDIDO_ID)).thenReturn(List.of(e));
        List<UbicacionResponseDTO> r = service.consultarCliente(TRACKING);
        assertEquals(1, r.size());
    }

    // ---------- helpers ----------

    private void stubActor(Rol rol) {
        when(actores.actorActual()).thenReturn(actor);
        when(actor.getRol()).thenReturn(rol);
        when(actor.getId()).thenReturn(USUARIO_ID);
    }

    private void stubPedido(EstadoPedido estado) {
        when(pedido.getEstado()).thenReturn(estado);
        when(pedido.getId()).thenReturn(PEDIDO_ID);
        when(pedido.getNumeroTracking()).thenReturn(TRACKING);
        when(pedido.getDireccionOrigen()).thenReturn("Origen");
    }

    private void stubJdbc() throws Exception {
        when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class))).thenAnswer(inv -> {
            String sql = inv.getArgument(0);
            @SuppressWarnings("unchecked")
            RowMapper<Object> mapper = (RowMapper<Object>) inv.getArgument(1);
            ResultSet rs = mock(ResultSet.class);
            when(rs.getString(anyString())).thenReturn("x");
            if (sql.contains("ciudades_logisticas")) {
                when(rs.getLong("id")).thenReturn(1L);
                when(rs.getString("nombre")).thenReturn("Medellin");
                when(rs.getString("departamento")).thenReturn("Antioquia");
                return List.of(mapper.mapRow(rs, 0));
            }
            if (sql.contains("oficinas_logisticas")) {
                when(rs.getLong("id")).thenReturn(10L);
                when(rs.getString("nombre")).thenReturn("Oficina Centro");
                when(rs.getString("direccion")).thenReturn("Calle 10");
                return List.of(mapper.mapRow(rs, 0));
            }
            return List.of(); // puntos_entrega
        });
    }

    private RegistrarUbicacionRequestDTO dto(EtapaUbicacion etapa, OrigenUbicacion origen, String codigoQr,
                                             Long ciudadId, Long oficinaId, Long puntoEntregaId, String vehiculoId, Long corrigeEventoId) {
        return new RegistrarUbicacionRequestDTO(etapa, origen, ID_EVENTO, codigoQr, ciudadId, oficinaId, puntoEntregaId, vehiculoId, OffsetDateTime.now(), corrigeEventoId);
    }

    private EventoUbicacionPedido evento(EtapaUbicacion etapa, OrigenUbicacion origen, Long ciudadId, Long oficinaId, Long puntoId, String vehiculo, Long corrige) {
        return new EventoUbicacionPedido(ID_EVENTO, PEDIDO_ID, TRACKING, etapa, EstadoPedido.RECIBIDO_EN_ORIGEN, ciudadId, "Medellin", oficinaId, "Oficina Centro", puntoId, null, "Calle 10", vehiculo, USUARIO_ID, origen, LocalDateTime.now(), corrige);
    }
}
