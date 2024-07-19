package vn.elca.training.pilot_project_front.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employer {
    private Long id;
    private String fundType;
    private String name;
    private String number;
    private String ideNumber;
    private LocalDate createdDate;
    private LocalDate expiredDate;
}
