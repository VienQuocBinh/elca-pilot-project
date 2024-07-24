package vn.elca.training.pilot_project_front.controller;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import vn.elca.training.proto.employer.EmployerCreateRequest;
import vn.elca.training.proto.employer.EmployerResponse;
import vn.elca.training.proto.employer.EmployerServiceGrpc;
import vn.elca.training.proto.employer.PensionTypeProto;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EmployerCreatePopupController implements Initializable {
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
    private TextField tfNumber;
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
    private Label lbDateExpiration;
    @FXML
    private DatePicker dpDateExpiration;
    @FXML
    private Label lbDateError;

    private EmployerServiceGrpc.EmployerServiceBlockingStub stub;
    private ResourceBundle resourceBundle;

    public EmployerCreatePopupController() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
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
            cbPensionType.getItems().addAll(PensionTypeProto.REGIONAL, PensionTypeProto.PROFESSIONAL);
            cbPensionType.getSelectionModel().selectFirst();

            dpDateCreation.setValue(LocalDate.now());
            dpDateExpiration.setValue(LocalDate.now());
        });
    }

    @FXML
    private void handleSave(ActionEvent event) {
        boolean valid = validateInputs();
        if (!valid) return;

        EmployerResponse employer = stub.createEmployer(EmployerCreateRequest.newBuilder()
                .setName(tfName.getText())
                .setIdeNumber(tfIdeNumber.getText())
                .setPensionType(cbPensionType.getValue())
                .build());
        // Close the popup and reload the perspective
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
        // Reload your perspective here
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
                Stage stage = (Stage) tfName.getScene().getWindow();
                stage.close();
            }
        });
    }

    private boolean validateInputs() {
        String errorStyleClass = "error";
        String regex = "^(CHE|ADM)-\\d{3}.\\d{3}.\\d{3}";
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
        if (dpDateCreation.getValue() != null && dpDateExpiration.getValue() != null) {
            if (!dpDateCreation.getValue().isBefore(dpDateExpiration.getValue())) {
                dpDateCreation.getStyleClass().add(errorStyleClass);
                dpDateExpiration.getStyleClass().add(errorStyleClass);
                lbDateError.setText(resourceBundle.getString("error.dateOrder"));
                lbDateError.setVisible(true);
                isValid = false;
            } else {
                dpDateCreation.getStyleClass().remove(errorStyleClass);
                dpDateExpiration.getStyleClass().remove(errorStyleClass);
                lbDateError.setVisible(false);
            }
        }
        return isValid;
    }
}
