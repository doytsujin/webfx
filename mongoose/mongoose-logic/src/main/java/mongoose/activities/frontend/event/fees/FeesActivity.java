package mongoose.activities.frontend.event.fees;

import javafx.beans.property.Property;
import mongoose.activities.frontend.event.shared.BookingProcessActivity;
import mongoose.activities.frontend.event.shared.FeesGroup;
import mongoose.activities.shared.logic.preselection.OptionsPreselection;
import mongoose.entities.Person;
import mongoose.services.PersonService;
import naga.commons.type.SpecializedTextType;
import naga.commons.util.Booleans;
import naga.commons.util.tuples.Pair;
import naga.framework.ui.i18n.I18n;
import naga.framework.ui.rx.RxScheduler;
import naga.framework.ui.rx.RxUi;
import naga.platform.json.Json;
import naga.platform.json.spi.JsonObject;
import naga.platform.json.spi.WritableJsonObject;
import naga.platform.spi.Platform;
import naga.toolkit.display.DisplayColumn;
import naga.toolkit.display.DisplayResultSet;
import naga.toolkit.display.DisplayResultSetBuilder;
import naga.toolkit.spi.Toolkit;
import naga.toolkit.spi.events.ActionEvent;
import naga.toolkit.spi.nodes.GuiNode;
import naga.toolkit.spi.nodes.controls.RadioButton;
import naga.toolkit.util.Properties;
import rx.Observable;

/**
 * @author Bruno Salmon
 */
public class FeesActivity extends BookingProcessActivity<FeesViewModel, FeesPresentationModel> {

    public FeesActivity() {
        super(FeesPresentationModel::new, "options");
        registerViewBuilder(getClass(), new FeesViewModelBuilder());
    }

    @Override
    protected void bindViewModelWithPresentationModel(FeesViewModel vm, FeesPresentationModel pm) {
        super.bindViewModelWithPresentationModel(vm, pm);
        I18n i18n = getI18n();
        i18n.translateText(setImage(vm.getProgramButton(), "{url: 'images/calendar.svg', width: 16, height: 16}"), "Program")
                .actionEventObservable().subscribe(this::onProgramButtonPressed);
        i18n.translateText(setImage(vm.getTermsButton(), "{url: 'images/certificate.svg', width: 16, height: 16}"), "TermsAndConditions")
                .actionEventObservable().subscribe(this::onTermsButtonPressed);
        vm.getDateInfoCollator().displayResultSetProperty().bind(pm.dateInfoDisplayResultSetProperty());
    }

    private void onProgramButtonPressed(ActionEvent actionEvent) {
        goToNextBookingProcessPage("program");
    }

    private void onTermsButtonPressed(ActionEvent actionEvent) {
        goToNextBookingProcessPage("terms");
    }

    protected void bindPresentationModelWithLogic(FeesPresentationModel pm) {
        // Load and display fees groups now but also on event change
        Properties.runNowAndOnPropertiesChange(property -> loadAndDisplayFeesGroups(pm), pm.eventIdProperty());
    }

    private void loadAndDisplayFeesGroups(FeesPresentationModel pm) {
        onFeesGroup().setHandler(async -> {
            if (async.failed())
                Platform.log(async.cause());
            else {
                FeesGroup[] feesGroups = async.result();
                Property<DisplayResultSet> dateInfoDisplayResultSetProperty = pm.dateInfoDisplayResultSetProperty();
                I18n i18n = getI18n();
                Observable.combineLatest(
                        RxUi.observe(i18n.dictionaryProperty()),
                        RxUi.observe(activeProperty()),
                        (dictionary, active) -> active)
                        .filter(active -> active)
                        .observeOn(RxScheduler.UI_SCHEDULER)
                        .subscribe(active -> displayFeesGroups(feesGroups, dateInfoDisplayResultSetProperty));
                onEventAvailabilities().setHandler(ar -> {
                    if (ar.succeeded())
                        Toolkit.get().scheduler().runInUiThread(() -> displayFeesGroups(feesGroups, dateInfoDisplayResultSetProperty));
                });
            }
        });
    }

    private void displayFeesGroups(FeesGroup[] feesGroups, Property<DisplayResultSet> dateInfoDisplayResultSetProperty) {
        int n = feesGroups.length;
        DisplayResultSetBuilder rsb = DisplayResultSetBuilder.create(n, new DisplayColumn[]{
                DisplayColumn.create(value -> renderFeesGroupHeader((Pair<JsonObject, String>) value, feesGroups, dateInfoDisplayResultSetProperty)),
                DisplayColumn.create(value -> renderFeesGroupBody((DisplayResultSet) value)),
                DisplayColumn.create(null, SpecializedTextType.HTML)});
        I18n i18n = getI18n();
        WritableJsonObject jsonImage = Json.parseObject("{url: 'images/price-tag.svg', width: 16, height: 16}");
        for (int i = 0; i < n; i++) {
            FeesGroup feesGroup = feesGroups[i];
            rsb.setValue(i, 0, new Pair<>(jsonImage, feesGroup.getDisplayName(i18n)));
            rsb.setValue(i, 1, feesGroup.generateDisplayResultSet(i18n, this, this::onBookButtonPressed));
            if (i == n - 1) // Showing the fees bottom text only on the last fees group
                rsb.setValue(i, 2, feesGroup.getFeesBottomText(i18n));
        }
        DisplayResultSet rs = rsb.build();
        dateInfoDisplayResultSetProperty.setValue(rs);
    }

    private GuiNode renderFeesGroupHeader(Pair<JsonObject, String> pair, FeesGroup[] feesGroups, Property<DisplayResultSet> dateInfoDisplayResultSetProperty) {
        Toolkit toolkit = Toolkit.get();
        I18n i18n = getI18n();
        boolean hasUnemployedRate = hasUnemployedRate();
        boolean hasFacilityFeeRate = hasFacilityFeeRate();
        boolean hasDiscountRates = hasUnemployedRate || hasFacilityFeeRate;
        RadioButton noDiscountRadio  = hasDiscountRates ?   i18n.instantTranslateText(toolkit.createRadioButton(), "NoDiscount") : null;
        RadioButton unemployedRadio  = hasUnemployedRate ?  i18n.instantTranslateText(toolkit.createRadioButton(), "UnemployedDiscount") : null;
        RadioButton facilityFeeRadio = hasFacilityFeeRate ? i18n.instantTranslateText(toolkit.createRadioButton(), "FacilityFeeDiscount") : null;
        PersonService personService = PersonService.get(getDataSourceModel());
        Person person = personService.getPreselectionProfilePerson();
        if (unemployedRadio != null) {
            unemployedRadio.setSelected(Booleans.isTrue(person.isUnemployed()));
            unemployedRadio.selectedProperty().addListener((observable, oldValue, unemployed) -> {
                person.setUnemployed(unemployed);
                if (unemployed)
                    person.setFacilityFee(false);
                displayFeesGroups(feesGroups, dateInfoDisplayResultSetProperty);
            });
        }
        if (facilityFeeRadio != null) {
            facilityFeeRadio.setSelected(Booleans.isTrue(person.isFacilityFee()));
            facilityFeeRadio.selectedProperty().addListener((observable, oldValue, facilityFee) -> {
                person.setFacilityFee(facilityFee);
                if (facilityFee)
                    person.setUnemployed(false);
                displayFeesGroups(feesGroups, dateInfoDisplayResultSetProperty);
            });
        }
        if (noDiscountRadio != null) {
            noDiscountRadio.setSelected(Booleans.isNotTrue(person.isUnemployed()) && Booleans.isNotTrue(person.isFacilityFee()));
            noDiscountRadio.selectedProperty().addListener((observable, oldValue, noDiscount) -> {
                if (noDiscount) {
                    person.setUnemployed(false);
                    person.setFacilityFee(false);
                }
                displayFeesGroups(feesGroups, dateInfoDisplayResultSetProperty);
            });
        }
        return toolkit.createHBox(toolkit.createImage(pair.get1()), toolkit.createTextView(pair.get2()), noDiscountRadio, unemployedRadio, facilityFeeRadio);
    }

    private GuiNode renderFeesGroupBody(DisplayResultSet rs) {
        return Toolkit.get().createTable(rs);
    }

    private void onBookButtonPressed(OptionsPreselection optionsPreselection) {
        setWorkingDocument(optionsPreselection.getWorkingDocument());
        onNextButtonPressed(null);
    }
}
