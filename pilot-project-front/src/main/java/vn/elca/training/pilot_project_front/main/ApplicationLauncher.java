package vn.elca.training.pilot_project_front.main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.jacpfx.minimal.launcher.JacpFXApplicationLauncher;
import org.jacpfx.rcp.workbench.FXWorkbench;
import vn.elca.training.pilot_project_front.util.ObservableResourceFactory;
import vn.elca.training.pilot_project_front.workbench.JacpFXWorkbench;

public class ApplicationLauncher extends JacpFXApplicationLauncher {
    public static void main(String[] args) {
        Application.launch(ApplicationLauncher.class, args);
    }

    @Override
    protected Class<? extends FXWorkbench> getWorkbenchClass() {
        return JacpFXWorkbench.class;
    }

    @Override
    protected String[] getBasePackages() {
        return new String[]{"vn.elca.training.pilot_project_front"};
    }

    @Override
    protected void postInit(Stage stage) {
        stage.titleProperty().setValue(ObservableResourceFactory.getProperty().getString("title"));
    }
}
