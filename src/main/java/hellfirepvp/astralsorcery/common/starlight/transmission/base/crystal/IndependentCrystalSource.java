/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalCalculations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.world.SkyCollectionHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IndependentCrystalSource
 * Created by HellFirePvP
 * Date: 04.08.2016 / 15:01
 */
public class IndependentCrystalSource implements IIndependentStarlightSource {

    public static final double MIN_DST = 16;

    private IWeakConstellation constellation = null;
    private CrystalAttributes crystalAttributes = null;
    private boolean doesSeeSky = false;
    private boolean doesAutoLink = false;

    private double collectionDstMultiplier = 1;
    private BlockPos closestOtherCollector = null;

    private float posDistribution = -1;
    private boolean enhanced = false;

    @Override
    public float produceStarlightTick(ServerWorld world, BlockPos pos) {
        if (!doesSeeSky || crystalAttributes == null) {
            return 0F;
        }
        IWeakConstellation cst = getStarlightType();
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.SERVER);
        if (ctx == null || cst == null) {
            return 0F;
        }

        if (posDistribution == -1) {
            posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, pos);
        }

        if (closestOtherCollector != null && rand.nextInt(40) == 0) {
            PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.LIGHTNING)
                    .addData(buf -> {
                        ByteBufUtils.writeVector(buf, new Vector3(pos).add(0.5, 0.5, 0.5));
                        ByteBufUtils.writeVector(buf, new Vector3(closestOtherCollector).add(0.5, 0.5, 0.5));
                        buf.writeInt(this.constellation.getConstellationColor().darker().getRGB());
                    });
            PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(world, pos, 32));
        }

        Function<Float, Float> distrFunction = getDistributionFunc();
        float perc = CrystalCalculations.getCollectorCrystalCollectionRate(this);
        perc *= distrFunction.apply(0.3F + (0.7F * DayTimeHelper.getCurrentDaytimeDistribution(world)));
        perc *= collectionDstMultiplier;
        perc *= 1 + (0.3 * posDistribution);
        perc *= 0.4 + 0.6 * ctx.getDistributionHandler().getDistribution(cst);
        return perc;
    }

    private Function<Float, Float> getDistributionFunc() {
        if (enhanced) {
            return (in) -> 0.6F + (0.5F * in);
        } else {
            return (in) -> 0.2F + (0.8F * in);
        }
    }

    @Override
    public boolean providesAutoLink() {
        return this.doesAutoLink;
    }

    @Override
    public <T extends TileEntity> boolean updateFromTileEntity(T tile) {
        if (!(tile instanceof TileCollectorCrystal)) {
            return true;
        }
        TileCollectorCrystal tcc = (TileCollectorCrystal) tile;
        this.doesSeeSky = tcc.doesSeeSky();
        this.doesAutoLink = !tcc.isPlayerMade(); //Structural, not player-placed.
        this.enhanced = tcc.isEnhanced();
        this.constellation = tcc.getAttunedConstellation();
        this.crystalAttributes = tcc.getAttributes();
        return true;
    }

    @Override
    public void threadedUpdateProximity(BlockPos thisPos, Map<BlockPos, IIndependentStarlightSource> otherSources) {
        double minDstSq = Double.MAX_VALUE;
        BlockPos closest = null;
        for (BlockPos other : otherSources.keySet()) {
            if (other.equals(thisPos)) {
                continue;
            }
            double dstSq = thisPos.distanceSq(Vector3d.copy(other), false);
            if (dstSq < minDstSq) {
                minDstSq = dstSq;
                closest = other;
            }
        }
        double dst = Math.sqrt(minDstSq);
        if (dst <= MIN_DST) {
            this.collectionDstMultiplier = dst / MIN_DST;
            this.closestOtherCollector = closest;
        } else {
            this.collectionDstMultiplier = 1;
            this.closestOtherCollector = null;
        }
    }

    public CrystalAttributes getCrystalAttributes() {
        return crystalAttributes;
    }

    @Nullable
    @Override
    public IWeakConstellation getStarlightType() {
        return this.constellation;
    }

    @Override
    public SourceClassRegistry.SourceProvider getProvider() {
        return new Provider();
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        this.constellation = NBTHelper.readOptional(compound, "constellation", (nbt) -> {
            IConstellation cst = IConstellation.readFromNBT(nbt);
            if (cst instanceof IWeakConstellation) {
                return (IWeakConstellation) cst;
            }
            return null;
        });
        this.crystalAttributes = CrystalAttributes.getCrystalAttributes(compound);
        this.doesSeeSky = compound.getBoolean("doesSeeSky");
        this.doesAutoLink = compound.getBoolean("doesAutoLink");
        this.collectionDstMultiplier = compound.getDouble("collectionDstMultiplier");
        this.closestOtherCollector = NBTHelper.readOptional(compound, "closestOtherCollector", NBTHelper::readBlockPosFromNBT);
        this.enhanced = compound.getBoolean("enhanced");
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        if (crystalAttributes != null) {
            crystalAttributes.store(compound);
        }
        NBTHelper.writeOptional(compound, "constellation", this.constellation, (nbt, cst) -> cst.writeToNBT(nbt));
        compound.putBoolean("doesSeeSky", doesSeeSky);
        compound.putBoolean("doesAutoLink", doesAutoLink);
        compound.putDouble("collectionDstMultiplier", collectionDstMultiplier);
        NBTHelper.writeOptional(compound, "closestOtherCollector", this.closestOtherCollector, (nbt, pos) -> NBTHelper.writeBlockPosToNBT(pos, nbt));
        compound.putBoolean("enhanced", enhanced);
    }

    public static class Provider implements SourceClassRegistry.SourceProvider {

        @Override
        public IIndependentStarlightSource provideEmptySource() {
            return new IndependentCrystalSource();
        }

        @Override
        @Nonnull
        public ResourceLocation getIdentifier() {
            return AstralSorcery.key("independent_crystal_source");
        }

    }

}
