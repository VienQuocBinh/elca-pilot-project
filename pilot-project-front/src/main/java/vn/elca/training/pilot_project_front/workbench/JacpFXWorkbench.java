package vn.elca.training.pilot_project_front.workbench;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jacpfx.api.annotations.workbench.Workbench;
import org.jacpfx.api.componentLayout.WorkbenchLayout;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.components.toolBar.JACPToolBar;
import org.jacpfx.rcp.workbench.FXWorkbench;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;
import vn.elca.training.pilot_project_front.util.ObservableResourceFactory;

import java.util.Locale;
import java.util.ResourceBundle;

@Workbench(id = "id1",
        name = "workbench",
        perspectives = {
                PerspectiveId.HOME_PERSPECTIVE,
                PerspectiveId.EMPLOYER_DETAIL_PERSPECTIVE
        })
public class JacpFXWorkbench implements FXWorkbench {
    @Override
    public void postHandle(FXComponentLayout fxComponentLayout) {
        JACPToolBar toolBar = fxComponentLayout.getRegisteredToolBar(ToolbarPosition.NORTH);
        // Multilingual: Set locale to translate
        ComboBox<Locale> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(Locale.FRENCH, Locale.ENGLISH);
        comboBox.getSelectionModel().selectFirst();
        toolBar.addAllOnEnd(comboBox);

        comboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        ObservableResourceFactory.setProperty(ResourceBundle.getBundle("bundles/languageBundle", newValue));
                    }
                });
    }

    @Override
    public void handleInitialLayout(Message<Event, Object> message, WorkbenchLayout<Node> workbenchLayout, Stage stage) {
        workbenchLayout.setWorkbenchXYSize(1050, 768);
        workbenchLayout.registerToolBar(ToolbarPosition.NORTH);
        workbenchLayout.setStyle(StageStyle.DECORATED);
    }
}
