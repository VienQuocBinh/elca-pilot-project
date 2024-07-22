package vn.elca.training.pilot_project_front.perspective;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jacpfx.api.annotations.perspective.Perspective;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.componentLayout.PerspectiveLayout;
import org.jacpfx.rcp.perspective.FXPerspective;
import org.jacpfx.rcp.util.FXUtil;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;
import vn.elca.training.pilot_project_front.util.StageManager;

@Perspective(id = PerspectiveId.EMPLOYER_DETAIL_PERSPECTIVE,
        name = "employerDetailPerspective",
        components = {
                ComponentId.EMPLOYER_DETAIL_CP,
                ComponentId.EMPLOYER_CALLBACK_CP
        },
        viewLocation = "/fxml/employerDetailPerspective.fxml",
        resourceBundleLocation = "bundles.languageBundle",
        active = false)
public class EmployerDetailPerspective implements FXPerspective {
    @FXML
    private VBox mainContainer;
    @FXML
    private VBox vbDetail;

    @Override
    public void handlePerspective(Message<Event, Object> message, PerspectiveLayout perspectiveLayout) {
        perspectiveLayout.registerRootComponent(mainContainer);
        perspectiveLayout.registerTargetLayoutComponent(PerspectiveId.EMPLOYER_DETAIL_PERSPECTIVE, vbDetail);
        // Set title for the perspective
        if (message.getMessageBody() instanceof String && !message.messageBodyEquals(FXUtil.MessageUtil.INIT)) {
            String title = (String) message.getMessageBody();
            Stage primaryStage = StageManager.getPrimaryStage();
            if (primaryStage != null) {
                Platform.runLater(() -> primaryStage.setTitle(title));
            }
        }
    }
}
