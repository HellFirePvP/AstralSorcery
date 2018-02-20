/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.infusion;

import hellfirepvp.astralsorcery.common.crafting.IGatedRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidActionResult;
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
    private boolean acceptsChalices = true;

    @Nonnull
    protected ItemStack output = ItemStack.EMPTY;
    @Nonnull
    protected ItemHandle input;

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

    public void setCanBeSupportedByChalices(boolean acceptsChalices) {
        this.acceptsChalices = acceptsChalices;
    }

    public boolean canBeSupportedByChalice() {
        return this.acceptsChalices;
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
        return 200;
    }

    public boolean mayDeleteInput(TileStarlightInfuser infuser) {
        return input.getFluidTypeAndAmount() == null;
    }

    public void handleInputDecrement(TileStarlightInfuser infuser) {
        ItemStack stack = infuser.getInputStack();
        if(!stack.isEmpty()) {
            FluidActionResult fas = ItemUtils.drainFluidFromItem(stack, input.getFluidTypeAndAmount(), true);
            if(fas.isSuccess()) {
                infuser.setStack(fas.getResult());
            }
        }
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    public ItemStack getOutputForRender() {
        return ItemUtils.copyStackWithSize(output, output.getCount());
    }

    @Nonnull
    public ItemStack getOutput(@Nullable TileStarlightInfuser infuser) {
        return ItemUtils.copyStackWithSize(output, output.getCount());
    }

    @Nonnull
    public ItemStack getOutputForMatching() {
        return ItemUtils.copyStackWithSize(output, output.getCount());
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

        return infuser.hasMultiblock() && !infuser.getInputStack().isEmpty() && input.matchCrafting(infuser.getInputStack());
    }
}
