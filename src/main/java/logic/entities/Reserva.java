package logic.entities;

import logic.entities.enums.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import data.adapter.LocalDateAdapter;
import data.adapter.LocalTimeAdapter;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Reserva {
    private String id;
    private String actividad;
    private String idFuncionario;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fecha;
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaInicio;
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaFin;
    private String estado;
    private List<DetalleReserva> detalleReservas = new ArrayList<>();

    public Reserva() {}

    public Reserva(String id, String idFuncionario, String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        this.id = id;
        this.idFuncionario = idFuncionario;
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
    public String getIdFuncionario() {
        return idFuncionario;
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
}
