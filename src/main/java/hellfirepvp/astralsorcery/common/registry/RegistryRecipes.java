/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockCustomSandOre;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.ShapedLightProximityRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.*;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade.AttunementUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade.ConstellationUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.crafting.helper.SmeltingRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.recipes.InfusionRecipeChargeTool;
import hellfirepvp.astralsorcery.common.item.ItemColoredLens;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.RecipeSorter;

import static hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry.*;
import static hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry.registerInfusionRecipe;
import static hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry.registerLowConsumptionInfusion;

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

    public static DiscoveryRecipe rJournal;
    public static DiscoveryRecipe rHandTelescope;
    public static TelescopeRecipe rTelescope;
    public static GrindstoneRecipe rGrindstone;
    public static RecipeRitualPedestal rRitualPedestal;
    public static DiscoveryRecipe rLightwell;
    public static DiscoveryRecipe rSkyResonator;
    public static AttunementRecipe rIlluminator;
    public static DiscoveryRecipe rAttenuationAltarRelay;
    public static AttunementAltarRecipe rAttunementAltar;
    public static ConstellationRecipe rStarlightInfuser;
    public static ConstellationRecipe rTreeBeacon;
    public static ConstellationRecipe rIlluminationWand;
    public static AttunementRecipe rArchitectWand;
    public static AttunementRecipe rExchangeWand;
    public static ConstellationRecipe rShiftStar;
    public static ConstellationRecipe rRitualLink;
    public static DiscoveryRecipe rIlluminationPowder;
    public static CelestialGatewayRecipe rCelestialGateway;

    public static LensRecipe rLens;
    public static PrismLensRecipe rPrism;
    public static CollectorCrystalRecipe rCollectRock, rCollectCel;

    public static AttunementUpgradeRecipe rAltarUpgradeAttenuation;
    public static ConstellationUpgradeRecipe rAltarUpgradeConstellation;

    //CraftingComponents
    public static DiscoveryRecipe rCCGlassLens;
    public static ShapedRecipe rCCParchment;
    public static ConstellationRecipe rGlassLensFire, rGlassLensBreak, rGlassLensGrowth, rGlassLensDamage, rGlassLensRegeneration, rGlassLensPush;

    //Smelting
    public static SmeltingRecipe rSmeltStarmetalOre;
    public static SmeltingRecipe rSmeltAquamarineOre;

    //Tools
    public static CrystalToolRecipe rCToolPick, rCToolShovel, rCToolAxe, rCToolSword;

    public static DiscoveryRecipe rWand;
    public static AttunementRecipe rLinkTool;

    public static void init() {
        initVanillaRecipes();

        initAltarRecipes();

        initInfusionRecipes();

        InfusionRecipeRegistry.cacheLocalRecipes();
        AltarRecipeRegistry.cacheLocalRecipes();
    }

    public static void initInfusionRecipes() {
        registerLowConsumptionInfusion(ItemCraftingComponent.MetaType.RESO_GEM.asStack(), ItemCraftingComponent.MetaType.AQUAMARINE.asStack());
        registerLowConsumptionInfusion(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),         new ItemStack(Blocks.GLASS_PANE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Blocks.CLAY, 1, 0), new ItemStack(Blocks.SAND, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Blocks.GRASS, 1, 0), new ItemStack(Blocks.DIRT, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.ENDER_EYE, 1, 0), new ItemStack(Items.ENDER_PEARL, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.GLOWSTONE_DUST, 1, 0), new ItemStack(Items.GUNPOWDER, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.GUNPOWDER, 1, 0), new ItemStack(Items.REDSTONE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.DYE, 4, 15), new ItemStack(Items.BONE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.BLAZE_POWDER, 4, 0), new ItemStack(Items.BLAZE_ROD, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.MAGMA_CREAM, 1, 0), new ItemStack(Items.SLIME_BALL, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.GOLDEN_CARROT, 1, 0), new ItemStack(Items.CARROT, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.SPECKLED_MELON, 1, 0), new ItemStack(Items.MELON, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.IRON_INGOT, 2, 0), new ItemStack(Blocks.IRON_ORE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.GOLD_INGOT, 2, 0), new ItemStack(Blocks.GOLD_ORE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Blocks.LAPIS_BLOCK, 1, 0), new ItemStack(Blocks.LAPIS_ORE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Blocks.REDSTONE_BLOCK, 1, 0), new ItemStack(Blocks.REDSTONE_ORE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.DIAMOND, 4, 0), new ItemStack(Blocks.DIAMOND_ORE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.EMERALD, 4, 0), new ItemStack(Blocks.EMERALD_ORE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Blocks.ICE, 1, 0), new ItemStack(Blocks.GLASS, 1, 0));

        registerInfusionRecipe(new InfusionRecipeChargeTool(ItemsAS.chargedCrystalAxe));
        registerInfusionRecipe(new InfusionRecipeChargeTool(ItemsAS.chargedCrystalPickaxe));
        registerInfusionRecipe(new InfusionRecipeChargeTool(ItemsAS.chargedCrystalShovel));
        registerInfusionRecipe(new InfusionRecipeChargeTool(ItemsAS.chargedCrystalSword));
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
                .addPart(ItemCraftingComponent.MetaType.PARCHMENT.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(Items.BOOK,
                        ShapedRecipeSlot.CENTER)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER);
        rCCParchment = new ShapedRecipe(ItemUtils.copyStackWithSize(ItemCraftingComponent.MetaType.PARCHMENT.asStack(), 2))
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(Items.PAPER,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT);

        rBlackMarbleRaw = new ShapedRecipe(new ItemStack(BlocksAS.blockBlackMarble, 8, BlockBlackMarble.BlackMarbleBlockType.RAW.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE, ShapedRecipeSlot.values())
                .addPart(Items.COAL, ShapedRecipeSlot.CENTER);

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

        rSmeltAquamarineOre = new SmeltingRecipe(ItemUtils.copyStackWithSize(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(), 3))
                .setInput(new ItemStack(BlocksAS.customSandOre, 1, BlockCustomSandOre.OreType.AQUAMARINE.ordinal()))
                .setExp(1F);

        manager.addRecipe(rLPRAltar.makeLightProximityRecipe());
        manager.addRecipe(rLPRWand.makeLightProximityRecipe());
        manager.addRecipe(rRJournal.make());
        manager.addRecipe(rCCParchment.make());

        manager.addRecipe(rBlackMarbleRaw.make());
        manager.addRecipe(rMarbleArch.make());
        manager.addRecipe(rMarblePillar.make());
        manager.addRecipe(rMarbleRuned.make());
        manager.addRecipe(rMarbleEngraved.make());
        manager.addRecipe(rMarbleChiseled.make());
        manager.addRecipe(rMarbleBricks.make());

        rSmeltStarmetalOre.register();
        rSmeltAquamarineOre.register();
    }

    public static void initAltarRecipes() {
        rTelescope = registerAltarRecipe(new TelescopeRecipe());
        rGrindstone = registerAltarRecipe(new GrindstoneRecipe());
        rGrindstone.setPassiveStarlightRequirement(150);

        rRitualPedestal = registerAltarRecipe(new RecipeRitualPedestal());

        rLens = registerAltarRecipe(new LensRecipe());
        rLens.setPassiveStarlightRequirement(1000);

        rPrism = registerAltarRecipe(new PrismLensRecipe());

        rCollectRock = registerAltarRecipe(new CollectorCrystalRecipe(false));
        rCollectCel = registerAltarRecipe(new CollectorCrystalRecipe(true));

        rAttunementAltar = registerAltarRecipe(new AttunementAltarRecipe());

        rAltarUpgradeAttenuation = registerAltarRecipe(new AttunementUpgradeRecipe());
        rAltarUpgradeConstellation = registerAltarRecipe(new ConstellationUpgradeRecipe());

        DiscoveryRecipe dr = registerDiscoveryRecipe(rCCParchment);
        dr.setPassiveStarlightRequirement(50);

        rCelestialGateway = registerAltarRecipe(new CelestialGatewayRecipe());

        rIlluminationPowder = registerDiscoveryRecipe(new ShapedRecipe(new ItemStack(ItemsAS.illuminationPowder, 16))
                .addPart(OreDictAlias.ITEM_GLOWSTONE_DUST,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.CENTER));
        rIlluminationPowder.setPassiveStarlightRequirement(200);

        rRitualLink = registerConstellationRecipe(new ShapedRecipe(new ItemStack(BlocksAS.ritualLink, 2))
                .addPart(OreDictAlias.ITEM_GOLD_NUGGET,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.UPPER_LEFT)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemCraftingComponent.MetaType.STARDUST.asStack(),
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.BLOCK_GLASS_PANE_NOCOLOR,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT));
        rRitualLink.setCstItem(OreDictAlias.ITEM_GOLD_NUGGET,
                ConstellationRecipe.ConstellationAtlarSlot.UP_UP_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT);
        rRitualLink.setCstItem(OreDictAlias.BLOCK_GLASS_PANE_NOCOLOR,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT);
        rRitualLink.setPassiveStarlightRequirement(2600);

        rArchitectWand = registerAttenuationRecipe(new ShapedRecipe(ItemsAS.architectWand)
                .addPart(OreDictAlias.ITEM_DYE_PURPLE,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemCraftingComponent.MetaType.STARDUST.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.RIGHT)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT));
        rArchitectWand.setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(), AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);
        rArchitectWand.setPassiveStarlightRequirement(1600);

        rExchangeWand = registerAttenuationRecipe(new ShapedRecipe(ItemsAS.exchangeWand)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.CENTER)
                .addPart(ItemCraftingComponent.MetaType.STARDUST.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_DIAMOND,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LEFT));
        rExchangeWand.setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(), AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);
        rExchangeWand.setPassiveStarlightRequirement(1600);

        rShiftStar = registerConstellationRecipe(new ShapedRecipe(ItemsAS.shiftingStar)
                .addPart(OreDictAlias.ITEM_GLOWSTONE_DUST,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(BlocksAS.fluidLiquidStarlight,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LOWER_LEFT)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(ItemCraftingComponent.MetaType.STARDUST.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER));
        rShiftStar.setCstItem(BlockMarble.MarbleBlockType.RUNED.asStack(), ConstellationRecipe.ConstellationAtlarSlot.values());

        rIlluminationWand = registerConstellationRecipe(new ShapedRecipe(ItemsAS.illuminationWand)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemCraftingComponent.MetaType.STARDUST.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.RIGHT)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT));
        rIlluminationWand.setCstItem(ItemHandle.getCrystalVariant(false, false),
                ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT);
        rIlluminationWand.setCstItem(ItemCraftingComponent.MetaType.STARDUST.asStack(),
                ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT);
        rIlluminationWand.setAttItem(ItemsAS.illuminationPowder,
                AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);

        rSkyResonator = registerDiscoveryRecipe(new ShapedRecipe(ItemsAS.skyResonator)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(BlocksAS.fluidLiquidStarlight,
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT,
                        ShapedRecipeSlot.LOWER_CENTER));
        rSkyResonator.setPassiveStarlightRequirement(100);

        rTreeBeacon = registerConstellationRecipe(new ShapedRecipe(BlocksAS.treeBeacon)
                .addPart(OreDictAlias.BLOCK_LEAVES,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.BLOCK_SAPLING,
                        ShapedRecipeSlot.CENTER)
                .addPart(BlocksAS.fluidLiquidStarlight,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER));
        rTreeBeacon.setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(),
                AttunementRecipe.AttunementAltarSlot.LOWER_LEFT,
                AttunementRecipe.AttunementAltarSlot.LOWER_RIGHT);
        rTreeBeacon.setCstItem(BlockMarble.MarbleBlockType.RUNED.asStack(),
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT);
        rTreeBeacon.setPassiveStarlightRequirement(2000);

        rStarlightInfuser = registerConstellationRecipe(new ShapedRecipe(BlocksAS.starlightInfuser)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(BlocksAS.fluidLiquidStarlight,
                        ShapedRecipeSlot.CENTER));
        rStarlightInfuser.setCstItem(BlockMarble.MarbleBlockType.ENGRAVED.asStack(),
                ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT);
        rStarlightInfuser.setCstItem(BlockMarble.MarbleBlockType.PILLAR.asStack(),
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT);
        rStarlightInfuser.setAttItem(BlockMarble.MarbleBlockType.PILLAR.asStack(),
                AttunementRecipe.AttunementAltarSlot.LOWER_LEFT,
                AttunementRecipe.AttunementAltarSlot.LOWER_RIGHT);

        rHandTelescope = registerDiscoveryRecipe(new ShapedRecipe(ItemsAS.handTelescope)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.ITEM_STICKS,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT,
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.BLOCK_WOOD_PLANKS,
                        ShapedRecipeSlot.LOWER_LEFT));

        rGlassLensFire = registerConstellationRecipe(new ShapedRecipe(ItemColoredLens.ColorType.FIRE.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER));
        rGlassLensFire.setAttItem(Items.BLAZE_POWDER, AttunementRecipe.AttunementAltarSlot.values());
        rGlassLensFire.setCstItem(Items.BLAZE_POWDER,
                ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT);

        rGlassLensBreak = registerConstellationRecipe(new ShapedRecipe(ItemColoredLens.ColorType.BREAK.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_DIAMOND,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(Items.IRON_PICKAXE,
                        ShapedRecipeSlot.LOWER_CENTER));
        rGlassLensBreak.setAttItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                AttunementRecipe.AttunementAltarSlot.UPPER_RIGHT,
                AttunementRecipe.AttunementAltarSlot.UPPER_LEFT);
        rGlassLensBreak.setCstItem(OreDictAlias.ITEM_GOLD_INGOT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT);

        rGlassLensDamage = registerConstellationRecipe(new ShapedRecipe(ItemColoredLens.ColorType.DAMAGE.asStack())
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
                AttunementRecipe.AttunementAltarSlot.LOWER_LEFT,
                AttunementRecipe.AttunementAltarSlot.LOWER_RIGHT);
        rGlassLensDamage.setCstItem(OreDictAlias.ITEM_IRON_INGOT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT);

        rGlassLensGrowth = registerConstellationRecipe(new ShapedRecipe(ItemColoredLens.ColorType.GROW.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_CARROT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER));
        rGlassLensGrowth.setAttItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(), AttunementRecipe.AttunementAltarSlot.values());
        rGlassLensGrowth.setCstItem(OreDictAlias.ITEM_SUGARCANE,
                ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_UP_LEFT);
        rGlassLensGrowth.setCstItem(Items.WHEAT_SEEDS,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT);

        rGlassLensRegeneration = registerConstellationRecipe(new ShapedRecipe(ItemColoredLens.ColorType.REGEN.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(Items.GHAST_TEAR,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_DIAMOND,
                        ShapedRecipeSlot.LOWER_CENTER));
        rGlassLensRegeneration.setAttItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                AttunementRecipe.AttunementAltarSlot.UPPER_LEFT,
                AttunementRecipe.AttunementAltarSlot.UPPER_RIGHT);

        rGlassLensPush = registerConstellationRecipe(new ShapedRecipe(ItemColoredLens.ColorType.PUSH.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(Blocks.PISTON,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT));
        rGlassLensPush.setAttItem(OreDictAlias.ITEM_GLOWSTONE_DUST, AttunementRecipe.AttunementAltarSlot.values());
        rGlassLensPush.setCstItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT);

        rAttenuationAltarRelay = registerDiscoveryRecipe(new ShapedRecipe(BlocksAS.attunementRelay)
                .addPart(OreDictAlias.ITEM_GOLD_NUGGET,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.BLOCK_WOOD_PLANKS,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER));
        rAttenuationAltarRelay.setPassiveStarlightRequirement(250);

        rLinkTool = registerAttenuationRecipe(new ShapedRecipe(ItemsAS.linkingTool)
                .addPart(OreDictAlias.BLOCK_WOOD_LOGS,
                        ShapedRecipeSlot.LOWER_LEFT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_STICKS,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.CENTER))
                .setAttItem(OreDictAlias.BLOCK_WOOD_LOGS,
                        AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);
        rLinkTool.setPassiveStarlightRequirement(1000);

        rLightwell = registerDiscoveryRecipe(new ShapedRecipe(BlocksAS.blockWell)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.CENTER));
        rLightwell.setPassiveStarlightRequirement(200);

        rIlluminator = registerAttenuationRecipe(new ShapedRecipe(BlocksAS.blockIlluminator)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER))
                .setAttItem(ItemsAS.illuminationPowder, AttunementRecipe.AttunementAltarSlot.values());
        rIlluminator.setPassiveStarlightRequirement(1500);

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
                .addPart(ItemCraftingComponent.MetaType.PARCHMENT.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER));
        rJournal.setPassiveStarlightRequirement(20);

        registerDiscoveryRecipe(new ShapedRecipe(new ItemStack(BlocksAS.blockBlackMarble, 8, BlockBlackMarble.BlackMarbleBlockType.RAW.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE, ShapedRecipeSlot.values())
                .addPart(Items.COAL, ShapedRecipeSlot.CENTER)).setPassiveStarlightRequirement(20);

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

        rCCGlassLens = registerDiscoveryRecipe(new ShapedRecipe(ItemCraftingComponent.MetaType.GLASS_LENS.asStack())
                .addPart(OreDictAlias.BLOCK_GLASS_PANE_NOCOLOR,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.CENTER));
        rCCGlassLens.setPassiveStarlightRequirement(100);

        rCToolSword = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalSword)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER)
                        .addPart(ItemHandle.getCrystalVariant(false, false),
                                ShapedRecipeSlot.CENTER,
                                ShapedRecipeSlot.UPPER_CENTER),

                ShapedRecipeSlot.CENTER,
                ShapedRecipeSlot.UPPER_CENTER));

        rCToolShovel = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalShovel)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER,
                                ShapedRecipeSlot.CENTER)
                        .addPart(ItemHandle.getCrystalVariant(false, false),
                                ShapedRecipeSlot.UPPER_CENTER),

                ShapedRecipeSlot.UPPER_CENTER));

        rCToolPick = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalPickaxe)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER,
                                ShapedRecipeSlot.CENTER)
                        .addPart(ItemHandle.getCrystalVariant(false, false),
                                ShapedRecipeSlot.UPPER_LEFT,
                                ShapedRecipeSlot.UPPER_CENTER,
                                ShapedRecipeSlot.UPPER_RIGHT),

                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.UPPER_RIGHT));

        rCToolAxe = registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalAxe)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER,
                                ShapedRecipeSlot.CENTER)
                        .addPart(ItemHandle.getCrystalVariant(false, false),
                                ShapedRecipeSlot.UPPER_LEFT,
                                ShapedRecipeSlot.UPPER_CENTER,
                                ShapedRecipeSlot.LEFT),

                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LEFT));
    }

}
