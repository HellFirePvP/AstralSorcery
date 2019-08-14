/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedCrystalBase;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.network.TileReceiverBase;
import hellfirepvp.astralsorcery.common.tile.network.StarlightReceiverRitualPedestal;
import hellfirepvp.astralsorcery.common.util.EffectIncrementer;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalPropertyItem;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import hellfirepvp.astralsorcery.common.util.tile.TileInventoryFiltered;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileRitualPedestal
 * Created by HellFirePvP
 * Date: 09.07.2019 / 19:25
 */
public class TileRitualPedestal extends TileReceiverBase<StarlightReceiverRitualPedestal> {

    public static final int MAX_MIRROR_COUNT = 5;

    private TileInventoryFiltered inventory;

    //Own sync data
    private UUID ownerUUID = null;
    private BlockPos ritualLinkTo = null;

    //network rec data
    private boolean working = false;
    private Map<BlockPos, Boolean> offsetMirrors = new HashMap<>();

    //client data
    private EffectIncrementer effectWork = new EffectIncrementer(64);

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
        }

        this.effectWork.update(this.working);

        if (getWorld().isRemote() && this.working) {
            playEffects();
        }
    }

    private void updateLinkTile() {
        boolean hasLink = ritualLinkTo != null;
        BlockPos link = getPos().add(0, 5, 0);
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
    protected StructureType getRequiredStructureType() {
        return StructureTypesAS.PTYPE_RITUAL_PEDESTAL;
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

            ConstellationEffect clientEffect = ConstellationEffectRegistry.getClientEffect(ritualConstellation);
            if (clientEffect != null) {
                clientEffect.playClientEffect(getWorld(), getPos(), this, percRunning, this.isFullyEnhanced());
                if (this.ritualLinkTo != null) {
                    clientEffect.playClientEffect(getWorld(), this.ritualLinkTo, this, percRunning, this.isFullyEnhanced());
                }
            }

            CrystalProperties prop = this.getChannelingCrystalProperties();
            if (prop != null && prop.getFracturation() > 0 && rand.nextFloat() < (prop.getFracturation() / 100F)) {
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
                    Vector3 from = new Vector3(this).add(0.5, 1.25, 0.5);
                    Vector3 to;
                    if (this.offsetMirrors.isEmpty()) {
                        to = new Vector3(this).add(0.5, 3.5 + rand.nextFloat() * 2.5, 0.5);
                    } else {
                        BlockPos mirror = MiscUtils.getRandomEntry(this.getMirrors().keySet(), rand);
                        to = new Vector3(this).add(mirror).add(0.5, 0.5, 0.5);
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
                Vector3 source = new Vector3(this).add(0.5, 0.75, 0.5);
                Vector3 to = new Vector3(this).add(mirror).add(0.5, 0.5, 0.5);

                EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                        .setOwner(this.ownerUUID)
                        .spawn(source)
                        .setup(to, 0.8F, 0.8F);

                if (this.ritualLinkTo != null) {
                    source = new Vector3(this).add(0.5, 5.5, 0.5);

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
        return this.offsetMirrors.entrySet().stream()
                .map(e -> new Tuple<>(e.getKey().add(this.getPos()), e.getValue()))
                .collect(Collectors.toMap(tpl -> tpl.getA(), tpl -> tpl.getB()));
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
        if (!crystal.isEmpty() && crystal.getItem() instanceof ItemAttunedCrystalBase) {
            return ItemAttunedCrystalBase.getMainConstellation(crystal);
        }
        return null;
    }

    @Nullable
    public IMinorConstellation getRitualTrait() {
        ItemStack crystal = this.inventory.getStackInSlot(0);
        if (!crystal.isEmpty() && crystal.getItem() instanceof ItemAttunedCrystalBase) {
            return ItemAttunedCrystalBase.getTrait(crystal);
        }
        return null;
    }

    @Nullable
    public CrystalProperties getChannelingCrystalProperties() {
        ItemStack crystal = this.inventory.getStackInSlot(0);
        if (!crystal.isEmpty() && crystal.getItem() instanceof CrystalPropertyItem) {
            return ((CrystalPropertyItem) crystal.getItem()).getProperties(crystal);
        }
        return null;
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
    public void setReceiverData(boolean working, Map<BlockPos, Boolean> mirrorData, int fractureCount) {
        boolean needsReSync = false;

        this.working = working;
        this.offsetMirrors = new HashMap<>(mirrorData);

        CrystalProperties prop = this.getChannelingCrystalProperties();
        if (prop != null && fractureCount > 0) {
            prop = new CrystalProperties(prop.getSize(), prop.getPurity(),
                    prop.getCollectiveCapability(), prop.getFracturation() + fractureCount, prop.getSizeOverride());

            if (prop.getFracturation() >= 100) {
                SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, getWorld(), getPos(), 7.5F, 1.4F);
                //TODO shatter effect

                this.inventory.setStackInSlot(0, ItemStack.EMPTY);
            } else {
                this.setChannelingCrystalProperties(prop);
            }

            needsReSync = true;
        }

        this.markForUpdate();

        if (!needsReSync) {
            this.preventNetworkSync();
        }
    }

    private void setChannelingCrystalProperties(CrystalProperties prop) {
        ItemStack crystal = this.inventory.getStackInSlot(0);
        if (!crystal.isEmpty() && crystal.getItem() instanceof CrystalPropertyItem) {
            ((CrystalPropertyItem) crystal.getItem()).applyCrystalProperties(crystal, prop);
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
