package hellfirepvp.astralsorcery.common.data.world.data;

import hellfirepvp.astralsorcery.common.data.world.CachedWorldData;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightNetworkBuffer
 * Created by HellFirePvP
 * Date: 03.08.2016 / 00:10
 */
public class LightNetworkBuffer extends CachedWorldData {

    public LightNetworkBuffer() {
        super(WorldCacheManager.SaveKey.LIGHT_NETWORK);
    }

    public WorldNetworkHandler getNetworkHandler() {
        return new WorldNetworkHandler(this);
    }

    @Override
    public LightNetworkBuffer constructNewData() {
        return new LightNetworkBuffer();
    }

    @Override
    public void updateTick() {}

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        return nbt;
    }
}
