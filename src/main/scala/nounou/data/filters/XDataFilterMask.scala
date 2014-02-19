package nounou.data.filters

import nounou.data.XData
import nounou.data.discrete.XMask

/**
 * @author ktakagaki
 * @date 2/19/14.
 */
class XDataFilterMask(override val upstream: XData, val mask: XMask) extends XDataFilter(upstream) {

    override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = {
      if( mask.isMaskedFrame(frame, segment, upstream) ){
        0
      }else{
        upstream.readPointImpl(channel, frame, segment)
      }
    }

  }
