package reservationSystem.logic;

import reservationSystem.data.Data;
import reservationSystem.logic.entities.DetalleReserva;
import reservationSystem.logic.entities.Reserva;
import reservationSystem.logic.entities.ResultadoReserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {
    private Service service;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    // Subclase que evita escribir a disco: sobrescribe guardarCambios()
    // para que no llame a XmlPersister real durante el test.
    private static class ServiceParaTest extends Service {
        ServiceParaTest(Data data) {
            super(data);
        }
        @Override
        protected void guardarCambios() {
            // no-op: en un test de unidad no debe tocar el disco
        }
    }

    @BeforeEach
    void setUp() {
        // ARRANGE (parte que se repite en todos los tests):
        // Data() ya trae 2 proyectores (CAT001) y 2 salas (CAT002) sembrados.
        Data data = new Data();
        service = new ServiceParaTest(data);

        fecha = LocalDate.of(2026, 10, 5);
        horaInicio = LocalTime.of(9, 0);
        horaFin = LocalTime.of(10, 0);
    }

    @Test
    @DisplayName("Crea la reserva con éxito cuando hay un recurso disponible")
    void testCrearReservaExitosa() {
        // ARRANGE
        Reserva solicitud = new Reserva("TEMP", "f001", "Reunión de equipo", fecha, horaInicio, horaFin);
        solicitud.agregarDetalleReserva(new DetalleReserva("CAT001")); // pide un proyector

        // ACT
        ResultadoReserva resultado = service.crearReserva(solicitud);

        // ASSERT
        assertTrue(resultado.esExitosa());
        assertEquals(1, service.obtenerReservas().size());
    }

    @Test
    @DisplayName("Asigna un recurso concreto al detalle de la reserva")
    void testAsignaRecursoAlDetalle() {
        // ARRANGE
        Reserva solicitud = new Reserva("TEMP", "f001", "Reunión de equipo", fecha, horaInicio, horaFin);
        solicitud.agregarDetalleReserva(new DetalleReserva("CAT001"));

        // ACT
        service.crearReserva(solicitud);
        Reserva creada = service.obtenerReservas().get(0);

        // ASSERT
        assertNotNull(creada.getDetalles().get(0).getIdRecursoAsignado());
    }

    @Test
    @DisplayName("Rechaza la reserva si ya no hay recursos disponibles en esa categoría y horario")
    void testCrearReservaFallaSinDisponibilidad() {
        // ARRANGE: ocupamos los 2 proyectores existentes (REC001 y REC002)
        // en el mismo horario, con dos reservas distintas.
        Reserva solicitud1 = new Reserva("TEMP1", "f001", "Reunión 1", fecha, horaInicio, horaFin);
        solicitud1.agregarDetalleReserva(new DetalleReserva("CAT001"));
        service.crearReserva(solicitud1);

        Reserva solicitud2 = new Reserva("TEMP2", "f002", "Reunión 2", fecha, horaInicio, horaFin);
        solicitud2.agregarDetalleReserva(new DetalleReserva("CAT001"));
        service.crearReserva(solicitud2);

        // ACT: pedimos un tercer proyector en el mismo horario, ya no queda ninguno libre
        Reserva solicitud3 = new Reserva("TEMP3", "f001", "Reunión 3", fecha, horaInicio, horaFin);
        solicitud3.agregarDetalleReserva(new DetalleReserva("CAT001"));
        ResultadoReserva resultado3 = service.crearReserva(solicitud3);

        // ASSERT
        assertFalse(resultado3.esExitosa());
        assertEquals(1, resultado3.getCategoriasNoDisponibles().size());
        assertEquals("CAT001", resultado3.getCategoriasNoDisponibles().get(0).getId());
    }

    @Test
    @DisplayName("cancelarReserva() cambia el estado a CANCELADA")
    void testCancelarReservaConIdValido() {
        // ARRANGE
        Reserva solicitud = new Reserva("TEMP", "f001", "Reunión de equipo", fecha, horaInicio, horaFin);
        solicitud.agregarDetalleReserva(new DetalleReserva("CAT001"));
        service.crearReserva(solicitud);
        String idReal = service.obtenerReservas().get(0).getId();

        // ACT
        service.cancelarReserva(idReal);

        // ASSERT
        Reserva reservaActualizada = service.obtenerReservas().get(0);
        assertEquals(reservationSystem.logic.entities.enums.EstadoReserva.CANCELADA, reservaActualizada.getEstado());
    }

    @Test
    @DisplayName("cancelarReserva() con id inexistente no lanza excepción ni cambia nada")
    void testCancelarReservaConIdInexistente() {
        // ARRANGE
        Reserva solicitud = new Reserva("TEMP", "f001", "Reunión de equipo", fecha, horaInicio, horaFin);
        solicitud.agregarDetalleReserva(new DetalleReserva("CAT001"));
        service.crearReserva(solicitud);
        int cantidadAntes = service.obtenerReservas().size();

        // ACT + ASSERT
        assertDoesNotThrow(() -> service.cancelarReserva("ID_QUE_NO_EXISTE"));

        // ASSERT adicional: nada cambió
        assertEquals(cantidadAntes, service.obtenerReservas().size());
        assertEquals(reservationSystem.logic.entities.enums.EstadoReserva.ACTIVA, service.obtenerReservas().get(0).getEstado());
    }

    @Test
    @DisplayName("Dos reservas en horarios que no se solapan pueden compartir recurso")
    void testReservasEnHorariosDistintosNoCompitenPorElMismoRecurso() {
        // ARRANGE
        LocalTime horaInicio2 = LocalTime.of(10, 0);
        LocalTime horaFin2 = LocalTime.of(11, 0);

        Reserva solicitud1 = new Reserva("TEMP1", "f001", "Reunión 1", fecha, horaInicio, horaFin); // 9:00-10:00
        solicitud1.agregarDetalleReserva(new DetalleReserva("CAT001"));

        Reserva solicitud2 = new Reserva("TEMP2", "f002", "Reunión 2", fecha, horaInicio2, horaFin2); // 10:00-11:00
        solicitud2.agregarDetalleReserva(new DetalleReserva("CAT001"));

        // ACT
        ResultadoReserva resultado1 = service.crearReserva(solicitud1);
        ResultadoReserva resultado2 = service.crearReserva(solicitud2);

        // ASSERT: ambas exitosas, aunque solo hay 2 proyectores en total
        assertTrue(resultado1.esExitosa());
        assertTrue(resultado2.esExitosa());
        assertEquals(2, service.obtenerReservas().size());
    }

    @Test
    @DisplayName("cambiarClave() rechaza el cambio si la clave actual es incorrecta")
    void testCambiarClaveConClaveActualIncorrecta() {
        // ACT
        boolean resultado = service.cambiarClave("f001", "claveIncorrecta", "nuevaClave123");

        // ASSERT: el cambio fue rechazado
        assertFalse(resultado);

        // ASSERT adicional: la clave original ("f001", según los datos sembrados) sigue funcionando
        assertNotNull(service.autenticar("f001", "f001"));
    }
}
