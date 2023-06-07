package edu.uniandes.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Objects;

class Console extends JPanel {
    private static final TitledBorder TITLE = new TitledBorder("Console for AlohandesB");
    private final JTextArea console = new JTextArea("Resultado de las operaciones solicitadas");
    private Boolean status;
    private static final int FONT_SIZE = 12;

    {
        console.setEditable(false);
        console.setLineWrap(true);
        console.setFont(new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE));
    }

    Console() {
        status = null;
        setBorder(border());
        setLayout(new BorderLayout());
        add(new JScrollPane(console), BorderLayout.CENTER);
    }

    private Border border() {
        var inner = BorderFactory.createLineBorder(UIManager.getColor("edu.uniandes.console.status." + status), 5);
        return BorderFactory.createCompoundBorder(TITLE, inner);
    }

    void print(String str, Boolean b) {
        Boolean prev = status;
        status = b;
        setBorder(border());

        if (Objects.equals(prev, status)) {
            console.append(">>\n");
            console.append(str);
            console.append("\n");
            console.setCaretPosition(console.getDocument().getLength());
        } else console.setText(str);
    }

    void reset() {
        setBorder(border());
    }

    void clear() {
        console.setText("");
    }

}
