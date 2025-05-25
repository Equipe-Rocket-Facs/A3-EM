package org.example.view;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ResultTableModel extends AbstractTableModel {
    private final String[] columns = {"Tempo (t)", "População P(t)", "Taxa de Crescimento P'(t)"};
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
        times.clear(); populations.clear(); growthRates.clear();
        fireTableDataChanged();
    }
    @Override public int getRowCount() { return times.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public Object getValueAt(int row, int col) {
        return switch (col) {
            case 0 -> df.format(times.get(row));
            case 1 -> df.format(populations.get(row));
            case 2 -> df.format(growthRates.get(row));
            default -> "";
        };
    }
    @Override public String getColumnName(int col) { return columns[col]; }
}


