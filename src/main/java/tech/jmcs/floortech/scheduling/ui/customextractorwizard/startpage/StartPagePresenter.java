package tech.jmcs.floortech.scheduling.ui.customextractorwizard.startpage;

import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.CustomExtractorData;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.TriggeredFXMLView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class StartPagePresenter implements Initializable, TriggeredFXMLView {
    protected static final Logger LOG = LoggerFactory.getLogger(StartPagePresenter.class);

    @Inject private CustomExtractorData customExtractorData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.debug("Instance id for CustomExtractorData: " + this.customExtractorData.hashCode());
    }

    @Override
    public void trigger() {
        LOG.debug("No data to commit from the start page");
    }

    @Override
    public void reset() {

    }

    @Override
    public void update() {

    }
}
