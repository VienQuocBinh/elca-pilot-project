package vn.elca.training.pilot_project_front.util;

import vn.elca.training.proto.employer.PensionTypeProto;

public class PensionTypeUtil {
    private PensionTypeUtil() {
    }

    public static String getLocalizedPensionType(PensionTypeProto pensionType) {
        return ObservableResourceFactory.getProperty().getString("pensionType." + pensionType.name().toLowerCase());
    }
}
