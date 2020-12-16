package tech.jmcs.floortech.scheduling;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.settings.SettingsLoader;
import tech.jmcs.floortech.scheduling.ui.DashboardView;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("In start() method");

        Map<Object, Object> customProperties = new HashMap<>();
        Injector.setConfigurationSource(customProperties::get);

        DashboardView appView = new DashboardView();
        Scene scene = new Scene(appView.getView());
        stage.setTitle("Floortech Data Scraper | Scheduling");
//        final String uri = getClass().getResource("skin.css").toExternalForm();
//        scene.getStylesheets().add(uri);
        stage.setScene(scene);
        stage.setMinHeight(800);
        stage.setMinWidth(1400);
        stage.show();
    }


    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        System.out.println("In main() method");
        launch(args);
    }

}
