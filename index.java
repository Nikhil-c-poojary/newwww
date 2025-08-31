import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class LabReportGUI extends JFrame {
    private DefaultTableModel tableModel;

    public LabReportGUI() {
        setTitle("Blood Grouping — Lab Report");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Split main layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        add(splitPane);

        // LEFT: Procedure + Observations
        JTextArea procedure = new JTextArea();
        procedure.setText("Procedure:\n"
                + "1. Take the blood sample.\n"
                + "2. Place drops of Anti-A, Anti-B, and Anti-D.\n"
                + "3. Observe agglutination.\n\n"
                + "Observations:\n"
                + "- Agglutination in Anti-A → Group A.\n"
                + "- Agglutination in Anti-B → Group B.\n"
                + "- Both → Group AB.\n"
                + "- None → Group O.\n"
                + "- Agglutination in Anti-D → Rh+.");
        procedure.setEditable(false);
        procedure.setLineWrap(true);
        procedure.setWrapStyleWord(true);
        JScrollPane leftScroll = new JScrollPane(procedure);
        splitPane.setLeftComponent(leftScroll);

        // RIGHT: Table + Buttons + Graph
        JPanel rightPanel = new JPanel(new BorderLayout());
        splitPane.setRightComponent(rightPanel);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Sample", "Anti-A", "Anti-B", "Anti-D", "Result"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        // Add 3 empty rows initially
        for (int i = 1; i <= 3; i++) {
            tableModel.addRow(new Object[]{"Sample " + i, "", "", "", ""});
        }

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addRowBtn = new JButton("Add Row");
        JButton generateResultBtn = new JButton("Generate Results");
        buttonPanel.add(addRowBtn);
        buttonPanel.add(generateResultBtn);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Graph Panel
        JPanel graphPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int[] counts = new int[4]; // A, B, AB, O
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String result = (String) tableModel.getValueAt(i, 4);
                    if (result == null) continue;
                    switch (result) {
                        case "A+": case "A-": counts[0]++; break;
                        case "B+": case "B-": counts[1]++; break;
                        case "AB+": case "AB-": counts[2]++; break;
                        case "O+": case "O-": counts[3]++; break;
                    }
                }
                String[] labels = {"A", "B", "AB", "O"};
                int width = getWidth() / counts.length;
                for (int i = 0; i < counts.length; i++) {
                    int barHeight = counts[i] * 40;
                    g.setColor(Color.CYAN);
                    g.fillRect(i * width + 20, getHeight() - barHeight - 30, width - 40, barHeight);
                    g.setColor(Color.BLACK);
                    g.drawString(labels[i] + " (" + counts[i] + ")", i * width + 30, getHeight() - 10);
                }
            }
        };
        graphPanel.setPreferredSize(new Dimension(400, 200));
        rightPanel.add(graphPanel, BorderLayout.NORTH);

        // Button actions
        addRowBtn.addActionListener(e -> {
            int rowCount = tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[]{"Sample " + rowCount, "", "", "", ""});
        });

        generateResultBtn.addActionListener(e -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String a = (String) tableModel.getValueAt(i, 1);
                String b = (String) tableModel.getValueAt(i, 2);
                String d = (String) tableModel.getValueAt(i, 3);

                String result = "";
                if ("+".equals(a) && "-".equals(b)) result = d.equals("+") ? "A+" : "A-";
                else if ("-".equals(a) && "+".equals(b)) result = d.equals("+") ? "B+" : "B-";
                else if ("+".equals(a) && "+".equals(b)) result = d.equals("+") ? "AB+" : "AB-";
                else if ("-".equals(a) && "-".equals(b)) result = d.equals("+") ? "O+" : "O-";

                tableModel.setValueAt(result, i, 4);
            }
            graphPanel.repaint();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LabReportGUI().setVisible(true));
    }
}
