//package nounou;
//
////import nounou.package$.MODULE$;
//
//
//import scala.Int;
//import scala.collection.immutable.Range;
//import scala.collection.immutable.Vector;
//import scala.reflect.ClassTag$;
//
///**
// * @author ktakagaki
// * @date 1/30/14.
// */
//class NN {
//
//    //package$ instance = package$.MODULE$;
//
//    public static final int get(int n)  { return n * 2; }
//
//    public static final FrameRange frAll = FrameRange$.MODULE$.All();
//    public static final FrameRange fr(int start, int endMarker, int step) {
//        return new FrameRange(start, endMarker, step, false);
//    }
//    public static final FrameRange fr(int start, int endMarker) {
//        return new FrameRange(start, endMarker, 1, false);
//    }
//    public static final FrameRange fr(int frame) {
//        return new FrameRange(frame, frame, 1, false);
//    }
//
//    public static final Range ran(int start, int endMarker, int step) {
//        return new Range.Inclusive(start, endMarker, step);
//    }
//    public static final Range ran(int start, int endMarker) {
//        return new Range.Inclusive(start, endMarker, 1);
//    }
//
//
//    public static final DataReader newReader() {
//        return new DataReader();
//    }
//
//    public static final Double[] toArray(Vector<Double> vector) {
//        Double[] tempret = new Double[vector.length()];
//        for(int c = 0; c < tempret.length; c ++ ){
//            tempret[c] = vector.apply(c);
//        }
//        return tempret;
//    }
//    public static final Int[] toArray(Vector<Int> vector) {
//        //return package$.MODULE$.toArrayInt(vector)
//        Int[] tempret = new Int[vector.length()];
//        for(int c = 0; c < tempret.length; c ++ ){
//          tempret[c] = vector.apply(c);
//        }
//        return tempret;
//    }
//    public static final Long[] toArray(Vector<Long> vector) {
//        Long[] tempret = new Long[vector.length()];
//        for(int c = 0; c < tempret.length; c ++ ){
//            tempret[c] = vector.apply(c);
//        }
//        return tempret;
//    }
//
//
//}
