package security;

import model.Medicamento;

/** Valida os dados recebidos antes de executar uma operação de reserva. */
public final class ReservaValidator {
    private ReservaValidator() { }

    public static void validar(String idReserva, Medicamento medicamento, int quantidade, int horasTTL) {
        if (idReserva == null || idReserva.isBlank()) throw new IllegalArgumentException("O ID da reserva é obrigatório.");
        if (medicamento == null) throw new IllegalArgumentException("O medicamento é obrigatório.");
        if (quantidade <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        if (horasTTL <= 0) throw new IllegalArgumentException("O TTL deve ser maior que zero.");
    }
}