package vn.elca.training.pilot_project_front.component;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
import java.util.Locale;
import java.util.ResourceBundle;

@DeclarativeView(id = ComponentId.HOME_SEARCH_EMPLOYER_CP,
        name = "homeSearchEmployerCp",
        viewLocation = "/fxml/homeSearchEmployerCp.fxml",
        resourceBundleLocation = "bundles.languageBundle",
        initialTargetLayoutId = PerspectiveId.HORIZONTAL_CONTAINER_TOP
)
public class HomeSearchEmployerCp implements FXComponent {
    private static final Logger log = LoggerFactory.getLogger(HomeSearchEmployerCp.class);
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
    private Label lbCreatedDate;
    @FXML
    private DatePicker dpCreateDate;
    @FXML
    private Label lbExpiredDate;
    @FXML
    private DatePicker dpExpiredDate;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnReset;
    @FXML
    private Button btnAdd;
    @FXML
    private ComboBox<PensionTypeProto> cbPensionType;
    private Stage stagePopup;

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
        lbCreatedDate.textProperty().bind(ObservableResourceFactory.getStringBinding("createdDate"));
        lbExpiredDate.textProperty().bind(ObservableResourceFactory.getStringBinding("expiredDate"));
        btnSearch.textProperty().bind(ObservableResourceFactory.getStringBinding("search"));
        btnReset.textProperty().bind(ObservableResourceFactory.getStringBinding("reset"));
        btnAdd.textProperty().bind(ObservableResourceFactory.getStringBinding("add"));
        cbPensionType.getItems().addAll(PensionTypeProto.NONE, PensionTypeProto.REGIONAL, PensionTypeProto.PROFESSIONAL);
        cbPensionType.getSelectionModel().selectFirst();

        btnSearch.setOnMouseClicked(event -> searchEmployers());
        btnReset.setOnMouseClicked(event -> resetSearchFields());
        btnAdd.setOnMouseClicked(event -> showCreatePopup());
    }

    private void searchEmployers() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.PATTERN);
        EmployerSearchRequest searchRequest = EmployerSearchRequest.newBuilder()
                .setPensionType(cbPensionType.getSelectionModel().getSelectedItem())
                .setName(tfName.getText())
                .setIdeNumber(tfIdeNumber.getText())
                .setNumber(tfNumber.getText())
                .setCreatedDate(dpCreateDate.getValue() != null ? dpCreateDate.getValue().format(dateTimeFormatter) : "")
                .setExpiredDate(dpExpiredDate.getValue() != null ? dpExpiredDate.getValue().format(dateTimeFormatter) : "")
                .build();
        context.send(ComponentId.EMPLOYER_CALLBACK_CP, searchRequest);
    }

    private void resetSearchFields() {
        cbPensionType.getSelectionModel().selectFirst();
        tfName.clear();
        tfNumber.clear();
        tfIdeNumber.clear();
        dpCreateDate.setValue(null);
        dpExpiredDate.setValue(null);
    }

    private void showCreatePopup() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/employerCreatePopup.fxml"));
            Locale locale = ObservableResourceFactory.resourceProperty().get().getLocale();
            fxmlLoader.setResources(ResourceBundle.getBundle("bundles.languageBundle", locale));
            Parent parent = fxmlLoader.load();
            stagePopup = new Stage();
            stagePopup.initModality(Modality.APPLICATION_MODAL);
            stagePopup.setTitle("Add Employer");
            stagePopup.setScene(new Scene(parent));
            stagePopup.setResizable(false);
            stagePopup.showAndWait();
            // Reload your table view here after the popup is closed
            System.out.println("reload");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
