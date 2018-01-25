/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.tool.ItemSkyResonator;
import hellfirepvp.astralsorcery.common.item.useables.ItemUsableDust;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResonatorLiquidRecipe
 * Created by HellFirePvP
 * Date: 12.11.2017 / 21:56
 */
public class ResonatorLiquidRecipe extends TraitRecipe {

    public ResonatorLiquidRecipe() {
        super(ShapedRecipe.Builder.newShapedRecipe("internal/altar/resonator/liquid",
                ItemSkyResonator.setCurrentUpgradeUnsafe(
                        ItemSkyResonator.setUpgradeUnlocked(
                                ItemSkyResonator.setEnhanced(new ItemStack(ItemsAS.skyResonator)),
                                ItemSkyResonator.ResonatorUpgrade.FLUID_FIELDS),
                        ItemSkyResonator.ResonatorUpgrade.FLUID_FIELDS))
                .addPart(ItemsAS.skyResonator,
                        ShapedRecipeSlot.CENTER)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemUsableDust.DustType.ILLUMINATION.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
        .unregisteredAccessibleShapedRecipe());

        setCstItem(ItemCraftingComponent.MetaType.STARDUST.asStack(),
                ConstellationAtlarSlot.UP_RIGHT_RIGHT,
                ConstellationAtlarSlot.UP_LEFT_LEFT);
        setInnerTraitItem(ItemUsableDust.DustType.ILLUMINATION.asStack(),
                TraitRecipeSlot.UPPER_CENTER);
        setCstItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                ConstellationAtlarSlot.DOWN_DOWN_LEFT,
                ConstellationAtlarSlot.DOWN_DOWN_RIGHT);

        addOuterTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack());
        addOuterTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack());
        addOuterTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack());
        setRequiredConstellation(Constellations.octans);
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler, boolean ignoreStarlightRequirement) {
        ItemStack center = invHandler.getStackInSlot(ShapedRecipeSlot.CENTER.getSlotID());
        if (center.isEmpty() || !(center.getItem() instanceof ItemSkyResonator)) {
            return false;
        }
        List<ItemSkyResonator.ResonatorUpgrade> out = ItemSkyResonator.getUpgrades(center);
        return !out.contains(ItemSkyResonator.ResonatorUpgrade.FLUID_FIELDS) && super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack resOut = super.getOutput(centralGridMap, altar);
        if(!resOut.isEmpty() && resOut.getItem() instanceof ItemSkyResonator) {
            ItemSkyResonator.setEnhanced(resOut);
            ItemSkyResonator.setUpgradeUnlocked(resOut, ItemSkyResonator.ResonatorUpgrade.FLUID_FIELDS);
        }
        return resOut;
    }
}
