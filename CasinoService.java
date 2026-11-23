import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/** Gerencia os dados do cassino, independentemente da interface grafica. */
public class CasinoService {
    private ArrayList<Cliente> clientes;
    private ArrayList<Jogo> jogos;
    private ArrayList<Partida> partidas;

    public CasinoService() {
        clientes = new ArrayList<>();
        jogos = new ArrayList<>();
        partidas = new ArrayList<>();
        jogos.add(new Roleta());
        jogos.add(new Blackjack());
    }

    public void cadastrarCliente(Cliente cliente) {
        if (cliente == null) throw new IllegalArgumentException("Cliente nulo.");
        if (buscarCliente(cliente.getUsuario()) != null) {
            throw new IllegalArgumentException("Usuario ja cadastrado.");
        }
        for (Cliente c : clientes) {
            if (c.getId() == cliente.getId()) {
                throw new IllegalArgumentException("ID ja cadastrado.");
            }
        }
        clientes.add(cliente);
    }

    public Cliente autenticar(String usuario, String senha) {
        Cliente cliente = buscarCliente(usuario);
        return cliente != null && cliente.getSenha().equals(senha) ? cliente : null;
    }

    public Cliente buscarCliente(String usuario) {
        if (usuario == null) return null;
        for (Cliente c : clientes) {
            if (c.getUsuario().equalsIgnoreCase(usuario.trim())) return c;
        }
        return null;
    }

    public Cliente buscarClientePorId(int id) {
        for (Cliente c : clientes) if (c.getId() == id) return c;
        return null;
    }

    public Jogo buscarJogo(String nome) {
        if (nome == null) return null;
        for (Jogo jogo : jogos) {
            if (jogo.getNome().equalsIgnoreCase(nome.trim())) return jogo;
        }
        return null;
    }

    public ArrayList<Cliente> listarClientes() { return new ArrayList<>(clientes); }
    public ArrayList<Jogo> listarJogos() { return new ArrayList<>(jogos); }
    public ArrayList<Partida> listarPartidas() { return new ArrayList<>(partidas); }

    public ArrayList<Partida> buscarPartidasPorCliente(int clienteId) {
        return partidas.stream().filter(p -> p.getClienteId() == clienteId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Cliente> obterRanking() {
        ArrayList<Cliente> ranking = new ArrayList<>(clientes);
        ranking.sort(Comparator.comparingInt(Cliente::getSaldo).reversed());
        return ranking;
    }

    /** A interface informa a escolha; null significa Blackjack ainda em andamento. */
    public Partida jogar(String usuario, String nomeJogo, int aposta, String escolha) {
        Cliente cliente = buscarCliente(usuario);
        Jogo jogo = buscarJogo(nomeJogo);
        if (cliente == null || jogo == null) throw new IllegalArgumentException("Cliente ou jogo inexistente.");
        Partida partida = jogo.jogar(cliente, aposta, escolha);
        if (partida != null) registrarPartida(cliente, jogo, partida);
        return partida;
    }

    /** O jogo atualiza saldo e historico; o service organiza o registro central. */
    public void registrarPartida(Cliente cliente, Jogo jogo, Partida partida) {
        if (cliente == null || jogo == null || partida == null
                || buscarClientePorId(cliente.getId()) != cliente || buscarJogo(jogo.getNome()) != jogo) {
            throw new IllegalArgumentException("Dados da partida invalidos.");
        }
        if (partidas.contains(partida)) throw new IllegalArgumentException("Partida ja registrada.");
        partida.associar(cliente.getId(), jogo.getNome());
        partidas.add(partida);
        if (!cliente.getHistorico().contains(partida)) cliente.adicionarPartida(partida);
    }

    /** Reconstroi o historico a partir do arquivo, sem alterar novamente o saldo. */
    public void recuperarPartida(Partida partida) {
        Cliente cliente = buscarClientePorId(partida.getClienteId());
        Jogo jogo = buscarJogo(partida.getNomeJogo());
        if (cliente == null || jogo == null) throw new IllegalArgumentException("Partida com referencia desconhecida.");
        registrarPartida(cliente, jogo, partida);
        jogo.registrarPartida();
    }

    // Estatisticas com colecoes e programacao funcional (streams e lambdas).
    public long obterTotalJogos() { return jogos.size(); }
    public long contarJogosPorTipo(String tipo) {
        return jogos.stream().filter(j -> j.getClass().getSimpleName().equalsIgnoreCase(tipo)).count();
    }
    public long obterTotalClientes() { return clientes.size(); }
    public long obterTotalPartidas() { return partidas.size(); }
    public long contarPartidasPorJogo(String nomeJogo) {
        return partidas.stream().filter(p -> p.getNomeJogo().equalsIgnoreCase(nomeJogo)).count();
    }
    public long obterTotalApostado() {
        return partidas.stream().mapToLong(Partida::getAposta).sum();
    }
    public double obterMediaApostas() {
        return partidas.stream().mapToInt(Partida::getAposta).average().orElse(0.0);
    }
    public long obterTotalFichas() {
        return clientes.stream().mapToLong(Cliente::getSaldo).sum();
    }
    public double obterMediaFichas() {
        return clientes.stream().mapToInt(Cliente::getSaldo).average().orElse(0.0);
    }

    public int proximoIdCliente() {
        int maior = 0;
        for (Cliente c : clientes) if (c.getId() > maior) maior = c.getId();
        return maior + 1;
    }

    public void criarDadosExemplo() {
        if (!clientes.isEmpty() || !partidas.isEmpty()) return;
        cadastrarCliente(new Cliente(1, "Ana", "ana", "teste123", 1200));
        cadastrarCliente(new Cliente(2, "Bruno", "bruno", "teste123", 850));
        cadastrarCliente(new Cliente(3, "Carla", "carla", "teste123", 1450));
        recuperarPartida(new Partida(100, "VITORIA", 100, "Blackjack", 1, "2026-11-20 18:10:00"));
        recuperarPartida(new Partida(50, "DERROTA", -50, "Roleta", 2, "2026-11-20 18:15:00"));
        recuperarPartida(new Partida(200, "VITORIA", 200, "Roleta", 3, "2026-11-20 18:20:00"));
    }
}
