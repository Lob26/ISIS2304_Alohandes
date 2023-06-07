package edu.uniandes.view.input;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.stream.IntStream;

public final class TimePicker extends JPanel {
    private final JComboBox<String> hour = new JComboBox<>(
            IntStream.range(0, 24).mapToObj(i -> String.format("%02d", i)).toArray(String[]::new));
    private final JComboBox<String> minute = new JComboBox<>(
            IntStream.range(0, 4).map(i -> i * 15).mapToObj(i -> String.format("%02d", i)).toArray(String[]::new));

    {
        setLayout(new GridLayout(2, 2, 4, 2));
        add(new JLabel("HH:"));
        add(hour);
        add(new JLabel("mm:"));
        add(minute);
    }

    @Override public String toString() {return hour.getSelectedItem() + ":" + minute.getSelectedItem();}
}
