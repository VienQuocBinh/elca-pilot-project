package vn.elca.training.pilot_project_front.component;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.context.Context;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.pilot_project_front.constant.DatePattern;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;
import vn.elca.training.pilot_project_front.util.ObservableResourceFactory;
import vn.elca.training.proto.employer.EmployerSearchRequest;
import vn.elca.training.proto.employer.PensionTypeProto;

import java.time.format.DateTimeFormatter;

@DeclarativeView(id = ComponentId.HOME_SEARCH_EMPLOYER_CP,
        name = "homeSearchEmployerCp",
        viewLocation = "/fxml/homeSearchEmployerCp.fxml",
        resourceBundleLocation = "bundles.languageBundle",
        initialTargetLayoutId = PerspectiveId.HORIZONTAL_CONTAINER_TOP
)
public class HomeSearchEmployerCp implements FXComponent {
    @Resource
    private Context context;
    @FXML
    private Label lbPensionType;
    @FXML
    private Label lbName;
    @FXML
    private TextField tfName;
    @FXML
    private Label lbNumber;
    @FXML
    private TextField tfNumber;
    @FXML
    private Label lbIdeNumber;
    @FXML
    private TextField tfIdeNumber;
    @FXML
    private Label lbDateCreation;
    @FXML
    private DatePicker dpDateCreation;
    @FXML
    private Label lbDateExpiration;
    @FXML
    private DatePicker dpDateExpiration;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnReset;
    @FXML
    private ComboBox<PensionTypeProto> cbPensionType;

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
        lbPensionType.textProperty().bind(ObservableResourceFactory.getStringBinding("pensionType"));
        lbNumber.textProperty().bind(ObservableResourceFactory.getStringBinding("number"));
        lbIdeNumber.textProperty().bind(ObservableResourceFactory.getStringBinding("ideNumber"));
        lbName.textProperty().bind(ObservableResourceFactory.getStringBinding("name"));
        lbDateCreation.textProperty().bind(ObservableResourceFactory.getStringBinding("dateCreation"));
        lbDateExpiration.textProperty().bind(ObservableResourceFactory.getStringBinding("dateExpiration"));
        btnSearch.textProperty().bind(ObservableResourceFactory.getStringBinding("search"));
        btnReset.textProperty().bind(ObservableResourceFactory.getStringBinding("reset"));
        cbPensionType.getItems().addAll(PensionTypeProto.NONE, PensionTypeProto.REGIONAL, PensionTypeProto.PROFESSIONAL);
        cbPensionType.getSelectionModel().selectFirst();

        btnSearch.setOnMouseClicked(event -> searchEmployers());
        btnReset.setOnMouseClicked(event -> resetSearchFields());
    }

    private void searchEmployers() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.PATTERN);
        EmployerSearchRequest searchRequest = EmployerSearchRequest.newBuilder()
                .setPensionType(cbPensionType.getSelectionModel().getSelectedItem())
                .setName(tfName.getText())
                .setIdeNumber(tfIdeNumber.getText())
                .setNumber(tfNumber.getText())
                .setDateCreation(dpDateCreation.getValue() != null ? dpDateCreation.getValue().format(dateTimeFormatter) : "")
                .setDateExpiration(dpDateExpiration.getValue() != null ? dpDateExpiration.getValue().format(dateTimeFormatter) : "")
                .build();
        context.send(ComponentId.EMPLOYER_CALLBACK_CP, searchRequest);
    }

    private void resetSearchFields() {
        cbPensionType.getSelectionModel().selectFirst();
        tfName.clear();
        tfNumber.clear();
        tfIdeNumber.clear();
        dpDateCreation.setValue(null);
        dpDateExpiration.setValue(null);
    }
}
