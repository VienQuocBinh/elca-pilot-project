package vn.elca.training.pilot_project_back.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.elca.training.pilot_project_back.dto.EmployerCreateRequestDto;
import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;
import vn.elca.training.pilot_project_back.dto.EmployerSearchRequestDto;
import vn.elca.training.pilot_project_back.dto.EmployerUpdateRequestDto;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.pilot_project_back.exception.ValidationException;
import vn.elca.training.pilot_project_back.service.ValidationService;
import vn.elca.training.proto.employer.EmployerCreateRequest;
import vn.elca.training.proto.employer.EmployerResponse;
import vn.elca.training.proto.employer.EmployerSearchRequest;
import vn.elca.training.proto.employer.EmployerUpdateRequest;

@Mapper(uses = {DateMapper.class, PensionTypeMapper.class})
@Component
public abstract class EmployerMapper {
    @Autowired
    private ValidationService validationService;

    public abstract EmployerResponseDto mapEntityToResponseDto(Employer employer);

    public abstract Employer mapCreateDtoToEntity(EmployerCreateRequestDto employerCreateRequestDto);

    @Mapping(target = "pensionType", source = "pensionType", qualifiedByName = "pensionProtoToDto")
    @Mapping(target = "dateExpiration", source = "dateExpiration", qualifiedByName = "mapStringDateToDate")
    @Mapping(target = "dateCreation", source = "dateCreation", qualifiedByName = "mapStringDateToDate")
    public abstract EmployerSearchRequestDto mapSearchRequestProtoToDto(EmployerSearchRequest searchRequest);

    @Mapping(target = "pensionType", source = "pensionType", qualifiedByName = "pensionProtoToDto")
    @Mapping(target = "dateCreation", source = "dateCreation", qualifiedByName = "mapStringDateToDate")
    @Mapping(target = "dateExpiration", source = "dateExpiration", qualifiedByName = "mapStringDateToDate")
    public abstract EmployerCreateRequestDto mapCreateRequestProtoToCreateRequestDto(EmployerCreateRequest employerCreateRequest) throws ValidationException;

    @Mapping(target = "pensionType", source = "pensionType", qualifiedByName = "pensionDtoToProto")
    @Mapping(target = "dateCreation", source = "dateCreation", qualifiedByName = "mapDateToString")
    @Mapping(target = "dateExpiration", source = "dateExpiration", qualifiedByName = "mapDateToString")
    @Mapping(target = "salariesList", ignore = true)
    public abstract EmployerResponse mapResponseDtoToResponseProto(EmployerResponseDto employerResponseDto);

    @Mapping(target = "pensionType", source = "pensionType", qualifiedByName = "pensionProtoToDto")
    @Mapping(target = "dateCreation", source = "dateCreation", qualifiedByName = "mapStringDateToDate")
    @Mapping(target = "dateExpiration", source = "dateExpiration", qualifiedByName = "mapStringDateToDate")
    @Mapping(target = "salaries", ignore = true)
    public abstract EmployerUpdateRequestDto mapUpdateRequestProtoToCreateRequestDto(EmployerUpdateRequest employerUpdateRequest) throws ValidationException;

    @BeforeMapping
    protected void validateCreateRequest(EmployerCreateRequest employerCreateRequest) throws ValidationException {
        validationService.validateEmployerCreateRequestProto(employerCreateRequest);
    }

    @BeforeMapping
    protected void validateUpdateRequest(EmployerUpdateRequest employerUpdateRequest) throws ValidationException {
        validationService.validateEmployerUpdateRequestProto(employerUpdateRequest);
    }
}
