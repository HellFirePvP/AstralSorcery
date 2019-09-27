/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.util.sound.PositionedLoopSound;
import hellfirepvp.astralsorcery.common.auxiliary.RecipeHelper;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.item.base.IConstellationFocus;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.network.TileReceiverBase;
import hellfirepvp.astralsorcery.common.tile.network.StarlightReceiverAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import hellfirepvp.astralsorcery.common.util.tile.TileInventoryFiltered;
import hellfirepvp.astralsorcery.common.util.world.SkyCollectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAltar
 * Created by HellFirePvP
 * Date: 12.08.2019 / 19:53
 */
public class TileAltar extends TileReceiverBase<StarlightReceiverAltar> {

    private float posDistribution = -1;

    private AltarType altarType = AltarType.DISCOVERY;
    private ActiveSimpleAltarRecipe activeRecipe = null;
    private TileInventoryFiltered inventory;
    private ItemStack focusItem = ItemStack.EMPTY;

    private int storedStarlight = 0;

    private Object clientCraftSound = null;

    public TileAltar() {
        super(TileEntityTypesAS.ALTAR);
        this.inventory = new TileInventoryFiltered(this, () -> 25);
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isRemote()) {
            this.doesSeeSky();
            this.hasMultiblock();

            this.gatherStarlight();
            this.doCraftingCycle();
        } else {
            if (this.activeRecipe != null) {
                this.doCraftEffects();
                this.doCraftSound();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void doCraftEffects() {
        this.activeRecipe.getRecipeToCraft().getCraftingEffects()
                .forEach(effect -> effect.onTick(this, this.activeRecipe.getState()));
    }

    @OnlyIn(Dist.CLIENT)
    private void doCraftSound() {
        if(Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.MASTER) > 0) {
            if (clientCraftSound == null || ((PositionedLoopSound) clientCraftSound).hasStoppedPlaying()) {
                clientCraftSound = SoundHelper.playSoundLoopClient(SoundsAS.CRAFT_ATTUNEMENT, new Vector3(this), 0.25F, 1F, false,
                        () -> isRemoved() ||
                                Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.MASTER) <= 0 ||
                                this.activeRecipe == null);
            }
        } else {
            clientCraftSound = null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void finishCraftingEffects(PktPlayEffect pkt) {
        ResourceLocation recipeName = ByteBufUtils.readResourceLocation(pkt.getExtraData());
        BlockPos at = ByteBufUtils.readPos(pkt.getExtraData());
        boolean isChaining = pkt.getExtraData().readBoolean();

        World world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }

        TileAltar thisAltar = MiscUtils.getTileAt(world, at, TileAltar.class, false);
        if (thisAltar != null) {
            world.getRecipeManager().getRecipe(recipeName).ifPresent(recipe -> {
                if (recipe instanceof SimpleAltarRecipe) {
                    ((SimpleAltarRecipe) recipe).getCraftingEffects().forEach(effect -> {
                        effect.onCraftingFinish(thisAltar, isChaining);
                    });
                }
            });
        }
    }

    private void doCraftingCycle() {
        if (this.activeRecipe == null) {
            return;
        }

        if (!this.hasMultiblock() || !this.activeRecipe.matches(this)) {
            this.abortCrafting();
            return;
        }
        if (this.activeRecipe.isFinished()) {
            this.finishRecipe();
            return;
        }

        this.activeRecipe.setState(this.activeRecipe.tick(this));
    }

    private void finishRecipe() {
        ForgeHooks.setCraftingPlayer(this.activeRecipe.tryGetCraftingPlayerServer());
        this.activeRecipe.consumeInputs(this);
        ForgeHooks.setCraftingPlayer(null);

        this.activeRecipe.createItemOutputs(this);

        boolean isChaining;
        ResourceLocation recipeName = this.activeRecipe.getRecipeToCraft().getId();

        if (!(isChaining = this.activeRecipe.matches(this))) {
            this.abortCrafting();
        }
        PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.ALTAR_RECIPE_FINISH)
                .addData(buf -> {
                    ByteBufUtils.writeResourceLocation(buf, recipeName);
                    ByteBufUtils.writePos(buf, this.getPos());
                    buf.writeBoolean(isChaining);
                });
        PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(this.getWorld(), this.getPos(), 32));

        markForUpdate();
    }

    private void abortCrafting() {
        this.activeRecipe = null;
        markForUpdate();
    }

    private void gatherStarlight() {
        WorldContext ctx = SkyHandler.getContext(getWorld());
        if (ctx == null || !this.doesSeeSky()) {
            return;
        }

        this.storedStarlight *= 0.95;
        int yLevel = getPos().getY();
        if (yLevel > 40) {
            float collect = 160;

            float dstr;
            if (yLevel > 120) {
                dstr = 1F + ((yLevel - 120) / 272F);
            } else {
                dstr = (yLevel - 20) / 100F;
            }

            if (posDistribution == -1) {
                posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, pos);
            }

            collect *= dstr;
            collect *= (0.75 + (0.25 * posDistribution));
            collect *= DayTimeHelper.getCurrentDaytimeDistribution(getWorld());

            this.storedStarlight = Math.min(getAltarType().getStarlightCapacity(), (int) (this.storedStarlight + collect));
        }
        this.markForUpdate();
    }

    @Nonnull
    public Set<BlockPos> nearbyRelays() {
        Set<BlockPos> eligableRelayOffsets = new HashSet<>();
        for (int xx = -3; xx <= 3; xx++) {
            for (int zz = -3; zz <= 3; zz++) {
                if (xx == 0 && zz == 0) {
                    continue; //Not that it matters though
                }

                BlockPos offset = new BlockPos(xx, 0, zz);
                TileSpectralRelay tar = MiscUtils.getTileAt(getWorld(), getPos().add(offset), TileSpectralRelay.class, true);
                if (tar != null) {
                    eligableRelayOffsets.add(offset);
                }
            }
        }
        return eligableRelayOffsets;
    }

    public void receiveStarlight(double amount) {
        storedStarlight = Math.min(this.getAltarType().getStarlightCapacity(), (int) (storedStarlight + (amount * 80D)));
        markForUpdate();
    }

    public void dropItemOnTop(ItemStack stack) {
        ItemUtils.dropItem(getWorld(),
                getPos().getX() + 0.5,
                getPos().getY() + 1.5,
                getPos().getZ() + 0.5,
                stack);
    }

    @Override
    public void onBreak() {
        super.onBreak();

        if (!getWorld().isRemote() && !getFocusItem().isEmpty()) {
            ItemUtils.dropItemNaturally(getWorld(),
                    getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5,
                    this.focusItem);

            this.focusItem = ItemStack.EMPTY;
        }
    }

    public int getStoredStarlight() {
        return storedStarlight;
    }

    public float getAmbientStarlightPercent() {
        return ((float) getStoredStarlight()) / ((float) getAltarType().getStarlightCapacity());
    }

    public AltarType getAltarType() {
        return altarType;
    }

    @Nullable
    public ActiveSimpleAltarRecipe getActiveRecipe() {
        return activeRecipe;
    }

    @Nonnull
    public ItemStack getFocusItem() {
        return focusItem;
    }

    public void setFocusItem(@Nonnull ItemStack focusItem) {
        this.focusItem = focusItem;
        this.markForUpdate();
    }

    @Nullable
    public IConstellation getFocusedConstellation() {
        ItemStack focus = getFocusItem();
        if (focus.getItem() instanceof IConstellationFocus) {
            return ((IConstellationFocus) focus.getItem()).getFocusConstellation(focus);
        }
        return null;
    }

    @Nullable
    @Override
    protected StructureType getRequiredStructureType() {
        return this.altarType.getRequiredStructure();
    }

    @Nonnull
    public TileInventoryFiltered getInventory() {
        return inventory;
    }

    public <T extends TileAltar> T updateType(AltarType newType, boolean initialPlacement) {
        if (!initialPlacement) {
            this.abortCrafting();
        }

        this.altarType = newType;

        CompoundNBT thisTag = new CompoundNBT();
        this.writeCustomNBT(thisTag);
        this.readCustomNBT(thisTag);
        if (!initialPlacement) {
            this.markForUpdate();

            this.hasMultiblock();
        }
        return (T) this;
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.altarType = AltarType.values()[compound.getInt("altarType")];
        this.inventory = this.inventory.deserialize(compound.getCompound("inventory"));
        this.focusItem = NBTHelper.readFromSubTag(compound, "focusItem", ItemStack::read);

        if (compound.contains("activeRecipe", Constants.NBT.TAG_COMPOUND)) {
            this.activeRecipe = ActiveSimpleAltarRecipe.deserialize(compound.getCompound("activeRecipe"), this.activeRecipe);
        } else {
            this.activeRecipe = null;
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.putInt("altarType", this.altarType.ordinal());
        compound.put("inventory", this.inventory.serialize());
        NBTHelper.setAsSubTag(compound, "focusItem", this.focusItem::write);

        if (this.activeRecipe != null) {
            compound.put("activeRecipe", this.activeRecipe.serialize());
        }
    }

    @Nonnull
    @Override
    public StarlightReceiverAltar provideEndpoint(BlockPos at) {
        return new StarlightReceiverAltar(at);
    }
}
