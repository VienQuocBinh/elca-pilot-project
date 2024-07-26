package vn.elca.training.pilot_project_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import vn.elca.training.pilot_project_back.constant.PensionType;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class EmployerSearchRequestDto {
    private PensionType pensionType;
    private String name;
    private String number;
    private String ideNumber;
    private Date dateCreation;
    private Date dateExpiration;
    private PagingRequestDto pagingRequest;
}
