package ntut.csie.sslab.ddd.usecase;

public class UseCaseFailureException extends RuntimeException{

    public UseCaseFailureException(){
        super();
    }

    public UseCaseFailureException(Exception e){
        super(e);
    }

    public UseCaseFailureException(String message){
        super(message);
    }

    public UseCaseFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
