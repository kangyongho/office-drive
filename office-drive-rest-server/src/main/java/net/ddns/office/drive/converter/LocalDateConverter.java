package net.ddns.office.drive.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by NPOST on 2017-06-12.
 */
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {
    @Override
    public Date convertToDatabaseColumn(LocalDate attribute) {
        return (attribute == null ? null : Date.valueOf(attribute));
    }

    @Override
    public LocalDate convertToEntityAttribute(Date dbData) {
        return (dbData == null ? null : dbData.toLocalDate());
    }
}
