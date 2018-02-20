/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.base.LightOreTransmutations;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseWrapper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmutationRecipeWrapper
 * Created by HellFirePvP
 * Date: 15.02.2017 / 15:57
 */
public class TransmutationRecipeWrapper extends JEIBaseWrapper {

    private LightOreTransmutations.Transmutation transmutation;

    public TransmutationRecipeWrapper(LightOreTransmutations.Transmutation transmutation) {
        this.transmutation = transmutation;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ItemStack in = transmutation.getInputDisplayStack();
        ItemStack out = transmutation.getOutputDisplayStack();

        if(!in.isEmpty() && !out.isEmpty()) {
            ingredients.setInput(ItemStack.class, in);
            ingredients.setOutput(ItemStack.class, out);
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {}

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
