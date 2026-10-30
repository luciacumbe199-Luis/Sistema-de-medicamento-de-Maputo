package medicamentos.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Medicamento {
    private final String id;
    private final String nome;
    private final double preco;
    private final boolean requerReceita;
    private final List<Lote> lotes;

    public Medicamento(String id, String nome, double preco, boolean requerReceita) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.requerReceita = requerReceita;
        this.lotes = new ArrayList<>();
    }

    public void adicionarLote(Lote lote) {
        this.lotes.add(lote);
    }

    public List<Lote> getLotes() {
        return lotes;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public boolean isRequerReceita() {
        return requerReceita;
    }

    public boolean temStockDisponivel() {
        return lotes.stream()
                .anyMatch(l -> l.getQuantidade() > 0 && l.getDataValidade().isAfter(LocalDate.now()));
    }
    public boolean decrementarStock() {
    for (Lote lote : lotes) {
        if (lote.getQuantidade() > 0 && lote.getDataValidade().isAfter(LocalDate.now())) {
            lote.setQuantidade(lote.getQuantidade() - 1);
            return true;
        }
    }
    return false;
}
}