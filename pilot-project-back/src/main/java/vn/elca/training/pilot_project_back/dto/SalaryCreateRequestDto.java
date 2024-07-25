package vn.elca.training.pilot_project_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class SalaryCreateRequestDto {
    @NotNull
    private String avsNumber;
    @NotNull
    private String lastName;
    @NotNull
    private String firstName;
    @NotNull
    private Date startDate;
    private Date endDate;
    private BigDecimal avsAmount;
    private BigDecimal acAmount;
    private BigDecimal afAmount;
}
