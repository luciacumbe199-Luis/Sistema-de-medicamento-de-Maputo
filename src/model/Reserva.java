package model;

import java.time.LocalDateTime;

public class Reserva {
    public enum EstadoReserva { DISPONIVEL, RESERVADO, VENDIDO, EXPIRADO, CANCELADO }

    private final String idReserva;
    private final Medicamento medicamento;
    private final Lote loteReservado;
    private final int quantidade;
    private final LocalDateTime dataCriacao;
    private final LocalDateTime dataExpiracao;
    private EstadoReserva estado;

    public Reserva(String idReserva, Medicamento medicamento, Lote loteReservado, int horasTTL) {
        this(idReserva, medicamento, loteReservado, 1, horasTTL);
    }

    public Reserva(String idReserva, Medicamento medicamento, Lote loteReservado, int quantidade, int horasTTL) {
        if (idReserva == null || idReserva.isBlank()) throw new IllegalArgumentException("O ID da reserva é obrigatório.");
        if (medicamento == null) throw new IllegalArgumentException("O medicamento é obrigatório.");
        if (loteReservado == null) throw new IllegalArgumentException("O lote reservado é obrigatório.");
        if (quantidade <= 0) throw new IllegalArgumentException("A quantidade da reserva deve ser maior que zero.");
        if (horasTTL <= 0) throw new IllegalArgumentException("O TTL deve ser maior que zero.");
        this.idReserva = idReserva;
        this.medicamento = medicamento;
        this.loteReservado = loteReservado;
        this.quantidade = quantidade;
        this.dataCriacao = LocalDateTime.now();
        this.dataExpiracao = dataCriacao.plusHours(horasTTL);
        this.estado = EstadoReserva.RESERVADO;
    }

    public boolean estaExpirada() { return !LocalDateTime.now().isBefore(dataExpiracao); }
    public String getIdReserva() { return idReserva; }
    public Medicamento getMedicamento() { return medicamento; }
    public Lote getLoteReservado() { return loteReservado; }
    public int getQuantidade() { return quantidade; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
    public LocalDateTime getDataExpiracao() { return dataExpiracao; }
}