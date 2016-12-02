package naga.toolkit.fx.scene.transform.impl;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import naga.toolkit.fx.geom.Point2D;
import naga.toolkit.fx.scene.transform.Affine;
import naga.toolkit.fx.scene.transform.Scale;
import naga.toolkit.fx.scene.transform.Transform;

/**
 * @author Bruno Salmon
 */
public class ScaleImpl extends TransformImpl implements Scale {

    public ScaleImpl() {
    }

    public ScaleImpl(double x, double y) {
        setX(x);
        setY(y);
    }

    private final Property<Double> xProperty = new SimpleObjectProperty<>(1d);
    @Override
    public Property<Double> xProperty() {
        return xProperty;
    }

    private final Property<Double> yProperty = new SimpleObjectProperty<>(1d);
    @Override
    public Property<Double> yProperty() {
        return yProperty;
    }

    @Override
    public Point2D transform(double x, double y) {
        return Point2D.create(x * getX(), y * getY());
    }

    @Override
    public Transform createInverse() {
        return new ScaleImpl(1 / getX(), 1 / getY());
    }

    @Override
    protected Property[] propertiesInvalidatingCache() {
        return new Property[]{xProperty, yProperty};
    }

    @Override
    public Affine toAffine() {
        return new AffineImpl(getX(), 0, 0, getY(), 0, 0);
    }
}