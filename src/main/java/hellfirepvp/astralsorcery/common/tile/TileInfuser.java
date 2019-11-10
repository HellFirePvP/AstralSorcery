/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.google.common.collect.ImmutableSet;
import hellfirepvp.astralsorcery.client.util.sound.PositionedLoopSound;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusionContext;
import hellfirepvp.astralsorcery.common.crafting.recipe.infusion.ActiveLiquidInfusionRecipe;
import hellfirepvp.astralsorcery.common.item.wand.WandInteractable;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.sound.CategorizedSoundEvent;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileInfuser
 * Created by HellFirePvP
 * Date: 09.11.2019 / 11:18
 */
public class TileInfuser extends TileEntityTick implements WandInteractable {

    private static final Set<BlockPos> LIQUID_OFFSETS = ImmutableSet.of(
            new BlockPos( 1, -1,  2),
            new BlockPos( 0, -1,  2),
            new BlockPos(-1, -1,  2),

            new BlockPos( 1, -1, -2),
            new BlockPos( 0, -1, -2),
            new BlockPos(-1, -1, -2),

            new BlockPos( 2, -1,  1),
            new BlockPos( 2, -1,  0),
            new BlockPos( 2, -1, -1),

            new BlockPos(-2, -1,  1),
            new BlockPos(-2, -1,  0),
            new BlockPos(-2, -1, -1)
    );

    private TileInventory inventory;

    private ActiveLiquidInfusionRecipe activeRecipe = null;

    private Object clientCraftSound = null;

    public TileInfuser() {
        super(TileEntityTypesAS.INFUSER);
        this.inventory = new TileInventory(this, () -> 1);
    }

    @Nullable
    @Override
    public StructureType getRequiredStructureType() {
        return StructureTypesAS.PTYPE_INFUSER;
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isRemote()) {
            this.doCraftingCycle();
        } else {
            if (getActiveRecipe() != null) {
                this.doCraftSound();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void doCraftSound() {
        if (SoundHelper.getSoundVolume(SoundCategory.BLOCKS) > 0) {
            if (clientCraftSound == null || ((PositionedLoopSound) clientCraftSound).hasStoppedPlaying()) {
                CategorizedSoundEvent sound = SoundsAS.INFUSER_CRAFT_LOOP;

                clientCraftSound = SoundHelper.playSoundLoopFadeInClient(sound,
                        new Vector3(this).add(0.5, 0.5, 0.5),
                        1F,
                        1F,
                        false,
                        () -> isRemoved() ||
                                Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.BLOCKS) <= 0 ||
                                this.getActiveRecipe() == null)
                        .setFadeInTicks(30)
                        .setFadeOutTicks(20);
            }
        } else {
            clientCraftSound = null;
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

        this.activeRecipe.tick();
        this.markForUpdate();
    }

    private void finishRecipe() {
        ForgeHooks.setCraftingPlayer(this.activeRecipe.tryGetCraftingPlayerServer());
        this.activeRecipe.createItemOutputs(this, this::dropItemOnTop);
        this.activeRecipe.consumeInputs(this);
        ForgeHooks.setCraftingPlayer(null);
        this.abortCrafting();

        SoundHelper.playSoundAround(SoundsAS.INFUSER_CRAFT_FINISH, this.getWorld(), this.getPos(), 1F, 1F);
    }

    private void abortCrafting() {
        this.activeRecipe = null;
        markForUpdate();
    }

    protected LiquidInfusion findRecipe(PlayerEntity crafter) {
        return RecipeTypesAS.TYPE_INFUSION.findRecipe(new LiquidInfusionContext(this, crafter, LogicalSide.SERVER));
    }

    protected void startCrafting(LiquidInfusion recipe, PlayerEntity crafter) {
        if (this.getActiveRecipe() != null) {
            return;
        }

        this.activeRecipe = new ActiveLiquidInfusionRecipe(getWorld(), getPos(), recipe, crafter.getUniqueID());
        markForUpdate();

        SoundHelper.playSoundAround(SoundsAS.INFUSER_CRAFT_START, SoundCategory.BLOCKS, this.world, new Vector3(this).add(0.5, 0.5, 0.5), 1F, 1F);
    }

    @Override
    public void onInteract(World world, BlockPos pos, PlayerEntity player, Direction side, boolean sneak) {
        if (!world.isRemote() && this.hasMultiblock() && !this.getItemInput().isEmpty()) {
            if (this.getActiveRecipe() != null) {
                if (this.getActiveRecipe().matches(this)) {
                    return;
                }

                abortCrafting();
            }
            LiquidInfusion recipe = this.findRecipe(player);
            if (recipe != null) {
                this.startCrafting(recipe, player);
            }
        }
    }

    @Nonnull
    public ItemStack getItemInput() {
        return this.inventory.getStackInSlot(0);
    }

    @Nonnull
    public ItemStack setItemInput(@Nonnull ItemStack stack) {
        ItemStack prev = this.getItemInput();
        this.inventory.setStackInSlot(0, stack);
        return prev;
    }

    @Nullable
    public ActiveLiquidInfusionRecipe getActiveRecipe() {
        return activeRecipe;
    }

    @Nonnull
    public static Set<BlockPos> getLiquidOffsets() {
        return LIQUID_OFFSETS;
    }

    @Nonnull
    public Map<BlockPos, Fluid> getLiquids() {
        return MapStream.ofKeys(getLiquidOffsets(), pos -> getWorld().getFluidState(getPos().add(pos)).getFluid())
                .toMap();
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.inventory = this.inventory.deserialize(compound.getCompound("inventory"));

        if (compound.contains("activeRecipe", Constants.NBT.TAG_COMPOUND)) {
            this.activeRecipe = ActiveLiquidInfusionRecipe.deserialize(compound.getCompound("activeRecipe"));
        } else {
            this.activeRecipe = null;
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.put("inventory", this.inventory.serialize());

        if (this.activeRecipe != null) {
            compound.put("activeRecipe", this.activeRecipe.serialize());
        }
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
