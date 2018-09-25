package mongooses.core.sharedends.activities.generic.eventdependent;

import mongooses.core.sharedends.aggregates.EventAggregate;
import mongooses.core.sharedends.aggregates.EventAggregateMixin;
import webfx.framework.activity.impl.elementals.domain.DomainActivityContext;
import webfx.framework.activity.impl.elementals.domain.DomainActivityContextMixin;
import webfx.framework.activity.impl.elementals.uiroute.UiRouteActivityContext;
import webfx.framework.activity.impl.elementals.uiroute.UiRouteActivityContextMixin;

/**
 * @author Bruno Salmon
 */
public interface EventDependentActivityMixin
        <C extends DomainActivityContext<C> & UiRouteActivityContext<C>>

        extends UiRouteActivityContextMixin<C>,
        DomainActivityContextMixin<C>,
        EventAggregateMixin,
        EventDependentPresentationModelMixin
{

    default EventAggregate getEventService() {
        return EventAggregate.getOrCreate(getEventId(), getDataSourceModel());
    }

    default void updateEventDependentPresentationModelFromContextParameters() {
        setEventId(getParameter("eventId"));
        setOrganizationId(getParameter("organizationId"));
    }

}