public abstract class Jogo {
    private String nome;
    private int apostaMinima;
    private int totalPartidas;

    public Jogo(String nome){
        this.nome = nome;
        this.apostaMinima = 1;
        this.totalPartidas = 0;
    }

    public String getNome(){
        return nome;
    }

    public int getApostaMinima(){
        return apostaMinima;
    }

    public int getTotalPartidas(){
        return totalPartidas;
    }

    public void registarPartida(){
        totalPartidas++;
    }

    public abstract Partida Jogar(Cliente cliente, int aposta);

}
