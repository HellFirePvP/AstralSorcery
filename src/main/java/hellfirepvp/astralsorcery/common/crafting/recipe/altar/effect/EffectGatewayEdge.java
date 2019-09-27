/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectGatewayEdge
 * Created by HellFirePvP
 * Date: 25.09.2019 / 19:09
 */
public class EffectGatewayEdge extends AltarRecipeEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        if (state == ActiveSimpleAltarRecipe.CraftingState.ACTIVE) {
            int scale = 2;
            if(altar.getAltarType().ordinal() > AltarType.ATTUNEMENT.ordinal()) {
                scale = 3;
            }
            int edgeScale = (scale * 2 + 1);
            for (int amount = 0; amount < 3; amount++) {

                Vector3 offset = new Vector3(altar).add(-scale, 0, -scale);
                if(rand.nextBoolean()) {
                    offset.add(edgeScale * (rand.nextBoolean() ? 1 : 0), 0, rand.nextFloat() * edgeScale);
                } else {
                    offset.add(rand.nextFloat() * edgeScale, 0, edgeScale * (rand.nextBoolean() ? 1 : 0));
                }
                FXFacingParticle particle = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(offset)
                        .setGravityStrength(-0.003F)
                        .setScaleMultiplier(0.25F + rand.nextFloat() * 0.15F)
                        .color(VFXColorFunction.constant(ColorsAS.DEFAULT_GENERIC_PARTICLE))
                        .setMaxAge(20 + rand.nextInt(30));

                switch (rand.nextInt(4)) {
                    case 0:
                        particle.color(VFXColorFunction.WHITE);
                        break;
                    case 1:
                        particle.color(VFXColorFunction.constant(ColorsAS.CELESTIAL_CRYSTAL));
                        break;
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTESR(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state, double x, double y, double z, float pTicks) {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onCraftingFinish(TileAltar altar, boolean isChaining) {}
}
