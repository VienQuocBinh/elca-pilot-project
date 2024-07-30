package vn.elca.training.pilot_project_front.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Salary {
    private Long id;
    private String avsNumber;
    private String lastName;
    private String firstName;
    private String startDate;
    private String endDate;
    private String avsAmount;
    private String acAmount;
    private String afAmount;
    @Builder.Default
    private boolean isImported = false; // true: newly; false: from db
}
