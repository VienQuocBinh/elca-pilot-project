package vn.elca.training.pilot_project_front.callback;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.Alert;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.Component;
import org.jacpfx.api.annotations.component.Stateless;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.CallbackComponent;
import org.jacpfx.rcp.context.Context;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.proto.employer.*;

import java.util.ResourceBundle;
import java.util.logging.Logger;

@Component(id = ComponentId.EMPLOYER_CALLBACK_CP,
        name = "employerCallbackCp",
        resourceBundleLocation = "bundles.languageBundle")
@Stateless
public class EmployerCallbackCp implements CallbackComponent {
    private final Logger log = Logger.getLogger(EmployerCallbackCp.class.getName());
    private ManagedChannel channel;
    private EmployerServiceGrpc.EmployerServiceBlockingStub stub;
    @Resource
    private Context context;

    @Override
    public Object handle(Message<Event, Object> message) throws Exception {
        try {
            if (message.getMessageBody() instanceof EmployerSearchRequest) {
                // Get list (search)
                EmployerListResponse employers = stub.getEmployers(message.getTypedMessageBody(EmployerSearchRequest.class));
                context.setReturnTarget(ComponentId.HOME_EMPLOYER_TABLE_CP);
                return employers;
            } else if (message.getMessageBody() instanceof Long) {
                // Delete
                Empty empty = stub.deleteEmployer(EmployerId.newBuilder().setId(message.getTypedMessageBody(Long.class)).build());
                context.setReturnTarget(ComponentId.HOME_EMPLOYER_TABLE_CP);
                return empty;
            }
        } catch (StatusRuntimeException e) {
            log.warning(e.getMessage());
            Platform.runLater(() -> showAlert(e)); // To not crash with current thread
        }
        return null;
    }

    @PostConstruct
    public void onPostConstructComponent(final ResourceBundle resourceBundle) {
        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        this.stub = EmployerServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void onPreDestroyComponent() {
        log.info("Run on tear down of EmployerCallbackCp");
        if (channel != null) channel.shutdown();
    }

    private void showAlert(StatusRuntimeException e) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("gRPC Error");
        alert.setContentText("An error occurred: " + e.getStatus().getDescription());
        alert.showAndWait();
    }
}
