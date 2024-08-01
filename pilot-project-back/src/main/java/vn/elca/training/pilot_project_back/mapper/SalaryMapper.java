package vn.elca.training.pilot_project_back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import vn.elca.training.pilot_project_back.dto.SalaryCreateRequestDto;
import vn.elca.training.pilot_project_back.dto.SalaryListRequestDto;
import vn.elca.training.pilot_project_back.dto.SalaryResponseDto;
import vn.elca.training.pilot_project_back.entity.Salary;
import vn.elca.training.proto.salary.SalaryCreateRequest;
import vn.elca.training.proto.salary.SalaryListRequest;
import vn.elca.training.proto.salary.SalaryResponse;

import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {DateMapper.class, BigDecimalMapper.class, PageMapper.class})
@Component
public abstract class SalaryMapper {
    public abstract SalaryResponseDto mapEntityToResponseDto(Salary salary);

    @Mapping(target = "avsAmount", source = "avsAmount", qualifiedByName = "mapBigDecimalToString")
    @Mapping(target = "acAmount", source = "acAmount", qualifiedByName = "mapBigDecimalToString")
    @Mapping(target = "afAmount", source = "afAmount", qualifiedByName = "mapBigDecimalToString")
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "mapDateToString")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "mapDateToString")
    public abstract SalaryResponse mapResponseDtoToResponseProto(SalaryResponseDto salaryResponseDto);

    public List<SalaryResponse> mapResponseDtoListToResponseProtoList(List<SalaryResponseDto> salaryResponseDtos) {
        List<SalaryResponse> salaryResponses = new ArrayList<>();
        for (SalaryResponseDto salaryResponseDto : salaryResponseDtos) {
            salaryResponses.add(mapResponseDtoToResponseProto(salaryResponseDto));
        }
        return salaryResponses;
    }

    @Mapping(target = "avsAmount", source = "avsAmount", qualifiedByName = "mapStringToBigDecimal")
    @Mapping(target = "acAmount", source = "acAmount", qualifiedByName = "mapStringToBigDecimal")
    @Mapping(target = "afAmount", source = "afAmount", qualifiedByName = "mapStringToBigDecimal")
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "mapStringDateToDate")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "mapStringDateToDate")
    public abstract SalaryCreateRequestDto mapCreateRequestProtoToDto(SalaryCreateRequest salaryCreateRequest);

    public abstract Salary mapCreateRequestDtoToEntity(SalaryCreateRequestDto salaryCreateRequestDto);

    public List<SalaryCreateRequestDto> mapCreateListRequestProtoToDtoList(List<SalaryCreateRequest> salaryCreateRequests) {
        List<SalaryCreateRequestDto> salaries = new ArrayList<>();
        for (SalaryCreateRequest salaryCreateRequestDto : salaryCreateRequests) {
            salaries.add(mapCreateRequestProtoToDto(salaryCreateRequestDto));
        }
        return salaries;
    }

    public List<Salary> mapCreateListRequestDtoToEntityList(List<SalaryCreateRequestDto> salaryCreateRequestDtos) {
        List<Salary> salaries = new ArrayList<>();
        for (SalaryCreateRequestDto salaryCreateRequestDto : salaryCreateRequestDtos) {
            salaries.add(mapCreateRequestDtoToEntity(salaryCreateRequestDto));
        }
        return salaries;
    }

    @Mapping(target = "pagingRequest", source = "pagingRequest", qualifiedByName = "mapPagingRequestProtoToDto")
    public abstract SalaryListRequestDto mapListRequestProtoToDto(SalaryListRequest request);
}
