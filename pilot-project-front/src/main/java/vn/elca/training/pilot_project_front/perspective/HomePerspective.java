package vn.elca.training.pilot_project_front.perspective;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jacpfx.api.annotations.perspective.Perspective;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.componentLayout.PerspectiveLayout;
import org.jacpfx.rcp.perspective.FXPerspective;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;

@Perspective(id = PerspectiveId.HOME_PERSPECTIVE,
        name = "homePerspective",
        components = {
                ComponentId.HOME_SEARCH_EMPLOYER_CP,
                ComponentId.HOME_EMPLOYER_TABLE_CP
        },
        viewLocation = "/fxml/homeEmployerPerspective.fxml",
        resourceBundleLocation = "bundles.languageBundle")
public class HomePerspective implements FXPerspective {
    @FXML
    private VBox mainContainer;
    @FXML
    private HBox searchBox;
    @FXML
    private HBox employerTable;

    @Override
    public void handlePerspective(Message<Event, Object> message, PerspectiveLayout perspectiveLayout) {
        perspectiveLayout.registerRootComponent(mainContainer);
        perspectiveLayout.registerTargetLayoutComponent(PerspectiveId.HORIZONTAL_CONTAINER_TOP, searchBox);
        perspectiveLayout.registerTargetLayoutComponent(PerspectiveId.HORIZONTAL_CONTAINER_BOT, employerTable);
    }
}
