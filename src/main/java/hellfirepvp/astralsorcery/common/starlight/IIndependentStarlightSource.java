package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IIndependentStarlightSource
 * Created by HellFirePvP
 * Date: 04.08.2016 / 12:34
 */
public interface IIndependentStarlightSource {

    //We don't really need that method at the moment tho.... lol..
    //Called eventhough the tile is not loaded. that's what we wanted.
    //public void onUpdate(World world, BlockPos pos);

    //As the purpose of the source, this should produce the starlight - called once every tick
    public double produceStarlightTick(World world, BlockPos pos);

    public Constellation getStarlightType();

    //Update the state of the independent tile. for example if "doesSeeSky" has changed or something.
    public void informTileStateChange(IStarlightSource sourceTile);

    public SourceClassRegistry.SourceProvider getProvider();

    public void readFromNBT(NBTTagCompound compound);

    public void writeToNBT(NBTTagCompound compound);

}
