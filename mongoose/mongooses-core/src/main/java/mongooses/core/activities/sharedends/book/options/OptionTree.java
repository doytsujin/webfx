package mongooses.core.activities.sharedends.book.options;

import javafx.scene.Node;
import mongooses.core.activities.sharedends.book.shared.TranslateFunction;
import mongooses.core.activities.sharedends.logic.ui.validation.MongooseValidationSupport;
import mongooses.core.activities.sharedends.logic.work.WorkingDocument;
import mongooses.core.activities.sharedends.logic.work.transaction.WorkingDocumentTransaction;
import mongooses.core.entities.Event;
import mongooses.core.entities.Option;
import webfx.fxkits.core.spi.FxKit;
import webfx.platforms.core.util.collection.Collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bruno Salmon
 */
public final class OptionTree {

    private final OptionsActivity activity;
    private Event event;
    private List<Option> topLevelOptions;
    private WorkingDocumentTransaction workingDocumentTransaction;
    private final MongooseValidationSupport validationSupport = new MongooseValidationSupport();

    OptionTree(OptionsActivity activity) {
        this.activity = activity;
        new TranslateFunction<Option>("translateOption") {
            @Override
            protected String translate(Option option) {
                String optionTranslation = bestTranslationOrName(option);
                boolean multiSite = option.getParent() != null && option.getParent().getSite() == null;
                if (multiSite)
                    optionTranslation = bestTranslationOrName(option.getSite()) + " - " + optionTranslation;
                return optionTranslation;
            }
        }.register();
    }

    OptionsActivity getActivity() {
        return activity;
    }

    WorkingDocument getWorkingDocument() {
        return activity.getWorkingDocument();
    }

    WorkingDocumentTransaction getWorkingDocumentTransaction() {
        WorkingDocument workingDocument = getWorkingDocument();
        workingDocument.setOptionTree(this);
        if (workingDocumentTransaction == null || workingDocumentTransaction.getWorkingDocument() != workingDocument)
            workingDocumentTransaction = new WorkingDocumentTransaction(workingDocument);
        return workingDocumentTransaction;
    }

    MongooseValidationSupport getValidationSupport() {
        return validationSupport;
    }

    private void clearDataOnEventChange() {
        Event currentEvent = activity.getEvent();
        if (this.event != currentEvent) {
            topLevelOptions = null;
            this.event = currentEvent;
        }
    }

    private List<Option> getTopLevelOptions() {
        clearDataOnEventChange();
        if (topLevelOptions == null)
            topLevelOptions = Collections.filter(activity.getEventOptions(), Option::hasNoParent);
        return topLevelOptions;
    }

    private List<Option> getTopLevelNonObligatoryOptions() {
        return Collections.filter(getTopLevelOptions(), Option::isNotObligatory);
    }

    List<Node> getUpdatedTopLevelOptionButtons() {
        return Collections.map(getTopLevelNonObligatoryOptions(), this::getUpdatedTopLevelOptionButton);
    }

    List<Node> getUpdatedTopLevelOptionSections() {
        return Collections.map(getTopLevelOptions(), this::getUpdatedTopLevelOptionSection);
    }

    private Node getUpdatedTopLevelOptionButton(Option o) {
        return getOptionTreeNode(o).createOrUpdateTopLevelOptionButtonFromModel();
    }

    private Node getUpdatedTopLevelOptionSection(Option o) {
        return getOptionTreeNode(o).createOrUpdateTopLevelOptionSectionFromModel();
    }

    private Map<Option, OptionTreeNode> optionTreeNodes = new HashMap<>();

    private OptionTreeNode getOptionTreeNode(Option option) {
        OptionTreeNode optionTreeNode = optionTreeNodes.get(option);
        if (optionTreeNode == null)
            optionTreeNode = new OptionTreeNode(option, this);
        return optionTreeNode;
    }

    void registerOptionTreeNode(OptionTreeNode optionTreeNode) { // Called by OptionTreeNode constructor
        optionTreeNodes.put(optionTreeNode.getOption(), optionTreeNode);
    }

    public boolean isOptionSelected(Option option) {
        OptionTreeNode optionTreeNode = optionTreeNodes.get(option);
        return optionTreeNode != null && (optionTreeNode.isOptionSelectedInModel() || optionTreeNode.isUiOptionSelected(true));
    }

    private boolean pendingTransactionCommitAndUiSync;

    void deferTransactionCommitAndUiSync() {
        if (!pendingTransactionCommitAndUiSync) {
            pendingTransactionCommitAndUiSync = true;
            FxKit.get().scheduler().scheduleDeferred(() -> {
                getWorkingDocumentTransaction().commit();
                getActivity().createOrUpdateOptionPanelsIfReady(true);
                pendingTransactionCommitAndUiSync = false;
            });
        }
    }

    void reset() {
        Collections.forEach(optionTreeNodes.values(), OptionTreeNode::reset);
    }
}