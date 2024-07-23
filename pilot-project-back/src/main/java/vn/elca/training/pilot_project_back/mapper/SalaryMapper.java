package vn.elca.training.pilot_project_back.mapper;

import com.google.protobuf.ByteString;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.elca.training.pilot_project_back.dto.SalaryResponseDto;
import vn.elca.training.pilot_project_back.entity.Salary;
import vn.elca.training.proto.salary.DecimalValue;
import vn.elca.training.proto.salary.SalaryResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {DateMapper.class})
@Component
public abstract class SalaryMapper {
    @Autowired
    private DateMapper dateMapper;

    public abstract SalaryResponseDto mapEntityToResponseDto(Salary salary);

    @Mapping(target = "avsAmount", source = "avsAmount", qualifiedByName = "mapBigDecimalToString")
    @Mapping(target = "acAmount", source = "acAmount", qualifiedByName = "mapBigDecimalToString")
    @Mapping(target = "afAmount", source = "afAmount", qualifiedByName = "mapBigDecimalToString")
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "mapDateToString")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "mapDateToString")
    public abstract SalaryResponse mapResponseDtoToResponseProto(SalaryResponseDto salaryResponseDto);

    public List<SalaryResponse> mapResponseDtoListToResponseProtoList(List<SalaryResponseDto> salaryResponseDtos) {
        List<SalaryResponse> salaryResponses = new ArrayList<>();
        for (SalaryResponseDto salaryResponseDto : salaryResponseDtos) {
            salaryResponses.add(mapResponseDtoToResponseProto(salaryResponseDto));
        }
        return salaryResponses;
    }

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
}
