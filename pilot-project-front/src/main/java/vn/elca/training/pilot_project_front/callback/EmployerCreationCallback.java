package vn.elca.training.pilot_project_front.callback;

import vn.elca.training.proto.employer.EmployerResponse;

public interface EmployerCreationCallback {
    void onEmployerCreated(EmployerResponse employer);
}
