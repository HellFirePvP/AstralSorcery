/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure.change;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.StructureMatchingBuffer;
import hellfirepvp.astralsorcery.common.event.BlockModifyEvent;
import hellfirepvp.astralsorcery.common.structure.BlockStructureObserver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureIntegrityObserver
 * Created by HellFirePvP
 * Date: 02.12.2018 / 11:45
 */
public class StructureIntegrityObserver {

    public static final StructureIntegrityObserver INSTANCE = new StructureIntegrityObserver();

    private StructureIntegrityObserver() {}

    @SubscribeEvent
    public void onChange(BlockModifyEvent event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }

        StructureMatchingBuffer buf = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_MATCH);
        ChunkPos ch = event.getChunk().getPos();
        BlockPos pos = event.getPos();
        IBlockState oldS = event.getOldState();
        IBlockState newS = event.getNewState();

        List<ChangeSubscriber<?>> subscribers = buf.getSubscribers(ch);
        for (ChangeSubscriber<?> subscriber : subscribers) {
            if (subscriber.observes(pos)) {
                subscriber.addChange(pos, oldS, newS);
                buf.markDirty();
            }
        }

        if (oldS.getBlock() instanceof BlockStructureObserver) {
            if (((BlockStructureObserver) oldS.getBlock()).removeWithNewState(world, pos, oldS, newS)) {
                buf.removeSubscriber(pos);
            }
        }
    }

}
