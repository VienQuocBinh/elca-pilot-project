package vn.elca.training.pilot_project_back.mapper;

import vn.elca.training.pilot_project_back.constant.PensionType;
import vn.elca.training.proto.employer.PensionTypeProto;

public class PensionTypeMapper {
    public static PensionType mapProtoToEntity(PensionTypeProto fundTypeProto) {
        switch (fundTypeProto) {
            case REGIONAL:
                return PensionType.REGIONAL;
            case PROFESSIONAL:
                return PensionType.PROFESSIONAL;
            case NONE:
            default:
                return PensionType.NONE;
        }
    }

    public static PensionTypeProto mapEntityToProto(PensionType pensionType) {
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
