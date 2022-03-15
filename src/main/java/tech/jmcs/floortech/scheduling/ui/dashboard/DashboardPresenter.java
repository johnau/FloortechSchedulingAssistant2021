package tech.jmcs.floortech.scheduling.ui.dashboard;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.ExtractedDataToScheduleConverter;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.settings.SettingsLoader;
import tech.jmcs.floortech.scheduling.app.settings.SettingsWriter;
import tech.jmcs.floortech.scheduling.ui.*;
import tech.jmcs.floortech.scheduling.ui.commitbutton.CommitButtonView;
import tech.jmcs.floortech.scheduling.ui.dataframe.DataFrameView;
import tech.jmcs.floortech.scheduling.ui.datatarget.DataTargetView;
import tech.jmcs.floortech.scheduling.ui.extractbutton.ExtractButtonView;
import tech.jmcs.floortech.scheduling.ui.extractors.ExtractorsView;
import tech.jmcs.floortech.scheduling.ui.quicklookup.QuickLookupView;
import tech.jmcs.floortech.scheduling.ui.settings.SettingsView;
import tech.jmcs.floortech.scheduling.ui.settingsbutton.SettingsButtonView;

import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.ResourceBundle;

public class DashboardPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(DashboardPresenter.class);

    /**
     * All container held classes injected here to preload
     */

    @Inject private SettingsLoader settingsLoader;
    @Inject private SettingsWriter settingsWriter;
    @Inject private SettingsHolder settingsHolder;
    @Inject private AutoFillManagerFX autoFillManagerFX;
    @Inject private ExtractedDataToScheduleConverter extractedDataToScheduleConverter;
    @Inject private ExtractorComponentHolderFX extractorComponentHolderFX;
//    @Inject private ExtractorManagerFX extractorManagerFX;
    @Inject private ExtractedDataHolderFX extractedDataHolderFX;
    @Inject private CommitErrors commitErrors;

    @Inject private SettingsView settingsView;
    @Inject private SettingsButtonView settingsButtonView;
    @Inject private QuickLookupView quickLookupView;
    @Inject private ExtractorsView extractorsView;
    @Inject private ExtractButtonView extractButtonView;
    @Inject private DataTargetView dataTargetView;
    @Inject private CommitButtonView commitButtonView;
    @Inject private DataFrameView dataFrameView;

    @FXML private VBox leftPanelVbox; // left panel
    @FXML private VBox leftVbox1;
    @FXML private VBox leftVbox2;
    @FXML private VBox leftVbox3;
    @FXML private VBox leftVbox4;
    @FXML private VBox leftVbox5;
    @FXML private AnchorPane rightPanelAnchorPane; // right panel
    @FXML private HBox toolbarHbox; // top right panel (alongside header / title)

    @FXML private ListView<String> loggingList;

    private Scene settingsScene;


    @Override
    public void initialize(URL url, ResourceBundle rb) { // resource bundle populated from *.properties
        LOG.info("DashboardPresenter initializing...");

        System.setOut(new PrintStream(System.out) {
            public void println(String s) {
                Platform.runLater(() -> {
                    Date date = new Date();
                    DateFormat df = new SimpleDateFormat("hh:mm:ss dd-MM-yy");
                    String o = String.format("[%s] %s", df.format(date), s);
                    loggingList.getItems().add(o);
                });
                super.println(s);
            }
        });
        loggingList.getItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        loggingList.scrollTo(loggingList.getItems().size()-1);
                    }
                }
            }
        });

        loadSettings();
        buildExtractorViews(); // call after settings are loaded
        setupDashboard();
    }

    private void loadSettings() {
        try {
            this.settingsLoader.load();
            System.out.println("Loaded settings...");
        } catch (IOException e) {
            LOG.warn("Could not load settings: {}", e.getMessage());
            System.out.println("Warning! Could not load settings!");
        }
    }

    private void buildExtractorViews() {
        this.extractorComponentHolderFX.buildAll();
    }

    private void setupDashboard() {
        quickLookupView.getViewAsync(this.leftVbox1.getChildren()::add);
        settingsButtonView.getViewAsync(this.toolbarHbox.getChildren()::add);
        extractorsView.getViewAsync(this.leftVbox2.getChildren()::add);
        extractButtonView.getViewAsync(this.leftVbox3.getChildren()::add);
        dataTargetView.getViewAsync(this.leftVbox4.getChildren()::add);
        commitButtonView.getViewAsync(this.leftVbox5.getChildren()::add);
        dataFrameView.getViewAsync(this.rightPanelAnchorPane.getChildren()::add);
    }

}