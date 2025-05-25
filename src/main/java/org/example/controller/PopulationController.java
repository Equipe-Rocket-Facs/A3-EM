package org.example.controller;

import org.example.model.PopulationModel;
import org.example.model.ValidationResult;
import org.example.view.*;

import javax.swing.*;
import java.util.Map;

public class PopulationController {
    private final PopulationModel model;
    private final InputPanel inputPanel;
    private final ResultTablePanel tablePanel;
    private final AnalysisPanel analysisPanel;
    private final ChartPanelWrapper chartPanel;

    private static final double MIN_GROWTH_RATE = -1.0, MAX_GROWTH_RATE = 1.0, MIN_POPULATION = 0.0, MIN_TIME = 0.0;
    private static final Map<String, String> VALIDATION_MESSAGES = Map.of(
            "population", "A população inicial (P₀) deve ser maior que zero.",
            "growthRate", "A taxa de crescimento (r) deve estar entre -1.0 e 1.0.",
            "finalTime", "O tempo final (T) deve ser maior que zero.",
            "deltaTime", "O intervalo de tempo (Δt) deve ser maior que zero.",
            "timeInterval", "O intervalo de tempo (Δt) não pode ser maior que o tempo final (T)."
    );

    public PopulationController(PopulationModel model, InputPanel inputPanel, ResultTablePanel tablePanel,
                                AnalysisPanel analysisPanel, ChartPanelWrapper chartPanel) {
        this.model = model;
        this.inputPanel = inputPanel;
        this.tablePanel = tablePanel;
        this.analysisPanel = analysisPanel;
        this.chartPanel = chartPanel;

        inputPanel.setSimulateAction(this::onSimulate);
        inputPanel.setResetAction(this::onReset);
    }

    private void onSimulate() {
        ValidationResult validation = validateInputs();
        if (!validation.isValid()) {
            showError(validation.getMessage());
            return;
        }

        // Se a validação passou, prosseguir com a simulação
        model.calculatePopulationData(validation.getP0(), validation.getR(), validation.getT(), validation.getDeltaT());
        tablePanel.getModel().setData(model.getTimePoints(), model.getPopulationValues(), model.getGrowthRates());
        analysisPanel.setAnalysisText(model.analyzeLimit());
        chartPanel.updateChart(model.getTimePoints(), model.getPopulationValues(), model.getGrowthRates());
    }

    private ValidationResult validateInputs() {
        try {
            double P0 = parseDouble(inputPanel.getInitialPopulation(), "population");
            double r = parseDouble(inputPanel.getGrowthRate(), "growthRate");
            double T = parseDouble(inputPanel.getFinalTime(), "finalTime");
            double deltaT = parseDouble(inputPanel.getDeltaTime(), "deltaTime");

            if (P0 <= MIN_POPULATION) return new ValidationResult(false, VALIDATION_MESSAGES.get("population"));
            if (r < MIN_GROWTH_RATE || r > MAX_GROWTH_RATE) return new ValidationResult(false, VALIDATION_MESSAGES.get("growthRate"));
            if (T <= MIN_TIME) return new ValidationResult(false, VALIDATION_MESSAGES.get("finalTime"));
            if (deltaT <= MIN_TIME) return new ValidationResult(false, VALIDATION_MESSAGES.get("deltaTime"));
            if (deltaT > T) return new ValidationResult(false, VALIDATION_MESSAGES.get("timeInterval"));

            return new ValidationResult(true, null, P0, r, T, deltaT);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Por favor, insira apenas valores numéricos válidos em todos os campos.");
        }
    }

    private double parseDouble(String input, String field) {
        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Campo inválido: " + field);
        }
    }

    private void onReset() {
        inputPanel.clearFields();
        tablePanel.getModel().clearData();
        analysisPanel.clear();
        chartPanel.clearChart();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
    }
}
