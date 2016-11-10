package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.MaterialAirish;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.ItemJournal;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockAltar;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockRitualPedestal;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalAxe;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalPickaxe;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalShovel;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalSword;
import hellfirepvp.astralsorcery.common.item.tool.ItemLinkingTool;
import hellfirepvp.astralsorcery.common.item.block.ItemCollectorCrystal;
import hellfirepvp.astralsorcery.common.item.ItemConstellationPaper;
import hellfirepvp.astralsorcery.common.item.ItemEntityPlacer;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemRockCrystalSimple;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedRockCrystal;
import hellfirepvp.astralsorcery.common.item.base.IItemVariants;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockCustomName;
import hellfirepvp.astralsorcery.common.item.tool.ItemWand;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

import static hellfirepvp.astralsorcery.common.lib.ItemsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryItems
 * Created by HellFirePvP
 * Date: 07.05.2016 / 15:03
 */
public class RegistryItems {

    public static Item.ToolMaterial crystalToolMaterial;
    public static EnumRarity rarityCelestial;
    public static Material materialTransparentReplaceable;

    public static CreativeTabs creativeTabAstralSorcery,
            creativeTabAstralSorceryPapers,
            creativeTabAstralSorceryTunedCrystals;

    public static void setupDefaults() {
        creativeTabAstralSorcery = new CreativeTabs(AstralSorcery.MODID) {
            @Override
            public Item getTabIconItem() {
                return ItemsAS.entityPlacer;
            }
        };
        creativeTabAstralSorceryPapers = new CreativeTabs(AstralSorcery.MODID + ".papers") {
            @Override
            public Item getTabIconItem() {
                return ItemsAS.constellationPaper;
            }
        };
        creativeTabAstralSorceryTunedCrystals = new CreativeTabs(AstralSorcery.MODID + ".crystals") {
            @Override
            public Item getTabIconItem() {
                return ItemsAS.tunedRockCrystal;
            }
        };

        crystalToolMaterial = EnumHelper.addToolMaterial("CRYSTAL", 3, 1000, 20.0F, 5.5F, 32);
        crystalToolMaterial.customCraftingMaterial = null;

        rarityCelestial = EnumHelper.addRarity("CELESTIAL", TextFormatting.BLUE, "Celestial");
        materialTransparentReplaceable = new MaterialAirish();
    }

    public static void init() {
        registerItems();

        registerBlockItems();
    }

    //"Normal" items
    private static void registerItems() {
        craftingComponent = registerItem(new ItemCraftingComponent());
        constellationPaper = registerItem(new ItemConstellationPaper());

        rockCrystal = registerItem(new ItemRockCrystalSimple());
        tunedRockCrystal = registerItem(new ItemTunedRockCrystal());

        celestialCrystal = registerItem(new ItemCelestialCrystal());
        tunedCelestialCrystal = registerItem(new ItemTunedCelestialCrystal());

        entityPlacer = registerItem(new ItemEntityPlacer());
        linkingTool = registerItem(new ItemLinkingTool());
        journal = registerItem(new ItemJournal());
        wand = registerItem(new ItemWand());

        crystalPickaxe = registerItem(new ItemCrystalPickaxe());
        crystalShovel = registerItem(new ItemCrystalShovel());
        crystalAxe = registerItem(new ItemCrystalAxe());
        crystalSword = registerItem(new ItemCrystalSword());
    }

    //Items associated to blocks/itemblocks
    private static void registerBlockItems() {
        RegistryBlocks.defaultItemBlocksToRegister.forEach(RegistryItems::registerDefaultItemBlock);

        registerItem(new ItemBlockCustomName(BlocksAS.customOre));
        registerItem(new ItemBlockCustomName(BlocksAS.customSandOre));
        registerItem(new ItemBlockCustomName(BlocksAS.blockMarble));
        registerItem(new ItemBlockCustomName(BlocksAS.blockBlackMarble));
        registerItem(new ItemBlockCustomName(BlocksAS.celestialCrystals));
        registerItem(new ItemBlockRitualPedestal());
        registerItem(new ItemBlockAltar());

        registerItem(new ItemCollectorCrystal(BlocksAS.collectorCrystal));
        registerItem(new ItemCollectorCrystal(BlocksAS.celestialCollectorCrystal));
    }

    private static <T extends Block> void registerDefaultItemBlock(T block) {
        registerDefaultItemBlock(block, block.getClass().getSimpleName());
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
        String simpleName = item.getClass().getSimpleName();
        if (item instanceof ItemBlock) {
            simpleName = ((ItemBlock) item).getBlock().getClass().getSimpleName();
        }
        return registerItem(item, simpleName);
    }

    /*private static <T extends IForgeRegistryEntry> T registerItem(String modId, T item) {
        return registerItem(modId, item, item.getClass().getSimpleName());
    }


    private static <T extends IForgeRegistryEntry> T registerItem(String modId, T item, String name) {
        try {
            LoadController modController = (LoadController) Loader.class.getField("modController").get(Loader.instance());
            Object oldMod = modController.getClass().getField("activeContainer").get(modController);
            modController.getClass().getField("activeContainer").set(modController, Loader.instance().getIndexedModList().get(modId));

            register(item, name);

            modController.getClass().getField("activeContainer").set(modController, oldMod);
            return item;
        } catch (Exception exc) {
            AstralSorcery.log.error("Could not register item with name " + name);
            return null;
        }
    }*/

    private static <T extends IForgeRegistryEntry> void register(T item, String name) {
        GameRegistry.register(item);

        if (item instanceof Item) {
            registerItemInformations((Item) item, name);
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
        } else {
            AstralSorcery.proxy.registerFromSubItems(item, name);
        }
    }

}
