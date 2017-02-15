/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar;

import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.IGatedRecipe;
import hellfirepvp.astralsorcery.common.crafting.INighttimeRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    private int experiencePerCraft = 5, passiveStarlightRequirement;
    private final TileAltar.AltarLevel neededLevel;
    private final IAccessibleRecipe recipe;
    private final ItemStack out;

    private int uniqueRecipeId = -1;

    public AbstractAltarRecipe(TileAltar.AltarLevel neededLevel, AbstractCacheableRecipe recipe) {
        this(neededLevel, recipe.make());
    }

    public AbstractAltarRecipe(TileAltar.AltarLevel neededLevel, IAccessibleRecipe recipe) {
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

    @Nonnull
    public ItemStack getOutputForRender() {
        return ItemUtils.copyStackWithSize(out, out.stackSize);
    }

    public IAccessibleRecipe getNativeRecipe() {
        return recipe;
    }

    @Nullable
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        return ItemUtils.copyStackWithSize(out, out.stackSize);
    }

    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler, boolean ignoreStarlightRequirement) {
        if(!ignoreStarlightRequirement && altar.getStarlightStored() < getPassiveStarlightRequired()) return false;

        if(this instanceof IGatedRecipe) {
            if(altar.getWorld().isRemote) {
                if(!((IGatedRecipe) this).hasProgressionClient()) return false;
            }
        }

        if(this instanceof INighttimeRecipe) {
            if(!ConstellationSkyHandler.getInstance().isNight(altar.getWorld())) return false;
        }

        ItemStack[] altarInv = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            altarInv[i] = invHandler.getStackInSlot(i);
        }
        RecipeAdapter adapter = new RecipeAdapter(altar.getCraftingRecipeWidth(), altar.getCraftingRecipeHeight());
        adapter.fill(altarInv);
        return recipe.matches(adapter, altar.getWorld());
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

    //Return false and the item in the slot is not consumed.
    public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
        ItemHandle handle = recipe.getExpectedStackHandle(slot);
        return handle == null || handle.getFluidTypeAndAmount() == null;
    }

    public boolean mayDecrement(TileAltar ta, AttunementRecipe.AltarSlot slot) {
        if(!(this instanceof AttunementRecipe)) return true;
        AttunementRecipe thisRecipe = (AttunementRecipe) this;
        ItemHandle handle = thisRecipe.getAttItemHandle(slot);
        return handle == null || handle.getFluidTypeAndAmount() == null;
    }

    public boolean mayDecrement(TileAltar ta, ConstellationRecipe.AltarAdditionalSlot slot) {
        if(!(this instanceof ConstellationRecipe)) return true;
        ConstellationRecipe thisRecipe = (ConstellationRecipe) this;
        ItemHandle handle = thisRecipe.getCstItemHandle(slot);
        return handle == null || handle.getFluidTypeAndAmount() == null;
    }

    //Called if the respective method above returns 'false' to allow for proper decrement-handling.
    public void handleItemConsumption(TileAltar ta, ShapedRecipeSlot slot) {
        ItemHandle handle = recipe.getExpectedStackHandle(slot);
        if(handle == null) return;

        TileReceiverBaseInventory.ItemHandlerTile inventory = ta.getInventoryHandler();
        ItemStack stack = inventory.getStackInSlot(slot.getSlotID());
        if(stack != null) {
            ItemUtils.drainFluidFromItem(stack, handle.getFluidTypeAndAmount(), true);
        }
    }

    public void handleItemConsumption(TileAltar ta, AttunementRecipe.AltarSlot slot) {
        if(!(this instanceof AttunementRecipe)) return;
        AttunementRecipe thisRecipe = (AttunementRecipe) this;
        ItemHandle handle = thisRecipe.getAttItemHandle(slot);
        if(handle == null) return;

        TileReceiverBaseInventory.ItemHandlerTile inventory = ta.getInventoryHandler();
        ItemStack stack = inventory.getStackInSlot(slot.slotId);
        if(stack != null) {
            ItemUtils.drainFluidFromItem(stack, handle.getFluidTypeAndAmount(), true);
        }
    }

    public void handleItemConsumption(TileAltar ta, ConstellationRecipe.AltarAdditionalSlot slot) {
        if(!(this instanceof ConstellationRecipe)) return;
        ConstellationRecipe thisRecipe = (ConstellationRecipe) this;
        ItemHandle handle = thisRecipe.getCstItemHandle(slot);
        if(handle == null) return;

        TileReceiverBaseInventory.ItemHandlerTile inventory = ta.getInventoryHandler();
        ItemStack stack = inventory.getStackInSlot(slot.getSlotId());
        if(stack != null) {
            ItemUtils.drainFluidFromItem(stack, handle.getFluidTypeAndAmount(), true);
        }
    }

    //Can be used to apply modifications to items on the shapeMap.
    public void applyOutputModificationsServer(TileAltar ta, Random rand) {}

    public void onCraftServerFinish(TileAltar altar, Random rand) {}

    public void onCraftServerTick(TileAltar altar, int tick, Random rand) {}

    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, long tick, Random rand) {}

    @SideOnly(Side.CLIENT)
    public void onCraftTESRRender(TileAltar te, double x, double y, double z, float partialTicks) {}

}
