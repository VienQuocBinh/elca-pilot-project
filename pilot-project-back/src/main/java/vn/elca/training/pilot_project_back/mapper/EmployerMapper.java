package vn.elca.training.pilot_project_back.mapper;

import org.apache.commons.lang3.StringUtils;
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
import vn.elca.training.pilot_project_back.util.ValidationUtil;
import vn.elca.training.proto.employer.EmployerCreateRequest;
import vn.elca.training.proto.employer.EmployerResponse;
import vn.elca.training.proto.employer.EmployerSearchRequest;
import vn.elca.training.proto.employer.PensionTypeProto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper
@Component
public abstract class EmployerMapper {
    @Autowired
    private SimpleDateFormat simpleDateFormat;
    @Autowired
    private ValidationUtil validationUtil;

    public abstract EmployerResponseDto mapEntityToResponseDto(Employer employer);

    public abstract Employer mapCreateDtoToEntity(EmployerCreateRequestDto employerCreateRequestDto);

    @Mapping(target = "pensionType", source = "pensionType", qualifiedByName = "pensionProtoToDto")
    @Mapping(target = "expiredDate", source = "expiredDate", qualifiedByName = "mapStringDateToDate")
    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "mapStringDateToDate")
    public abstract EmployerSearchRequestDto mapSearchRequestProtoToDto(EmployerSearchRequest searchRequest) throws ValidationException;

    @Mapping(target = "pensionType", source = "pensionType", qualifiedByName = "pensionProtoToDto")
    @Mapping(target = "expiredDate", source = "expiredDate", qualifiedByName = "mapStringDateToDate")
    public abstract EmployerCreateRequestDto mapCreateRequestProtoToCreateRequestDto(EmployerCreateRequest employerCreateRequest);

    @Mapping(target = "pensionType", source = "pensionType", qualifiedByName = "pensionDtoToProto")
    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "mapDateToString")
    @Mapping(target = "expiredDate", source = "expiredDate", qualifiedByName = "mapDateToString")
    public abstract EmployerResponse mapResponseDtoToResponseProto(EmployerResponseDto employerResponseDto);

    @BeforeMapping
    protected void validate(EmployerSearchRequest employerSearchRequest) throws ValidationException {
        validationUtil.validateEmployerSearchRequestDto(employerSearchRequest);
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

    @Named("mapDateToString")
    public String mapDateToString(Date date) {
        return date != null ? simpleDateFormat.format(date) : "";
    }

    @Named("mapStringDateToDate")
    public Date mapStringDateToDate(String dateString) throws ParseException {
        if (dateString == null || StringUtils.isBlank(dateString)) return null;
        return simpleDateFormat.parse(dateString);
    }
}
