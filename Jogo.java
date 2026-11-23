public abstract class Jogo {
    private String nome;
    private int apostaMinima;
    private int totalPartidas;

    public Jogo(String nome) {
        this.nome = nome;
        this.apostaMinima = 1;
        this.totalPartidas = 0;
    }

    public String getNome() { return nome; }
    public int getApostaMinima() { return apostaMinima; }
    public int getTotalPartidas() { return totalPartidas; }
    public void registrarPartida() { totalPartidas++; }

    protected void validarAposta(Cliente cliente, int aposta) {
        if (cliente == null) throw new IllegalArgumentException("Cliente inexistente.");
        if (aposta < apostaMinima || aposta > cliente.getSaldo()) {
            throw new IllegalArgumentException("Aposta invalida ou saldo insuficiente.");
        }
    }

    // Retorna null enquanto uma partida ainda nao terminou (Blackjack).
    // A interface chama este metodo com a escolha do usuario.
    public abstract Partida jogar(Cliente cliente, int aposta, String escolha);
}
