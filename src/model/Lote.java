package model;

import java.time.LocalDate;

public class Lote {
    private final String numeroLote;
    private int quantidade;
    private final LocalDate dataValidade;

    public Lote(String numeroLote, int quantidade, LocalDate dataValidade) {
        if (quantidade < 0) throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        this.numeroLote = numeroLote;
        this.quantidade = quantidade;
        this.dataValidade = dataValidade;
    }

    public boolean isValido() { return !dataValidade.isBefore(LocalDate.now()); }
    public String getNumeroLote() { return numeroLote; }
    public synchronized int getQuantidade() { return quantidade; }
    public synchronized void setQuantidade(int quantidade) {
        if (quantidade < 0) throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        this.quantidade = quantidade;
    }
    public synchronized void reporUmaUnidade() { quantidade++; }
    public LocalDate getDataValidade() { return dataValidade; }
}