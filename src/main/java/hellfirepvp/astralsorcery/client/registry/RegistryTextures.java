/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.registry;

import static hellfirepvp.astralsorcery.client.lib.TexturesAS.*;
import static hellfirepvp.astralsorcery.client.resource.AssetLibrary.loadTexture;
import static hellfirepvp.astralsorcery.client.resource.AssetLoader.TextureLocation.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryTextures
 * Created by HellFirePvP
 * Date: 07.07.2019 / 11:03
 */
public class RegistryTextures {

    private RegistryTextures() {}

    public static void loadTextures() {
        TEX_BLACK = loadTexture(MISC, "black");

        TEX_CONTAINER_TOME_STORAGE          = loadTexture(GUI, "container_tome_storage");
        TEX_CONTAINER_ALTAR_DISCOVERY       = loadTexture(GUI, "container_altar_discovery");
        TEX_CONTAINER_ALTAR_ATTUNEMENT      = loadTexture(GUI, "container_altar_attunement");
        TEX_CONTAINER_ALTAR_CONSTELLATION   = loadTexture(GUI, "container_altar_constellation");
        TEX_CONTAINER_ALTAR_RADIANCE        = loadTexture(GUI, "container_altar_radiance");

        TEX_GUI_CONSTELLATION_PAPER         = loadTexture(GUI, "constellation_paper");
        TEX_GUI_KNOWLEDGE_FRAGMENT          = loadTexture(GUI, "knowledge_fragment");
        TEX_GUI_KNOWLEDGE_FRAGMENT_BLANK    = loadTexture(GUI, "knowledge_fragment_blank");
        TEX_GUI_BOOKMARK                    = loadTexture(GUI, "bookmark");
        TEX_GUI_BOOKMARK_STRETCHED          = loadTexture(GUI, "bookmark_stretched");
        TEX_GUI_BOOK_ARROWS                 = loadTexture(GUI, "book_arrows");
        TEX_GUI_BOOK_UNDERLINE              = loadTexture(GUI, "book_underline");
        TEX_GUI_BOOK_BLANK                  = loadTexture(GUI, "book_blank");
        TEX_GUI_BOOK_FRAME_FULL             = loadTexture(GUI, "book_frame_full");
        TEX_GUI_BOOK_FRAME_LEFT             = loadTexture(GUI, "book_frame_left");
        TEX_GUI_STARFIELD_OVERLAY           = loadTexture(GUI, "starfield_overlay");
        TEX_GUI_BACKGROUND_DEFAULT          = loadTexture(GUI, "book_background_default");
        TEX_GUI_BACKGROUND_PERKS            = loadTexture(GUI, "book_background_perks");
        TEX_GUI_BACKGROUND_CONSTELLATIONS   = loadTexture(GUI, "book_background_constellations");
        TEX_GUI_CLUSTER_ATTUNEMENT          = loadTexture(GUI, "cluster_attunement");
        TEX_GUI_CLUSTER_BASICCRAFT          = loadTexture(GUI, "cluster_basiccraft");
        TEX_GUI_CLUSTER_BRILLIANCE          = loadTexture(GUI, "cluster_brilliance");
        TEX_GUI_CLUSTER_CONSTELLATION       = loadTexture(GUI, "cluster_constellation");
        TEX_GUI_CLUSTER_DISCOVERY           = loadTexture(GUI, "cluster_discovery");
        TEX_GUI_CLUSTER_RADIANCE            = loadTexture(GUI, "cluster_radiance");
        TEX_GUI_BOOK_STRUCTURE_ICONS        = loadTexture(GUI, "book_structure_icons");

        TEX_GUI_PERK_INACTIVE               = loadTexture(EFFECT, "perk_inactive");
        TEX_GUI_PERK_ACTIVE                 = loadTexture(EFFECT, "perk_active");
        TEX_GUI_PERK_ACTIVATEABLE           = loadTexture(EFFECT, "perk_activateable");
        TEX_GUI_PERK_HALO_INACTIVE          = loadTexture(EFFECT, "perk_halo_inactive");
        TEX_GUI_PERK_HALO_ACTIVE            = loadTexture(EFFECT, "perk_halo_active");
        TEX_GUI_PERK_HALO_ACTIVATEABLE      = loadTexture(EFFECT, "perk_halo_activateable");
        TEX_GUI_PERK_SEARCH                 = TEX_GUI_PERK_INACTIVE; //Works fine enough
        TEX_GUI_PERK_SEAL                   = loadTexture(EFFECT, "perk_seal");
        TEX_GUI_PERK_SEAL_BREAK             = loadTexture(EFFECT, "perk_seal_break");
        TEX_GUI_PERK_UNLOCK                 = loadTexture(EFFECT, "perk_unlock");

        TEX_GUI_LINE_CONNECTION             = loadTexture(EFFECT, "line_connection");
        TEX_GUI_TEXT_FIELD                  = loadTexture(GUI, "text_field");
        TEX_GUI_MENU_SLOT_GEM_CONTEXT       = loadTexture(GUI, "menu_slot_gem_context");
        TEX_GUI_MENU_SLOT                   = loadTexture(GUI, "menu_slot");

        TEX_SOLAR_ECLIPSE   = loadTexture(ENVIRONMENT, "solar_eclipse");
        TEX_STAR_CONNECTION = loadTexture(ENVIRONMENT, "line");
        TEX_STAR_1          = loadTexture(ENVIRONMENT, "star_1");
        TEX_STAR_2          = loadTexture(ENVIRONMENT, "star_2");

        TEX_STATIC_FLARE     = loadTexture(EFFECT, "flare_light");
        TEX_CRYSTAL_EFFECT_1 = loadTexture(EFFECT, "crystal_burst_effect_1");
        TEX_CRYSTAL_EFFECT_2 = loadTexture(EFFECT, "crystal_burst_effect_2");
        TEX_CRYSTAL_EFFECT_3 = loadTexture(EFFECT, "crystal_burst_effect_3");
        TEX_COLLECTOR_EFFECT = loadTexture(EFFECT, "collector_crystal_burst");

        TEX_LIGHTNING_PART = loadTexture(EFFECT, "lightning_part");
        TEX_LIGHTBEAM      = loadTexture(EFFECT, "lightbeam");

        TEX_STARLIGHT_STORE = loadTexture(EFFECT, "starlight_storage");
    }

}
