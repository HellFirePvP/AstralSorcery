/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionProvider;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalCalculations;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.world.SkyCollectionHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.Constants;

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
            new BlockPos( 4, 0,  0),
            new BlockPos( 4, 0,  1),
            new BlockPos( 3, 0,  2),
            new BlockPos( 2, 0,  3),
            new BlockPos( 1, 0,  4),
            new BlockPos( 0, 0,  4),
            new BlockPos(-1, 0,  4),
            new BlockPos(-2, 0,  3),
            new BlockPos(-3, 0,  2),
            new BlockPos(-4, 0,  1),
            new BlockPos(-4, 0,  0),
            new BlockPos(-4, 0, -1),
            new BlockPos(-3, 0, -2),
            new BlockPos(-2, 0, -3),
            new BlockPos(-1, 0, -4),
            new BlockPos( 0, 0, -4),
            new BlockPos( 1, 0, -4),
            new BlockPos( 2, 0, -3),
            new BlockPos( 3, 0, -2),
            new BlockPos( 4, 0, -1)
    };

    //own receiver data
    private Map<BlockPos, Boolean> offsetMirrors = new HashMap<>();

    //tile data
    private boolean doesSeeSky = false, hasMultiblock = false;
    private IWeakConstellation channelingType = null;
    private IMinorConstellation channelingTrait = null;
    private CrystalProperties properties = null;
    private BlockPos ritualLinkPos = null;

    //Random other data
    private int ticksExisted = 0;
    private ConstellationEffect effect = null;
    private double collectedStarlight = 0;
    private int fracturationToSync = 0;

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

        if (!this.hasMultiblock || this.channelingType == null || this.properties == null) {
            return;
        }
        if (this.properties.getFracturation() + this.fracturationToSync > 100) {
            return; //Crystal will shatter the next time the tile ticks. No need to run the ritual.
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

        double maxDrain = 14D;
        maxDrain /= CrystalCalculations.getMaxRitualReduction(this.properties);
        maxDrain /= Math.max(1, this.getMirrorCount() - 1);
        collectedStarlight *= properties.getPotency();
        int executeTimes = MathHelper.floor(collectedStarlight / maxDrain);

        int freeCap = MathHelper.floor(CrystalCalculations.getChannelingCapacity(this.properties) * properties.getFracturationLowerBoundaryMultiplier());
        double addFractureChance = CrystalCalculations.getFractureChance(executeTimes, freeCap) * CrystalCalculations.getCstFractureModifier(this.channelingType) * properties.getFracturationRate();
        int part = Math.max(1, executeTimes - freeCap);

        BlockPos to = getLocationPos();
        if (this.ritualLinkPos != null) {
            to = this.ritualLinkPos;
        }

        if (this.effect instanceof ConstellationEffectStatus && collectedStarlight > 0) {
            collectedStarlight = 0;
            if (((ConstellationEffectStatus) this.effect).runEffect(world, to, this.getMirrorCount(), properties, this.channelingTrait)) {
                for (int i = 0; i < part; i++) {
                    if(rand.nextFloat() < (addFractureChance * properties.getEffectAmplifier() / part)) {
                        fractureCrystal();
                    }
                }
                markDirty(world);
            }
            return;
        }

        executeTimes = MathHelper.floor(executeTimes * properties.getEffectAmplifier());
        for (int i = 0; i <= executeTimes; i++) {
            if (collectedStarlight >= maxDrain) {
                collectedStarlight -= maxDrain;
            } else {
                collectedStarlight = 0F;
            }
            if (this.effect.playEffect(world, to, properties, this.channelingTrait)) {
                if (rand.nextFloat() < (addFractureChance * properties.getEffectAmplifier() / part)) {
                    fractureCrystal();
                }
                markDirty(world);
            }
        }
    }

    private void fractureCrystal() {
        this.fracturationToSync++;
    }

    private void collectStarlight(World world) {
        WorldContext ctx = SkyHandler.getContext(world, Dist.DEDICATED_SERVER);
        if (ctx == null) {
            return;
        }

        double collected = 0.25 + (0.75 * DayTimeHelper.getCurrentDaytimeDistribution(world));

        if (this.noiseDistribution == -1) {
            this.noiseDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, this.getLocationPos());
        }

        collected *= 1F + (0.35F * ctx.getDistributionHandler().getDistribution(this.channelingType));
        collected *= 1F + (0.4F * CrystalCalculations.getCollectionAmt(this.properties, noiseDistribution));

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
            trp.setReceiverData(this.effect != null, this.offsetMirrors, this.fracturationToSync);
            return true;
        }
        return false;
    }

    public boolean setPedestalData(TileRitualPedestal trp) {
        if (this.channelingType != trp.getRitualConstellation() ||
                (this.properties != null && trp.getChannelingCrystalProperties() == null) ||
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
        this.properties = trp.getChannelingCrystalProperties();
        this.ritualLinkPos = trp.getRitualLinkTo();

        if (this.channelingType != null && this.properties != null && this.hasMultiblock && this.effect == null) {
            this.effect = ConstellationEffectRegistry.createInstance(this, this.channelingType);
            this.needsTileSync = true;
        }

        if (!this.hasMultiblock || this.effect == null) {
            this.collectedStarlight = 0;
        }

        this.markDirty(trp.getWorld());
        return true;
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

        if (compound.contains("crystal")) {
            this.properties = CrystalProperties.readFromNBT(compound.getCompound("crystal"));
        } else {
            this.properties = null;
        }
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

        if (this.properties != null) {
            compound.put("crystal", this.properties.writeToNBT(new CompoundNBT()));
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
