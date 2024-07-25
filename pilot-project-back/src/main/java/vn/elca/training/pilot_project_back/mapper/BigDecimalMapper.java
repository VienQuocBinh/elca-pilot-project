package vn.elca.training.pilot_project_back.mapper;

import com.google.protobuf.ByteString;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import vn.elca.training.proto.salary.DecimalValue;

import java.math.BigDecimal;

@Component
public class BigDecimalMapper {
    @Named("mapBigDecimalToDecimalValue")
    public DecimalValue mapBigDecimalToDecimalValue(BigDecimal bigDecimal) {
        return DecimalValue.newBuilder()
                .setScale(bigDecimal.scale())
                .setPrecision(bigDecimal.precision())
                .setValue(ByteString.copyFrom(bigDecimal.unscaledValue().toByteArray()))
                .build();
    }

    @Named("mapBigDecimalToString")
    public String mapBigDecimalToString(BigDecimal bigDecimal) {
        return bigDecimal.toString();
    }

    @Named("mapStringToBigDecimal")
    public BigDecimal mapStringToBigDecimal(String bigDecString) {
        return new BigDecimal(bigDecString);
    }
}
