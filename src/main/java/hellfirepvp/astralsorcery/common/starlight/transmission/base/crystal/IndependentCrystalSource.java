/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleIndependentSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.tile.base.TileNetworkSkybound;
import hellfirepvp.astralsorcery.common.tile.base.TileSourceBase;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.CrystalCalculations;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.SkyCollectionHelper;
import hellfirepvp.astralsorcery.common.util.world.SkyCollectionHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;

import java.util.Map;
import java.util.function.Function;

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
    private boolean doesSeeSky, structural;
    private double collectionDstMultiplier = 1;
    private float posDistribution = -1;

    private boolean enhanced = false;

    public IndependentCrystalSource(CrystalProperties properties, IWeakConstellation constellation, boolean seesSky, boolean hasBeenLinkedBefore, BlockCollectorCrystalBase.CollectorCrystalType type) {
        super(constellation);
        this.crystalProperties = properties;
        this.doesSeeSky = seesSky;
        this.type = type;
        this.structural = hasBeenLinkedBefore;
    }

    @Override
    public float produceStarlightTick(World world, BlockPos pos) {
        IWeakConstellation cst = getStarlightType();
        if(cst == null) return 0F;
        if(crystalProperties == null || type == null) return 0F;

        WorldContext ctx = SkyHandler.getContext(world, Dist.DEDICATED_SERVER);
        if (!doesSeeSky || ctx == null) {
            return 0F;
        }

        if(posDistribution == -1) {
            posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, pos);
        }

        Function<Float, Float> distrFunction = getDistributionFunc();
        double perc = distrFunction.apply(DayTimeHelper.getCurrentDaytimeDistribution(world));
        perc *= collectionDstMultiplier;
        perc *= 1 + (0.3 * posDistribution);
        return (float) (perc * CrystalCalculations.getCollectionAmt(crystalProperties,
                distrFunction.apply(ctx.getDistributionHandler().getDistribution(cst))));
    }

    public void setEnhanced(boolean enhanced) {
        this.enhanced = enhanced;
    }

    private Function<Float, Float> getDistributionFunc() {
        if(enhanced) {
            return (in) -> 0.6F + (1.1F * in);
        } else {
            return (in) -> 0.2F + (0.8F * in);
        }
    }

    @Override
    public boolean providesAutoLink() {
        return structural;
    }

    @Override
    public void informTileStateChange(IStarlightSource sourceTile) {
        TileEntityTick tns = MiscUtils.getTileAt(sourceTile.getTrWorld(), sourceTile.getTrPos(), TileEntityTick.class, true);
        if(tns != null) {
            this.doesSeeSky = tns.doesSeeSky();
        }
        if(tns instanceof TileSourceBase && ((TileSourceBase) tns).hasBeenLinked()) {
            this.structural = true;
        }
        if(tns instanceof TileCollectorCrystal) {
            this.crystalProperties = ((TileCollectorCrystal) tns).getCrystalProperties();
            this.type = ((TileCollectorCrystal) tns).getType();
            this.starlightType = ((TileCollectorCrystal) tns).getConstellation();
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
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);

        this.crystalProperties = CrystalProperties.readFromNBT(compound);
        this.doesSeeSky = compound.getBoolean("seesSky");
        this.structural = compound.getBoolean("linkedBefore");
        this.type = BlockCollectorCrystalBase.CollectorCrystalType.values()[compound.getInt("collectorType")];
        this.collectionDstMultiplier = compound.getDouble("dstMul");
        this.enhanced = compound.getBoolean("enhanced");
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);

        if(crystalProperties != null) {
            crystalProperties.writeToNBT(compound);
        }
        compound.putBoolean("seesSky", doesSeeSky);
        compound.putBoolean("linkedBefore", structural);
        compound.putInt("collectorType", type == null ? 0 : type.ordinal());
        compound.putDouble("dstMul", collectionDstMultiplier);
        compound.putBoolean("enhanced", enhanced);
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
