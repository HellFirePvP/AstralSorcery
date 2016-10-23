package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.ShapedLightProximityRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttenuationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.SimpleCrystalAttunationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade.AttenuationUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.CrystalToolRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TelescopeRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade.ConstellationUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.RecipeSorter;

import static hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryRecipes
 * Created by HellFirePvP
 * Date: 31.07.2016 / 10:49
 */
public class RegistryRecipes {

    public static DiscoveryRecipe rMarbleRuned, rMarbleEngraved, rMarbleChiseled, rMarbleArch, rMarblePillar, rMarbleBricks;
    public static DiscoveryRecipe rBlackMarbleRaw;

    //Ugh. Important machines/stuff
    public static TelescopeRecipe rTelescope;
    public static GrindstoneRecipe rGrindstone;
    public static DiscoveryRecipe rAltar;
    public static AttenuationRecipe rRitualPedestalRock, rRitualPedestalCel;
    public static AttenuationRecipe rLightwell;

    public static AttenuationUpgradeRecipe rAltarUpgradeAttenuation;
    public static ConstellationUpgradeRecipe rAltarUpgradeConstellation;

    public static SimpleCrystalAttunationRecipe rAttuneRockCrystalBasic, rAttuneCelestialCrystalBasic;

    //CraftingComponents
    public static DiscoveryRecipe rCCGlassLensRockCrystal, rCCGlassLensCelCrystal;

    //Tools
    public static CrystalToolRecipe rCToolRockPick, rCToolRockShovel, rCToolRockAxe, rCToolRockSword;
    public static CrystalToolRecipe rCToolCelPick, rCToolCelShovel, rCToolCelAxe, rCToolCelSword;

    public static DiscoveryRecipe rWand;//, rLinkTool;

    public static void init() {
        initVanillaRecipes();

        initAltarRecipes();
    }

    public static void initVanillaRecipes() {
        RecipeSorter.register("LightProximityCrafting", ShapedLightProximityRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");

        CraftingManager manager = CraftingManager.getInstance();
        FurnaceRecipes furnace = FurnaceRecipes.instance();

        manager.addRecipe(new ShapedLightProximityRecipe(BlocksAS.blockAltar,
                "MCM", "MTM", "M M",
                'C', ItemsAS.rockCrystal,
                'M', new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                'T', Blocks.CRAFTING_TABLE));
        manager.addRecipe(new ShapedLightProximityRecipe(ItemsAS.wand,
                " AE", " MA", "M  ",
                'A', new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.AQUAMARINE.getItemMeta()),
                'E', Items.ENDER_PEARL,
                'M', new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal())));

        furnace.addSmeltingRecipe(new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.STARMETAL.ordinal()),
                new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.STARMETAL_INGOT.getItemMeta()), 2F);
    }

    public static void initAltarRecipes() {
        rTelescope = registerAltarRecipe(new TelescopeRecipe());
        rGrindstone = registerAltarRecipe(new GrindstoneRecipe());

        rAltarUpgradeAttenuation = registerAltarRecipe(new AttenuationUpgradeRecipe());
        rAltarUpgradeConstellation = registerAltarRecipe(new ConstellationUpgradeRecipe());

        rAttuneRockCrystalBasic = registerAltarRecipe(new SimpleCrystalAttunationRecipe(ItemsAS.rockCrystal, ItemsAS.tunedRockCrystal));
        rAttuneCelestialCrystalBasic = registerAltarRecipe(new SimpleCrystalAttunationRecipe(ItemsAS.celestialCrystal, ItemsAS.tunedCelestialCrystal));

        rLightwell = registerAttenuationRecipe(new ShapedRecipe(BlocksAS.blockWell)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RUNED.ordinal()),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.CHISELED.ordinal()),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemsAS.rockCrystal,
                        ShapedRecipeSlot.CENTER))
        .setItem(new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.AQUAMARINE.getItemMeta()),
                AttenuationRecipe.AltarSlot.values());
        rLightwell.setPassiveStarlightRequirement(3900);

        rRitualPedestalRock = registerAttenuationRecipe(new ShapedRecipe(BlocksAS.ritualPedestal)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RUNED.ordinal()),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.PILLAR.ordinal()),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.CHISELED.ordinal()),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemsAS.rockCrystal,
                        ShapedRecipeSlot.CENTER))
        .setItem(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.ENGRAVED.ordinal()),
                AttenuationRecipe.AltarSlot.UPPER_LEFT,
                AttenuationRecipe.AltarSlot.UPPER_RIGHT)
        .setItem(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.PILLAR.ordinal()),
                AttenuationRecipe.AltarSlot.LOWER_LEFT,
                AttenuationRecipe.AltarSlot.LOWER_RIGHT);
        rRitualPedestalRock.setPassiveStarlightRequirement(3000);

        rRitualPedestalCel = registerAttenuationRecipe(new ShapedRecipe(BlocksAS.ritualPedestal)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RUNED.ordinal()),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.PILLAR.ordinal()),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.CHISELED.ordinal()),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemsAS.celestialCrystal,
                        ShapedRecipeSlot.CENTER))
                .setItem(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.ENGRAVED.ordinal()),
                        AttenuationRecipe.AltarSlot.UPPER_LEFT,
                        AttenuationRecipe.AltarSlot.UPPER_RIGHT)
                .setItem(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.PILLAR.ordinal()),
                        AttenuationRecipe.AltarSlot.LOWER_LEFT,
                        AttenuationRecipe.AltarSlot.LOWER_RIGHT);
        rRitualPedestalCel.setPassiveStarlightRequirement(3000);

        /*rLinkTool = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(ItemsAS.linkingTool)
                .addPart(Items.STICK,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(ItemsAS.rockCrystal,
                        ShapedRecipeSlot.UPPER_RIGHT)));*/

        rWand = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(ItemsAS.wand)
                .addPart(new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.AQUAMARINE.getItemMeta()),
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(Items.ENDER_PEARL,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT)));

        rWand.setPassiveStarlightRequirement(200);

        rBlackMarbleRaw = registerDiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockBlackMarble, 4, BlockBlackMarble.BlackMarbleBlockType.RAW.ordinal()))
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(Items.COAL,
                        ShapedRecipeSlot.CENTER));
        rBlackMarbleRaw.setPassiveStarlightRequirement(1);

        rMarbleRuned = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.RUNED.ordinal()))
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.CHISELED.ordinal()),
                        ShapedRecipeSlot.UPPER_CENTER))
                );

        rMarbleEngraved = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 5, BlockMarble.MarbleBlockType.ENGRAVED.ordinal()))
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.CHISELED.ordinal()),
                        ShapedRecipeSlot.CENTER)));

        rMarbleChiseled = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.CHISELED.ordinal()))
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)));

        rMarbleArch = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.ARCH.ordinal()))
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.UPPER_RIGHT)));

        rMarblePillar = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 2, BlockMarble.MarbleBlockType.PILLAR.ordinal()))
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT)));

        rMarbleBricks = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.BRICKS.ordinal()))
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                        ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.CENTER)));

        rMarbleArch    .setPassiveStarlightRequirement(20);
        rMarbleBricks  .setPassiveStarlightRequirement(20);
        rMarbleChiseled.setPassiveStarlightRequirement(20);
        rMarbleEngraved.setPassiveStarlightRequirement(20);
        rMarblePillar  .setPassiveStarlightRequirement(20);
        rMarbleRuned   .setPassiveStarlightRequirement(20);

        rAltar = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(BlocksAS.blockAltar)
                .addPart(Blocks.CRAFTING_TABLE, ShapedRecipeSlot.CENTER)
                .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                        ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_LEFT, ShapedRecipeSlot.LOWER_RIGHT)));
        rAltar.setPassiveStarlightRequirement(10);

        rCCGlassLensRockCrystal = registerDiscoveryRecipe(new ShapedRecipe(new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.GLASS_LENS.getItemMeta()))
                .addPart(Blocks.GLASS_PANE, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT, ShapedRecipeSlot.LOWER_CENTER)
                .addPart(new ItemStack(ItemsAS.rockCrystal), ShapedRecipeSlot.CENTER));
        rCCGlassLensRockCrystal.setPassiveStarlightRequirement(200);

        rCCGlassLensCelCrystal = registerDiscoveryRecipe(new ShapedRecipe(new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.GLASS_LENS.getItemMeta()))
                .addPart(Blocks.GLASS_PANE, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT, ShapedRecipeSlot.LOWER_CENTER)
                .addPart(new ItemStack(ItemsAS.celestialCrystal), ShapedRecipeSlot.CENTER));
        rCCGlassLensCelCrystal.setPassiveStarlightRequirement(200);

        rCToolRockSword = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalSword)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER)
                        .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.CENTER, ShapedRecipeSlot.UPPER_CENTER),
                ShapedRecipeSlot.CENTER, ShapedRecipeSlot.UPPER_CENTER));
        rCToolRockShovel = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalShovel)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.UPPER_CENTER),
                ShapedRecipeSlot.UPPER_CENTER));
        rCToolRockPick = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalPickaxe)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.UPPER_RIGHT),
                ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.UPPER_RIGHT));
        rCToolRockAxe = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalAxe)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT),
                ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT));

        rCToolCelSword = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalSword)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER)
                        .addPart(ItemsAS.celestialCrystal, ShapedRecipeSlot.CENTER, ShapedRecipeSlot.UPPER_CENTER),
                ShapedRecipeSlot.CENTER, ShapedRecipeSlot.UPPER_CENTER));
        rCToolCelShovel = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalShovel)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.celestialCrystal, ShapedRecipeSlot.UPPER_CENTER),
                ShapedRecipeSlot.UPPER_CENTER));
        rCToolCelPick = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalPickaxe)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.celestialCrystal, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.UPPER_RIGHT),
                ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.UPPER_RIGHT));
        rCToolCelAxe = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalAxe)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.celestialCrystal, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT),
                ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT));
    }

}
