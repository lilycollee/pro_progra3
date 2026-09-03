package logic.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Reserva {
    private String id;
    private String actividad;
    private Funcionario funcionario;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
    private List<DetalleReserva> detalleReservas = new ArrayList<>();

    public Reserva() {}
    public Reserva(String id, Funcionario funcionario, String actividad, LocalDate fecha,LocalTime horaInicio, LocalTime horaFin) {
        this.id = id;
        this.funcionario = funcionario;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = EstadoReserva.ACTIVA;
    }

    public void agregarDetalleReserva(DetalleReserva detalleReserva) {
        this.detalleReservas.add(detalleReserva);
    }
    public void cancelarReserva(){
        this.estado = EstadoReserva.CANCELADA;
    }

    public String getId() {
        return id;
    }
    public Funcionario getFuncionario() {
        return funcionario;
    }
    public String getActividad() {
        return actividad;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    public LocalTime getHoraFin() {
        return horaFin;
    }
    public String getEstado() {
        return estado;
    }
    public List<DetalleReserva> getDetalles() {
        return detalleReservas;
    }
    public String getRecursosAsignado() {
        String recursosAsignado = "";
        for (DetalleReserva detalleReserva : detalleReservas) {
            recursosAsignado += detalleReserva.toString() + ",";
        }
        return recursosAsignado;
    }
}
