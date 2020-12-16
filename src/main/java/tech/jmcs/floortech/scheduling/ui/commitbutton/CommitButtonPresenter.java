package tech.jmcs.floortech.scheduling.ui.commitbutton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class CommitButtonPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(CommitButtonPresenter.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("CommitButtonPresenter initializing...");
    }

    @FXML
    public void handleCommitButtonAction(ActionEvent event) {

    }
}
