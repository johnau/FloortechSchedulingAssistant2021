package tech.jmcs.floortech.scheduling.ui.customextractor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tech.jmcs.floortech.scheduling.ui.customextractor.columndescriptors.ColumnDescriptorsView;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CustomExtractorPresenter implements Initializable {

    private class PopupData {
        private String name;
        private Button btn;
        private Node popup;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Button getBtn() {
            return btn;
        }

        public void setBtn(Button btn) {
            this.btn = btn;
        }

        public Node getPopup() {
            return popup;
        }

        public void setPopup(Node popup) {
            this.popup = popup;
        }
    }

    @FXML private AnchorPane pagePane;

    @FXML private Label extractorNameHelp;
    @FXML private Label tableTitleHelp;
    @FXML private Label columnHeaderHelp;
    @FXML private Label dataStartRowHelp;
    @FXML private Button closeButton;

    @FXML private Button columnDescriptorButton;
    @FXML private Button recordValidationButton;
    @FXML private Button validationFunctionsButton;
    @FXML private Button valueCalculationsButton;

//    @FXML private HBox columnDescriptorBubble;
//    @FXML private HBox recordValidationBubble;
//    @FXML private HBox validationFunctionsBubble;
//    @FXML private HBox valueCalculationBubble;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void createAndBindTooltips() {
        Tooltip extractNameHelpTip = new Tooltip("Name of this custom extractor");
        Tooltip tableTileHelpTip = new Tooltip("Row number of Table Title and all or beginning of the title text");
        Tooltip columnHeadersHelpTip = new Tooltip("Row number of Column Headers and");


        extractorNameHelp.setTooltip(extractNameHelpTip);
        bindTooltip(extractorNameHelp, extractNameHelpTip);
    }

    public static void bindTooltip(final Node node, final Tooltip tooltip){
        node.setOnMouseMoved(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                // move the tooltip 15 pixels below the mouse cursor;
                // change y coordinate of tooltip to avoid screen flicker
                tooltip.show(node, event.getScreenX() + 15, event.getScreenY() - 15);
            }
        });
        node.setOnMouseExited(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                tooltip.hide();
            }
        });
    }

    @FXML
    public void handleAddColumnDescriptorsAction(ActionEvent event) {
        ColumnDescriptorsView view = new ColumnDescriptorsView();
        Stage s = new Stage();
        s.setScene(view.getScene());
        s.showAndWait();
    }

    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        ((Stage) this.closeButton.getScene().getWindow()).close();
    }
}
