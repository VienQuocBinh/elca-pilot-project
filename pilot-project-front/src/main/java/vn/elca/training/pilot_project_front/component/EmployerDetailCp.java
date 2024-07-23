package vn.elca.training.pilot_project_front.component;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.context.Context;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.pilot_project_front.constant.DatePattern;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;
import vn.elca.training.pilot_project_front.model.Employee;
import vn.elca.training.pilot_project_front.model.Employer;
import vn.elca.training.pilot_project_front.util.ObservableResourceFactory;
import vn.elca.training.pilot_project_front.util.StageManager;
import vn.elca.training.proto.employer.EmployerSearchRequest;
import vn.elca.training.proto.employer.PensionTypeProto;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@DeclarativeView(id = ComponentId.EMPLOYER_DETAIL_CP,
        name = "employerDetailCp",
        viewLocation = "/fxml/employerDetailCp.fxml",
        initialTargetLayoutId = PerspectiveId.EMPLOYER_DETAIL_PERSPECTIVE,
        resourceBundleLocation = "bundles.languageBundle")
public class EmployerDetailCp implements FXComponent {
    private final Logger log = Logger.getLogger(EmployerDetailCp.class.getName());
    @Resource
    private Context context;
    @FXML
    private Label lbPensionType;
    @FXML
    private ComboBox<PensionTypeProto> cbPensionType;
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
    private Button btnReturn;
    @FXML
    private Button btnImport;
    @FXML
    private Button btnSave;
    @FXML
    private TableView<Employee> tbvEmployee;
    @FXML
    private Label fileInput;
    @Getter
    private List<File> files = new ArrayList<>();

    @Override
    public Node postHandle(Node node, Message<Event, Object> message) throws Exception {
        if (message.getMessageBody() instanceof Employer) {
            // From EmployerDetailPerspective
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.PATTERN);
            Employer employer = message.getTypedMessageBody(Employer.class);
            tfNumber.setText(employer.getNumber());
            tfName.setText(employer.getName());
            tfIdeNumber.setText(employer.getIdeNumber());
            cbPensionType.getItems().clear();
            cbPensionType.getItems().addAll(PensionTypeProto.NONE, PensionTypeProto.REGIONAL, PensionTypeProto.PROFESSIONAL);
            cbPensionType.setValue(employer.getPensionType());
            dpCreateDate.setValue(LocalDate.parse(employer.getCreatedDate(), formatter));
            dpExpiredDate.setValue(LocalDate.parse(employer.getExpiredDate(), formatter));
        }
        return null;
    }

    @Override
    public Node handle(Message<Event, Object> message) throws Exception {
        return null;
    }

    @PostConstruct
    public void onPostConstruct() {
        lbPensionType.textProperty().bind(ObservableResourceFactory.getStringBinding("pensionType"));
        lbNumber.textProperty().bind(ObservableResourceFactory.getStringBinding("number"));
        lbIdeNumber.textProperty().bind(ObservableResourceFactory.getStringBinding("ideNumber"));
        lbName.textProperty().bind(ObservableResourceFactory.getStringBinding("name"));
        lbCreatedDate.textProperty().bind(ObservableResourceFactory.getStringBinding("createdDate"));
        lbExpiredDate.textProperty().bind(ObservableResourceFactory.getStringBinding("expiredDate"));
        btnSave.textProperty().bind(ObservableResourceFactory.getStringBinding("save"));
        btnReturn.textProperty().bind(ObservableResourceFactory.getStringBinding("return"));
        btnImport.textProperty().bind(ObservableResourceFactory.getStringBinding("import"));

        btnSave.setOnMouseClicked(event -> {
            context.send(ComponentId.EMPLOYER_CALLBACK_CP, EmployerSearchRequest.newBuilder().build());
        });
        btnReturn.setOnMouseClicked(event -> {
            // Set back app title
            Stage primaryStage = StageManager.getPrimaryStage();
            if (primaryStage != null) {
                primaryStage.titleProperty().setValue(ObservableResourceFactory.getProperty().getString("title"));
            }
            log.info(tfNumber.getText());
            context.send(PerspectiveId.HOME_PERSPECTIVE, "return");
        });
        fileInput.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter csvFilter = new FileChooser
                    .ExtensionFilter("CSV Files (*.csv)", "*.csv");
            FileChooser.ExtensionFilter xlsxFilter = new FileChooser
                    .ExtensionFilter("Excel Files (*.xlsx)", "*.xlsx");

            fileChooser.getExtensionFilters().addAll(csvFilter, xlsxFilter);
            Optional<List<File>> selectedFiles = Optional.ofNullable(fileChooser.showOpenMultipleDialog(fileInput.getScene().getWindow()));
            fileInput.setText(selectedFiles.map(fileList -> fileList.stream().map(File::getName)
                    .collect(Collectors.joining(","))).orElse(""));
            files = selectedFiles.orElse(new ArrayList<>());
        });
        tbvEmployee.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                tbvEmployee.prefHeightProperty().bind(tbvEmployee.getScene().heightProperty());
                tbvEmployee.prefWidthProperty().bind(tbvEmployee.getScene().widthProperty());
            }
        });
        tbvEmployee.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
