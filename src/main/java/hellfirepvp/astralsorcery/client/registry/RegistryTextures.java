/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
        TEX_BLACK   = loadTexture(MISC, "black");
        TEX_BLANK   = loadTexture(MISC, "blank");
        TEX_SMOKE_1 = loadTexture(MISC, "smoke_1");
        TEX_SMOKE_2 = loadTexture(MISC, "smoke_2");
        TEX_SMOKE_3 = loadTexture(MISC, "smoke_3");
        TEX_SMOKE_4 = loadTexture(MISC, "smoke_4");

        TEX_CONTAINER_TOME_STORAGE          = loadTexture(GUI, "container_tome_storage");
        TEX_CONTAINER_ALTAR_DISCOVERY       = loadTexture(GUI, "container_altar_discovery");
        TEX_CONTAINER_ALTAR_ATTUNEMENT      = loadTexture(GUI, "container_altar_attunement");
        TEX_CONTAINER_ALTAR_CONSTELLATION   = loadTexture(GUI, "container_altar_constellation");
        TEX_CONTAINER_ALTAR_RADIANCE        = loadTexture(GUI, "container_altar_radiance");

        TEX_GUI_CONSTELLATION_PAPER         = loadTexture(GUI, "constellation_paper");
        TEX_GUI_PARCHMENT_BLANK             = loadTexture(GUI, "parchment_blank");
        TEX_GUI_HAND_TELESCOPE              = loadTexture(GUI, "hand_telescope_frame");
        TEX_GUI_TELESCOPE                   = loadTexture(GUI, "telescope_frame");
        TEX_GUI_OBSERVATORY                 = loadTexture(GUI, "observatory_frame");
        TEX_GUI_REFRACTION_TABLE_EMPTY      = loadTexture(GUI, "refraction_table_empty");
        TEX_GUI_REFRACTION_TABLE_PARCHMENT  = loadTexture(GUI, "refraction_table_parchment");

        TEX_GUI_BOOKMARK                    = loadTexture(GUI, "book", "bookmark");
        TEX_GUI_BOOKMARK_STRETCHED          = loadTexture(GUI, "book", "bookmark_stretched");
        TEX_GUI_BOOK_ARROWS                 = loadTexture(GUI, "book", "arrows");
        TEX_GUI_BOOK_UNDERLINE              = loadTexture(GUI, "book", "underline");
        TEX_GUI_BOOK_BLANK                  = loadTexture(GUI, "book", "blank");
        TEX_GUI_BOOK_FRAME_FULL             = loadTexture(GUI, "book", "frame_full");
        TEX_GUI_BOOK_FRAME_LEFT             = loadTexture(GUI, "book", "frame_left");
        TEX_GUI_BOOK_STRUCTURE_ICONS        = loadTexture(GUI, "book", "structure_icons");
        TEX_GUI_BOOK_GRID_SLOT              = loadTexture(GUI, "book", "grid_slot");
        TEX_GUI_BOOK_GRID_RECIPE            = loadTexture(GUI, "book", "grid_recipe");
        TEX_GUI_BOOK_GRID_TRANSMUTATION     = loadTexture(GUI, "book", "grid_transmutation");
        TEX_GUI_BOOK_GRID_INFUSION          = loadTexture(GUI, "book", "grid_infusion");
        TEX_GUI_BOOK_GRID_T1                = loadTexture(GUI, "book", "grid_t1");
        TEX_GUI_BOOK_GRID_T2                = loadTexture(GUI, "book", "grid_t2");
        TEX_GUI_BOOK_GRID_T3                = loadTexture(GUI, "book", "grid_t3");
        TEX_GUI_BOOK_GRID_T4                = loadTexture(GUI, "book", "grid_t4");
        TEX_GUI_BACKGROUND_DEFAULT          = loadTexture(GUI, "book", "background_default");
        TEX_GUI_BACKGROUND_PERKS            = loadTexture(GUI, "book", "background_perks");
        TEX_GUI_BACKGROUND_CONSTELLATIONS   = loadTexture(GUI, "book", "background_constellations");
        TEX_GUI_STARFIELD_OVERLAY           = loadTexture(GUI, "book", "starfield_overlay");
        TEX_GUI_CLUSTER_ATTUNEMENT          = loadTexture(GUI, "book", "cluster_attunement");
        TEX_GUI_CLUSTER_BASICCRAFT          = loadTexture(GUI, "book", "cluster_basiccraft");
        TEX_GUI_CLUSTER_BRILLIANCE          = loadTexture(GUI, "book", "cluster_brilliance");
        TEX_GUI_CLUSTER_CONSTELLATION       = loadTexture(GUI, "book", "cluster_constellation");
        TEX_GUI_CLUSTER_DISCOVERY           = loadTexture(GUI, "book", "cluster_discovery");
        TEX_GUI_CLUSTER_RADIANCE            = loadTexture(GUI, "book", "cluster_radiance");

        TEX_GUI_LINE_CONNECTION             = loadTexture(GUI, "line_connection");
        TEX_GUI_TEXT_FIELD                  = loadTexture(GUI, "text_field");
        TEX_GUI_MENU_SLOT_GEM_CONTEXT       = loadTexture(GUI, "menu_slot_gem_context");
        TEX_GUI_MENU_SLOT                   = loadTexture(GUI, "menu_slot");

        TEX_GUI_PERK_INACTIVE               = loadTexture(GUI, "perk", "inactive");
        TEX_GUI_PERK_ACTIVE                 = loadTexture(GUI, "perk", "active");
        TEX_GUI_PERK_ACTIVATEABLE           = loadTexture(GUI, "perk", "activateable");
        TEX_GUI_PERK_HALO_INACTIVE          = loadTexture(GUI, "perk", "halo_inactive");
        TEX_GUI_PERK_HALO_ACTIVE            = loadTexture(GUI, "perk", "halo_active");
        TEX_GUI_PERK_HALO_ACTIVATEABLE      = loadTexture(GUI, "perk", "halo_activateable");
        TEX_GUI_PERK_SEARCH                 = TEX_GUI_PERK_HALO_INACTIVE; //Works fine enough
        TEX_GUI_PERK_SEAL                   = loadTexture(GUI, "perk", "seal");
        TEX_GUI_PERK_SEAL_BREAK             = loadTexture(GUI, "perk", "seal_break");
        TEX_GUI_PERK_UNLOCK                 = loadTexture(GUI, "perk", "unlock");

        TEX_OVERLAY_CHARGE                  = loadTexture(GUI, "overlay", "charge");
        TEX_OVERLAY_CHARGE_COLORLESS        = loadTexture(GUI, "overlay", "charge_colorless");
        TEX_OVERLAY_EXP_BAR                 = loadTexture(GUI, "overlay", "exp_bar");
        TEX_OVERLAY_EXP_FRAME               = loadTexture(GUI, "overlay", "exp_frame");
        TEX_OVERLAY_ITEM_FRAME              = loadTexture(GUI, "overlay", "item_frame");
        TEX_OVERLAY_ITEM_FRAME_EXTENSION    = loadTexture(GUI, "overlay", "item_frame_extension");

        TEX_SOLAR_ECLIPSE   = loadTexture(ENVIRONMENT, "solar_eclipse");
        TEX_STAR_CONNECTION = loadTexture(ENVIRONMENT, "line");
        TEX_STAR_1          = loadTexture(ENVIRONMENT, "star_1");
        TEX_STAR_2          = loadTexture(ENVIRONMENT, "star_2");

        TEX_PARTICLE_SMALL          = loadTexture(EFFECT, "particle_small");
        TEX_PARTICLE_LARGE          = loadTexture(EFFECT, "particle_large");
        TEX_CRYSTAL_EFFECT_1        = loadTexture(EFFECT, "crystal_burst_effect_1");
        TEX_CRYSTAL_EFFECT_2        = loadTexture(EFFECT, "crystal_burst_effect_2");
        TEX_CRYSTAL_EFFECT_3        = loadTexture(EFFECT, "crystal_burst_effect_3");
        TEX_GEM_CRYSTAL_BURST       = loadTexture(EFFECT, "gem_crystal_burst");
        TEX_GEM_CRYSTAL_BURST_SKY   = loadTexture(EFFECT, "gem_crystal_burst_sky");
        TEX_GEM_CRYSTAL_BURST_DAY   = loadTexture(EFFECT, "gem_crystal_burst_day");
        TEX_GEM_CRYSTAL_BURST_NIGHT = loadTexture(EFFECT, "gem_crystal_burst_night");
        TEX_COLLECTOR_EFFECT        = loadTexture(EFFECT, "collector_crystal_burst");
        TEX_CRAFT_BURST             = loadTexture(EFFECT, "craft_burst");
        TEX_CRAFT_FLARE             = loadTexture(EFFECT, "craft_flare");
        TEX_RELAY_FLARE             = loadTexture(EFFECT, "relay_flare");
        TEX_ATTUNEMENT_FLARE        = loadTexture(EFFECT, "attunement_flare");
        TEX_ENTITY_FLARE            = loadTexture(EFFECT, "entity_flare");
        TEX_GRAPPLING_HOOK          = loadTexture(EFFECT, "grappling_hook");
        TEX_AREA_OF_EFFECT_CUBE     = loadTexture(EFFECT, "area_of_effect_cube");
        TEX_HALO_INFUSION           = loadTexture(EFFECT, "halo_infusion");
        TEX_HALO_RITUAL             = loadTexture(EFFECT, "halo_ritual");

        TEX_LIGHTNING_PART      = loadTexture(EFFECT, "lightning_part");
        TEX_LIGHTBEAM           = loadTexture(EFFECT, "lightbeam");
        TEX_LIGHTBEAM_TRANSFER  = loadTexture(EFFECT, "lightbeam_transfer");

        TEX_MODEL_CRYSTAL_WHITE = loadTexture(MODEL, "crystal_white");
        TEX_MODEL_CRYSTAL_BLUE  = loadTexture(MODEL, "crystal_blue");
        TEX_MODEL_CELESTIAL_WINGS = loadTexture(MODEL, "celestial_wings_background");

        TEX_STARLIGHT_STORE = loadTexture(EFFECT, "starlight_storage");
    }

}
