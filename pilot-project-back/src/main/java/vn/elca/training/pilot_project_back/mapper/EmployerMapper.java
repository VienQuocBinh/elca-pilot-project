package vn.elca.training.pilot_project_back.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.elca.training.pilot_project_back.constant.PensionType;
import vn.elca.training.pilot_project_back.dto.EmployerCreateRequestDto;
import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;
import vn.elca.training.pilot_project_back.dto.EmployerSearchRequestDto;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.pilot_project_back.exception.ValidationException;
import vn.elca.training.pilot_project_back.service.ValidationService;
import vn.elca.training.proto.employer.EmployerCreateRequest;
import vn.elca.training.proto.employer.EmployerResponse;
import vn.elca.training.proto.employer.EmployerSearchRequest;
import vn.elca.training.proto.employer.PensionTypeProto;

import java.text.SimpleDateFormat;

@Mapper(uses = {DateMapper.class})
@Component
public abstract class EmployerMapper {
    @Autowired
    private SimpleDateFormat simpleDateFormat;
    @Autowired
    private ValidationService validationService;

    public abstract EmployerResponseDto mapEntityToResponseDto(Employer employer);

    public abstract Employer mapCreateDtoToEntity(EmployerCreateRequestDto employerCreateRequestDto);

    @Mapping(target = "pensionType", source = "pensionType", qualifiedByName = "pensionProtoToDto")
    @Mapping(target = "dateExpiration", source = "dateExpiration", qualifiedByName = "mapStringDateToDate")
    @Mapping(target = "dateCreation", source = "dateCreation", qualifiedByName = "mapStringDateToDate")
    public abstract EmployerSearchRequestDto mapSearchRequestProtoToDto(EmployerSearchRequest searchRequest) throws ValidationException;

    @Mapping(target = "pensionType", source = "pensionType", qualifiedByName = "pensionProtoToDto")
    @Mapping(target = "dateCreation", source = "dateCreation", qualifiedByName = "mapStringDateToDate")
    @Mapping(target = "dateExpiration", source = "dateExpiration", qualifiedByName = "mapStringDateToDate")
    public abstract EmployerCreateRequestDto mapCreateRequestProtoToCreateRequestDto(EmployerCreateRequest employerCreateRequest);

    @Mapping(target = "pensionType", source = "pensionType", qualifiedByName = "pensionDtoToProto")
    @Mapping(target = "dateCreation", source = "dateCreation", qualifiedByName = "mapDateToString")
    @Mapping(target = "dateExpiration", source = "dateExpiration", qualifiedByName = "mapDateToString")
    @Mapping(target = "salariesList", ignore = true)
    public abstract EmployerResponse mapResponseDtoToResponseProto(EmployerResponseDto employerResponseDto);

    @BeforeMapping
    protected void validate(EmployerSearchRequest employerSearchRequest) throws ValidationException {
        validationService.validateEmployerSearchRequestDto(employerSearchRequest);
    }

    @Named("pensionProtoToDto")
    public PensionType mapPensionTypeProtoToDto(PensionTypeProto pensionTypeProto) {
        switch (pensionTypeProto) {
            case REGIONAL:
                return PensionType.REGIONAL;
            case PROFESSIONAL:
                return PensionType.PROFESSIONAL;
            case NONE:
            default:
                return PensionType.NONE;
        }
    }

    @Named("pensionDtoToProto")
    public PensionTypeProto mapPensionTypeDtoToProto(PensionType pensionType) {
        switch (pensionType) {
            case REGIONAL:
                return PensionTypeProto.REGIONAL;
            case PROFESSIONAL:
                return PensionTypeProto.PROFESSIONAL;
            case NONE:
            default:
                return PensionTypeProto.NONE;
        }
    }

}
