package hellfirepvp.astralsorcery.common.starlight.transmission.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.tile.network.TileNetworkSkybound;
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
public class SimpleIndependentCrystalSource implements IIndependentStarlightSource {

    private CrystalProperties crystalProperties;
    private Constellation sourceType;
    private boolean doesSeeSky;

    public SimpleIndependentCrystalSource() {}

    public SimpleIndependentCrystalSource(@Nonnull CrystalProperties properties, @Nonnull Constellation constellation, boolean seesSky) {
        this.crystalProperties = properties;
        this.sourceType = constellation;
        this.doesSeeSky = seesSky;
    }

    @Override
    public void onUpdate(World world, BlockPos pos) {
        if(world.isRemote || world.provider.getDimension() != 0) return;

        //System.out.println("update tick");
    }

    @Override
    public void informTileStateChange(IStarlightSource sourceTile) {
        TileNetworkSkybound tns = MiscUtils.getTileAt(sourceTile.getWorld(), sourceTile.getPos(), TileNetworkSkybound.class);
        if(tns != null) {
            this.doesSeeSky = tns.doesSeeSky();
        }
        //System.out.println("updated State");
    }

    @Override
    public SourceClassRegistry.SourceProvider getProvider() {
        return new Provider();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {

    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        crystalProperties.writeToNBT(compound);

    }

    public static class Provider implements SourceClassRegistry.SourceProvider {

        @Override
        public IIndependentStarlightSource provideEmptySource() {
            return new SimpleIndependentCrystalSource();
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":SimpleCrystalSource";
        }

    }

}
