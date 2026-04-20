package com.example.decathlon.gui;

import com.example.decathlon.core.CompetitionService;
import com.example.decathlon.core.ScoringService;
import com.example.decathlon.excel.ExcelPrinter;
import com.example.decathlon.excel.ExcelReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainGUI {

    private static class EventDef {
        private final String id;
        private final String label;
        private final double min;
        private final double max;

        private EventDef(String id, String label, double min, double max) {
            this.id = id;
            this.label = label;
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private enum Mode {
        DECATHLON("Decathlon"),
        HEPTATHLON("Heptathlon");

        private final String apiName;

        Mode(String apiName) {
            this.apiName = apiName;
        }

        public String apiName() {
            return apiName;
        }
    }

    private static final EventDef[] DECATHLON_EVENTS = {
            new EventDef("100m", "100m (s)", 5, 20),
            new EventDef("longJump", "Long Jump (cm)", 0, 1000),
            new EventDef("shotPut", "Shot Put (m)", 0, 30),
            new EventDef("highJump", "High Jump (cm)", 0, 300),
            new EventDef("400m", "400m (s)", 20, 100),
            new EventDef("110mHurdles", "110m Hurdles (s)", 10, 30),
            new EventDef("discusThrow", "Discus Throw (m)", 0, 85),
            new EventDef("poleVault", "Pole Vault (cm)", 0, 1000),
            new EventDef("javelinThrow", "Javelin Throw (m)", 0, 110),
            new EventDef("1500m", "1500m (s)", 150, 400)
    };

    private static final EventDef[] HEPTATHLON_EVENTS = {
            new EventDef("100mHurdles", "100m Hurdles (s)", 10, 30),
            new EventDef("highJump", "High Jump (cm)", 0, 300),
            new EventDef("shotPut", "Shot Put (m)", 0, 30),
            new EventDef("200m", "200m (s)", 20, 100),
            new EventDef("longJump", "Long Jump (cm)", 0, 1000),
            new EventDef("javelinThrow", "Javelin Throw (m)", 0, 110),
            new EventDef("800m", "800m (s)", 70, 250)
    };

    private final CompetitionService competitionService = new CompetitionService(new ScoringService());

    private JFrame frame;
    private JTextField nameField;
    private JTextField resultField;
    private JComboBox<EventDef> disciplineComboBox;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JRadioButton decathlonButton;
    private JRadioButton heptathlonButton;
    private Mode currentMode = Mode.DECATHLON;

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

        decathlonButton.addActionListener(e -> switchMode(Mode.DECATHLON));
        heptathlonButton.addActionListener(e -> switchMode(Mode.HEPTATHLON));

        nameField = new JTextField(15);
        resultField = new JTextField(10);
        disciplineComboBox = new JComboBox<>();

        JButton addButton = new JButton("Add competitor");
        addButton.addActionListener(e -> addCompetitor());

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
        topPanel.add(nameField, gbc);

        gbc.gridx = 2;
        topPanel.add(addButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
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

        rebuildModeView();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void switchMode(Mode mode) {
        currentMode = mode;
        rebuildModeView();
    }

    private void rebuildModeView() {
        updateDisciplineList();
        rebuildTableModel();
        updateResultTable();
    }

    private EventDef[] getCurrentEvents() {
        return currentMode == Mode.DECATHLON ? DECATHLON_EVENTS : HEPTATHLON_EVENTS;
    }

    private void updateDisciplineList() {
        disciplineComboBox.removeAllItems();
        for (EventDef event : getCurrentEvents()) {
            disciplineComboBox.addItem(event);
        }
    }

    private String[] getCurrentColumns() {
        EventDef[] events = getCurrentEvents();
        String[] columns = new String[events.length + 2];
        columns[0] = "Name";
        for (int i = 0; i < events.length; i++) {
            columns[i + 1] = events[i].id;
        }
        columns[columns.length - 1] = "Total";
        return columns;
    }

    private void rebuildTableModel() {
        tableModel.setDataVector(new Object[][]{}, getCurrentColumns());
        resultTable.setModel(tableModel);
    }

    private void addCompetitor() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Name is required");
            return;
        }

        competitionService.addCompetitor(name, currentMode.apiName());
        updateResultTable();
    }

    private void saveResult() {
        String name = nameField.getText().trim();
        String resultText = resultField.getText().trim();
        EventDef event = (EventDef) disciplineComboBox.getSelectedItem();

        //Om resultat innehåller kommatecken, ersätt med punkt
        if(resultText.contains(",")){
            resultText = resultText.replace(',', '.');

        }

        if (name.isEmpty() || resultText.isEmpty() || event == null) {
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

        if (rawValue < 0) {
            JOptionPane.showMessageDialog(frame, "Result cannot be negative.");
            return;
        }

        if (rawValue < event.min || rawValue > event.max) {
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

        try {
            competitionService.score(name, currentMode.apiName(), event.id, rawValue);
            updateResultTable();
            resultField.setText("");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void updateResultTable() {
        tableModel.setRowCount(0);

        Map<String, Object> standings = competitionService.standings();
        String key = currentMode == Mode.DECATHLON ? "decathlon" : "heptathlon";
        List<Map<String, Object>> group = (List<Map<String, Object>>) standings.get(key);

        if (group == null) {
            return;
        }

        EventDef[] events = getCurrentEvents();

        for (Map<String, Object> standing : group) {
            List<Object> row = new ArrayList<>();
            row.add(standing.get("name"));

            Map<String, Integer> scores = (Map<String, Integer>) standing.get("scores");
            int total = 0;

            for (EventDef event : events) {
                Integer points = scores.get(event.id);
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

        String[] header = new String[tableModel.getColumnCount() + 1];
        header[0] = "MultiEventType";
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            header[i + 1] = tableModel.getColumnName(i);
        }
        rows.add(header);

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String[] values = new String[tableModel.getColumnCount() + 1];
            values[0] = currentMode.apiName();
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                Object value = tableModel.getValueAt(row, col);
                values[col + 1] = value == null ? "" : value.toString();
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

            String[] header = rows.get(0);
            if (header.length < 3 || !"MultiEventType".equals(header[0]) || !"Name".equals(header[1])) {
                JOptionPane.showMessageDialog(frame, "Import failed.");
                return;
            }

            String importedType = rows.size() > 1 ? getCell(rows.get(1), 0).trim() : "";
            if ("Decathlon".equalsIgnoreCase(importedType)) {
                currentMode = Mode.DECATHLON;
                decathlonButton.setSelected(true);
            } else if ("Heptathlon".equalsIgnoreCase(importedType)) {
                currentMode = Mode.HEPTATHLON;
                heptathlonButton.setSelected(true);
            } else {
                JOptionPane.showMessageDialog(frame, "Import failed.");
                return;
            }

            competitionService.clear();
            rebuildModeView();

            EventDef[] events = getCurrentEvents();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                String multiEventType = getCell(row, 0).trim();
                String name = getCell(row, 1).trim();

                if (name.isEmpty() || multiEventType.isEmpty()) {
                    continue;
                }

                competitionService.addCompetitor(name, multiEventType);

                for (int col = 0; col < events.length; col++) {
                    String valueText = getCell(row, col + 2).trim();
                    if (valueText.isEmpty()) {
                        continue;
                    }

                    try {
                        int points = Integer.parseInt(valueText);
                        competitionService.setPoints(name, multiEventType, events[col].id, points);
                    } catch (NumberFormatException ignored) {
                    }
                }
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
}