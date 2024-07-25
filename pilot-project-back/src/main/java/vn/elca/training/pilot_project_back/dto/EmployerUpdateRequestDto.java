package vn.elca.training.pilot_project_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import vn.elca.training.pilot_project_back.constant.PensionType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EmployerUpdateRequestDto {
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private PensionType pensionType;
    @NotNull
    @Length(max = 36)
    @Pattern(regexp = "^(CHE|ADM)-\\d{3}.\\d{3}.\\d{3}")
    private String ideNumber;
    @NotNull
    private Date dateCreation;
    private Date dateExpiration;
    private List<SalaryCreateRequestDto> salaries;
}
