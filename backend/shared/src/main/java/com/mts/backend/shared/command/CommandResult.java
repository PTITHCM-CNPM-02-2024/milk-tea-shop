package com.mts.backend.shared.command;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CommandResult {
    
    private Object successObject;
    
    private List<String> failureReasons = new ArrayList<>();
    
    private FAILURE_TYPE failureType;
    
    private CommandResult (){}
    
    private CommandResult(Object successObject){
        this.successObject = successObject;
    }
    
    private CommandResult (FAILURE_TYPE failureType, List<String> failureReasons){
        this.failureType = failureType;
        this.failureReasons = new ArrayList<>();
        
        if(failureReasons != null){
            this.failureReasons.addAll(failureReasons);
        }
    }
    
    private CommandResult (FAILURE_TYPE failureType, @NonNull String failureReason){
        this.failureType = failureType;
        this.failureReasons = new ArrayList<>();

        this.failureReasons.add(failureReason);
    }

    public static CommandResult systemFail(String message) {
        return new CommandResult(FAILURE_TYPE.NONE, message);
    }

    public boolean isSuccess(){
        return failureReasons.isEmpty();
    }
    
    public Object getSuccessObject(){
        return successObject;
    }
    
    public List<String> getFailureReasons(){
        return failureReasons;
    }
    
    public FAILURE_TYPE getFailureType(){
        return failureType;
    }
    
    public static CommandResult success(){
        return new CommandResult();
    }
    
    public static CommandResult success(Object successObject){
        return new CommandResult(successObject);
    }
    
    public static CommandResult businessFail(@NonNull String failureReason){
        return new CommandResult(FAILURE_TYPE.BUSINESS_RULE, failureReason);
    }
    
    public static CommandResult businessFail(List<String> failureReasons){
        return new CommandResult(FAILURE_TYPE.BUSINESS_RULE, failureReasons);
    }
    
    public static CommandResult notFoundFail(String failureReason){
        return new CommandResult(FAILURE_TYPE.NOT_FOUND, failureReason);
    }

    public enum FAILURE_TYPE {
        NONE,
        NOT_FOUND,
        BUSINESS_RULE,
        DUPLICATE,
    }
}
