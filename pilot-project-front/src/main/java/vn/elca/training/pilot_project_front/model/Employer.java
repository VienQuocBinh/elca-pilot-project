package vn.elca.training.pilot_project_front.model;

import lombok.*;
import vn.elca.training.proto.employer.PensionTypeProto;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employer {
    private Long id;
    private PensionTypeProto pensionType;
    private String name;
    private String number;
    private String ideNumber;
    private String createdDate;
    private String expiredDate;
}
