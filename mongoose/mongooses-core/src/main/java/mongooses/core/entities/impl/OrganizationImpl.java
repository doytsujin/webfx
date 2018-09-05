package mongooses.core.entities.impl;

import mongooses.core.entities.Organization;
import webfx.framework.orm.entity.EntityId;
import webfx.framework.orm.entity.EntityStore;
import webfx.framework.orm.entity.impl.DynamicEntity;

/**
 * @author Bruno Salmon
 */
public final class OrganizationImpl extends DynamicEntity implements Organization {

    public OrganizationImpl(EntityId id, EntityStore store) {
        super(id, store);
    }
}