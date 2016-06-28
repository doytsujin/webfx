package naga.core.spi.toolkit.javafx.charts;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import naga.core.spi.toolkit.charts.LineChart;

/**
 * @author Bruno Salmon
 */
public class FxLineChart extends FxXYChart implements LineChart<javafx.scene.chart.Chart> {

    public FxLineChart() {
        this(createLineChart());
    }

    public FxLineChart(javafx.scene.chart.LineChart lineChart) {
        super(lineChart);
    }

    private static javafx.scene.chart.LineChart createLineChart() {
        return new javafx.scene.chart.LineChart(new CategoryAxis(), new NumberAxis());
    }
}
