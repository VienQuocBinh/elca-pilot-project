package vn.elca.training.pilot_project_front.component;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.util.FXUtil;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;
import vn.elca.training.pilot_project_front.controller.EmployerCreatePopupController;
import vn.elca.training.pilot_project_front.model.Employer;
import vn.elca.training.pilot_project_front.util.ObservableResourceFactory;
import vn.elca.training.proto.employer.EmployerListResponse;
import vn.elca.training.proto.employer.EmployerSearchRequest;
import vn.elca.training.proto.employer.Empty;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@DeclarativeView(id = ComponentId.HOME_EMPLOYER_TABLE_CP,
        name = "homeEmployerTableCp",
        viewLocation = "/fxml/homeEmployerTableCp.fxml",
        resourceBundleLocation = "bundles.languageBundle",
        initialTargetLayoutId = PerspectiveId.HORIZONTAL_CONTAINER_BOT
)
public class HomeEmployerTableCp implements FXComponent {
    private final Logger log = Logger.getLogger(HomeEmployerTableCp.class.getName());
    @Resource
    private Context context;
    @FXML
    private Button btnAdd;
    @FXML
    private TableView<Employer> tbvEmployer;
    @FXML
    private TableColumn<Employer, Void> actionCol;
    @FXML
    private TableColumn<Employer, String> pensionTypeCol;
    @FXML
    private TableColumn<Employer, String> numberCol;
    @FXML
    private TableColumn<Employer, String> ideNumberCol;
    @FXML
    private TableColumn<Employer, String> nameCol;
    @FXML
    private TableColumn<Employer, String> dateCreationCol;
    @FXML
    private TableColumn<Employer, String> dateExpirationCol;
    private Stage stagePopup;

    @PostConstruct
    public void onPostConstruct() {
        bindingResource();
        context.send(ComponentId.EMPLOYER_CALLBACK_CP, EmployerSearchRequest.newBuilder().build());
        addButtonToTable();
        // Make TableView resize with the window. Delay the binding until the scene is available
        tbvEmployer.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                tbvEmployer.prefHeightProperty().bind(tbvEmployer.getScene().heightProperty());
                tbvEmployer.prefWidthProperty().bind(tbvEmployer.getScene().widthProperty());
            }
        });
        tbvEmployer.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @Override
    public Node postHandle(Node node, Message<Event, Object> message) throws Exception {
        return null;
    }

    @Override
    public Node handle(Message<Event, Object> message) throws Exception {
        if (message.getMessageBody() instanceof String && !message.messageBodyEquals(FXUtil.MessageUtil.INIT)) {
            log.info(message.getTypedMessageBody(String.class));
        } else if (message.getMessageBody() instanceof Empty) {
            // Reload the employer list
            context.send(ComponentId.EMPLOYER_CALLBACK_CP, EmployerSearchRequest.newBuilder().build());
        } else if (message.getMessageBody() instanceof EmployerListResponse) {
            // For init table data and
            EmployerListResponse listResponse = message.getTypedMessageBody(EmployerListResponse.class);
            List<Employer> collect = listResponse.getEmployersList().stream()
                    .map(employer -> Employer.builder()
                            .id(employer.getId())
                            .pensionType(employer.getPensionType())
                            .name(employer.getName())
                            .number(employer.getNumber())
                            .ideNumber(employer.getIdeNumber())
                            .dateCreation(employer.getDateCreation())
                            .dateExpiration(employer.getDateExpiration())
                            .build())
                    .collect(Collectors.toList());

            tbvEmployer.getItems().clear();
            tbvEmployer.setItems(FXCollections.observableList(collect));
        }
        return null;
    }

    private void addButtonToTable() {
        Callback<TableColumn<Employer, Void>, TableCell<Employer, Void>> cellFactory = param -> new TableCell<Employer, Void>() {
            private final Button btnDetail = new Button("Detail");
            private final Button btnDelete = new Button("Delete");
            private final HBox pane = new HBox(10, btnDetail, btnDelete);

            {
                btnDetail.setOnMouseClicked(event -> {
                    // Switch to EmployerDetailPerspective
                    Employer employer = getTableView().getItems().get(getIndex());
                    log.info("Button clicked for: " + employer);
                    context.send(PerspectiveId.EMPLOYER_DETAIL_PERSPECTIVE, employer);
                });
                btnDelete.setOnMouseClicked(event -> {
                    Employer employer = getTableView().getItems().get(getIndex());
                    log.info("Button clicked for: " + employer);
                    Optional<ButtonType> buttonType = showConfirmDialog(employer);
                    if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
                        context.send(ComponentId.EMPLOYER_CALLBACK_CP, employer.getId());
                    }
                });
                btnDelete.getStyleClass().add("delete-button");
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    pane.setAlignment(Pos.CENTER);
                    setGraphic(pane);
                }
            }
        };

        actionCol.setCellFactory(cellFactory);
    }

    private Optional<ButtonType> showConfirmDialog(Employer employer) {
        String title = ObservableResourceFactory.getProperty().getString("dialog.confirmation.delete.title");
        String header = ObservableResourceFactory.getProperty().getString("dialog.confirmation.delete.header");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header + " \"" + employer.getName() + "\"?");
        return alert.showAndWait();
    }

    private void bindingResource() {
        btnAdd.textProperty().bind(ObservableResourceFactory.getStringBinding("add"));
        pensionTypeCol.textProperty().bind(ObservableResourceFactory.getStringBinding("pensionType"));
        numberCol.textProperty().bind(ObservableResourceFactory.getStringBinding("number"));
        ideNumberCol.textProperty().bind(ObservableResourceFactory.getStringBinding("ideNumber"));
        nameCol.textProperty().bind(ObservableResourceFactory.getStringBinding("name"));
        dateCreationCol.textProperty().bind(ObservableResourceFactory.getStringBinding("dateCreation"));
        dateExpirationCol.textProperty().bind(ObservableResourceFactory.getStringBinding("dateExpiration"));
        btnAdd.setOnMouseClicked(event -> showCreatePopup());
    }

    private void showCreatePopup() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/employerCreatePopup.fxml"));
            Locale locale = ObservableResourceFactory.resourceProperty().get().getLocale();
            fxmlLoader.setResources(ResourceBundle.getBundle("bundles.languageBundle", locale));
            Parent parent = fxmlLoader.load();
            // Add new employer to current observable list by callback
            EmployerCreatePopupController popupController = fxmlLoader.getController();
            popupController.setCallback(employer -> tbvEmployer.getItems().add(Employer.builder()
                    .id(employer.getId())
                    .name(employer.getName())
                    .number(employer.getNumber())
                    .ideNumber(employer.getIdeNumber())
                    .pensionType(employer.getPensionType())
                    .dateCreation(employer.getDateCreation())
                    .dateExpiration(employer.getDateExpiration())
                    .build()));

            stagePopup = new Stage();
            stagePopup.initModality(Modality.APPLICATION_MODAL);
            stagePopup.setTitle(ObservableResourceFactory.getProperty().getString("employer.add"));
            stagePopup.setScene(new Scene(parent));
//            stagePopup.setResizable(false);
            stagePopup.showAndWait();

        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }
}
