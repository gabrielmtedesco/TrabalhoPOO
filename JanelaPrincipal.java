import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Tela inicial do cassino e navegação para as janelas auxiliares. */
public class JanelaPrincipal extends JFrame {
    private final CasinoService cassino;
    private final ArquivoService arquivos;
    private final JLabel resumo = Estilo.texto("");

    public JanelaPrincipal(CasinoService cassino, ArquivoService arquivos) {
        this.cassino = cassino;
        this.arquivos = arquivos;
        setTitle("Cassino");
        setSize(710, 510);
        setMinimumSize(new Dimension(580, 440));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { sair(); }
        });
        JPanel principal = Estilo.painel();
        principal.setLayout(new BorderLayout(22, 22));
        JLabel titulo = Estilo.titulo("CASSINO");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 40));
        principal.add(titulo, BorderLayout.NORTH);
        JPanel botoes = new JPanel(new GridLayout(3, 2, 16, 16));
        botoes.setOpaque(false);
        adicionar(botoes, "Roleta", () -> new JanelaRoleta(this, cassino, arquivos, this::atualizarResumo).setVisible(true));
        adicionar(botoes, "Blackjack", () -> new JanelaBlackjack(this, cassino, arquivos, this::atualizarResumo).setVisible(true));
        adicionar(botoes, "Clientes", () -> new JanelaClientes(this, cassino, arquivos, this::atualizarResumo).setVisible(true));
        adicionar(botoes, "Estatísticas", () -> new JanelaEstatisticas(this, cassino).setVisible(true));
        adicionar(botoes, "Histórico", () -> new JanelaHistorico(this, cassino).setVisible(true));
        adicionar(botoes, "Sair", this::sair);
        principal.add(botoes, BorderLayout.CENTER);
        resumo.setHorizontalAlignment(SwingConstants.CENTER);
        atualizarResumo();
        principal.add(resumo, BorderLayout.SOUTH);
        setContentPane(principal);
    }
    private void adicionar(JPanel painel, String nome, Runnable acao) {
        JButton b = Estilo.botao(nome);
        b.setFont(new Font("SansSerif", Font.BOLD, 19));
        b.addActionListener(e -> acao.run());
        painel.add(b);
    }
    private void atualizarResumo() {
        resumo.setText("Clientes: " + cassino.obterTotalClientes()
                + "       |       Partidas: " + cassino.obterTotalPartidas());
    }
    private void sair() {
        Blackjack blackjack = (Blackjack) cassino.buscarJogo("Blackjack");
        if (blackjack.isEmAndamento()) {
            JOptionPane.showMessageDialog(this, "Termine a partida de Blackjack antes de sair.");
            return;
        }
        try {
            arquivos.salvarTudo();
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível salvar os dados: " + ex.getMessage(),
                    "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
