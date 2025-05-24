package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Simulador de crescimento populacional exponencial com cálculo de derivadas e gráfico.
 * Usa Java Swing para interface e JFreeChart para gráficos.
 */
public class PopulationGrowthSimulator extends JFrame {

    private JTextField tfInitialPopulation;
    private JTextField tfGrowthRate;
    private JTextField tfFinalTime;
    private JTextField tfDeltaTime;

    private JButton btnSimulate;
    private JButton btnReset;

    private JTextArea taLimitAnalysis;
    private JTable resultTable;
    private ResultTableModel tableModel;

    private ChartPanel chartPanel;

    private PopulationModel populationModel;

    public PopulationGrowthSimulator() {
        setTitle("Simulador de Crescimento Populacional Exponencial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        populationModel = new PopulationModel();

        initUI();
    }

    private void initUI() {
        // Painel de entrada
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblInitialPopulation = new JLabel("População Inicial (P₀):");
        tfInitialPopulation = new JTextField(10);
        tfInitialPopulation.setToolTipText("Ex.: 1000");

        JLabel lblGrowthRate = new JLabel("Taxa de Crescimento (r):");
        tfGrowthRate = new JTextField(10);
        tfGrowthRate.setToolTipText("Ex.: 0.03 (para crescimento) ou -0.02 (para decaimento)");

        JLabel lblFinalTime = new JLabel("Tempo Final da Simulação (T):");
        tfFinalTime = new JTextField(10);
        tfFinalTime.setToolTipText("Ex.: 50");

        JLabel lblDeltaTime = new JLabel("Intervalo de Tempo (Δt):");
        tfDeltaTime = new JTextField(10);
        tfDeltaTime.setToolTipText("Ex.: 1");

        btnSimulate = new JButton("Simular");
        btnReset = new JButton("Resetar");

        // Layout entrada com GridBag
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(lblInitialPopulation, gbc);
        gbc.gridx = 1;
        inputPanel.add(tfInitialPopulation, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(lblGrowthRate, gbc);
        gbc.gridx = 1;
        inputPanel.add(tfGrowthRate, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(lblFinalTime, gbc);
        gbc.gridx = 1;
        inputPanel.add(tfFinalTime, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(lblDeltaTime, gbc);
        gbc.gridx = 1;
        inputPanel.add(tfDeltaTime, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(btnSimulate, gbc);
        gbc.gridx = 1;
        inputPanel.add(btnReset, gbc);

        // Área texto para análise do limite
        taLimitAnalysis = new JTextArea(3, 30);
        taLimitAnalysis.setEditable(false);
        taLimitAnalysis.setLineWrap(true);
        taLimitAnalysis.setWrapStyleWord(true);
        JScrollPane scrollLimit = new JScrollPane(taLimitAnalysis);
        scrollLimit.setBorder(BorderFactory.createTitledBorder("Análise do Limite Assintótico"));

        // Tabela para dados tabulares
        tableModel = new ResultTableModel();
        resultTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setPreferredSize(new Dimension(400, 300));
        tableScroll.setBorder(BorderFactory.createTitledBorder("Tabela de Resultados"));

        // Painel esquerdo contendo entrada, tabela e limite
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout(10,10));
        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(tableScroll, BorderLayout.CENTER);
        leftPanel.add(scrollLimit, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        // Painel para gráfico
        chartPanel = new ChartPanel(null);
        chartPanel.setPreferredSize(new Dimension(550, 650));
        chartPanel.setBorder(BorderFactory.createTitledBorder("Gráfico de Crescimento Populacional e Taxa"));
        add(chartPanel, BorderLayout.CENTER);

        // Ações dos botões
        btnSimulate.addActionListener(e -> onSimulate());
        btnReset.addActionListener(e -> onReset());
    }

    private void onSimulate() {
        // Validar entradas
        double P0, r, T, deltaT;
        try {
            P0 = Double.parseDouble(tfInitialPopulation.getText());
            r = Double.parseDouble(tfGrowthRate.getText());
            T = Double.parseDouble(tfFinalTime.getText());
            deltaT = Double.parseDouble(tfDeltaTime.getText());

            if (P0 <= 0) {
                showError("População inicial deve ser maior que zero.");
                return;
            }
            if (deltaT <= 0) {
                showError("Intervalo de tempo (Δt) deve ser maior que zero.");
                return;
            }
            if (T <= 0) {
                showError("Tempo final (T) deve ser maior que zero.");
                return;
            }
            if (deltaT > T) {
                showError("Intervalo de tempo (Δt) não pode ser maior que o tempo final (T).");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Por favor, insira valores numéricos válidos.");
            return;
        }

        // Calcular dados
        populationModel.calculatePopulationData(P0, r, T, deltaT);

        // Atualizar tabela
        tableModel.setData(populationModel.getTimePoints(), populationModel.getPopulationValues(), populationModel.getGrowthRates());

        // Atualizar análise do limite
        String limitMessage = populationModel.analyzeLimit();
        taLimitAnalysis.setText(limitMessage);

        // Atualizar gráfico
        updateChart();
    }

    private void onReset() {
        tfInitialPopulation.setText("");
        tfGrowthRate.setText("");
        tfFinalTime.setText("");
        tfDeltaTime.setText("");
        taLimitAnalysis.setText("");
        tableModel.clearData();
        chartPanel.setChart(null);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro de entrada", JOptionPane.ERROR_MESSAGE);
    }

    private void updateChart() {
        XYSeries populationSeries = new XYSeries("População P(t)");
        XYSeries growthRateSeries = new XYSeries("Taxa de Crescimento P'(t)");

        List<Double> times = populationModel.getTimePoints();
        List<Double> populations = populationModel.getPopulationValues();
        List<Double> growths = populationModel.getGrowthRates();

        for (int i = 0; i < times.size(); i++) {
            populationSeries.add(times.get(i), populations.get(i));
            growthRateSeries.add(times.get(i), growths.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(populationSeries);
        dataset.addSeries(growthRateSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Crescimento Populacional e Taxa de Crescimento",
                "Tempo (t)",
                "Valor",
                dataset
        );

        // Custom renderer para cores e formas diferentes
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // População em azul sólido com pontos
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesFilled(0, true);

        // Taxa de crescimento em vermelho tracejado sem pontos
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesStroke(1, new BasicStroke(
                2.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                0,
                new float[]{6.0f, 6.0f},
                0
        ));
        renderer.setSeriesShapesVisible(1, false);

        plot.setRenderer(renderer);

        chartPanel.setChart(chart);
    }

    // Classe para modelagem dos cálculos matemáticos do crescimento populacional
    private static class PopulationModel {
        private List<Double> timePoints;
        private List<Double> populationValues;
        private List<Double> growthRates;

        public PopulationModel() {
            timePoints = new ArrayList<>();
            populationValues = new ArrayList<>();
            growthRates = new ArrayList<>();
        }

        public void calculatePopulationData(double P0, double r, double T, double deltaT) {
            timePoints.clear();
            populationValues.clear();
            growthRates.clear();

            for (double t = 0; t <= T + 1e-9; t += deltaT) {
                timePoints.add(t);
                double p = P0 * Math.exp(r * t);
                populationValues.add(p);
                double pPrime = P0 * r * Math.exp(r * t);
                growthRates.add(pPrime);
            }
        }

        public String analyzeLimit() {
            if (timePoints.isEmpty()) {
                return "Nenhuma simulação realizada.";
            }
            double r = 0;
            // To get r, recalc it from first two time points if possible
            if (timePoints.size() >= 2) {
                double t0 = timePoints.get(0);
                double t1 = timePoints.get(1);
                double p0 = populationValues.get(0);
                double p1 = populationValues.get(1);
                // Avoid div by zero
                if ((t1 - t0) != 0) {
                    r = Math.log(p1 / p0) / (t1 - t0);
                }
            }

            if (r > 0) {
                return "Com r = " + new DecimalFormat("#.#####").format(r) +
                        ", a população tende a crescer até o infinito conforme o tempo tende a infinito.";
            } else if (r < 0) {
                return "Com r = " + new DecimalFormat("#.#####").format(r) +
                        ", a população tende a decair até zero conforme o tempo tende a infinito.";
            } else {
                return "Com r = 0, a população permanece constante ao longo do tempo.";
            }
        }

        public List<Double> getTimePoints() {
            return timePoints;
        }

        public List<Double> getPopulationValues() {
            return populationValues;
        }

        public List<Double> getGrowthRates() {
            return growthRates;
        }
    }

    // Modelo para tabela de resultados
    private static class ResultTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Tempo (t)", "População P(t)", "Taxa de Crescimento P'(t)"};
        private List<Double> times = new ArrayList<>();
        private List<Double> populations = new ArrayList<>();
        private List<Double> growthRates = new ArrayList<>();
        private final DecimalFormat df = new DecimalFormat("#.###");

        public void setData(List<Double> times, List<Double> populations, List<Double> growthRates) {
            this.times = new ArrayList<>(times);
            this.populations = new ArrayList<>(populations);
            this.growthRates = new ArrayList<>(growthRates);
            fireTableDataChanged();
        }

        public void clearData() {
            this.times.clear();
            this.populations.clear();
            this.growthRates.clear();
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return times.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return switch (columnIndex) {
                case 0 -> df.format(times.get(rowIndex));
                case 1 -> df.format(populations.get(rowIndex));
                case 2 -> df.format(growthRates.get(rowIndex));
                default -> "";
            };
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

    public static void main(String[] args) {
        // Certifique-se de que o JFreeChart está no classpath para rodar este programa.
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch(Exception ignored){}
            new PopulationGrowthSimulator().setVisible(true);
        });
    }
}

