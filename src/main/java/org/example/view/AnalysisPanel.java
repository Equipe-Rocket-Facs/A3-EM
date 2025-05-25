package org.example.view;

import javax.swing.*;
import java.awt.*;

public class AnalysisPanel extends JPanel {
    private final JTextArea taAnalysis = new JTextArea(3, 30);

    public AnalysisPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Análise do Limite Assintótico"));
        setBackground(UIManager.getColor("Panel.background"));
        taAnalysis.setEditable(false);
        taAnalysis.setLineWrap(true);
        taAnalysis.setWrapStyleWord(true);
        taAnalysis.setBackground(UIManager.getColor("Panel.background"));
        add(new JScrollPane(taAnalysis), BorderLayout.CENTER);
    }

    public void setAnalysisText(String text) { taAnalysis.setText(text); }
    public void clear() { taAnalysis.setText(""); }
}
