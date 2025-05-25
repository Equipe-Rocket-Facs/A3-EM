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
    private final JButton btnHelp = new JButton("Ajuda");

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
        gbc.gridx = 2; gbc.gridy = y; add(btnHelp, gbc);
    }

    private void addRow(GridBagConstraints gbc, int y, String label, JTextField field, String tooltip) {
        gbc.gridx = 0; gbc.gridy = y; add(new JLabel(label), gbc);
        gbc.gridx = 1; field.setToolTipText(tooltip); add(field, gbc);
    }

    public void setSimulateAction(Runnable action) { btnSimulate.addActionListener(e -> action.run()); }
    public void setResetAction(Runnable action) { btnReset.addActionListener(e -> action.run()); }
    public void setHelpAction(Runnable action) { btnHelp.addActionListener(e -> action.run());}

    public void showHelpDialog() {
        JOptionPane.showMessageDialog(
                this, // ou outro componente pai, se preferir
                """
                🛈 Instruções de uso do Simulador de Crescimento Populacional
                
                ① Preencha TODOS os campos:
                - População Inicial (P₀): número positivo (ex: 1000).
                - Taxa de Crescimento (r): entre -1.0 e 1.0 (ex: 0.03 para 3% ao ano).
                - Tempo Final (T): número positivo (> 0).
                - Intervalo de Tempo (Δt): número positivo menor que T.
        
                ② Clique em 'Simular' para ver o gráfico, tabela e análise.
        
                ③ Clique em 'Resetar' para limpar tudo.
        
                Interpretação:
                - Linha azul: população ao longo do tempo.
                - Linha vermelha: taxa de crescimento instantânea.
                - A análise mostra o que acontece com a população no longo prazo.
        
                Dica: valores negativos de r simulam decaimento populacional.
                """,
                "Ajuda e Instruções",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

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
