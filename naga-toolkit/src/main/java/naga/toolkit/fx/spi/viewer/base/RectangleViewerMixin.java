package naga.toolkit.fx.spi.viewer.base;

import naga.toolkit.fx.scene.shape.Rectangle;
import naga.toolkit.fx.spi.viewer.RectangleViewer;

/**
 * @author Bruno Salmon
 */
public interface RectangleViewerMixin
        extends RectangleViewer,
        ShapeViewerMixin<Rectangle, RectangleViewerBase, RectangleViewerMixin> {

    void updateX(Double x);

    void updateY(Double y);

    void updateWidth(Double width);

    void updateHeight(Double height);

    void updateArcWidth(Double arcWidth);

    void updateArcHeight(Double arcHeight);
}