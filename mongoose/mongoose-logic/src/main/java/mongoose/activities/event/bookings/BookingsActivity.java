package mongoose.activities.event.bookings;

import naga.framework.expression.Expression;
import naga.framework.expression.terms.function.java.AbcNames;
import naga.toolkit.spi.Toolkit;
import naga.toolkit.spi.nodes.controls.CheckBox;
import naga.toolkit.spi.nodes.controls.SearchBox;
import naga.toolkit.spi.nodes.controls.Table;
import naga.framework.ui.presentation.PresentationActivity;
import naga.commons.util.Strings;

/**
 * @author Bruno Salmon
 */
public class BookingsActivity extends PresentationActivity<BookingsViewModel, BookingsPresentationModel> {

    public BookingsActivity() {
        super(BookingsPresentationModel::new);
    }

    protected BookingsViewModel buildView(Toolkit toolkit) {
        // Building the UI components
        SearchBox searchBox = toolkit.createSearchBox();
        Table table = toolkit.createTable();
        CheckBox limitCheckBox = toolkit.createCheckBox();

        return new BookingsViewModel(toolkit.createVPage()
                .setHeader(searchBox)
                .setCenter(table)
                .setFooter(limitCheckBox)
                , searchBox, table, limitCheckBox);
    }

    protected void bindViewModelWithPresentationModel(BookingsViewModel vm, BookingsPresentationModel pm) {
        // Hard coded initialization
        SearchBox searchBox = vm.getSearchBox();
        CheckBox limitCheckBox = vm.getLimitCheckBox();
        searchBox.setPlaceholder("Search here to narrow the list");
        searchBox.requestFocus();
        limitCheckBox.setText("Limit to 100");

        // Initialization from the presentation model current state
        searchBox.setText(pm.searchTextProperty().getValue());
        limitCheckBox.setSelected(pm.limitProperty().getValue());

        // Binding the UI with the presentation model for further state changes
        // User inputs: the UI state changes are transferred in the presentation model
        pm.searchTextProperty().bind(searchBox.textProperty());
        pm.limitProperty().bind(limitCheckBox.selectedProperty());
        pm.bookingsDisplaySelectionProperty().bind(vm.getTable().displaySelectionProperty());
        // User outputs: the presentation model changes are transferred in the UI
        vm.getTable().displayResultSetProperty().bind(pm.bookingsDisplayResultSetProperty());
    }

    @Override
    protected void initializePresentationModel(BookingsPresentationModel pm) {
        pm.eventIdProperty().setValue(getParameter("eventId"));
    }


    protected void bindPresentationModelWithLogic(BookingsPresentationModel pm) {
        // Loading the domain model and setting up the reactive filter
        createReactiveExpressionFilter("{class: 'Document', fields: 'cart.uuid', where: '!cancelled', orderBy: 'ref desc'}")
                // Condition
                .combine(pm.eventIdProperty(), s -> "{where: 'event=" + s + "'}")
                // Search box condition
                .combine(pm.searchTextProperty(), s -> {
                    if (Strings.isEmpty(s))
                        return null;
                    s = s.trim();
                    if (Character.isDigit(s.charAt(0)))
                        return "{where: 'ref = " + s + "'}";
                    if (s.contains("@"))
                        return "{where: 'lower(person_email) like `%" + s.toLowerCase() + "%`'}";
                    return "{where: 'person_abcNames like `" + AbcNames.evaluate(s, true) + "`'}";
                })
                // Limit condition
                .combine(pm.limitProperty(), "{limit: '100'}")
                .setExpressionColumns("[" +
                        "'ref'," +
                        "'multipleBookingIcon','countryOrLangIcon','genderIcon'," +
                        "'person_firstName'," +
                        "'person_lastName'," +
                        "'person_age','noteIcon'," +
                        "{expression: 'price_net', format: 'price'}," +
                        "{expression: 'price_minDeposit', format: 'price'}," +
                        "{expression: 'price_deposit', format: 'price'}," +
                        "{expression: 'price_balance', format: 'price'}" +
                        "]")
                .applyDomainModelRowStyle()
                .displayResultSetInto(pm.bookingsDisplayResultSetProperty())
                .setSelectedEntityHandler(pm.bookingsDisplaySelectionProperty(), document -> {
                    if (document != null) {
                        Expression cartUuidExpression = getDataSourceModel().getDomainModel().parseExpression("cart.uuid", "Document");
                        Object cartUuid = document.evaluate(cartUuidExpression);
                        if (cartUuid != null)
                            getHistory().push("/cart/" + cartUuid);
                    }
                });
    }
}
