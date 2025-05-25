package org.example.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.axis.ValueAxis;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class ChartPanelWrapper extends JPanel {
    private final ChartPanel chartPanel = new ChartPanel(null);

    public ChartPanelWrapper() {
        setLayout(new BorderLayout());

        // Fonte do FlatLaf
        Font titleFont = UIManager.getFont("Label.font").deriveFont(Font.BOLD, 14f);

        // Bordas modernas com FlatLaf
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                "Gráfico de Crescimento Populacional e Taxa"
        );
        titledBorder.setTitleFont(titleFont);
        titledBorder.setTitleColor(UIManager.getColor("Label.foreground"));
        setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                titledBorder
        ));

        chartPanel.setPreferredSize(new Dimension(550, 650));
        chartPanel.setBackground(UIManager.getColor("Panel.background"));
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
                "Tempo (t)",
                "Valor",
                dataset
        );

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(UIManager.getColor("Panel.background")); // cor do painel FlatLaf
        plot.setDomainGridlinePaint(new Color(180, 180, 180));
        plot.setRangeGridlinePaint(new Color(180, 180, 180));

        // Eixos com FlatLaf
        ValueAxis domainAxis = plot.getDomainAxis();
        ValueAxis rangeAxis = plot.getRangeAxis();
        Font axisFont = UIManager.getFont("Label.font");

        domainAxis.setLabelFont(axisFont.deriveFont(Font.BOLD));
        domainAxis.setTickLabelFont(axisFont);
        domainAxis.setLabelPaint(UIManager.getColor("Label.foreground"));
        domainAxis.setTickLabelPaint(UIManager.getColor("Label.foreground"));

        rangeAxis.setLabelFont(axisFont.deriveFont(Font.BOLD));
        rangeAxis.setTickLabelFont(axisFont);
        rangeAxis.setLabelPaint(UIManager.getColor("Label.foreground"));
        rangeAxis.setTickLabelPaint(UIManager.getColor("Label.foreground"));

        // Renderer com estilo moderno
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-2, -2, 4, 4));

        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesStroke(1, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{6.0f, 6.0f}, 0));
        renderer.setSeriesShapesVisible(1, false);

        plot.setRenderer(renderer);

        // Estilo da legenda
        chart.getLegend().setItemFont(axisFont);
        chart.getLegend().setBackgroundPaint(new Color(255, 255, 255, 180));
        chart.getLegend().setBorder(0, 0, 0, 0);

        chartPanel.setChart(chart);
    }

    public void clearChart() {
        chartPanel.setChart(null);
    }
}
