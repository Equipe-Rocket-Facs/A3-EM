package org.example.view;

import javax.swing.*;
import java.awt.*;

public class AnalysisPanel extends JPanel {
    private final JTextArea taAnalysis = new JTextArea(3, 30);

    public AnalysisPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Análise do Limite Assintótico"));
        taAnalysis.setEditable(false);
        taAnalysis.setLineWrap(true);
        taAnalysis.setWrapStyleWord(true);
        add(new JScrollPane(taAnalysis), BorderLayout.CENTER);
    }

    public void setAnalysisText(String text) { taAnalysis.setText(text); }
    public void clear() { taAnalysis.setText(""); }
}
