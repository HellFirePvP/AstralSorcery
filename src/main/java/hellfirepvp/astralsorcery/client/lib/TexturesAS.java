/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterMaterialAtlasesEvent;

import static hellfirepvp.astralsorcery.client.resource.AssetLibrary.loadTexture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TexturesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TexturesAS {

    //Atlases
    public static ResourceLocation ATLAS_LUMEN = AstralSorcery.key("textures/atlas/lumen.png");

    //Screen
    public static AbstractRenderTexture SCREEN_TOME_FRAME_FULL;
    public static AbstractRenderTexture SCREEN_TOME_FRAME_CUTOUT;
    public static AbstractRenderTexture SCREEN_TOME_FRAME_LEFT;
    public static AbstractRenderTexture SCREEN_TOME_BOOKMARK;
    public static AbstractRenderTexture SCREEN_TOME_SEARCH_TEXT_INPUT;
    public static AbstractRenderTexture SCREEN_TOME_NAV_ARROWS;
    public static AbstractRenderTexture SCREEN_TOME_SLICE_ARROWS;
    public static AbstractRenderTexture SCREEN_TOME_SLICE_ICONS;
    public static AbstractRenderTexture SCREEN_TOME_INFO_STAR;
    public static AbstractRenderTexture SCREEN_TOME_BACKGROUND_RESEARCH;
    public static AbstractRenderTexture SCREEN_TOME_BACKGROUND_CONSTELLATION;
    public static AbstractRenderTexture SCREEN_TOME_BACKGROUND_CONSTELLATION_DETAIL;
    public static AbstractRenderTexture SCREEN_TOME_BACKGROUND_PERKS;
    public static AbstractRenderTexture SCREEN_TOME_BACKGROUND_LUMEN;
    public static AbstractRenderTexture SCREEN_TOME_STARFIELD_OVERLAY;
    public static AbstractRenderTexture SCREEN_TOME_RESEARCH_FRAME_WOOD;
    public static AbstractRenderTexture SCREEN_TOME_RESEARCH_CONNECTION;
    public static AbstractRenderTexture SCREEN_TOME_UNDERLINE;

    public static AbstractRenderTexture SCREEN_TOME_PAGE_SLOT;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_SLOT_BORDER;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_RESULT_STAR;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_HEADER_NIGHT;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_HEADER_NO_NIGHT;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_EMPTY_RESULT;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_SINGLE_INPUT;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_CRAFTING;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_LUMEN_GENERATION;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_LUMEN_CRYSTALLIZATION;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_FOCAL_TRANSMUTATION;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_LIGHTWELL;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_STARLIGHT_INFUSION;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_ALTAR_T1;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_ALTAR_T2;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_ALTAR_T2_EXPANDED;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_ALTAR_T3;
    public static AbstractRenderTexture SCREEN_TOME_PAGE_GRID_ALTAR_T4;

    public static AbstractRenderTexture SCREEN_ELEMENT_LINE_CONNECTION;
    public static AbstractRenderTexture SCREEN_ELEMENT_MENU_SLOT;
    public static AbstractRenderTexture SCREEN_ELEMENT_MENU_SLOT_GEM_PERK;

    public static AbstractRenderTexture SCREEN_LUMEN_BAR_LARGE;
    public static AbstractRenderTexture SCREEN_LUMEN_BAR_MEDIUM;
    public static AbstractRenderTexture SCREEN_LUMEN_BAR_SMALL;

    public static AbstractRenderTexture SCREEN_CONTAINER_TOME_PAPERS;
    public static AbstractRenderTexture SCREEN_CONTAINER_ALTAR_ILLUMINATION;
    public static AbstractRenderTexture SCREEN_CONTAINER_ALTAR_RESONANCE;
    public static AbstractRenderTexture SCREEN_CONTAINER_ALTAR_RESONANCE_EXPANDED;
    public static AbstractRenderTexture SCREEN_CONTAINER_ALTAR_LUMINANCE;
    public static AbstractRenderTexture SCREEN_CONTAINER_ALTAR_RADIANCE;

    public static AbstractRenderTexture SCREEN_CONSTELLATION_PAPER;

    public static AbstractRenderTexture SCREEN_OVERLAY_ASTROLABE;
    public static AbstractRenderTexture SCREEN_OVERLAY_ASTROLABE_RULE;
    public static AbstractRenderTexture SCREEN_OVERLAY_ASTROLABE_INDICATOR;
    public static AbstractRenderTexture SCREEN_PERK_EXPERIENCE_BAR;
    public static AbstractRenderTexture SCREEN_PERK_EXPERIENCE_FRAME;

    //Environment
    public static AbstractRenderTexture SOLAR_ECLIPSE;
    public static AbstractRenderTexture STAR_1;
    public static AbstractRenderTexture STAR_2;
    public static AbstractRenderTexture STAR_LINE;

    //Effects
    public static AbstractRenderTexture PARTICLE_SMALL;
    public static AbstractRenderTexture PARTICLE_LARGE;
    public static AbstractRenderTexture LIGHT_BEAM;
    public static AbstractRenderTexture LIGHT_BEAM_TRANSFER;
    public static AbstractRenderTexture LIGHTNING_ELEMENT;
    public static AbstractRenderTexture ATTUNEMENT_RELAY_FLARE;
    public static AbstractRenderTexture ATTUNEMENT_ITEM_FLARE;
    public static AbstractRenderTexture ENTITY_FLARE;
    public static AbstractRenderTexture GRAPPLING_HOOK;

    public static AbstractRenderTexture EFFECT_SMOKE_1;
    public static AbstractRenderTexture EFFECT_SMOKE_2;
    public static AbstractRenderTexture EFFECT_SMOKE_3;
    public static AbstractRenderTexture EFFECT_SMOKE_4;

    public static AbstractRenderTexture SCREEN_EFFECT_PERK_ACTIVATABLE;
    public static AbstractRenderTexture SCREEN_EFFECT_PERK_ACTIVE;
    public static AbstractRenderTexture SCREEN_EFFECT_PERK_INACTIVE;
    public static AbstractRenderTexture SCREEN_EFFECT_PERK_HALO_ACTIVATABLE;
    public static AbstractRenderTexture SCREEN_EFFECT_PERK_HALO_ACTIVE;
    public static AbstractRenderTexture SCREEN_EFFECT_PERK_HALO_INACTIVE;
    public static AbstractRenderTexture SCREEN_EFFECT_PERK_SEARCH;
    public static AbstractRenderTexture SCREEN_EFFECT_PERK_SEAL;
    public static AbstractRenderTexture SCREEN_EFFECT_PERK_SEAL_BREAK;
    public static AbstractRenderTexture SCREEN_EFFECT_PERK_UNLOCK;

    public static void init() {
        SCREEN_TOME_FRAME_FULL = loadTexture(AssetLocation.SCREEN, "tome", "frame_full");
        SCREEN_TOME_FRAME_CUTOUT = loadTexture(AssetLocation.SCREEN, "tome", "frame_cutout");
        SCREEN_TOME_FRAME_LEFT = loadTexture(AssetLocation.SCREEN, "tome", "frame_left");
        SCREEN_TOME_BOOKMARK = loadTexture(AssetLocation.SCREEN, "tome", "bookmark");
        SCREEN_TOME_SEARCH_TEXT_INPUT = loadTexture(AssetLocation.SCREEN, "tome", "search_text_input");
        SCREEN_TOME_NAV_ARROWS = loadTexture(AssetLocation.SCREEN, "tome", "nav_arrows");
        SCREEN_TOME_SLICE_ARROWS = loadTexture(AssetLocation.SCREEN, "tome", "slice_arrows");
        SCREEN_TOME_SLICE_ICONS = loadTexture(AssetLocation.SCREEN, "tome", "slice_icons");
        SCREEN_TOME_INFO_STAR = loadTexture(AssetLocation.SCREEN, "tome", "info_star");
        SCREEN_TOME_BACKGROUND_RESEARCH = loadTexture(AssetLocation.SCREEN, "tome", "background_research");
        SCREEN_TOME_BACKGROUND_CONSTELLATION = loadTexture(AssetLocation.SCREEN, "tome", "background_constellation");
        SCREEN_TOME_BACKGROUND_CONSTELLATION_DETAIL = loadTexture(AssetLocation.SCREEN, "tome", "background_constellation_detail");
        SCREEN_TOME_BACKGROUND_PERKS = loadTexture(AssetLocation.SCREEN, "tome", "background_perks");
        SCREEN_TOME_BACKGROUND_LUMEN = loadTexture(AssetLocation.SCREEN, "tome", "background_lumen");
        SCREEN_TOME_STARFIELD_OVERLAY = loadTexture(AssetLocation.SCREEN, "tome", "starfield_overlay");
        SCREEN_TOME_RESEARCH_FRAME_WOOD = loadTexture(AssetLocation.SCREEN, "tome", "research_frame_wood");
        SCREEN_TOME_RESEARCH_CONNECTION = loadTexture(AssetLocation.SCREEN, "tome", "research_connection");
        SCREEN_TOME_UNDERLINE = loadTexture(AssetLocation.SCREEN, "tome", "underline");

        SCREEN_TOME_PAGE_SLOT = loadTexture(AssetLocation.SCREEN, "tome", "page", "slot");
        SCREEN_TOME_PAGE_SLOT_BORDER = loadTexture(AssetLocation.SCREEN, "tome", "page", "slot_border");
        SCREEN_TOME_PAGE_RESULT_STAR = loadTexture(AssetLocation.SCREEN, "tome", "page", "result_star");
        SCREEN_TOME_PAGE_HEADER_NIGHT = loadTexture(AssetLocation.SCREEN, "tome", "page", "recipe_header_night");
        SCREEN_TOME_PAGE_HEADER_NO_NIGHT = loadTexture(AssetLocation.SCREEN, "tome", "page", "recipe_header_no_night");
        SCREEN_TOME_PAGE_GRID_EMPTY_RESULT = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_empty_result");
        SCREEN_TOME_PAGE_GRID_SINGLE_INPUT = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_single_input");
        SCREEN_TOME_PAGE_GRID_CRAFTING = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_crafting");
        SCREEN_TOME_PAGE_GRID_LUMEN_GENERATION = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_lumen_generation");
        SCREEN_TOME_PAGE_GRID_LUMEN_CRYSTALLIZATION = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_lumen_crystallization");
        SCREEN_TOME_PAGE_GRID_FOCAL_TRANSMUTATION = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_focal_transmutation");
        SCREEN_TOME_PAGE_GRID_LIGHTWELL = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_lightwell");
        SCREEN_TOME_PAGE_GRID_STARLIGHT_INFUSION = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_starlight_infusion");
        SCREEN_TOME_PAGE_GRID_ALTAR_T1 = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_altar_t1");
        SCREEN_TOME_PAGE_GRID_ALTAR_T2 = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_altar_t2");
        SCREEN_TOME_PAGE_GRID_ALTAR_T2_EXPANDED = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_altar_t2_expanded");
        SCREEN_TOME_PAGE_GRID_ALTAR_T3 = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_altar_t3");
        SCREEN_TOME_PAGE_GRID_ALTAR_T4 = loadTexture(AssetLocation.SCREEN, "tome", "page", "grid_altar_t4");

        SCREEN_ELEMENT_LINE_CONNECTION = loadTexture(AssetLocation.SCREEN, "element_line_connection");
        SCREEN_ELEMENT_MENU_SLOT = loadTexture(AssetLocation.SCREEN, "element_menu_slot");
        SCREEN_ELEMENT_MENU_SLOT_GEM_PERK = loadTexture(AssetLocation.SCREEN, "element_menu_slot_gem_perk");

        SCREEN_LUMEN_BAR_LARGE = loadTexture(AssetLocation.SCREEN, "lumen_bar_large");
        SCREEN_LUMEN_BAR_MEDIUM = loadTexture(AssetLocation.SCREEN, "lumen_bar_medium");
        SCREEN_LUMEN_BAR_SMALL = loadTexture(AssetLocation.SCREEN, "lumen_bar_small");

        SCREEN_CONTAINER_TOME_PAPERS = loadTexture(AssetLocation.SCREEN, "container", "tome_papers");
        SCREEN_CONTAINER_ALTAR_ILLUMINATION = loadTexture(AssetLocation.SCREEN, "container", "altar_illumination");
        SCREEN_CONTAINER_ALTAR_RESONANCE = loadTexture(AssetLocation.SCREEN, "container", "altar_resonance");
        SCREEN_CONTAINER_ALTAR_RESONANCE_EXPANDED = loadTexture(AssetLocation.SCREEN, "container", "altar_resonance_expanded");
        SCREEN_CONTAINER_ALTAR_LUMINANCE = loadTexture(AssetLocation.SCREEN, "container", "altar_luminance");
        SCREEN_CONTAINER_ALTAR_RADIANCE = loadTexture(AssetLocation.SCREEN, "container", "altar_radiance");

        SCREEN_CONSTELLATION_PAPER = loadTexture(AssetLocation.SCREEN, "constellation_paper");

        SCREEN_OVERLAY_ASTROLABE           = loadTexture(AssetLocation.SCREEN, "overlay", "astrolabe_overlay");
        SCREEN_OVERLAY_ASTROLABE_INDICATOR = loadTexture(AssetLocation.SCREEN, "overlay", "astrolabe_overlay_indicator");
        SCREEN_OVERLAY_ASTROLABE_RULE      = loadTexture(AssetLocation.SCREEN, "overlay", "astrolabe_overlay_rule");
        SCREEN_PERK_EXPERIENCE_BAR         = loadTexture(AssetLocation.SCREEN, "overlay", "perk_experience_bar");
        SCREEN_PERK_EXPERIENCE_FRAME       = loadTexture(AssetLocation.SCREEN, "overlay", "perk_experience_frame");

        SOLAR_ECLIPSE  = loadTexture(AssetLocation.ENVIRONMENT, "solar_eclipse");
        STAR_1         = loadTexture(AssetLocation.ENVIRONMENT, "star_1");
        STAR_2         = loadTexture(AssetLocation.ENVIRONMENT, "star_2");
        STAR_LINE      = loadTexture(AssetLocation.ENVIRONMENT, "star_line");

        PARTICLE_SMALL         = loadTexture(AssetLocation.EFFECT, "particle_small");
        PARTICLE_LARGE         = loadTexture(AssetLocation.EFFECT, "particle_large");
        LIGHT_BEAM             = loadTexture(AssetLocation.EFFECT, "light_beam");
        LIGHT_BEAM_TRANSFER    = loadTexture(AssetLocation.EFFECT, "light_beam_transfer");
        ATTUNEMENT_RELAY_FLARE = loadTexture(AssetLocation.EFFECT, "attunement_relay_flare");
        ATTUNEMENT_ITEM_FLARE  = loadTexture(AssetLocation.EFFECT, "attunement_item_flare");
        LIGHTNING_ELEMENT      = loadTexture(AssetLocation.EFFECT, "lightning_element");
        ENTITY_FLARE           = loadTexture(AssetLocation.EFFECT, "entity_flare");
        GRAPPLING_HOOK         = loadTexture(AssetLocation.EFFECT, "grappling_hook");

        EFFECT_SMOKE_1 = loadTexture(AssetLocation.EFFECT, "smoke_1");
        EFFECT_SMOKE_2 = loadTexture(AssetLocation.EFFECT, "smoke_2");
        EFFECT_SMOKE_3 = loadTexture(AssetLocation.EFFECT, "smoke_3");
        EFFECT_SMOKE_4 = loadTexture(AssetLocation.EFFECT, "smoke_4");

        SCREEN_EFFECT_PERK_ACTIVATABLE      = loadTexture(AssetLocation.EFFECT, "perk", "activateable");
        SCREEN_EFFECT_PERK_ACTIVE           = loadTexture(AssetLocation.EFFECT, "perk", "active");
        SCREEN_EFFECT_PERK_INACTIVE         = loadTexture(AssetLocation.EFFECT, "perk", "inactive");
        SCREEN_EFFECT_PERK_HALO_ACTIVATABLE = loadTexture(AssetLocation.EFFECT, "perk", "halo_activateable");
        SCREEN_EFFECT_PERK_HALO_ACTIVE      = loadTexture(AssetLocation.EFFECT, "perk", "halo_active");
        SCREEN_EFFECT_PERK_HALO_INACTIVE    = loadTexture(AssetLocation.EFFECT, "perk", "halo_inactive");
        SCREEN_EFFECT_PERK_SEARCH           = SCREEN_EFFECT_PERK_HALO_INACTIVE;
        SCREEN_EFFECT_PERK_SEAL             = loadTexture(AssetLocation.EFFECT, "perk", "seal");
        SCREEN_EFFECT_PERK_SEAL_BREAK       = loadTexture(AssetLocation.EFFECT, "perk", "seal_break");
        SCREEN_EFFECT_PERK_UNLOCK           = loadTexture(AssetLocation.EFFECT, "perk", "unlock");
    }

    public static void registerAtlases(RegisterMaterialAtlasesEvent event) {
        event.register(ATLAS_LUMEN, AstralSorcery.key("lumen"));
    }
}
