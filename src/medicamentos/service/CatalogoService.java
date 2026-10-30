package medicamentos.service;

import medicamentos.model.Medicamento;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogoService {

    // Paradigma Funcional: utiliza Streams, filter e map para pesquisar medicamentos
    public static List<Medicamento> pesquisarMedicamentos(List<Medicamento> catalogoGeral, String termoPesquisa) {
        if (termoPesquisa == null || termoPesquisa.trim().isEmpty()) {
            return List.of();
        }
        
        return catalogoGeral.stream()
                .filter(m -> m.getNome().toLowerCase().contains(termoPesquisa.toLowerCase()))
                .filter(Medicamento::temStockDisponivel)
                .collect(Collectors.toList());
    }
}