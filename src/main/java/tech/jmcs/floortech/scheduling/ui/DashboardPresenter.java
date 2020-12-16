package tech.jmcs.floortech.scheduling.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.settings.SettingsLoader;
import tech.jmcs.floortech.scheduling.ui.data.DataFrameView;
import tech.jmcs.floortech.scheduling.ui.files.FileChooserView;
import tech.jmcs.floortech.scheduling.ui.settings.SettingsPresenter;
import tech.jmcs.floortech.scheduling.ui.settings.SettingsView;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(DashboardPresenter.class);

//    @FXML
//    Label message;
//
//    @FXML
//    Pane lightsBox;
//
//    @Inject
//    Tower tower;
//
//    @Inject
//    private String prefix;
//
//    @Inject
//    private String happyEnding;
//
//    @Inject
//    private LocalDate date;
//
//    private String theVeryEnd;

    @Inject
    private SettingsView settingsView;

    @Inject
    private FileChooserView fileChooserView;

    @Inject
    private DataFrameView dataFrameView;

    @Inject
    private SettingsLoader settingsLoader;

    @FXML
    private VBox fileChooserVbox;

    @FXML
    private AnchorPane dataFrameAnchorPane;

    @FXML
    private Button settingsButton;

    private Scene settingsScene;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //fetched from followme.properties
//        this.theVeryEnd = rb.getString("theEnd");
        LOG.debug("Settings view is: {}, fileChooserView is {}, dataFrameView is {}", settingsView, fileChooserView, dataFrameView);

        loadSettings();
        setupDashboard();
    }

    private void loadSettings() {
        try {
            this.settingsLoader.load();
        } catch (IOException e) {
            LOG.warn("Could not load settings: {}", e.getMessage());
        }
    }

    private void setupDashboard() {
        this.fileChooserVbox.getChildren().clear();
        this.fileChooserVbox.getChildren().add(fileChooserView.getView());
        this.dataFrameAnchorPane.getChildren().clear();
        this.dataFrameAnchorPane.getChildren().add(dataFrameView.getView());
    }

    public void handleSettingsButtonAction(ActionEvent event) {
        ((SettingsPresenter) this.settingsView.getPresenter()).update(); // update the settings view

        Stage settingsStage = new Stage();
        if (this.settingsScene == null) {
            this.settingsScene = new Scene(this.settingsView.getView());
        } else {
            settingsStage.setScene(this.settingsScene);
        }
        settingsStage.setScene(settingsScene);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.show();
    }


//    public void createLights() {
//        for (int i = 0; i < 255; i++) {
//            final int red = i;
//            LightView view = new LightView((f) -> red);
//            view.getViewAsync(lightsBox.getChildren()::add);
//        }
//    }

//    public void launch() {
//        message.setText("Date: " + date + " -> " + prefix + tower.readyToTakeoff() + happyEnding + theVeryEnd
//        );
//    }

}