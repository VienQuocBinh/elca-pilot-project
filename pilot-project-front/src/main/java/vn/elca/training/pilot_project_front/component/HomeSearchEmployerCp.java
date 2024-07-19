package vn.elca.training.pilot_project_front.component;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;
import vn.elca.training.pilot_project_front.util.ObservableResourceFactory;

@DeclarativeView(id = ComponentId.HOME_SEARCH_EMPLOYER_CP,
        name = "homeSearchEmployerCp",
        viewLocation = "/fxml/homeSearchEmployerCp.fxml",
        resourceBundleLocation = "bundles.languageBundle",
        initialTargetLayoutId = PerspectiveId.HORIZONTAL_CONTAINER_TOP
)
public class HomeSearchEmployerCp implements FXComponent {
    @FXML
    private Label lbFundType;
    @FXML
    private Label lbName;
    @FXML
    private Label lbNumber;
    @FXML
    private Label lbIdeNumber;
    @FXML
    private Label lbCreatedDate;
    @FXML
    private Label lbExpiredDate;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnReset;
    @FXML
    private Button btnAdd;

    @Override
    public Node postHandle(Node node, Message<Event, Object> message) throws Exception {
        return null;
    }

    @Override
    public Node handle(Message<Event, Object> message) throws Exception {

        return null;
    }

    @PostConstruct
    public void onPostConstruct() {
        // Binding text value according to Locale
        lbFundType.textProperty().bind(ObservableResourceFactory.getStringBinding("fundType"));
        lbNumber.textProperty().bind(ObservableResourceFactory.getStringBinding("number"));
        lbIdeNumber.textProperty().bind(ObservableResourceFactory.getStringBinding("ideNumber"));
        lbName.textProperty().bind(ObservableResourceFactory.getStringBinding("name"));
        lbCreatedDate.textProperty().bind(ObservableResourceFactory.getStringBinding("createdDate"));
        lbExpiredDate.textProperty().bind(ObservableResourceFactory.getStringBinding("expiredDate"));
        btnSearch.textProperty().bind(ObservableResourceFactory.getStringBinding("search"));
        btnReset.textProperty().bind(ObservableResourceFactory.getStringBinding("reset"));
        btnAdd.textProperty().bind(ObservableResourceFactory.getStringBinding("add"));
    }
}
