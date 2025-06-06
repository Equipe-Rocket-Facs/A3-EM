package org.example.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

public class ChartPanelWrapper extends JPanel {

    private final CustomLineChartPanel chartPanel;
    private final JLabel instructionLabel;
    public ChartPanelWrapper() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(6, 8, getWidth() - 12, getHeight() - 16, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(48, 48, 48, 48));
        card.setMaximumSize(new Dimension(900, Integer.MAX_VALUE));
        card.setPreferredSize(new Dimension(800, 600));

        JLabel title = new JLabel("Crescimento Populacional e Taxa");
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(new Color(15, 23, 42));
        title.setBorder(new EmptyBorder(0, 0, 14, 0));
        JLabel subtitle = new JLabel("Visualização clara e objetiva do comportamento ao longo do tempo");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(107, 114, 128));
        subtitle.setBorder(new EmptyBorder(0, 0, 28, 0));
        chartPanel = new CustomLineChartPanel();
        chartPanel.setMinimumSize(new Dimension(300, 220));
        instructionLabel = new JLabel("Use o mouse para zoom, arraste para navegar e dê duplo clique para resetar");
        instructionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        instructionLabel.setForeground(new Color(120, 120, 140));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setBorder(new EmptyBorder(18, 4, 0, 4));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(title, gbc);
        gbc.gridy++;
        card.add(subtitle, gbc);
        gbc.gridy++;
        gbc.weighty = 1; gbc.insets = new Insets(0, 0, 32, 0); gbc.fill = GridBagConstraints.BOTH;
        card.add(chartPanel, gbc);
        gbc.gridy++;
        gbc.weighty = 0; gbc.insets = new Insets(0, 0, 0, 0); gbc.fill = GridBagConstraints.NONE;
        card.add(instructionLabel, gbc);
        GridBagConstraints cgc = new GridBagConstraints();
        cgc.gridx = 0; cgc.gridy = 0; cgc.weightx = 1; cgc.weighty = 1; cgc.fill = GridBagConstraints.BOTH;
        centerContainer.add(card, cgc);
        GridBagConstraints outGbc = new GridBagConstraints();
        outGbc.gridx = 0; outGbc.gridy = 0; outGbc.weightx = 1; outGbc.weighty = 1; outGbc.fill = GridBagConstraints.BOTH;
        add(centerContainer, outGbc);
    }

    public void updateChart(List<Double> times, List<Double> populations, List<Double> growths) {
        chartPanel.setData(times, populations, growths);
    }

    public void clearChart() {
        chartPanel.clearData();
    }

    private static class CustomLineChartPanel extends JPanel {

        private List<Double> times = Collections.emptyList();
        private List<Double> populations = Collections.emptyList();
        private List<Double> growths = Collections.emptyList();

        // Margens aumentadas para acomodar rótulos longos confortavelmente
        private final int leftMargin = 120;
        private final int rightMargin = 40;
        private final int topMargin = 60;
        private final int bottomMargin = 60;

        private double zoomX = 1.0;
        private double zoomY = 1.0;
        private double offsetX = 0;
        private double offsetY = 0;
        private Point lastDragPoint = null;
        private Point mousePos = null;

        private final Color popLineColor = new Color(37, 99, 235);
        private final Color growthLineColor = new Color(107, 114, 128);
        private final Color axisColor = new Color(156, 163, 175);
        private final Color gridColor = new Color(229, 231, 235);
        private final Color chartBg = Color.WHITE;
        private final Color labelColor = new Color(75, 85, 99);

        private final Stroke axisStroke = new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        private final Stroke gridStroke = new BasicStroke(1f);
        private final Stroke popStroke = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        private final Stroke growthStroke = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f, new float[]{6f,5f},0f);

        private final Font chartTitleFont = new Font("Segoe UI", Font.BOLD, 20);
        private final Font axisLabelFont = new Font("Segoe UI", Font.BOLD, 16);
        private final Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        private final Font legendFont = new Font("Segoe UI", Font.PLAIN, 14);

        private double dataMinX = 0, dataMaxX = 1;
        private double dataMinY = 0, dataMaxY = 1;

        public CustomLineChartPanel() {
            setBackground(chartBg);
            setFocusable(true);
            setMinimumSize(new Dimension(300,220));
            setupListeners();
        }

        private void setupListeners() {
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) lastDragPoint = e.getPoint();
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    lastDragPoint = null;
                }
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (lastDragPoint != null) {
                        Point curr = e.getPoint();
                        offsetX += curr.x - lastDragPoint.x;
                        offsetY += curr.y - lastDragPoint.y;
                        lastDragPoint = curr;
                        repaint();
                    }
                }
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) resetZoom();
                }
            };
            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);

            addMouseWheelListener(e -> {
                double zoomFactor = 1.1;
                double delta = e.getPreciseWheelRotation();

                double mouseX = e.getX();
                double mouseY = e.getY();
                double dataX = screenToDataX(mouseX);
                double dataY = screenToDataY(mouseY);

                if (delta < 0) {
                    zoomX *= Math.pow(zoomFactor, -delta);
                    zoomY *= Math.pow(zoomFactor, -delta);
                } else {
                    zoomX /= Math.pow(zoomFactor, delta);
                    zoomY /= Math.pow(zoomFactor, delta);
                }

                zoomX = clamp(zoomX, 0.18, 12);
                zoomY = clamp(zoomY, 0.18, 12);

                double newScreenX = dataToScreenX(dataX);
                double newScreenY = dataToScreenY(dataY);
                offsetX += mouseX - newScreenX;
                offsetY += mouseY - newScreenY;
                repaint();
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    mousePos = e.getPoint();
                    setCursor(isOverDataPoint(mousePos) ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
                    repaint();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    mousePos = null;
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                }
            });
        }

        public void setData(List<Double> times, List<Double> populations, List<Double> growths) {
            this.times = times != null ? times : Collections.emptyList();
            this.populations = populations != null ? populations : Collections.emptyList();
            this.growths = growths != null ? growths : Collections.emptyList();
            calculateDataBounds();
            resetZoom();
        }

        public void clearData() {
            setData(null, null, null);
        }

        private void calculateDataBounds() {
            if (times.isEmpty()) {
                dataMinX = 0; dataMaxX = 1; dataMinY = 0; dataMaxY = 1;
                return;
            }
            dataMinX = Collections.min(times);
            dataMaxX = Collections.max(times);

            double popMin = populations.isEmpty() ? 0 : Collections.min(populations);
            double popMax = populations.isEmpty() ? 0 : Collections.max(populations);

            double growthMin = growths.isEmpty() ? 0 : Collections.min(growths);
            double growthMax = growths.isEmpty() ? 0 : Collections.max(growths);

            dataMinY = Math.min(popMin, growthMin);
            dataMaxY = Math.max(popMax, growthMax);

            double marginY = (dataMaxY - dataMinY) * 0.12;
            if (marginY == 0) marginY = Math.max(1, dataMaxY * 0.12);

            dataMinY = dataMinY - marginY < 0 ? 0 : dataMinY - marginY;
            dataMaxY = dataMaxY + marginY;
        }

        public void resetZoom() {
            zoomX = zoomY = 1.0;
            offsetX = offsetY = 0;
            repaint();
        }

        private double clamp(double val, double min, double max) {
            return Math.max(min, Math.min(max, val));
        }

        private double dataToScreenX(double dataX) {
            if (dataMaxX == dataMinX) return leftMargin;
            double scale = (getWidth() - leftMargin - rightMargin) * zoomX / (dataMaxX - dataMinX);
            return leftMargin + (dataX - dataMinX) * scale + offsetX;
        }

        private double dataToScreenY(double dataY) {
            if (dataMaxY == dataMinY) return getHeight() - bottomMargin;
            double scale = (getHeight() - topMargin - bottomMargin) * zoomY / (dataMaxY - dataMinY);
            return getHeight() - bottomMargin - (dataY - dataMinY) * scale + offsetY;
        }

        private double screenToDataX(double screenX) {
            double scale = (getWidth() - leftMargin - rightMargin) * zoomX / (dataMaxX - dataMinX);
            return (screenX - leftMargin - offsetX) / scale + dataMinX;
        }

        private double screenToDataY(double screenY) {
            double scale = (getHeight() - topMargin - bottomMargin) * zoomY / (dataMaxY - dataMinY);
            return (getHeight() - bottomMargin - screenY + offsetY) / scale + dataMinY;
        }

        private double dataToScreenXNoOffset(double dataX) {
            if (dataMaxX == dataMinX) return leftMargin;
            double scale = (getWidth() - leftMargin - rightMargin) * zoomX / (dataMaxX - dataMinX);
            return leftMargin + (dataX - dataMinX) * scale;
        }

        private double dataToScreenYNoOffset(double dataY) {
            if (dataMaxY == dataMinY) return getHeight() - bottomMargin;
            double scale = (getHeight() - topMargin - bottomMargin) * zoomY / (dataMaxY - dataMinY);
            return getHeight() - bottomMargin - (dataY - dataMinY) * scale;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(chartBg);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            drawChartTitle(g2d);
            drawGridBg(g2d);
            drawGrid(g2d);
            drawAxes(g2d);
            drawDataSeries(g2d);
            drawLegend(g2d);
            drawTooltip(g2d);

            g2d.dispose();
        }

        private void drawChartTitle(Graphics2D g2d) {
            g2d.setFont(chartTitleFont);
            g2d.setColor(new Color(15, 23, 42));
            String chartTitle = "Gráfico de Crescimento e Taxa";
            FontMetrics fm = g2d.getFontMetrics();
            int x = leftMargin + (getWidth() - leftMargin - rightMargin) / 2 - fm.stringWidth(chartTitle) / 2;
            int y = topMargin - 30;
            g2d.drawString(chartTitle, x, y);
        }

        private void drawGridBg(Graphics2D g2d) {
            int width = getWidth();
            int height = getHeight();
            int numYTicks = calculateYTicks();
            for (int i = 0; i < numYTicks; i++) {
                double yRatio1 = (double) i / numYTicks;
                double yRatio2 = (double) (i + 1) / numYTicks;
                int y1 = (int) (topMargin + yRatio1 * (height - topMargin - bottomMargin));
                int y2 = (int) (topMargin + yRatio2 * (height - topMargin - bottomMargin));
                if (i % 2 == 0) {
                    g2d.setColor(new Color(243, 244, 246));
                    g2d.fillRect(leftMargin, y1,
                            width - leftMargin - rightMargin, y2 - y1);
                }
            }
        }

        private int calculateYTicks() {
            int baseTicks = 7;
            int extraTicks = (int) (Math.log(zoomY) / Math.log(1.5)) * 2;
            return Math.max(baseTicks, baseTicks + extraTicks);
        }

        private int calculateXTicks() {
            int baseTicks = 8;
            int extraTicks = (int) (Math.log(zoomX) / Math.log(1.5)) * 2;
            return Math.max(baseTicks, baseTicks + extraTicks);
        }

        private void drawAxes(Graphics2D g2d) {
            g2d.setColor(axisColor);
            g2d.setStroke(axisStroke);
            g2d.drawLine(leftMargin, topMargin, leftMargin, getHeight() - bottomMargin);
            g2d.drawLine(leftMargin, getHeight() - bottomMargin,
                    getWidth() - rightMargin, getHeight() - bottomMargin);

            g2d.setFont(axisLabelFont);
            g2d.setColor(labelColor);
            drawRotatedString(g2d, "Valor",
                    leftMargin - 80,
                    topMargin + (getHeight() - topMargin - bottomMargin) / 2,
                    -90);

            String xLabel = "Tempo (t)";
            FontMetrics fm = g2d.getFontMetrics();
            int xLabelWidth = fm.stringWidth(xLabel);
            g2d.drawString(xLabel,
                    leftMargin + (getWidth() - leftMargin - rightMargin) / 2 - xLabelWidth / 2,
                    getHeight() - bottomMargin + 40);

            g2d.setFont(labelFont);
            g2d.setColor(labelColor);

            int numXTicks = calculateXTicks();
            for (int i = 0; i <= numXTicks; i++) {
                double ratio = (double) i / numXTicks;
                double dataX = dataMinX + ratio * (dataMaxX - dataMinX);
                int x = (int) dataToScreenX(dataX);
                g2d.drawLine(x, getHeight() - bottomMargin, x, getHeight() - bottomMargin + 6);
                String label = String.format("%d", (int) dataX);
                int lw = g2d.getFontMetrics().stringWidth(label);
                g2d.drawString(label, x - lw / 2, getHeight() - bottomMargin + 22);
            }

            int numYTicks = calculateYTicks();
            DecimalFormat df = new DecimalFormat("#,##0");
            for (int i = 0; i <= numYTicks; i++) {
                double ratio = (double) i / numYTicks;
                double dataY = dataMinY + ratio * (dataMaxY - dataMinY);
                int y = (int) dataToScreenY(dataY);
                g2d.drawLine(leftMargin - 6, y, leftMargin, y);
                String label = formatLargeNumber(dataY);
                FontMetrics fmY = g2d.getFontMetrics();
                int lh = fmY.getHeight();
                g2d.drawString(label, leftMargin - 10 - fmY.stringWidth(label), y + lh / 3);
            }
        }

        private String formatLargeNumber(double value) {
            if (value >= 1_000_000) {
                double valM = value / 1_000_000;
                return String.format("%.0fM", valM);
            }
            return String.format("%.0f", value);
        }

        private void drawRotatedString(Graphics2D g2d, String text, int x, int y, int angle) {
            AffineTransform orig = g2d.getTransform();
            g2d.translate(x, y);
            g2d.rotate(Math.toRadians(angle));
            g2d.drawString(text, 0, 0);
            g2d.setTransform(orig);
        }

        private void drawGrid(Graphics2D g2d) {
            int width = getWidth();
            int height = getHeight();
            int numXTicks = calculateXTicks();
            int numYTicks = calculateYTicks();
            g2d.setColor(gridColor);
            g2d.setStroke(gridStroke);

            for (int i = 0; i <= numXTicks; i++) {
                double ratio = (double) i / numXTicks;
                double dataX = dataMinX + ratio * (dataMaxX - dataMinX);
                int x = (int) dataToScreenX(dataX);
                g2d.drawLine(x, topMargin, x, height - bottomMargin);
            }

            for (int i = 0; i <= numYTicks; i++) {
                double ratio = (double) i / numYTicks;
                double dataY = dataMinY + ratio * (dataMaxY - dataMinY);
                int y = (int) dataToScreenY(dataY);
                g2d.drawLine(leftMargin, y, width - rightMargin, y);
            }
        }

        private void drawDataSeries(Graphics2D g2d) {
            if (times.isEmpty()) return;

            g2d.setColor(popLineColor);
            g2d.setStroke(popStroke);
            drawLineSeries(g2d, times, populations);

            int highLightIndex = -1;
            if (mousePos != null) {
                int closest = -1;
                double minDist = Double.MAX_VALUE;
                for (int i = 0; i < times.size(); i++) {
                    double x = dataToScreenX(times.get(i));
                    double y = dataToScreenY(populations.get(i));
                    double d = mousePos.distance(x, y);
                    if (d < minDist) {
                        minDist = d;
                        closest = i;
                    }
                }
                if (minDist <= 16) highLightIndex = closest;
            }

            for (int i = 0; i < times.size(); i++) {
                double x = dataToScreenX(times.get(i));
                double y = dataToScreenY(populations.get(i));
                if (i == highLightIndex) {
                    g2d.setColor(Color.WHITE);
                    g2d.fill(new Ellipse2D.Double(x - 8, y - 8, 16, 16));
                    g2d.setColor(popLineColor);
                    g2d.fill(new Ellipse2D.Double(x - 5, y - 5, 10, 10));
                } else {
                    g2d.setColor(popLineColor.darker());
                    g2d.fill(new Ellipse2D.Double(x - 3, y - 3, 6, 6));
                }
            }

            g2d.setColor(growthLineColor);
            g2d.setStroke(growthStroke);
            drawLineSeries(g2d, times, growths);
        }

        private void drawLineSeries(Graphics2D g2d, List<Double> xs, List<Double> ys) {
            if (xs.isEmpty() || ys.isEmpty()) return;
            Path2D.Double path = new Path2D.Double();
            boolean first = true;
            for (int i = 0; i < xs.size(); i++) {
                double x = dataToScreenX(xs.get(i));
                double y = dataToScreenY(ys.get(i));
                if (first) {
                    path.moveTo(x, y);
                    first = false;
                } else
                    path.lineTo(x, y);
            }
            g2d.draw(path);
        }

        private void drawLegend(Graphics2D g2d) {
            int x = getWidth() - rightMargin - 180;
            int y = topMargin + 12;
            int w = 160;
            int h = 46;
            RoundRectangle2D.Double bg = new RoundRectangle2D.Double(x, y, w, h, 14, 14);
            g2d.setColor(new Color(255, 255, 255, 240));
            g2d.fill(bg);
            g2d.setColor(new Color(203, 213, 225));
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.draw(bg);
            g2d.setFont(legendFont);

            int lineY = y + 18;
            g2d.setColor(popLineColor);
            g2d.setStroke(popStroke);
            g2d.drawLine(x + 16, lineY, x + 48, lineY);
            g2d.fillOval(x + 26, lineY - 5, 10, 10);
            g2d.setColor(labelColor);
            g2d.drawString("População P(t)", x + 58, lineY + 5);

            lineY += 24;
            g2d.setColor(growthLineColor);
            g2d.setStroke(growthStroke);
            g2d.drawLine(x + 16, lineY, x + 48, lineY);
            g2d.setColor(labelColor);
            g2d.drawString("Taxa P'(t)", x + 58, lineY + 5);
        }

        private void drawTooltip(Graphics2D g2d) {
            if (mousePos == null || times.isEmpty()) return;
            final int MAX_DIST = 18;
            int bestIndex = -1;
            double minDist = Double.MAX_VALUE;
            for (int i = 0; i < times.size(); i++) {
                double x = dataToScreenX(times.get(i));
                double y = dataToScreenY(populations.get(i));
                double d = mousePos.distance(x, y);
                if (d < minDist) {
                    minDist = d;
                    bestIndex = i;
                }
            }
            if (bestIndex < 0 || minDist > MAX_DIST) return;
            double px = dataToScreenX(times.get(bestIndex));
            double py = dataToScreenY(populations.get(bestIndex));

            String tStr = String.format("t: %.4f", times.get(bestIndex));
            String popStr = String.format("População: %,.2f", populations.get(bestIndex));
            String growthStr = String.format("Crescimento: %,.2f", growths.get(bestIndex));
            double perc = (populations.get(bestIndex) == 0) ? 0.0 : (growths.get(bestIndex) / populations.get(bestIndex)) * 100;
            String percStr = String.format("Diferença: %.2f%%", perc);

            Font tooltipFont = new Font("Segoe UI", Font.BOLD, 14);
            g2d.setFont(tooltipFont);
            FontMetrics fm = g2d.getFontMetrics();

            int width = Math.max(fm.stringWidth(popStr), Math.max(fm.stringWidth(growthStr), Math.max(fm.stringWidth(tStr), fm.stringWidth(percStr)))) + 24;
            int height = fm.getHeight() * 5 + 14;
            int x = (int) px + 14;
            int y = (int) py - height - 6;

            if (x + width > getWidth()) x = (int) px - width - 14;
            if (y < 0) y = (int) py + 18;

            g2d.setColor(new Color(240, 243, 250, 210));
            g2d.fillRoundRect(x + 2, y + 2, width + 2, height + 5, 14, 14);
            g2d.setColor(new Color(255, 255, 255, 255));
            g2d.fillRoundRect(x, y, width, height, 14, 14);
            g2d.setColor(new Color(189, 197, 209));
            g2d.setStroke(new BasicStroke(1f));
            g2d.drawRoundRect(x, y, width, height, 14, 14);

            int tx = x + 10;
            int ty = y + fm.getAscent() + 12;

            g2d.setColor(popLineColor);
            g2d.drawString(popStr, tx, ty);

            g2d.setColor(growthLineColor.darker());
            g2d.drawString(growthStr, tx, ty + fm.getHeight());

            g2d.setColor(labelColor.darker());
            g2d.drawString(tStr, tx, ty + 2 * fm.getHeight());
            g2d.drawString(percStr, tx, ty + 3 * fm.getHeight());
        }

        private boolean isOverDataPoint(Point p) {
            if (times.isEmpty() || p == null) return false;
            final int R = 10;
            for (int i = 0; i < times.size(); i++) {
                double x = dataToScreenX(times.get(i));
                double y = dataToScreenY(populations.get(i));
                Ellipse2D.Double circle = new Ellipse2D.Double(x - R, y - R, 2 * R, 2 * R);
                if (circle.contains(p)) return true;
            }
            return false;
        }
    }

    private static class TargetIcon implements Icon {
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.3f));
            g2.drawOval(x + 2, y + 4, 16, 16);
            g2.drawLine(x + 10, y + 5, x + 10, y + 11);
            g2.drawLine(x + 10, y + 19, x + 10, y + 13);
            g2.drawLine(x + 5, y + 12, x + 9, y + 12);
            g2.drawLine(x + 15, y + 12, x + 11, y + 12);
            g2.dispose();
        }
        @Override public int getIconWidth() { return 20; }
        @Override public int getIconHeight() { return 22; }
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        public RoundedBorder(int radius) { this.radius = radius; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(59, 130, 246, 150));
            g2.setStroke(new BasicStroke(2.2f));
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
        }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(6, 24, 6, 24); }
        @Override
        public Insets getBorderInsets(Component c, Insets insets) { return getBorderInsets(c); }
    }
}

