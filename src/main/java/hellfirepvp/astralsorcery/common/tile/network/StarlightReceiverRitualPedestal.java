/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectStatus;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalCalculations;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionProvider;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.world.SkyCollectionHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightReceiverRitualPedestal
 * Created by HellFirePvP
 * Date: 09.07.2019 / 19:26
 */
public class StarlightReceiverRitualPedestal extends SimpleTransmissionReceiver {

    private static final Random rand = new Random();
    private static final BlockPos[] circleOffsets = new BlockPos[] {
            new BlockPos( 4, 2,  0),
            new BlockPos( 4, 2,  1),
            new BlockPos( 3, 2,  2),
            new BlockPos( 2, 2,  3),
            new BlockPos( 1, 2,  4),
            new BlockPos( 0, 2,  4),
            new BlockPos(-1, 2,  4),
            new BlockPos(-2, 2,  3),
            new BlockPos(-3, 2,  2),
            new BlockPos(-4, 2,  1),
            new BlockPos(-4, 2,  0),
            new BlockPos(-4, 2, -1),
            new BlockPos(-3, 2, -2),
            new BlockPos(-2, 2, -3),
            new BlockPos(-1, 2, -4),
            new BlockPos( 0, 2, -4),
            new BlockPos( 1, 2, -4),
            new BlockPos( 2, 2, -3),
            new BlockPos( 3, 2, -2),
            new BlockPos( 4, 2, -1)
    };

    //own receiver data
    private Map<BlockPos, Boolean> offsetMirrors = new HashMap<>();

    //tile data
    private boolean doesSeeSky = false, hasMultiblock = false;
    private IWeakConstellation channelingType = null;
    private IMinorConstellation channelingTrait = null;
    private CrystalAttributes attributes = null;
    private BlockPos ritualLinkPos = null;

    //Random other data
    private int ticksExisted = 0;
    private ConstellationEffect effect = null;
    private double collectedStarlight = 0;

    private boolean needsTileSync = false;
    private float noiseDistribution = -1;

    public StarlightReceiverRitualPedestal(BlockPos thisPos) {
        super(thisPos);
    }

    @Override
    public void update(World world) {
        this.ticksExisted++;

        if (this.needsTileSync && this.syncData(world)) {
            this.needsTileSync = false;
        }

        if (!this.hasMultiblock || this.channelingType == null || this.attributes == null) {
            return;
        }

        if ((this.ticksExisted % 20) == 0) {
            validateMirrorPositions(world);
        }

        if (this.doesSeeSky) {
            collectStarlight(world);
        }

        if (this.effect != null && this.collectedStarlight > 0) {
            doRitualEffect(world);
        }
    }

    private void doRitualEffect(World world) {
        ConstellationEffectProperties properties = this.effect.createProperties(this.getMirrorCount());
        if (this.channelingTrait != null) {
            properties.modify(this.channelingTrait);
        }
        properties.multiplySize(CrystalCalculations.getRitualEffectRangeFactor(this, this.attributes));

        double maxDrain = 12D;
        maxDrain *= CrystalCalculations.getRitualCostReductionFactor(this, this.attributes);
        maxDrain /= Math.max(1, this.getMirrorCount() - 1);
        collectedStarlight *= properties.getPotency();
        int executeTimes = MathHelper.floor(collectedStarlight / maxDrain);

        // Ranges from 1 to 23.21637[...]
        int nonFracturingExecutions = MathHelper.floor(
                Math.max(1, Math.sqrt(CrystalCalculations.getRitualEffectCapacityFactor(this, this.attributes)) * 10) *
                        properties.getFracturationLowerBoundaryMultiplier());
        double fractureChancePer = CrystalCalculations.getRitualCrystalFractureChance(executeTimes, nonFracturingExecutions)
                * properties.getFracturationRate();
        int part = Math.max(1, executeTimes - nonFracturingExecutions);

        BlockPos to = getLocationPos();
        if (this.ritualLinkPos != null) {
            to = this.ritualLinkPos;
        }

        if (this.effect instanceof ConstellationEffectStatus && collectedStarlight > 0) {
            collectedStarlight = 0;
            if (this.effect.getConfig().enabled.get() &&
                    ((ConstellationEffectStatus) this.effect).runEffect(world, to, this.getMirrorCount(), properties, this.channelingTrait)) {
                for (int i = 0; i < part; i++) {
                    if(rand.nextFloat() < (fractureChancePer * properties.getEffectAmplifier() / part)) {
                        fractureCrystal();
                    }
                }
                markDirty(world);
            }
            return;
        }

        executeTimes = MathHelper.floor(executeTimes * properties.getEffectAmplifier());
        for (int i = 0; i < executeTimes; i++) {
            if (collectedStarlight >= maxDrain) {
                collectedStarlight -= maxDrain;
            } else {
                collectedStarlight = 0F;
            }
            if (this.effect.getConfig().enabled.get() &&
                    this.effect.playEffect(world, to, properties, this.channelingTrait)) {
                if (rand.nextFloat() < (fractureChancePer * properties.getEffectAmplifier() / part)) {
                    fractureCrystal();
                }
                markDirty(world);
            }
        }
    }

    private void fractureCrystal() {
        //TODO fracture?
    }

    private void collectStarlight(World world) {
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.SERVER);
        if (ctx == null) {
            return;
        }

        double collected = 0.25 + (0.75 * DayTimeHelper.getCurrentDaytimeDistribution(world));

        if (this.noiseDistribution == -1) {
            this.noiseDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, this.getLocationPos());
        }

        collected *= CrystalCalculations.getCrystalCollectionRate(attributes);
        collected *= 0.2F + (0.8F * ctx.getDistributionHandler().getDistribution(this.channelingType));
        collected *= 1F + (0.5F * this.noiseDistribution);

        this.collectedStarlight += collected / 2F;
    }

    @Override
    public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
        if (this.channelingType != null && this.hasMultiblock && this.channelingType.equals(type)) {
            this.collectedStarlight += amount;
            this.findNextMirror(world);
        }
    }

    //=========================================================================================
    // Tile/Node sync stuff
    //=========================================================================================

    private boolean syncData(World world) {
        TileRitualPedestal trp = this.getTileAtPos(world, TileRitualPedestal.class);
        if (trp != null) {
            trp.setReceiverData(this.effect != null, this.offsetMirrors);
            return true;
        }
        return false;
    }

    @Override
    public <T extends TileEntity> boolean updateFromTileEntity(T tile) {
        if (!(tile instanceof TileRitualPedestal)) {
            return super.updateFromTileEntity(tile); //Whatever.
        }

        TileRitualPedestal trp = (TileRitualPedestal) tile;
        if (this.channelingType != trp.getRitualConstellation() ||
                (this.attributes != null && trp.getAttributes() == null) ||
                this.hasMultiblock != trp.hasMultiblock()) {

            this.effect = null;
            this.offsetMirrors.clear();
            if (trp.isWorking() || !trp.getMirrors().isEmpty()) {
                this.needsTileSync = true;
            }
        }

        this.doesSeeSky = trp.doesSeeSky();
        this.hasMultiblock = trp.hasMultiblock();
        this.channelingType = trp.getRitualConstellation();
        this.channelingTrait = trp.getRitualTrait();
        this.attributes = trp.getAttributes();
        this.ritualLinkPos = trp.getRitualLinkTo();

        if (this.channelingType != null && this.attributes != null && this.hasMultiblock && this.effect == null) {
            this.effect = ConstellationEffectRegistry.createInstance(this, this.channelingType);
            this.needsTileSync = true;
        }

        if (!this.hasMultiblock || this.effect == null) {
            this.collectedStarlight = 0;
        }

        this.markDirty(trp.getWorld());
        return super.updateFromTileEntity(tile);
    }

    //=========================================================================================
    // Stuff surrounding lenses
    //=========================================================================================

    private void findNextMirror(World world) {
        if (this.offsetMirrors.size() >= TileRitualPedestal.MAX_MIRROR_COUNT || this.effect == null || this.channelingType == null) {
            return;
        }

        long seed = 3451968351053166105L;
        seed |= this.getLocationPos().toLong() * 31;
        seed |= this.channelingType.getUnlocalizedName().hashCode() * 31;
        Random r = new Random(seed);
        for (int i = 0; i < this.getMirrorCount(); i++) {
            r.nextInt(circleOffsets.length);
        }
        BlockPos offset = null;
        int c = 100;
        lblWhile: while (offset == null && c > 0) {
            c--;

            BlockPos test = circleOffsets[r.nextInt(circleOffsets.length)];
            RaytraceAssist ray = new RaytraceAssist(getLocationPos(), getLocationPos().add(test));
            Vector3 from = new Vector3(0.5, 0.7, 0.5);
            Vector3 newDir = new Vector3(test).add(0.5, 0.5, 0.5).subtract(from);

            for (BlockPos p : offsetMirrors.keySet()) {
                Vector3 toDir = new Vector3(p).add(0.5, 0.5, 0.5).subtract(from);
                if(Math.toDegrees(toDir.angle(newDir)) <= 30) {
                    continue lblWhile;
                }
                if(test.distanceSq(p) <= 3) {
                    continue lblWhile;
                }
            }

            if(!ray.isClear(world)) {
                continue;
            }
            offset = test;
        }

        if (offset != null) {
            this.offsetMirrors.put(offset, false);
            this.markDirty(world);
            this.needsTileSync = true;
        }
    }

    private void validateMirrorPositions(World world) {
        WorldNetworkHandler handle = WorldNetworkHandler.getNetworkHandler(world);
        List<BlockPos> srcLinkingToThis = this.getSources();

        for (BlockPos pos : new ArrayList<>(this.offsetMirrors.keySet())) {
            BlockPos actualPos = this.getLocationPos().add(pos);

            //If the source is not linking to this
            if (!srcLinkingToThis.contains(actualPos)) {
                this.offsetMirrors.put(pos, false);
                continue;
            }

            IPrismTransmissionNode other = handle.getTransmissionNode(actualPos);
            if (other == null) {
                continue;
            }

            boolean foundLink = false;
            for (NodeConnection<IPrismTransmissionNode> n : other.queryNext(handle)) {
                if (n.getTo().equals(getLocationPos())) {
                    this.offsetMirrors.put(pos, n.canConnect());
                    foundLink = true;
                    break;
                }
            }

            if (!foundLink) {
                this.offsetMirrors.put(pos, false);
            }
        }
    }

    private int getMirrorCount() {
        return (int) this.offsetMirrors.values().stream()
                .filter(b -> b)
                .count();
    }

    //=========================================================================================
    // Misc and I/O
    //=========================================================================================

    @Nullable
    public IWeakConstellation getChannelingType() {
        return channelingType;
    }

    @Nullable
    public IMinorConstellation getChannelingTrait() {
        return channelingTrait;
    }

    @Override
    public boolean needsUpdate() {
        return true;
    }

    @Override
    public TransmissionProvider getProvider() {
        return new Provider();
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);

        this.needsTileSync = compound.getBoolean("needsTileSync");

        this.doesSeeSky = compound.getBoolean("doesSeeSky");
        this.hasMultiblock = compound.getBoolean("hasMultiblock");
        this.ticksExisted = compound.getInt("ticksExisted");
        this.collectedStarlight = compound.getDouble("collectedStarlight");

        IConstellation channeling = IConstellation.readFromNBT(compound, "channelingType");
        if (channeling instanceof IWeakConstellation) {
            this.channelingType = (IWeakConstellation) channeling;
        } else {
            this.channelingType = null;
        }
        IConstellation trait = IConstellation.readFromNBT(compound, "channelingTrait");
        if (trait instanceof IMinorConstellation) {
            this.channelingTrait = (IMinorConstellation) trait;
        } else {
            this.channelingTrait = null;
        }

        this.attributes = CrystalAttributes.getCrystalAttributes(compound);
        if (compound.contains("ritualLinkPos")) {
            this.ritualLinkPos = NBTHelper.readBlockPosFromNBT(compound.getCompound("ritualLinkPos"));
        } else {
            this.ritualLinkPos = null;
        }

        this.offsetMirrors.clear();
        ListNBT tagList = compound.getList("mirrors", Constants.NBT.TAG_COMPOUND);
        for (INBT nbt : tagList) {
            CompoundNBT tag = (CompoundNBT) nbt;
            this.offsetMirrors.put(NBTHelper.readBlockPosFromNBT(tag), tag.getBoolean("connect"));
        }

        //Reset ritual effect.
        if (this.channelingType != null) {
            this.effect = ConstellationEffectRegistry.createInstance(this, this.channelingType);
            if (this.effect != null && compound.contains("effect")) {
                this.effect.readFromNBT(compound.getCompound("effect"));
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);

        compound.putBoolean("needsTileSync", this.needsTileSync);

        compound.putBoolean("doesSeeSky", this.doesSeeSky);
        compound.putBoolean("hasMultiblock", this.hasMultiblock);
        compound.putInt("ticksExisted", this.ticksExisted);
        compound.putDouble("collectedStarlight", this.collectedStarlight);

        if (this.channelingType != null) {
            this.channelingType.writeToNBT(compound, "channelingType");
        }
        if (this.channelingTrait != null) {
            this.channelingTrait.writeToNBT(compound, "channelingTrait");
        }

        if (attributes != null) {
            attributes.store(compound);
        }
        if (this.ritualLinkPos != null) {
            compound.put("ritualLinkPos", NBTHelper.writeBlockPosToNBT(this.ritualLinkPos, new CompoundNBT()));
        }

        ListNBT listPositions = new ListNBT();
        for (Map.Entry<BlockPos, Boolean> posEntry : this.offsetMirrors.entrySet()) {
            CompoundNBT cmp = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(posEntry.getKey(), cmp);
            cmp.putBoolean("connect", posEntry.getValue());
            listPositions.add(cmp);
        }
        compound.put("mirrors", listPositions);

        if (this.channelingType != null && this.effect != null) {
            NBTHelper.setAsSubTag(compound, "effect", this.effect::writeToNBT);
        }
    }

    public static class Provider extends TransmissionProvider {

        @Override
        public IPrismTransmissionNode get() {
            return new StarlightReceiverRitualPedestal(null);
        }

    }
}
