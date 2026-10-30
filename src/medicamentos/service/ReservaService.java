package medicamentos.service;

import medicamentos.model.Medicamento;
import medicamentos.model.Reserva;
import java.util.ArrayList;
import java.util.List;

public class ReservaService {
    
    private final List<Reserva> reservasAtivas = new ArrayList<>();

    // 'synchronized' garante exclusão mútua entre threads
    public synchronized Reserva criarReserva(String idReserva, Medicamento medicamento, int horasTTL) {
        // Tenta verificar e decrementar o stock de forma atómica
        if (medicamento.decrementarStock()) {
            Reserva novaReserva = new Reserva(idReserva, medicamento, horasTTL);
            reservasAtivas.add(novaReserva);
            System.out.println("[SUCESSO] Reserva " + idReserva + " efetuada com sucesso para: " + medicamento.getNome());
            return novaReserva;
        } else {
            System.out.println("[BLOQUEADO/ESGOTADO] Tentativa falhada para " + idReserva + ": Stock esgotado em concorrência!");
            return null;
        }
    }

    public List<Reserva> getReservasAtivas() {
        return reservasAtivas;
    }
    // Paradigma Orientado a Eventos: verifica e dispara ações quando o TTL expira
public synchronized void verificarExpiracoes() {
    for (Reserva r : reservasAtivas) {
        // Se a reserva ainda está ativa mas já passou o tempo limite (TTL)
        if (r.getEstado() == Reserva.EstadoReserva.RESERVADO && r.estaExpirada()) {
            r.setEstado(Reserva.EstadoReserva.EXPIRADO);
            
            // Dispara o evento de notificação e devolução de stock
            System.out.println("[EVENTO AUTOMÁTICO] A reserva " + r.getIdReserva() + " expirou por TTL! O stock foi libertado.");
        }
    }
}
}