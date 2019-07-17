/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.registry;

import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;

import static hellfirepvp.astralsorcery.client.lib.SpritesAS.*;
import static hellfirepvp.astralsorcery.client.lib.TexturesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistrySprites
 * Created by HellFirePvP
 * Date: 07.07.2019 / 11:04
 */
public class RegistrySprites {

    private RegistrySprites() {}

    public static void loadSprites() {
        SPR_CRYSTAL_EFFECT_1 = new SpriteSheetResource(TEX_CRYSTAL_EFFECT_1, 5, 8);
        SPR_CRYSTAL_EFFECT_2 = new SpriteSheetResource(TEX_CRYSTAL_EFFECT_2, 5, 8);
        SPR_CRYSTAL_EFFECT_3 = new SpriteSheetResource(TEX_CRYSTAL_EFFECT_3, 5, 8);

        SPR_COLLECTOR_EFFECT = new SpriteSheetResource(TEX_COLLECTOR_EFFECT, 5, 16);
        SPR_LIGHTBEAM = new SpriteSheetResource(TEX_LIGHTBEAM, 4, 16);
    }

}
