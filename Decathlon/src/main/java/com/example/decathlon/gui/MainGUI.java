package com.example.decathlon.gui;

import com.example.decathlon.common.SelectDiscipline;
import com.example.decathlon.excel.ExcelPrinter;
import com.example.decathlon.excel.ExcelReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainGUI {

    private JFrame frame;
    private JTextField nameField;
    private JTextField resultField;
    private JComboBox<String> disciplineComboBox;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JRadioButton decathlonButton;
    private JRadioButton heptathlonButton;

    private final SelectDiscipline selectDiscipline = new SelectDiscipline();
    private final Map<String, LinkedHashMap<String, Integer>> resultMap = new LinkedHashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Decathlon");
        frame.setSize(1100, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        decathlonButton = new JRadioButton("Decathlon", true);
        heptathlonButton = new JRadioButton("Heptathlon");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(decathlonButton);
        buttonGroup.add(heptathlonButton);

        decathlonButton.addActionListener(e -> switchMode());
        heptathlonButton.addActionListener(e -> switchMode());

        nameField = new JTextField(15);
        resultField = new JTextField(10);
        disciplineComboBox = new JComboBox<>();

        updateDisciplineList();

        JButton calculateButton = new JButton("Calculate score");
        calculateButton.addActionListener(e -> saveResult());

        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(e -> exportResults());

        JButton importButton = new JButton("Import");
        importButton.addActionListener(e -> importResults());

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Mode:"), gbc);

        gbc.gridx = 1;
        topPanel.add(decathlonButton, gbc);

        gbc.gridx = 2;
        topPanel.add(heptathlonButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        topPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        topPanel.add(new JLabel("Discipline:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        topPanel.add(disciplineComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        topPanel.add(new JLabel("Result:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        topPanel.add(resultField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(calculateButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        topPanel.add(buttonPanel, gbc);

        frame.add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(getCurrentColumns(), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void switchMode() {
        updateDisciplineList();
        rebuildTableModel();
        updateResultTable();
    }

    private void updateDisciplineList() {
        disciplineComboBox.removeAllItems();
        String[] disciplines = decathlonButton.isSelected()
                ? selectDiscipline.getDecathlonDisciplines()
                : selectDiscipline.getHeptathlonDisciplines();

        for (String discipline : disciplines) {
            disciplineComboBox.addItem(discipline);
        }
    }

    private String[] getCurrentColumns() {
        if (decathlonButton.isSelected()) {
            return new String[]{
                    "Name",
                    "100m",
                    "Long Jump",
                    "Shot Put",
                    "High Jump",
                    "400m",
                    "110m Hurdles",
                    "Discus",
                    "Pole Vault",
                    "Javelin",
                    "1500m",
                    "Total"
            };
        }

        return new String[]{
                "Name",
                "100m Hurdles",
                "High Jump",
                "Shot Put",
                "200m",
                "Long Jump",
                "Javelin",
                "800m",
                "Total"
        };
    }

    private void rebuildTableModel() {
        tableModel.setDataVector(new Object[][]{}, getCurrentColumns());
        resultTable.setModel(tableModel);
    }

    private void saveResult() {
        String name = nameField.getText().trim();
        String selectedDiscipline = (String) disciplineComboBox.getSelectedItem();
        String resultText = resultField.getText().trim();

        if (name.isEmpty() || selectedDiscipline == null || resultText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
            return;
        }

        double rawValue;
        try {
            rawValue = Double.parseDouble(resultText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid result value.");
            return;
        }

        Range range = getRange(selectedDiscipline);
        if (rawValue < range.min || rawValue > range.max) {
            int choice = JOptionPane.showConfirmDialog(
                    frame,
                    "The value is outside the normal range.\nDo you want to use the result anyway?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        String eventName = normalizeDisciplineName(selectedDiscipline);
        int points = calculatePoints(selectedDiscipline, rawValue);

        resultMap.putIfAbsent(name, new LinkedHashMap<>());
        resultMap.get(name).put(eventName, points);

        updateResultTable();
        resultField.setText("");
    }

    private void updateResultTable() {
        tableModel.setRowCount(0);

        for (Map.Entry<String, LinkedHashMap<String, Integer>> entry : resultMap.entrySet()) {
            String name = entry.getKey();
            LinkedHashMap<String, Integer> events = entry.getValue();

            List<Object> row = new ArrayList<>();
            row.add(name);

            int total = 0;

            for (int i = 1; i < tableModel.getColumnCount() - 1; i++) {
                String eventName = tableModel.getColumnName(i);
                Integer points = events.get(eventName);

                if (points == null) {
                    row.add("");
                } else {
                    row.add(points);
                    total += points;
                }
            }

            row.add(total);
            tableModel.addRow(row.toArray());
        }
    }

    private void exportResults() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(frame);

        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();
        String filename = file.getAbsolutePath();
        if (!filename.toLowerCase().endsWith(".xlsx")) {
            filename += ".xlsx";
        }

        List<String[]> rows = new ArrayList<>();

        String[] header = new String[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            header[i] = tableModel.getColumnName(i);
        }
        rows.add(header);

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String[] values = new String[tableModel.getColumnCount()];
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                Object value = tableModel.getValueAt(row, col);
                values[col] = value == null ? "" : value.toString();
            }
            rows.add(values);
        }

        try {
            ExcelPrinter printer = new ExcelPrinter();
            printer.print(rows, filename);
            JOptionPane.showMessageDialog(frame, "Export completed.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Export failed.");
        }
    }

    private void importResults() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(frame);

        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();

        try {
            ExcelReader reader = new ExcelReader();
            List<String[]> rows = reader.read(file.getAbsolutePath());

            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Import failed.");
                return;
            }

            String[] importedHeader = rows.get(0);

            if (isDecathlonHeader(importedHeader)) {
                decathlonButton.setSelected(true);
            } else if (isHeptathlonHeader(importedHeader)) {
                heptathlonButton.setSelected(true);
            }

            updateDisciplineList();
            rebuildTableModel();

            resultMap.clear();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length == 0) {
                    continue;
                }

                String name = getCell(row, 0).trim();
                if (name.isEmpty()) {
                    continue;
                }

                LinkedHashMap<String, Integer> events = new LinkedHashMap<>();

                for (int col = 1; col < tableModel.getColumnCount() - 1; col++) {
                    String eventName = tableModel.getColumnName(col);
                    String valueText = getCell(row, col).trim();

                    if (!valueText.isEmpty()) {
                        try {
                            events.put(eventName, Integer.parseInt(valueText));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }

                resultMap.put(name, events);
            }

            updateResultTable();
            JOptionPane.showMessageDialog(frame, "Import completed.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Import failed.");
        }
    }

    private String getCell(String[] row, int index) {
        if (index >= row.length || row[index] == null) {
            return "";
        }
        return row[index];
    }

    private boolean isDecathlonHeader(String[] header) {
        String[] decathlonColumns = {
                "Name",
                "100m",
                "Long Jump",
                "Shot Put",
                "High Jump",
                "400m",
                "110m Hurdles",
                "Discus",
                "Pole Vault",
                "Javelin",
                "1500m",
                "Total"
        };

        if (header.length < decathlonColumns.length) {
            return false;
        }

        for (int i = 0; i < decathlonColumns.length; i++) {
            if (!decathlonColumns[i].equals(header[i])) {
                return false;
            }
        }

        return true;
    }

    private boolean isHeptathlonHeader(String[] header) {
        String[] heptathlonColumns = {
                "Name",
                "100m Hurdles",
                "High Jump",
                "Shot Put",
                "200m",
                "Long Jump",
                "Javelin",
                "800m",
                "Total"
        };

        if (header.length < heptathlonColumns.length) {
            return false;
        }

        for (int i = 0; i < heptathlonColumns.length; i++) {
            if (!heptathlonColumns[i].equals(header[i])) {
                return false;
            }
        }

        return true;
    }

    private String normalizeDisciplineName(String discipline) {
        int index = discipline.indexOf(" (");
        if (index >= 0) {
            return discipline.substring(0, index);
        }
        return discipline;
    }

    private int calculatePoints(String discipline, double value) {
        switch (discipline) {
            case "100m (s)":
                return trackScore(25.4347, 18, 1.81, value);
            case "Long Jump (cm)":
                if (decathlonButton.isSelected()) {
                    return fieldScore(0.14354, 220, 1.4, value);
                }
                return fieldScore(0.188807, 210, 1.41, value);
            case "Shot Put (m)":
                if (decathlonButton.isSelected()) {
                    return fieldScore(51.39, 1.5, 1.05, value);
                }
                return fieldScore(56.0211, 1.5, 1.05, value);
            case "High Jump (cm)":
                if (decathlonButton.isSelected()) {
                    return fieldScore(0.8465, 75, 1.42, value);
                }
                return fieldScore(1.84523, 75, 1.348, value);
            case "400m (s)":
                return trackScore(1.53775, 82, 1.81, value);
            case "110m Hurdles (s)":
                return trackScore(5.74352, 28.5, 1.92, value);
            case "Discus (m)":
                return fieldScore(12.91, 4, 1.1, value);
            case "Pole Vault (cm)":
                return fieldScore(0.2797, 100, 1.35, value);
            case "Javelin (m)":
                if (decathlonButton.isSelected()) {
                    return fieldScore(10.14, 7, 1.08, value);
                }
                return fieldScore(15.9803, 3.8, 1.04, value);
            case "1500m (s)":
                return trackScore(0.03768, 480, 1.85, value);
            case "100m Hurdles (s)":
                return trackScore(9.23076, 26.7, 1.835, value);
            case "200m (s)":
                return trackScore(4.99087, 42.5, 1.81, value);
            case "800m (s)":
                return trackScore(0.11193, 254, 1.88, value);
            default:
                return 0;
        }
    }

    private int trackScore(double a, double b, double c, double time) {
        double scoreValue = b - time;
        if (scoreValue < 0) {
            scoreValue = 0;
        }
        return (int) (a * Math.pow(scoreValue, c));
    }

    private int fieldScore(double a, double b, double c, double distance) {
        double scoreValue = distance - b;
        if (scoreValue < 0) {
            scoreValue = 0;
        }
        return (int) (a * Math.pow(scoreValue, c));
    }

    private Range getRange(String discipline) {
        switch (discipline) {
            case "100m (s)":
                return new Range(5, 17.8);
            case "Long Jump (cm)":
                if (decathlonButton.isSelected()) {
                    return new Range(0, 900);
                }
                return new Range(0, 800);
            case "Shot Put (m)":
                if (decathlonButton.isSelected()) {
                    return new Range(0, 30);
                }
                return new Range(0, 25);
            case "High Jump (cm)":
                if (decathlonButton.isSelected()) {
                    return new Range(0, 100);
                }
                return new Range(0, 250);
            case "400m (s)":
                return new Range(20, 100);
            case "110m Hurdles (s)":
                return new Range(10, 28.5);
            case "Discus (m)":
                return new Range(0, 85);
            case "Pole Vault (cm)":
                return new Range(0, 700);
            case "Javelin (m)":
                if (decathlonButton.isSelected()) {
                    return new Range(0, 120);
                }
                return new Range(0, 100);
            case "1500m (s)":
                return new Range(2, 480);
            case "100m Hurdles (s)":
                return new Range(10, 30);
            case "200m (s)":
                return new Range(15, 60);
            case "800m (s)":
                return new Range(60, 500);
            default:
                return new Range(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
    }

    private static class Range {
        private final double min;
        private final double max;

        private Range(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }
}