import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/** Tela de Blackjack com ações iniciar, comprar e parar. */
public class JanelaBlackjack extends JDialog {
    private final CasinoService cassino;
    private final ArquivoService arquivos;
    private final Runnable atualizarPrincipal;
    private final Blackjack jogo;
    private final JComboBox<String> clientes = new JComboBox<>();
    private final JTextField aposta = Estilo.campo(8);
    private final JTextArea mesa = Estilo.area();
    private final JButton iniciar = Estilo.botao("Iniciar partida");
    private final JButton comprar = Estilo.botao("Comprar carta");
    private final JButton parar = Estilo.botao("Parar");
    private String usuarioAtivo;
    private int apostaAtiva;

    public JanelaBlackjack(JFrame pai, CasinoService cassino, ArquivoService arquivos, Runnable atualizarPrincipal) {
        super(pai, "Blackjack", true);
        this.cassino = cassino;
        this.arquivos = arquivos;
        this.atualizarPrincipal = atualizarPrincipal;
        this.jogo = (Blackjack) cassino.buscarJogo("Blackjack");
        setSize(680, 510);
        setLocationRelativeTo(pai);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        JPanel painel = Estilo.painel();
        painel.setLayout(new BorderLayout(14, 14));
        painel.add(Estilo.titulo("BLACKJACK"), BorderLayout.NORTH);
        JPanel campos = new JPanel(new GridLayout(2, 2, 12, 12));
        campos.setOpaque(false);
        for (Cliente c : cassino.listarClientes()) clientes.addItem(c.getUsuario());
        if (clientes.getItemCount() > 0) clientes.setSelectedIndex(0);
        campos.add(Estilo.texto("Cliente:")); campos.add(clientes);
        campos.add(Estilo.texto("Valor da aposta:")); campos.add(aposta);
        JPanel centro = new JPanel(new BorderLayout(12, 12));
        centro.setOpaque(false);
        centro.add(campos, BorderLayout.NORTH);
        centro.add(Estilo.rolagem(mesa), BorderLayout.CENTER);
        painel.add(centro, BorderLayout.CENTER);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        acoes.setOpaque(false);
        iniciar.addActionListener(e -> acao("iniciar"));
        comprar.addActionListener(e -> acao("comprar"));
        parar.addActionListener(e -> acao("parar"));
        JButton voltar = Estilo.botao("Voltar");
        voltar.addActionListener(e -> voltar());
        acoes.add(iniciar); acoes.add(comprar); acoes.add(parar); acoes.add(voltar);
        painel.add(acoes, BorderLayout.SOUTH);
        setContentPane(painel);
        atualizarMesa(null);
    }

    private void acao(String comando) {
        try {
            if (comando.equals("iniciar")) {
                usuarioAtivo = (String) clientes.getSelectedItem();
                if (usuarioAtivo == null) throw new IllegalArgumentException("Cadastre um cliente primeiro.");
                apostaAtiva = Integer.parseInt(aposta.getText().trim());
            }
            Partida p = cassino.jogar(usuarioAtivo, "Blackjack", apostaAtiva, comando);
            if (p != null) {
                arquivos.salvarTudo();
                atualizarPrincipal.run();
            }
            atualizarMesa(p);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um valor inteiro válido para a aposta.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            atualizarMesa(null);
        }
    }

    private void atualizarMesa(Partida partida) {
        boolean ativa = jogo.isEmAndamento();
        iniciar.setEnabled(!ativa);
        comprar.setEnabled(ativa);
        parar.setEnabled(ativa);
        clientes.setEnabled(!ativa);
        aposta.setEnabled(!ativa);
        StringBuilder texto = new StringBuilder();
        if (jogo.getPontosJogador() == 0 && !ativa) {
            texto.append("Escolha um cliente, informe a aposta e inicie a partida.\n")
                 .append("Objetivo: aproximar-se de 21 sem ultrapassar.\n");
        } else {
            texto.append("Suas cartas: ").append(jogo.getMaoJogador()).append("\n")
                 .append("Seus pontos: ").append(jogo.getPontosJogador()).append("\n\n")
                 .append("Cartas do dealer: ").append(jogo.getMaoDealer()).append("\n")
                 .append("Pontos do dealer: ").append(ativa ? "Ocultos até o fim" : jogo.getPontosDealer())
                 .append("\n\n").append(jogo.getUltimaMensagem());
            if (partida != null) {
                texto.append("\n\nResultado: ").append(partida.getResultado())
                     .append("\nVariação: ").append(partida.getVariacaoFichas()).append(" fichas")
                     .append("\nSaldo: ").append(cassino.buscarCliente(usuarioAtivo).getSaldo()).append(" fichas");
            }
        }
        mesa.setText(texto.toString());
    }
    private void voltar() {
        if (jogo.isEmAndamento()) {
            JOptionPane.showMessageDialog(this, "Termine a partida de Blackjack antes de voltar.");
            return;
        }
        dispose();
    }
}
