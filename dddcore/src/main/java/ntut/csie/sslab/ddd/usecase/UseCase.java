package ntut.csie.sslab.ddd.usecase;


public interface UseCase<I extends Input, O extends Output> {
	O execute(I input);
}