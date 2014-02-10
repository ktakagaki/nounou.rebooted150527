package nounou;

//import nounou.package$.MODULE$;

/**
 * @author ktakagaki
 * @date 1/30/14.
 */
class NN {

    //package$ instance = package$.MODULE$;

    public static final int variable = 5;

    public static final int get(int n)  { return n * 2; }

    public static final FrameRange framesAll = FrameRange$.MODULE$.All();

    public static final FrameRange frames(int start, int endMarker, int step) {
        return new FrameRange(start, endMarker, step, false);
    }
    public static final FrameRange frames(int start, int endMarker) {
        return new FrameRange(start, endMarker, 1, false);
    }


}
