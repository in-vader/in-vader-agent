package com.github.invader.agent.interceptors.constraints;

/**
 * Created by Jacek on 2017-07-02.
 */
public class UnparseableValueException extends RuntimeException {
    public UnparseableValueException(String fieldName, Exception e) {
        super("Field "+fieldName+" unparseable ("+e.getMessage()+")", e);
    }
}
