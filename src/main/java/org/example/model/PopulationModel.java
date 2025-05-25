package org.example.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Modela o crescimento populacional exponencial e calcula os dados.
 */
public class PopulationModel {
    private final List<Double> timePoints = new ArrayList<>();
    private final List<Double> populationValues = new ArrayList<>();
    private final List<Double> growthRates = new ArrayList<>();


    public void calculatePopulationData(double initialPopulation, double growthRate, double finalTime, double deltaTime) {
        timePoints.clear();
        populationValues.clear();
        growthRates.clear();

        for (double t = 0; t <= finalTime + 1e-9; t += deltaTime) {
            timePoints.add(t);
            double population = initialPopulation * Math.exp(growthRate * t);
            populationValues.add(population);
            double growth = initialPopulation * growthRate * Math.exp(growthRate * t);
            growthRates.add(growth);
        }
    }

    public String analyzeLimit() {
        if (timePoints.isEmpty()) {
            return "Nenhuma simulação realizada.";
        }
        double r = calculateGrowthRate();
        return generateLimitAnalysisMessage(r);
    }

    private double calculateGrowthRate() {
        if (timePoints.size() < 2) return 0;
        double t0 = timePoints.get(0);
        double t1 = timePoints.get(1);
        double p0 = populationValues.get(0);
        double p1 = populationValues.get(1);
        return (t1 - t0) != 0 ? Math.log(p1 / p0) / (t1 - t0) : 0;
    }

    private String generateLimitAnalysisMessage(double r) {
        DecimalFormat df = new DecimalFormat("#.#####");
        if (r > 0) {
            return "Com r = " + df.format(r) + ", a população tende a crescer até o infinito conforme o tempo tende a infinito.";
        } else if (r < 0) {
            return "Com r = " + df.format(r) + ", a população tende a decair até zero conforme o tempo tende a infinito.";
        } else {
            return "Com r = 0, a população permanece constante ao longo do tempo.";
        }
    }

    public List<Double> getTimePoints() { return timePoints; }
    public List<Double> getPopulationValues() { return populationValues; }
    public List<Double> getGrowthRates() { return growthRates; }
}
