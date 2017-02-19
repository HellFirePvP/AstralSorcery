/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.infusion;

import hellfirepvp.astralsorcery.common.crafting.IGatedRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
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
 * Class: AbstractInfusionRecipe
 * Created by HellFirePvP
 * Date: 11.12.2016 / 17:08
 */
public abstract class AbstractInfusionRecipe {

    private int uniqueRecipeId = -1;
    private float consumptionChance = 0.1F;
    private boolean consumeMultiple = false;

    @Nonnull
    private ItemStack output;
    @Nonnull
    private ItemHandle input;

    public AbstractInfusionRecipe(@Nonnull ItemStack output, @Nonnull ItemHandle input) {
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

    public void setConsumeMultiple() {
        this.consumeMultiple = true;
    }

    public boolean doesConsumeMultiple() {
        return consumeMultiple;
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

    public boolean mayDeleteInput(TileStarlightInfuser infuser) {
        return input.getFluidTypeAndAmount() == null;
    }

    public void handleInputDecrement(TileStarlightInfuser infuser) {
        ItemStack stack = infuser.getInputStack();
        if(stack != null) {
            ItemUtils.drainFluidFromItem(stack, input.getFluidTypeAndAmount(), true);
        }
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getOutputForRender() {
        return ItemUtils.copyStackWithSize(output, output.stackSize);
    }

    public ItemStack getOutput(@Nullable TileStarlightInfuser infuser) {
        return ItemUtils.copyStackWithSize(output, output.stackSize);
    }

    @Nonnull
    public ItemHandle getInput() {
        return input;
    }

    public void onCraftServerFinish(TileStarlightInfuser infuser, Random rand) {}

    public void onCraftServerTick(TileStarlightInfuser infuser, int tick, Random rand) {}

    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileStarlightInfuser infuser, long tick, Random rand) {}

    public boolean matches(TileStarlightInfuser infuser) {
        if(this instanceof IGatedRecipe) {
            if(infuser.getWorld().isRemote) {
                if(!((IGatedRecipe) this).hasProgressionClient()) return false;
            }
        }

        return infuser.hasMultiblock() && infuser.getInputStack() != null && input.matchCrafting(infuser.getInputStack());
    }
}
