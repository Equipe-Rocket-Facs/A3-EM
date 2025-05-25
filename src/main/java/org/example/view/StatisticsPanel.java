package org.example.view;

import javax.swing.*;
import java.awt.*;

public class StatisticsPanel extends JPanel {
    private final JLabel meanLabel = new JLabel("Média: ");
    private final JLabel medianLabel = new JLabel("Mediana: ");
    private final JLabel stddevLabel = new JLabel("Desvio padrão: ");

    public StatisticsPanel() {
        setLayout(new GridLayout(3,1));
        setBorder(BorderFactory.createTitledBorder("Estatísticas Populacionais"));
        add(meanLabel); add(medianLabel); add(stddevLabel);
    }
    public void setStatistics(double mean, double median, double stddev) {
        meanLabel.setText("Média: " + String.format("%.2f", mean));
        medianLabel.setText("Mediana: " + String.format("%.2f", median));
        stddevLabel.setText("Desvio padrão: " + String.format("%.2f", stddev));
    }
    public void clear() {
        meanLabel.setText("Média: ");
        medianLabel.setText("Mediana: ");
        stddevLabel.setText("Desvio padrão: ");
    }
}
