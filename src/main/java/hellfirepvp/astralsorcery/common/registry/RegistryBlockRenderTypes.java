package hellfirepvp.astralsorcery.common.registry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.fluid.Fluid;

import java.util.Arrays;
import java.util.List;

import static hellfirepvp.astralsorcery.common.lib.BlocksAS.*;
import static hellfirepvp.astralsorcery.common.lib.FluidsAS.LIQUID_STARLIGHT_FLOWING;
import static hellfirepvp.astralsorcery.common.lib.FluidsAS.LIQUID_STARLIGHT_SOURCE;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryBlockRenderTypes
 * Created by HellFirePvP
 * Date: 10.06.2020 / 21:06
 */
public class RegistryBlockRenderTypes {

    private RegistryBlockRenderTypes() {}

    public static void initBlocks() {
        setRenderLayer(MARBLE_ARCH,           RenderType.getSolid());
        setRenderLayer(MARBLE_BRICKS,         RenderType.getSolid());
        setRenderLayer(MARBLE_CHISELED,       RenderType.getSolid());
        setRenderLayer(MARBLE_ENGRAVED,       RenderType.getSolid());
        setRenderLayer(MARBLE_PILLAR,         RenderType.getSolid());
        setRenderLayer(MARBLE_RAW,            RenderType.getSolid());
        setRenderLayer(MARBLE_RUNED,          RenderType.getSolid());
        setRenderLayer(MARBLE_STAIRS,         RenderType.getSolid());
        setRenderLayer(MARBLE_SLAB,           RenderType.getSolid());
        setRenderLayer(BLACK_MARBLE_ARCH,     RenderType.getSolid());
        setRenderLayer(BLACK_MARBLE_BRICKS,   RenderType.getSolid());
        setRenderLayer(BLACK_MARBLE_CHISELED, RenderType.getSolid());
        setRenderLayer(BLACK_MARBLE_ENGRAVED, RenderType.getSolid());
        setRenderLayer(BLACK_MARBLE_PILLAR,   RenderType.getSolid());
        setRenderLayer(BLACK_MARBLE_RAW,      RenderType.getSolid());
        setRenderLayer(BLACK_MARBLE_RUNED,    RenderType.getSolid());
        setRenderLayer(BLACK_MARBLE_STAIRS,   RenderType.getSolid());
        setRenderLayer(BLACK_MARBLE_SLAB,     RenderType.getSolid());
        setRenderLayer(INFUSED_WOOD,          RenderType.getSolid());
        setRenderLayer(INFUSED_WOOD_ARCH,     RenderType.getSolid());
        setRenderLayer(INFUSED_WOOD_COLUMN,   RenderType.getSolid());
        setRenderLayer(INFUSED_WOOD_ENGRAVED, RenderType.getSolid());
        setRenderLayer(INFUSED_WOOD_ENRICHED, RenderType.getSolid());
        setRenderLayer(INFUSED_WOOD_INFUSED,  RenderType.getSolid());
        setRenderLayer(INFUSED_WOOD_PLANKS,   RenderType.getSolid());
        setRenderLayer(INFUSED_WOOD_STAIRS,   RenderType.getSolid());
        setRenderLayer(INFUSED_WOOD_SLAB,     RenderType.getSolid());

        setRenderLayer(AQUAMARINE_SAND_ORE, RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(ROCK_CRYSTAL_ORE,    RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(STARMETAL_ORE,       RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(GLOW_FLOWER,         RenderType.getCutout());

        setRenderLayer(SPECTRAL_RELAY,              RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(ALTAR_DISCOVERY,             RenderType.getSolid());
        setRenderLayer(ALTAR_ATTUNEMENT,            RenderType.getSolid());
        setRenderLayer(ALTAR_CONSTELLATION,         RenderType.getSolid());
        setRenderLayer(ALTAR_RADIANCE,              RenderType.getSolid());
        setRenderLayer(ATTUNEMENT_ALTAR,            RenderType.getSolid());
        setRenderLayer(CELESTIAL_CRYSTAL_CLUSTER,   RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(GEM_CRYSTAL_CLUSTER,         RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(ROCK_COLLECTOR_CRYSTAL,      RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(CELESTIAL_COLLECTOR_CRYSTAL, RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(LENS,                        RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(PRISM,                       RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(RITUAL_LINK,                 RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(RITUAL_PEDESTAL,             RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(INFUSER,                     RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(CHALICE,                     RenderType.getSolid());
        setRenderLayer(WELL,                        RenderType.getSolid());
        setRenderLayer(ILLUMINATOR,                 RenderType.getSolid(), RenderType.getTranslucent());
        setRenderLayer(TELESCOPE,                   RenderType.getSolid());
        setRenderLayer(TELESCOPE,                   RenderType.getSolid());
        setRenderLayer(OBSERVATORY,                 RenderType.getSolid());
        setRenderLayer(REFRACTION_TABLE,            RenderType.getSolid());

        setRenderLayer(FLARE_LIGHT,       RenderType.getTranslucent());
        setRenderLayer(TRANSLUCENT_BLOCK, RenderType.getTranslucent());
        setRenderLayer(VANISHING,         RenderType.getTranslucent());
        setRenderLayer(STRUCTURAL,        RenderType.getTranslucent());
    }

    public static void initFluids() {
        RegistryFluids.FLUID_BLOCKS.forEach((fluidBlock) -> setRenderLayer(fluidBlock, RenderType.getTranslucent()));

        setRenderLayer(LIQUID_STARLIGHT_SOURCE, RenderType.getTranslucent());
        setRenderLayer(LIQUID_STARLIGHT_FLOWING, RenderType.getTranslucent());
    }

    private static void setRenderLayer(Block block, RenderType... types) {
        List<RenderType> typeList = Arrays.asList(types);
        RenderTypeLookup.setRenderLayer(block, typeList::contains);
    }

    private static void setRenderLayer(Fluid fluid, RenderType... types) {
        List<RenderType> typeList = Arrays.asList(types);
        RenderTypeLookup.setRenderLayer(fluid, typeList::contains);
    }
}
