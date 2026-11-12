package eventos;

import java.time.LocalDateTime;

/** Facto imutável que regista a criação de uma reserva.. */
public final class ReservaCriadaEvent {
    private final String idReserva;
    private final String idMedicamento;
    private final LocalDateTime ocorridoEm;

    public ReservaCriadaEvent(String idReserva, String idMedicamento, LocalDateTime ocorridoEm) {
        this.idReserva = idReserva;
        this.idMedicamento = idMedicamento;
        this.ocorridoEm = ocorridoEm;
    }

    public String getIdReserva() { return idReserva; }
    public String getIdMedicamento() { return idMedicamento; }
    public LocalDateTime getOcorridoEm() { return ocorridoEm; }
}