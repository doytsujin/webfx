package mongoose.activities.shared.logic.work;

import mongoose.activities.shared.book.event.shared.FeesGroup;
import mongoose.activities.shared.book.event.shared.FeesGroupBuilder;
import mongoose.activities.shared.logic.time.DayTimeRange;
import mongoose.activities.shared.logic.time.DaysArrayBuilder;
import mongoose.activities.shared.logic.time.TimeInterval;
import mongoose.entities.DateInfo;
import mongoose.entities.Event;
import mongoose.entities.Option;
import mongoose.services.EventService;
import naga.framework.orm.entity.EntityList;
import naga.util.Numbers;
import naga.util.collection.Collections;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Bruno Salmon
 */
public class BusinessRules {

    // External entry points

    public static FeesGroup[] createFeesGroups(EventService eventService) {
        List<FeesGroup> feesGroups = new ArrayList<>();
        EntityList<DateInfo> dateInfos = eventService.getEventDateInfos();
        List<Option> defaultOptions = selectDefaultOptions(eventService);
        List<Option> accommodationOptions = eventService.selectOptions(o -> o.isConcrete() && o.isAccommodation());
        if (!dateInfos.isEmpty())
            for (DateInfo dateInfo : dateInfos)
                populateFeesGroups(eventService, dateInfo, defaultOptions, accommodationOptions, feesGroups);
        else if (eventService.getEvent() != null) // May happen if event is empty (ie has no option)
            populateFeesGroups(eventService, null, defaultOptions, accommodationOptions, feesGroups);
        return Collections.toArray(feesGroups, FeesGroup[]::new);
    }

    static void applyBusinessRules(WorkingDocument workingDocument) {
        applyBreakfastRule(workingDocument);
        applyDietRule(workingDocument);
        applyTouristTaxRule(workingDocument);
        applyTranslationRule(workingDocument);
    }

    // Private implementation

    private static boolean isOptionManagedByBusinessRules(Option o) {
        return isBreakfastOption(o) || o.isDiet() || isTouristTaxOption(o) || o.isTranslation();
    }

    private static void populateFeesGroups(EventService eventService, DateInfo dateInfo, List<Option> defaultOptions, List<Option> accommodationOptions, List<FeesGroup> feesGroups) {
        feesGroups.add(createFeesGroup(eventService, dateInfo, defaultOptions, accommodationOptions));
    }

    private static FeesGroup createFeesGroup(EventService eventService, DateInfo dateInfo, List<Option> defaultOptions, List<Option> accommodationOptions) {
        return new FeesGroupBuilder(eventService)
                .setDateInfo(dateInfo)
                .setDefaultOptions(defaultOptions)
                .setAccommodationOptions(accommodationOptions)
                .build();
    }

    private static void applyBreakfastRule(WorkingDocument wd) {
        if (!wd.hasAccommodation() || !hasMeals(wd))
            wd.getWorkingDocumentLines().remove(wd.getBreakfastLine());
        else if (!wd.hasBreakfast()) {
            Option breakfastOption = getBreakfastOption(wd.getEventService());
            if (breakfastOption != null)
                wd.setBreakfastLine(wd.addNewDependantLine(breakfastOption, wd.getAccommodationLine(), 1));
        }
    }

    private static void applyDietRule(WorkingDocument wd) {
        if (!hasMeals(wd))
                wd.getWorkingDocumentLines().remove(wd.getDietLine());
        else {
            WorkingDocumentLine dietLine = wd.getDietLine();
            if (dietLine == null) {
                Option dietOption = getDefaultDietOption(wd.getEventService());
                if (dietOption == null)
                    return;
                dietLine = new WorkingDocumentLine(dietOption, wd);
                wd.getWorkingDocumentLines().add(dietLine);
            }
            DaysArrayBuilder dab = new DaysArrayBuilder();
            if (wd.hasLunch())
                dab.addSeries(wd.getLunchLine().getDaysArray().toSeries(), null);
            if (wd.hasSupper())
                dab.addSeries(wd.getSupperLine().getDaysArray().toSeries(), null);
            dietLine.setDaysArray(dab.build());
        }
    }

    private static void applyTouristTaxRule(WorkingDocument wd) {
        if (!wd.hasAccommodation())
            wd.getWorkingDocumentLines().remove(wd.getTouristTaxLine());
        else if (!wd.hasTouristTax()) {
            Option touristTaxOption = wd.getEventService().findFirstOption(o -> isTouristTaxOption(o) && (o.getParent() == null || wd.getAccommodationLine() != null && o.getParent().getItem() == wd.getAccommodationLine().getItem()));
            if (touristTaxOption != null)
                wd.setTouristTaxLine(wd.addNewDependantLine(touristTaxOption, wd.getAccommodationLine(), 0));
        }
    }

    private static void applyTranslationRule(WorkingDocument wd) {
        if (!wd.hasTeaching())
            wd.getWorkingDocumentLines().remove(wd.getTranslationLine());
        else if (wd.hasTranslation())
            wd.applySameAttendances(wd.getTranslationLine(), wd.getTeachingLine(), 0);
    }

    private static boolean hasMeals(WorkingDocument wd) {
        return wd.hasLunch() || wd.hasSupper();
    }

    private static List<Option> selectDefaultOptions(EventService eventService) {
        return eventService.selectOptions(o -> isOptionIncludedByDefault(o, eventService));
    }

    private static boolean areMealsIncludedByDefault(EventService eventService) {
        // Answer: yes except for day courses, public talks and International Festivals
        Event event = eventService.getEvent();
        String eventName = event.getName();
        return !eventName.contains("Day Course")
                && !eventName.contains("Public Talk")
                && Numbers.toInteger(event.getOrganizationId().getPrimaryKey()) != 1;
    }

    private static boolean isOptionIncludedByDefault(Option o, EventService eventService) {
        return (o.isConcrete() || o.hasItem() && o.hasTimeRange()/* Ex: Prayers -> to include in the working document so it is displayed in the calendar*/ )
                && !isOptionManagedByBusinessRules(o)
                && (o.isTeaching() || (o.isMeals() ? areMealsIncludedByDefault(eventService) : o.isObligatory()))
                ;
    }

    static boolean isBreakfastOption(Option option) {
        return isMealsOptionInDayTimeRange(option, 0, 10 * 60);
    }

    static boolean isLunchOption(Option option) {
        return isMealsOptionInDayTimeRange(option, 10 * 60, 15 * 60);
    }

    static boolean isSupperOption(Option option) {
        return isMealsOptionInDayTimeRange(option,15 * 60, 24 * 60);
    }

    private static boolean isMealsOptionInDayTimeRange(Option option, long startMinutes, long endMinutes) {
        if (!option.isMeals())
            return false;
        DayTimeRange dayTimeRange = option.getParsedTimeRangeOrParent();
        if (dayTimeRange == null)
            return false;
        TimeInterval dayTimeInterval = dayTimeRange.getDayTimeInterval(0, TimeUnit.DAYS);
        return dayTimeInterval.getIncludedStart() >= startMinutes && dayTimeInterval.getExcludedEnd() < endMinutes;
    }

    static boolean isTouristTaxOption(Option option) {
        return option.isTax(); // The only tax for now is the tourist tax
    }

    private static Option getBreakfastOption(EventService eventService) {
        Option breakfastOption = eventService.getBreakfastOption();
        if (breakfastOption == null)
            eventService.setBreakfastOption(breakfastOption = eventService.findFirstConcreteOption(BusinessRules::isBreakfastOption));
        return breakfastOption;
    }

    private static Option getDefaultDietOption(EventService eventService) {
        Option defaultDietOption = eventService.getDefaultDietOption();
        // If meals are included by default, then we return a default diet option (the first proposed one) which will be
        // automatically selected as initial choice
        if (defaultDietOption == null && areMealsIncludedByDefault(eventService))
            eventService.setDefaultDietOption(defaultDietOption = eventService.findFirstConcreteOption(Option::isDiet));
        // If meals are not included by default, we don't return a default diet option so bookers will need to
        // explicitly select the diet option when ticking meals (the diet option will initially be blank)
        return defaultDietOption;
    }
}