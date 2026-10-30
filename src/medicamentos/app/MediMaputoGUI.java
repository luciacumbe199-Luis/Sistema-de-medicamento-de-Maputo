package medicamentos.app;

import medicamentos.model.Lote;
import medicamentos.model.Medicamento;
import medicamentos.model.Reserva;
import medicamentos.service.CatalogoService;
import medicamentos.service.ReservaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MediMaputoGUI extends JFrame {
    private List<Medicamento> catalogoGeral;
    private ReservaService reservaService;
    
    private JTextField txtPesquisa;
    private JTextField txtIdReserva;
    private JTextField txtIdMedicamento;
    private JTextField txtNomeUtente; // Novo campo para o nome do cidadão
    private JComboBox<String> cbFarmacias;
    private JTextArea txtResultados;

    public MediMaputoGUI() {
        setTitle("MediMaputo - Sistema Inteligente de Gestão de Farmácias");
        setSize(800, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarCatalogo();
        reservaService = new ReservaService();

        // Layout principal com margens limpas e espaçadas
        setLayout(new BorderLayout(15, 15));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().setBackground(new Color(240, 243, 246));

        // ----------------------------------------------------
        // Painel Superior: Pesquisa
        // ----------------------------------------------------
        JPanel panelPesquisa = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        panelPesquisa.setBorder(BorderFactory.createTitledBorder(" 1. Pesquisa de Medicamentos (Paradigma Funcional) "));
        panelPesquisa.setBackground(Color.WHITE);
        
        txtPesquisa = new JTextField(20);
        JButton btnPesquisar = new JButton("Pesquisar");
        btnPesquisar.setBackground(new Color(41, 128, 185)); // Azul moderno
        btnPesquisar.setForeground(Color.WHITE);
        btnPesquisar.setFocusPainted(false);
        btnPesquisar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        panelPesquisa.add(new JLabel("Nome / ID:"));
        panelPesquisa.add(txtPesquisa);
        panelPesquisa.add(btnPesquisar);

        // ----------------------------------------------------
        // Painel Intermédio: Reserva, Nome e Escolha da Farmácia
        // ----------------------------------------------------
        JPanel panelReserva = new JPanel(new GridLayout(4, 1, 5, 8));
        panelReserva.setBorder(BorderFactory.createTitledBorder(" 2. Efetuar Reserva e Identificação (Concorrência & TTL) "));
        panelReserva.setBackground(Color.WHITE);
        
        // Linha 1: IDs
        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        linha1.setOpaque(false);
        txtIdMedicamento = new JTextField(8);
        txtIdReserva = new JTextField(8);
        linha1.add(new JLabel("ID Med:"));
        linha1.add(txtIdMedicamento);
        linha1.add(Box.createHorizontalStrut(15));
        linha1.add(new JLabel("ID Reserva:"));
        linha1.add(txtIdReserva);

        // Linha 2: Nome do Utente
        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        linha2.setOpaque(false);
        txtNomeUtente = new JTextField(25);
        linha2.add(new JLabel("Nome do Utente:"));
        linha2.add(txtNomeUtente);

        // Linha 3: Farmácia de Levantamento
        JPanel linha3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        linha3.setOpaque(false);
        cbFarmacias = new JComboBox<>(new String[]{
            "Farmácia Central (Av. 24 de Julho)",
            "Farmácia Maputo (Baixa)",
            "Farmácia Polana Cimento (Av. Julius Nyerere)"
        });
        cbFarmacias.setPreferredSize(new Dimension(280, 26));
        linha3.add(new JLabel("Farmácia:"));
        linha3.add(cbFarmacias);

        // Linha 4: Botão de Confirmação
        JPanel linha4 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        linha4.setOpaque(false);
        JButton btnReservar = new JButton("Confirmar Reserva");
        btnReservar.setBackground(new Color(39, 174, 96)); // Verde moderno
        btnReservar.setForeground(Color.WHITE);
        btnReservar.setFocusPainted(false);
        btnReservar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        linha4.add(btnReservar);

        panelReserva.add(linha1);
        panelReserva.add(linha2);
        panelReserva.add(linha3);
        panelReserva.add(linha4);

        JPanel panelTopoGeral = new JPanel(new BorderLayout(0, 10));
        panelTopoGeral.setOpaque(false);
        panelTopoGeral.add(panelPesquisa, BorderLayout.NORTH);
        panelTopoGeral.add(panelReserva, BorderLayout.CENTER);
        add(panelTopoGeral, BorderLayout.NORTH);

        // ----------------------------------------------------
        // Painel Central: Resultados e Logs
        // ----------------------------------------------------
        txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtResultados.setBackground(new Color(252, 252, 252));
        
        JScrollPane scrollPane = new JScrollPane(txtResultados);
        scrollPane.setBorder(BorderFactory.createTitledBorder(" Registo de Atividade / Detalhes da Reserva "));
        add(scrollPane, BorderLayout.CENTER);

        // Ações dos Botões
        btnPesquisar.addActionListener(e -> executarPesquisa());
        txtPesquisa.addActionListener(e -> executarPesquisa());
        btnReservar.addActionListener(e -> executarReserva());
    }

    private void inicializarCatalogo() {
        catalogoGeral = new ArrayList<>();

        Medicamento m1 = new Medicamento("MED001", "Paracetamol 500mg", 150.0, false);
        m1.adicionarLote(new Lote("LOTE-A1", 10, LocalDate.now().plusMonths(6)));

        Medicamento m2 = new Medicamento("MED002", "Ibuprofeno 400mg", 250.0, false);
        m2.adicionarLote(new Lote("LOTE-B2", 5, LocalDate.now().plusMonths(3)));

        Medicamento m3 = new Medicamento("MED003", "Amoxicilina 500mg", 400.0, true);
        m3.adicionarLote(new Lote("LOTE-C3", 2, LocalDate.now().minusDays(2))); // Expirado

        catalogoGeral.add(m1);
        catalogoGeral.add(m2);
        catalogoGeral.add(m3);
    }

    private void executarPesquisa() {
        String termo = txtPesquisa.getText().trim();
        if (termo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, digite o nome de um medicamento para pesquisar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Medicamento> resultadosValidos = CatalogoService.pesquisarMedicamentos(catalogoGeral, termo);

        txtResultados.setText("");

        if (!resultadosValidos.isEmpty()) {
            txtResultados.append("=== MEDICAMENTOS DISPONÍVEIS NAS FARMÁCIAS ===\n\n");
            for (Medicamento m : resultadosValidos) {
                txtResultados.append(" • ID: " + m.getId() + " | " + m.getNome() + " | Preço: " + m.getPreco() + " MZN\n");
                txtResultados.append("   Status: Disponível para reserva imediata.\n");
                txtResultados.append("--------------------------------------------------\n");
            }
            txtResultados.append("\n[DICA] Copie o ID (ex: MED001), preencha o seu nome e escolha a farmácia.\n");
        } else {
            txtResultados.append("--------------------------------------------------\n");
            txtResultados.append(" [ESTADO: MEDICAMENTO ESGOTADO / INDISPONÍVEL]\n");
            
        }
    }

    private void executarReserva() {
        String idMed = txtIdMedicamento.getText().trim();
        String idRes = txtIdReserva.getText().trim();
        String nomeUtente = txtNomeUtente.getText().trim();
        String farmaciaEscolhida = (String) cbFarmacias.getSelectedItem();

        if (idMed.isEmpty() || idRes.isEmpty() || nomeUtente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o ID do Medicamento, o ID da Reserva e o Nome do Utente!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Medicamento medAlvo = null;
        for (Medicamento m : catalogoGeral) {
            if (m.getId().equalsIgnoreCase(idMed)) {
                medAlvo = m;
                break;
            }
        }

        if (medAlvo == null) {
            JOptionPane.showMessageDialog(this, "Medicamento não encontrado com o ID: " + idMed, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int horasTTL = 24;
        Reserva novaReserva = reservaService.criarReserva(idRes, medAlvo, horasTTL);

        if (novaReserva != null) {
            txtResultados.append("\n==================================================\n");
            txtResultados.append(" [SUCESSO] RESERVA EFETUADA COM SUCESSO!\n");
            txtResultados.append("==================================================\n");
            txtResultados.append(" • Utente: " + nomeUtente + "\n");
            txtResultados.append(" • ID da Reserva: " + novaReserva.getIdReserva() + "\n");
            txtResultados.append(" • Medicamento: " + medAlvo.getNome() + " (" + medAlvo.getPreco() + " MZN)\n");
            txtResultados.append(" • LOCAL DE LEVANTAMENTO:\n");
            txtResultados.append("   -> " + farmaciaEscolhida + "\n");
            txtResultados.append(" • VALIDADE (TTL): " + horasTTL + " horas\n");
            txtResultados.append("--------------------------------------------------\n");
        } else {
            txtResultados.append("\n[FALHA] Não foi possível reservar (Stock esgotado ou concorrência ativa).\n");
        }
        
        txtIdMedicamento.setText("");
        txtIdReserva.setText("");
        txtNomeUtente.setText("");
    }

    public static void main(String[] args) {
        // Aplica o visual moderno "Nimbus" do Java Swing automaticamente
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Se falhar, mantém o padrão do sistema operativo
        }

        SwingUtilities.invokeLater(() -> {
            new MediMaputoGUI().setVisible(true);
        });
    }
}