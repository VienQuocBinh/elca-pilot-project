package vn.elca.training.pilot_project_front.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.elca.training.pilot_project_front.constant.ActionType;
import vn.elca.training.proto.employer.EmployerResponse;

@Getter
@Setter
@AllArgsConstructor
public class EmployerResponseWrapper {
    private EmployerResponse employerResponse;
    private ActionType actionType;
}
