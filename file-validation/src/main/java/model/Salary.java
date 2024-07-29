package model;

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
    private String employerIdeNumber;

    // Method to convert Salary object to String array
    public String[] toStringArray() {
        return new String[]{
                id != null ? id.toString() : null,
                avsNumber,
                lastName,
                firstName,
                startDate,
                endDate,
                avsAmount,
                acAmount,
                afAmount,
                employerIdeNumber != null ? employerIdeNumber : ""
        };
    }
}
