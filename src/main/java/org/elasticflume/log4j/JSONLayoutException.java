package org.elasticflume.log4j;

public class JSONLayoutException extends RuntimeException{

    public JSONLayoutException(String message, Throwable t){
        super(message,t);
    }

    public JSONLayoutException(Throwable t){
        super(t);
    }
}
