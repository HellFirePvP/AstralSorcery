/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import hellfirepvp.astralsorcery.client.effect.function.RefreshFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXSpritePlane;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.common.constellation.ConstellationItem;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeTile;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalCalculations;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedCrystalBase;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.TileAreaOfInfluence;
import hellfirepvp.astralsorcery.common.tile.base.network.TileReceiverBase;
import hellfirepvp.astralsorcery.common.tile.network.StarlightReceiverRitualPedestal;
import hellfirepvp.astralsorcery.common.util.EffectIncrementer;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.tile.TileInventoryFiltered;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileRitualPedestal
 * Created by HellFirePvP
 * Date: 09.07.2019 / 19:25
 */
public class TileRitualPedestal extends TileReceiverBase<StarlightReceiverRitualPedestal> implements CrystalAttributeTile, TileAreaOfInfluence {

    public static final BlockPos RITUAL_ANCHOR_OFFEST = new BlockPos(0, 5, 0);
    public static final BlockPos RITUAL_LENS_OFFSET = new BlockPos(0, 2, 0);
    public static final Set<BlockPos> RITUAL_CIRCLE_OFFSETS = ImmutableSet.copyOf(new BlockPos[] {
            new BlockPos(4, 0, 0),
            new BlockPos(4, 0, 1),
            new BlockPos(3, 0, 2),
            new BlockPos(2, 0, 3),
            new BlockPos(1, 0, 4),
            new BlockPos(0, 0, 4),
            new BlockPos(-1, 0, 4),
            new BlockPos(-2, 0, 3),
            new BlockPos(-3, 0, 2),
            new BlockPos(-4, 0, 1),
            new BlockPos(-4, 0, 0),
            new BlockPos(-4, 0, -1),
            new BlockPos(-3, 0, -2),
            new BlockPos(-2, 0, -3),
            new BlockPos(-1, 0, -4),
            new BlockPos(0, 0, -4),
            new BlockPos(1, 0, -4),
            new BlockPos(2, 0, -3),
            new BlockPos(3, 0, -2),
            new BlockPos(4, 0, -1)
    });
    public static final int MAX_MIRROR_COUNT = 5;

    private TileInventoryFiltered inventory;
    private Map<BlockPos, BlockState> offsetConfigurations = new HashMap<>();

    //Own sync data
    private UUID ownerUUID = null;
    private BlockPos ritualLinkTo = null;

    //network rec data
    private boolean working = false;
    private Map<BlockPos, Boolean> offsetMirrors = new HashMap<>();

    //client data
    private EffectIncrementer effectWork = new EffectIncrementer(64);
    private ConstellationEffect clientEffectInstance = null;
    private Object ritualHaloEffect = null;

    public TileRitualPedestal() {
        super(TileEntityTypesAS.RITUAL_PEDESTAL);

        this.inventory = new TileInventoryFiltered(this, () -> 1, Direction.DOWN);
        this.inventory.canExtract((slot, amount, existing) -> !existing.isEmpty());
        this.inventory.canInsert(((slot, toAdd, existing) ->
                existing.isEmpty() && toAdd.getItem() instanceof ItemAttunedCrystalBase &&
                        ((ItemAttunedCrystalBase) toAdd.getItem()).getFocusConstellation(toAdd) != null));
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isRemote()) {
            this.doesSeeSky();
            this.hasMultiblock();

            this.updateLinkTile();
            this.updateBlockConfigurations();
        }

        this.effectWork.update(this.working);

        if (getWorld().isRemote() && this.working) {
            playEffects();
        }
    }

    private void updateBlockConfigurations() {
        if (ticksExisted % 20 == 0) {
            for (BlockPos offset : RITUAL_CIRCLE_OFFSETS) {
                BlockPos pos = getPos().add(offset);
                MiscUtils.executeWithChunk(getWorld(), pos, pos, (at) -> {
                    BlockState savedState = this.offsetConfigurations.get(offset);
                    if (getWorld().isAirBlock(at)) {
                        if (savedState != null) {
                            this.offsetConfigurations.remove(offset);
                            this.markForUpdate();
                        }
                    } else {
                        BlockState actualState = getWorld().getBlockState(at);
                        if (savedState == null || !savedState.equals(actualState)) {
                            this.offsetConfigurations.put(offset, actualState);
                            this.markForUpdate();
                        }
                    }
                });
            }
        }
    }

    private void updateLinkTile() {
        boolean hasLink = ritualLinkTo != null;
        BlockPos link = getPos().add(RITUAL_ANCHOR_OFFEST);
        TileRitualLink linkTile = MiscUtils.getTileAt(world, link, TileRitualLink.class, true);
        boolean hasLinkNow;
        if (linkTile != null) {
            this.ritualLinkTo = linkTile.getLinkedTo();
            hasLinkNow = this.ritualLinkTo != null;
        } else {
            this.ritualLinkTo = null;
            hasLinkNow = false;
        }
        if (hasLink != hasLinkNow) {
            markForUpdate();
        }
    }

    @Nullable
    @Override
    public StructureType getRequiredStructureType() {
        return StructureTypesAS.PTYPE_RITUAL_PEDESTAL;
    }

    @Nonnull
    public Set<BlockState> getConfiguredBlockStates() {
        return Sets.newHashSet(this.offsetConfigurations.values());
    }

    //=========================================================================================
    // AoE highlighting effects
    //=========================================================================================


    @Nullable
    @Override
    public Color getEffectColor() {
        if (!this.providesEffect()) {
            return null;
        }

        IWeakConstellation running = this.getRitualConstellation();
        if (running == null) {
            return null;
        }
        return running.getConstellationColor();
    }

    @Override
    public float getRadius() {
        if (!this.providesEffect()) {
            return 0F;
        }

        IWeakConstellation running = this.getRitualConstellation();
        if (running == null) {
            return 0F;
        }
        ConstellationEffect effect = ConstellationEffectRegistry.createInstance(this, running);
        if (effect == null) {
            return 0F;
        }
        ConstellationEffectProperties properties = effect.createProperties(this.getMirrorCount());
        if (properties != null) {
            if (this.getRitualTrait() != null) {
                properties.modify(this.getRitualTrait());
            }
            if (!this.getCurrentCrystal().isEmpty()) {
                CrystalAttributes attributes = CrystalAttributes.getCrystalAttributes(this.getCurrentCrystal());
                if (attributes != null) {
                    properties.multiplySize(CrystalCalculations.getRitualEffectRangeFactor(this, attributes));
                }
            }
            return (float) properties.getSize() * 1.3F;
        }
        return 0;
    }

    @Nonnull
    @Override
    public BlockPos getEffectOriginPosition() {
        return this.getPos();
    }

    @Nonnull
    @Override
    public Vector3 getEffectPosition() {
        return new Vector3(this.getEffectOriginPosition()).add(0.5F, 0.5F, 0.5F);
    }

    @Nonnull
    @Override
    public DimensionType getDimensionType() {
        return this.getWorld().getDimension().getType();
    }

    @Override
    public boolean providesEffect() {
        return this.isWorking() && !this.isRemoved();
    }

    //=========================================================================================
    // Client effects
    //=========================================================================================

    @OnlyIn(Dist.CLIENT)
    private void playEffects() {
        float alphaDaytime = DayTimeHelper.getCurrentDaytimeDistribution(getWorld());
        alphaDaytime *= 0.8F;

        float percRunning = this.effectWork.getAsPercentage();
        int chance = 15 + (int) ((1F - percRunning) * 50);

        if (rand.nextInt(chance) == 0) {
            Vector3 from = new Vector3(this).add(0.5, 0.05, 0.5);
            MiscUtils.applyRandomOffset(from, rand, 0.05F);

            EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                    .setOwner(this.ownerUUID)
                    .spawn(from)
                    .setup(from.clone().addY(6), 1.5F, 1.5F)
                    .setAlphaMultiplier(0.5F + (0.5F * alphaDaytime))
                    .setMaxAge(64);
        }

        if (this.ritualLinkTo != null) {
            if (rand.nextBoolean()) {
                Vector3 at = new Vector3(this).add(0, 0.1, 0);
                at.add(rand.nextFloat() * 0.5 + 0.25, 0, rand.nextFloat() * 0.5 + 0.25);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .setOwner(this.ownerUUID)
                        .spawn(at)
                        .setAlphaMultiplier(0.7F)
                        .color(VFXColorFunction.WHITE)
                        .setGravityStrength(0.09F)
                        .setScaleMultiplier(0.2F + rand.nextFloat() * 0.15F)
                        .setMaxAge(30 + rand.nextInt(50));
            }
        }

        IWeakConstellation ritualConstellation = getRitualConstellation();
        if (this.working && ritualConstellation != null) {
            if (!this.offsetMirrors.isEmpty() && DayTimeHelper.isNight(getWorld())) {
                if (rand.nextInt(chance * 2) == 0) {
                    Vector3 from = new Vector3(this).add(0.5, 0.1, 0.5);
                    MiscUtils.applyRandomOffset(from, rand, 2F);
                    from.setY(getPos().getY() - 0.6 + 1 * rand.nextFloat() * (rand.nextBoolean() ? 1 : -1));

                    EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                            .setOwner(this.ownerUUID)
                            .spawn(from)
                            .setup(from.clone().addY(5 + rand.nextInt(3)), 1.3F, 1.3F)
                            .setAlphaMultiplier(alphaDaytime)
                            .color(VFXColorFunction.constant(ritualConstellation.getConstellationColor()))
                            .setMaxAge(64);
                }
            }

            if (this.ritualHaloEffect == null) {
                this.ritualHaloEffect = EffectHelper.of(EffectTemplatesAS.TEXTURE_SPRITE)
                        .spawn(new Vector3(this).add(0.5, 0.05, 0.5))
                        .setSprite(SpritesAS.SPR_HALO_RITUAL)
                        .setAxis(Vector3.RotAxis.Y_AXIS)
                        .setNoRotation(25)
                        .setScaleMultiplier(6.5F)
                        .refresh(RefreshFunction.tileExistsAnd(this, (tile, effect) -> tile.isWorking() && !tile.getCurrentCrystal().isEmpty()));
            }

            if (this.ritualHaloEffect != null) {
                FXSpritePlane effectPlane = ((FXSpritePlane) this.ritualHaloEffect);
                EffectHelper.refresh(effectPlane, EffectTemplatesAS.TEXTURE_SPRITE);

                float dayTimeMul = DayTimeHelper.getCurrentDaytimeDistribution(this.getWorld());
                effectPlane.setAlphaMultiplier(Math.max(0.05F, dayTimeMul * 0.75F));
            }

            Vector3 offset = Vector3.random().setY(0).normalize().multiply(rand.nextFloat() * 4 * (rand.nextBoolean() ? 1 : -1));
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .setOwner(this.ownerUUID)
                    .spawn(new Vector3(this).add(0.5, 0.02, 0.5).add(offset))
                    .setAlphaMultiplier(1F)
                    .setGravityStrength(-0.001F)
                    .color(VFXColorFunction.constant(ritualConstellation.getConstellationColor()))
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setScaleMultiplier(0.3F + rand.nextFloat() * 0.15F)
                    .setMaxAge(25 + rand.nextInt(15));

            if (this.clientEffectInstance != null && !this.clientEffectInstance.getConstellation().equals(ritualConstellation)) {
                this.clientEffectInstance = null;
            }
            if (this.clientEffectInstance == null) {
                this.clientEffectInstance = ConstellationEffectRegistry.createInstance(ILocatable.fromPos(getPos()), ritualConstellation);
            }
            if (this.clientEffectInstance != null) {
                clientEffectInstance.playClientEffect(getWorld(), getPos(), this, percRunning, this.isFullyEnhanced());
                if (this.ritualLinkTo != null && getWorld().isBlockPresent(this.ritualLinkTo)) {
                    clientEffectInstance.playClientEffect(getWorld(), this.ritualLinkTo, this, percRunning, this.isFullyEnhanced());
                }
            }

            CrystalAttributes prop = this.getAttributes();
            if (prop != null && rand.nextInt(4) == 0) {
                for (int i = 0; i < 3; i++) {
                    Vector3 at = new Vector3(this)
                            .add(0.5, 1.35, 0.5)
                            .add(Vector3.random().multiply(0.6F));
                    Vector3 motion = Vector3.random().multiply(0.02F);

                    EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .setOwner(this.ownerUUID)
                            .spawn(at)
                            .setMotion(motion)
                            .setAlphaMultiplier(1F)
                            .color(VFXColorFunction.constant(ritualConstellation.getConstellationColor()))
                            .alpha(VFXAlphaFunction.FADE_OUT)
                            .setScaleMultiplier(0.15F + rand.nextFloat() * 0.05F)
                            .setMaxAge(16 + rand.nextInt(15));
                }

                if (rand.nextInt(3) == 0) {
                    Vector3 from = new Vector3(this).add(0.5, 1.2, 0.5);
                    Vector3 to;
                    if (this.offsetMirrors.isEmpty()) {
                        to = new Vector3(this).add(0.5, 3.5 + rand.nextFloat() * 2.5, 0.5);
                    } else {
                        BlockPos mirror = MiscUtils.getRandomEntry(this.getMirrors().keySet(), rand);
                        to = new Vector3(mirror).add(0.5, 0.5, 0.5);
                    }

                    EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                            .setOwner(this.ownerUUID)
                            .spawn(from)
                            .makeDefault(to)
                            .color(VFXColorFunction.constant(ritualConstellation.getConstellationColor()));
                }
            }
        }

        for (BlockPos mirror : this.offsetMirrors.keySet()) {
            if (ticksExisted % 32 == 0) {
                Vector3 source = new Vector3(this).add(0.5, 0.9, 0.5);
                Vector3 to = new Vector3(this).add(mirror).add(0.5, 0.5, 0.5);

                EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                        .setOwner(this.ownerUUID)
                        .spawn(source)
                        .setup(to, 0.8F, 0.8F);

                if (this.ritualLinkTo != null) {
                    source = new Vector3(this).add(RITUAL_ANCHOR_OFFEST).add(0.5, 0.5, 0.5);

                    EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                            .setOwner(this.ownerUUID)
                            .spawn(source)
                            .setup(to, 0.8F, 0.8F)
                            .color(VFXColorFunction.random());
                }
            }
        }
    }

    //=========================================================================================
    // Getters
    //=========================================================================================

    public boolean isWorking() {
        return this.working;
    }

    public Map<BlockPos, Boolean> getMirrors() {
        return MapStream.of(this.offsetMirrors)
                .mapKey(pos -> pos.add(this.getPos()))
                .toMap();
    }

    public int getMirrorCount() {
        return (int) this.offsetMirrors.values().stream()
                .filter(b -> b)
                .count();
    }

    public boolean isFullyEnhanced() {
        return this.working && this.offsetMirrors.size() == MAX_MIRROR_COUNT;
    }

    @Nullable
    public PlayerEntity getOwner() {
        if (this.ownerUUID == null || this.world == null) {
            return null;
        }
        return this.world.getPlayerByUuid(this.ownerUUID);
    }

    @Nonnull
    public ItemStack getCurrentCrystal() {
        ItemStack crystal = this.inventory.getStackInSlot(0);
        return ItemUtils.copyStackWithSize(crystal, crystal.getCount());
    }

    @Nullable
    public BlockPos getRitualLinkTo() {
        return this.ritualLinkTo;
    }

    @Nonnull
    public EffectIncrementer getWorkEffectTimer() {
        return this.effectWork;
    }

    @Nullable
    public IWeakConstellation getRitualConstellation() {
        ItemStack crystal = this.inventory.getStackInSlot(0);
        if (!crystal.isEmpty() && crystal.getItem() instanceof ConstellationItem) {
            return ((ConstellationItem) crystal.getItem()).getAttunedConstellation(crystal);
        }
        return null;
    }

    @Nullable
    public IMinorConstellation getRitualTrait() {
        ItemStack crystal = this.inventory.getStackInSlot(0);
        if (!crystal.isEmpty() && crystal.getItem() instanceof ConstellationItem) {
            return ((ConstellationItem) crystal.getItem()).getTraitConstellation(crystal);
        }
        return null;
    }

    @Nullable
    @Override
    public CrystalAttributes getAttributes() {
        ItemStack crystal = this.inventory.getStackInSlot(0);
        if (!crystal.isEmpty() && crystal.getItem() instanceof CrystalAttributeItem) {
            return ((CrystalAttributeItem) crystal.getItem()).getAttributes(crystal);
        }
        return null;
    }

    @Override
    public void setAttributes(@Nullable CrystalAttributes attributes) {
        ItemStack crystal = this.inventory.getStackInSlot(0);
        if (!crystal.isEmpty() && crystal.getItem() instanceof CrystalAttributeItem) {
            ((CrystalAttributeItem) crystal.getItem()).setAttributes(crystal, attributes);
        }
    }

    //=========================================================================================
    // Modifications
    //=========================================================================================

    public void setOwner(@Nullable UUID playerUUID) {
        this.ownerUUID = playerUUID;
        this.markForUpdate();
    }

    //Returns the ItemStack to be returned to the player.
    //Inventory change automatically marks this tile for update
    @Nonnull
    public ItemStack tryPlaceCrystalInPedestal(@Nonnull ItemStack crystal) {
        ItemStack currentCatalyst = this.inventory.getStackInSlot(0);
        ItemStack toInsert = ItemUtils.copyStackWithSize(crystal, Math.min(crystal.getCount(), 1));

        if (toInsert.isEmpty()) {
            if (!this.inventory.canExtractItem(0, 1)) {
                return ItemStack.EMPTY;
            }

            if (currentCatalyst.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                this.inventory.setStackInSlot(0, ItemStack.EMPTY);
                return currentCatalyst;
            }
        } else {
            if (!this.inventory.canInsertItem(0, crystal)) {
                return crystal;
            }

            if (currentCatalyst.isEmpty()) {
                this.inventory.setStackInSlot(0, toInsert);
                return ItemUtils.copyStackWithSize(crystal, Math.max(0, crystal.getCount() - 1));
            } else {
                return crystal;
            }
        }
    }

    //Stuff sent over from StarlightReceiverRitualPedestal
    public void setReceiverData(boolean working, Map<BlockPos, Boolean> mirrorData, @Nullable CrystalAttributes newAttributes, List<CrystalAttributes> fracturedCrystalStats) {
        boolean needsReSync = false;

        this.working = working;
        this.offsetMirrors = new HashMap<>(mirrorData);

        ItemStack crystal = this.getCurrentCrystal();
        if (!crystal.isEmpty() && crystal.getItem() instanceof CrystalAttributeItem) {
            for (CrystalAttributes attr : fracturedCrystalStats) {
                ItemStack newCrystal = new ItemStack(crystal.getItem(), 1);
                ((CrystalAttributeItem) newCrystal.getItem()).setAttributes(newCrystal, attr);
                ItemUtils.dropItemNaturally(this.getWorld(), this.getPos().getX() + 0.5, this.getPos().getY() + 0.8, this.getPos().getZ() + 0.5, newCrystal);
            }

            if (newAttributes == null) {
                this.tryPlaceCrystalInPedestal(ItemStack.EMPTY);
            } else {
                this.setAttributes(newAttributes.copy());
            }
        }

        this.markForUpdate();

        if (!needsReSync) {
            this.preventNetworkSync();
        }
    }

    //=========================================================================================
    // Misc and I/O
    //=========================================================================================

    @Nonnull
    @Override
    public StarlightReceiverRitualPedestal provideEndpoint(BlockPos at) {
        return new StarlightReceiverRitualPedestal(at);
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.inventory = this.inventory.deserialize(compound.getCompound("inventory"));
        if (compound.hasUniqueId("ownerUUID")) {
            this.ownerUUID = compound.getUniqueId("ownerUUID");
        } else {
            this.ownerUUID = null;
        }
        this.ritualLinkTo = NBTHelper.readFromSubTag(compound, "ritualLinkTo", NBTHelper::readBlockPosFromNBT);
        this.working = compound.getBoolean("working");

        this.offsetMirrors.clear();
        ListNBT tagList = compound.getList("mirrors", Constants.NBT.TAG_COMPOUND);
        for (INBT nbt : tagList) {
            CompoundNBT tag = (CompoundNBT) nbt;
            this.offsetMirrors.put(NBTHelper.readBlockPosFromNBT(tag), tag.getBoolean("connect"));
        }

        this.offsetConfigurations.clear();
        ListNBT tagBlocks = compound.getList("blockConfiguration", Constants.NBT.TAG_COMPOUND);
        for (INBT nbt : tagBlocks) {
            CompoundNBT tag = (CompoundNBT) nbt;
            this.offsetConfigurations.put(NBTHelper.readBlockPosFromNBT(tag), NBTHelper.getBlockState(tag, "state"));
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.put("inventory", this.inventory.serialize());
        if (this.ownerUUID != null) {
            compound.putUniqueId("ownerUUID", this.ownerUUID);
        }
        if (this.ritualLinkTo != null) {
            NBTHelper.setAsSubTag(compound, "ritualLinkTo", cmp -> NBTHelper.writeBlockPosToNBT(this.ritualLinkTo, cmp));
        }
        compound.putBoolean("working", this.working);

        ListNBT listPositions = new ListNBT();
        for (Map.Entry<BlockPos, Boolean> posEntry : this.offsetMirrors.entrySet()) {
            CompoundNBT cmp = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(posEntry.getKey(), cmp);
            cmp.putBoolean("connect", posEntry.getValue());
            listPositions.add(cmp);
        }
        compound.put("mirrors", listPositions);

        ListNBT listConfigurations = new ListNBT();
        for (Map.Entry<BlockPos, BlockState> posEntry : this.offsetConfigurations.entrySet()) {
            CompoundNBT cmp = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(posEntry.getKey(), cmp);
            NBTHelper.setBlockState(cmp, "state", posEntry.getValue());
            listConfigurations.add(cmp);
        }
        compound.put("blockConfiguration", listConfigurations);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.inventory.hasCapability(cap, side)) {
            return this.inventory.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }
}
