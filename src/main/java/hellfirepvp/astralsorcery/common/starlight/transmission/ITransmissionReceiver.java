package hellfirepvp.astralsorcery.common.starlight.transmission;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ITransmissionReceiver
 * Created by HellFirePvP
 * Date: 05.08.2016 / 13:43
 */
public interface ITransmissionReceiver extends IPrismTransmissionNode {

    @Override
    default public List<NodeConnection<IPrismTransmissionNode>> queryNext(WorldNetworkHandler handler) {
        return new LinkedList<>();
    }

    @Override
    default public void notifyLink(World world, BlockPos to) {}

    @Override
    default public void notifyUnlink(World world, BlockPos to) {}

    public void onStarlightReceive(World world, boolean isChunkLoaded, Constellation type, double amount);

}
