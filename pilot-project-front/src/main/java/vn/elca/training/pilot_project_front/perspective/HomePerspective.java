package vn.elca.training.pilot_project_front.perspective;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.perspective.Perspective;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.componentLayout.PerspectiveLayout;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.perspective.FXPerspective;
import vn.elca.training.pilot_project_front.constant.ActionType;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;
import vn.elca.training.proto.common.PagingRequest;
import vn.elca.training.proto.common.PagingResponse;
import vn.elca.training.proto.employer.EmployerSearchRequest;

@Perspective(id = PerspectiveId.HOME_PERSPECTIVE,
        name = "homePerspective",
        components = {
                ComponentId.HOME_SEARCH_EMPLOYER_CP,
                ComponentId.HOME_EMPLOYER_TABLE_CP,
                ComponentId.EMPLOYER_CALLBACK_CP
        },
        viewLocation = "/fxml/homeEmployerPerspective.fxml",
        resourceBundleLocation = "bundles.languageBundle")
public class HomePerspective implements FXPerspective {
    @Resource
    private Context context;
    @FXML
    private VBox mainContainer;
    @FXML
    private HBox searchBox;
    @FXML
    private HBox employerTable;
    @FXML
    private Label lbTotalElements;
    @FXML
    private Pagination pagination;
    private int totalPages;

    @Override
    public void handlePerspective(Message<Event, Object> message, PerspectiveLayout perspectiveLayout) {
        perspectiveLayout.registerRootComponent(mainContainer);
        perspectiveLayout.registerTargetLayoutComponent(PerspectiveId.HORIZONTAL_CONTAINER_TOP, searchBox);
        perspectiveLayout.registerTargetLayoutComponent(PerspectiveId.HORIZONTAL_CONTAINER_BOT, employerTable);
        if (message.getMessageBody() instanceof ActionType
                && message.getTypedMessageBody(ActionType.class).equals(ActionType.RETURN)) {
            // Reload employer table view on returning
            context.send(ComponentId.EMPLOYER_CALLBACK_CP, EmployerSearchRequest.newBuilder().build());
        } else if (message.getMessageBody() instanceof PagingResponse) {
            // From init table of HomeEmployerTableCp
            PagingResponse pagingResponse = message.getTypedMessageBody(PagingResponse.class);
            totalPages = pagingResponse.getTotalPages();
            pagination.setPageCount(totalPages);
            pagination.setVisible(true);
            lbTotalElements.setText("Total items: " + pagingResponse.getTotalElements());
        } else if (message.getMessageBody() instanceof EmployerSearchRequest) {
            // Get from HomeSearchEmployerCp to append paging info then send to callback
            EmployerSearchRequest searchRequest = message.getTypedMessageBody(EmployerSearchRequest.class);
            searchRequest.toBuilder().setPagingRequest(PagingRequest.newBuilder()
                    .setPageIndex(pagination.getCurrentPageIndex())
                    .build());
            context.send(ComponentId.EMPLOYER_CALLBACK_CP, searchRequest);
        }
    }

    @PostConstruct
    public void onPostConstruct() {
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageCount(totalPages);
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) ->
                // Send message to callback
                context.send(ComponentId.EMPLOYER_CALLBACK_CP, EmployerSearchRequest.newBuilder()
                        .setPagingRequest(PagingRequest.newBuilder()
                                .setPageIndex(pagination.getCurrentPageIndex())
                                .build())
                        .build())
        );
    }
}
