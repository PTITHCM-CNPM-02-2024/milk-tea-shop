package com.mts.backend.domain.customer.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DomainException;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value
@Builder
public class RewardPoint implements Comparable<RewardPoint>  {
    
   Integer value;
    
    private RewardPoint(Integer value){
        List<String> errors = new ArrayList<>();

        if(value < 0){
            errors.add("Điểm thưởng không được nhỏ hơn 0");
        }

        if(!errors.isEmpty()){
            throw new DomainBusinessLogicException(errors);
        }

        this.value = value;
    }
    
    
    public RewardPoint subtract(RewardPoint rewardPoint){
        var result = this.value - rewardPoint.value;
        return result < 0 ? new RewardPoint(0) : new RewardPoint(result);
    }
    
    public RewardPoint add(RewardPoint rewardPoint){
        return new RewardPoint(this.value + rewardPoint.value);
    }
    
    public RewardPoint multiply(int multiplier){
        return new RewardPoint(this.value * multiplier);
    }
    
    public RewardPoint divide(int divisor){
        if(divisor == 0){
            throw new DomainException("Không thể chia cho 0");
        }
        return new RewardPoint(this.value / divisor);
    }


    /**
     * @param o the object to be compared. 
     * @return
     */
    @Override
    public int compareTo(RewardPoint o) {
        return Integer.compare(this.value, o.value);
    }
    
    public static final class RewardPointConverter implements jakarta.persistence.AttributeConverter<RewardPoint, Integer> {
        @Override
        public Integer convertToDatabaseColumn(RewardPoint attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
        
        @Override
        public RewardPoint convertToEntityAttribute(Integer dbData) {
            return Objects.isNull(dbData) ? null : new RewardPoint(dbData);
        }
    }
}
