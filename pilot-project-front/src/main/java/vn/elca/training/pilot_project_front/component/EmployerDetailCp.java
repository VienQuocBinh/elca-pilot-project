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
import model.SalaryError;
import model.SalaryFileResult;
import org.apache.commons.lang3.StringUtils;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.context.Context;
import util.FileUtil;
import util.SalaryHeaderBuild;
import vn.elca.training.pilot_project_front.constant.ActionType;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.pilot_project_front.constant.DatePattern;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;
import vn.elca.training.pilot_project_front.model.*;
import vn.elca.training.pilot_project_front.service.PensionTypeService;
import vn.elca.training.pilot_project_front.util.*;
import vn.elca.training.proto.common.PagingRequest;
import vn.elca.training.proto.employer.EmployerUpdateRequest;
import vn.elca.training.proto.employer.PensionTypeProto;
import vn.elca.training.proto.salary.SalaryCreateRequest;
import vn.elca.training.proto.salary.SalaryListRequest;
import vn.elca.training.proto.salary.SalaryListResponse;
import vn.elca.training.proto.salary.SalaryResponse;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@DeclarativeView(id = ComponentId.EMPLOYER_DETAIL_CP,
        name = "employerDetailCp",
        viewLocation = "/fxml/employerDetailCp.fxml",
        initialTargetLayoutId = PerspectiveId.EMPLOYER_DETAIL_PERSPECTIVE,
        resourceBundleLocation = "bundles.languageBundle")
public class EmployerDetailCp implements FXComponent {
    private static final String ERROR_STYLE_CLASS = "error";
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
    private Label lbNameError;
    @FXML
    private Label lbNumber;
    @FXML
    private Label lbNumberValue;
    @FXML
    private Label lbIdeNumber;
    @FXML
    private TextField tfIdeNumber;
    @FXML
    private Label lbIdeNumberError;
    @FXML
    private Label lbDateCreation;
    @FXML
    private DatePicker dpDateCreation;
    @FXML
    private Label lbDateCreationError;
    @FXML
    private Label lbDateExpiration;
    @FXML
    private DatePicker dpDateExpiration;
    @FXML
    private Label lbDateExpirationError;
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
    @FXML
    private Label lbTotalElements;
    @FXML
    private Pagination pgSalary;
    @Getter
    private File file;
    private Employer employer;
    private List<Salary> importedSalaries = new ArrayList<>();

    @Override
    public Node postHandle(Node node, Message<Event, Object> message) {
        if (message.getMessageBody() instanceof Employer) {
            // From EmployerDetailPerspective
            clearErrors();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.PATTERN);
            employer = message.getTypedMessageBody(Employer.class);
            lbNumberValue.setText(employer.getNumber());
            tfName.setText(employer.getName());
            tfIdeNumber.setTextFormatter(null); // Clear formatter
            tfIdeNumber.setText(employer.getIdeNumber());
            tfIdeNumber.setTextFormatter(TextFieldUtil.applyIdeNumberTextFormatter(tfIdeNumber));
            PensionTypeService.getInstance().setCbPensionTypeMandatory(cbPensionType);
            PensionTypeService.getInstance().updateCbPensionType();
            cbPensionType.setValue(PensionTypeUtil.getLocalizedPensionType(employer.getPensionType()));
            dpDateCreation.setValue(LocalDate.parse(employer.getDateCreation(), formatter));
            if (!StringUtils.isBlank(employer.getDateExpiration()))
                dpDateExpiration.setValue(LocalDate.parse(employer.getDateExpiration(), formatter));
            else dpDateExpiration.setValue(null);
            // To get salaries and paging
            context.send(ComponentId.SALARY_CALLBACK_CP, SalaryListRequest.newBuilder()
                    .setEmployerId(employer.getId())
                    .setPagingRequest(PagingRequest.newBuilder()
                            .setPageIndex(pgSalary.getCurrentPageIndex())
                            .build())
                    .build());
        } else if (message.getMessageBody() instanceof EmployerResponseWrapper) {
            // From update stub callback or update catch ALREADY_EXISTS code
            EmployerResponseWrapper employerResponseWrapper = message.getTypedMessageBody(EmployerResponseWrapper.class);
            if (employerResponseWrapper.getActionType().equals(ActionType.UPDATE)
                    || employerResponseWrapper.getActionType().equals(ActionType.RELOAD)) {
                // Update Salary table
                context.send(ComponentId.SALARY_CALLBACK_CP, SalaryListRequest.newBuilder()
                        .setEmployerId(employer.getId())
                        .setPagingRequest(PagingRequest.newBuilder()
                                .setPageIndex(pgSalary.getCurrentPageIndex())
                                .build())
                        .build());
                if (employerResponseWrapper.getActionType().equals(ActionType.UPDATE))
                    showSuccessAlert("Update employer", "Update employer successfully");
            }
        } else if (message.getMessageBody() instanceof ExceptionMessage) {
            // From EmployerCallbackCp catch
            String errorMessage = message.getTypedMessageBody(ExceptionMessage.class).getErrorMessage();
            showErrorDetails(JsonStringify.convertStringErrorDetailToList(errorMessage));
        } else if (message.getMessageBody() instanceof SalaryListResponse) {
            // From SalaryCallbackCp to get salaries of Employer
            SalaryListResponse listResponse = message.getTypedMessageBody(SalaryListResponse.class);
            List<SalaryResponse> salariesResponse = listResponse.getSalariesList();
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
            // Default sort
            tbvSalary.getSortOrder().add(lastNameCol);
            tbvSalary.getSortOrder().add(firstNameCol);
            tbvSalary.sort(); // Trigger sort
            // Paging
            pgSalary.setPageCount(listResponse.getPagingResponse().getTotalPages());
            pgSalary.setVisible(listResponse.getPagingResponse().getTotalPages() != 0);
            lbTotalElements.setText("Total: " + listResponse.getPagingResponse().getTotalElements());
        }
        return null;
    }

    @Override
    public Node handle(Message<Event, Object> message) {
        return null;
    }

    @PostConstruct
    public void onPostConstruct() {
        bindingResource();
        btnSave.setOnMouseClicked(event -> {
            if (validateInputs()) {
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
                        .setName(tfName.getText().trim())
                        .setDateCreation(dpDateCreation.getValue().format(dateFormater))
                        .setDateExpiration(dpDateExpiration.getValue() != null ? dpDateExpiration.getValue().format(dateFormater) : "")
                        .setIdeNumber(tfIdeNumber.getText())
                        .setPensionType(cbPensionType.getSelectionModel().getSelectedItem())
                        .addAllSalaries(salaryCreateRequests)
                        .build());
            }
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
            SalaryFileResult salaryFileResult = FileUtil.processSalaryCsvFiles(file);
            importedSalaries = salaryFileResult.getSalaries().stream().map(this::mapFromFileModel).collect(Collectors.toList());
            List<SalaryError> errors = salaryFileResult.getErrors();
            if (errors.isEmpty()) {
                tbvSalary.getItems().addAll(importedSalaries);
                showSuccessAlert("Import salary success dialog", "Import successfully");
            } else {
                String[] header = SalaryHeaderBuild.buildErrorHeader();
                String filename = FileUtil.writErrorCsvFile(file.getName(), header, errors.stream().map(SalaryError::toStringArray).collect(Collectors.toList()));
                showWarningAlert("Import Error", ("Please check file: " + filename + " for detail"));
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
                btnImport.setDisable(false);
            }
        });
        tbvSalary.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                tbvSalary.prefHeightProperty().bind(tbvSalary.getScene().heightProperty());
                tbvSalary.prefWidthProperty().bind(tbvSalary.getScene().widthProperty());
            }
        });
        tbvSalary.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        pgSalary.setMaxPageIndicatorCount(5);
        pgSalary.currentPageIndexProperty().addListener((observable, oldValue, newValue) ->
                context.send(ComponentId.SALARY_CALLBACK_CP, SalaryListRequest.newBuilder()
                        .setEmployerId(employer.getId())
                        .setPagingRequest(PagingRequest.newBuilder()
                                .setPageIndex(pgSalary.getCurrentPageIndex())
                                .build())
                        .build()));
    }

    private void bindingResource() {
        lbPensionType.textProperty().bind(ObservableResourceFactory.getStringBinding("pensionType.required"));
        lbNumber.textProperty().bind(ObservableResourceFactory.getStringBinding("number"));
        lbIdeNumber.textProperty().bind(ObservableResourceFactory.getStringBinding("ideNumber.required"));
        lbName.textProperty().bind(ObservableResourceFactory.getStringBinding("name.required"));
        lbDateCreation.textProperty().bind(ObservableResourceFactory.getStringBinding("dateCreation.required"));
        lbDateExpiration.textProperty().bind(ObservableResourceFactory.getStringBinding("dateExpiration"));
        lbSalDeclaration.textProperty().bind(ObservableResourceFactory.getStringBinding("label.salary.declaration"));
        TextFieldUtil.applyDateFilter(dpDateCreation.getEditor());
        dpDateCreation.promptTextProperty().bind(ObservableResourceFactory.getStringBinding("date.format"));
        dpDateCreation.setConverter(TextFieldUtil.dateStringConverter());
        TextFieldUtil.applyDateFilter(dpDateExpiration.getEditor());
        dpDateExpiration.promptTextProperty().bind(ObservableResourceFactory.getStringBinding("date.format"));
        dpDateExpiration.setConverter(TextFieldUtil.dateStringConverter());
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

    // Validate on UI layer
    private boolean validateInputs() {
        String regex = "^(CHE|ADM)-\\d{3}.\\d{3}.\\d{3}$";
        ResourceBundle resourceBundle = ObservableResourceFactory.getProperty();
        boolean isValid = true;
        if (tfName.getText().isEmpty()) {
            tfName.getStyleClass().add(ERROR_STYLE_CLASS);
            lbNameError.setVisible(true);
            lbNameError.setText(resourceBundle.getString("error.name.required"));
            isValid = false;
        } else {
            tfName.getStyleClass().remove(ERROR_STYLE_CLASS);
            lbNameError.setVisible(false);
        }
        if (tfIdeNumber.getText().isEmpty()) {
            tfIdeNumber.getStyleClass().add(ERROR_STYLE_CLASS);
            lbIdeNumberError.setVisible(true);
            lbIdeNumberError.setText(resourceBundle.getString("error.ideNumber.required"));
            isValid = false;
        } else if (!tfIdeNumber.getText().matches(regex)) {
            tfIdeNumber.getStyleClass().add(ERROR_STYLE_CLASS);
            lbIdeNumberError.setVisible(true);
            lbIdeNumberError.setText(resourceBundle.getString("error.ideNumber.format"));
            isValid = false;
        } else {
            tfIdeNumber.getStyleClass().remove(ERROR_STYLE_CLASS);
            lbIdeNumberError.setVisible(false);
        }
        ValidationUtil.validateDateFields(dpDateCreation, dpDateExpiration,
                lbDateCreationError, lbDateExpirationError,
                ERROR_STYLE_CLASS, resourceBundle);
        return isValid;
    }

    // Show errors thrown from BE and catch at FE Callback
    private void showErrorDetails(List<ErrorDetail> errorDetails) {
        ResourceBundle resourceBundle = ObservableResourceFactory.getProperty();
        clearErrors();
        for (ErrorDetail errorDetail : errorDetails) {
            switch (errorDetail.getFxErrorKey()) {
                case "error.name.required":
                    tfName.getStyleClass().add(ERROR_STYLE_CLASS);
                    lbNameError.setVisible(true);
                    lbNameError.setText(resourceBundle.getString(errorDetail.getFxErrorKey()));
                    break;
                case "error.ideNumber.required":
                case "error.ideNumber.format":
                case "error.ideNumber.duplicate":
                    tfIdeNumber.getStyleClass().add(ERROR_STYLE_CLASS);
                    lbIdeNumberError.setVisible(true);
                    lbIdeNumberError.setText(resourceBundle.getString(errorDetail.getFxErrorKey()));
                    break;
                case "error.dateCreation.format":
                case "error.dateCreation.required":
                    dpDateCreation.getStyleClass().add(ERROR_STYLE_CLASS);
                    lbDateCreationError.setText(resourceBundle.getString(errorDetail.getFxErrorKey()));
                    lbDateCreationError.setVisible(true);
                    break;
                case "error.dateExpiration.format":
                    dpDateExpiration.getStyleClass().add(ERROR_STYLE_CLASS);
                    lbDateExpirationError.setText(resourceBundle.getString(errorDetail.getFxErrorKey()));
                    lbDateExpirationError.setVisible(true);
                    break;
                case "error.dateOrder":
                    dpDateCreation.getStyleClass().add(ERROR_STYLE_CLASS);
                    dpDateExpiration.getStyleClass().add(ERROR_STYLE_CLASS);
                    lbDateExpirationError.setText(resourceBundle.getString(errorDetail.getFxErrorKey()));
                    lbDateExpirationError.setVisible(true);
                    break;
                default:
                    break;
            }
        }
    }

    private void showSuccessAlert(String title, String headerText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.show();
    }

    private void showWarningAlert(String title, String headerText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.show();
    }

    private void clearErrors() {
        tfName.getStyleClass().remove(ERROR_STYLE_CLASS);
        lbNameError.setVisible(false);
        tfIdeNumber.getStyleClass().remove(ERROR_STYLE_CLASS);
        lbIdeNumberError.setVisible(false);
        dpDateCreation.getStyleClass().remove(ERROR_STYLE_CLASS);
        dpDateExpiration.getStyleClass().remove(ERROR_STYLE_CLASS);
        lbDateExpirationError.setVisible(false);
    }

    private Salary mapFromFileModel(model.Salary fileSalary) {
        return Salary.builder()
                .id(fileSalary.getId())
                .avsNumber(fileSalary.getAvsNumber())
                .lastName(fileSalary.getLastName())
                .firstName(fileSalary.getFirstName())
                .startDate(fileSalary.getStartDate())
                .endDate(fileSalary.getEndDate())
                .avsAmount(fileSalary.getAvsAmount())
                .acAmount(fileSalary.getAcAmount())
                .afAmount(fileSalary.getAfAmount())
                .build();
    }
}
