package vn.elca.training.pilot_project_front.controller;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Setter;
import vn.elca.training.pilot_project_front.callback.EmployerCreationCallback;
import vn.elca.training.pilot_project_front.config.GrpcConfig;
import vn.elca.training.pilot_project_front.constant.DatePattern;
import vn.elca.training.pilot_project_front.model.ErrorDetail;
import vn.elca.training.pilot_project_front.service.PensionTypeService;
import vn.elca.training.pilot_project_front.util.JsonStringify;
import vn.elca.training.pilot_project_front.util.ObservableResourceFactory;
import vn.elca.training.pilot_project_front.util.TextFieldUtil;
import vn.elca.training.proto.employer.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class EmployerCreatePopupController implements Initializable {
    private final EmployerServiceGrpc.EmployerServiceBlockingStub stub;
    private final ManagedChannel channel;
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
    private Label lbDateOrderError;
    //    @FXML
//    private Label lbDateOrderError;
    @FXML
    private ImageView infoName;
    @FXML
    private ImageView infoNumber;
    @FXML
    private ImageView infoIdeNumber;
    private ResourceBundle resourceBundle;
    @Setter
    private EmployerCreationCallback callback;

    public EmployerCreatePopupController() {
        channel = ManagedChannelBuilder
                .forAddress(GrpcConfig.ADDRESS, GrpcConfig.PORT)
                .usePlaintext()
                .build();
        stub = EmployerServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
        Platform.runLater(() -> {
            Stage stage = (Stage) tfName.getScene().getWindow();
            stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::handleWindowClose);
            EmployerNextNumberResponse employerNextNumber = stub.getEmployerNextNumber(Empty.newBuilder().build());
            lbNumberValue.setText(employerNextNumber.getNumber());
            PensionTypeService.getInstance().setCbPensionTypeMandatory(cbPensionType);
            PensionTypeService.getInstance().updateCbPensionType();
            TextFieldUtil.applyDateFilter(dpDateCreation.getEditor());
            dpDateCreation.promptTextProperty().bind(ObservableResourceFactory.getStringBinding("date.format"));
            dpDateCreation.setConverter(TextFieldUtil.dateStringConverter());
            TextFieldUtil.applyDateFilter(dpDateExpiration.getEditor());
            dpDateExpiration.promptTextProperty().bind(ObservableResourceFactory.getStringBinding("date.format"));
            dpDateExpiration.setConverter(TextFieldUtil.dateStringConverter());
            buildInfoTooltip();
            // Force Name text field can not input digit and special chars
            TextFieldUtil.applyAlphabeticFilter(tfName);
        });
    }

    @FXML
    private void handleSave(ActionEvent event) {
        boolean valid = validateInputs();
        if (!valid) return;
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.PATTERN);
            EmployerCreateRequest.Builder builder = EmployerCreateRequest.newBuilder()
                    .setName(tfName.getText())
                    .setIdeNumber(tfIdeNumber.getText())
                    .setPensionType(cbPensionType.getValue())
                    .setDateCreation(dpDateCreation.getValue().format(dateTimeFormatter));
            if (dpDateExpiration.getValue() != null)
                builder.setDateExpiration(dpDateExpiration.getValue().format(dateTimeFormatter));
            EmployerResponse employer = stub.createEmployer(builder.build());

            // Close the popup and reload the perspective
            Stage stage = (Stage) tfName.getScene().getWindow();
            stage.close();
            // Invoke the callback with the created employer
            if (callback != null) {
                callback.onEmployerCreated(employer);
            }
        } catch (StatusRuntimeException e) {
            // Show errors on UI
            showErrorDetails(JsonStringify.convertStringErrorDetailToList(e.getMessage()));
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        showConfirmationDialog();
    }

    private void handleWindowClose(WindowEvent event) {
        event.consume();
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        String title = resourceBundle.getString("dialog.confirmation.close.title");
        String header = resourceBundle.getString("dialog.confirmation.close.header");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                channel.shutdown();
                Stage stage = (Stage) tfName.getScene().getWindow();
                stage.close();
            }
        });
    }

    private void buildInfoTooltip() {
        Image infoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/info_icon.png")));
        infoName.setImage(infoImage);
        infoNumber.setImage(infoImage);
        infoIdeNumber.setImage(infoImage);
        Tooltip.install(infoName, new Tooltip(resourceBundle.getString("tooltip.name")));
        Tooltip.install(infoNumber, new Tooltip(resourceBundle.getString("tooltip.number")));
        Tooltip.install(infoIdeNumber, new Tooltip(resourceBundle.getString("tooltip.ideNumber")));
    }

    // Validate on UI layer
    private boolean validateInputs() {
        String errorStyleClass = "error";
        String regex = "^(CHE|ADM)-\\d{3}.\\d{3}.\\d{3}$";
        boolean isValid = true;
        if (tfName.getText().isEmpty()) {
            tfName.getStyleClass().add(errorStyleClass);
            lbNameError.setVisible(true);
            lbNameError.setText(resourceBundle.getString("error.name.required"));
            isValid = false;
        } else {
            tfName.getStyleClass().remove(errorStyleClass);
            lbNameError.setVisible(false);
        }
        if (tfIdeNumber.getText().isEmpty()) {
            tfIdeNumber.getStyleClass().add(errorStyleClass);
            lbIdeNumberError.setVisible(true);
            lbIdeNumberError.setText(resourceBundle.getString("error.ideNumber.required"));
            isValid = false;
        } else if (!tfIdeNumber.getText().matches(regex)) {
            tfIdeNumber.getStyleClass().add(errorStyleClass);
            lbIdeNumberError.setVisible(true);
            lbIdeNumberError.setText(resourceBundle.getString("error.ideNumber.format"));
            isValid = false;
        } else {
            tfIdeNumber.getStyleClass().remove(errorStyleClass);
            lbIdeNumberError.setVisible(false);
        }
        if (dpDateCreation.getValue() == null) {
            dpDateCreation.getStyleClass().add(errorStyleClass);
            lbDateCreationError.setText(resourceBundle.getString("error.dateCreation.required"));
            lbDateCreationError.setVisible(true);
            isValid = false;
        } else {
            dpDateCreation.getStyleClass().remove(errorStyleClass);
            lbDateCreationError.setVisible(false);
        }
        if (dpDateCreation.getValue() != null && dpDateExpiration.getValue() != null) {
            if (!dpDateCreation.getValue().isBefore(dpDateExpiration.getValue())) {
                dpDateCreation.getStyleClass().add(errorStyleClass);
                dpDateExpiration.getStyleClass().add(errorStyleClass);
                lbDateOrderError.setText(resourceBundle.getString("error.dateOrder"));
                lbDateOrderError.setVisible(true);
                isValid = false;
            } else {
                dpDateCreation.getStyleClass().remove(errorStyleClass);
                dpDateExpiration.getStyleClass().remove(errorStyleClass);
                lbDateOrderError.setVisible(false);
            }
        }
        return isValid;
    }

    // Handle validation error thrown from BE
    private void showErrorDetails(List<ErrorDetail> errorDetails) {
        String errorStyleClass = "error";
        tfName.getStyleClass().remove(errorStyleClass);
        lbNameError.setVisible(false);
        tfIdeNumber.getStyleClass().remove(errorStyleClass);
        lbIdeNumberError.setVisible(false);
        dpDateCreation.getStyleClass().remove(errorStyleClass);
        lbDateCreationError.setVisible(false);
        dpDateExpiration.getStyleClass().remove(errorStyleClass);
        lbDateOrderError.setVisible(false);

        for (ErrorDetail errorDetail : errorDetails) {
            switch (errorDetail.getFxErrorKey()) {
                case "error.name.required":
                    tfName.getStyleClass().add(errorStyleClass);
                    lbNameError.setVisible(true);
                    lbNameError.setText(resourceBundle.getString(errorDetail.getFxErrorKey()));
                    break;
                case "error.ideNumber.required":
                case "error.ideNumber.format":
                case "error.ideNumber.duplicate":
                    tfIdeNumber.getStyleClass().add(errorStyleClass);
                    lbIdeNumberError.setVisible(true);
                    lbIdeNumberError.setText(resourceBundle.getString(errorDetail.getFxErrorKey()));
                    break;
                case "error.dateCreation.format":
                case "error.dateCreation.required":
                    dpDateCreation.getStyleClass().add(errorStyleClass);
                    lbDateCreationError.setText(resourceBundle.getString(errorDetail.getFxErrorKey()));
                    lbDateCreationError.setVisible(true);
                    break;
                case "error.dateExpiration.format":
                    dpDateExpiration.getStyleClass().add(errorStyleClass);
                    lbDateOrderError.setText(resourceBundle.getString(errorDetail.getFxErrorKey()));
                    lbDateOrderError.setVisible(true);
                    break;
                case "error.dateOrder":
                    dpDateCreation.getStyleClass().add(errorStyleClass);
                    dpDateExpiration.getStyleClass().add(errorStyleClass);
                    lbDateOrderError.setText(resourceBundle.getString(errorDetail.getFxErrorKey()));
                    lbDateOrderError.setVisible(true);
                    break;
                default:
                    break;
            }
        }
    }
}
