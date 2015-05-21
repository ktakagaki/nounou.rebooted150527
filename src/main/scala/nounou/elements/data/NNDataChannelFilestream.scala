package nounou.elements.data

import java.io.DataInput

/**
 * Created by ktakagaki on 15/05/21.
 */
abstract class NNDataChannelFilestream extends NNDataChannel {

    val fileHandle: DataInput

}
