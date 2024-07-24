package vn.elca.training.pilot_project_back.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import vn.elca.training.pilot_project_back.constant.PensionType;
import vn.elca.training.proto.employer.PensionTypeProto;

@Component
public class PensionTypeMapper {
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
