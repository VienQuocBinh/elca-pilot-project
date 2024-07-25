package vn.elca.training.pilot_project_front.component;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.context.Context;
import vn.elca.training.pilot_project_front.constant.ActionType;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.pilot_project_front.constant.DatePattern;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;
import vn.elca.training.pilot_project_front.model.Employer;
import vn.elca.training.pilot_project_front.model.EmployerResponseWrapper;
import vn.elca.training.pilot_project_front.model.FileError;
import vn.elca.training.pilot_project_front.model.Salary;
import vn.elca.training.pilot_project_front.util.FileUtil;
import vn.elca.training.pilot_project_front.util.ObservableResourceFactory;
import vn.elca.training.pilot_project_front.util.StageManager;
import vn.elca.training.pilot_project_front.util.ValidationUtil;
import vn.elca.training.proto.employer.EmployerId;
import vn.elca.training.proto.employer.EmployerUpdateRequest;
import vn.elca.training.proto.employer.PensionTypeProto;
import vn.elca.training.proto.salary.SalaryCreateRequest;
import vn.elca.training.proto.salary.SalaryResponse;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@DeclarativeView(id = ComponentId.EMPLOYER_DETAIL_CP,
        name = "employerDetailCp",
        viewLocation = "/fxml/employerDetailCp.fxml",
        initialTargetLayoutId = PerspectiveId.EMPLOYER_DETAIL_PERSPECTIVE,
        resourceBundleLocation = "bundles.languageBundle")
public class EmployerDetailCp implements FXComponent {
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
    private Label lbNumberValue;
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
    private Button btnReturn;
    @FXML
    private Button btnImport;
    @FXML
    private Button btnSave;
    @FXML
    private TableView<Salary> tbvSalary;
    @FXML
    private TableColumn<Salary, String> avsNumberCol;
    @FXML
    private TableColumn<Salary, String> lastNameCol;
    @FXML
    private TableColumn<Salary, String> firstNameCol;
    @FXML
    private TableColumn<Salary, String> startDateCol;
    @FXML
    private TableColumn<Salary, String> endDateCol;
    @FXML
    private TableColumn<Salary, String> avsAmountCol;
    @FXML
    private TableColumn<Salary, String> acAmountCol;
    @FXML
    private TableColumn<Salary, String> afAmountCol;
    @FXML
    private Label lbSalDeclaration;
    @FXML
    private Label fileInput;
    @Getter
    private File file;
    private Employer employer;
    private List<Salary> importedSalaries = new ArrayList<>();

    @Override
    public Node postHandle(Node node, Message<Event, Object> message) throws Exception {
        if (message.getMessageBody() instanceof Employer) {
            // From EmployerDetailPerspective
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.PATTERN);
            employer = message.getTypedMessageBody(Employer.class);
            lbNumberValue.setText(employer.getNumber());
            tfName.setText(employer.getName());
            tfIdeNumber.setText(employer.getIdeNumber());
            cbPensionType.getItems().clear();
            cbPensionType.getItems().addAll(PensionTypeProto.REGIONAL, PensionTypeProto.PROFESSIONAL);
            cbPensionType.setValue(employer.getPensionType());
            dpDateCreation.setValue(LocalDate.parse(employer.getDateCreation(), formatter));
            if (!StringUtils.isBlank(employer.getDateExpiration()))
                dpDateExpiration.setValue(LocalDate.parse(employer.getDateExpiration(), formatter));
            context.send(ComponentId.EMPLOYER_CALLBACK_CP, EmployerId.newBuilder().setId(employer.getId()).build());
        } else if (message.getMessageBody() instanceof EmployerResponseWrapper) {
            // From get employer detail, update stub callback
            EmployerResponseWrapper employerResponseWrapper = message.getTypedMessageBody(EmployerResponseWrapper.class);
            List<SalaryResponse> salariesResponse = employerResponseWrapper.getEmployerResponse().getSalariesList();
            List<Salary> salaries = salariesResponse
                    .stream()
                    .map(salaryResponse -> Salary.builder()
                            .id(salaryResponse.getId())
                            .avsNumber(salaryResponse.getAvsNumber())
                            .firstName(salaryResponse.getFirstName())
                            .lastName(salaryResponse.getLastName())
                            .startDate(salaryResponse.getStartDate())
                            .endDate(salaryResponse.getEndDate())
                            .avsAmount(salaryResponse.getAvsAmount())
                            .acAmount(salaryResponse.getAcAmount())
                            .afAmount(salaryResponse.getAfAmount())
                            .build())
                    .collect(Collectors.toList());
            tbvSalary.getItems().clear();
            tbvSalary.setItems(FXCollections.observableList(salaries));
            if (employerResponseWrapper.getActionType().equals(ActionType.UPDATE)) {
                showSuccessAlert("Update employer", "Update employer successfully");
            }
        }
        return null;
    }

    @Override
    public Node handle(Message<Event, Object> message) throws Exception {
        return null;
    }

    @PostConstruct
    public void onPostConstruct() {
        bindingResource();
        btnSave.setOnMouseClicked(event -> {
            // Just send imported salaries to save to db
            List<SalaryCreateRequest> salaryCreateRequests = importedSalaries.stream()
                    .map(salary -> SalaryCreateRequest.newBuilder()
                            .setFirstName(salary.getFirstName())
                            .setLastName(salary.getLastName())
                            .setAvsNumber(salary.getAvsNumber())
                            .setStartDate(salary.getStartDate())
                            .setEndDate(salary.getEndDate())
                            .setAvsAmount(salary.getAvsAmount())
                            .setAcAmount(salary.getAcAmount())
                            .setAfAmount(salary.getAfAmount())
                            .build())
                    .collect(Collectors.toList());
            DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern(DatePattern.PATTERN);
            context.send(ComponentId.EMPLOYER_CALLBACK_CP, EmployerUpdateRequest.newBuilder()
                    .setId(employer.getId())
                    .setName(tfName.getText())
                    .setDateCreation(dpDateCreation.getValue().format(dateFormater))
                    .setDateExpiration(dpDateExpiration.getValue().format(dateFormater))
                    .setIdeNumber(tfIdeNumber.getText())
                    .setPensionType(cbPensionType.getSelectionModel().getSelectedItem())
                    .addAllSalaries(salaryCreateRequests)
                    .build());
        });
        btnReturn.setOnMouseClicked(event -> {
            // Set back app title
            Stage primaryStage = StageManager.getPrimaryStage();
            if (primaryStage != null) {
                primaryStage.titleProperty().setValue(ObservableResourceFactory.getProperty().getString("title"));
            }
            context.send(PerspectiveId.HOME_PERSPECTIVE, ActionType.RETURN);
        });
        btnImport.setOnMouseClicked(e -> {
            ValidationUtil validationUtil = new ValidationUtil();
            List<FileError> errors = validationUtil.validateSalaryFile(file);
            if (errors.isEmpty()) {
                importedSalaries = FileUtil.processSalaryCsvFiles(file);
                tbvSalary.getItems().addAll(importedSalaries);
                showSuccessAlert("Import salary success dialog", "Import successfully");
            } else {
                showWarningAlert("Import salary warning dialog", "Import fail", errors);
            }
        });
        fileInput.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter csvFilter = new FileChooser
                    .ExtensionFilter("CSV Files (*.csv)", "*.csv");
            FileChooser.ExtensionFilter xlsxFilter = new FileChooser
                    .ExtensionFilter("Excel Files (*.xlsx)", "*.xlsx");

            fileChooser.getExtensionFilters().addAll(csvFilter, xlsxFilter);
            Optional<File> selectedFile = Optional.ofNullable(fileChooser.showOpenDialog(fileInput.getScene().getWindow()));
            if (selectedFile.isPresent()) {
                fileInput.setText(selectedFile.get().getName());
                file = selectedFile.get();
            }
        });
        tbvSalary.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                tbvSalary.prefHeightProperty().bind(tbvSalary.getScene().heightProperty());
                tbvSalary.prefWidthProperty().bind(tbvSalary.getScene().widthProperty());
            }
        });
        tbvSalary.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void bindingResource() {
        lbPensionType.textProperty().bind(ObservableResourceFactory.getStringBinding("pensionType"));
        lbNumber.textProperty().bind(ObservableResourceFactory.getStringBinding("number"));
        lbIdeNumber.textProperty().bind(ObservableResourceFactory.getStringBinding("ideNumber.required"));
        lbName.textProperty().bind(ObservableResourceFactory.getStringBinding("name.required"));
        lbDateCreation.textProperty().bind(ObservableResourceFactory.getStringBinding("dateCreation.required"));
        lbDateExpiration.textProperty().bind(ObservableResourceFactory.getStringBinding("dateExpiration"));
        lbSalDeclaration.textProperty().bind(ObservableResourceFactory.getStringBinding("label.salary.declaration"));
        btnSave.textProperty().bind(ObservableResourceFactory.getStringBinding("save"));
        btnReturn.textProperty().bind(ObservableResourceFactory.getStringBinding("return"));
        btnImport.textProperty().bind(ObservableResourceFactory.getStringBinding("import"));
        // Table view col
        avsNumberCol.textProperty().bind(ObservableResourceFactory.getStringBinding("avsNumber"));
        lastNameCol.textProperty().bind(ObservableResourceFactory.getStringBinding("employee.lastName"));
        firstNameCol.textProperty().bind(ObservableResourceFactory.getStringBinding("employee.firstName"));
        startDateCol.textProperty().bind(ObservableResourceFactory.getStringBinding("startDate"));
        endDateCol.textProperty().bind(ObservableResourceFactory.getStringBinding("endDate"));
        avsAmountCol.textProperty().bind(ObservableResourceFactory.getStringBinding("avs.ai.apg"));
        acAmountCol.textProperty().bind(ObservableResourceFactory.getStringBinding("ac"));
        afAmountCol.textProperty().bind(ObservableResourceFactory.getStringBinding("af"));
    }

    private void showSuccessAlert(String title, String headerText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.show();
    }

    private void showWarningAlert(String title, String headerText, List<FileError> fileErrors) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        List<String> errorMessages = fileErrors.stream()
                .map(e -> "At line " + e.getLineNumber() + ": " + e.getErrorMessage() + " value: " + e.getErrorValue())
                .collect(Collectors.toList());

        alert.setContentText(String.join("\n", errorMessages));
        alert.show();
    }
}
