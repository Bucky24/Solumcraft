package SpongeBridge;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solum on 1/10/2016.
 */
public class BlockList extends ArrayList {
    List<Location<World>> affectedBlocks;

    public BlockList(List<Location<World>> blocks) {
        this.affectedBlocks = blocks;
    }

    public void clear() {
        affectedBlocks.clear();
    }
}
