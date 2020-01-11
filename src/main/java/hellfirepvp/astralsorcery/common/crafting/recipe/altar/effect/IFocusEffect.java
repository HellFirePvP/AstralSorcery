/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IFocusEffect
 * Created by HellFirePvP
 * Date: 27.09.2019 / 21:11
 */
public interface IFocusEffect {

    @Nonnull
    default Color getFocusColor(IConstellation cst, Random rand) {
        Color c = Color.WHITE;
        if (cst != null && rand.nextInt(4) == 0) {
            if (rand.nextInt(3) == 0) {
                c = cst.getConstellationColor().brighter();
            } else {
                c = cst.getConstellationColor();
            }
        }
        return c;
    }

}
