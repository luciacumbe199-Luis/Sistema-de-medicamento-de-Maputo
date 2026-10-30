/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package medicamentos.model;

import java.time.LocalDateTime;

public class Reserva {
    
    // Estados da reserva definidos no documento TO-BE do grupo
    public enum EstadoReserva {
        DISPONIVEL, RESERVADO, VENDIDO, EXPIRADO, CANCELADO
    }

    private final String idReserva;
    private final Medicamento medicamento;
    private final LocalDateTime dataCriacao;
    private final LocalDateTime dataExpiracao; // TTL (Tempo de vida da reserva)
    private EstadoReserva estado;

    public Reserva(String idReserva, Medicamento medicamento, int horasTTL) {
        this.idReserva = idReserva;
        this.medicamento = medicamento;
        this.dataCriacao = LocalDateTime.now();
        this.dataExpiracao = this.dataCriacao.plusHours(horasTTL); // Ex: 24h ou 2h
        this.estado = EstadoReserva.RESERVADO;
    }

    // Método para verificar se o TTL expirou
    public boolean estaExpirada() {
        return LocalDateTime.now().isAfter(dataExpiracao);
    }

    // Métodos Getters e Setters
    public String getIdReserva() { return idReserva; }
    public Medicamento getMedicamento() { return medicamento; }
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
    public LocalDateTime getDataExpiracao() { return dataExpiracao; }
}
