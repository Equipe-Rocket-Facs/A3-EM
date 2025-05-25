package org.example;

import com.formdev.flatlaf.FlatIntelliJLaf;
import org.example.view.PopulationGrowthSimulatorFrame;

import javax.swing.*;
import java.awt.*;

public class PopulationGrowthSimulator {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
            UIManager.put("Panel.background", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new PopulationGrowthSimulatorFrame().setVisible(true);
        });
    }
}
