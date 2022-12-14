package ntut.csie.sslab.ddd.usecase.cqrs;

import ntut.csie.sslab.ddd.usecase.Output;

public class CqrsOutput<T extends CqrsOutput<T>> implements Output {
	private String id;
	private String message;
	private ExitCode exitCode;

	public static CqrsOutput<?> create(){
		return new CqrsOutput<>();
	}

	@Override
	public String getId(){
		return id;
	}

	@Override
	public T setId(String id){
		this.id = id;
		return  self();
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public T setMessage(String message) {
		this.message =  message;
		return  self();
	}

	@Override
	public ExitCode getExitCode() {
		return exitCode;
	}

	@Override
	public T setExitCode(ExitCode exitCode) {
		this.exitCode = exitCode;
		return  self();
	}

	@SuppressWarnings("unchecked")
	final T self() {
		return (T) this;
	}
}