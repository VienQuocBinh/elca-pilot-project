package vn.elca.training.pilot_project_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class SalaryResponseDto {
    private Long id;
    private String avsNumber;
    private String lastName;
    private String firstName;
    private Date startDate;
    private Date endDate;
    private BigDecimal avsAmount;
    private BigDecimal acAmount;
    private BigDecimal afAmount;

    public String[] toStringArray() {
        return new String[]{id.toString(),
                avsNumber,
                lastName,
                firstName,
                String.valueOf(startDate),
                String.valueOf(endDate),
                String.valueOf(avsAmount),
                String.valueOf(acAmount),
                String.valueOf(afAmount)};
    }
}
