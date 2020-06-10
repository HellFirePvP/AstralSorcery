/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectPillarSparkle
 * Created by HellFirePvP
 * Date: 27.09.2019 / 22:10
 */
public class EffectPillarSparkle extends AltarRecipeEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        if (state == ActiveSimpleAltarRecipe.CraftingState.ACTIVE &&
                rand.nextInt(10) == 0) {
            Vector3 at = new Vector3(altar).add(getRandomPillarOffset(altar.getAltarType()));
            at.addY(rand.nextFloat() * getPillarHeight(altar.getAltarType()));
            at.add(-0.3 + 1.6 * rand.nextFloat(), 0, -0.3 + 1.6 * rand.nextFloat());

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setGravityStrength(-0.01F + rand.nextFloat() * -0.02F)
                    .setScaleMultiplier(0.2F + rand.nextFloat() * 0.4F);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTESR(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state, MatrixStack renderStack, IRenderTypeBuffer buffer, float pTicks, int combinedLight) {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onCraftingFinish(TileAltar altar, boolean isChaining) {}
}
