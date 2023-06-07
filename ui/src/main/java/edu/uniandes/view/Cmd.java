package edu.uniandes.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Cmd extends JPanel {
    private static final int COMMAND_LENGTH = 20;
    private static final int FONT_SIZE = 12;
    private final JTextField command = new JTextField(COMMAND_LENGTH);
    private final AlohandesV parent;

    {
        command.setFont(new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE));
        command.addKeyListener(new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case KeyEvent.VK_ENTER: executeToken(command.getText().trim());
                    case KeyEvent.VK_ESCAPE: command.setText(""); break;
                }
            }
        });
        command.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                command.setText("");
            }

            @Override public void focusLost(FocusEvent e) {
                command.setText("Enter command..");
            }
        });
    }

    Cmd(AlohandesV parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        add(command, BorderLayout.CENTER);
    }

    private void executeToken(String token) {
        String[] command = readToken(token);
        parent.actionPerformed(new ActionEvent(this, 0, String.join("", command)));
    }

    private static String[] readToken(String input) {
        String[] tokens = new String[2];
        Matcher matcher = Pattern.compile("(\\D+)(\\d*)").matcher(input);
        if (matcher.find()) {
            String raw = matcher.group(1).trim().toLowerCase(); //word
            tokens[0] = switch (raw) {
                case "reqf", "f", "exec" -> "req";
                case "reqc", "c", "find" -> "reqC";
                default -> raw;
            };
            try {tokens[1] = "%02d".formatted(Integer.parseInt(matcher.group(2)));} //number
            catch (NumberFormatException ignored) {tokens[1] = "";}

        }
        return tokens;
    }
}
