package mongooses.core.shared.entities.impl;

import mongooses.core.shared.entities.Country;
import webfx.framework.orm.entity.EntityId;
import webfx.framework.orm.entity.EntityStore;
import webfx.framework.orm.entity.impl.DynamicEntity;

/**
 * @author Bruno Salmon
 */
public final class CountryImpl extends DynamicEntity implements Country {

    public CountryImpl(EntityId id, EntityStore store) {
        super(id, store);
    }
}