package tech.jmcs.floortech.scheduling;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.ExtractedDataToScheduleConverter;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.settings.SettingsLoader;
import tech.jmcs.floortech.scheduling.app.settings.SettingsWriter;
import tech.jmcs.floortech.scheduling.ui.*;
import tech.jmcs.floortech.scheduling.ui.commitbutton.CommitButtonView;
import tech.jmcs.floortech.scheduling.ui.dashboard.DashboardView;
import tech.jmcs.floortech.scheduling.ui.dataframe.DataFrameView;
import tech.jmcs.floortech.scheduling.ui.datatarget.DataTargetView;
import tech.jmcs.floortech.scheduling.ui.extractbutton.ExtractButtonView;
import tech.jmcs.floortech.scheduling.ui.extractors.ExtractorsView;
import tech.jmcs.floortech.scheduling.ui.quicklookup.QuickLookupView;
import tech.jmcs.floortech.scheduling.ui.settings.SettingsView;
import tech.jmcs.floortech.scheduling.ui.settingsbutton.SettingsButtonView;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class App extends Application {
    protected static final Logger LOG = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage stage) throws Exception {
        Map<Object, Object> customProperties = new HashMap<>();

        SettingsHolder settingsHolder = new SettingsHolder();
        SettingsWriter settingsWriter = new SettingsWriter(settingsHolder);
        SettingsLoader settingsLoader = new SettingsLoader(settingsHolder, settingsWriter);

        ExtractorComponentHolderFX extractorComponentHolderFX = new ExtractorComponentHolderFX(settingsHolder);
//        ExtractedDataHolderFX extractedDataHolderFx = new ExtractedDataHolderFX(settingsHolder);

        ExtractedDataToScheduleConverter extractedDataToScheduleConverter = new ExtractedDataToScheduleConverter();

        AutoFillManagerFX autoFillManagerFX = new AutoFillManagerFX(settingsHolder);
//        ExtractorManagerFX extractorManagerFX = new ExtractorManagerFX();


        CommitErrors commitErrors = new CommitErrors();
//
//        @Inject private SettingsView settingsView;
//        @Inject private SettingsButtonView settingsButtonView;
//        @Inject private QuickLookupView quickLookupView;
//        @Inject private ExtractorsView extractorsView;
//        @Inject private ExtractButtonView extractButtonView;
//        @Inject private DataTargetView dataTargetView;
//        @Inject private CommitButtonView commitButtonView;
//        @Inject private DataFrameView dataFrameView;


        customProperties.put("settingsHolder", settingsHolder);
        customProperties.put("settingsLoader", settingsLoader);
        customProperties.put("settingsWriter", settingsWriter);

        customProperties.put("extractedDataToScheduleConverter", extractedDataToScheduleConverter);
        customProperties.put("extractorComponentHolderFX", extractorComponentHolderFX);
//        customProperties.put("extractedDataHolderFx", extractedDataHolderFx);

        customProperties.put("autoFillManagerFX", autoFillManagerFX);
//        customProperties.put("extractorManagerFX", extractorManagerFX);

        customProperties.put("commitErrors", commitErrors);

        Injector.setConfigurationSource(customProperties::get);

        DashboardView appView = new DashboardView();
        Scene scene = new Scene(appView.getView());

//        BootView bootView = new BootView();
//        Scene scene = new Scene(bootView.getView());

        stage.setTitle("Floortech Data Scraper | Scheduling");
//        final String uri = getClass().getResource("skin.css").toExternalForm();
//        scene.getStylesheets().add(uri);
        stage.setScene(scene);
        stage.setHeight(1000);
        stage.setWidth(1600);
        stage.show();
    }


    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
