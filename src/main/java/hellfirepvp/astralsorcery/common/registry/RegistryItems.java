/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.MaterialAirish;
import hellfirepvp.astralsorcery.common.item.*;
import hellfirepvp.astralsorcery.common.item.base.IItemVariants;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockAltar;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockCustomName;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockRitualPedestal;
import hellfirepvp.astralsorcery.common.item.block.ItemCollectorCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemRockCrystalSimple;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedRockCrystal;
import hellfirepvp.astralsorcery.common.item.tool.*;
import hellfirepvp.astralsorcery.common.item.tool.sextant.ItemSextant;
import hellfirepvp.astralsorcery.common.item.tool.wand.ItemWand;
import hellfirepvp.astralsorcery.common.item.useables.ItemShiftingStar;
import hellfirepvp.astralsorcery.common.item.useables.ItemUsableDust;
import hellfirepvp.astralsorcery.common.item.wand.ItemArchitectWand;
import hellfirepvp.astralsorcery.common.item.wand.ItemExchangeWand;
import hellfirepvp.astralsorcery.common.item.wand.ItemGrappleWand;
import hellfirepvp.astralsorcery.common.item.wand.ItemIlluminationWand;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.item.wearable.ItemEnchantmentAmulet;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.LinkedList;
import java.util.List;

import static hellfirepvp.astralsorcery.common.lib.ItemsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryItems
 * Created by HellFirePvP
 * Date: 07.05.2016 / 15:03
 */
public class RegistryItems {

    public static List<ItemDynamicColor> pendingDynamicColorItems = new LinkedList<>();

    public static Item.ToolMaterial crystalToolMaterial;
    public static EnumRarity rarityCelestial;
    public static Material materialTransparentReplaceable;
    public static ItemArmor.ArmorMaterial imbuedLeatherMaterial;

    public static CreativeTabs creativeTabAstralSorcery,
            creativeTabAstralSorceryPapers,
            creativeTabAstralSorceryTunedCrystals;

    public static void setupDefaults() {
        creativeTabAstralSorcery = new CreativeTabs(AstralSorcery.MODID) {
            @Override
            public ItemStack getTabIconItem() {
                return new ItemStack(ItemsAS.journal);
            }
        };
        creativeTabAstralSorceryPapers = new CreativeTabs(AstralSorcery.MODID + ".papers") {
            @Override
            public ItemStack getTabIconItem() {
                return new ItemStack(ItemsAS.constellationPaper);
            }
        };
        creativeTabAstralSorceryTunedCrystals = new CreativeTabs(AstralSorcery.MODID + ".crystals") {
            @Override
            public ItemStack getTabIconItem() {
                return new ItemStack(ItemsAS.tunedRockCrystal);
            }
        };

        crystalToolMaterial = EnumHelper.addToolMaterial("CRYSTAL", 3, 1000, 20.0F, 5.5F, 40);
        crystalToolMaterial.setRepairItem(ItemStack.EMPTY);

        rarityCelestial = EnumHelper.addRarity("CELESTIAL", TextFormatting.BLUE, "Celestial");
        materialTransparentReplaceable = new MaterialAirish();

        imbuedLeatherMaterial = EnumHelper.addArmorMaterial("AS_IMBUEDLEATHER",
                "as.imbuedleather", 26, new int[] { 0, 0, 7, 0}, 30, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 2);
        imbuedLeatherMaterial.setRepairItem(ItemCraftingComponent.MetaType.STARDUST.asStack());
    }

    public static void init() {
        registerItems();

        registerBlockItems();

        registerDispenseBehavior();
    }

    //"Normal" items
    private static void registerItems() {
        craftingComponent = registerItem(new ItemCraftingComponent());
        constellationPaper = registerItem(new ItemConstellationPaper());
        infusedGlass = registerItem(new ItemInfusedGlass());

        rockCrystal = registerItem(new ItemRockCrystalSimple());
        tunedRockCrystal = registerItem(new ItemTunedRockCrystal());

        celestialCrystal = registerItem(new ItemCelestialCrystal());
        tunedCelestialCrystal = registerItem(new ItemTunedCelestialCrystal());

        journal = registerItem(new ItemJournal());
        handTelescope = registerItem(new ItemHandTelescope());
        linkingTool = registerItem(new ItemLinkingTool());
        wand = registerItem(new ItemWand());
        sextant = registerItem(new ItemSextant());
        illuminationWand = registerItem(new ItemIlluminationWand());
        coloredLens = registerItem(new ItemColoredLens());
        skyResonator = registerItem(new ItemSkyResonator());
        shiftingStar = registerItem(new ItemShiftingStar());
        //roseBranchBow = registerItem(new ItemRoseBranchBow());
        architectWand = registerItem(new ItemArchitectWand());
        exchangeWand = registerItem(new ItemExchangeWand());
        grapplingWand = registerItem(new ItemGrappleWand());
        useableDust = registerItem(new ItemUsableDust());
        knowledgeShare = registerItem(new ItemKnowledgeShare());

        crystalPickaxe = registerItem(new ItemCrystalPickaxe());
        crystalShovel = registerItem(new ItemCrystalShovel());
        crystalAxe = registerItem(new ItemCrystalAxe());
        crystalSword = registerItem(new ItemCrystalSword());
        chargedCrystalAxe = registerItem(new ItemChargedCrystalAxe());
        chargedCrystalSword = registerItem(new ItemChargedCrystalSword());
        chargedCrystalPickaxe = registerItem(new ItemChargedCrystalPickaxe());
        chargedCrystalShovel = registerItem(new ItemChargedCrystalShovel());

        armorImbuedCape = registerItem(new ItemCape());
        enchantmentAmulet = registerItem(new ItemEnchantmentAmulet());
    }

    //Items associated to blocks/itemblocks
    private static void registerBlockItems() {
        RegistryBlocks.defaultItemBlocksToRegister.forEach(RegistryItems::registerDefaultItemBlock);
        registerItem(new ItemSlab(BlocksAS.blockMarbleSlab, BlocksAS.blockMarbleSlab, BlocksAS.blockMarbleDoubleSlab));
        RegistryBlocks.customNameItemBlocksToRegister.forEach(RegistryItems::registerCustomNameItemBlock);

        registerItem(new ItemBlockRitualPedestal());
        registerItem(new ItemBlockAltar());

        registerItem(new ItemCollectorCrystal(BlocksAS.collectorCrystal));
        registerItem(new ItemCollectorCrystal(BlocksAS.celestialCollectorCrystal));
    }

    private static void registerDispenseBehavior() {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(useableDust, useableDust);
    }

    private static <T extends Block> void registerCustomNameItemBlock(T block) {
        registerItem(new ItemBlockCustomName(block), block.getClass().getSimpleName().toLowerCase());
    }

    private static <T extends Block> void registerDefaultItemBlock(T block) {
        registerDefaultItemBlock(block, block.getClass().getSimpleName().toLowerCase());
    }

    private static <T extends Block> void registerDefaultItemBlock(T block, String name) {
        registerItem(new ItemBlock(block), name);
    }

    private static <T extends Item> T registerItem(T item, String name) {
        item.setUnlocalizedName(name);
        item.setRegistryName(name);
        register(item, name);
        return item;
    }

    private static <T extends Item> T registerItem(T item) {
        String simpleName = item.getClass().getSimpleName().toLowerCase();
        if (item instanceof ItemBlock) {
            simpleName = ((ItemBlock) item).getBlock().getClass().getSimpleName().toLowerCase();
        }
        return registerItem(item, simpleName);
    }

    /*private static <T extends IForgeRegistryEntry> T registerItem(String modId, T item) {
        return registerItem(modId, item, item.getClass().getSimpleName());
    }


    private static <T extends IForgeRegistryEntry> T registerItem(String modId, T item, String name) {
        try {
            LoadController modController = (LoadController) Loader.class.getField("modController").get(Loader.INSTANCE());
            Object oldMod = modController.getClass().getField("activeContainer").get(modController);
            modController.getClass().getField("activeContainer").set(modController, Loader.INSTANCE().getIndexedModList().get(modId));

            register(item, name);

            modController.getClass().getField("activeContainer").set(modController, oldMod);
            return item;
        } catch (Exception exc) {
            AstralSorcery.log.error("Could not register item with name " + name);
            return null;
        }
    }*/

    private static <T extends IForgeRegistryEntry<T>> void register(T item, String name) {
        CommonProxy.registryPrimer.register(item);

        if (item instanceof Item) {
            registerItemInformations((Item) item, name);
            if(item instanceof ItemDynamicColor) {
                pendingDynamicColorItems.add((ItemDynamicColor) item);
            }
        }
    }

    private static <T extends Item> void registerItemInformations(T item, String name) {
        if (item instanceof IItemVariants) {
            for (int i = 0; i < ((IItemVariants) item).getVariants().length; i++) {
                int m = i;
                if (((IItemVariants) item).getVariantMetadatas() != null) {
                    m = ((IItemVariants) item).getVariantMetadatas()[i];
                }
                String vName = name + "_" + ((IItemVariants) item).getVariants()[i];
                if (((IItemVariants) item).getVariants()[i].equals("*")) {
                    vName = name;
                }
                AstralSorcery.proxy.registerItemRender(item, m, vName, true);
            }
        } else if(!(item instanceof ItemBlockCustomName)) {
            AstralSorcery.proxy.registerFromSubItems(item, name);
        }
    }

}
