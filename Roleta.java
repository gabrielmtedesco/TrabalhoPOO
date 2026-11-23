import java.util.Random;

public class Roleta extends Jogo {
    private Random random = new Random();
    private int ultimoNumeroSorteado = -1;
    private String ultimaCorSorteada = "";

    public Roleta() { super("Roleta"); }

    // escolha: "numero:17", "cor:vermelho", "cor:preto", "par" ou "impar".
    @Override
    public Partida jogar(Cliente cliente, int aposta, String escolha) {
        validarAposta(cliente, aposta);
        if (escolha == null) throw new IllegalArgumentException("Escolha obrigatoria.");
        String opcao = escolha.trim().toLowerCase().replace("í", "i");
        int numeroEscolhido = -1;
        int multiplicador;

        if (opcao.startsWith("numero:")) {
            try {
                numeroEscolhido = Integer.parseInt(opcao.substring(7));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Numero invalido (0 a 36).");
            }
            if (numeroEscolhido < 0 || numeroEscolhido > 36) {
                throw new IllegalArgumentException("Numero invalido (0 a 36).");
            }
            multiplicador = 36;
        } else if (opcao.equals("cor:vermelho") || opcao.equals("cor:preto")
                || opcao.equals("par") || opcao.equals("impar")) {
            multiplicador = 2;
        } else {
            throw new IllegalArgumentException("Escolha de roleta invalida.");
        }

        ultimoNumeroSorteado = random.nextInt(37);
        ultimaCorSorteada = corDoNumero(ultimoNumeroSorteado);
        boolean ganhou;
        if (numeroEscolhido >= 0) {
            ganhou = ultimoNumeroSorteado == numeroEscolhido;
        } else if (opcao.startsWith("cor:")) {
            ganhou = ultimaCorSorteada.equals(opcao.substring(4));
        } else {
            ganhou = ultimoNumeroSorteado != 0 &&
                    ((opcao.equals("par") && ultimoNumeroSorteado % 2 == 0)
                    || (opcao.equals("impar") && ultimoNumeroSorteado % 2 != 0));
        }
        long calculo = ganhou ? (long) aposta * (multiplicador - 1) : -aposta;
        if (calculo > Integer.MAX_VALUE) throw new IllegalArgumentException("Valor da aposta muito alto.");
        int variacao = (int) calculo;
        cliente.atualizarSaldo(variacao);
        Partida partida = new Partida(aposta, ganhou ? "VITORIA" : "DERROTA", variacao);
        cliente.adicionarPartida(partida);
        registrarPartida();
        return partida;
    }

    public int getUltimoNumeroSorteado() { return ultimoNumeroSorteado; }
    public String getUltimaCorSorteada() { return ultimaCorSorteada; }

    private String corDoNumero(int numero) {
        if (numero == 0) return "verde";
        int[] vermelhos = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        for (int vermelho : vermelhos) if (numero == vermelho) return "vermelho";
        return "preto";
    }
}
