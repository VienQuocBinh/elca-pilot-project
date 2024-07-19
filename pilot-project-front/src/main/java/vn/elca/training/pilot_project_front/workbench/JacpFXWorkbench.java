package vn.elca.training.pilot_project_front.workbench;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jacpfx.api.annotations.workbench.Workbench;
import org.jacpfx.api.componentLayout.WorkbenchLayout;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.workbench.FXWorkbench;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;
import vn.elca.training.pilot_project_front.util.ObservableResourceFactory;

import java.util.Locale;
import java.util.ResourceBundle;

@Workbench(id = "id1",
        name = "workbench",
        perspectives = {
                PerspectiveId.HOME_PERSPECTIVE,
//                PerspectiveId.ADD_EMPLOYER_PERSPECTIVE
        })
public class JacpFXWorkbench implements FXWorkbench {
    @Override
    public void postHandle(FXComponentLayout fxComponentLayout) {
        // Multilingual: Set locale to translate
        ObservableResourceFactory.setProperty(ResourceBundle.getBundle("bundles/languageBundle", Locale.FRANCE));

    }

    @Override
    public void handleInitialLayout(Message<Event, Object> message, WorkbenchLayout<Node> workbenchLayout, Stage stage) {
        workbenchLayout.setWorkbenchXYSize(1050, 768);
        workbenchLayout.registerToolBar(ToolbarPosition.NORTH);
        workbenchLayout.setStyle(StageStyle.DECORATED);
    }
}
