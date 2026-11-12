package repository;

import model.Reserva;
import java.util.ArrayList;
import java.util.List;

/** Repositório em memória de reservas, com acesso seguro entre threads. */
public class ReservaRepository {
    private final List<Reserva> reservas = new ArrayList<>();

    public synchronized void guardar(Reserva reserva) { reservas.add(reserva); }
    public synchronized List<Reserva> listarTodas() { return List.copyOf(reservas); }
}