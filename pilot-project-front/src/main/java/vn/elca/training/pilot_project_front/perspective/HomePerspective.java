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
import vn.elca.training.proto.employer.PensionTypeProto;

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
    private Pagination pgEmployer;

    private String name = "";
    private String number = "";
    private String ideNumber = "";
    private String dateCreation = "";
    private String dateExpiration = "";
    private PensionTypeProto pensionType = PensionTypeProto.NONE;

    @Override
    public void handlePerspective(Message<Event, Object> message, PerspectiveLayout perspectiveLayout) {
        perspectiveLayout.registerRootComponent(mainContainer);
        perspectiveLayout.registerTargetLayoutComponent(PerspectiveId.HORIZONTAL_CONTAINER_TOP, searchBox);
        perspectiveLayout.registerTargetLayoutComponent(PerspectiveId.HORIZONTAL_CONTAINER_BOT, employerTable);
        if (message.getMessageBody() instanceof ActionType
                && message.getTypedMessageBody(ActionType.class).equals(ActionType.RETURN)) {
            // Reload employer table view on returning
            context.send(ComponentId.EMPLOYER_CALLBACK_CP, EmployerSearchRequest.newBuilder()
                    .setNumber(number)
                    .setIdeNumber(ideNumber)
                    .setPensionType(pensionType)
                    .setName(name)
                    .setDateCreation(dateCreation)
                    .setDateExpiration(dateExpiration)
                    .setPagingRequest(PagingRequest.newBuilder()
                            .setPageIndex(pgEmployer.getCurrentPageIndex())
                            .build())
                    .build());
        } else if (message.getMessageBody() instanceof PagingResponse) {
            // From init table of HomeEmployerTableCp
            PagingResponse pagingResponse = message.getTypedMessageBody(PagingResponse.class);
            pgEmployer.setPageCount(pagingResponse.getTotalPages());
            pgEmployer.setVisible(pagingResponse.getTotalPages() != 0);
            lbTotalElements.setText("Total items: " + pagingResponse.getTotalElements());
        } else if (message.getMessageBody() instanceof EmployerSearchRequest) {
            // Get from HomeSearchEmployerCp to append paging info then send to callback
            EmployerSearchRequest searchRequest = message.getTypedMessageBody(EmployerSearchRequest.class);
            searchRequest.toBuilder().setPagingRequest(PagingRequest.newBuilder()
                    .setPageIndex(0)
                    .build());
            // Store search params
            name = searchRequest.getName();
            number = searchRequest.getNumber();
            ideNumber = searchRequest.getIdeNumber();
            dateCreation = searchRequest.getDateCreation();
            dateExpiration = searchRequest.getDateExpiration();
            pensionType = searchRequest.getPensionType();
            context.send(ComponentId.EMPLOYER_CALLBACK_CP, searchRequest);
        }
    }

    @PostConstruct
    public void onPostConstruct() {
        pgEmployer.setMaxPageIndicatorCount(5);
        pgEmployer.currentPageIndexProperty().addListener((observable, oldValue, newValue) ->
                // Send message to callback
                context.send(ComponentId.EMPLOYER_CALLBACK_CP, EmployerSearchRequest.newBuilder()
                        .setNumber(number)
                        .setIdeNumber(ideNumber)
                        .setPensionType(pensionType)
                        .setName(name)
                        .setDateCreation(dateCreation)
                        .setDateExpiration(dateExpiration)
                        .setPagingRequest(PagingRequest.newBuilder()
                                .setPageIndex(pgEmployer.getCurrentPageIndex())
                                .build())
                        .build())
        );
    }

    private void storeSearchParam(EmployerSearchRequest searchRequest) {

    }
}
