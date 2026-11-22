public class Partida {
    private int aposta;
    private String resultado;
    private int variacaoFichas;

    public Partida(int aposta, String resultado, int variacaoFichas) {
        this.aposta = aposta;
        this.resultado = resultado;
        this.variacaoFichas = variacaoFichas;
    }

    public int getAposta() {
        return aposta;
    }

    public String getResultado() {
        return resultado;
    }

    public int getVariacaoFichas() {
        return variacaoFichas;
    }

    @Override
    public String toString() {
        return "Aposta: " + aposta + " | Resultado: " + resultado + " | Variacao de Fichas: " + variacaoFichas;
    }
}