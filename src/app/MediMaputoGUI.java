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
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

/** Interface Swing; operações de reserva correm fora da thread de eventos da UI. */
public class MediMaputoGUI extends JFrame {
    private final MedicamentoRepository catalogo = new MedicamentoRepository();
    private final ReservaService reservaService = new ReservaService();
    private final JTextField txtPesquisa = new JTextField(20);
    private final JTextField txtIdReserva = new JTextField(10);
    private final JTextField txtQuantidade = new JTextField(8);
    private final JTextField txtIdMedicamento = new JTextField(10);
    private final JTextField txtNomeUtente = new JTextField(20);
    private final JComboBox<String> cbFarmacias = new JComboBox<>(new String[]{
        "Farmácia Central (Av. 24 de Julho)", "Farmácia Maputo (Baixa)",
        "Farmácia Polana Cimento (Av. Julius Nyerere)"});
    private final JTextArea txtResultados = new JTextArea();

    public MediMaputoGUI() {
        setTitle("MediMaputo - Sistema de Gestão de Farmácias");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        inicializarCatalogo();
        construirInterface();
        registarConsumidores();
    }

    private void inicializarCatalogo() {
        Medicamento paracetamol = new Medicamento("MED001", "Paracetamol 500mg", 150.0, false);
        paracetamol.adicionarLote(new Lote("LOTE-A1", 10, LocalDate.now().plusMonths(6)));
        Medicamento ibuprofeno = new Medicamento("MED002", "Ibuprofeno 400mg", 250.0, false);
        ibuprofeno.adicionarLote(new Lote("LOTE-B2", 5, LocalDate.now().plusMonths(3)));
        Medicamento amoxicilina = new Medicamento("MED003", "Amoxicilina 500mg", 400.0, true);
        amoxicilina.adicionarLote(new Lote("LOTE-C3", 2, LocalDate.now().minusDays(2)));
        catalogo.guardar(paracetamol);
        catalogo.guardar(ibuprofeno);
        catalogo.guardar(amoxicilina);
    }

    private void construirInterface() {
        JPanel raiz = new JPanel(new BorderLayout(12, 12));
        raiz.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(raiz);
        JPanel topo = new JPanel(new GridLayout(2, 1, 8, 8));
        JPanel pesquisa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pesquisa.setBorder(BorderFactory.createTitledBorder("Pesquisa de medicamentos"));
        JButton btnPesquisar = new JButton("Pesquisar");
        pesquisa.add(new JLabel("Nome / ID:")); pesquisa.add(txtPesquisa); pesquisa.add(btnPesquisar);
        JPanel reserva = new JPanel(new GridLayout(5, 1, 5, 5));
        reserva.setBorder(BorderFactory.createTitledBorder("Reserva e levantamento"));
        JPanel ids = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ids.add(new JLabel("ID medicamento:")); ids.add(txtIdMedicamento);
        ids.add(new JLabel("ID reserva:")); ids.add(txtIdReserva);
        ids.add(new JLabel("Qtde:")); ids.add(txtQuantidade);
        JPanel utente = new JPanel(new FlowLayout(FlowLayout.LEFT));
        utente.add(new JLabel("Nome do utente:")); utente.add(txtNomeUtente);
        JPanel farmacia = new JPanel(new FlowLayout(FlowLayout.LEFT));
        farmacia.add(new JLabel("Farmácia:")); farmacia.add(cbFarmacias);
        JButton btnReservar = new JButton("Confirmar reserva");
        JButton btnExpiracoes = new JButton("Verificar expirações");
        JButton btnSimularConcorrencia = new JButton("Simular 2 clientes");
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acoes.add(btnReservar); acoes.add(btnExpiracoes); acoes.add(btnSimularConcorrencia);
        reserva.add(ids); reserva.add(utente); reserva.add(farmacia); reserva.add(acoes);
        topo.add(pesquisa); topo.add(reserva); raiz.add(topo, BorderLayout.NORTH);
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        raiz.add(new JScrollPane(txtResultados), BorderLayout.CENTER);
        btnPesquisar.addActionListener(e -> executarPesquisa());
        txtPesquisa.addActionListener(e -> executarPesquisa());
        btnReservar.addActionListener(e -> executarReserva(btnReservar));
        btnExpiracoes.addActionListener(e -> {
            reservaService.verificarExpiracoes();
            txtResultados.append("[UI] Verificação de expirações concluída.\n");
        });
        btnSimularConcorrencia.addActionListener(e -> simularDoisClientes());
    }

    private void registarConsumidores() {
        EventBus bus = reservaService.getEventBus();
        bus.subscribe(ReservaCriadaEvent.class, evento -> SwingUtilities.invokeLater(() ->
                txtResultados.append("[EVENTO] Reserva criada: " + evento.getIdReserva() + "\n")));
        bus.subscribe(ReservaExpiradaEvent.class, evento -> SwingUtilities.invokeLater(() ->
                txtResultados.append("[EVENTO] Reserva expirada; stock devolvido: " + evento.getIdReserva() + "\n")));
    }

    private void executarPesquisa() {
        var resultados = CatalogoService.pesquisarMedicamentos(catalogo.listarTodos(), txtPesquisa.getText());
        txtResultados.append("\n=== RESULTADOS DA PESQUISA ===\n");
        if (resultados.isEmpty()) txtResultados.append("Nenhum medicamento com stock válido foi encontrado.\n");
        resultados.forEach(m -> txtResultados.append(m.getId() + " | " + m.getNome() + " | " + m.getPreco() + " MZN | Stock: " + m.getStockDisponivel() + "\n"));
    }

    private void executarReserva(JButton botao) {
        String idMed = txtIdMedicamento.getText().trim();
        String idReserva = txtIdReserva.getText().trim();
        String utente = txtNomeUtente.getText().trim();
        String quantidadeTexto = txtQuantidade.getText().trim();
        if (idMed.isEmpty() || idReserva.isEmpty() || utente.isEmpty() || quantidadeTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha o ID do medicamento, o ID da reserva, a quantidade e o nome do utente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Medicamento medicamento = catalogo.procurarPorId(idMed).orElse(null);
        if (medicamento == null) {
            JOptionPane.showMessageDialog(this, "Medicamento não encontrado: " + idMed, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser um número inteiro.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String nomeFarmacia = (String) cbFarmacias.getSelectedItem();
        if (quantidade <= 0) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!medicamento.temStockSuficiente(quantidade)) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente para " + quantidade + " unidade(s) de " + medicamento.getNome() + ". Stock disponível: " + medicamento.getStockDisponivel(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        botao.setEnabled(false);
        new SwingWorker<Reserva, Void>() {
            @Override protected Reserva doInBackground() { return reservaService.criarReserva(idReserva, medicamento, quantidade, 24); }
            @Override protected void done() {
                botao.setEnabled(true);
                try {
                    Reserva reserva = get();
                    if (reserva == null) txtResultados.append("[FALHA] Stock esgotado; reserva não criada.\n");
                    else txtResultados.append("Utente: " + utente + " | Reserva: " + reserva.getIdReserva()
                            + " | Medicamento: " + medicamento.getNome() + " | Qtde: " + reserva.getQuantidade()
                            + " | Farmácia: " + nomeFarmacia + " | TTL: 24h\n");
                    txtIdMedicamento.setText(""); txtIdReserva.setText(""); txtQuantidade.setText(""); txtNomeUtente.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MediMaputoGUI.this, ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void simularDoisClientes() {
        txtResultados.append("\n=== SIMULAÇÃO DE 2 CLIENTES CONCORRENTES ===\n");
        Medicamento medicamento = catalogo.procurarPorId("MED001").orElse(null);
        if (medicamento == null) {
            txtResultados.append("Medicamento MED001 não encontrado.\n");
            return;
        }
        txtResultados.append("[CLIENTE A] Vai tentar reservar 1 unidade de " + medicamento.getNome() + "\n");
        txtResultados.append("[CLIENTE B] Vai tentar reservar 1 unidade de " + medicamento.getNome() + "\n");
        new Thread(() -> {
            Reserva r1 = reservaService.criarReserva("RES-CONC-A", medicamento, 1, 24);
            SwingUtilities.invokeLater(() -> txtResultados.append("[RESULTADO] Cliente A: " + (r1 != null ? "reservado" : "sem stock") + "\n"));
        }).start();
        new Thread(() -> {
            Reserva r2 = reservaService.criarReserva("RES-CONC-B", medicamento, 1, 24);
            SwingUtilities.invokeLater(() -> txtResultados.append("[RESULTADO] Cliente B: " + (r2 != null ? "reservado" : "sem stock") + "\n"));
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MediMaputoGUI().setVisible(true));
    }
}