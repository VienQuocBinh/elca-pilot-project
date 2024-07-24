package vn.elca.training.pilot_project_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import vn.elca.training.pilot_project_back.constant.PensionType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class EmployerCreateRequestDto {
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
    @NotNull
    private Date dateExpiration;
}
