package naga.fxdata.cell.renderer;

import javafx.scene.image.ImageView;
import naga.commons.util.Strings;
import naga.fx.util.ImageStore;

/**
 * @author Bruno Salmon
 */
class ImageRenderer implements ValueRenderer {

    public static ImageRenderer SINGLETON = new ImageRenderer();

    private ImageRenderer() {}

    @Override
    public ImageView renderCellValue(Object value) {
        return value == null ? new ImageView() : ImageStore.createImageView(Strings.toString(value));
    }
}
