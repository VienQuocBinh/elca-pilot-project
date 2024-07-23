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
import vn.elca.training.proto.employer.EmployerServiceGrpc;
import vn.elca.training.proto.employer.PensionTypeProto;

import java.net.URL;
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
        });
    }

    @FXML
    private void handleSave(ActionEvent event) {
//        EmployerResponse employer = stub.createEmployer(EmployerCreateRequest.newBuilder()
//                .setName(tfName.getText())
//                .setIdeNumber(tfIdeNumber.getText())
//                .setPensionType()
//                .build());
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
}
