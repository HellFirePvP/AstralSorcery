package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStarlightTransmission
 * Created by HellFirePvP
 * Date: 01.08.2016 / 12:25
 */
public interface IStarlightTransmission {

    @Nullable
    default public IPrismTransmissionNode getNode() {
        WorldNetworkHandler netHandler = WorldNetworkHandler.getNetworkHandler(getWorld());
        return netHandler.getTransmissionNode(getPos());
    }

    public BlockPos getPos();

    public World getWorld();

    public IPrismTransmissionNode provideTransmissionNode(BlockPos at);

}
