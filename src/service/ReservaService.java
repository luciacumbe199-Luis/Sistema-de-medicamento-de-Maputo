package service;

import eventos.EventBus;
import eventos.ReservaCriadaEvent;
import eventos.ReservaExpiradaEvent;
import model.Lote;
import model.Medicamento;
import model.Reserva;
import repository.ReservaRepository;
import security.ReservaValidator;
import java.time.LocalDateTime;
import java.util.List;

/** Coordena a regra de reserva e publica os factos de negócio resultantes. */
public class ReservaService {
    private final ReservaRepository reservas;
    private final EventBus eventBus;

    public ReservaService() { this(new ReservaRepository(), new EventBus()); }
    public ReservaService(ReservaRepository reservas, EventBus eventBus) {
        this.reservas = reservas;
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() { return eventBus; }

    public Reserva criarReserva(String idReserva, Medicamento medicamento, int quantidade, int horasTTL) {
        ReservaValidator.validar(idReserva, medicamento, quantidade, horasTTL);
        Reserva nova;
        synchronized (this) {
            if (reservas.listarTodas().stream().anyMatch(r -> r.getIdReserva().equalsIgnoreCase(idReserva))) {
                throw new IllegalArgumentException("Este ID de reserva já existe.");
            }
            synchronized (medicamento) {
                if (!medicamento.temStockSuficiente(quantidade)) {
                    System.out.println("[BLOQUEADO/ESGOTADO] Não há stock suficiente para " + idReserva + ". Pedido=" + quantidade + ", stock=" + medicamento.getStockDisponivel() + ".");
                    return null;
                }
                Lote lote = medicamento.reservarQuantidade(quantidade);
                if (lote == null) {
                    System.out.println("[BLOQUEADO/ESGOTADO] Não há stock válido para " + idReserva + ".");
                    return null;
                }
                nova = new Reserva(idReserva, medicamento, lote, quantidade, horasTTL);
                reservas.guardar(nova);
            }
        }
        System.out.println("[CLIENTE] Reserva criada: " + idReserva + " | Medicamento: " + medicamento.getNome() + " | Quantidade: " + quantidade + ".");
        eventBus.publish(new ReservaCriadaEvent(idReserva, medicamento.getId(), LocalDateTime.now()));
        return nova;
    }

    public Reserva criarReserva(String idReserva, Medicamento medicamento, int horasTTL) {
        return criarReserva(idReserva, medicamento, 1, horasTTL);
    }

    public List<Reserva> getReservasAtivas() {
        return reservas.listarTodas().stream().filter(r -> r.getEstado() == Reserva.EstadoReserva.RESERVADO).toList();
    }

    public synchronized void verificarExpiracoes() {
        for (Reserva reserva : reservas.listarTodas()) {
            if (reserva.getEstado() == Reserva.EstadoReserva.RESERVADO && reserva.estaExpirada()) {
                reserva.setEstado(Reserva.EstadoReserva.EXPIRADO);
                reserva.getMedicamento().devolverQuantidade(reserva.getLoteReservado(), reserva.getQuantidade());
                System.out.println("[EXPIRACAO] Reserva " + reserva.getIdReserva() + " expirou; " + reserva.getQuantidade() + " unidade(s) devolvida(s) ao stock.");
                eventBus.publish(new ReservaExpiradaEvent(reserva.getIdReserva(), reserva.getMedicamento().getId(), LocalDateTime.now()));
            }
        }
    }
}