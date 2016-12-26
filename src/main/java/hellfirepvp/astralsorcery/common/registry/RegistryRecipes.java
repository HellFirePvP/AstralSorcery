package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.ShapedLightProximityRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.CollectorCrystalRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.LensRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.PrismLensRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.RecipeRitualPedestal;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade.AttunementUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.CrystalToolRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TelescopeRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade.ConstellationUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.crafting.helper.SmeltingRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.item.ItemColoredLens;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
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
    public static AttunementRecipe rLightwell;
    public static AttunementRecipe rIlluminatorRock, rIlluminatorCel;
    public static AttunementRecipe rAttenuationAltarRelay;
    public static AttunementAltarRecipe rAttunementAltarRock, rAttunementAltarCel;

    public static LensRecipe rLensRock, rLensCel;
    public static PrismLensRecipe rPrismRock, rPrismCel;
    public static CollectorCrystalRecipe rCollectRock, rCollectCel;

    public static AttunementUpgradeRecipe rAltarUpgradeAttenuation;
    public static ConstellationUpgradeRecipe rAltarUpgradeConstellation;

    //public static SimpleCrystalAttunationRecipe rAttuneRockCrystalBasic, rAttuneCelestialCrystalBasic;

    //CraftingComponents
    //public static DiscoveryRecipe rCCGlassLensRockCrystal, rCCGlassLensCelCrystal;
    public static DiscoveryRecipe rCCGlassLens;
    public static AttunementRecipe rGlassLensFire, rGlassLensBreak, rGlassLensGrowth, rGlassLensDamage, rGlassLensRegeneration, rGlassLensNightvision;

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

        initInfusionRecipes();
    }

    public static void initInfusionRecipes() {
        InfusionRecipeRegistry.registerBasicInfusion(ItemCraftingComponent.MetaType.RESO_GEM.asStack(), ItemCraftingComponent.MetaType.AQUAMARINE.asStack());
    }

    public static void initVanillaRecipes() {
        RecipeSorter.register("LightProximityCrafting", ShapedLightProximityRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        RecipeSorter.register("ShapedRecipeAdapter", AccessibleRecipeAdapater.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");

        CraftingManager manager = CraftingManager.getInstance();

        rLPRAltar = new ShapedRecipe(BlocksAS.blockAltar)
                .addPart(BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.BLOCK_CRAFTING_TABLE,
                        ShapedRecipeSlot.CENTER);
        rLPRWand = new ShapedRecipe(ItemsAS.wand)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_ENDERPEARL,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.BLOCK_MARBLE,
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
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(Items.COAL,
                        ShapedRecipeSlot.CENTER);

        rMarbleEngraved = new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 5, BlockMarble.MarbleBlockType.ENGRAVED.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.CENTER);
        rMarbleRuned = new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.RUNED.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER);
        rMarbleChiseled = new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.CHISELED.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER);
        rMarbleArch = new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.ARCH.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.UPPER_RIGHT);
        rMarblePillar = new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 2, BlockMarble.MarbleBlockType.PILLAR.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT);
        rMarbleBricks = new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.BRICKS.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
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

        rAttunementAltarRock = registerAltarRecipe(new AttunementAltarRecipe(false));
        rAttunementAltarCel = registerAltarRecipe(new AttunementAltarRecipe(true));

        rAltarUpgradeAttenuation = registerAltarRecipe(new AttunementUpgradeRecipe());
        rAltarUpgradeConstellation = registerAltarRecipe(new ConstellationUpgradeRecipe());

        rGlassLensFire = registerAttenuationRecipe(new ShapedRecipe(ItemColoredLens.ColorType.FIRE.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER));
        rGlassLensFire.setAttItem(Items.BLAZE_POWDER, AttunementRecipe.AltarSlot.values());

        rGlassLensBreak = registerAttenuationRecipe(new ShapedRecipe(ItemColoredLens.ColorType.BREAK.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_DIAMOND,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT));
        rGlassLensBreak.setAttItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                AttunementRecipe.AltarSlot.UPPER_RIGHT,
                AttunementRecipe.AltarSlot.UPPER_LEFT);

        rGlassLensDamage = registerAttenuationRecipe(new ShapedRecipe(ItemColoredLens.ColorType.DAMAGE.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(Items.FLINT,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.ITEM_DIAMOND,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_IRON_INGOT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT));
        rGlassLensDamage.setAttItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                AttunementRecipe.AltarSlot.LOWER_LEFT,
                AttunementRecipe.AltarSlot.LOWER_RIGHT);

        rGlassLensGrowth = registerAttenuationRecipe(new ShapedRecipe(ItemColoredLens.ColorType.GROW.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(Items.WHEAT_SEEDS,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.ITEM_CARROT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_SUGARCANE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER));
        rGlassLensGrowth.setAttItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(), AttunementRecipe.AltarSlot.values());

        rGlassLensRegeneration = registerAttenuationRecipe(new ShapedRecipe(ItemColoredLens.ColorType.REGEN.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(Items.GHAST_TEAR,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_DIAMOND,
                        ShapedRecipeSlot.LOWER_CENTER));
        rGlassLensRegeneration.setAttItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                AttunementRecipe.AltarSlot.UPPER_LEFT,
                AttunementRecipe.AltarSlot.UPPER_RIGHT);

        rGlassLensNightvision = registerAttenuationRecipe(new ShapedRecipe(ItemColoredLens.ColorType.NIGHT.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(Items.GOLDEN_CARROT,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.ITEM_GLOWSTONE_DUST,
                        ShapedRecipeSlot.LOWER_CENTER));
        rGlassLensNightvision.setAttItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                AttunementRecipe.AltarSlot.UPPER_RIGHT,
                AttunementRecipe.AltarSlot.UPPER_LEFT);

        rAttenuationAltarRelay = registerAttenuationRecipe(new ShapedRecipe(BlocksAS.attunementRelay)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.BLOCK_WOOD_PLANKS,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER));

        rLinkToolRock = registerConstellationRecipe(new ShapedRecipe(ItemsAS.linkingTool)
                .addPart(OreDictAlias.BLOCK_WOOD_LOGS,
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_STICKS,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemsAS.rockCrystal,
                        ShapedRecipeSlot.UPPER_RIGHT))
        .setCstItem(OreDictAlias.ITEM_STICKS,
                ConstellationRecipe.AltarAdditionalSlot.UP_UP_LEFT,
                ConstellationRecipe.AltarAdditionalSlot.DOWN_RIGHT_RIGHT)
        .setCstItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                ConstellationRecipe.AltarAdditionalSlot.UP_UP_RIGHT,
                ConstellationRecipe.AltarAdditionalSlot.UP_RIGHT_RIGHT);
        rLinkToolRock.setAttItem(OreDictAlias.BLOCK_WOOD_LOGS, AttunementRecipe.AltarSlot.LOWER_LEFT);

        rLinkToolCel = registerConstellationRecipe(new ShapedRecipe(ItemsAS.linkingTool)
                .addPart(OreDictAlias.BLOCK_WOOD_LOGS,
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_STICKS,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemsAS.celestialCrystal,
                        ShapedRecipeSlot.UPPER_RIGHT))
                .setCstItem(OreDictAlias.ITEM_STICKS,
                        ConstellationRecipe.AltarAdditionalSlot.UP_UP_LEFT,
                        ConstellationRecipe.AltarAdditionalSlot.DOWN_RIGHT_RIGHT)
                .setCstItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ConstellationRecipe.AltarAdditionalSlot.UP_UP_RIGHT,
                        ConstellationRecipe.AltarAdditionalSlot.UP_RIGHT_RIGHT);
        rLinkToolCel.setAttItem(OreDictAlias.BLOCK_WOOD_LOGS, AttunementRecipe.AltarSlot.LOWER_LEFT);

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
        .setAttItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(), AttunementRecipe.AltarSlot.values());
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
                .setAttItem(OreDictAlias.ITEM_GLOWSTONE_DUST, AttunementRecipe.AltarSlot.values());
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
        .setAttItem(OreDictAlias.ITEM_GLOWSTONE_DUST, AttunementRecipe.AltarSlot.values());
        rIlluminatorCel.setPassiveStarlightRequirement(3700);

        rWand = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(ItemsAS.wand)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_ENDERPEARL,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.BLOCK_MARBLE,
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
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(Items.COAL,
                        ShapedRecipeSlot.CENTER)).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.RUNED.ordinal()))
                        .addPart(OreDictAlias.BLOCK_MARBLE,
                                ShapedRecipeSlot.UPPER_LEFT,
                                ShapedRecipeSlot.UPPER_RIGHT)
                        .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                                ShapedRecipeSlot.UPPER_CENTER))).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 5, BlockMarble.MarbleBlockType.ENGRAVED.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.CENTER))).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.CHISELED.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER))).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.ARCH.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.UPPER_RIGHT))).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 2, BlockMarble.MarbleBlockType.PILLAR.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT))).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.BRICKS.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.CENTER))).setPassiveStarlightRequirement(20);

        rAltar = registerAltarRecipe(new DiscoveryRecipe(new ShapedRecipe(BlocksAS.blockAltar)
                .addPart(OreDictAlias.BLOCK_CRAFTING_TABLE,
                        ShapedRecipeSlot.CENTER)
                .addPart(BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_LEFT, ShapedRecipeSlot.LOWER_RIGHT)));
        rAltar.setPassiveStarlightRequirement(10);

        rCCGlassLens = registerDiscoveryRecipe(new ShapedRecipe(ItemCraftingComponent.MetaType.GLASS_LENS.asStack())
                .addPart(OreDictAlias.BLOCK_GLASS_PANE_NOCOLOR,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.CENTER));
        rCCGlassLens.setPassiveStarlightRequirement(100);

        rCToolRockSword = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalSword)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER)
                        .addPart(ItemsAS.rockCrystal,
                                ShapedRecipeSlot.CENTER,
                                ShapedRecipeSlot.UPPER_CENTER),

                ShapedRecipeSlot.CENTER,
                ShapedRecipeSlot.UPPER_CENTER));

        rCToolRockShovel = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalShovel)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER,
                                ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.rockCrystal,
                                ShapedRecipeSlot.UPPER_CENTER),

                ShapedRecipeSlot.UPPER_CENTER));

        rCToolRockPick = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalPickaxe)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER,
                                ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.rockCrystal,
                                ShapedRecipeSlot.UPPER_LEFT,
                                ShapedRecipeSlot.UPPER_CENTER,
                                ShapedRecipeSlot.UPPER_RIGHT),

                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.UPPER_RIGHT));

        rCToolRockAxe = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalAxe)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER,
                                ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.rockCrystal,
                                ShapedRecipeSlot.UPPER_LEFT,
                                ShapedRecipeSlot.UPPER_CENTER,
                                ShapedRecipeSlot.LEFT),

                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LEFT));


        rCToolCelSword = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalSword)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER)
                        .addPart(ItemsAS.celestialCrystal,
                                ShapedRecipeSlot.CENTER,
                                ShapedRecipeSlot.UPPER_CENTER),

                ShapedRecipeSlot.CENTER,
                ShapedRecipeSlot.UPPER_CENTER));

        rCToolCelShovel = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalShovel)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER,
                                ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.celestialCrystal,
                                ShapedRecipeSlot.UPPER_CENTER),

                ShapedRecipeSlot.UPPER_CENTER));

        rCToolCelPick = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalPickaxe)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER,
                                ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.celestialCrystal,
                                ShapedRecipeSlot.UPPER_LEFT,
                                ShapedRecipeSlot.UPPER_CENTER,
                                ShapedRecipeSlot.UPPER_RIGHT),

                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.UPPER_RIGHT));

        rCToolCelAxe = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalAxe)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER,
                                ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.celestialCrystal,
                                ShapedRecipeSlot.UPPER_LEFT,
                                ShapedRecipeSlot.UPPER_CENTER,
                                ShapedRecipeSlot.LEFT),

                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LEFT));
    }

}
