package naga.fx.spi.javafx.fx.viewer;

import naga.fx.scene.control.Slider;
import naga.fx.spi.viewer.base.SliderViewerBase;
import naga.fx.spi.viewer.base.SliderViewerMixin;

/**
 * @author Bruno Salmon
 */
public class FxSliderViewer
        <FxN extends javafx.scene.control.Slider, N extends Slider, NB extends SliderViewerBase<N, NB, NM>, NM extends SliderViewerMixin<N, NB, NM>>

        extends FxControlViewer<FxN, N, NB, NM>
        implements SliderViewerMixin<N, NB, NM>, FxLayoutMeasurable {

    public FxSliderViewer() {
        super((NB) new SliderViewerBase());
    }

    @Override
    protected FxN createFxNode() {
        javafx.scene.control.Slider slider = new javafx.scene.control.Slider();
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMinorTickCount(4);
        slider.setMajorTickUnit(500);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> updateNodeValue(newValue.doubleValue()));
        return (FxN) slider;
    }

    @Override
    public void updateMin(Double min) {
        getFxNode().setMin(min);
    }

    @Override
    public void updateMax(Double max) {
        getFxNode().setMax(max);
    }

    @Override
    public void updateValue(Double value) {
        getFxNode().setValue(value);
    }
}