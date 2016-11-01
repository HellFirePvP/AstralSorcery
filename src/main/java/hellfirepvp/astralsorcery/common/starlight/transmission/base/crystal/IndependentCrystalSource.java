package hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleIndependentSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileNetworkSkybound;
import hellfirepvp.astralsorcery.common.tile.base.TileSourceBase;
import hellfirepvp.astralsorcery.common.util.CrystalCalculations;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IndependentCrystalSource
 * Created by HellFirePvP
 * Date: 04.08.2016 / 15:01
 */
public class IndependentCrystalSource extends SimpleIndependentSource {

    public static final double MIN_DST = 16;

    private CrystalProperties crystalProperties;
    private BlockCollectorCrystalBase.CollectorCrystalType type;
    private boolean doesSeeSky, hasBeenLinkedBefore;
    private double collectionDstMultiplier = 1;

    public IndependentCrystalSource(@Nonnull CrystalProperties properties, @Nonnull Constellation constellation, boolean seesSky, boolean hasBeenLinkedBefore, @Nonnull BlockCollectorCrystalBase.CollectorCrystalType type) {
        super(constellation);
        this.crystalProperties = properties;
        this.doesSeeSky = seesSky;
        this.type = type;
        this.hasBeenLinkedBefore = hasBeenLinkedBefore;
    }

    @Override
    public float produceStarlightTick(World world, BlockPos pos) {
        if(!doesSeeSky || world.provider.getDimension() != 0) {
            return 0F;
        }
        double perc = 0.2D + (0.8D * CelestialHandler.calcDaytimeDistribution(world));
        perc *= collectionDstMultiplier;
        return (float) (perc * CrystalCalculations.getCollectionAmt(crystalProperties, CelestialHandler.getCurrentDistribution(getStarlightType())));
    }

    @Override
    public boolean providesAutoLink() {
        return !hasBeenLinkedBefore;
    }

    @Override
    public void informTileStateChange(IStarlightSource sourceTile) {
        TileNetworkSkybound tns = MiscUtils.getTileAt(sourceTile.getTrWorld(), sourceTile.getTrPos(), TileNetworkSkybound.class, true);
        if(tns != null) {
            this.doesSeeSky = tns.doesSeeSky();
        }
        if(tns instanceof TileSourceBase && ((TileSourceBase) tns).hasBeenLinked()) {
            this.hasBeenLinkedBefore = true;
        }
    }

    @Override
    public void threadedUpdateProximity(BlockPos thisPos, Map<BlockPos, IIndependentStarlightSource> otherSources) {
        double minDstSq = Double.MAX_VALUE;
        for (BlockPos other : otherSources.keySet()) {
            if(other.equals(thisPos)) continue;
            double dstSq = thisPos.distanceSq(other);
            if(dstSq < minDstSq) {
                minDstSq = dstSq;
            }
        }
        double dst = Math.sqrt(minDstSq);
        if(dst <= MIN_DST) {
            collectionDstMultiplier = dst / MIN_DST;
        } else {
            collectionDstMultiplier = 1;
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
        this.hasBeenLinkedBefore = compound.getBoolean("linkedBefore");
        this.type = BlockCollectorCrystalBase.CollectorCrystalType.values()[compound.getInteger("collectorType")];
        this.collectionDstMultiplier = compound.getDouble("dstMul");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        crystalProperties.writeToNBT(compound);
        compound.setBoolean("seesSky", doesSeeSky);
        compound.setBoolean("linkedBefore", hasBeenLinkedBefore);
        compound.setInteger("collectorType", type.ordinal());
        compound.setDouble("dstMul", collectionDstMultiplier);
    }

    public static class Provider implements SourceClassRegistry.SourceProvider {

        @Override
        public IIndependentStarlightSource provideEmptySource() {
            return new IndependentCrystalSource(null, null, false, false, null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":IndependentCrystalSource";
        }

    }

}
