package vn.elca.training.pilot_project_back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import vn.elca.training.pilot_project_back.dto.PagingRequestDto;
import vn.elca.training.proto.common.PagingRequest;

@Mapper
public abstract class PageMapper {
    @Named("mapPagingRequestProtoToDto")
    public abstract PagingRequestDto mapRequestProtoToDto(PagingRequest pagingRequest);
}
