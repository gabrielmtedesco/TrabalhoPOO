import java.util.Random;
import java.util.Scanner;

public class Roleta extends Jogo {

    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();

    public Roleta() {
        super("Roleta");
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

        System.out.println("\n=== ROLETA ===");
        System.out.println("1 - Número (paga 36 para 1)");
        System.out.println("2 - Cor: vermelho ou preto (paga 2 para 1)");
        System.out.println("3 - Par ou ímpar (paga 2 para 1)");
        int tipo = lerInteiro("Escolha o tipo de aposta: ", 1, 3);

        int numeroEscolhido = -1;
        String corEscolhida = null;
        String paridadeEscolhida = null;

        switch (tipo) {
            case 1:
                numeroEscolhido = lerInteiro("Escolha um número (0 a 36): ", 0, 36);
                break;
            case 2:
                corEscolhida = lerOpcao("Escolha a cor (vermelho/preto): ", "vermelho", "preto");
                break;
            default:
                paridadeEscolhida = lerOpcao("Escolha (par/impar): ", "par", "impar");
                break;
        }

        // Gira a roleta: 0 a 36
        int sorteado = random.nextInt(37);
        String corSorteada = corDoNumero(sorteado);
        System.out.println("\nA bola parou no " + sorteado + " (" + corSorteada + ")");

        boolean ganhou;
        int multiplicador; // total devolvido por ficha apostada (36 para 1, 2 para 1)

        switch (tipo) {
            case 1:
                ganhou = (sorteado == numeroEscolhido);
                multiplicador = 36;
                break;
            case 2:
                ganhou = corSorteada.equals(corEscolhida);
                multiplicador = 2;
                break;
            default:
                // O zero não é par nem ímpar para fins de aposta
                if (sorteado == 0) {
                    ganhou = false;
                } else if (paridadeEscolhida.equals("par")) {
                    ganhou = (sorteado % 2 == 0);
                } else {
                    ganhou = (sorteado % 2 != 0);
                }
                multiplicador = 2;
                break;
        }

        // Variação de fichas: ganho líquido ou perda da aposta
        int variacaoSaldo;
        String resultado;
        if (ganhou) {
            variacaoSaldo = aposta * (multiplicador - 1);
            resultado = "VITORIA";
            System.out.println("Você ganhou " + variacaoSaldo + " fichas!");
        } else {
            variacaoSaldo = -aposta;
            resultado = "DERROTA";
            System.out.println("Você perdeu " + aposta + " fichas.");
        }

        // Atualiza saldo do cliente
        cliente.atualizarSaldo(variacaoSaldo);
        System.out.println("Novo saldo: " + cliente.getSaldo());

        // Registra a partida
        Partida partida = new Partida(aposta, resultado, variacaoSaldo);
        cliente.adicionarPartida(partida);
        registarPartida();
        return partida;
    }

    // ===================== Métodos auxiliares =====================

    // 0 = verde; 1-36 alternam entre vermelho (ímpares) e preto (pares)
    private String corDoNumero(int numero) {
        if (numero == 0) {
            return "verde";
        }
        return (numero % 2 != 0) ? "vermelho" : "preto";
    }

    private int lerInteiro(String mensagem, int min, int max) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(entrada);
                if (valor >= min && valor <= max) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                // cai na mensagem de erro abaixo
            }
            System.out.println("Entrada inválida. Digite um número entre " + min + " e " + max + ".");
        }
    }

    private String lerOpcao(String mensagem, String opcaoA, String opcaoB) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim().toLowerCase();
            // aceita "ímpar" com acento também
            entrada = entrada.replace("í", "i");
            if (entrada.equals(opcaoA) || entrada.equals(opcaoB)) {
                return entrada;
            }
            System.out.println("Opção inválida. Digite " + opcaoA + " ou " + opcaoB + ".");
        }
    }
}