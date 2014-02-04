package nounou;

import scala.collection.immutable.Range;

/**
 * @author ktakagaki
 * @date 1/30/14.
 */
class NNJ {

    public static final Range RangeAll = Range(0, -1, 1);
    public static final Range Range(int start, int endExclusive, int step) {
        return new Range(start, endExclusive, step);
    }
    public static final Range Range(int start, int end) {
        return new Range(start, end, 1);
    }

//    public static final Span SpanAll = Span(0, -1, 1);
//    public static final Span Span(int start, int end, int step) {
//        return new nounou.data.Span(start, end, step);
//    }
}
