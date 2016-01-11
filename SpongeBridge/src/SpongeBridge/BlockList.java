package SpongeBridge;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solum on 1/10/2016.
 */
public class BlockList extends ArrayList {
    List<Transaction<BlockSnapshot> > snapshots;

    public BlockList(List<Transaction<BlockSnapshot> > snapshots) {
        this.snapshots = snapshots;
    }

    public void clear() {
        snapshots.clear();
    }
}
