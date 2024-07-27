package vn.elca.training.pilot_project_front.workbench;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.workbench.Workbench;
import org.jacpfx.api.componentLayout.WorkbenchLayout;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.components.toolBar.JACPToolBar;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.workbench.FXWorkbench;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.pilot_project_front.constant.LocaleString;
import vn.elca.training.pilot_project_front.constant.PerspectiveId;
import vn.elca.training.pilot_project_front.service.PensionTypeService;
import vn.elca.training.pilot_project_front.util.ObservableResourceFactory;
import vn.elca.training.proto.common.PagingRequest;
import vn.elca.training.proto.employer.EmployerSearchRequest;

import java.util.Locale;
import java.util.ResourceBundle;

@Workbench(id = "id1",
        name = "workbench",
        perspectives = {
                PerspectiveId.HOME_PERSPECTIVE,
                PerspectiveId.EMPLOYER_DETAIL_PERSPECTIVE
        })
public class JacpFXWorkbench implements FXWorkbench {
    @Resource
    private Context context;

    @Override
    public void postHandle(FXComponentLayout fxComponentLayout) {
        JACPToolBar toolBar = fxComponentLayout.getRegisteredToolBar(ToolbarPosition.NORTH);
        // Multilingual: Set locale to translate
        ComboBox<Locale> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(Locale.FRENCH, Locale.ENGLISH);
        comboBox.getSelectionModel().selectFirst();
        comboBox.setConverter(new StringConverter<Locale>() {
            @Override
            public String toString(Locale object) {
                if (object.equals(Locale.FRENCH)) return LocaleString.FRENCH;
                return LocaleString.ENGLISH;
            }

            @Override
            public Locale fromString(String string) {
                if (string.equalsIgnoreCase(LocaleString.FRENCH)) return Locale.FRENCH;
                return Locale.ENGLISH;
            }
        });
        toolBar.addAllOnEnd(comboBox);

        comboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        ObservableResourceFactory.setProperty(ResourceBundle.getBundle("bundles/languageBundle", newValue));
                        PensionTypeService.getInstance().updateCbPensionTypeSearch();
                        PensionTypeService.getInstance().updateCbPensionType();
                        // Reload employer table to re-convert pension type
                        context.send(PerspectiveId.HOME_PERSPECTIVE + "." + ComponentId.EMPLOYER_CALLBACK_CP,
                                EmployerSearchRequest.newBuilder()
                                        .setPagingRequest(PagingRequest.newBuilder()
                                                .setPageIndex(0)
                                                .build())
                                        .build());
                    }
                });
    }

    @Override
    public void handleInitialLayout(Message<Event, Object> message, WorkbenchLayout<Node> workbenchLayout, Stage stage) {
        workbenchLayout.setWorkbenchXYSize(1110, 800);
        workbenchLayout.registerToolBar(ToolbarPosition.NORTH);
        workbenchLayout.setStyle(StageStyle.DECORATED);
    }
}
