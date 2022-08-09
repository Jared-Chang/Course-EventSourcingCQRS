package ntut.csie.sslab.ddd.usecase;

public class RepositorySaveException extends RuntimeException{
    public static final String OPTIMISTIC_LOCKING_FAILURE = "Optimistic locking failure";

    public RepositorySaveException(){
        super();
    }

    public RepositorySaveException(Exception e){
        super(e);
    }

    public RepositorySaveException(String message){
        super(message);
    }

    public RepositorySaveException(String message, Throwable cause) {
        super(message, cause);
    }

}
