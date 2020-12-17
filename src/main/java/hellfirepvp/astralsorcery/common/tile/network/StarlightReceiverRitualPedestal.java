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
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.PartialEffectExecutor;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.world.SkyCollectionHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
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
public class StarlightReceiverRitualPedestal extends SimpleTransmissionReceiver<TileRitualPedestal> {

    private static final Random rand = new Random();

    //own receiver data
    private final Map<BlockPos, Boolean> offsetMirrors = new HashMap<>();

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

    private float noiseDistribution = -1;

    public StarlightReceiverRitualPedestal(BlockPos thisPos) {
        super(thisPos);
    }

    @Override
    public void update(World world) {
        super.update(world);
        this.ticksExisted++;

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
            this.channelingTrait.affectConstellationEffect(properties);
        }
        properties.multiplySize(CrystalCalculations.getRitualEffectRangeFactor(this, this.attributes));

        float maxDrain = 12;
        maxDrain *= CrystalCalculations.getRitualCostReductionFactor(this, this.attributes);
        maxDrain /= Math.max(1F, ((float) (this.getMirrorCount() - 1)) * 0.33F);
        float ritualStrength = ((float) collectedStarlight) / maxDrain;

        BlockPos to = getLocationPos();
        if (this.ritualLinkPos != null) {
            to = this.ritualLinkPos;
        }

        if (this.effect instanceof ConstellationEffectStatus && this.collectedStarlight > 0) {
            this.collectedStarlight = 0;
            if (this.effect.getConfig().enabled.get() && ((ConstellationEffectStatus) this.effect).runStatusEffect(world, to, this.getMirrorCount(), properties, this.channelingTrait)) {
                markDirty(world);
            }
            return;
        }

        float max = 10F * properties.getEffectAmplifier();
        float stretch = 10F / properties.getPotency();

        float executeTimes = (float) Math.atan(ritualStrength / stretch) * max;
        if (properties.isCorrupted()) {
            executeTimes *= Math.max(rand.nextDouble() * 1.4, 0.1);
        }
        PartialEffectExecutor exec = new PartialEffectExecutor(executeTimes, rand);
        while (exec.canExecute()) {
            exec.markExecution();
            if (this.effect.getConfig().enabled.get()) {
                boolean didEffectExecute;
                if (this.effect.needsChunkToBeLoaded()) {
                    didEffectExecute = MiscUtils.executeWithChunk(world, to, to, (pos) -> {
                        return this.effect.playEffect(world, pos, properties, this.channelingTrait);
                    }, false);
                } else {
                    didEffectExecute = this.effect.playEffect(world, to, properties, this.channelingTrait);
                }

                if (didEffectExecute) {
                    markDirty(world);
                }
            }
        }
        this.collectedStarlight = 0F;
    }

    private void collectStarlight(World world) {
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.SERVER);
        if (ctx == null) {
            return;
        }

        double collected = 1.3;
        collected *= 0.25 + (0.75 * DayTimeHelper.getCurrentDaytimeDistribution(world));

        if (this.noiseDistribution == -1) {
            if (world instanceof ISeedReader) {
                this.noiseDistribution = SkyCollectionHelper.getSkyNoiseDistribution((ISeedReader) world, this.getLocationPos());
            } else {
                this.noiseDistribution = 0.3F;
            }
        }

        collected *= CrystalCalculations.getCrystalCollectionRate(attributes);
        collected *= 0.4F + (0.6F * ctx.getDistributionHandler().getDistribution(this.channelingType));
        collected *= 1F + (0.5F * this.noiseDistribution);

        this.collectedStarlight += collected;
    }

    @Override
    public void onStarlightReceive(World world, IWeakConstellation type, double amount) {
        if (this.channelingType != null && this.hasMultiblock && this.channelingType.equals(type)) {
            this.collectedStarlight += amount / 2;
            this.findNextMirror(world);
        }
    }

    //=========================================================================================
    // Tile/Node sync stuff
    //=========================================================================================


    @Override
    public boolean syncTileData(World world, TileRitualPedestal tile) {
        tile.setReceiverData(this.effect != null, this.offsetMirrors, this.attributes);
        this.markDirty(world);
        return true;
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
                this.markForTileSync();
            }
        }

        boolean ritualLinkChanged;
        if (this.ritualLinkPos == null) {
            ritualLinkChanged = trp.getRitualLinkTo() != null;
        } else {
            ritualLinkChanged = !this.ritualLinkPos.equals(trp.getRitualLinkTo());
        }

        this.doesSeeSky = trp.doesSeeSky();
        this.hasMultiblock = trp.hasMultiblock();
        this.channelingType = trp.getRitualConstellation();
        this.channelingTrait = trp.getRitualTrait();
        this.attributes = trp.getAttributes();
        this.ritualLinkPos = trp.getRitualLinkTo();

        if (this.channelingType != null && this.attributes != null && this.hasMultiblock && (this.effect == null || ritualLinkChanged)) {
            this.effect = ConstellationEffectRegistry.createInstance(this, this.channelingType);
            this.markForTileSync();
        }

        if (!this.hasMultiblock || this.effect == null) {
            this.collectedStarlight = 0;
        }

        this.markDirty(trp.getWorld());
        return super.updateFromTileEntity(tile);
    }

    @Override
    public Class<TileRitualPedestal> getTileClass() {
        return TileRitualPedestal.class;
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
        seed |= this.channelingType.getSimpleName().hashCode() * 31;
        Random r = new Random(seed);
        for (int i = 0; i < this.getMirrorCount(); i++) {
            r.nextInt(TileRitualPedestal.RITUAL_CIRCLE_OFFSETS.size());
        }
        BlockPos offset = null;
        int c = 100;
        lblWhile: while (offset == null && c > 0) {
            c--;

            BlockPos test = MiscUtils.getRandomEntry(TileRitualPedestal.RITUAL_CIRCLE_OFFSETS, r);
            RaytraceAssist ray = new RaytraceAssist(getLocationPos(), getLocationPos().add(test));
            Vector3 from = new Vector3(0.5, 0.7, 0.5);
            Vector3 newDir = new Vector3(test).add(0.5, 0.5, 0.5).subtract(from);

            for (BlockPos p : offsetMirrors.keySet()) {
                Vector3 toDir = new Vector3(p).add(0.5, 0.5, 0.5).subtract(from);
                if (Math.toDegrees(toDir.angle(newDir)) <= 30) {
                    continue lblWhile;
                }
                if (test.distanceSq(p) <= 3) {
                    continue lblWhile;
                }
            }

            if (!ray.isClear(world)) {
                continue;
            }
            offset = test;
        }

        if (offset != null) {
            this.offsetMirrors.put(offset, false);
            this.markForTileSync();
            this.markDirty(world);
        }
    }

    private void validateMirrorPositions(World world) {
        WorldNetworkHandler handle = WorldNetworkHandler.getNetworkHandler(world);
        List<BlockPos> srcLinkingToThis = this.getSources();

        boolean needsUpdate = false;
        for (BlockPos pos : new ArrayList<>(this.offsetMirrors.keySet())) {
            BlockPos actualPos = this.getLocationPos().add(pos);
            boolean existingFlag = this.offsetMirrors.get(pos);

            //If the source is not linking to this
            if (!srcLinkingToThis.contains(actualPos)) {
                this.offsetMirrors.put(pos, false);
                if (existingFlag) {
                    needsUpdate = true;
                }
                continue;
            }

            IPrismTransmissionNode other = handle.getTransmissionNode(actualPos);
            if (other == null) {
                continue;
            }

            boolean foundLink = false;
            for (NodeConnection<IPrismTransmissionNode> n : other.queryNext(handle)) {
                if (n.getTo().equals(getLocationPos())) {
                    boolean connect = n.canConnect();
                    this.offsetMirrors.put(pos, connect);
                    if (connect != existingFlag) {
                        needsUpdate = true;
                    }
                    foundLink = true;
                    break;
                }
            }

            if (!foundLink) {
                this.offsetMirrors.put(pos, false);
                if (existingFlag) {
                    needsUpdate = true;
                }
            }
        }
        if (needsUpdate) {
            this.markForTileSync();
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
