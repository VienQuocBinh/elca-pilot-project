package vn.elca.training.pilot_project_back.dto;

import lombok.*;
import vn.elca.training.pilot_project_back.constant.PensionType;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployerResponseDto {
    private Long id;
    private PensionType pensionType;
    private String name;
    private String number;
    private String ideNumber;
    private Date createdDate;
    private Date expiredDate;
}
