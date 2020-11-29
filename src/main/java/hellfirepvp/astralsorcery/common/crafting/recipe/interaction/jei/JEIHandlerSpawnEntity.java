/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.interaction.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.util.LightmapUtil;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.InteractionResult;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.ResultSpawnEntity;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JEIHandlerSpawnEntity
 * Created by HellFirePvP
 * Date: 31.10.2020 / 14:51
 */
public class JEIHandlerSpawnEntity extends JEIInteractionResultHandler {
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addToRecipeLayout(IRecipeLayout recipeLayout, LiquidInteraction recipe, IIngredients ingredients) {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addToRecipeIngredients(LiquidInteraction recipe, IIngredients ingredients) {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawRecipe(LiquidInteraction recipe, double mouseX, double mouseY) {
        InteractionResult result = recipe.getResult();
        if (!(result instanceof ResultSpawnEntity)) {
            return;
        }
        Entity le = ((ResultSpawnEntity) result).getEntityType().create(Minecraft.getInstance().world);
        if (!(le instanceof LivingEntity)) {
            return;
        }

        MatrixStack renderStack = new MatrixStack();
        renderStack.translate(55, 35, 500);
        renderStack.scale(15, 15, 15);
        renderStack.rotate(Vector3f.XP.rotationDegrees(180));
        renderStack.rotate(Vector3f.YP.rotationDegrees(145));
        IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        Minecraft.getInstance().getRenderManager()
                .renderEntityStatic(le, 0, 0, 0, 0, 0, renderStack, buffer, LightmapUtil.getPackedFullbrightCoords());
        buffer.finish();
    }
}
