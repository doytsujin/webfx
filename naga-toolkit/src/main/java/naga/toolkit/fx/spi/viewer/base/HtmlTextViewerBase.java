package naga.toolkit.fx.spi.viewer.base;

import javafx.beans.value.ObservableValue;
import naga.toolkit.fx.ext.control.HtmlText;
import naga.toolkit.fx.spi.DrawingRequester;
import naga.toolkit.fx.spi.viewer.HtmlTextViewer;

/**
 * @author Bruno Salmon
 */
public class HtmlTextViewerBase
        <N extends HtmlText, NV extends HtmlTextViewerBase<N, NV, NM>, NM extends HtmlTextViewerMixin<N, NV, NM>>
        extends RegionViewerBase<N, NV, NM>
        implements HtmlTextViewer<N> {

    @Override
    public void bind(N t, DrawingRequester drawingRequester) {
        super.bind(t, drawingRequester);
        requestUpdateOnPropertiesChange(drawingRequester
                , t.textProperty()
        );
    }

    @Override
    public boolean updateProperty(ObservableValue changedProperty) {
        N n = node;
        return super.updateProperty(changedProperty)
                || updateProperty(n.textProperty(), changedProperty, mixin::updateText)
                ;
    }
}