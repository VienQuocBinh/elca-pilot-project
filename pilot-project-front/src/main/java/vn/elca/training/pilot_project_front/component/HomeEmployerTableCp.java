package vn.elca.training.pilot_project_front.component;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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
import vn.elca.training.pilot_project_front.model.Employer;
import vn.elca.training.pilot_project_front.util.ObservableResourceFactory;
import vn.elca.training.proto.employer.EmployerListResponse;
import vn.elca.training.proto.employer.EmployerSearchRequest;
import vn.elca.training.proto.employer.Empty;

import java.util.List;
import java.util.Optional;
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
    private TableView<Employer> tbvEmployer;
    @FXML
    private TableColumn<Employer, Void> actionCol;

    @PostConstruct
    public void onPostConstruct() {
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
            EmployerListResponse listResponse = message.getTypedMessageBody(EmployerListResponse.class);
            List<Employer> collect = listResponse.getEmployersList().stream()
                    .map(employer -> Employer.builder()
                            .id(employer.getId())
                            .pensionType(employer.getPensionType())
                            .name(employer.getName())
                            .number(employer.getNumber())
                            .ideNumber(employer.getIdeNumber())
                            .createdDate(employer.getCreatedDate())
                            .expiredDate(employer.getExpiredDate())
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
                    Employer employer = getTableView().getItems().get(getIndex());
                    log.info("Button clicked for: " + employer);
                    context.send(PerspectiveId.EMPLOYER_DETAIL_PERSPECTIVE, employer.getName());
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
}
