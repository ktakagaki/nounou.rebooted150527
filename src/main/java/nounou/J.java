package nounou;

import nounou.data.Span;
import nounou.data.Span$;

/**
 * @author ktakagaki
 * @date 1/30/14.
 */
class NNJ {

    public static final Span SpanAll = Span(0, -1, 1);
    public static final Span Span(int start, int end, int step) {
        return new nounou.data.Span(start, end, step);
    }
}
