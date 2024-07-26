package vn.elca.training.pilot_project_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.elca.training.pilot_project_back.constant.PensionType;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EmployerResponseDto {
    private Long id;
    private PensionType pensionType;
    private String name;
    private String number;
    private String ideNumber;
    private Date dateCreation;
    private Date dateExpiration;
    private List<SalaryResponseDto> salaries;
}
