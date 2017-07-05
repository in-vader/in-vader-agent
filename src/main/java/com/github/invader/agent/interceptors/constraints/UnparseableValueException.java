package com.github.invader.agent.interceptors.constraints;

public class UnparseableValueException extends RuntimeException {
    public UnparseableValueException(String fieldName, Exception e) {
        super("Field "+fieldName+" unparseable ("+e.getMessage()+")", e);
    }
}
