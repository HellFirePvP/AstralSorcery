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
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlockProperties;
import hellfirepvp.astralsorcery.common.item.*;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedRockCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemRockCrystal;
import hellfirepvp.astralsorcery.common.item.dust.ItemIlluminationPowder;
import hellfirepvp.astralsorcery.common.item.dust.ItemNocturnalPowder;
import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGemDay;
import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGemNight;
import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGemSky;
import hellfirepvp.astralsorcery.common.item.lens.*;
import hellfirepvp.astralsorcery.common.item.tool.*;
import hellfirepvp.astralsorcery.common.item.useables.*;
import hellfirepvp.astralsorcery.common.item.wand.*;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;

import java.util.List;

import static hellfirepvp.astralsorcery.common.lib.ItemsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryItems
 * Created by HellFirePvP
 * Date: 01.06.2019 / 13:57
 */
public class RegistryItems {

    private static List<ItemDynamicColor> colorItems = Lists.newArrayList();

    private RegistryItems() {}

    public static void registerItems() {
        AQUAMARINE = registerItem(new ItemAquamarine());
        RESONATING_GEM = registerItem(new ItemResonatingGem());
        GLASS_LENS = registerItem(new ItemGlassLens());
        PARCHMENT = registerItem(new ItemParchment());
        STARMETAL_INGOT = registerItem(new ItemStarmetalIngot());
        STARDUST = registerItem(new ItemStardust());

        PERK_GEM_SKY = registerItem(new ItemPerkGemSky());
        PERK_GEM_DAY = registerItem(new ItemPerkGemDay());
        PERK_GEM_NIGHT = registerItem(new ItemPerkGemNight());
        CRYSTAL_AXE = registerItem(new ItemCrystalAxe());
        CRYSTAL_PICKAXE = registerItem(new ItemCrystalPickaxe());
        CRYSTAL_SHOVEL = registerItem(new ItemCrystalShovel());
        CRYSTAL_SWORD = registerItem(new ItemCrystalSword());
        INFUSED_CRYSTAL_AXE = registerItem(new ItemInfusedCrystalAxe());
        INFUSED_CRYSTAL_PICKAXE = registerItem(new ItemInfusedCrystalPickaxe());
        INFUSED_CRYSTAL_SHOVEL = registerItem(new ItemInfusedCrystalShovel());
        INFUSED_CRYSTAL_SWORD = registerItem(new ItemInfusedCrystalSword());

        TOME = registerItem(new ItemTome());
        CONSTELLATION_PAPER = registerItem(new ItemConstellationPaper());
        ENCHANTMENT_AMULET = registerItem(new ItemEnchantmentAmulet());
        KNOWLEDGE_SHARE = registerItem(new ItemKnowledgeShare());
        WAND = registerItem(new ItemWand());
        CHISEL = registerItem(new ItemChisel());
        RESONATOR = registerItem(new ItemResonator());
        LINKING_TOOL = registerItem(new ItemLinkingTool());
        ILLUMINATION_WAND = registerItem(new ItemIlluminationWand());
        ARCHITECT_WAND = registerItem(new ItemArchitectWand());
        EXCHANGE_WAND = registerItem(new ItemExchangeWand());
        GRAPPLE_WAND = registerItem(new ItemGrappleWand());
        BLINK_WAND = registerItem(new ItemBlinkWand());
        HAND_TELESCOPE = registerItem(new ItemHandTelescope());
        INFUSED_GLASS = registerItem(new ItemInfusedGlass());

        MANTLE = registerItem(new ItemMantle());

        PERK_SEAL = registerItem(new ItemPerkSeal());
        NOCTURNAL_POWDER = registerItem(new ItemNocturnalPowder());
        ILLUMINATION_POWDER = registerItem(new ItemIlluminationPowder());
        SHIFTING_STAR = registerItem(new ItemShiftingStar());
        SHIFTING_STAR_AEVITAS = registerItem(new ItemShiftingStarAevitas());
        SHIFTING_STAR_ARMARA = registerItem(new ItemShiftingStarArmara());
        SHIFTING_STAR_DISCIDIA = registerItem(new ItemShiftingStarDiscidia());
        SHIFTING_STAR_EVORSIO = registerItem(new ItemShiftingStarEvorsio());
        SHIFTING_STAR_VICIO = registerItem(new ItemShiftingStarVicio());

        COLORED_LENS_FIRE = registerItem(new ItemColoredLensFire());
        COLORED_LENS_BREAK = registerItem(new ItemColoredLensBreak());
        COLORED_LENS_GROWTH = registerItem(new ItemColoredLensGrowth());
        COLORED_LENS_DAMAGE = registerItem(new ItemColoredLensDamage());
        COLORED_LENS_REGENERATION = registerItem(new ItemColoredLensRegeneration());
        COLORED_LENS_PUSH = registerItem(new ItemColoredLensPush());
        COLORED_LENS_SPECTRAL = registerItem(new ItemColoredLensSpectral());

        ROCK_CRYSTAL = registerItem(new ItemRockCrystal());
        ATTUNED_ROCK_CRYSTAL = registerItem(new ItemAttunedRockCrystal());
        CELESTIAL_CRYSTAL = registerItem(new ItemCelestialCrystal());
        ATTUNED_CELESTIAL_CRYSTAL = registerItem(new ItemAttunedCelestialCrystal());
    }

    public static void registerItemBlocks() {
        RegistryBlocks.ITEM_BLOCKS.forEach(RegistryItems::registerItemBlock);
    }

    public static void registerFluidContainerItems() {
        RegistryFluids.FLUID_HOLDER_ITEMS.forEach(RegistryItems::registerItem);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerColors(ColorHandlerEvent.Item itemColorEvent) {
        colorItems.forEach(item -> itemColorEvent.getItemColors().register(item::getColor, (Item) item));
    }

    private static void registerItemBlock(CustomItemBlock block) {
        BlockItem itemBlock = block.createItemBlock(buildItemBlockProperties((Block) block));
        itemBlock.setRegistryName(itemBlock.getBlock().getRegistryName());
        AstralSorcery.getProxy().getRegistryPrimer().register(itemBlock);
    }

    private static <T extends Item> T registerItem(T item) {
        ResourceLocation name = NameUtil.fromClass(item, "Item");
        item.setRegistryName(name);
        AstralSorcery.getProxy().getRegistryPrimer().register(item);
        if (item instanceof ItemDynamicColor) {
            colorItems.add((ItemDynamicColor) item);
        }
        return item;
    }

    private static Item.Properties buildItemBlockProperties(Block block) {
        Item.Properties props = new Item.Properties();
        props.group(CommonProxy.ITEM_GROUP_AS);
        if (block instanceof CustomItemBlockProperties) {
            ItemGroup group = ((CustomItemBlockProperties) block).getItemGroup();
            if (group != null) {
                props.group(group);
            }
            if (!((CustomItemBlockProperties) block).canItemBeRepaired()) {
                props.setNoRepair();
            }

            props.rarity(((CustomItemBlockProperties) block).getItemRarity());
            props.maxStackSize(((CustomItemBlockProperties) block).getItemMaxStackSize());
            props.defaultMaxDamage(((CustomItemBlockProperties) block).getItemMaxDamage());
            props.containerItem(((CustomItemBlockProperties) block).getContainerItem());
            props.setISTER(((CustomItemBlockProperties) block).getItemTEISR());

            ((CustomItemBlockProperties) block).getItemToolLevels().forEach(props::addToolType);
        }
        return props;
    }

}
