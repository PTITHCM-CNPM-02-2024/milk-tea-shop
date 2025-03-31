package com.mts.backend.domain.staff.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Value
@Builder
public class Position{
    String value;
    private final static short MAX_LENGTH = 50;
    private Position(String value) {
        Objects.requireNonNull(value, "Position is required");
        List<String> errors = new ArrayList<>();

        if(value.length() > 50){
            errors.add("Tên chức vụ không được vượt quá 50 ký tự");
        }

        if(value.isBlank()){
            errors.add("Tên chức vụ không được để trống");
        }
        
        if(!errors.isEmpty()){
            throw new DomainBusinessLogicException(errors);
        }
        this.value = normalize(value);
    }
    private static String normalize(String position) {
        return position.trim().toUpperCase();
    }
    public static final class PositionConverter implements AttributeConverter<Position, String> {
        @Override
        public String convertToDatabaseColumn(Position position) {
            return Objects.isNull(position) ? null : position.getValue();
        }
        
        @Override
        public Position convertToEntityAttribute(String value) {
            return Objects.isNull(value) ? null : new Position(value);
        }
    }
}
