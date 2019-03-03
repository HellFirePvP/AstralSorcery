/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar;

import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.IGatedRecipe;
import hellfirepvp.astralsorcery.common.crafting.INighttimeRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractAltarRecipe
 * Created by HellFirePvP
 * Date: 19.09.2016 / 12:08
 */
public abstract class AbstractAltarRecipe {

    private AbstractAltarRecipe specialEffectRecovery = null;

    private int experiencePerCraft = 5, passiveStarlightRequirement;
    private final TileAltar.AltarLevel neededLevel;
    private final AccessibleRecipe recipe;
    private ItemStack out;

    private int uniqueRecipeId = -1;

    public AbstractAltarRecipe(TileAltar.AltarLevel neededLevel, AccessibleRecipe recipe) {
        this.neededLevel = neededLevel;
        this.recipe = recipe;
        this.out = recipe.getRecipeOutput();
    }

    public final void updateUniqueId(int id) {
        this.uniqueRecipeId = id;
    }

    public final int getUniqueRecipeId() {
        return uniqueRecipeId;
    }

    public final void setSpecialEffectRecovery(AbstractAltarRecipe specialEffectRecovery) {
        this.specialEffectRecovery = specialEffectRecovery;
    }

    @Nullable
    protected ActiveCraftingTask getCurrentTask(TileAltar ta) {
        return ta.getActiveCraftingTask();
    }

    //Output used for rendering purposes. (Shouldn't be empty)
    @Nonnull
    public ItemStack getOutputForRender() {
        return ItemUtils.copyStackWithSize(out, out.getCount());
    }

    public AccessibleRecipe getNativeRecipe() {
        return recipe;
    }

    //Output that's dropped into the world (may be empty)
    @Nonnull
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        return ItemUtils.copyStackWithSize(out, out.getCount());
    }

    //Output that's used to search for the recipe and match/recognize special outputs.
    //PLEASE never return an empty stack as the recipe becomes unrecognisable by the mod!
    @Nonnull
    public ItemStack getOutputForMatching() {
        return ItemUtils.copyStackWithSize(out, out.getCount());
    }

    //Instead of calling this directly, call it via TileAltar.doesRecipeMatch() since that is more sensitive for the altar.
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler, boolean ignoreStarlightRequirement) {
        if(!ignoreStarlightRequirement && !fulfillesStarlightRequirement(altar)) return false;

        if(this instanceof IGatedRecipe) {
            if(altar.getWorld().isRemote) {
                if(!((IGatedRecipe) this).hasProgressionClient()) return false;
            }
        }

        if(this instanceof INighttimeRecipe) {
            if(!ConstellationSkyHandler.getInstance().isNight(altar.getWorld())) return false;
        }

        int slotsContainRecipe = this.getNeededLevel().getAccessibleInventorySize();
        for (int slotId = 0; slotId < invHandler.getSlots(); slotId++) {
            if (slotId < slotsContainRecipe) continue;

            if (!invHandler.getStackInSlot(slotId).isEmpty()) {
                return false; // ItemStacks outside of the required slots for the recipe must be empty.
            }
        }

        ItemStack[] altarInv = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            altarInv[i] = invHandler.getStackInSlot(i);
        }
        RecipeAdapter adapter = new RecipeAdapter(altar.getCraftingRecipeWidth(), altar.getCraftingRecipeHeight());
        adapter.fill(altarInv);
        return recipe.matches(adapter, altar.getWorld());
    }

    public boolean fulfillesStarlightRequirement(TileAltar altar) {
        return altar.getStarlightStored() >= getPassiveStarlightRequired();
    }

    public AbstractAltarRecipe setPassiveStarlightRequirement(int starlightRequirement) {
        this.passiveStarlightRequirement = starlightRequirement;
        return this;
    }

    public int getPassiveStarlightRequired() {
        return passiveStarlightRequirement;
    }

    public AbstractAltarRecipe setCraftExperience(int exp) {
        this.experiencePerCraft = exp;
        return this;
    }

    public boolean allowsForChaining() {
        return true;
    }

    public int getCraftExperience() {
        return experiencePerCraft;
    }

    public float getCraftExperienceMultiplier() {
        return 1F;
    }

    public TileAltar.AltarLevel getNeededLevel() {
        return neededLevel;
    }

    public int craftingTickTime() {
        return 100;
    }

    public void handleInputConsumption(TileAltar ta, ActiveCraftingTask craftingTask, ItemStackHandler inventory) {}

    //Return false and the item in the slot is not consumed.
    public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
        return !requiresSpecialConsumption(recipe.getExpectedStackHandle(slot),
                ta.getInventoryHandler().getStackInSlot(slot.getSlotID()));
    }

    public boolean mayDecrement(TileAltar ta, AttunementRecipe.AttunementAltarSlot slot) {
        if(!(this instanceof AttunementRecipe)) return true;
        AttunementRecipe thisRecipe = (AttunementRecipe) this;
        return !requiresSpecialConsumption(thisRecipe.getAttItemHandle(slot),
                ta.getInventoryHandler().getStackInSlot(slot.getSlotId()));
    }

    public boolean mayDecrement(TileAltar ta, ConstellationRecipe.ConstellationAtlarSlot slot) {
        if(!(this instanceof ConstellationRecipe)) return true;
        ConstellationRecipe thisRecipe = (ConstellationRecipe) this;
        return !requiresSpecialConsumption(thisRecipe.getCstItemHandle(slot),
                ta.getInventoryHandler().getStackInSlot(slot.getSlotId()));
    }

    public boolean mayDecrement(TileAltar ta, TraitRecipe.TraitRecipeSlot slot) {
        if(!(this instanceof TraitRecipe)) return true;
        TraitRecipe thisRecipe = (TraitRecipe) this;
        return !requiresSpecialConsumption(thisRecipe.getInnerTraitItemHandle(slot),
                ta.getInventoryHandler().getStackInSlot(slot.getSlotId()));
    }

    protected boolean requiresSpecialConsumption(ItemHandle handle, ItemStack stack) {
        return handle != null && !stack.isEmpty() &&
                (!ForgeHooks.getContainerItem(stack).isEmpty() ||
                        (handle.handleType == ItemHandle.Type.FLUID && FluidUtil.getFluidContained(stack) != null));
    }

    //Called if the respective method above returns 'false' to allow for proper decrement-handling.
    public void handleItemConsumption(TileAltar ta, ShapedRecipeSlot slot) {
        ItemHandle handle = recipe.getExpectedStackHandle(slot);
        if(handle == null) return;

        consumeAndSetResult(ta.getInventoryHandler(), slot.getSlotID(), handle);
    }

    public void handleItemConsumption(TileAltar ta, AttunementRecipe.AttunementAltarSlot slot) {
        if(!(this instanceof AttunementRecipe)) return;
        AttunementRecipe thisRecipe = (AttunementRecipe) this;
        ItemHandle handle = thisRecipe.getAttItemHandle(slot);
        if(handle == null) return;

        consumeAndSetResult(ta.getInventoryHandler(), slot.getSlotId(), handle);
    }

    public void handleItemConsumption(TileAltar ta, ConstellationRecipe.ConstellationAtlarSlot slot) {
        if(!(this instanceof ConstellationRecipe)) return;
        ConstellationRecipe thisRecipe = (ConstellationRecipe) this;
        ItemHandle handle = thisRecipe.getCstItemHandle(slot);
        if(handle == null) return;

        consumeAndSetResult(ta.getInventoryHandler(), slot.getSlotId(), handle);
    }

    public void handleItemConsumption(TileAltar ta, TraitRecipe.TraitRecipeSlot slot) {
        if(!(this instanceof TraitRecipe)) return;
        TraitRecipe thisRecipe = (TraitRecipe) this;
        ItemHandle handle = thisRecipe.getInnerTraitItemHandle(slot);
        if(handle == null) return;

        consumeAndSetResult(ta.getInventoryHandler(), slot.getSlotId(), handle);
    }

    protected void consumeAndSetResult(IItemHandlerModifiable inv, int slot, ItemHandle handle) {
        ItemStack stack = inv.getStackInSlot(slot);
        if(!stack.isEmpty()) {
            FluidStack fs = FluidUtil.getFluidContained(stack);
            if(fs != null && handle.handleType == ItemHandle.Type.FLUID) {
                FluidActionResult fas = ItemUtils.drainFluidFromItem(stack, handle.getFluidTypeAndAmount(), true);
                if(fas.isSuccess()) {
                    inv.setStackInSlot(slot, fas.getResult());
                }
            } else {
                inv.setStackInSlot(slot, ForgeHooks.getContainerItem(stack));
            }
        }
    }

    protected static ShapedRecipe.Builder shapedRecipe(String basicName, Item out) {
        return ShapedRecipe.Builder.newShapedRecipe("internal/altar/" + basicName, out);
    }

    protected static ShapedRecipe.Builder shapedRecipe(String basicName, Block out) {
        return ShapedRecipe.Builder.newShapedRecipe("internal/altar/" + basicName, out);
    }

    protected static ShapedRecipe.Builder shapedRecipe(String basicName, ItemStack out) {
        return ShapedRecipe.Builder.newShapedRecipe("internal/altar/" + basicName, out);
    }

    //Can be used to applyServer modifications to items on the shapeMap.
    public void applyOutputModificationsServer(TileAltar ta, Random rand) {}

    public void onCraftServerFinish(TileAltar altar, Random rand) {}

    public void onCraftServerTick(TileAltar altar, ActiveCraftingTask.CraftingState state, int tick, int totalCraftingTime, Random rand) {}

    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        if(specialEffectRecovery != null) {
            try {
                specialEffectRecovery.onCraftClientTick(altar, state, tick, rand);
            } catch (Exception ignored) {}
        }
    }

    @SideOnly(Side.CLIENT)
    public void onCraftTESRRender(TileAltar te, double x, double y, double z, float partialTicks) {
        if(specialEffectRecovery != null) {
            try {
                specialEffectRecovery.onCraftTESRRender(te, x, y, z, partialTicks);
            } catch (Exception ignored) {}
        }
    }

}
