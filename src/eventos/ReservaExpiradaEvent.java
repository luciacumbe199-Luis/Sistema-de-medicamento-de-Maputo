package eventos;

import java.time.LocalDateTime;

/** Facto imutável que regista a expiração de uma reserva.. */
public final class ReservaExpiradaEvent {
    private final String idReserva;
    private final String idMedicamento;
    private final LocalDateTime ocorridoEm;

    public ReservaExpiradaEvent(String idReserva, String idMedicamento, LocalDateTime ocorridoEm) {
        this.idReserva = idReserva;
        this.idMedicamento = idMedicamento;
        this.ocorridoEm = ocorridoEm;
    }

    public String getIdReserva() { return idReserva; }
    public String getIdMedicamento() { return idMedicamento; }
    public LocalDateTime getOcorridoEm() { return ocorridoEm; }
}