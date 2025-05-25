package org.example.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChartPanelWrapper extends JPanel {
    private final ChartPanel chartPanel = new ChartPanel(null);

    public ChartPanelWrapper() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Gráfico de Crescimento Populacional e Taxa"));
        chartPanel.setPreferredSize(new Dimension(550, 650));
        add(chartPanel, BorderLayout.CENTER);
    }

    public void updateChart(List<Double> times, List<Double> populations, List<Double> growths) {
        XYSeries popSeries = new XYSeries("População P(t)");
        XYSeries growthSeries = new XYSeries("Taxa de Crescimento P'(t)");
        for (int i = 0; i < times.size(); i++) {
            popSeries.add(times.get(i), populations.get(i));
            growthSeries.add(times.get(i), growths.get(i));
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(popSeries);
        dataset.addSeries(growthSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Crescimento Populacional e Taxa de Crescimento",
                "Tempo (t)", "Valor", dataset);

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesFilled(0, true);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesStroke(1, new BasicStroke(
                2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6.0f, 6.0f}, 0));
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        chartPanel.setChart(chart);
    }

    public void clearChart() { chartPanel.setChart(null); }
}
