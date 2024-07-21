package vn.elca.training.pilot_project_front.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employer {
    private Long id;
    private String pensionType;
    private String name;
    private String number;
    private String ideNumber;
    private LocalDate createdDate;
    private LocalDate expiredDate;
}
