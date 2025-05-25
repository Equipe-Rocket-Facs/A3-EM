package org.example.view;

import org.example.controller.PopulationController;
import org.example.model.PopulationModel;

import javax.swing.*;
import java.awt.*;

public class PopulationGrowthSimulatorFrame extends JFrame {
    public PopulationGrowthSimulatorFrame() {
        setTitle("Simulador de Crescimento Populacional Exponencial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        // Componentes
        InputPanel inputPanel = new InputPanel();
        ResultTablePanel tablePanel = new ResultTablePanel();
        AnalysisPanel analysisPanel = new AnalysisPanel();
        ChartPanelWrapper chartPanel = new ChartPanelWrapper();

        // Painel esquerdo
        JPanel leftPanel = new JPanel(new BorderLayout(10,10));
        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(tablePanel, BorderLayout.CENTER);
        leftPanel.add(analysisPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(chartPanel, BorderLayout.CENTER);

        // Controller
        new PopulationController(new PopulationModel(), inputPanel, tablePanel, analysisPanel, chartPanel);
    }
}
