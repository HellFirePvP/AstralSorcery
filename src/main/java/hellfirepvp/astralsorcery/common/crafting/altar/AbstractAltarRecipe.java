package hellfirepvp.astralsorcery.common.crafting.altar;

import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    private final IRecipe recipe;
    private final ItemStack out;

    private int uniqueRecipeId = -1;

    public AbstractAltarRecipe(TileAltar.AltarLevel neededLevel, AbstractCacheableRecipe recipe) {
        this(neededLevel, recipe.make());
    }

    public AbstractAltarRecipe(TileAltar.AltarLevel neededLevel, IRecipe recipe) {
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

    public ItemStack getOutput(ShapeMap centralGridMap) {
        return out;
    }

    public boolean matches(TileAltar altar) {
        if(altar.getStarlightStored() < getPassiveStarlightRequired()) return false;

        ItemStack[] altarInv = new ItemStack[altar.getSizeInventory()];
        for (int i = 0; i < altar.getSizeInventory(); i++) {
            altarInv[i] = altar.getStackInSlot(i);
        }
        RecipeAdapter adapter = new RecipeAdapter(altar.getCraftingRecipeWidth(), altar.getCraftingRecipeHeight());
        adapter.fill(altarInv);
        return recipe.matches(adapter, altar.getWorld());
    }

    public void setPassiveStarlightRequirement(int starlightRequirement) {
        this.passiveStarlightRequirement = starlightRequirement;
    }

    public int getPassiveStarlightRequired() {
        return passiveStarlightRequirement;
    }

    public void setCraftExperience(int exp) {
        this.experiencePerCraft = exp;
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

    public void onCraftServerFinish(TileAltar altar, Random rand) {}

    public void onCraftServerTick(TileAltar altar, int tick, Random rand) {}

    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, int tick, Random rand) {}

    @SideOnly(Side.CLIENT)
    public void onCraftTESRRender(TileAltar te, double x, double y, double z, float partialTicks) {}

}
