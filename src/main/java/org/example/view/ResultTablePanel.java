package org.example.view;

import javax.swing.*;
import java.awt.*;

public class ResultTablePanel extends JPanel {
    private final ResultTableModel tableModel = new ResultTableModel();
    private final JTable table = new JTable(tableModel);

    public ResultTablePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Tabela de Resultados"));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(400, 300));
        add(scroll, BorderLayout.CENTER);
    }

    public ResultTableModel getModel() { return tableModel; }
}
