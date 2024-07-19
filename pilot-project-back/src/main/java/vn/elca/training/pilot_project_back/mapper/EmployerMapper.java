package vn.elca.training.pilot_project_back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.elca.training.pilot_project_back.constant.PensionType;
import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.proto.employer.EmployerResponse;
import vn.elca.training.proto.employer.PensionTypeProto;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper
@Component
public abstract class EmployerMapper {
    @Autowired
    private SimpleDateFormat simpleDateFormat;

    public abstract EmployerResponseDto entityToResponseDto(Employer employer);

    @Mapping(target = "pensionType", source = "pensionType", qualifiedByName = "pensionDtoToProto")
    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "dateToString")
    @Mapping(target = "expiredDate", source = "expiredDate", qualifiedByName = "dateToString")
    public abstract EmployerResponse dtoToProtoResponse(EmployerResponseDto employerResponseDto);

    @Named("pensionProtoToDto")
    public PensionType mapProtoToDto(PensionTypeProto pensionTypeProto) {
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
    public PensionTypeProto mapDtoToProto(PensionType pensionType) {
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

    @Named("dateToString")
    public String dateToString(Date date) {
        return date != null ? simpleDateFormat.format(date) : "";
    }
}
