/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.base;

import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractData
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:11
 */
public abstract class AbstractData {

    private final ResourceLocation key;

    protected AbstractData(ResourceLocation key) {
        this.key = key;
    }

    public final void markDirty() {
        SyncDataHolder.markForUpdate(this.key);
    }

    public abstract void clear(DimensionType dimType);

    public abstract void clearServer();

    public abstract void writeAllDataToPacket(CompoundNBT compound);

    public abstract void writeDiffDataToPacket(CompoundNBT compound);

}
