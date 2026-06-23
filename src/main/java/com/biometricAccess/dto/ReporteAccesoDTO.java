package com.biometricAccess.dto;

public class ReporteAccesoDTO {

    private String fecha;
    private String horaIngreso;
    private String horaSalida;
    private String identificacion;
    private String nombre;
    private String apellido;
    private String rol;
    private String numeroFicha;
    private String tipoDocumento;
    private String equipo;
    private String tipoEquipo;
    private String resultado;
    private String observacion;

    public ReporteAccesoDTO() {
    }

    public ReporteAccesoDTO(
            String fecha,
            String horaIngreso,
            String horaSalida,
            String identificacion,
            String nombre,
            String apellido,
            String rol,
            String numeroFicha,
            String tipoDocumento,
            String equipo,
            String tipoEquipo,
            String resultado,
            String observacion
    ) {
        this.fecha = fecha;
        this.horaIngreso = horaIngreso;
        this.horaSalida = horaSalida;
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.numeroFicha = numeroFicha;
        this.tipoDocumento = tipoDocumento;
        this.equipo = equipo;
        this.tipoEquipo = tipoEquipo;
        this.resultado = resultado;
        this.observacion = observacion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHoraIngreso() {
        return horaIngreso;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getRol() {
        return rol;
    }

    public String getNumeroFicha() {
        return numeroFicha;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public String getEquipo() {
        return equipo;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public String getResultado() {
        return resultado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setHoraIngreso(String horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setNumeroFicha(String numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}