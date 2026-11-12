package repository;

import model.Medicamento;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Repositório em memória do catálogo de medicamentos. */
public class MedicamentoRepository {
    private final List<Medicamento> medicamentos = new ArrayList<>();

    public synchronized void guardar(Medicamento medicamento) { medicamentos.add(medicamento); }
    public synchronized List<Medicamento> listarTodos() { return List.copyOf(medicamentos); }
    public synchronized Optional<Medicamento> procurarPorId(String id) {
        return medicamentos.stream().filter(m -> m.getId().equalsIgnoreCase(id)).findFirst();
    }
}