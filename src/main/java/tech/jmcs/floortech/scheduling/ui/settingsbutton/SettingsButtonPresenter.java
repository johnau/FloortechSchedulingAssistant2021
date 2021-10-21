package tech.jmcs.floortech.scheduling.ui.settingsbutton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.ui.settings.SettingsPresenter;
import tech.jmcs.floortech.scheduling.ui.settings.SettingsView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsButtonPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(SettingsButtonPresenter.class);

    @Inject private SettingsView settingsView;

    @FXML private Button settingsButton;

    private Scene settingsScene;
    private Scene helpScene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("SettingsButtonPresenter initializing...");
    }

    @FXML
    public void handleSettingsButtonAction(ActionEvent event) {
        ((SettingsPresenter) this.settingsView.getPresenter()).update(); // update the settings view
        // TODO Preferable to replace above with listener in the view for when its shown

        Stage settingsStage = new Stage();
        if (this.settingsScene == null) {
            this.settingsScene = new Scene(this.settingsView.getView());
        }
        settingsStage.setScene(this.settingsScene);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.setTitle("Settings");
        settingsStage.setWidth(800);
        settingsStage.setHeight(1000);
        settingsStage.setResizable(true);
        settingsStage.show();
    }

    @FXML
    public void handleHelpButtonAction(ActionEvent event) {
        Stage stage = new Stage();
        if (this.helpScene == null) {
            Parent container = buildHelpScene();
            this.helpScene = new Scene(container, 500, 500);
        }
        stage.setScene(this.helpScene);
        stage.initOwner(settingsButton.getScene().getWindow());
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private Parent buildHelpScene() {
        VBox container = new VBox();
        container.setPadding(new Insets(10, 10, 10, 10));
        container.setSpacing(10d);
        Label autoFillHeader = new Label("Auto-fill");
        autoFillHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #444; -fx-font-size: 16;");
        container.getChildren().add(autoFillHeader);

        TextFlow tf = new TextFlow();
        tf.setLineSpacing(3d);
        Text t1 = new Text("Enter job number into the Job Number field to auto-fill all fields (data sources and data target)");
        t1.setStyle("-fx-font-size: 14;");
        Text t2 = new Text(" OR ");
        t2.setStyle("-fx-font-weight: bold;");
        t2.setStyle("-fx-font-size: 16;");
        Text t3 = new Text("Choose a job detailing folder path to auto-fill just the data source fields.\n");
        t3.setStyle("-fx-font-size: 14;");
        tf.getChildren().addAll(t1, t2, t3);
        container.getChildren().add(tf);

        TextFlow tf2 = new TextFlow();
        tf2.setLineSpacing(3d);
        Text ttx2 = new Text("> Beam Data");
        ttx2.setStyle("-fx-font-weight: bold; -fx-fill: #0099ff");
        Text ttx3 = new Text("> Sheet Data");
        ttx3.setStyle("-fx-font-weight: bold; -fx-fill: #0099ff");
        Text ttx4 = new Text("> Slab Data");
        ttx4.setStyle("-fx-font-weight: bold; -fx-fill: #0099ff");
        Text ttx5 = new Text("> Hopley Truss Data");
        ttx5.setStyle("-fx-font-weight: bold; -fx-fill: #0099ff");
        Text ttx6 = new Text("> Coldwright Truss Data");
        ttx6.setStyle("-fx-font-weight: bold; -fx-fill: #0099ff");

        Text tt0 = new Text("* Note: ");
        tt0.setStyle("-fx-font-weight: bold;");
        Text tt1 = new Text("Files will be recognized based on filename only...\n");
        Text tt2 = new Text(" from Excel List\n");
        tt2.setStyle("-fx-font-weight: bold;");
        Text tt2b = new Text("\t\tMust contain the words 'Beam' and 'List' (case insensitive)\n");
        tt2b.setStyle("-fx-fill: #444");
        Text tt3 = new Text(" from Excel List\n");
        tt3.setStyle("-fx-font-weight: bold;");
        Text tt3b = new Text("\t\tMust contain the words 'Sheet' and 'List' (case insensitive)\n");
        tt3b.setStyle("-fx-fill: #444");
        Text tt4 = new Text(" from Job PDF file\n");
        tt4.setStyle("-fx-font-weight: bold;");
        Text tt4b = new Text("\t\tMust have the same name as parent folder (Job Number)\n");
        tt4b.setStyle("-fx-fill: #444");
        Text tt5 = new Text(" from Excel List\n");
        tt5.setStyle("-fx-font-weight: bold;");
        Text tt5b = new Text("\t\tMust have the word 'List' and one or more of these words: 'Hopley', 'HJ200', 'HJ300' (case insensitive)\n");
        tt5b.setStyle("-fx-fill: #444");
        Text tt6 = new Text(" from Excel List\n");
        tt6.setStyle("-fx-font-weight: bold;");
        Text tt6b = new Text("\t\tCan have the word 'List' and one or more of these words: 'Coldwright', 'CW260', 'CW346', 'CX346' (case insensitive). Can be the only other excel file in the folder not matched by any of the above (to suit current system) \n");
        tt6b.setStyle("-fx-fill: #444");

        tf2.getChildren().addAll(tt0, tt1, ttx2, tt2, tt2b, ttx3, tt3, tt3b, ttx4, tt4, tt4b, ttx5, tt5, tt5b, ttx6, tt6, tt6b);

        container.getChildren().add(tf2);

        return container;
    }
}
