import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/** Painel de estatísticas e ranking calculados pelo CasinoService. */
public class JanelaEstatisticas extends JDialog {
    public JanelaEstatisticas(JFrame pai, CasinoService cassino) {
        super(pai, "Estatísticas", true);
        setSize(600, 510);
        setLocationRelativeTo(pai);
        JPanel p = Estilo.painel();
        p.setLayout(new BorderLayout(12, 12));
        p.add(Estilo.titulo("ESTATÍSTICAS"), BorderLayout.NORTH);
        JTextArea area = Estilo.area();
        StringBuilder sb = new StringBuilder();
        sb.append("JOGOS (INSTÂNCIAS POR ESPECIALIZAÇÃO)\n\n")
          .append("Total de jogos: ").append(cassino.obterTotalJogos()).append("\n")
          .append("Roleta: ").append(cassino.contarJogosPorTipo("Roleta")).append("\n")
          .append("Blackjack: ").append(cassino.contarJogosPorTipo("Blackjack")).append("\n\n")
          .append("CLIENTES E PARTIDAS\n\n")
          .append("Total de clientes: ").append(cassino.obterTotalClientes()).append("\n")
          .append("Total de partidas: ").append(cassino.obterTotalPartidas()).append("\n")
          .append("Partidas de Roleta: ").append(cassino.contarPartidasPorJogo("Roleta")).append("\n")
          .append("Partidas de Blackjack: ").append(cassino.contarPartidasPorJogo("Blackjack")).append("\n\n")
          .append("FICHAS E APOSTAS\n\n")
          .append("Soma das apostas: ").append(cassino.obterTotalApostado()).append("\n")
          .append(String.format("Média das apostas: %.2f\n", cassino.obterMediaApostas()))
          .append("Fichas em circulação: ").append(cassino.obterTotalFichas()).append("\n")
          .append(String.format("Média de fichas por cliente: %.2f\n", cassino.obterMediaFichas()))
          .append("\nRANKING DE CLIENTES (SALDO)\n\n");
        int posicao = 1;
        for (Cliente c : cassino.obterRanking()) {
            sb.append(posicao++).append("º ").append(c.getNome()).append(" (@")
              .append(c.getUsuario()).append(") — ").append(c.getSaldo()).append(" fichas\n");
        }
        area.setText(sb.toString());
        p.add(Estilo.rolagem(area), BorderLayout.CENTER);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setOpaque(false);
        JButton fechar = Estilo.botao("Voltar");
        fechar.addActionListener(e -> dispose());
        botoes.add(fechar);
        p.add(botoes, BorderLayout.SOUTH);
        setContentPane(p);
    }
}
