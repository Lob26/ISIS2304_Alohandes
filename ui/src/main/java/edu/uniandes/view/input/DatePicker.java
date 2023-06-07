package edu.uniandes.view.input;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.text.SimpleDateFormat;

public final class DatePicker extends JPanel {
    private static final String PATTERN = "yyyy-MM-dd";
    private final JDateChooser date; //Let's not re-create the wheel

    {
        JTextFieldDateEditor field = new JTextFieldDateEditor(true, PATTERN, "####-##-##", '_');
        date = new JDateChooser(null, PATTERN, field);
        date.setPreferredSize(new Dimension(100, 20));

        add(new JLabel(PATTERN));
        add(date);
    }

    @Override public String toString() {return new SimpleDateFormat(PATTERN).format(date.getDate());}
}