package edu.uniandes.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class TextTable {
    private final List<List<Object>> matrix = new ArrayList<>();
    private int[] widths;
    private final StringBuilder out = new StringBuilder();

    private TextTable(){}

    public static TextTable builder() {return new TextTable();}

    public TextTable append(Object[] row) {
        matrix.add(Arrays.stream(row).toList());
        return this;
    }

    public TextTable appends(Object[][] rows) {
        Arrays.stream(rows).forEach(this::append);
        return this;
    }

    @Override public String toString() {
        build();
        return out.toString();
    }

    private void build() {
        calculateWidths();
        printHorizontalLine();
        printRow(0);
        printHorizontalLine();
        IntStream.range(1, matrix.size()).forEach(this::printRow);
        if (matrix.size() > 1) printHorizontalLine();
    }

    private void calculateWidths() {
        int cols = matrix.get(0).size();
        widths = new int[cols];
        for (int j = 0; j < cols; j++) {
            int max = 0;
            for (List<Object> objects : matrix)
                if (objects.get(j).toString().length() > max) max = objects.get(j).toString().length();
            widths[j] = max;
        }
    }

    private void printHorizontalLine() {
        out.append("+");
        Arrays.stream(widths).forEach(width -> {
            out.append("-".repeat(Math.max(0, width + 2)));
            out.append("+");
        });
        out.append("\n");
    }

    private void printRow(int rowIndex) {
        List<Object> row = matrix.get(rowIndex);
        out.append("|");
        IntStream.range(0, row.size()).forEach(i -> {
            String value = row.get(i).toString();
            out.append(" ").append(value).append(" ".repeat(Math.max(0, widths[i]) - value.length())).append(" |");
        });
        out.append("\n");
    }
}