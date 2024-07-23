package vn.elca.training.pilot_project_back.mapper;


import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateMapper {
    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Named("mapDateToString")
    public String mapDateToString(Date date) {
        return date != null ? simpleDateFormat.format(date) : "";
    }

    @Named("mapStringDateToDate")
    public Date mapStringDateToDate(String dateString) throws ParseException {
        if (dateString == null || StringUtils.isBlank(dateString)) return null;
        return simpleDateFormat.parse(dateString);
    }
}
