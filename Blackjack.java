import java.util.ArrayList;
import java.util.Collections;

public class Blackjack extends Jogo {
    private ArrayList<Integer> baralho;
    private ArrayList<Integer> maoJogador;
    private ArrayList<Integer> maoDealer;
    private Cliente clienteAtual;
    private int apostaAtual;
    private boolean emAndamento;
    private String ultimaMensagem = "";

    public Blackjack() { super("Blackjack"); }

    // escolha: "iniciar", "comprar" ou "parar".
    // Retorna null se o jogador ainda puder decidir; retorna Partida quando terminar.
    @Override
    public Partida jogar(Cliente cliente, int aposta, String escolha) {
        if (escolha == null) throw new IllegalArgumentException("Escolha obrigatoria.");
        String acao = escolha.trim().toLowerCase();
        if (acao.equals("iniciar")) {
            if (emAndamento) throw new IllegalStateException("Termine a partida atual primeiro.");
            validarAposta(cliente, aposta);
            // O pagamento maximo ocorre no Blackjack natural (3:2).
            // Evita estouro de int antes de iniciar a partida.
            if ((long) cliente.getSaldo() + (long) aposta * 3 / 2 > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Aposta muito alta para o saldo permitido.");
            }
            clienteAtual = cliente;
            apostaAtual = aposta;
            baralho = criarBaralho();
            maoJogador = new ArrayList<>();
            maoDealer = new ArrayList<>();
            maoJogador.add(comprarCarta());
            maoDealer.add(comprarCarta());
            maoJogador.add(comprarCarta());
            maoDealer.add(comprarCarta());
            emAndamento = true;
            ultimaMensagem = "Partida iniciada.";
            boolean bjJogador = calcularPontos(maoJogador) == 21;
            boolean bjDealer = calcularPontos(maoDealer) == 21;
            if (bjJogador && bjDealer) return finalizar("EMPATE", 0, "Ambos fizeram Blackjack.");
            if (bjJogador) {
                long premio = (long) apostaAtual * 3 / 2;
                if (premio > Integer.MAX_VALUE) throw new IllegalArgumentException("Premio muito alto.");
                return finalizar("VITORIA", (int) premio, "Blackjack natural!");
            }
            if (bjDealer) return finalizar("DERROTA", -apostaAtual, "Dealer fez Blackjack.");
            return null;
        }

        if (!emAndamento || clienteAtual != cliente || apostaAtual != aposta) {
            throw new IllegalStateException("Nao ha partida ativa deste cliente/aposta.");
        }
        if (acao.equals("comprar")) {
            maoJogador.add(comprarCarta());
            int pontos = calcularPontos(maoJogador);
            ultimaMensagem = "Jogador comprou uma carta.";
            if (pontos > 21) return finalizar("DERROTA", -apostaAtual, "Jogador estourou.");
            if (pontos == 21) return turnoDealer();
            return null;
        }
        if (acao.equals("parar")) return turnoDealer();
        throw new IllegalArgumentException("Acao invalida: use iniciar, comprar ou parar.");
    }

    private Partida turnoDealer() {
        while (calcularPontos(maoDealer) < 17) maoDealer.add(comprarCarta());
        int jogador = calcularPontos(maoJogador);
        int dealer = calcularPontos(maoDealer);
        if (dealer > 21 || jogador > dealer) return finalizar("VITORIA", apostaAtual, "Jogador venceu.");
        if (jogador < dealer) return finalizar("DERROTA", -apostaAtual, "Dealer venceu.");
        return finalizar("EMPATE", 0, "Empate.");
    }

    private Partida finalizar(String resultado, int variacao, String mensagem) {
        // O saldo so muda na conclusao; o service registra a partida centralmente.
        clienteAtual.atualizarSaldo(variacao);
        Partida partida = new Partida(apostaAtual, resultado, variacao);
        clienteAtual.adicionarPartida(partida);
        registrarPartida();
        ultimaMensagem = mensagem;
        emAndamento = false;
        clienteAtual = null;
        return partida;
    }

    public boolean isEmAndamento() { return emAndamento; }
    public String getUltimaMensagem() { return ultimaMensagem; }
    public int getPontosJogador() { return maoJogador == null ? 0 : calcularPontos(maoJogador); }
    public int getPontosDealer() { return maoDealer == null ? 0 : calcularPontos(maoDealer); }
    public String getMaoJogador() { return maoJogador == null ? "[]" : mostrarMao(maoJogador); }
    public String getMaoDealer() {
        if (maoDealer == null) return "[]";
        if (emAndamento) return "[" + nomeCarta(maoDealer.get(0)) + ", ?]";
        return mostrarMao(maoDealer);
    }

    private ArrayList<Integer> criarBaralho() {
        ArrayList<Integer> cartas = new ArrayList<>();
        for (int naipe = 0; naipe < 4; naipe++)
            for (int valor = 1; valor <= 13; valor++) cartas.add(valor);
        Collections.shuffle(cartas);
        return cartas;
    }
    private int comprarCarta() { return baralho.remove(baralho.size() - 1); }
    private int calcularPontos(ArrayList<Integer> mao) {
        int total = 0, ases = 0;
        for (int carta : mao) {
            if (carta == 1) { total += 11; ases++; }
            else if (carta >= 10) total += 10;
            else total += carta;
        }
        while (total > 21 && ases > 0) { total -= 10; ases--; }
        return total;
    }
    private String nomeCarta(int carta) {
        switch (carta) {
            case 1: return "A";
            case 11: return "J";
            case 12: return "Q";
            case 13: return "K";
            default: return String.valueOf(carta);
        }
    }
    private String mostrarMao(ArrayList<Integer> mao) {
        StringBuilder texto = new StringBuilder("[");
        for (int i = 0; i < mao.size(); i++) {
            if (i > 0) texto.append(", ");
            texto.append(nomeCarta(mao.get(i)));
        }
        return texto.append("]").toString();
    }
}
