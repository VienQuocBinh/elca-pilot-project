package vn.elca.training.pilot_project_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SalaryListRequestDto {
    private Long employerId;
    private PagingRequestDto pagingRequest;
}
