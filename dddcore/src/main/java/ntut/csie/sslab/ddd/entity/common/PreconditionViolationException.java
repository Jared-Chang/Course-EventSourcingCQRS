package ntut.csie.sslab.ddd.entity.common;

public class PreconditionViolationException extends Error {

    public PreconditionViolationException(){
        super();
    }

    public PreconditionViolationException(String message){
        super(message);
    }

    public PreconditionViolationException(String message, Throwable cause) {
        super(message, cause);
    }

}
