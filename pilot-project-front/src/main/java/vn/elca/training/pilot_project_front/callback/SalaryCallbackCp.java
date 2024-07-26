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
import vn.elca.training.pilot_project_front.config.GrpcConfig;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.proto.common.EmployerId;
import vn.elca.training.proto.salary.SalaryListResponse;
import vn.elca.training.proto.salary.SalaryServiceGrpc;

import java.util.logging.Logger;

@Component(id = ComponentId.SALARY_CALLBACK_CP,
        name = "salaryCallbackCp",
        resourceBundleLocation = "bundles.languageBundle")
@Stateless
public class SalaryCallbackCp implements CallbackComponent {
    private final Logger log = Logger.getLogger(SalaryCallbackCp.class.getName());
    private ManagedChannel channel;
    private SalaryServiceGrpc.SalaryServiceBlockingStub stub;
    @Resource
    private Context context;

    @Override
    public Object handle(Message<Event, Object> message) throws Exception {
        try {
            if (message.getMessageBody() instanceof EmployerId) {
                // Get salaries of employer
                SalaryListResponse salaries = stub.getSalariesByEmployerId(message.getTypedMessageBody(EmployerId.class));
                context.setReturnTarget(ComponentId.EMPLOYER_DETAIL_CP);
                return salaries;
            }
        } catch (StatusRuntimeException e) {
            log.warning(e.getMessage());
            Platform.runLater(() -> showAlert(e)); // To not crash with current thread
        }
        return null;
    }

    @PostConstruct
    public void onPostConstructComponent() {
        this.channel = ManagedChannelBuilder
                .forAddress(GrpcConfig.ADDRESS, GrpcConfig.PORT)
                .usePlaintext()
                .build();
        this.stub = SalaryServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void onPreDestroyComponent() {
        log.info("Run on tear down of SalaryCallbackCp");
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
