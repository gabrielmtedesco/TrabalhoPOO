import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Inicializa os dados e abre a janela principal do cassino. */
public class Main {
    public static void main(String[] args) {
        CasinoService cassino = new CasinoService();
        ArquivoService arquivos = new ArquivoService(cassino, "dados");

        try {
            arquivos.inicializar();
            SwingUtilities.invokeLater(() -> {
                JanelaPrincipal janela = new JanelaPrincipal(cassino, arquivos);
                janela.setVisible(true);
            });
        } catch (IOException | IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(null,
                    "Não foi possível iniciar o cassino: " + e.getMessage(),
                    "Erro de inicialização", JOptionPane.ERROR_MESSAGE);
        }
    }
}
