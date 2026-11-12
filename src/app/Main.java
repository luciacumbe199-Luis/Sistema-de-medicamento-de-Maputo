package app;

import eventos.EventBus;
import eventos.ReservaCriadaEvent;
import eventos.ReservaExpiradaEvent;
import model.Lote;
import model.Medicamento;
import model.Reserva;
import repository.MedicamentoRepository;
import service.CatalogoService;
import service.ReservaService;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MedicamentoRepository catalogo = new MedicamentoRepository();
        Medicamento paracetamol = new Medicamento("MED001", "Paracetamol 500mg", 150.0, false);
        paracetamol.adicionarLote(new Lote("LOTE-A1", 1, LocalDate.now().plusMonths(6)));
        Medicamento ibuprofeno = new Medicamento("MED002", "Ibuprofeno 400mg", 250.0, false);
        ibuprofeno.adicionarLote(new Lote("LOTE-B2", 2, LocalDate.now().plusMonths(4)));
        Medicamento amoxicilina = new Medicamento("MED003", "Amoxicilina 500mg", 400.0, true);
        amoxicilina.adicionarLote(new Lote("LOTE-C3", 10, LocalDate.now().plusMonths(3)));
        catalogo.guardar(paracetamol);
        catalogo.guardar(ibuprofeno);
        catalogo.guardar(amoxicilina);

        ReservaService reservas = new ReservaService();
        EventBus bus = reservas.getEventBus();
        bus.subscribe(ReservaCriadaEvent.class, e -> System.out.println("[EVENTO] Reserva criada: " + e.getIdReserva() + " / " + e.getIdMedicamento()));
        bus.subscribe(ReservaExpiradaEvent.class, e -> System.out.println("[EVENTO] Reserva expirada e stock devolvido: " + e.getIdReserva()));

        try (Scanner scanner = new Scanner(System.in)) {
            int opcao = -1;
            while (opcao != 0) {
                System.out.println("\n=== MEDI-MAPUTO ===");
                System.out.println("1. Pesquisar medicamento");
                System.out.println("2. Criar reserva");
                System.out.println("3. Verificar expirações");
                System.out.println("4. Simular dois clientes concorrentes");
                System.out.println("0. Sair");
                System.out.print("Opção: ");
                if (!scanner.hasNextInt()) { scanner.nextLine(); System.out.println("Opção inválida."); continue; }
                opcao = scanner.nextInt(); scanner.nextLine();
                switch (opcao) {
                    case 1 -> {
                        System.out.print("Nome ou ID: ");
                        String termo = scanner.nextLine();
                        CatalogoService.pesquisarMedicamentos(catalogo.listarTodos(), termo)
                                .forEach(m -> System.out.println(m.getId() + " | " + m.getNome() + " | " + m.getPreco() + " MZN | Stock: " + m.getStockDisponivel()));
                    }
                    case 2 -> {
                        System.out.print("ID do medicamento: ");
                        Medicamento medicamento = catalogo.procurarPorId(scanner.nextLine()).orElse(null);
                        if (medicamento == null) { System.out.println("Medicamento não encontrado."); break; }
                        System.out.print("Quantidade: ");
                        if (!scanner.hasNextInt()) { scanner.nextLine(); System.out.println("Quantidade inválida."); break; }
                        int quantidade = scanner.nextInt(); scanner.nextLine();
                        System.out.print("ID da reserva: "); String id = scanner.nextLine();
                        System.out.print("TTL em horas: ");
                        if (!scanner.hasNextInt()) { scanner.nextLine(); System.out.println("TTL inválido."); break; }
                        int ttl = scanner.nextInt(); scanner.nextLine();
                        System.out.println("[INFO] Verificando stock para " + quantidade + " unidade(s) de " + medicamento.getNome() + ".");
                        Reserva criada = reservas.criarReserva(id, medicamento, quantidade, ttl);
                        if (criada == null) {
                            System.out.println("[RESULTADO] Reserva não criada: stock insuficiente ou inválido.");
                        } else {
                            System.out.println("[RESULTADO] Reserva criada com sucesso. Stock restante: " + medicamento.getStockDisponivel());
                        }
                    }
                    case 3 -> {
                        System.out.println("[VERIFICACAO] A verificar reservas activas e expiradas...");
                        reservas.verificarExpiracoes();
                        System.out.println("[VERIFICACAO] Estado final das reservas:");
                        reservas.getReservasAtivas().forEach(r ->
                                System.out.println("- " + r.getIdReserva() + " | " + r.getMedicamento().getNome() + " | " + r.getEstado() + " | expira em " + r.getDataExpiracao()));
                    }
                    case 4 -> simularConcorrencia(reservas, paracetamol);
                    case 0 -> System.out.println("A encerrar o sistema.");
                    default -> System.out.println("Opção inválida.");
                }
            }
        }
    }

    private static void simularConcorrencia(ReservaService service, Medicamento medicamento) {
        System.out.println("\n[SIMULACAO] Iniciando concorrência com 2 clientes no mesmo medicamento.");
        System.out.println("[SIMULACAO] Stock inicial: " + medicamento.getStockDisponivel() + " unidade(s) de " + medicamento.getNome());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            var clienteA = executor.submit(() -> {
                System.out.println("[CLIENTE A] a pedido: tentar reservar 1 unidade.");
                Reserva r = service.criarReserva("RES-CONC-A", medicamento, 1, 24);
                System.out.println("[CLIENTE A] resultado: " + (r != null ? "reservado" : "sem stock"));
                return r;
            });
            var clienteB = executor.submit(() -> {
                System.out.println("[CLIENTE B] a pedido: tentar reservar 1 unidade.");
                Reserva r = service.criarReserva("RES-CONC-B", medicamento, 1, 24);
                System.out.println("[CLIENTE B] resultado: " + (r != null ? "reservado" : "sem stock"));
                return r;
            });

            System.out.println("[SIMULACAO] Esperando conclusão dos clientes...");
            System.out.println("[CLIENTE A] final: " + (clienteA.get() != null ? "reservado" : "sem stock"));
            System.out.println("[CLIENTE B] final: " + (clienteB.get() != null ? "reservado" : "sem stock"));
            System.out.println("[SIMULACAO] Stock final: " + medicamento.getStockDisponivel() + " unidade(s)");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[SIMULACAO] A simulação foi interrompida.");
        } catch (java.util.concurrent.ExecutionException e) {
            System.out.println("[SIMULACAO] Falha numa tarefa: " + e.getCause().getMessage());
        } finally {
            executor.shutdown();
            try { if (!executor.awaitTermination(5, TimeUnit.SECONDS)) executor.shutdownNow(); }
            catch (InterruptedException e) { executor.shutdownNow(); Thread.currentThread().interrupt(); }
        }
    }
}