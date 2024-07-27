package vn.elca.training.pilot_project_front.service;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;
import vn.elca.training.pilot_project_front.util.PensionTypeUtil;
import vn.elca.training.proto.employer.PensionTypeProto;

@Setter
@Getter
public class PensionTypeService {
    private static PensionTypeService instance;
    private ComboBox<PensionTypeProto> cbPensionTypeSearch;
    private ComboBox<PensionTypeProto> cbPensionTypeMandatory;

    private PensionTypeService() {
    }

    public static PensionTypeService getInstance() {
        if (instance == null) {
            instance = new PensionTypeService();
        }
        return instance;
    }

    public void updateCbPensionTypeSearch() {
        cbPensionTypeSearch.getItems().clear();
        cbPensionTypeSearch.getItems().addAll(PensionTypeProto.NONE, PensionTypeProto.REGIONAL, PensionTypeProto.PROFESSIONAL);
        cbPensionTypeSearch.setConverter(stringConverter());
        cbPensionTypeSearch.setButtonCell(pensionTypeListCell());
        cbPensionTypeSearch.getSelectionModel().selectFirst();
    }

    public void updateCbPensionType() {
        cbPensionTypeMandatory.getItems().clear();
        cbPensionTypeMandatory.getItems().addAll(PensionTypeProto.REGIONAL, PensionTypeProto.PROFESSIONAL);
        cbPensionTypeMandatory.setConverter(stringConverter());
        cbPensionTypeMandatory.setButtonCell(pensionTypeListCell());
        cbPensionTypeMandatory.getSelectionModel().selectFirst();
    }

    private StringConverter<PensionTypeProto> stringConverter() {
        return new StringConverter<PensionTypeProto>() {
            @Override
            public String toString(PensionTypeProto pensionType) {
                return PensionTypeUtil.getLocalizedPensionType(pensionType);
            }

            @Override
            public PensionTypeProto fromString(String string) {
                for (PensionTypeProto type : PensionTypeProto.values()) {
                    if (PensionTypeUtil.getLocalizedPensionType(type).equals(string)) {
                        return type;
                    }
                }
                return PensionTypeProto.NONE;
            }
        };
    }

    private ListCell<PensionTypeProto> pensionTypeListCell() {
        return new ListCell<PensionTypeProto>() {
            @Override
            protected void updateItem(PensionTypeProto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(PensionTypeUtil.getLocalizedPensionType(item));
                }
            }
        };
    }
}
