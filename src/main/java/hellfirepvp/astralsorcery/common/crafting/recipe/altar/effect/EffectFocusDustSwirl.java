/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectFocusDustSwirl
 * Created by HellFirePvP
 * Date: 27.09.2019 / 20:41
 */
public class EffectFocusDustSwirl extends AltarRecipeEffect implements IFocusEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        if (state == ActiveSimpleAltarRecipe.CraftingState.ACTIVE) {
            ActiveSimpleAltarRecipe recipe = altar.getActiveRecipe();
            if (recipe == null) {
                return; //Uh... what
            }
            IConstellation focus = recipe.getRecipeToCraft().getFocusConstellation();

            long tick = getClientTick();
            float total = 180;
            float percCycle = (float) (((tick % total) / total) * 2 * Math.PI);
            int parts = 5;
            Vector3 center = new Vector3(altar).add(0.5, 0.1, 0.5);
            float angleSwirl = 70F;
            float dst = 3.5F;

            for (int i = 0; i < parts; i++) {
                Vector3 v = Vector3.RotAxis.X_AXIS.clone();
                float originalAngle = (((float) i) / ((float) parts)) * 360F;
                double angle = originalAngle + (MathHelper.sin(percCycle) * angleSwirl);
                v.rotate(-Math.toRadians(angle), Vector3.RotAxis.Y_AXIS).normalize().multiply(dst);
                Vector3 pos = center.clone().add(v);
                Vector3 mot = center.clone().subtract(pos).normalize().multiply(0.07);

                Color c = getFocusColor(focus, rand);
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .setScaleMultiplier(0.25F + rand.nextFloat() * 0.7F)
                        .setMotion(mot)
                        .color(VFXColorFunction.constant(c))
                        .setMaxAge(50);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTESR(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state, MatrixStack renderStack, IRenderTypeBuffer buffer, float pTicks, int combinedLight) {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onCraftingFinish(TileAltar altar, boolean isChaining) {}
}
