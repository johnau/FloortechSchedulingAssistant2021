package tech.jmcs.floortech.scheduling.ui.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.ExtractedDataToScheduleConverter;
import tech.jmcs.floortech.scheduling.app.schedulewriter.ExcelScheduleScanner;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.settings.SettingsLoader;
import tech.jmcs.floortech.scheduling.app.settings.SettingsWriter;
import tech.jmcs.floortech.scheduling.ui.ExtractorComponentHolderFX;
import tech.jmcs.floortech.scheduling.ui.ExtractorManagerFX;
import tech.jmcs.floortech.scheduling.ui.commitbutton.CommitButtonView;
import tech.jmcs.floortech.scheduling.ui.dataframe.DataFrameView;
import tech.jmcs.floortech.scheduling.ui.ExtractedDataHolderFX;
import tech.jmcs.floortech.scheduling.ui.datatarget.DataTargetView;
import tech.jmcs.floortech.scheduling.ui.extractbutton.ExtractButtonView;
import tech.jmcs.floortech.scheduling.ui.extractors.ExtractorsView;
import tech.jmcs.floortech.scheduling.ui.quicklookup.QuickLookupView;
import tech.jmcs.floortech.scheduling.ui.settings.SettingsView;
import tech.jmcs.floortech.scheduling.ui.settingsbutton.SettingsButtonView;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(DashboardPresenter.class);

    /**
     * All container held classes injected here to preload
     */
    @Inject private SettingsView settingsView;
    @Inject private SettingsButtonView settingsButtonView;
    @Inject private QuickLookupView quickLookupView;
    @Inject private ExtractorsView extractorsView;
    @Inject private ExtractButtonView extractButtonView;
    @Inject private DataTargetView dataTargetView;
    @Inject private CommitButtonView commitButtonView;
    @Inject private DataFrameView dataFrameView;

    @Inject private SettingsLoader settingsLoader;
    @Inject private SettingsWriter settingsWriter;
    @Inject private SettingsHolder settingsHolder;
    @Inject private ExtractedDataToScheduleConverter extractedDataConverter;
    @Inject private ExtractorComponentHolderFX extractorHolder;
    @Inject private ExtractorManagerFX extractorManager;
    @Inject private ExtractedDataHolderFX extractedDataHolder;

    @FXML private VBox leftPanelVbox; // left panel
    @FXML private VBox quickLookupVbox;
    @FXML private VBox extractorsVbox;
    @FXML private VBox extractButtonVbox;
    @FXML private VBox dataTargetVbox;
    @FXML private VBox commitButtonVbox;
    @FXML private AnchorPane rightPanelAnchorPane; // right panel
    @FXML private HBox toolbarHbox; // top right panel (alongside header / title)

    private Scene settingsScene;

    @Override
    public void initialize(URL url, ResourceBundle rb) { // resource bundle populated from *.properties
        LOG.info("DashboardPresenter initializing...");

        loadSettings();
        buildExtractorViews(); // call after settings are loaded
        setupDashboard();
    }

    private void loadSettings() {
        try {
            this.settingsLoader.load();
        } catch (IOException e) {
            LOG.warn("Could not load settings: {}", e.getMessage());
        }
    }

    private void buildExtractorViews() {
        this.extractorHolder.buildAll();
    }

    private void setupDashboard() {
        settingsButtonView.getViewAsync(this.toolbarHbox.getChildren()::add);
        quickLookupView.getViewAsync(this.quickLookupVbox.getChildren()::add);
        extractorsView.getViewAsync(this.extractorsVbox.getChildren()::add);
        extractButtonView.getViewAsync(this.extractButtonVbox.getChildren()::add);
        dataTargetView.getViewAsync(this.dataTargetVbox.getChildren()::add);
        commitButtonView.getViewAsync(this.commitButtonVbox.getChildren()::add);
        dataFrameView.getViewAsync(this.rightPanelAnchorPane.getChildren()::add);
    }

}