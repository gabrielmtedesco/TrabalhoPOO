import java.util.ArrayList;

public class Cliente{
    private int id;
    private String nome;
    private String usuario;
    private String senha;
    private int saldo;
    private ArrayList<Partida>historico;

    public Cliente(int id, String nome, String usuario, String senha){
        this.id = id;
        this.nome = nome;
        this.usuario = usuario;
        this.senha = senha;
        this.saldo = 1000;
        this.historico = new ArrayList<>();
    }

    public void atualizarSaldo(int valor){
        saldo += valor;
    }

    public void adicionarPartida(Partida partida){
        historico.add(partida);
    }

    public int getId(){
        return id;
    }

    public String getNome(){
        return nome;
    }

    public String getUsuario(){
        return usuario;
    }

    public String getSenha(){
        return senha;
    }

    public int getSaldo(){
        return saldo;
    }

    public ArrayList<Partida>getHistorico(){
        return historico;
    }

}