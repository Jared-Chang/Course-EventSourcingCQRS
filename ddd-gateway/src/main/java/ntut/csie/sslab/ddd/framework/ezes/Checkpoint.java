package ntut.csie.sslab.ddd.framework.ezes;

public record Checkpoint(Long position){

    public static Checkpoint valueOf(Long position){
        return new Checkpoint(position);
    }

    public static Checkpoint valueOf(int position){
        return new Checkpoint(Long.valueOf(position));
    }
}
