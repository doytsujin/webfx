package naga.framework.activity.client;

import naga.framework.orm.domainmodel.DataSourceModel;
import naga.framework.ui.i18n.I18n;

/**
 * @author Bruno Salmon
 */
public interface UiDomainApplicationContext<C extends UiDomainApplicationContext<C>> extends UiApplicationContext<C>, UiDomainActivityContext<C> {

    static UiDomainApplicationContext<?> create(String[] mainArgs) {
        return new UiDomainApplicationContextImpl(mainArgs, UiDomainActivityContext::create);
    }

    static UiDomainApplicationContext<?> create(DataSourceModel dataSourceModel, String[] mainArgs) {
        return create(mainArgs).setDataSourceModel(dataSourceModel).setI18n(I18n.create("mongoose/dictionaries/{lang}.json"));
    }

}
