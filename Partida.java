import java.text.SimpleDateFormat;
import java.util.Date;

public class Partida {
    private int aposta;
    private String resultado;
    private int variacaoFichas;
    private String dataHora;
    private String nomeJogo;
    private int clienteId;

    // Mantem compatibilidade com o construtor original.
    public Partida(int aposta, String resultado, int variacaoFichas) {
        this(aposta, resultado, variacaoFichas, "", -1,
             new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    public Partida(int aposta, String resultado, int variacaoFichas,
                   String nomeJogo, int clienteId, String dataHora) {
        this.aposta = aposta;
        this.resultado = resultado;
        this.variacaoFichas = variacaoFichas;
        this.nomeJogo = nomeJogo;
        this.clienteId = clienteId;
        this.dataHora = dataHora;
    }

    public int getAposta() { return aposta; }
    public String getResultado() { return resultado; }
    public int getVariacaoFichas() { return variacaoFichas; }
    public String getDataHora() { return dataHora; }
    public String getNomeJogo() { return nomeJogo; }
    public int getClienteId() { return clienteId; }

    public void associar(int clienteId, String nomeJogo) {
        this.clienteId = clienteId;
        this.nomeJogo = nomeJogo;
    }

    @Override
    public String toString() {
        return dataHora + " | " + nomeJogo + " | Aposta: " + aposta
               + " | " + resultado + " | Variacao: " + variacaoFichas;
    }
}
