package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.SimpleCrystalPrismTransmissionNode;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCrystalPrismLens
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:15
 */
public class TileCrystalPrismLens extends TileCrystalLens {

    @Nullable
    @Override
    public String getUnlocalizedDisplayName() {
        return "undefined";
    }

    @Override
    public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return new SimpleCrystalPrismTransmissionNode(at, getCrystalProperties());
    }
}
