package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Medicamento {
    private final String id;
    private final String nome;
    private final double preco;
    private final boolean requerReceita;
    private final List<Lote> lotes = new ArrayList<>();

    public Medicamento(String id, String nome, double preco, boolean requerReceita) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.requerReceita = requerReceita;
    }

    public synchronized void adicionarLote(Lote lote) { lotes.add(lote); }
    public synchronized List<Lote> getLotes() { return List.copyOf(lotes); }
    public String getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public boolean isRequerReceita() { return requerReceita; }

    public synchronized boolean temStockDisponivel() {
        return lotes.stream().anyMatch(l -> l.getQuantidade() > 0 && !l.getDataValidade().isBefore(LocalDate.now()));
    }

    public synchronized int getStockDisponivel() {
        return lotes.stream()
                .filter(l -> !l.getDataValidade().isBefore(LocalDate.now()))
                .mapToInt(Lote::getQuantidade)
                .sum();
    }

    public synchronized boolean temStockSuficiente(int quantidade) {
        return quantidade > 0 && getStockDisponivel() >= quantidade;
    }

    /** Verificar e decrementar são uma única operação sobre este medicamento. */
    public synchronized Lote reservarUnidade() {
        for (Lote lote : lotes) {
            if (lote.getQuantidade() > 0 && !lote.getDataValidade().isBefore(LocalDate.now())) {
                lote.setQuantidade(lote.getQuantidade() - 1);
                return lote;
            }
        }
        return null;
    }

    public synchronized Lote reservarQuantidade(int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        if (!temStockSuficiente(quantidade)) return null;

        int restante = quantidade;
        Lote loteReservado = null;
        for (Lote lote : lotes) {
            if (restante == 0) break;
            if (lote.getQuantidade() > 0 && !lote.getDataValidade().isBefore(LocalDate.now())) {
                int retirada = Math.min(lote.getQuantidade(), restante);
                lote.setQuantidade(lote.getQuantidade() - retirada);
                if (loteReservado == null) loteReservado = lote;
                restante -= retirada;
            }
        }

        if (restante != 0) {
            throw new IllegalStateException("Não foi possível reservar a quantidade pedida.");
        }

        return loteReservado;
    }

    public synchronized void devolverUnidade(Lote lote) {
        if (lote != null && lotes.contains(lote)) lote.reporUmaUnidade();
    }

    public synchronized void devolverQuantidade(Lote lote, int quantidade) {
        if (lote == null || quantidade <= 0 || !lotes.contains(lote)) return;
        for (int i = 0; i < quantidade; i++) {
            lote.reporUmaUnidade();
        }
    }
}