/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.component.ColorComponent;
import hellfirepvp.astralsorcery.common.item.*;
import hellfirepvp.astralsorcery.common.item.base.BlockItemCustom;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.item.block.*;
import hellfirepvp.astralsorcery.common.item.crystal.AttunedCelestialCrystalItem;
import hellfirepvp.astralsorcery.common.item.crystal.AttunedRockCrystalItem;
import hellfirepvp.astralsorcery.common.item.crystal.CelestialCrystalItem;
import hellfirepvp.astralsorcery.common.item.crystal.RockCrystalItem;
import hellfirepvp.astralsorcery.common.item.dust.IlluminationPowderItem;
import hellfirepvp.astralsorcery.common.item.dust.NocturnalPowderItem;
import hellfirepvp.astralsorcery.common.item.dust.VividPowderItem;
import hellfirepvp.astralsorcery.common.item.tool.*;
import hellfirepvp.astralsorcery.common.item.wand.ArchitectWandItem;
import hellfirepvp.astralsorcery.common.item.wand.BlinkWandItem;
import hellfirepvp.astralsorcery.common.item.wand.ExchangeWandItem;
import hellfirepvp.astralsorcery.common.item.wand.GrapplingWandItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ItemsAS {

    public static final DeferredRegister.Items ITEM_REGISTER = DeferredRegister.createItems(AstralSorcery.MODID);

    public static final Tier CRYSTAL_TOOL_TIER = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            741, 4F, 1F, 26, () -> Ingredient.EMPTY);

    //------------------------------- ITEMS -------------------------------//

    public static DeferredItem<TomeItem> TOME = register("tome", TomeItem::new);
    public static DeferredItem<AstrolabeItem> ASTROLABE = register("astrolabe", AstrolabeItem::new);
    public static DeferredItem<WandItem> WAND = register("wand", WandItem::new);
    public static DeferredItem<ChiselItem> CHISEL = register("chisel", ChiselItem::new);
    public static DeferredItem<LinkingToolItem> LINKING_TOOL = register("linking_tool", LinkingToolItem::new);
    public static DeferredItem<IlluminationWandItem> ILLUMINATION_WAND = register("illumination_wand", IlluminationWandItem::new);
    public static DeferredItem<ArchitectWandItem> ARCHITECT_WAND = register("architect_wand", ArchitectWandItem::new);
    public static DeferredItem<ExchangeWandItem> EXCHANGE_WAND = register("exchange_wand", ExchangeWandItem::new);
    public static DeferredItem<BlinkWandItem> BLINK_WAND = register("blink_wand", BlinkWandItem::new);
    public static DeferredItem<GrapplingWandItem> GRAPPLING_WAND = register("grappling_wand", GrapplingWandItem::new);
    public static DeferredItem<KnowledgeShareItem> KNOWLEDGE_SHARE = register("knowledge_share", KnowledgeShareItem::new);
    public static DeferredItem<ConstellationPaperItem> CONSTELLATION_PAPER = register("constellation_paper", ConstellationPaperItem::new);
    public static DeferredItem<ArtifactItem> ARTIFACT = register("artifact", ArtifactItem::new);
    public static DeferredItem<ArtifactShardItem> ARTIFACT_SHARD = register("artifact_shard", ArtifactShardItem::new);

    public static DeferredItem<Item> AQUAMARINE = registerSimple("aquamarine");
    public static DeferredItem<Item> GLASS_LENS = registerSimple("glass_lens");
    public static DeferredItem<Item> RESONATING_GEM = registerSimple("resonating_gem");
    public static DeferredItem<Item> PARCHMENT = registerSimple("parchment");
    public static DeferredItem<Item> STARDUST = registerSimple("stardust");
    public static DeferredItem<StarmetalIngotItem> STARMETAL_INGOT = register("starmetal_ingot", StarmetalIngotItem::new);
    public static DeferredItem<Item> RAW_STARMETAL = registerSimple("raw_starmetal");
    public static DeferredItem<PerkSealItem> PERK_SEAL = register("perk_seal", PerkSealItem::new);
    public static DeferredItem<PerkNullifierItem> PERK_NULLIFIER = register("perk_nullifier", PerkNullifierItem::new);
    public static DeferredItem<DynamismGemItem> DYNAMISM_GEM_SKY = register("dynamism_gem_sky", () -> new DynamismGemItem(DynamismGemItem.GemType.SKY));
    public static DeferredItem<DynamismGemItem> DYNAMISM_GEM_DAY = register("dynamism_gem_day", () -> new DynamismGemItem(DynamismGemItem.GemType.DAY));
    public static DeferredItem<DynamismGemItem> DYNAMISM_GEM_NIGHT = register("dynamism_gem_night", () -> new DynamismGemItem(DynamismGemItem.GemType.NIGHT));

    public static DeferredItem<IlluminationPowderItem> ILLUMINATION_POWDER = register("illumination_powder", IlluminationPowderItem::new);
    public static DeferredItem<NocturnalPowderItem> NOCTURNAL_POWDER = register("nocturnal_powder", NocturnalPowderItem::new);
    public static DeferredItem<VividPowderItem> VIVID_POWDER = register("vivid_powder", VividPowderItem::new);
    public static DeferredItem<ShiftingStarItem> SHIFTING_STAR = register("shifting_star", ShiftingStarItem::new);
    public static DeferredItem<ShiftingStarItem> SHIFTING_STAR_AEVITAS = register("shifting_star_aevitas", () -> new ShiftingStarItem(ConstellationsAS.AEVITAS));
    public static DeferredItem<ShiftingStarItem> SHIFTING_STAR_ARMARA = register("shifting_star_armara", () -> new ShiftingStarItem(ConstellationsAS.ARMARA));
    public static DeferredItem<ShiftingStarItem> SHIFTING_STAR_DISCIDIA = register("shifting_star_discidia", () -> new ShiftingStarItem(ConstellationsAS.DISCIDIA));
    public static DeferredItem<ShiftingStarItem> SHIFTING_STAR_EVORSIO = register("shifting_star_evorsio", () -> new ShiftingStarItem(ConstellationsAS.EVORSIO));
    public static DeferredItem<ShiftingStarItem> SHIFTING_STAR_VICIO = register("shifting_star_vicio", () -> new ShiftingStarItem(ConstellationsAS.VICIO));
    public static DeferredItem<AkashicSingularityItem> AKASHIC_SINGULARITY = register("akashic_singularity", AkashicSingularityItem::new);

    public static DeferredItem<LumenCrystalItem> LUMEN_CRYSTAL = register("lumen_crystal", LumenCrystalItem::new);
    public static DeferredItem<RockCrystalItem> ROCK_CRYSTAL = register("rock_crystal", RockCrystalItem::new);
    public static DeferredItem<CelestialCrystalItem> CELESTIAL_CRYSTAL = register("celestial_crystal", CelestialCrystalItem::new);
    public static DeferredItem<AttunedRockCrystalItem> ATTUNED_ROCK_CRYSTAL = register("attuned_rock_crystal", AttunedRockCrystalItem::new);
    public static DeferredItem<AttunedCelestialCrystalItem> ATTUNED_CELESTIAL_CRYSTAL = register("attuned_celestial_crystal", AttunedCelestialCrystalItem::new);

    public static DeferredItem<CrystalAxeItem> CRYSTAL_AXE = register("crystal_axe", CrystalAxeItem::new);
    public static DeferredItem<CrystalPickaxeItem> CRYSTAL_PICKAXE = register("crystal_pickaxe", CrystalPickaxeItem::new);
    public static DeferredItem<CrystalShovelItem> CRYSTAL_SHOVEL = register("crystal_shovel", CrystalShovelItem::new);
    public static DeferredItem<CrystalSwordItem> CRYSTAL_SWORD = register("crystal_sword", CrystalSwordItem::new);
    public static DeferredItem<IridescentCrystalAxeItem> IRIDESCENT_CRYSTAL_AXE = register("iridescent_crystal_axe", IridescentCrystalAxeItem::new);
    public static DeferredItem<IridescentCrystalPickaxeItem> IRIDESCENT_CRYSTAL_PICKAXE = register("iridescent_crystal_pickaxe", IridescentCrystalPickaxeItem::new);
    public static DeferredItem<IridescentCrystalShovelItem> IRIDESCENT_CRYSTAL_SHOVEL = register("iridescent_crystal_shovel", IridescentCrystalShovelItem::new);
    public static DeferredItem<IridescentCrystalSwordItem> IRIDESCENT_CRYSTAL_SWORD = register("iridescent_crystal_sword", IridescentCrystalSwordItem::new);
    public static DeferredItem<EnchantmentAmuletItem> ENCHANTMENT_AMULET = register("enchantment_amulet", EnchantmentAmuletItem::new);
    public static DeferredItem<StardewItem> STARDEW = register("stardew", StardewItem::new);

    //------------------------------- BLOCK -------------------------------//

    public static DeferredItem<BlockItem> BLOCK_MARBLE_ARCH               = registerBlockItem(BlocksAS.MARBLE_ARCH);
    public static DeferredItem<BlockItem> BLOCK_MARBLE_BRICKS             = registerBlockItem(BlocksAS.MARBLE_BRICKS);
    public static DeferredItem<BlockItem> BLOCK_MARBLE_CHISELED           = registerBlockItem(BlocksAS.MARBLE_CHISELED);
    public static DeferredItem<BlockItem> BLOCK_MARBLE_ENGRAVED           = registerBlockItem(BlocksAS.MARBLE_ENGRAVED);
    public static DeferredItem<BlockItem> BLOCK_MARBLE_PILLAR             = registerBlockItem(BlocksAS.MARBLE_PILLAR);
    public static DeferredItem<BlockItem> BLOCK_MARBLE_RAW                = registerBlockItem(BlocksAS.MARBLE_RAW);
    public static DeferredItem<BlockItem> BLOCK_MARBLE_RUNED              = registerBlockItem(BlocksAS.MARBLE_RUNED);
    public static DeferredItem<BlockItem> BLOCK_MARBLE_SLAB               = registerBlockItem(BlocksAS.MARBLE_SLAB);
    public static DeferredItem<BlockItem> BLOCK_MARBLE_STAIRS             = registerBlockItem(BlocksAS.MARBLE_STAIRS);

    public static DeferredItem<BlockItem> BLOCK_SOOTY_MARBLE_ARCH         = registerBlockItem(BlocksAS.SOOTY_MARBLE_ARCH);
    public static DeferredItem<BlockItem> BLOCK_SOOTY_MARBLE_BRICKS       = registerBlockItem(BlocksAS.SOOTY_MARBLE_BRICKS);
    public static DeferredItem<BlockItem> BLOCK_SOOTY_MARBLE_CHISELED     = registerBlockItem(BlocksAS.SOOTY_MARBLE_CHISELED);
    public static DeferredItem<BlockItem> BLOCK_SOOTY_MARBLE_ENGRAVED     = registerBlockItem(BlocksAS.SOOTY_MARBLE_ENGRAVED);
    public static DeferredItem<BlockItem> BLOCK_SOOTY_MARBLE_PILLAR       = registerBlockItem(BlocksAS.SOOTY_MARBLE_PILLAR);
    public static DeferredItem<BlockItem> BLOCK_SOOTY_MARBLE_RAW          = registerBlockItem(BlocksAS.SOOTY_MARBLE_RAW);
    public static DeferredItem<BlockItem> BLOCK_SOOTY_MARBLE_RUNED        = registerBlockItem(BlocksAS.SOOTY_MARBLE_RUNED);
    public static DeferredItem<BlockItem> BLOCK_SOOTY_MARBLE_SLAB         = registerBlockItem(BlocksAS.SOOTY_MARBLE_SLAB);
    public static DeferredItem<BlockItem> BLOCK_SOOTY_MARBLE_STAIRS       = registerBlockItem(BlocksAS.SOOTY_MARBLE_STAIRS);

    public static DeferredItem<BlockItem> BLOCK_INFUSED_WOOD_RAW          = registerBlockItem(BlocksAS.INFUSED_WOOD_RAW);
    public static DeferredItem<BlockItem> BLOCK_INFUSED_WOOD_ARCH         = registerBlockItem(BlocksAS.INFUSED_WOOD_ARCH);
    public static DeferredItem<BlockItem> BLOCK_INFUSED_WOOD_COLUMN       = registerBlockItem(BlocksAS.INFUSED_WOOD_COLUMN);
    public static DeferredItem<BlockItem> BLOCK_INFUSED_WOOD_ENGRAVED     = registerBlockItem(BlocksAS.INFUSED_WOOD_ENGRAVED);
    public static DeferredItem<BlockItem> BLOCK_INFUSED_WOOD_ENRICHED     = registerBlockItem(BlocksAS.INFUSED_WOOD_ENRICHED);
    public static DeferredItem<BlockItem> BLOCK_INFUSED_WOOD_INFUSED      = registerBlockItem(BlocksAS.INFUSED_WOOD_INFUSED);
    public static DeferredItem<BlockItem> BLOCK_INFUSED_WOOD_PLANKS       = registerBlockItem(BlocksAS.INFUSED_WOOD_PLANKS);
    public static DeferredItem<BlockItem> BLOCK_INFUSED_WOOD_SLAB         = registerBlockItem(BlocksAS.INFUSED_WOOD_SLAB);
    public static DeferredItem<BlockItem> BLOCK_INFUSED_WOOD_STAIRS       = registerBlockItem(BlocksAS.INFUSED_WOOD_STAIRS);

    public static DeferredItem<BlockItem> BLOCK_AQUAMARINE_SHALE_ORE      = registerBlockItem(BlocksAS.AQUAMARINE_SHALE);
    public static DeferredItem<BlockItem> BLOCK_ROCK_CRYSTAL_ORE          = registerBlockItem(BlocksAS.ROCK_CRYSTAL_ORE);
    public static DeferredItem<BlockItem> BLOCK_STARMETAL_ORE             = registerBlockItem(BlocksAS.STARMETAL_ORE);
    public static DeferredItem<BlockItem> BLOCK_RAW_STARMETAL_BLOCK       = registerBlockItem(BlocksAS.RAW_STARMETAL_BLOCK);

    public static DeferredItem<BlockItem> BLOCK_GLIMMER_AMARANTH          = registerBlockItem(BlocksAS.GLIMMER_AMARANTH);
    public static DeferredItem<BlockItem> BLOCK_HYACINTH                  = registerBlockItem(BlocksAS.HYACINTH);
    public static DeferredItem<BlockItem> BLOCK_IRIS                      = registerBlockItem(BlocksAS.IRIS);
    public static DeferredItem<BlockItem> BLOCK_ORCHID                    = registerBlockItem(BlocksAS.ORCHID);
    public static DeferredItem<BlockItem> BLOCK_PROTEA                    = registerBlockItem(BlocksAS.PROTEA);
    public static DeferredItem<BlockItem> BLOCK_THISTLE                   = registerBlockItem(BlocksAS.THISTLE);

    public static DeferredItem<BlockItem> BLOCK_ALTAR_ILLUMINATION        = registerBlockItem(BlocksAS.ALTAR_ILLUMINATION);
    public static DeferredItem<BlockItem> BLOCK_ALTAR_RESONANCE           = registerBlockItem(BlocksAS.ALTAR_RESONANCE);
    public static DeferredItem<BlockItem> BLOCK_ALTAR_LUMINANCE           = registerBlockItem(BlocksAS.ALTAR_LUMINANCE);
    public static DeferredItem<BlockItem> BLOCK_ALTAR_RADIANCE            = registerBlockItem(BlocksAS.ALTAR_RADIANCE);

    public static DeferredItem<BlockItem> BLOCK_FOCUS_RELAY               = registerBlockItem(BlocksAS.FOCUS_RELAY);
    public static DeferredItem<BlockItem> BLOCK_CELESTIAL_CRYSTAL_CLUSTER = registerBlockItem(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER, CelestialCrystalClusterBlockItem::new);
    public static DeferredItem<BlockItem> BLOCK_GEM_CRYSTAL_CLUSTER       = registerBlockItem(BlocksAS.GEM_CRYSTAL_CLUSTER, GemCrystalClusterBlockItem::new);
    public static DeferredItem<BlockItem> BLOCK_LUMEN_CRYSTAL_CLUSTER     = registerBlockItem(BlocksAS.LUMEN_CRYSTAL_CLUSTER, LumenCrystalClusterBlockItem::new);

    public static DeferredItem<BlockItem> BLOCK_LUMEN_ARRAY               = registerBlockItem(BlocksAS.LUMEN_ARRAY);
    public static DeferredItem<BlockItem> BLOCK_LUMEN_ALCHEMY_ARRAY       = registerBlockItem(BlocksAS.LUMEN_ALCHEMY_ARRAY);
    public static DeferredItem<BlockItem> BLOCK_LUMEN_FILAMENT            = registerBlockItem(BlocksAS.LUMEN_FILAMENT);
    public static DeferredItem<BlockItem> BLOCK_LUMEN_CRYSTALLIZER        = registerBlockItem(BlocksAS.LUMEN_CRYSTALLIZER);
    public static DeferredItem<BlockItem> BLOCK_LIGHTWELL                 = registerBlockItem(BlocksAS.LIGHTWELL);
    public static DeferredItem<BlockItem> BLOCK_INFUSER                   = registerBlockItem(BlocksAS.INFUSER);
    public static DeferredItem<BlockItem> BLOCK_CHALICE                   = registerBlockItem(BlocksAS.CHALICE);
    public static DeferredItem<BlockItem> BLOCK_ATTUNEMENT_ALTAR          = registerBlockItem(BlocksAS.ATTUNEMENT_ALTAR);
    public static DeferredItem<BlockItem> BLOCK_TREE_BEACON               = registerBlockItem(BlocksAS.TREE_BEACON);
    public static DeferredItem<BlockItem> BLOCK_CELESTIAL_GATEWAY         = registerBlockItem(BlocksAS.CELESTIAL_GATEWAY,
            block -> new BlockItemCustom(block, new Item.Properties().component(DataComponentsAS.COLOR, ColorComponent.DEFAULT_YELLOW)));

    public static DeferredItem<BlockItem> BLOCK_LENS                      = registerBlockItem(BlocksAS.LENS, LensBlockItem::new);
    public static DeferredItem<BlockItem> BLOCK_PRISM                     = registerBlockItem(BlocksAS.PRISM, PrismBlockItem::new);
    public static DeferredItem<BlockItem> BLOCK_STARLIGHT_FOCUS_ROCK_CRYSTAL =
            registerBlockItem(BlocksAS.STARLIGHT_FOCUS_ROCK_CRYSTAL, StarlightFocusCrystalBlockItem::new);
    public static DeferredItem<BlockItem> BLOCK_STARLIGHT_FOCUS_CELESTIAL_CRYSTAL =
            registerBlockItem(BlocksAS.STARLIGHT_FOCUS_CELESTIAL_CRYSTAL, StarlightFocusCrystalBlockItem::new);
    public static DeferredItem<BlockItem> BLOCK_STELLAR_FILAMENT = registerBlockItem(BlocksAS.STELLAR_FILAMENT);
    public static DeferredItem<BlockItem> BLOCK_CAVE_ILLUMINATOR = registerBlockItem(BlocksAS.CAVE_ILLUMINATOR,
            block -> new BlockItemCustom(block, new Item.Properties().component(DataComponentsAS.COLOR, ColorComponent.DEFAULT_YELLOW)));

    //----------------------------- REGISTER ------------------------------//

    private static DeferredItem<BlockItem> registerBlockItem(DeferredBlock<?> block) {
        return registerBlockItem(block, BlockItemCustom::new);
    }

    private static <T extends BlockItem, B extends Block> DeferredItem<T> registerBlockItem(DeferredBlock<B> block, Function<B, T> blockItem) {
        return register(block.getId().getPath(), () -> blockItem.apply(block.get()));
    }

    private static DeferredItem<BlockItem> registerBlockItem(DeferredBlock<?> block, BiFunction<Block, Item.Properties, BlockItem> ctor) {
        Item.Properties blockItemProperties = new Item.Properties();
        ResourceLocation key = block.getId();
        return register(key.getPath(), () -> ctor.apply(block.get(), blockItemProperties));
    }

    private static DeferredItem<Item> registerSimple(String name) {
        return register(name, () -> new ItemCustom(new Item.Properties()));
    }

    private static <T extends Item> DeferredItem<T> register(String name, Supplier<T> item) {
        return ITEM_REGISTER.register(name, item);
    }
}
