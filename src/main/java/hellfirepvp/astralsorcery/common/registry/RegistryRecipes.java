package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.ShapedLightProximityRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttenuationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.CollectorCrystalRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.LensRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.PrismLensRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.RecipeRitualPedestal;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.SimpleCrystalAttunationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade.AttenuationUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.CrystalToolRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TelescopeRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade.ConstellationUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.crafting.helper.SmeltingRecipe;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
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

    public static ShapedRecipe rMarbleRuned, rMarbleEngraved, rMarbleChiseled, rMarbleArch, rMarblePillar, rMarbleBricks;
    public static ShapedRecipe rBlackMarbleRaw;

    //LightProximityRecipes
    public static ShapedRecipe rLPRAltar;
    public static ShapedRecipe rLPRWand;
    public static ShapedRecipe rRJournal;

    //Ugh. Important machines/stuff
    public static DiscoveryRecipe rJournal;
    public static TelescopeRecipe rTelescope;
    public static GrindstoneRecipe rGrindstone;
    public static DiscoveryRecipe rAltar;
    public static RecipeRitualPedestal rRitualPedestalRock, rRitualPedestalCel;
    public static AttenuationRecipe rLightwell;
    public static AttenuationRecipe rIlluminatorRock, rIlluminatorCel;

    public static LensRecipe rLensRock, rLensCel;
    public static PrismLensRecipe rPrismRock, rPrismCel;
    public static CollectorCrystalRecipe rCollectRock, rCollectCel;

    public static AttenuationUpgradeRecipe rAltarUpgradeAttenuation;
    public static ConstellationUpgradeRecipe rAltarUpgradeConstellation;

    public static SimpleCrystalAttunationRecipe rAttuneRockCrystalBasic, rAttuneCelestialCrystalBasic;

    //CraftingComponents
    //public static DiscoveryRecipe rCCGlassLensRockCrystal, rCCGlassLensCelCrystal;
    public static DiscoveryRecipe rCCGlassLens;

    //Smelting
    public static SmeltingRecipe rSmeltStarmetalOre;

    //Tools
    public static CrystalToolRecipe rCToolRockPick, rCToolRockShovel, rCToolRockAxe, rCToolRockSword;
    public static CrystalToolRecipe rCToolCelPick, rCToolCelShovel, rCToolCelAxe, rCToolCelSword;

    public static DiscoveryRecipe rWand;
    public static ConstellationRecipe rLinkToolRock, rLinkToolCel;

    public static void init() {
        initVanillaRecipes();

        initAltarRecipes();
    }

    public static void initVanillaRecipes() {
        RecipeSorter.register("LightProximityCrafting", ShapedLightProximityRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        RecipeSorter.register("ShapedRecipeAdapter", AccessibleRecipeAdapater.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");

        CraftingManager manager = CraftingManager.getInstance();

        rLPRAltar = new ShapedRecipe(BlocksAS.blockAltar)
                .addPart(BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(Blocks.CRAFTING_TABLE,
                        ShapedRecipeSlot.CENTER);
        rLPRWand = new ShapedRecipe(ItemsAS.wand)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.RIGHT)
                .addPart(Items.ENDER_PEARL,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT);
        rRJournal = new ShapedRecipe(ItemsAS.journal)
                .addPart(ItemsAS.constellationPaper,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(Items.BOOK,
                        ShapedRecipeSlot.CENTER)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER);

        rBlackMarbleRaw = new ShapedRecipe(new ItemStack(BlocksAS.blockBlackMarble, 4, BlockBlackMarble.BlackMarbleBlockType.RAW.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(Items.COAL,
                        ShapedRecipeSlot.CENTER);

        rMarbleEngraved = new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 5, BlockMarble.MarbleBlockType.ENGRAVED.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.CENTER);
        rMarbleRuned = new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.RUNED.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER);
        rMarbleChiseled = new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.CHISELED.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER);
        rMarbleArch = new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.ARCH.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.UPPER_RIGHT);
        rMarblePillar = new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 2, BlockMarble.MarbleBlockType.PILLAR.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT);
        rMarbleBricks = new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.BRICKS.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.CENTER);

        rSmeltStarmetalOre = new SmeltingRecipe(ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack())
                .setInput(new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.STARMETAL.ordinal()))
                .setExp(2F);

        manager.addRecipe(rLPRAltar.makeLightProximityRecipe());
        manager.addRecipe(rLPRWand.makeLightProximityRecipe());
        manager.addRecipe(rRJournal.make());

        manager.addRecipe(rBlackMarbleRaw.make());
        manager.addRecipe(rMarbleArch.make());
        manager.addRecipe(rMarblePillar.make());
        manager.addRecipe(rMarbleRuned.make());
        manager.addRecipe(rMarbleEngraved.make());
        manager.addRecipe(rMarbleChiseled.make());
        manager.addRecipe(rMarbleBricks.make());

        rSmeltStarmetalOre.register();
    }

    public static void initAltarRecipes() {
        //ItemStack mRuned = new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RUNED.ordinal());
        //ItemStack mChisel = new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.CHISELED.ordinal());
        //ItemStack mEngraved = new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.ENGRAVED.ordinal());
        //ItemStack mBricks = new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.BRICKS.ordinal());
        //ItemStack mPillar = new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.PILLAR.ordinal());
        //ItemStack mArch = new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.ARCH.ordinal());
        //ItemStack mRaw = new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal());

        //ItemStack aquamarine = new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.AQUAMARINE.getItemMeta());

        rTelescope = registerAltarRecipe(new TelescopeRecipe());
        rGrindstone = registerAltarRecipe(new GrindstoneRecipe());

        rRitualPedestalRock = registerAltarRecipe(new RecipeRitualPedestal(false));
        rRitualPedestalCel = registerAltarRecipe(new RecipeRitualPedestal(true));

        rLensRock = registerAltarRecipe(new LensRecipe(false));
        rLensCel = registerAltarRecipe(new LensRecipe(true));

        rPrismRock = registerAltarRecipe(new PrismLensRecipe(false));
        rPrismCel = registerAltarRecipe(new PrismLensRecipe(true));

        rCollectRock = registerAltarRecipe(new CollectorCrystalRecipe(false));
        rCollectCel = registerAltarRecipe(new CollectorCrystalRecipe(true));

        rAltarUpgradeAttenuation = registerAltarRecipe(new AttenuationUpgradeRecipe());
        rAltarUpgradeConstellation = registerAltarRecipe(new ConstellationUpgradeRecipe());

        rAttuneRockCrystalBasic = registerAltarRecipe(new SimpleCrystalAttunationRecipe(ItemsAS.rockCrystal, ItemsAS.tunedRockCrystal));
        rAttuneCelestialCrystalBasic = registerAltarRecipe(new SimpleCrystalAttunationRecipe(ItemsAS.celestialCrystal, ItemsAS.tunedCelestialCrystal));

        rLinkToolRock = registerConstellationRecipe(new ShapedRecipe(ItemsAS.linkingTool)
                .addPart(Blocks.LOG,
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.RIGHT)
                .addPart(Items.STICK,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemsAS.rockCrystal,
                        ShapedRecipeSlot.UPPER_RIGHT))
        .setCstItem(Items.STICK,
                ConstellationRecipe.AltarAdditionalSlot.UP_UP_LEFT,
                ConstellationRecipe.AltarAdditionalSlot.DOWN_RIGHT_RIGHT)
        .setCstItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                ConstellationRecipe.AltarAdditionalSlot.UP_UP_RIGHT,
                ConstellationRecipe.AltarAdditionalSlot.UP_RIGHT_RIGHT);
        rLinkToolRock.setAttItem(Blocks.LOG, AttenuationRecipe.AltarSlot.LOWER_LEFT);

        rLinkToolCel = registerConstellationRecipe(new ShapedRecipe(ItemsAS.linkingTool)
                .addPart(Blocks.LOG,
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.RIGHT)
                .addPart(Items.STICK,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemsAS.celestialCrystal,
                        ShapedRecipeSlot.UPPER_RIGHT))
                .setCstItem(Items.STICK,
                        ConstellationRecipe.AltarAdditionalSlot.UP_UP_LEFT,
                        ConstellationRecipe.AltarAdditionalSlot.DOWN_RIGHT_RIGHT)
                .setCstItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ConstellationRecipe.AltarAdditionalSlot.UP_UP_RIGHT,
                        ConstellationRecipe.AltarAdditionalSlot.UP_RIGHT_RIGHT);
        rLinkToolCel.setAttItem(Blocks.LOG, AttenuationRecipe.AltarSlot.LOWER_LEFT);

        rLightwell = registerAttenuationRecipe(new ShapedRecipe(BlocksAS.blockWell)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemsAS.rockCrystal,
                        ShapedRecipeSlot.CENTER))
        .setAttItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                AttenuationRecipe.AltarSlot.values());
        rLightwell.setPassiveStarlightRequirement(3900);

        rIlluminatorRock = registerAttenuationRecipe(new ShapedRecipe(BlocksAS.blockIlluminator)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemsAS.rockCrystal,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER))
                .setAttItem(Items.GLOWSTONE_DUST, AttenuationRecipe.AltarSlot.values());
        rIlluminatorRock.setPassiveStarlightRequirement(3700);

        rIlluminatorCel = registerAttenuationRecipe(new ShapedRecipe(BlocksAS.blockIlluminator)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemsAS.celestialCrystal,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER))
        .setAttItem(Items.GLOWSTONE_DUST, AttenuationRecipe.AltarSlot.values());
        rIlluminatorCel.setPassiveStarlightRequirement(3700);

        rWand = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(ItemsAS.wand)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(Items.ENDER_PEARL,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT)));

        rWand.setPassiveStarlightRequirement(200);

        rJournal = registerDiscoveryRecipe(new ShapedRecipe(ItemsAS.journal)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(Items.BOOK,
                        ShapedRecipeSlot.CENTER)
                .addPart(ItemsAS.constellationPaper,
                        ShapedRecipeSlot.UPPER_CENTER));
        rJournal.setPassiveStarlightRequirement(20);

        registerDiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockBlackMarble, 4, BlockBlackMarble.BlackMarbleBlockType.RAW.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(Items.COAL,
                        ShapedRecipeSlot.CENTER)).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.RUNED.ordinal()))
                        .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                                ShapedRecipeSlot.UPPER_LEFT,
                                ShapedRecipeSlot.UPPER_RIGHT)
                        .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                                ShapedRecipeSlot.UPPER_CENTER))).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 5, BlockMarble.MarbleBlockType.ENGRAVED.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.CENTER))).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.CHISELED.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER))).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.ARCH.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.UPPER_RIGHT))).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 2, BlockMarble.MarbleBlockType.PILLAR.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT))).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.BRICKS.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.CENTER))).setPassiveStarlightRequirement(20);

        rAltar = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(BlocksAS.blockAltar)
                .addPart(Blocks.CRAFTING_TABLE,
                        ShapedRecipeSlot.CENTER)
                .addPart(BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_LEFT, ShapedRecipeSlot.LOWER_RIGHT)));
        rAltar.setPassiveStarlightRequirement(10);

        rCCGlassLens = registerDiscoveryRecipe(new ShapedRecipe(ItemCraftingComponent.MetaType.GLASS_LENS.asStack())
                .addPart(Blocks.GLASS_PANE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.CENTER));
        rCCGlassLens.setPassiveStarlightRequirement(100);

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
