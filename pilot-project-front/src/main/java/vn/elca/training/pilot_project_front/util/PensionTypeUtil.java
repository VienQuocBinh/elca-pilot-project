package vn.elca.training.pilot_project_front.util;

import vn.elca.training.proto.employer.PensionTypeProto;

public class PensionTypeUtil {
    private PensionTypeUtil() {
    }

    public static String getLocalizedPensionType(PensionTypeProto pensionType) {
        return ObservableResourceFactory.getProperty().getString("pensionType." + pensionType.name().toLowerCase());
    }

    public static PensionTypeProto getLocalizedPensionType(String pensionTypeString) {
        String regional = ObservableResourceFactory.getProperty().getString("pensionType.regional");
        String professional = ObservableResourceFactory.getProperty().getString("pensionType.professional");
        if (pensionTypeString.equals(regional)) return PensionTypeProto.REGIONAL;
        if (pensionTypeString.equals(professional)) return PensionTypeProto.PROFESSIONAL;
        return PensionTypeProto.NONE;
    }
}
