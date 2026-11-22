import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Blackjack extends Jogo {

    private final Scanner scanner = new Scanner(System.in);

    public Blackjack() {
        super("Blackjack");
    }

    @Override
    public Partida Jogar(Cliente cliente, int aposta) {
        // Validações da aposta
        if (aposta < getApostaMinima()) {
            System.out.println("Aposta inválida. A aposta mínima é " + getApostaMinima() + ".");
            return null;
        }
        if (cliente.getSaldo() < aposta) {
            System.out.println("Saldo insuficiente para essa aposta.");
            return null;
        }

        // Baralho embaralhado
        List<Integer> baralho = criarBaralho();

        // Distribui duas cartas para o jogador e para o dealer
        List<Integer> maoJogador = new ArrayList<>();
        List<Integer> maoDealer = new ArrayList<>();
        maoJogador.add(comprarCarta(baralho));
        maoDealer.add(comprarCarta(baralho));
        maoJogador.add(comprarCarta(baralho));
        maoDealer.add(comprarCarta(baralho));

        System.out.println("\n=== BLACKJACK ===");
        System.out.println("Dealer: [" + nomeCarta(maoDealer.get(0)) + ", ?]");
        System.out.println("Você:   " + mostrarMao(maoJogador) + " = " + calcularPontos(maoJogador));

        boolean blackjackJogador = maoJogador.size() == 2 && calcularPontos(maoJogador) == 21;
        boolean blackjackDealer = maoDealer.size() == 2 && calcularPontos(maoDealer) == 21;

        String resultado;
        int variacaoSaldo; // positivo = ganhou, negativo = perdeu, 0 = empate

        if (blackjackJogador || blackjackDealer) {
            System.out.println("Dealer: " + mostrarMao(maoDealer) + " = " + calcularPontos(maoDealer));
            if (blackjackJogador && blackjackDealer) {
                resultado = "EMPATE";
                variacaoSaldo = 0;
                System.out.println("Ambos têm Blackjack! Empate.");
            } else if (blackjackJogador) {
                resultado = "VITORIA";
                variacaoSaldo = aposta * 3 / 2; // Blackjack paga 3:2 (divisão inteira)
                System.out.println("BLACKJACK! Você ganhou!");
            } else {
                resultado = "DERROTA";
                variacaoSaldo = -aposta;
                System.out.println("Dealer fez Blackjack. Você perdeu.");
            }
        } else {
            // Turno do jogador: hit ou stand
            boolean jogadorEstourou = false;
            while (true) {
                int pontos = calcularPontos(maoJogador);
                if (pontos > 21) {
                    jogadorEstourou = true;
                    break;
                }
                if (pontos == 21) {
                    System.out.println("Você atingiu 21!");
                    break;
                }

                System.out.print("Escolha: (H)it para comprar ou (S)tand para parar: ");
                String escolha = scanner.nextLine().trim().toLowerCase();

                if (escolha.equals("h") || escolha.equals("hit")) {
                    maoJogador.add(comprarCarta(baralho));
                    System.out.println("Você:   " + mostrarMao(maoJogador) + " = " + calcularPontos(maoJogador));
                } else if (escolha.equals("s") || escolha.equals("stand")) {
                    break;
                } else {
                    System.out.println("Opção inválida. Digite H ou S.");
                }
            }

            int pontosJogador = calcularPontos(maoJogador);

            if (jogadorEstourou) {
                System.out.println("Você estourou com " + pontosJogador + "! Dealer vence.");
                resultado = "DERROTA";
                variacaoSaldo = -aposta;
            } else {
                // Turno do dealer: compra até atingir 17 ou mais
                System.out.println("\nDealer revela: " + mostrarMao(maoDealer) + " = " + calcularPontos(maoDealer));
                while (calcularPontos(maoDealer) < 17) {
                    maoDealer.add(comprarCarta(baralho));
                    System.out.println("Dealer compra: " + mostrarMao(maoDealer) + " = " + calcularPontos(maoDealer));
                }

                int pontosDealer = calcularPontos(maoDealer);

                // Comparação das mãos
                if (pontosDealer > 21) {
                    System.out.println("Dealer estourou com " + pontosDealer + "! Você venceu!");
                    resultado = "VITORIA";
                    variacaoSaldo = aposta;
                } else if (pontosJogador > pontosDealer) {
                    System.out.println("Você venceu! " + pontosJogador + " x " + pontosDealer);
                    resultado = "VITORIA";
                    variacaoSaldo = aposta;
                } else if (pontosJogador < pontosDealer) {
                    System.out.println("Dealer venceu. " + pontosDealer + " x " + pontosJogador);
                    resultado = "DERROTA";
                    variacaoSaldo = -aposta;
                } else {
                    System.out.println("Empate em " + pontosJogador + ". Aposta devolvida.");
                    resultado = "EMPATE";
                    variacaoSaldo = 0;
                }
            }
        }

        // Atualiza saldo do cliente
        cliente.atualizarSaldo(variacaoSaldo);
        System.out.println("Novo saldo: " + cliente.getSaldo());

        // Registra a partida no histórico do cliente
        Partida partida = new Partida(aposta, resultado, variacaoSaldo);
        cliente.adicionarPartida(partida);
        registarPartida(); // incrementa totalPartidas em Jogo
        return partida;
    }

    // ===================== Métodos auxiliares =====================

    // Cartas: 1 = Ás, 2-10 = número, 11 = Valete, 12 = Dama, 13 = Rei
    private List<Integer> criarBaralho() {
        List<Integer> baralho = new ArrayList<>();
        for (int naipe = 0; naipe < 4; naipe++) {
            for (int valor = 1; valor <= 13; valor++) {
                baralho.add(valor);
            }
        }
        Collections.shuffle(baralho);
        return baralho;
    }

    private int comprarCarta(List<Integer> baralho) {
        return baralho.remove(baralho.size() - 1);
    }

    private int calcularPontos(List<Integer> mao) {
        int total = 0;
        int ases = 0;
        for (int carta : mao) {
            if (carta == 1) {
                ases++;
                total += 11;
            } else if (carta >= 10) {
                total += 10;
            } else {
                total += carta;
            }
        }
        // Ás vale 1 se 11 estourar a mão
        while (total > 21 && ases > 0) {
            total -= 10;
            ases--;
        }
        return total;
    }

    private String nomeCarta(int carta) {
        switch (carta) {
            case 1:  return "A";
            case 11: return "J";
            case 12: return "Q";
            case 13: return "K";
            default: return String.valueOf(carta);
        }
    }

    private String mostrarMao(List<Integer> mao) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < mao.size(); i++) {
            sb.append(nomeCarta(mao.get(i)));
            if (i < mao.size() - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}