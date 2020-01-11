/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IIndependentStarlightSource
 * Created by HellFirePvP
 * Date: 04.08.2016 / 12:34
 */
public interface IIndependentStarlightSource {

    //As the purpose of the source, this should produce the starlight - called once every tick
    public float produceStarlightTick(World world, BlockPos pos);

    //Can be null or change per tick.
    @Nullable
    public IWeakConstellation getStarlightType();

    default public boolean providesAutoLink() {
        return false;
    }

    //Update the state of the independent tile. for example if "doesSeeSky" has changed or something.
    //Return true to indicate a successful update.
    default public <T extends TileEntity> boolean updateFromTileEntity(T tile) {
        return true;
    }

    //Update (maybe) if proximity to other sources should be checked - to prevent the user from placing everything super dense.
    //Threaded to prevent overhead, so remember to sync savely to avoid CME or other threaded stuffs.
    //You may only do position-based logic here. Data on the sources MIGHT be invalid at this early stage of changes.
    //Called whenever sources are changed (added/removed) from a world.
    public void threadedUpdateProximity(BlockPos thisPos, Map<BlockPos, IIndependentStarlightSource> otherSources);

    public SourceClassRegistry.SourceProvider getProvider();

    public void readFromNBT(CompoundNBT compound);

    public void writeToNBT(CompoundNBT compound);

}
