/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson;

import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomRecipe
 * Created by HellFirePvP
 * Date: 02.12.2019 / 18:59
 */
public abstract class CustomRecipe {

    private final ResourceLocation key;

    protected CustomRecipe(ResourceLocation key) {
        this.key = key;
    }

    public final ResourceLocation getKey() {
        return key;
    }
}
