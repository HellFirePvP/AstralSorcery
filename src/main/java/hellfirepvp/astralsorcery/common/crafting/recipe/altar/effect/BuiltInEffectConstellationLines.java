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
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BuiltInEffectConstellationLines
 * Created by HellFirePvP
 * Date: 24.09.2019 / 18:02
 */
public class BuiltInEffectConstellationLines extends AltarRecipeEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        if (state == ActiveSimpleAltarRecipe.CraftingState.ACTIVE) {
            Vector3 thisAltar = new Vector3(altar).clone().add(0.5, 0.5, 0.5);
            for (int i = 0; i < 4; i++) {
                Vector3 at = getRandomPillarOffset(altar.getAltarType()).clone().addY(getPillarHeight(altar.getAltarType()));
                at.multiply(rand.nextFloat()).add(thisAltar);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(at)
                        .color(VFXColorFunction.randomBetween(ColorsAS.CONSTELLATION_TYPE_MAJOR, ColorsAS.CONSTELLATION_TYPE_MINOR))
                        .setScaleMultiplier(0.2F + rand.nextFloat() * 0.2F);
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
