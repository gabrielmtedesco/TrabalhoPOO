import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JComponent;

/** Cores e componentes simples compartilhados entre as janelas. */
public class Estilo {
    public static final Color FUNDO = new Color(20, 29, 29);
    public static final Color PAINEL = new Color(33, 47, 44);
    public static final Color OURO = new Color(223, 186, 106);
    public static final Color TEXTO = new Color(244, 242, 232);
    public static final Color SUAVE = new Color(184, 199, 193);

    public static JPanel painel() {
        JPanel p = new JPanel();
        p.setBackground(FUNDO);
        p.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        return p;
    }
    public static JLabel titulo(String valor) {
        JLabel label = new JLabel(valor);
        label.setForeground(OURO);
        label.setFont(new Font("SansSerif", Font.BOLD, 25));
        return label;
    }
    public static JLabel texto(String valor) {
        JLabel label = new JLabel(valor);
        label.setForeground(TEXTO);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return label;
    }
    public static JButton botao(String valor) {
        JButton b = new JButton(valor);
        b.setBackground(PAINEL);
        b.setForeground(TEXTO);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(OURO),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        return b;
    }
    public static JTextField campo(int colunas) {
        JTextField t = new JTextField(colunas);
        t.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return t;
    }
    public static JTextArea area() {
        JTextArea a = new JTextArea();
        a.setEditable(false);
        a.setFont(new Font("Monospaced", Font.PLAIN, 13));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBackground(PAINEL);
        a.setForeground(TEXTO);
        a.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return a;
    }
    public static JScrollPane rolagem(JTextArea area) {
        JScrollPane rolagem = new JScrollPane(area);
        rolagem.setBorder(BorderFactory.createLineBorder(OURO));
        return rolagem;
    }
    public static void fundo(JComponent componente) { componente.setBackground(FUNDO); }
}
