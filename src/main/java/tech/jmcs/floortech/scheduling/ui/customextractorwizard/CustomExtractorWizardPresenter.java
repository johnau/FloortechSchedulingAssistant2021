package tech.jmcs.floortech.scheduling.ui.customextractorwizard;

import com.airhacks.afterburner.views.FXMLView;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.columndescriptorspage.ColumnDescriptorsView;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.datasourcepage.DataSourceView;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.finishpage.FinishPageView;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.namepage.NamePageView;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.recordvalidation.RecordValidationView;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.requiredrecorddata.RequiredRecordDataView;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.startpage.StartPageView;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.tablelayout.TableLayoutView;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.valuecalculationpage.ValueCalculationPresenter;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.valuecalculationpage.ValueCalculationView;

import javax.inject.Inject;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CustomExtractorWizardPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(CustomExtractorWizardPresenter.class);

    @FXML private BorderPane pageBorderPane;

    @FXML private Button cancelButton;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button finishButton;

    @Inject private StartPageView startPageView;
    @Inject private NamePageView namePageView;
    @Inject private TableLayoutView tableLayoutView;
    @Inject private ColumnDescriptorsView columnDescriptorsView;
    @Inject private RequiredRecordDataView requiredRecordDataView;
    @Inject private RecordValidationView recordValidationView;
    @Inject private ValueCalculationView valueCalculationView;
    @Inject private DataSourceView dataSourceView;
    @Inject private FinishPageView finishPageView;

    private HashMap<Integer, FXMLView> views;
    private Integer currentPage = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.pageBorderPane.sceneProperty().addListener(exitListener);

        this.views = new HashMap<>();
        views.put(1, startPageView);
        views.put(2, namePageView);
        views.put(3, tableLayoutView);
        views.put(4, columnDescriptorsView);
        views.put(5, requiredRecordDataView);
        views.put(6, recordValidationView);
        views.put(7, valueCalculationView);
        views.put(8, dataSourceView);
        views.put(9, finishPageView);

        begin();
    }

    // need to check that we can have multiple pages here and they can all get created and injected

    private void begin() {
        this.pageBorderPane.setCenter(views.get(1).getView());
        this.currentPage = 1;
        this.showFirstPageButtons();
    }

    @FXML public void handleFinishButtonAction(ActionEvent event) {
        resetWizard();


        // complete the creation of the new custom extractor here

    }

    @FXML public void handleNextButtonAction(ActionEvent event) {
        try {
            triggerView();
        } catch (Exception e) {
            LOG.debug("Exception thrown: {} \n{}", e.getMessage(), e.getStackTrace());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Can not continue");
            alert.setHeaderText("There is a problem with provided data");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        updateNextView();
        incrementView();
    }

    @FXML public void handlePreviousButtonAction(ActionEvent event) {
        --currentPage;
        if (views.get(currentPage) == null) {
            LOG.error("Page not found in custom extractor wizard");
            return;
        } else {
            this.pageBorderPane.setCenter(views.get(currentPage).getView());

            if (views.size() == currentPage) {
                this.showLastPageButtons();
            } else if (currentPage == 1) {
                this.showFirstPageButtons();
            } else {
                this.showMiddlePagesButtons();
            }
        }
    }

    private void triggerView() throws Exception {
        FXMLView oldView = views.get(currentPage);
        try {
            ((TriggeredFXMLView) oldView.getPresenter()).trigger();
        } catch (Exception e) {
            throw e;
        }
    }

    private void incrementView() {
        ++currentPage;
        if (views.get(currentPage) == null) {
            LOG.error("Page not found in custom extractor wizard");
            return;
        } else {
            this.pageBorderPane.setCenter(views.get(currentPage).getView());

            if (views.size() == currentPage) {
                this.showLastPageButtons();
            } else {
                this.showMiddlePagesButtons();
            }
        }
    }

    private void updateNextView() {
        if (views.get(currentPage+1) == null) {
            return;
        }

        ((TriggeredFXMLView) views.get(currentPage+1).getPresenter()).update();
    }

    @FXML public void handleCancelButtonAction(ActionEvent event) {
        if (userConfirmCancel()) {
            resetWizard();
            ((Stage) this.pageBorderPane.getScene().getWindow()).close();
        }
    }

    private boolean userConfirmCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        alert.setTitle("Exit Wizard?");
        alert.setHeaderText("Are you sure you want to exit the wizard?");
        alert.setContentText("All changes will be lost");
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void resetWizard() {
        // reset all the forms
        this.views.forEach( (i, view) -> {
            ((TriggeredFXMLView) view.getPresenter()).reset();
        });
        begin();
    }

    /**
     * Show the button combination: Next button and Cancel button
     */
    private void showFirstPageButtons() {
        HBox buttonContainer = (HBox) this.cancelButton.getParent();
        buttonContainer.getChildren().clear();
        buttonContainer.getChildren().add(this.cancelButton);
        buttonContainer.getChildren().add(this.nextButton);
    }

    /**
     * Show the button combination: Finish button and Cancel button
     */
    private void showLastPageButtons() {
        HBox buttonContainer = (HBox) this.cancelButton.getParent();
        buttonContainer.getChildren().clear();
        buttonContainer.getChildren().add(this.cancelButton);
        buttonContainer.getChildren().add(this.previousButton);
        buttonContainer.getChildren().add(this.finishButton);
    }

    /**
     * Show the button combination: Previous, Next, and Cancel buttons
     */
    private void showMiddlePagesButtons() {
        HBox buttonContainer = (HBox) this.cancelButton.getParent();
        buttonContainer.getChildren().clear();
        buttonContainer.getChildren().add(this.cancelButton);
        buttonContainer.getChildren().add(this.previousButton);
        buttonContainer.getChildren().add(this.nextButton);
    }

    private ChangeListener<Scene> exitListener = (ChangeListener<Scene>) (observable, oldScene, newScene) -> {
        if (oldScene == null && newScene != null) {
            // scene is set for the first time. Now its the time to listen stage changes.
            newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                if (oldWindow == null && newWindow != null) {
                    // stage is set. now is the right time to do whatever we need to the stage in the controller.
                    ((Stage) newWindow).setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            if (userConfirmCancel()) {
                                resetWizard();
                            } else {
                                event.consume();
                            }
                        }
                    });
                }
            });
        }
    };

}
