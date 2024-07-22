package vn.elca.training.pilot_project_front.component;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.context.Context;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;
import vn.elca.training.pilot_project_front.util.ObservableResourceFactory;
import vn.elca.training.pilot_project_front.util.StageManager;

@DeclarativeView(id = ComponentId.EMPLOYER_DETAIL_CP,
        name = "employerDetailCp",
        viewLocation = "/fxml/employerDetailCp.fxml",
        initialTargetLayoutId = PerspectiveId.EMPLOYER_DETAIL_PERSPECTIVE,
        resourceBundleLocation = "bundles.languageBundle")
public class EmployerDetailCp implements FXComponent {
    @Resource
    private Context context;
    @FXML
    private Button btnReturn;

    @Override
    public Node postHandle(Node node, Message<Event, Object> message) throws Exception {
        return null;
    }

    @Override
    public Node handle(Message<Event, Object> message) throws Exception {
        return null;
    }

    @PostConstruct
    public void onPostConstruct() {
        btnReturn.setOnMouseClicked(event -> {
            // Set back app title
            Stage primaryStage = StageManager.getPrimaryStage();
            if (primaryStage != null) {
                primaryStage.titleProperty().setValue(ObservableResourceFactory.getProperty().getString("title"));
            }
            context.send(PerspectiveId.HOME_PERSPECTIVE, "return");
        });
    }
}
