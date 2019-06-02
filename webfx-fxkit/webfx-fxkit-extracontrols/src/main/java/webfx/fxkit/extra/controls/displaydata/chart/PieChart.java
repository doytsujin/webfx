package webfx.fxkit.extra.controls.displaydata.chart;

import webfx.fxkit.extra.controls.registry.ExtraControlsRegistry;

/**
 * Describes a chart that represents data in a form of a circle divided into triangular wedges called slices.
 *
 * See {@link Chart} for an explanation of the data format.
 *
 * @author Bruno Salmon
 */
public final class PieChart extends Chart {

    static {
        ExtraControlsRegistry.registerPieChart();
    }
}