package emul.com.sun.javafx.scene.control.skin;

import emul.com.sun.javafx.scene.control.behaviour.TextInputControlBehavior;
import emul.javafx.scene.control.TextInputControl;

/**
 * Abstract base class for text input skins.
 *
 * (empty as we rely on the target toolkit for now)
 */
public abstract class TextInputControlSkin<T extends TextInputControl, B extends TextInputControlBehavior<T>> extends BehaviorSkinBase<T, B> {


    public TextInputControlSkin(final T textInput, final B behavior) {
        super(textInput, behavior);
    }

}