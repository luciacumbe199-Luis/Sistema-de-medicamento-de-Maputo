package medicamentos.app;

import medicamentos.model.Lote;
import medicamentos.model.Medicamento;
import medicamentos.model.Reserva;
import medicamentos.service.CatalogoService;
import medicamentos.service.ReservaService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Inicializar o Catálogo de Farmácia de Maputo com dados reais
        List<Medicamento> catalogoGeral = new ArrayList<>();

        Medicamento m1 = new Medicamento("MED001", "Paracetamol 500mg", 150.0, false);
        m1.adicionarLote(new Lote("LOTE-A1", 5, LocalDate.now().plusMonths(6)));

        Medicamento m2 = new Medicamento("MED002", "Ibuprofeno 400mg", 250.0, false);
        m2.adicionarLote(new Lote("LOTE-B2", 2, LocalDate.now().plusMonths(4)));

        Medicamento m3 = new Medicamento("MED003", "Amoxicilina 500mg", 400.0, true);
        m3.adicionarLote(new Lote("LOTE-C3", 10, LocalDate.now().minusDays(3))); // Expirado (inválido)

        catalogoGeral.add(m1);
        catalogoGeral.add(m2);
        catalogoGeral.add(m3);

        ReservaService reservaService = new ReservaService();
        try (Scanner scanner = new Scanner(System.in)) {
            int opcao = 0;
            
            // 2. Menu Interativo Completo
            do {
                System.out.println("\n==================================================");
                System.out.println("   MEDI-MAPUTO: SISTEMA DE GESTÃO DE FARMÁCIAS   ");
                System.out.println("==================================================");
                System.out.println("1. Pesquisar Medicamento (Paradigma Funcional)");
                System.out.println("2. Efetuar Reserva de Medicamento (Com Stock)");
                System.out.println("3. Ver Reservas Ativas e Testar TTL / Eventos");
                System.out.println("4. Simular Concorrência (Múltiplos Clientes)");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");
                
                if (scanner.hasNextInt()) {
                    opcao = scanner.nextInt();
                    scanner.nextLine(); // Limpar o enter
                    
                    switch (opcao) {
                        case 1 -> {
                            System.out.print("\nDigite o nome do medicamento que procura: ");
                            String termo = scanner.nextLine();
                            List<Medicamento> resultados = CatalogoService.pesquisarMedicamentos(catalogoGeral, termo);
                            
                            if (resultados.isEmpty()) {
                                System.out.println("[INFO] Nenhum medicamento encontrado com stock válido.");
                            } else {
                                System.out.println("\n--- RESULTADOS DA PESQUISA ---");
                                for (Medicamento m : resultados) {
                                    System.out.println("ID: " + m.getId() + " | Nome: " + m.getNome() + " | Preço: " + m.getPreco() + " MZN | Receita: " + (m.isRequerReceita() ? "Sim" : "Não"));
                                }
                            }
                        }
                            
                        case 2 -> {
                            System.out.print("\nDigite o ID do medicamento a reservar (ex: MED001): ");
                            String idMed = scanner.nextLine();
                            Medicamento medEncontrado = null;
                            
                            for (Medicamento m : catalogoGeral) {
                                if (m.getId().equalsIgnoreCase(idMed)) {
                                    medEncontrado = m;
                                    break;
                                }
                            }
                            
                            if (medEncontrado != null) {
                                System.out.print("Digite o ID da Reserva (ex: RES-01): ");
                                String idRes = scanner.nextLine();
                                System.out.print("Digite o TTL em horas (ex: 24): ");
                                int ttl = scanner.nextInt();
                                scanner.nextLine();
                                
                                reservaService.criarReserva(idRes, medEncontrado, ttl);
                            } else {
                                System.out.println("[ERRO] Medicamento não encontrado no sistema.");
                            }
                        }
                            
                        case 3 -> {
                            System.out.println("\n--- ESTADO DAS RESERVAS E TTL ---");
                            List<Reserva> ativas = reservaService.getReservasAtivas();
                            if (ativas.isEmpty()) {
                                System.out.println("[INFO] Não existem reservas ativas no momento.");
                            } else {
                                for (Reserva r : ativas) {
                                    System.out.println("Reserva: " + r.getIdReserva() + " | Med: " + r.getMedicamento().getNome() + " | Estado: " + r.getEstado() + " | Expira em: " + r.getDataExpiracao());
                                }
                                System.out.println("\n-> A executar verificação automática de expirações por TTL...");
                                reservaService.verificarExpiracoes();
                            }
                        }
                            
                        case 4 -> {
                            System.out.println("\n--- A SIMULAR CONCORRÊNCIA (2 CLIENTES A 1 SÓ ITEM) ---");
                            Medicamento itemAlvo = m1; // Paracetamol
                            
                            Thread clienteA = new Thread(() -> {
                                reservaService.criarReserva("RES-CONC-A", itemAlvo, 24);
                            });
                            Thread clienteB = new Thread(() -> {
                                reservaService.criarReserva("RES-CONC-B", itemAlvo, 24);
                            });
                            
                            clienteA.start();
                            clienteB.start();
                            try {
                                clienteA.join();
                                clienteB.join();
                            } catch (InterruptedException e) {
                            }
                        }
                            
                        case 0 -> System.out.println("A encerrar o sistema. Obrigado por usar o MediMaputo!");
                            
                        default -> System.out.println("[ERRO] Opção inválida. Tente novamente.");
                    }
                } else {
                    System.out.println("[ERRO] Por favor, insira um número válido.");
                    scanner.next();
                }
                
            } while (opcao != 0);
        }
    }
}