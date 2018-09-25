package webfx.framework.operations.i18n;

import webfx.platforms.core.util.collection.Collections;

import java.util.Collection;
import java.util.ServiceLoader;

/**
 * @author Bruno Salmon
 */
public interface ChangeLanguageRequestEmitter {

    ChangeLanguageRequest emitLanguageRequest();

    static Collection<ChangeLanguageRequestEmitter> getProvidedEmitters() {
        return Collections.listOf(ServiceLoader.load(ChangeLanguageRequestEmitter.class));
    }
}