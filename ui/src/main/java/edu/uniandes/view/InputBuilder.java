package edu.uniandes.view;

import edu.uniandes.exception.UserException;
import edu.uniandes.util.OrderedMap;

import javax.swing.*;
import java.awt.*;
import java.util.stream.IntStream;

public class InputBuilder {
    private final OrderedMap<String, Component> map = new OrderedMap<>();

    /** Basic-level build of input-getter by builder */
    InputBuilder append(String key, Component value) {
        map.put(key, value);
        return this;
    }

    /** Multiple-level build of input-getter by builder */
    InputBuilder appends(String[] keys, Component[] values) {
        assert keys.length == values.length;
        IntStream.range(0, keys.length).forEach(i -> InputBuilder.this.append(keys[i], values[i]));
        return this;
    }

    /** Get the panel result */
    JPanel build() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.ipadx = 5;
        gbc.ipady = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;

        return new JPanel(){{
            setLayout(new GridBagLayout());
            map.forEach((str, component) -> {
                add(new JLabel(str), gbc);
                gbc.gridx = 1;
                add(component, gbc);
                gbc.gridy++;
                gbc.gridx = 0;
            });
        }};
    }

    /** Getter of the input */
    OrderedMap<String, String> getInput() {
        OrderedMap<String, String> response = new OrderedMap<>();
        map.forEach((k,v)->{
            String str;
            if (v instanceof JTextField field) str = field.getText();
            else if (v instanceof JTextArea area) str = area.getText();
            else if (v instanceof JComboBox<?> box) str = (String) box.getSelectedItem();
            else str = v.toString();
            if (str == null || str.isBlank()) throw new UserException("CANCEL");
            response.put(k, str);
        });
        return response;
    }
}
