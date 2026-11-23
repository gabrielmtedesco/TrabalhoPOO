import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/** Cadastro, consulta e listagem de clientes. */
public class JanelaClientes extends JDialog {
    private final CasinoService cassino;
    private final ArquivoService arquivos;
    private final Runnable atualizarPrincipal;
    private final JTextArea lista = Estilo.area();
    private final JTextField pesquisa = Estilo.campo(17);

    public JanelaClientes(JFrame pai, CasinoService cassino, ArquivoService arquivos, Runnable atualizarPrincipal) {
        super(pai, "Clientes", true);
        this.cassino = cassino;
        this.arquivos = arquivos;
        this.atualizarPrincipal = atualizarPrincipal;
        setSize(760, 510);
        setLocationRelativeTo(pai);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel painel = Estilo.painel();
        painel.setLayout(new BorderLayout(12, 12));
        painel.add(Estilo.titulo("CLIENTES"), BorderLayout.NORTH);
        painel.add(Estilo.rolagem(lista), BorderLayout.CENTER);

        JPanel inferior = new JPanel(new GridLayout(2, 1, 0, 12));
        inferior.setOpaque(false);
        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busca.setOpaque(false);
        busca.add(Estilo.texto("Usuário:"));
        busca.add(pesquisa);
        JButton procurar = Estilo.botao("Pesquisar");
        procurar.addActionListener(e -> pesquisar());
        busca.add(procurar);
        JButton todos = Estilo.botao("Listar todos");
        todos.addActionListener(e -> mostrarTodos());
        busca.add(todos);
        inferior.add(busca);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acoes.setOpaque(false);
        JButton cadastrar = Estilo.botao("Cadastrar cliente");
        cadastrar.addActionListener(e -> cadastrar());
        acoes.add(cadastrar);
        JButton fechar = Estilo.botao("Voltar");
        fechar.addActionListener(e -> dispose());
        acoes.add(fechar);
        inferior.add(acoes);
        painel.add(inferior, BorderLayout.SOUTH);
        setContentPane(painel);
        mostrarTodos();
    }

    private void mostrarTodos() {
        StringBuilder texto = new StringBuilder("ID | Nome | Usuário | Saldo de fichas\n\n");
        for (Cliente cliente : cassino.listarClientes()) {
            texto.append(cliente.getId()).append(" | ").append(cliente.getNome())
                 .append(" | ").append(cliente.getUsuario()).append(" | ")
                 .append(cliente.getSaldo()).append("\n");
        }
        if (cassino.listarClientes().isEmpty()) texto.append("Nenhum cliente cadastrado.");
        lista.setText(texto.toString());
        lista.setCaretPosition(0);
    }
    private void pesquisar() {
        Cliente cliente = cassino.buscarCliente(pesquisa.getText());
        if (cliente == null) {
            lista.setText("Nenhum cliente encontrado para esse usuário.");
        } else {
            lista.setText("ID: " + cliente.getId() + "\nNome: " + cliente.getNome()
                    + "\nUsuário: " + cliente.getUsuario() + "\nSaldo: " + cliente.getSaldo()
                    + " fichas\nPartidas: " + cliente.getHistorico().size());
        }
    }
    private void cadastrar() {
        JTextField nome = Estilo.campo(18);
        JTextField usuario = Estilo.campo(18);
        JPasswordField senha = new JPasswordField(18);
        JPanel formulario = new JPanel(new GridLayout(3, 2, 8, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        formulario.add(new JLabel("Nome:")); formulario.add(nome);
        formulario.add(new JLabel("Usuário:")); formulario.add(usuario);
        formulario.add(new JLabel("Senha:")); formulario.add(senha);
        int resposta = JOptionPane.showConfirmDialog(this, formulario, "Novo cliente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resposta != JOptionPane.OK_OPTION) return;
        try {
            Cliente cliente = new Cliente(cassino.proximoIdCliente(), nome.getText().trim(),
                    usuario.getText().trim(), new String(senha.getPassword()));
            cassino.cadastrarCliente(cliente);
            arquivos.salvarTudo();
            mostrarTodos();
            atualizarPrincipal.run();
            JOptionPane.showMessageDialog(this, "Cliente cadastrado! Saldo inicial: 1000 fichas.");
        } catch (IllegalArgumentException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
