package hellfirepvp.astralsorcery.common.crafting.infusion;

import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractInfusionRecipe
 * Created by HellFirePvP
 * Date: 11.12.2016 / 17:08
 */
public abstract class AbstractInfusionRecipe {

    private int uniqueRecipeId = -1;
    private float consumptionChance = 0.1F;

    private ItemStack output, input;

    public AbstractInfusionRecipe(ItemStack output, ItemStack input) {
        this.output = output;
        this.input = input;
    }

    public AbstractInfusionRecipe setLiquidStarlightConsumptionChance(float consumptionChance) {
        this.consumptionChance = consumptionChance;
        return this;
    }

    public float getLiquidStarlightConsumptionChance() {
        return consumptionChance;
    }

    public final void updateUniqueId(int id) {
        this.uniqueRecipeId = id;
    }

    public final int getUniqueRecipeId() {
        return uniqueRecipeId;
    }

    public int craftingTickTime() {
        return 500;
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getOutputForRender() {
        return output;
    }

    public ItemStack getOutput(TileStarlightInfuser infuser) {
        return output;
    }

    public ItemStack getInput() {
        return input;
    }

    public void onCraftServerFinish(TileStarlightInfuser infuser, Random rand) {}

    public void onCraftServerTick(TileStarlightInfuser infuser, int tick, Random rand) {}

    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileStarlightInfuser infuser, int tick, Random rand) {}

    public boolean matches(TileStarlightInfuser infuser) {
        return infuser.hasMultiblock() && infuser.getInputStack() != null && ItemUtils.matchStacks(input, infuser.getInputStack());
    }

}
