/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
        SPR_CRYSTAL_EFFECT_1        = new SpriteSheetResource(TEX_CRYSTAL_EFFECT_1, 5, 8);
        SPR_CRYSTAL_EFFECT_2        = new SpriteSheetResource(TEX_CRYSTAL_EFFECT_2, 5, 8);
        SPR_CRYSTAL_EFFECT_3        = new SpriteSheetResource(TEX_CRYSTAL_EFFECT_3, 5, 8);
        SPR_GEM_CRYSTAL_BURST       = new SpriteSheetResource(TEX_GEM_CRYSTAL_BURST, 5, 8);
        SPR_GEM_CRYSTAL_BURST_SKY   = new SpriteSheetResource(TEX_GEM_CRYSTAL_BURST_SKY, 5, 8);
        SPR_GEM_CRYSTAL_BURST_DAY   = new SpriteSheetResource(TEX_GEM_CRYSTAL_BURST_DAY, 5, 8);
        SPR_GEM_CRYSTAL_BURST_NIGHT = new SpriteSheetResource(TEX_GEM_CRYSTAL_BURST_NIGHT, 5, 8);

        SPR_PERK_INACTIVE          = new SpriteSheetResource(TEX_GUI_PERK_INACTIVE, 5, 8);
        SPR_PERK_ACTIVE            = new SpriteSheetResource(TEX_GUI_PERK_ACTIVE, 5, 8);
        SPR_PERK_ACTIVATEABLE      = new SpriteSheetResource(TEX_GUI_PERK_ACTIVATEABLE, 5, 8);
        SPR_PERK_HALO_INACTIVE     = new SpriteSheetResource(TEX_GUI_PERK_HALO_INACTIVE, 4, 8);
        SPR_PERK_HALO_ACTIVE       = new SpriteSheetResource(TEX_GUI_PERK_HALO_ACTIVE, 4, 8);
        SPR_PERK_HALO_ACTIVATEABLE = new SpriteSheetResource(TEX_GUI_PERK_HALO_ACTIVATEABLE, 4, 8);
        SPR_PERK_SEARCH            = new SpriteSheetResource(TEX_GUI_PERK_SEARCH, 4, 8);
        SPR_PERK_SEAL              = new SpriteSheetResource(TEX_GUI_PERK_SEAL, 4, 8);
        SPR_PERK_SEAL_BREAK        = new SpriteSheetResource(TEX_GUI_PERK_SEAL_BREAK, 7, 8);
        SPR_PERK_UNLOCK            = new SpriteSheetResource(TEX_GUI_PERK_UNLOCK, 5, 16);

        SPR_COLLECTOR_EFFECT = new SpriteSheetResource(TEX_COLLECTOR_EFFECT, 5, 16);
        SPR_CRAFT_BURST = new SpriteSheetResource(TEX_CRAFT_BURST, 6, 8);
        SPR_CRAFT_FLARE = new SpriteSheetResource(TEX_CRAFT_FLARE, 8, 8);
        SPR_ATTUNEMENT_FLARE = new SpriteSheetResource(TEX_ATTUNEMENT_FLARE, 6, 8);
        SPR_RELAY_FLARE = new SpriteSheetResource(TEX_RELAY_FLARE, 6, 8);
        SPR_LIGHTBEAM = new SpriteSheetResource(TEX_LIGHTBEAM, 4, 16);
        SPR_LIGHTBEAM_TRANSFER = new SpriteSheetResource(TEX_LIGHTBEAM_TRANSFER, 4, 16);
        SPR_ENTITY_FLARE = new SpriteSheetResource(TEX_ENTITY_FLARE, 6, 8);
        SPR_GRAPPLING_HOOK = new SpriteSheetResource(TEX_GRAPPLING_HOOK, 4, 8);
        SPR_HALO_INFUSION = new SpriteSheetResource(TEX_HALO_INFUSION, 8, 8);
        SPR_HALO_RITUAL = new SpriteSheetResource(TEX_HALO_RITUAL, 6, 8);

        SPR_OVERLAY_CHARGE = new SpriteSheetResource(TEX_OVERLAY_CHARGE, 8, 4);
        SPR_OVERLAY_CHARGE_COLORLESS = new SpriteSheetResource(TEX_OVERLAY_CHARGE_COLORLESS, 8, 4);
        SPR_STARLIGHT_STORE = new SpriteSheetResource(TEX_STARLIGHT_STORE, 16, 4);
    }

}
