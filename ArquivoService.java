import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/** Persistencia de arquivos texto seguindo os exemplos dos slides da disciplina. */
public class ArquivoService {
    private CasinoService cassino;
    private Path pasta;

    public ArquivoService(CasinoService cassino, String caminhoPasta) {
        if (cassino == null) throw new IllegalArgumentException("Service nulo.");
        if (caminhoPasta == null || caminhoPasta.trim().isEmpty()) {
            throw new IllegalArgumentException("Pasta de dados invalida.");
        }
        this.cassino = cassino;
        this.pasta = Paths.get(caminhoPasta);
    }

    private Path arquivo(String nome) {
        return pasta.resolve(nome);
    }

    private void prepararPasta() throws IOException {
        Files.createDirectories(pasta);
    }

    private String validarCampo(String valor) {
        if (valor == null || valor.contains(";") || valor.contains("\n") || valor.contains("\r")) {
            throw new IllegalArgumentException("Campo invalido: nao use ';' nem quebras de linha.");
        }
        return valor;
    }

    public void salvarClientes() throws IOException {
        prepararPasta();
        Path caminho = arquivo("clientes.txt");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(caminho, StandardCharsets.UTF_8))) {
            for (Cliente c : cassino.listarClientes()) {
                writer.println(c.getId() + ";" + validarCampo(c.getNome()) + ";"
                        + validarCampo(c.getUsuario()) + ";" + validarCampo(c.getSenha())
                        + ";" + c.getSaldo());
            }
        }
    }

    public void salvarPartidas() throws IOException {
        prepararPasta();
        Path caminho = arquivo("partidas.txt");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(caminho, StandardCharsets.UTF_8))) {
            for (Partida p : cassino.listarPartidas()) {
                writer.println(p.getClienteId() + ";" + validarCampo(p.getNomeJogo()) + ";"
                        + p.getAposta() + ";" + validarCampo(p.getResultado()) + ";"
                        + p.getVariacaoFichas() + ";" + validarCampo(p.getDataHora()));
            }
        }
    }

    /** Le os clientes em um CasinoService inicialmente vazio. */
    public void carregarClientes() throws IOException {
        if (!cassino.listarClientes().isEmpty()) {
            throw new IllegalStateException("Carregue clientes em um CasinoService vazio.");
        }
        Path caminho = arquivo("clientes.txt");
        if (!Files.exists(caminho)) return;
        try (Scanner sc = new Scanner(Files.newBufferedReader(caminho, StandardCharsets.UTF_8))) {
            int numero = 0;
            while (sc.hasNextLine()) {
                String linha = sc.nextLine();
                numero++;
                if (linha.trim().isEmpty()) continue;
                String[] campos = linha.split(";", -1);
                if (campos.length != 5) throw new IOException("Linha invalida em clientes.txt: " + numero);
                try {
                    cassino.cadastrarCliente(new Cliente(Integer.parseInt(campos[0]), campos[1],
                            campos[2], campos[3], Integer.parseInt(campos[4])));
                } catch (IllegalArgumentException e) {
                    throw new IOException("Cliente invalido na linha " + numero, e);
                }
            }
        }
    }

    public void carregarPartidas() throws IOException {
        if (!cassino.listarPartidas().isEmpty()) {
            throw new IllegalStateException("Carregue partidas em um CasinoService sem partidas.");
        }
        Path caminho = arquivo("partidas.txt");
        if (!Files.exists(caminho)) return;
        try (Scanner sc = new Scanner(Files.newBufferedReader(caminho, StandardCharsets.UTF_8))) {
            int numero = 0;
            while (sc.hasNextLine()) {
                String linha = sc.nextLine();
                numero++;
                if (linha.trim().isEmpty()) continue;
                String[] campos = linha.split(";", -1);
                if (campos.length != 6) throw new IOException("Linha invalida em partidas.txt: " + numero);
                try {
                    Partida p = new Partida(Integer.parseInt(campos[2]), campos[3],
                            Integer.parseInt(campos[4]), campos[1], Integer.parseInt(campos[0]), campos[5]);
                    cassino.recuperarPartida(p);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Partida invalida na linha " + numero, e);
                }
            }
        }
    }

    /** Na primeira execucao cria dados de exemplo, depois carrega o que foi salvo. */
    public void inicializar() throws IOException {
        Path clientes = arquivo("clientes.txt");
        Path partidas = arquivo("partidas.txt");
        if (!Files.exists(clientes) && !Files.exists(partidas)) {
            cassino.criarDadosExemplo();
            salvarTudo();
        } else {
            carregarClientes();
            carregarPartidas();
        }
    }

    public void salvarTudo() throws IOException {
        salvarClientes();
        salvarPartidas();
    }
}
