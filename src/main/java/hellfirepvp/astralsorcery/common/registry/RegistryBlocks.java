/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.base.BlockDynamicColor;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.base.template.BlockSlabTemplate;
import hellfirepvp.astralsorcery.common.block.base.template.BlockStairsTemplate;
import hellfirepvp.astralsorcery.common.block.blackmarble.*;
import hellfirepvp.astralsorcery.common.block.foliage.BlockGlowFlower;
import hellfirepvp.astralsorcery.common.block.infusedwood.*;
import hellfirepvp.astralsorcery.common.block.marble.*;
import hellfirepvp.astralsorcery.common.block.ore.BlockAquamarineSandOre;
import hellfirepvp.astralsorcery.common.block.ore.BlockRockCrystalOre;
import hellfirepvp.astralsorcery.common.block.ore.BlockStarmetalOre;
import hellfirepvp.astralsorcery.common.block.tile.*;
import hellfirepvp.astralsorcery.common.block.tile.altar.BlockAltarAttunement;
import hellfirepvp.astralsorcery.common.block.tile.altar.BlockAltarConstellation;
import hellfirepvp.astralsorcery.common.block.tile.altar.BlockAltarDiscovery;
import hellfirepvp.astralsorcery.common.block.tile.altar.BlockAltarRadiance;
import hellfirepvp.astralsorcery.common.block.tile.crystal.BlockCelestialCollectorCrystal;
import hellfirepvp.astralsorcery.common.block.tile.crystal.BlockRockCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;

import java.util.LinkedList;
import java.util.List;

import static hellfirepvp.astralsorcery.common.lib.BlocksAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryBlocks
 * Created by HellFirePvP
 * Date: 31.05.2019 / 21:44
 */
public class RegistryBlocks {

    private static List<BlockDynamicColor> colorBlocks = Lists.newArrayList();
    static final List<CustomItemBlock> ITEM_BLOCKS = new LinkedList<>();

    private RegistryBlocks() {}

    public static void registerBlocks() {
        MARBLE_ARCH           = registerBlock(new BlockMarbleArch());
        MARBLE_BRICKS         = registerBlock(new BlockMarbleBricks());
        MARBLE_CHISELED       = registerBlock(new BlockMarbleChiseled());
        MARBLE_ENGRAVED       = registerBlock(new BlockMarbleEngraved());
        MARBLE_PILLAR         = registerBlock(new BlockMarblePillar());
        MARBLE_RAW            = registerBlock(new BlockMarbleRaw());
        MARBLE_RUNED          = registerBlock(new BlockMarbleRuned());
        MARBLE_STAIRS         = makeStairs(MARBLE_BRICKS.getDefaultState(), "marble_stairs");
        MARBLE_SLAB           = makeSlab(MARBLE_BRICKS.getDefaultState(), "marble_slab");
        BLACK_MARBLE_ARCH     = registerBlock(new BlockBlackMarbleArch());
        BLACK_MARBLE_BRICKS   = registerBlock(new BlockBlackMarbleBricks());
        BLACK_MARBLE_CHISELED = registerBlock(new BlockBlackMarbleChiseled());
        BLACK_MARBLE_ENGRAVED = registerBlock(new BlockBlackMarbleEngraved());
        BLACK_MARBLE_PILLAR   = registerBlock(new BlockBlackMarblePillar());
        BLACK_MARBLE_RAW      = registerBlock(new BlockBlackMarbleRaw());
        BLACK_MARBLE_RUNED    = registerBlock(new BlockBlackMarbleRuned());
        BLACK_MARBLE_STAIRS   = makeStairs(BLACK_MARBLE_BRICKS.getDefaultState(), "black_marble_stairs");
        BLACK_MARBLE_SLAB     = makeSlab(BLACK_MARBLE_BRICKS.getDefaultState(), "black_marble_slab");
        INFUSED_WOOD          = registerBlock(new BlockInfusedWood());
        INFUSED_WOOD_ARCH     = registerBlock(new BlockInfusedWoodArch());
        INFUSED_WOOD_COLUMN   = registerBlock(new BlockInfusedWoodColumn());
        INFUSED_WOOD_ENGRAVED = registerBlock(new BlockInfusedWoodEngraved());
        INFUSED_WOOD_ENRICHED = registerBlock(new BlockInfusedWoodEnriched());
        INFUSED_WOOD_INFUSED  = registerBlock(new BlockInfusedWoodInfused());
        INFUSED_WOOD_PLANKS   = registerBlock(new BlockInfusedWoodPlanks());
        INFUSED_WOOD_STAIRS   = makeStairs(INFUSED_WOOD_PLANKS.getDefaultState(), "infused_wood_stairs");
        INFUSED_WOOD_SLAB     = makeSlab(INFUSED_WOOD_PLANKS.getDefaultState(), "infused_wood_slab");

        AQUAMARINE_SAND_ORE   = registerBlock(new BlockAquamarineSandOre());
        ROCK_CRYSTAL_ORE      = registerBlock(new BlockRockCrystalOre());
        STARMETAL_ORE         = registerBlock(new BlockStarmetalOre());
        GLOW_FLOWER           = registerBlock(new BlockGlowFlower());

        SPECTRAL_RELAY              = registerBlock(new BlockSpectralRelay());
        ALTAR_DISCOVERY             = registerBlock(new BlockAltarDiscovery());
        ALTAR_ATTUNEMENT            = registerBlock(new BlockAltarAttunement());
        ALTAR_CONSTELLATION         = registerBlock(new BlockAltarConstellation());
        ALTAR_RADIANCE              = registerBlock(new BlockAltarRadiance());
        ATTUNEMENT_ALTAR            = registerBlock(new BlockAttunementAltar());
        CELESTIAL_CRYSTAL_CLUSTER   = registerBlock(new BlockCelestialCrystalCluster());
        GEM_CRYSTAL_CLUSTER         = registerBlock(new BlockGemCrystalCluster());
        ROCK_COLLECTOR_CRYSTAL      = registerBlock(new BlockRockCollectorCrystal());
        CELESTIAL_COLLECTOR_CRYSTAL = registerBlock(new BlockCelestialCollectorCrystal());
        LENS                        = registerBlock(new BlockLens());
        PRISM                       = registerBlock(new BlockPrism());
        RITUAL_LINK                 = registerBlock(new BlockRitualLink());
        RITUAL_PEDESTAL             = registerBlock(new BlockRitualPedestal());
        INFUSER                     = registerBlock(new BlockInfuser());
        CHALICE                     = registerBlock(new BlockChalice());
        WELL                        = registerBlock(new BlockWell());
        ILLUMINATOR                 = registerBlock(new BlockIlluminator());
        TELESCOPE                   = registerBlock(new BlockTelescope());
        OBSERVATORY                 = registerBlock(new BlockObservatory());
        REFRACTION_TABLE            = registerBlock(new BlockRefractionTable());

        FLARE_LIGHT         = registerBlock(new BlockFlareLight());
        TRANSLUCENT_BLOCK   = registerBlock(new BlockTranslucentBlock());
        VANISHING           = registerBlock(new BlockVanishing());
        STRUCTURAL          = registerBlock(new BlockStructural());
    }

    public static void registerFluidBlocks() {
        RegistryFluids.FLUID_BLOCKS.forEach(RegistryBlocks::registerBlock);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerColors(ColorHandlerEvent.Block blockColorEvent) {
        colorBlocks.forEach(block -> blockColorEvent.getBlockColors().register(block::getColor, (Block) block));
    }

    private static BlockSlabTemplate makeSlab(BlockState base, String name) {
        BlockSlabTemplate slabs = new BlockSlabTemplate(base, Block.Properties.from(base.getBlock()));
        ResourceLocation slabsName = base.getBlock().getRegistryName();
        slabsName = new ResourceLocation(slabsName.getNamespace(), name);
        return registerBlock(slabs, slabsName);
    }

    private static BlockStairsTemplate makeStairs(BlockState base, String name) {
        BlockStairsTemplate stairs = new BlockStairsTemplate(base, Block.Properties.from(base.getBlock()));
        ResourceLocation stairsName = base.getBlock().getRegistryName();
        stairsName = new ResourceLocation(stairsName.getNamespace(), name);
        return registerBlock(stairs, stairsName);
    }

    private static <T extends Block> T registerBlock(T block) {
        return registerBlock(block, NameUtil.fromClass(block, "Block"));
    }

    private static <T extends Block> T registerBlock(T block, ResourceLocation name) {
        block.setRegistryName(name);
        AstralSorcery.getProxy().getRegistryPrimer().register(block);
        if (block instanceof CustomItemBlock) {
            ITEM_BLOCKS.add((CustomItemBlock) block);
        }
        if (block instanceof BlockDynamicColor) {
            colorBlocks.add((BlockDynamicColor) block);
        }
        return block;
    }
}
