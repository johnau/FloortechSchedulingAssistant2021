package tech.jmcs.floortech.scheduling;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.ui.dashboard.DashboardView;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {
    protected static final Logger LOG = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("In start() method");

        Map<Object, Object> customProperties = new HashMap<>();
        Injector.setConfigurationSource(customProperties::get);

        DashboardView appView = new DashboardView();
        Scene scene = new Scene(appView.getView());

//        BootView bootView = new BootView();
//        Scene scene = new Scene(bootView.getView());

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
