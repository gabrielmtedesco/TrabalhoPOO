import java.util.ArrayList;

public class Cliente {
    private int id;
    private String nome;
    private String usuario;
    private String senha;
    private int saldo;
    private ArrayList<Partida> historico;

    public Cliente(int id, String nome, String usuario, String senha) {
        this(id, nome, usuario, senha, 1000);
    }

    // Construtor usado ao recuperar o saldo salvo em arquivo.
    public Cliente(int id, String nome, String usuario, String senha, int saldo) {
        if (id <= 0 || nome == null || nome.trim().isEmpty()
                || usuario == null || usuario.trim().isEmpty()
                || senha == null || senha.isEmpty() || saldo < 0) {
            throw new IllegalArgumentException("Dados invalidos do cliente.");
        }
        this.id = id;
        this.nome = nome;
        this.usuario = usuario;
        this.senha = senha;
        this.saldo = saldo;
        this.historico = new ArrayList<>();
    }

    public void atualizarSaldo(int valor) {
        long novoSaldo = (long) saldo + valor;
        if (novoSaldo < 0 || novoSaldo > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Saldo resultante invalido.");
        }
        saldo = (int) novoSaldo;
    }

    public void adicionarPartida(Partida partida) {
        if (partida == null) throw new IllegalArgumentException("Partida nula.");
        historico.add(partida);
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getUsuario() { return usuario; }
    public String getSenha() { return senha; }
    public int getSaldo() { return saldo; }
    public ArrayList<Partida> getHistorico() { return new ArrayList<>(historico); }
}
