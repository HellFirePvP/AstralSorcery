/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderTileAltar
 * Created by HellFirePvP
 * Date: 28.09.2019 / 22:05
 */
public class RenderTileAltar extends CustomTileEntityRenderer<TileAltar> {

    // TODO constellation render,
    //  central crystal render

    @Override
    public void render(TileAltar altar, double x, double y, double z, float pTicks, int destroyStage) {
        if (altar.getAltarType() == AltarType.RADIANCE) {

        }

        ActiveSimpleAltarRecipe recipe = altar.getActiveRecipe();
        if (recipe != null) {
            recipe.getRecipeToCraft().getCraftingEffects()
                    .forEach(effect -> effect.onTESR(altar, recipe.getState(), x, y, z, pTicks));
        }
    }
}
