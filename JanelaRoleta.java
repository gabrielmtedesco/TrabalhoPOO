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

/** Tela de apostas da roleta. */
public class JanelaRoleta extends JDialog {
    private final CasinoService cassino;
    private final ArquivoService arquivos;
    private final Runnable atualizarPrincipal;
    private final JComboBox<String> clientes = new JComboBox<>();
    private final JComboBox<String> tipo = new JComboBox<>(new String[] {
            "Vermelho", "Preto", "Par", "Ímpar", "Número (0 a 36)"});
    private final JTextField numero = Estilo.campo(5);
    private final JTextField aposta = Estilo.campo(7);
    private final JTextArea resultado = Estilo.area();

    public JanelaRoleta(JFrame pai, CasinoService cassino, ArquivoService arquivos, Runnable atualizarPrincipal) {
        super(pai, "Roleta", true);
        this.cassino = cassino;
        this.arquivos = arquivos;
        this.atualizarPrincipal = atualizarPrincipal;
        setSize(640, 480);
        setLocationRelativeTo(pai);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel painel = Estilo.painel();
        painel.setLayout(new BorderLayout(12, 12));
        painel.add(Estilo.titulo("ROLETA"), BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridLayout(4, 2, 12, 12));
        campos.setOpaque(false);
        for (Cliente c : cassino.listarClientes()) clientes.addItem(c.getUsuario());
        clientes.setSelectedIndex(clientes.getItemCount() > 0 ? 0 : -1);
        campos.add(Estilo.texto("Cliente:")); campos.add(clientes);
        campos.add(Estilo.texto("Valor da aposta:")); campos.add(aposta);
        campos.add(Estilo.texto("Tipo da aposta:")); campos.add(tipo);
        campos.add(Estilo.texto("Número escolhido:")); campos.add(numero);
        numero.setEnabled(false);
        tipo.addActionListener(e -> numero.setEnabled(tipo.getSelectedIndex() == 4));

        JPanel meio = new JPanel(new BorderLayout(12, 12));
        meio.setOpaque(false);
        meio.add(campos, BorderLayout.NORTH);
        meio.add(Estilo.rolagem(resultado), BorderLayout.CENTER);
        resultado.setText("Escolha o cliente, o valor e o tipo de aposta.\n\nVermelho/preto e par/ímpar pagam 1:1. Número exato paga 35:1.\nO zero não é par nem ímpar.");
        painel.add(meio, BorderLayout.CENTER);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acoes.setOpaque(false);
        JButton girar = Estilo.botao("Girar roleta");
        girar.addActionListener(e -> girar());
        acoes.add(girar);
        JButton voltar = Estilo.botao("Voltar");
        voltar.addActionListener(e -> dispose());
        acoes.add(voltar);
        painel.add(acoes, BorderLayout.SOUTH);
        setContentPane(painel);
    }

    private void girar() {
        try {
            String usuario = (String) clientes.getSelectedItem();
            if (usuario == null) throw new IllegalArgumentException("Cadastre um cliente primeiro.");
            int valor = Integer.parseInt(aposta.getText().trim());
            String escolha;
            switch (tipo.getSelectedIndex()) {
                case 0: escolha = "cor:vermelho"; break;
                case 1: escolha = "cor:preto"; break;
                case 2: escolha = "par"; break;
                case 3: escolha = "impar"; break;
                default: escolha = "numero:" + numero.getText().trim();
            }
            Partida p = cassino.jogar(usuario, "Roleta", valor, escolha);
            Roleta jogo = (Roleta) cassino.buscarJogo("Roleta");
            arquivos.salvarTudo();
            resultado.setText("Número sorteado: " + jogo.getUltimoNumeroSorteado()
                    + " (" + jogo.getUltimaCorSorteada() + ")\n\nResultado: " + p.getResultado()
                    + "\nVariação: " + p.getVariacaoFichas() + " fichas"
                    + "\nSaldo atual: " + cassino.buscarCliente(usuario).getSaldo() + " fichas");
            atualizarPrincipal.run();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um valor inteiro válido para a aposta.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
