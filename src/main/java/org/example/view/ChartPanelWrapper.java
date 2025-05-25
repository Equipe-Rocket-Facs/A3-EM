package org.example.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.axis.ValueAxis;

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

        // Estilizando o gráfico
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(240, 240, 240)); // Fundo claro
        plot.setDomainGridlinePaint(new Color(200, 200, 200)); // Gridlines suaves
        plot.setRangeGridlinePaint(new Color(200, 200, 200)); // Gridlines suaves

        // Eixos
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        domainAxis.setLabelPaint(Color.DARK_GRAY);
        domainAxis.setTickLabelPaint(Color.DARK_GRAY);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        rangeAxis.setLabelPaint(Color.DARK_GRAY);
        rangeAxis.setTickLabelPaint(Color.DARK_GRAY);

        // Renderer
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(0, 204, 255)); // Azul-claro
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Linha sólida
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesFilled(0, true);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-2, -2, 4, 4)); // Marcadores menores

        renderer.setSeriesPaint(1, new Color(255, 102, 0)); // Vermelho-alaranjado
        renderer.setSeriesStroke(1, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6.0f, 6.0f}, 0)); // Linha tracejada
        renderer.setSeriesShapesVisible(1, false); // Sem pontos para a taxa de crescimento

        plot.setRenderer(renderer);

        // Melhorar a legenda
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setBackgroundPaint(new Color(255, 255, 255, 200)); // Fundo semi-transparente
        chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 12));
        chart.getLegend().setPosition(org.jfree.chart.ui.RectangleEdge.RIGHT); // Posição da legenda à direita

        // Definindo o gráfico no painel
        chartPanel.setChart(chart);
    }

    public void clearChart() {
        chartPanel.setChart(null);
    }
}
