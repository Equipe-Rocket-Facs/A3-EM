package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Painel de entrada de dados.
 */
public class InputPanel extends JPanel {
    private final JTextField tfInitialPopulation = new JTextField(10);
    private final JTextField tfGrowthRate = new JTextField(10);
    private final JTextField tfFinalTime = new JTextField(10);
    private final JTextField tfDeltaTime = new JTextField(10);
    private final JButton btnSimulate = new JButton("Simular");
    private final JButton btnReset = new JButton("Resetar");

    public InputPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Parâmetros de Entrada"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        addRow(gbc, y++, "População Inicial (P₀):", tfInitialPopulation, "Digite a população inicial (>0)");
        addRow(gbc, y++, "Taxa de Crescimento (r):", tfGrowthRate, "Entre -1.0 e 1.0 (ex: 0.03)");
        addRow(gbc, y++, "Tempo Final (T):", tfFinalTime, "Tempo final (>0)");
        addRow(gbc, y++, "Intervalo de Tempo (Δt):", tfDeltaTime, "Intervalo (>0 e <T)");

        gbc.gridx = 0; gbc.gridy = y; add(btnSimulate, gbc);
        gbc.gridx = 1; add(btnReset, gbc);
    }

    private void addRow(GridBagConstraints gbc, int y, String label, JTextField field, String tooltip) {
        gbc.gridx = 0; gbc.gridy = y; add(new JLabel(label), gbc);
        gbc.gridx = 1; field.setToolTipText(tooltip); add(field, gbc);
    }

    public void setSimulateAction(Runnable action) { btnSimulate.addActionListener(e -> action.run()); }
    public void setResetAction(Runnable action) { btnReset.addActionListener(e -> action.run()); }

    public String getInitialPopulation() { return tfInitialPopulation.getText(); }
    public String getGrowthRate() { return tfGrowthRate.getText(); }
    public String getFinalTime() { return tfFinalTime.getText(); }
    public String getDeltaTime() { return tfDeltaTime.getText(); }

    public void clearFields() {
        tfInitialPopulation.setText("");
        tfGrowthRate.setText("");
        tfFinalTime.setText("");
        tfDeltaTime.setText("");
    }
}
