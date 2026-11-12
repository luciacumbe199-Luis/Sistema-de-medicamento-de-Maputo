package service;

import model.Medicamento;
import java.util.List;
import java.util.Locale;

/** Pesquisa funcional sobre uma fotografia do catálogo. */
public class CatalogoService {
    public static List<Medicamento> pesquisarMedicamentos(List<Medicamento> catalogo, String termo) {
        if (catalogo == null || termo == null || termo.isBlank()) return List.of();
        String pesquisa = termo.trim().toLowerCase(Locale.ROOT);
        return catalogo.stream()
                .filter(m -> m.getNome().toLowerCase(Locale.ROOT).contains(pesquisa)
                        || m.getId().toLowerCase(Locale.ROOT).contains(pesquisa))
                .filter(Medicamento::temStockDisponivel)
                .toList();
    }
}