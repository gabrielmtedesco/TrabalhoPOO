import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/** Pesquisa e visualização do histórico de partidas. */
public class JanelaHistorico extends JDialog {
    private final CasinoService cassino;
    private final JTextArea area = Estilo.area();
    private final JComboBox<String> filtro = new JComboBox<>();

    public JanelaHistorico(JFrame pai, CasinoService cassino) {
        super(pai, "Histórico", true);
        this.cassino = cassino;
        setSize(840, 560);
        setLocationRelativeTo(pai);
        JPanel painel = Estilo.painel();
        painel.setLayout(new BorderLayout(12, 12));
        painel.add(Estilo.titulo("HISTÓRICO DE PARTIDAS"), BorderLayout.NORTH);
        JPanel meio = new JPanel(new BorderLayout(8, 8));
        meio.setOpaque(false);
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.setOpaque(false);
        filtros.add(Estilo.texto("Cliente:"));
        filtro.addItem("Todos os clientes");
        for (Cliente c : cassino.listarClientes()) filtro.addItem(c.getUsuario());
        filtro.addActionListener(e -> atualizar());
        filtros.add(filtro);
        meio.add(filtros, BorderLayout.NORTH);
        meio.add(Estilo.rolagem(area), BorderLayout.CENTER);
        painel.add(meio, BorderLayout.CENTER);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setOpaque(false);
        JButton voltar = Estilo.botao("Voltar");
        voltar.addActionListener(e -> dispose());
        botoes.add(voltar);
        painel.add(botoes, BorderLayout.SOUTH);
        setContentPane(painel);
        atualizar();
    }

    private void atualizar() {
        String selecionado = (String) filtro.getSelectedItem();
        Cliente clienteFiltro = selecionado == null || selecionado.equals("Todos os clientes")
                ? null : cassino.buscarCliente(selecionado);
        StringBuilder texto = new StringBuilder();
        for (Partida partida : cassino.listarPartidas()) {
            if (clienteFiltro != null && clienteFiltro.getId() != partida.getClienteId()) continue;
            Cliente c = cassino.buscarClientePorId(partida.getClienteId());
            texto.append(partida.getDataHora()).append(" | ")
                 .append(c == null ? "ID " + partida.getClienteId() : c.getNome())
                 .append(" | ").append(partida.getNomeJogo())
                 .append("\nAposta: ").append(partida.getAposta())
                 .append(" | ").append(partida.getResultado())
                 .append(" | Variação: ").append(partida.getVariacaoFichas()).append("\n\n");
        }
        area.setText(texto.length() == 0 ? "Não há partidas para esta seleção." : texto.toString());
        area.setCaretPosition(0);
    }
}
