/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectLargeDustSwirl
 * Created by HellFirePvP
 * Date: 27.09.2019 / 20:16
 */
public class EffectLargeDustSwirl extends AltarRecipeEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        if (state == ActiveSimpleAltarRecipe.CraftingState.ACTIVE) {
            long tick = getClientTick();
            float interval = 200F;
            float cycle = (float) (((tick % interval) / interval) * 2 * Math.PI);
            int parts = 6;
            for (int i = 0; i < parts; i++) {

                //inner
                float angleSwirl = 110F;
                Vector3 center = new Vector3(altar).add(0.5, 1.1, 0.5);
                Vector3 v = Vector3.RotAxis.X_AXIS.clone();
                float originalAngle = (((float) i) / ((float) parts)) * 360F;
                double angle = originalAngle + (MathHelper.sin(cycle) * angleSwirl);
                v.rotate(Math.toRadians(angle), Vector3.RotAxis.Y_AXIS).normalize().multiply(2.2);
                Vector3 pos = center.clone().add(v);

                Vector3 mot = center.clone().subtract(pos).normalize().multiply(0.09);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .setMotion(mot)
                        .setScaleMultiplier(0.15F + rand.nextFloat() * 0.4F);

                //outer
                angleSwirl = 180F;
                center = new Vector3(altar).add(0.5, 0.1, 0.5);
                v = new Vector3(1, 0, 0);
                originalAngle = (((float) i) / ((float) parts)) * 360F;
                angle = originalAngle + (MathHelper.sin(cycle) * angleSwirl);
                v.rotate(-Math.toRadians(angle), Vector3.RotAxis.Y_AXIS).normalize().multiply(4);
                pos = center.clone().add(v);

                mot = center.clone().subtract(pos).normalize().multiply(0.15);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .setMotion(mot)
                        .setScaleMultiplier(0.27F + rand.nextFloat() * 0.4F)
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
