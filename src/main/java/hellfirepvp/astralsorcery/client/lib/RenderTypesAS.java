package hellfirepvp.astralsorcery.client.lib;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.VertexFormat;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderTypesAS
 * Created by HellFirePvP
 * Date: 05.06.2020 / 15:50
 */
public class RenderTypesAS {

    public static VertexFormat POSITION_COLOR_TEX_NORMAL = new VertexFormat(ImmutableList.of(POSITION_3F, COLOR_4UB, TEX_2F, NORMAL_3B));

    //Effects/FX/VFX
    public static RenderType EFFECT_FX_GENERIC_PARTICLE;
    public static RenderType EFFECT_FX_GENERIC_PARTICLE_DEPTH;
    public static RenderType EFFECT_FX_GENERIC_PARTICLE_ATLAS;
    public static RenderType EFFECT_FX_LIGHTNING;
    public static RenderType EFFECT_FX_LIGHTBEAM;
    public static RenderType EFFECT_FX_CRYSTAL;
    public static RenderType EFFECT_FX_BURST;
    public static RenderType EFFECT_FX_DYNAMIC_TEXTURE_SPRITE;
    public static RenderType EFFECT_FX_TEXTURE_SPRITE;
    public static RenderType EFFECT_FX_CUBE_OPAQUE_ATLAS;
    public static RenderType EFFECT_FX_BLOCK_TRANSLUCENT;
    public static RenderType EFFECT_FX_BLOCK_TRANSLUCENT_DEPTH;
    public static RenderType EFFECT_FX_CUBE_TRANSLUCENT_ATLAS;
    public static RenderType EFFECT_FX_CUBE_TRANSLUCENT_ATLAS_DEPTH;
    public static RenderType EFFECT_FX_CUBE_AREA_OF_EFFECT;
    public static RenderType EFFECT_FX_COLOR_SPHERE;

    //Misc Effects
    public static RenderType EFFECT_LIGHTRAY_FAN;

    //Constellations
    public static RenderType CONSTELLATION_WORLD_STAR;
    public static RenderType CONSTELLATION_WORLD_CONNECTION;

    //Models
    public static RenderType MODEL_ATTUNEMENT_ALTAR;
    public static RenderType MODEL_LENS;
    public static RenderType MODEL_LENS_COLORED;
    public static RenderType MODEL_OBSERVATORY;
    public static RenderType MODEL_REFRACTION_TABLE;
    public static RenderType MODEL_REFRACTION_TABLE_GLASS;
    public static RenderType MODEL_TELESCOPE;

    public static RenderType MODEL_DEMON_WINGS;
    public static RenderType MODEL_CELESTIAL_WINGS;
    public static RenderType MODEL_WRAITH_WINGS;

    //TER stuff
    public static RenderType TER_WELL_LIQUID;
    public static RenderType TER_CHALICE_LIQUID;

    //GUI
    public static RenderType GUI_MISC_INFO_STAR;

}
