package vn.elca.training.pilot_project_back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vn.elca.training.pilot_project_back.constant.PensionType;
import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.proto.employer.EmployerResponse;
import vn.elca.training.proto.employer.PensionTypeProto;

@Mapper
public interface EmployerMapper {
    EmployerResponseDto toResponseDto(Employer employer);

    @Mapping(target = "pensionType", source = "pensionType", qualifiedByName = "pensionDtoToProto")
    EmployerResponse mapDtoToProtoResponse(EmployerResponseDto employerResponseDto);

    @Named("pensionProtoToDto")
    default PensionType mapProtoToDto(PensionTypeProto pensionTypeProto) {
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
    default PensionTypeProto mapDtoToProto(PensionType pensionType) {
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
