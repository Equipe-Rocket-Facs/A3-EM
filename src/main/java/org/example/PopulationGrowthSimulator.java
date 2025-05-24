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
import java.util.Map;

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

    private static final double MIN_GROWTH_RATE = -1.0;
    private static final double MAX_GROWTH_RATE = 1.0;
    private static final double MIN_POPULATION = 0.0;
    private static final double MIN_TIME = 0.0;

    // Add validation messages as constants
    private static final String ERROR_TITLE = "Erro de Validação";
    private static final Map<String, String> VALIDATION_MESSAGES = Map.of(
            "population", "A população inicial (P₀) deve ser maior que zero. Por favor, insira um número positivo válido.",
            "growthRate", "A taxa de crescimento (r) deve estar entre -1.0 e 1.0. Por favor, insira uma taxa de crescimento realista.",
            "finalTime", "O tempo final (T) deve ser maior que zero. Por favor, insira um número positivo válido.",
            "deltaTime", "O intervalo de tempo (Δt) deve ser maior que zero. Por favor, insira um número positivo válido.",
            "timeInterval", "O intervalo de tempo (Δt) não pode ser maior que o tempo final (T). Por favor, ajuste os valores adequadamente."
    );

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
        tfInitialPopulation.setToolTipText(
                "Digite a população inicial (P₀). Deve ser um número positivo maior que zero.");

        JLabel lblGrowthRate = new JLabel("Taxa de Crescimento (r):");
        tfGrowthRate = new JTextField(10);
        tfGrowthRate.setToolTipText(
                "Digite a taxa de crescimento (r) entre -1.0 e 1.0.\n" +
                        "Exemplo: 0.03 para crescimento de 3% ou -0.02 para decrescimento de 2%");

        JLabel lblFinalTime = new JLabel("Tempo Final da Simulação (T):");
        tfFinalTime = new JTextField(10);
        tfFinalTime.setToolTipText(
                "Digite o tempo final (T) da simulação. Deve ser um número positivo.");

        JLabel lblDeltaTime = new JLabel("Intervalo de Tempo (Δt):");
        tfDeltaTime = new JTextField(10);
        tfDeltaTime.setToolTipText(
                "Digite o intervalo de tempo (Δt). Deve ser positivo e menor que o tempo final.");

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

    private ValidationResult validateInputs() {
        try {
            // Parse all inputs first
            double P0 = parseDoubleInput(tfInitialPopulation.getText(), "população inicial");
            double r = parseDoubleInput(tfGrowthRate.getText(), "taxa de crescimento");
            double T = parseDoubleInput(tfFinalTime.getText(), "tempo final");
            double deltaT = parseDoubleInput(tfDeltaTime.getText(), "intervalo de tempo");

            // Validate initial population
            if (P0 <= MIN_POPULATION) {
                return new ValidationResult(false, VALIDATION_MESSAGES.get("population"));
            }

            // Validate growth rate range
            if (r < MIN_GROWTH_RATE || r > MAX_GROWTH_RATE) {
                return new ValidationResult(false, VALIDATION_MESSAGES.get("growthRate"));
            }

            // Validate final time
            if (T <= MIN_TIME) {
                return new ValidationResult(false, VALIDATION_MESSAGES.get("finalTime"));
            }

            // Validate time interval
            if (deltaT <= MIN_TIME) {
                return new ValidationResult(false, VALIDATION_MESSAGES.get("deltaTime"));
            }

            // Validate time interval relationship with final time
            if (deltaT > T) {
                return new ValidationResult(false, VALIDATION_MESSAGES.get("timeInterval"));
            }

            // All validations passed
            return new ValidationResult(true, null, P0, r, T, deltaT);

        } catch (NumberFormatException e) {
            return new ValidationResult(false,
                    "Por favor, insira apenas valores numéricos válidos em todos os campos.\n" +
                            "Exemplo: Use '.' como separador decimal (ex: 0.03)");
        }
    }

    // Helper method to parse double inputs
    private double parseDoubleInput(String input, String fieldName) throws NumberFormatException {
        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException(
                    String.format("O valor inserido para %s não é um número válido.", fieldName));
        }
    }

    // Enhanced showError method with better UI feedback
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                ERROR_TITLE,
                JOptionPane.ERROR_MESSAGE);
    }

    private void onSimulate() {
        ValidationResult validation = validateInputs();

        if (!validation.isValid()) {
            showError(validation.getMessage());
            return;
        }

        // If validation passed, proceed with simulation
        populationModel.calculatePopulationData(
                validation.getP0(),
                validation.getR(),
                validation.getT(),
                validation.getDeltaT()
        );

        // Update UI components
        updateUIWithResults();
    }

    private void updateUIWithResults() {
        tableModel.setData(
                populationModel.getTimePoints(),
                populationModel.getPopulationValues(),
                populationModel.getGrowthRates()
        );
        taLimitAnalysis.setText(populationModel.analyzeLimit());
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



