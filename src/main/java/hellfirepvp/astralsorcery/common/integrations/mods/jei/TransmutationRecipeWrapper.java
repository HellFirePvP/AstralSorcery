/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.base.LightOreTransmutations;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmutationRecipeWrapper
 * Created by HellFirePvP
 * Date: 15.02.2017 / 15:57
 */
public class TransmutationRecipeWrapper implements IRecipeWrapper {

    private LightOreTransmutations.Transmutation transmutation;

    public TransmutationRecipeWrapper(LightOreTransmutations.Transmutation transmutation) {
        this.transmutation = transmutation;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ItemStack in = ItemUtils.createBlockStack(transmutation.input);
        ItemStack out = ItemUtils.createBlockStack(transmutation.output);

        if(in != null && out != null) {
            ingredients.setInput(ItemStack.class, in);
            ingredients.setOutput(ItemStack.class, out);
        }
    }

    @Override
    @Deprecated
    public List getInputs() {
        return Lists.newArrayList();
    }

    @Override
    @Deprecated
    public List getOutputs() {
        return Lists.newArrayList();
    }

    @Override
    @Deprecated
    public List<FluidStack> getFluidInputs() {
        return Lists.newArrayList();
    }

    @Override
    @Deprecated
    public List<FluidStack> getFluidOutputs() {
        return Lists.newArrayList();
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {}

    @Override
    public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {}

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Lists.newArrayList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
