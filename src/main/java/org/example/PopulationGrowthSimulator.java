package org.example;

import org.example.view.PopulationGrowthSimulatorFrame;

import javax.swing.*;

public class PopulationGrowthSimulator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception ignored){}
            new PopulationGrowthSimulatorFrame().setVisible(true);
        });
    }
}
