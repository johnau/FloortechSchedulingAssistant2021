package tech.jmcs.floortech.scheduling.ui.collisions;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.util.ExcelCellAddress;
import tech.jmcs.floortech.scheduling.ui.CommitErrors;

import javax.inject.Inject;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CollisionsPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(CollisionsPresenter.class);

    @Inject private CommitErrors commitErrors;

    @FXML private VBox conflictsVbox;
    @FXML private VBox missingEntriesVbox;
    @FXML private VBox otherErrorsVbox;
    @FXML private Button closeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populate();
        this.closeButton.sceneProperty().addListener(exitCollisionsListener);
    }

    private void populate() {
        if (this.commitErrors.getConfirmer() == null) {
            // no errors, nothing to show.
            return;
        }
        populateConflicts();
        populateMissing();
        populateUpdateErrors();
    }

    private void populateUpdateErrors() {
        List<String> updateErrors = commitErrors.getConfirmer().getUpdateErrors();

        if (updateErrors.isEmpty()) {
            return;
        }

        this.otherErrorsVbox.getChildren().clear();
        this.otherErrorsVbox.getChildren().add(new Text(String.format("There are %s other errors:", updateErrors.size())));
        for (String updateError : updateErrors) {
            Text t = new Text(updateError);
            t.setStyle("-fx-fill: red; -fx-font-weight: italics;");
            this.otherErrorsVbox.getChildren().add(t);
        }
    }

    private void populateMissing() {
        Map<String, Object> notFoundProblems = commitErrors.getConfirmer().getNotFoundProblems();

        if (notFoundProblems.isEmpty()) {
            return;
        }

        this.missingEntriesVbox.getChildren().clear();
        this.missingEntriesVbox.getChildren().add(new Text(String.format("There are %s missing entries:", notFoundProblems.size())));
        for (Map.Entry<String, Object> entry : notFoundProblems.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            HBox row = new HBox();
            HBox containerLeft = new HBox();
            HBox.setHgrow(containerLeft, Priority.ALWAYS);
            Text tn = new Text(name);
            tn.setStyle("-fx-fill: black; -fx-font-weight: bold;");
            Text t = new Text("\t Value: ");
            Text tv = new Text(value.toString());
            tv.setStyle("-fx-fill: green; -fx-font-weight: bold;");
            containerLeft.getChildren().addAll(tn, t, tv);

            HBox containerRight = new HBox();
            containerRight.setPadding(new Insets(5 ,5 ,5 ,5));
            containerRight.setSpacing(5d);
            Button addButton = new Button("Add");
            Button replaceButton = new Button("Overwrite");

            addButton.setOnAction(event -> {
                // present the user with a list of all the rows in the schedule with their address next to them as index.
                // user can select and then it is converted to ExcelCellAddress where it can be used below.
                ExcelCellAddress selected = chooseEntryDialog("* Note: New row will be inserted below your chosen entry");
                if (selected != null) {
                    // break selected down into address and name.
                    this.commitErrors.getConfirmer().forceOverwrite_addNewCell(name, value, selected);
                    containerLeft.getChildren().remove(t);
                    tn.setStyle("-fx-fill: grey; -fx-font-weight: regular;");
                    tv.setStyle("-fx-fill: grey; -fx-font-weight: regular;");
                    Text updated = new Text("\t\t[ Added! ]");
                    updated.setStyle("-fx-fill: green; -fx-font-weight: bold;");
                    containerLeft.getChildren().add(updated);

                    replaceButton.setDisable(true);
                    addButton.setDisable(true);
                }
            });

            replaceButton.setOnAction(event -> {
                ExcelCellAddress selected = chooseEntryDialog("* Warning: Chosen entry name and value will be replaced!");
                if (selected != null) {
                    this.commitErrors.getConfirmer().forceOverwrite_replaceCell(name, value, selected);
                    containerLeft.getChildren().remove(t);
                    tn.setStyle("-fx-fill: grey; -fx-font-weight: regular;");
                    tv.setStyle("-fx-fill: grey; -fx-font-weight: regular;");
                    Text updated = new Text("\t\t[ Overwritten! ]");
                    updated.setStyle("-fx-fill: green; -fx-font-weight: bold;");
                    containerLeft.getChildren().add(updated);

                    replaceButton.setDisable(true);
                    addButton.setDisable(true);
                }
            });
            containerRight.getChildren().addAll(replaceButton, addButton);

            row.getChildren().addAll(containerLeft, containerRight);
            this.missingEntriesVbox.getChildren().add(row);
        }
    }

    private ExcelCellAddress chooseEntryDialog(String message) {
        AtomicReference<String> result = new AtomicReference<>("");

        List<String> choiceList = this.commitErrors.getConfirmer().getAllScheduleEntryNames();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10, 10, 10, 10));

        Label chooseLabel = new Label("Choose Schedule Entry:");
        ComboBox<String> choices = new ComboBox<>();
        choices.setPrefWidth(150d);
        choices.getItems().addAll(choiceList);
        choices.setPadding(new Insets(10, 10, 10, 10));
        Label belowNote = new Label(message);
        Label entriesLabel = new Label("Entries: ");
        entriesLabel.setPrefWidth(70d);
        Button okButton = new Button("OK");
        okButton.setPrefWidth(50d);
        okButton.setOnAction(event -> {
            result.set(choices.getValue());
            ((Stage) okButton.getScene().getWindow()).close();
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(80d);
        cancelButton.setOnAction(event -> {
            ((Stage) cancelButton.getScene().getWindow()).close();
        });
        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(10, 10, 10, 10));
        buttonBox.setSpacing(10d);
        buttonBox.getChildren().addAll(okButton, cancelButton);

        layout.add(chooseLabel, 1, 0);
        layout.add(entriesLabel, 0, 1);
        layout.add(choices, 1, 1);
        layout.add(belowNote, 1, 2);
        layout.add(buttonBox, 1, 3);

        Scene scene = new Scene(layout);
        stage.setTitle("Choose insert row...");
        stage.setScene(scene);
        stage.showAndWait();

        String[] parts = result.get().split("[|]");
        if (parts.length == 2) {
            String a = parts[0].trim();
            String b = parts[1].trim();
            try {
                ExcelCellAddress selectedCell = ExcelCellAddress.fromString(a);
                LOG.debug("Cell address: {} selected", selectedCell.toString());
                return selectedCell;
            } catch (Exception e) {
                LOG.debug("Could not convert cell address: {}", e.getMessage());
                return null;
            }
        }
        LOG.debug("Parts were bad, could not convert cell address");
        return null;
    }

    private void populateConflicts() {
        LOG.debug("Confirmer: {}", this.commitErrors.getConfirmer());
        Map<String, Object> conflictProblems = this.commitErrors.getConfirmer().getConflictProblems();
        Map<String, Object> conflictValues = this.commitErrors.getConfirmer().getConflictValues();

        if (conflictProblems.isEmpty() || conflictValues.isEmpty()) {
            return;
        }
        if (conflictProblems.size() != conflictValues.size()) {
            LOG.warn("There was a mismatch between conflict problems and conflict values");
            return;
        }

        this.conflictsVbox.getChildren().clear();
        this.conflictsVbox.getChildren().add(new Text(String.format("There are %s conflicting entries:", conflictProblems.size())));

        List<EventHandler<ActionEvent>> eventHandlers = new ArrayList<>();

        for (Map.Entry<String, Object> entry : conflictProblems.entrySet()) {
            String name = entry.getKey();
            Object existingValue = entry.getValue();
            Object newValue = conflictValues.get(name);
            LOG.info("Conflict for name: {} | value: {} (existing value: {})", name, newValue, existingValue);

            HBox row = new HBox();
            HBox containerLeft = new HBox();
            HBox.setHgrow(containerLeft, Priority.ALWAYS);
            Text tn = new Text(name + ": ");
            tn.setStyle("-fx-fill: black; -fx-font-weight: bold;");
            Text te = new Text("\tExisting Value: ");
            Text te2 = new Text(existingValue.toString());
            te2.setStyle("-fx-fill: red; -fx-font-weight: bold;");
            Text te3 = new Text(", \tNew Value: ");
            Text te4 = new Text(newValue.toString());
            te4.setStyle("-fx-fill: green; -fx-font-weight: bold;");
            containerLeft.getChildren().addAll(tn, te, te2, te3, te4);

            HBox containerRight = new HBox();
            Button ovwButton = new Button("Overwrite");
            EventHandler<ActionEvent> eventHandler = event -> {
                this.commitErrors.getConfirmer().forceOverwrite_replaceCurrentValue(name, newValue);
                containerLeft.getChildren().removeAll(te, te2, te3);
                tn.setStyle("-fx-fill: grey; -fx-font-weight: regular;");
                te4.setStyle("-fx-fill: grey; -fx-font-weight: regular;");
                Text updated = new Text("\t\t[ Updated! ]");
                updated.setStyle("-fx-fill: green; -fx-font-weight: bold;");
                containerLeft.getChildren().add(updated);

                ovwButton.setDisable(true);
            };
            eventHandlers.add(eventHandler);
            ovwButton.setOnAction(eventHandler);
            containerRight.getChildren().add(ovwButton);
            containerRight.setSpacing(10d);

            row.getChildren().addAll(containerLeft, containerRight);
            this.conflictsVbox.getChildren().add(row);
        }

        HBox popAllRow = new HBox();
        popAllRow.setAlignment(Pos.CENTER_RIGHT);
        Button popAllButton = new Button("Overwrite All");
        popAllButton.setStyle("-fx-font-weight: bold;");
        popAllButton.setOnAction(event -> {
            for (EventHandler<ActionEvent> eventHandler : eventHandlers) {
                eventHandler.handle(null);
            }
        });
        popAllRow.getChildren().add(popAllButton);
        this.conflictsVbox.getChildren().add(popAllRow);
    }

    @FXML public void handleCloseButtonAction(ActionEvent event) {
        this.commitErrors.clear();
        ((Stage) this.closeButton.getScene().getWindow()).close();
    }

    private ChangeListener<Scene> exitCollisionsListener = (ChangeListener<Scene>) (observable, oldScene, newScene) -> {
        if (oldScene == null && newScene != null) {
            // scene is set for the first time. Now its the time to listen stage changes.
            newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                if (oldWindow == null && newWindow != null) {
                    // stage is set. now is the right time to do whatever we need to the stage in the controller.
                    ((Stage)newWindow).setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            commitErrors.clear();
                        }
                    });
                }
            });
        }
    };
}
