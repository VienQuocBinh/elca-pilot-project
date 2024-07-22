package vn.elca.training.pilot_project_front.callback;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.event.Event;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.Component;
import org.jacpfx.api.annotations.component.Stateless;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.CallbackComponent;
import org.jacpfx.rcp.context.Context;
import vn.elca.training.pilot_project_front.constant.ComponentId;
import vn.elca.training.proto.employer.EmployerListResponse;
import vn.elca.training.proto.employer.EmployerSearchRequest;
import vn.elca.training.proto.employer.EmployerServiceGrpc;

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
        if (message.getMessageBody() instanceof EmployerSearchRequest) {
            EmployerListResponse employers = stub.getEmployers(message.getTypedMessageBody(EmployerSearchRequest.class));
            context.setReturnTarget(ComponentId.HOME_EMPLOYER_TABLE_CP);
            return employers;
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
        this.log.info("Run on tear down of EmployerCallbackCp");
        channel.shutdown();
    }
}
