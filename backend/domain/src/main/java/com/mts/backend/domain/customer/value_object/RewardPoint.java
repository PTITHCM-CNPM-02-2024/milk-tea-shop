package com.mts.backend.domain.customer.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;

public class RewardPoint extends AbstractValueObject {
    
    private final int point;
    
    private RewardPoint(int point){
        this.point = point;
    }
    
    public static ValueObjectValidationResult create(int point){
        List<String> errors = new ArrayList<>();
        
        if(point < 0){
            errors.add("Điểm thưởng không được nhỏ hơn 0");
        }
        
        if(errors.isEmpty()){
            return new ValueObjectValidationResult(new RewardPoint(point), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
    }
    
    public static RewardPoint of(int point){
        ValueObjectValidationResult result = create(point);
        
        if(result.getBusinessErrors().isEmpty()){
            return new RewardPoint(point);
        }
        
        throw new IllegalArgumentException("Điểm thưởng không hợp lệ: " + result.getBusinessErrors());
    }
    
    public int getValue(){
        return point;
    }
    
    @Override
    public String toString() {
        return String.valueOf(point);
    }
    
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(point);
    }
    
    public RewardPoint add(RewardPoint rewardPoint){
        return RewardPoint.of(this.point + rewardPoint.point);
    }
    
    public RewardPoint subtract(RewardPoint rewardPoint){
        var result = this.point - rewardPoint.point;
        return result < 0 ? RewardPoint.of(0) : RewardPoint.of(result);
    }
    
    public boolean isGreaterThan(RewardPoint rewardPoint){
        return this.point > rewardPoint.point;
    }
    
    public boolean isLessThan(RewardPoint rewardPoint){
        return this.point < rewardPoint.point;
    }
    
    
}
