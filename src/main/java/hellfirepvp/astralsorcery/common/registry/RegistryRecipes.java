/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.*;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.RecipeChangeWandColor;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.*;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade.AttunementUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade.ConstellationUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapelessRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.SmeltingRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.recipes.InfusionRecipeChargeTool;
import hellfirepvp.astralsorcery.common.item.ItemColoredLens;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.useables.ItemUsableDust;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import static hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry.*;
import static hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe.Builder.newShapedRecipe;
import static hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry.registerInfusionRecipe;
import static hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry.registerLowConsumptionInfusion;
import static hellfirepvp.astralsorcery.common.lib.RecipesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryRecipes
 * Created by HellFirePvP
 * Date: 31.07.2016 / 10:49
 */
public class RegistryRecipes {

    public static DiscoveryRecipe rJournal;
    public static DiscoveryRecipe rHandTelescope;
    public static TelescopeRecipe rTelescope;
    public static GrindstoneRecipe rGrindstone;
    public static RecipeRitualPedestal rRitualPedestal;
    public static DiscoveryRecipe rLightwell;
    public static DiscoveryRecipe rSkyResonator;
    public static DiscoveryRecipe rIlluminator;
    public static DiscoveryRecipe rAttenuationAltarRelay;
    public static AttunementAltarRecipe rAttunementAltar;
    public static ConstellationRecipe rStarlightInfuser;
    public static ConstellationRecipe rTreeBeacon;
    public static ConstellationRecipe rIlluminationWand;
    public static AttunementRecipe rArchitectWand;
    public static AttunementRecipe rExchangeWand;
    public static AttunementRecipe rGrappleWand;
    public static AttunementRecipe rShiftStar;
    public static ConstellationRecipe rRitualLink;
    public static DiscoveryRecipe rIlluminationPowder;
    public static DiscoveryRecipe rNocturnalPowder;
    public static CelestialGatewayRecipe rCelestialGateway;
    public static DrawingTableRecipe rDrawingTable;
    public static ConstellationRecipe rInfusedGlass;
    public static AttunementRecipe rKnowledgeShare;

    public static LensRecipe rLens;
    public static PrismLensRecipe rPrism;
    public static CollectorCrystalRecipe rCollectRock, rCollectCel;

    public static AttunementUpgradeRecipe rAltarUpgradeAttenuation;
    public static ConstellationUpgradeRecipe rAltarUpgradeConstellation;

    //CraftingComponents
    public static DiscoveryRecipe rCCGlassLens;
    public static ConstellationRecipe rGlassLensFire, rGlassLensBreak, rGlassLensGrowth,
            rGlassLensDamage, rGlassLensRegeneration, rGlassLensPush,
            rGlassLensSpectral;

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

    private static void initInfusionRecipes() {
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
        registerLowConsumptionInfusion(new ItemStack(Items.IRON_INGOT, 3, 0), new ItemStack(Blocks.IRON_ORE, 1, 0));
        registerLowConsumptionInfusion(new ItemStack(Items.GOLD_INGOT, 3, 0), new ItemStack(Blocks.GOLD_ORE, 1, 0));
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

    private static void initVanillaRecipes() {
        //RecipeSorter.register("LightProximityCrafting", ShapedLightProximityRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        //RecipeSorter.register("ShapedRecipeAdapter", AccessibleRecipeAdapater.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        //RecipeSorter.register("RecipeChangeIlluminationWandColor", RecipeChangeWandColor.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

        CommonProxy.registryPrimer.register(RecipeChangeWandColor.INSTANCE);

        rLPRAltar = newShapedRecipe("altar_tier_1", BlocksAS.blockAltar)
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
                        ShapedRecipeSlot.CENTER)
                .buildAndRegisterLightCraftingRecipe();
        rLPRWand = newShapedRecipe("tool_basicwand", ItemsAS.wand)
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_ENDERPEARL,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT)
                .buildAndRegisterLightCraftingRecipe();

        rRJournal = newShapedRecipe("journal", ItemsAS.journal)
                .addPart(ItemCraftingComponent.MetaType.PARCHMENT.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(Items.BOOK,
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .buildAndRegisterShapedRecipe();
        rCCParchment = newShapedRecipe("cc_parchment", ItemUtils.copyStackWithSize(ItemCraftingComponent.MetaType.PARCHMENT.asStack(), 4))
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.CENTER)
                .addPart(Items.PAPER,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .buildAndRegisterShapedRecipe();

        rBlackMarbleRaw = newShapedRecipe("marble_black_raw", new ItemStack(BlocksAS.blockBlackMarble, 8, BlockBlackMarble.BlackMarbleBlockType.RAW.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE, ShapedRecipeSlot.values())
                .addPart(Items.COAL, ShapedRecipeSlot.CENTER)
                .buildAndRegisterShapedRecipe();
        rMarbleArch = newShapedRecipe("marble_arch", new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.ARCH.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .buildAndRegisterShapedRecipe();
        rMarbleBricks = newShapedRecipe("marble_bricks", new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.BRICKS.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.CENTER)
                .buildAndRegisterShapedRecipe();
        rMarbleChiseled = newShapedRecipe("marble_chiseled", new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.CHISELED.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .buildAndRegisterShapedRecipe();
        rMarbleEngraved = newShapedRecipe("marble_engraved", new ItemStack(BlocksAS.blockMarble, 5, BlockMarble.MarbleBlockType.ENGRAVED.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.CENTER)
                .buildAndRegisterShapedRecipe();
        rMarbleRuned = newShapedRecipe("marble_runed", new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.RUNED.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .buildAndRegisterShapedRecipe();
        rMarblePillar = newShapedRecipe("marble_pillar", new ItemStack(BlocksAS.blockMarble, 2, BlockMarble.MarbleBlockType.PILLAR.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT)
                .buildAndRegisterShapedRecipe();
        rMarbleStairs = newShapedRecipe("marble_stairs", new ItemStack(BlocksAS.blockMarbleStairs, 4))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .buildAndRegisterShapedRecipe();
        rMarbleSlab = newShapedRecipe("marble_slab", new ItemStack(BlocksAS.blockMarbleSlab, 6, BlockMarbleSlab.EnumType.BRICKS.ordinal()))
                .addPart(BlockMarble.MarbleBlockType.BRICKS.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.UPPER_RIGHT)
        .buildAndRegisterShapedRecipe();

        rSmeltStarmetalOre = SmeltingRecipe.Builder.newSmelting("smelting_starmetal_ore", ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack())
                .setInput(new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.STARMETAL.ordinal()))
                .setExp(2F)
                .buildAndRegister();
        rSmeltAquamarineOre = SmeltingRecipe.Builder.newSmelting("smelting_aquamarine_ore", ItemUtils.copyStackWithSize(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(), 3))
                .setInput(new ItemStack(BlocksAS.customSandOre, 1, BlockCustomSandOre.OreType.AQUAMARINE.ordinal()))
                .setExp(1F)
                .buildAndRegister();
    }

    private static void initAltarRecipes() {
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
        rDrawingTable = registerAltarRecipe(new DrawingTableRecipe());

        rKnowledgeShare = registerAttenuationRecipe(newShapedRecipe("internal/altar/knowledgeshare", ItemsAS.knowledgeShare)
                .addPart(ItemCraftingComponent.MetaType.PARCHMENT.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(Items.FEATHER,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.getDyeOreDict(EnumDyeColor.BLACK),
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
        .unregisteredAccessibleShapedRecipe());
        rKnowledgeShare.setAttItem(ItemUsableDust.DustType.ILLUMINATION.asStack(), AttunementRecipe.AttunementAltarSlot.values());

        NonNullList<ItemStack> applicable = NonNullList.create();
        for (ItemColoredLens.ColorType type : ItemColoredLens.ColorType.values()) {
            applicable.add(type.asStack());
        }
        rInfusedGlass = registerConstellationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/infusedglass", ItemsAS.infusedGlass)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(new ItemHandle(applicable),
                        ShapedRecipeSlot.CENTER)
        .unregisteredAccessibleShapedRecipe());
        rInfusedGlass.setAttItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack(), AttunementRecipe.AttunementAltarSlot.values());
        rInfusedGlass.setCstItem(OreDictAlias.ITEM_STARMETAL_DUST,
                ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT);

        rGlassLensSpectral = registerConstellationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/lens_spectral", ItemColoredLens.ColorType.SPECTRAL.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(ItemUsableDust.DustType.ILLUMINATION.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rGlassLensSpectral.setAttItem(OreDictAlias.ITEM_STARMETAL_DUST, AttunementRecipe.AttunementAltarSlot.values());

        rIlluminationPowder = registerDiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/illuminationpowder", ItemUtils.copyStackWithSize(ItemUsableDust.DustType.ILLUMINATION.asStack(), 16))
                .addPart(OreDictAlias.ITEM_GLOWSTONE_DUST,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rIlluminationPowder.setPassiveStarlightRequirement(200);

        rNocturnalPowder = registerDiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/nocturnalpowder", ItemUtils.copyStackWithSize(ItemUsableDust.DustType.NOCTURNAL.asStack(), 4))
                .addPart(Items.COAL,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.getDyeOreDict(EnumDyeColor.BLACK),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemUsableDust.DustType.ILLUMINATION.asStack(),
                        ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rNocturnalPowder.setPassiveStarlightRequirement(300);

        rRitualLink = registerConstellationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/rituallink", new ItemStack(BlocksAS.ritualLink, 2))
                .addPart(OreDictAlias.ITEM_GOLD_NUGGET,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.UPPER_LEFT)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.BLOCK_GLASS_PANE_NOCOLOR,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .unregisteredAccessibleShapedRecipe());
        rRitualLink.setCstItem(OreDictAlias.ITEM_GOLD_NUGGET,
                ConstellationRecipe.ConstellationAtlarSlot.UP_UP_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT);
        rRitualLink.setCstItem(OreDictAlias.BLOCK_GLASS_PANE_NOCOLOR,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT);
        rRitualLink.setPassiveStarlightRequirement(2600);

        rGrappleWand = registerAttenuationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/tool_grapple", ItemsAS.grapplingWand)
                .addPart(OreDictAlias.ITEM_ENDERPEARL,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.ITEM_DYE_PURPLE,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST,
                        ShapedRecipeSlot.RIGHT)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT)
                .unregisteredAccessibleShapedRecipe());
        rGrappleWand.setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(),
                AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);
        rGrappleWand.setPassiveStarlightRequirement(1600);

        rArchitectWand = registerAttenuationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/tool_architect", ItemsAS.architectWand)
                .addPart(OreDictAlias.ITEM_DYE_PURPLE,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.RIGHT)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT)
                .unregisteredAccessibleShapedRecipe());
        rArchitectWand.setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(), AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);
        rArchitectWand.setPassiveStarlightRequirement(1600);

        rExchangeWand = registerAttenuationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/tool_exchange", ItemsAS.exchangeWand)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_DIAMOND,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LEFT)
        .unregisteredAccessibleShapedRecipe());
        rExchangeWand.setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(), AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);
        rExchangeWand.setPassiveStarlightRequirement(1600);

        rShiftStar = registerAttenuationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/tool_shiftstar", ItemsAS.shiftingStar)
                .addPart(ItemUsableDust.DustType.ILLUMINATION.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LOWER_RIGHT,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LOWER_LEFT)
                .addPart(BlocksAS.fluidLiquidStarlight,
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rShiftStar.setAttItem(OreDictAlias.ITEM_AQUAMARINE, AttunementRecipe.AttunementAltarSlot.values());

        rIlluminationWand = registerConstellationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/tool_illumination", ItemsAS.illuminationWand)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.RIGHT)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT)
                .unregisteredAccessibleShapedRecipe());
        rIlluminationWand.setCstItem(ItemHandle.getCrystalVariant(false, false),
                ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT);
        rIlluminationWand.setCstItem(OreDictAlias.ITEM_STARMETAL_DUST,
                ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT);
        rIlluminationWand.setAttItem(ItemUsableDust.DustType.ILLUMINATION.asStack(),
                AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);

        rSkyResonator = registerDiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/skyresonator", ItemsAS.skyResonator)
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(BlocksAS.fluidLiquidStarlight,
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rSkyResonator.setPassiveStarlightRequirement(100);

        rTreeBeacon = registerConstellationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/treebeacon", BlocksAS.treeBeacon)
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
                        ShapedRecipeSlot.UPPER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rTreeBeacon.setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(),
                AttunementRecipe.AttunementAltarSlot.LOWER_LEFT,
                AttunementRecipe.AttunementAltarSlot.LOWER_RIGHT);
        rTreeBeacon.setCstItem(BlockMarble.MarbleBlockType.RUNED.asStack(),
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT);
        rTreeBeacon.setPassiveStarlightRequirement(2000);

        rStarlightInfuser = registerConstellationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/starlightinfuser", BlocksAS.starlightInfuser)
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.ITEM_STARMETAL_INGOT,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(BlocksAS.fluidLiquidStarlight,
                        ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
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
        rStarlightInfuser.setPassiveStarlightRequirement(2500);

        rHandTelescope = registerDiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/handtelescope", ItemsAS.handTelescope)
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
                        ShapedRecipeSlot.LOWER_LEFT)
                .unregisteredAccessibleShapedRecipe());

        rGlassLensFire = registerConstellationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/lens_fire", ItemColoredLens.ColorType.FIRE.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        rGlassLensFire.setAttItem(Items.BLAZE_POWDER, AttunementRecipe.AttunementAltarSlot.values());
        rGlassLensFire.setCstItem(Items.BLAZE_POWDER,
                ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT);

        rGlassLensBreak = registerConstellationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/lens_break", ItemColoredLens.ColorType.BREAK.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_DIAMOND,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(Items.IRON_PICKAXE,
                        ShapedRecipeSlot.LOWER_CENTER)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        rGlassLensBreak.setAttItem(OreDictAlias.ITEM_AQUAMARINE,
                AttunementRecipe.AttunementAltarSlot.UPPER_RIGHT,
                AttunementRecipe.AttunementAltarSlot.UPPER_LEFT);
        rGlassLensBreak.setCstItem(OreDictAlias.ITEM_GOLD_INGOT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT);

        rGlassLensDamage = registerConstellationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/lens_damage", ItemColoredLens.ColorType.DAMAGE.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(Items.FLINT,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.ITEM_DIAMOND,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_IRON_INGOT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        rGlassLensDamage.setAttItem(OreDictAlias.ITEM_AQUAMARINE,
                AttunementRecipe.AttunementAltarSlot.LOWER_LEFT,
                AttunementRecipe.AttunementAltarSlot.LOWER_RIGHT);
        rGlassLensDamage.setCstItem(OreDictAlias.ITEM_IRON_INGOT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT);

        rGlassLensGrowth = registerConstellationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/lens_grow", ItemColoredLens.ColorType.GROW.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_CARROT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.UPPER_CENTER)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        rGlassLensGrowth.setAttItem(OreDictAlias.ITEM_AQUAMARINE, AttunementRecipe.AttunementAltarSlot.values());
        rGlassLensGrowth.setCstItem(OreDictAlias.ITEM_SUGARCANE,
                ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_UP_LEFT);
        rGlassLensGrowth.setCstItem(Items.WHEAT_SEEDS,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT);

        rGlassLensRegeneration = registerConstellationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/lens_regen", ItemColoredLens.ColorType.REGEN.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(Items.GHAST_TEAR,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_DIAMOND,
                        ShapedRecipeSlot.LOWER_CENTER)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        rGlassLensRegeneration.setAttItem(OreDictAlias.ITEM_AQUAMARINE,
                AttunementRecipe.AttunementAltarSlot.UPPER_LEFT,
                AttunementRecipe.AttunementAltarSlot.UPPER_RIGHT);
        rGlassLensRegeneration.setCstItem(OreDictAlias.ITEM_STARMETAL_DUST,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT,
                ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT);

        rGlassLensPush = registerConstellationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/lens_push", ItemColoredLens.ColorType.PUSH.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(Blocks.PISTON,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        rGlassLensPush.setAttItem(OreDictAlias.ITEM_GLOWSTONE_DUST, AttunementRecipe.AttunementAltarSlot.values());
        rGlassLensPush.setCstItem(OreDictAlias.ITEM_AQUAMARINE,
                ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT,
                ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT);

        rAttenuationAltarRelay = registerDiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/attunementrelay", BlocksAS.attunementRelay)
                .addPart(OreDictAlias.ITEM_GOLD_NUGGET,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.BLOCK_WOOD_PLANKS,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rAttenuationAltarRelay.setPassiveStarlightRequirement(250);

        rLinkTool = registerAttenuationRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/tool_linking", ItemsAS.linkingTool)
                .addPart(OreDictAlias.BLOCK_WOOD_LOGS,
                        ShapedRecipeSlot.LOWER_LEFT)
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_STICKS,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe())
        .setAttItem(OreDictAlias.BLOCK_WOOD_LOGS,
                AttunementRecipe.AttunementAltarSlot.LOWER_LEFT);
        rLinkTool.setPassiveStarlightRequirement(1000);

        rLightwell = registerDiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/lightwell", BlocksAS.blockWell)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rLightwell.setPassiveStarlightRequirement(200);

        rIlluminator = registerDiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/illuminator", BlocksAS.blockIlluminator)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemUsableDust.DustType.ILLUMINATION.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rIlluminator.setPassiveStarlightRequirement(600);

        rWand = registerAltarRecipe(new DiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/tool_basicwand", ItemsAS.wand)
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_ENDERPEARL,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_LEFT)
                .unregisteredAccessibleShapedRecipe()));

        rWand.setPassiveStarlightRequirement(200);

        rJournal = registerDiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/journal", ItemsAS.journal)
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(Items.BOOK,
                        ShapedRecipeSlot.CENTER)
                .addPart(ItemCraftingComponent.MetaType.PARCHMENT.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        rJournal.setPassiveStarlightRequirement(20);

        registerDiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/marble_black_raw", new ItemStack(BlocksAS.blockBlackMarble, 8, BlockBlackMarble.BlackMarbleBlockType.RAW.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE, ShapedRecipeSlot.values())
                .addPart(Items.COAL, ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe()).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/marble_runed", new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.RUNED.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(rMarbleStairs)).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/marble_engraved", new ItemStack(BlocksAS.blockMarble, 5, BlockMarble.MarbleBlockType.ENGRAVED.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/marble_chiseled", new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.CHISELED.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/marble_arch", new ItemStack(BlocksAS.blockMarble, 3, BlockMarble.MarbleBlockType.ARCH.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/marble_pillar", new ItemStack(BlocksAS.blockMarble, 2, BlockMarble.MarbleBlockType.PILLAR.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT)
                .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        registerAltarRecipe(new DiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/marble_bricks", new ItemStack(BlocksAS.blockMarble, 4, BlockMarble.MarbleBlockType.BRICKS.ordinal()))
                .addPart(OreDictAlias.BLOCK_MARBLE,
                        ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe())).setPassiveStarlightRequirement(20);

        rCCGlassLens = registerDiscoveryRecipe(ShapedRecipe.Builder.newShapedRecipe("internal/altar/glasslens", ItemCraftingComponent.MetaType.GLASS_LENS.asStack())
                .addPart(OreDictAlias.BLOCK_GLASS_PANE_NOCOLOR,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(OreDictAlias.ITEM_AQUAMARINE,
                        ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        rCCGlassLens.setPassiveStarlightRequirement(100);

        rCToolSword = registerAltarRecipe(new CrystalToolRecipe(
                ShapedRecipe.Builder.newShapedRecipe("internal/altar/crystaltool_sword", ItemsAS.crystalSword)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER)
                        .addPart(ItemHandle.getCrystalVariant(false, false),
                                ShapedRecipeSlot.CENTER,
                                ShapedRecipeSlot.UPPER_CENTER)
                        .unregisteredAccessibleShapedRecipe(),

                ShapedRecipeSlot.CENTER,
                ShapedRecipeSlot.UPPER_CENTER));

        rCToolShovel = registerAltarRecipe(new CrystalToolRecipe(
                ShapedRecipe.Builder.newShapedRecipe("internal/altar/crystaltool_shovel", ItemsAS.crystalShovel)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER,
                                ShapedRecipeSlot.CENTER)
                        .addPart(ItemHandle.getCrystalVariant(false, false),
                                ShapedRecipeSlot.UPPER_CENTER)
                        .unregisteredAccessibleShapedRecipe(),

                ShapedRecipeSlot.UPPER_CENTER));

        rCToolPick = registerAltarRecipe(new CrystalToolRecipe(
                ShapedRecipe.Builder.newShapedRecipe("internal/altar/crystaltool_pickaxe", ItemsAS.crystalPickaxe)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER,
                                ShapedRecipeSlot.CENTER)
                        .addPart(ItemHandle.getCrystalVariant(false, false),
                                ShapedRecipeSlot.UPPER_LEFT,
                                ShapedRecipeSlot.UPPER_CENTER,
                                ShapedRecipeSlot.UPPER_RIGHT)
                        .unregisteredAccessibleShapedRecipe(),

                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.UPPER_RIGHT));

        rCToolAxe = registerAltarRecipe(new CrystalToolRecipe(
                ShapedRecipe.Builder.newShapedRecipe("internal/altar/crystaltool_axe", ItemsAS.crystalAxe)
                        .addPart(OreDictAlias.ITEM_STICKS,
                                ShapedRecipeSlot.LOWER_CENTER,
                                ShapedRecipeSlot.CENTER)
                        .addPart(ItemHandle.getCrystalVariant(false, false),
                                ShapedRecipeSlot.UPPER_LEFT,
                                ShapedRecipeSlot.UPPER_CENTER,
                                ShapedRecipeSlot.LEFT)
                        .unregisteredAccessibleShapedRecipe(),

                ShapedRecipeSlot.UPPER_LEFT,
                ShapedRecipeSlot.UPPER_CENTER,
                ShapedRecipeSlot.LEFT));
    }

}
