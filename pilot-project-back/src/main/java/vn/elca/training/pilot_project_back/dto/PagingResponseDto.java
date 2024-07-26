package vn.elca.training.pilot_project_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PagingResponseDto {
    private int pageIndex;
    private int pageSize;
    private int totalPages;
    private int totalElements;
}
