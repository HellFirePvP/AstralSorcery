package hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleIndependentSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileNetworkSkybound;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleIndependentSource
 * Created by HellFirePvP
 * Date: 04.08.2016 / 15:01
 */
public class SimpleIndependentCrystalSource extends SimpleIndependentSource {

    private CrystalProperties crystalProperties;
    private boolean doesSeeSky;

    public SimpleIndependentCrystalSource(@Nonnull CrystalProperties properties, @Nonnull Constellation constellation, boolean seesSky) {
        super(constellation);
        this.crystalProperties = properties;
        this.doesSeeSky = seesSky;
    }

    //TODO produce.
    @Override
    public double produceStarlightTick(World world, BlockPos pos) {
        return 0;
    }

    @Override
    public void informTileStateChange(IStarlightSource sourceTile) {
        TileNetworkSkybound tns = MiscUtils.getTileAt(sourceTile.getWorld(), sourceTile.getPos(), TileNetworkSkybound.class);
        if(tns != null) {
            this.doesSeeSky = tns.doesSeeSky();
        }
    }

    @Override
    public SourceClassRegistry.SourceProvider getProvider() {
        return new Provider();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.crystalProperties = CrystalProperties.readFromNBT(compound);
        this.doesSeeSky = compound.getBoolean("seesSky");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        crystalProperties.writeToNBT(compound);
        compound.setBoolean("seesSky", doesSeeSky);
    }

    public static class Provider implements SourceClassRegistry.SourceProvider {

        @Override
        public IIndependentStarlightSource provideEmptySource() {
            return new SimpleIndependentCrystalSource(null, null, false);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":SimpleIndependentCrystalSource";
        }

    }

}
