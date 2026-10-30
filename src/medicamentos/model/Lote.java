/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package medicamentos.model;

import java.time.LocalDate;

public class Lote {
    private final String numeroLote;
    private int quantidade;
    private final LocalDate dataValidade;

    public Lote(String numeroLote, int quantidade, LocalDate dataValidade) {
        this.numeroLote = numeroLote;
        this.quantidade = quantidade;
        this.dataValidade = dataValidade;
    }

    // Regra de negócio: verifica se o lote ainda é válido e não expirou
    public boolean isValido() {
        return dataValidade.isAfter(LocalDate.now()) || dataValidade.isEqual(LocalDate.now());
    }

    public String getNumeroLote() { return numeroLote; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public LocalDate getDataValidade() { return dataValidade; }
}
