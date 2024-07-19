package vn.elca.training.pilot_project_front.util;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

public class ObservableResourceFactory {
    private static final ObjectProperty<ResourceBundle> property = new SimpleObjectProperty<>();

    static {
        property.set(ResourceBundle.getBundle("bundles/languageBundle", Locale.ENGLISH));
    }

    public static ObjectProperty<ResourceBundle> resourceProperty() {
        return property;
    }

    public static ResourceBundle getProperty() {
        return property.get();
    }

    public static void setProperty(ResourceBundle property) {
        resourceProperty().set(property);
    }

    public static StringBinding getStringBinding(String key) {
        return new StringBinding() {
            {
                bind(resourceProperty());
            }

            @Override
            protected String computeValue() {
                return getProperty().getString(key);
            }
        };
    }
}
