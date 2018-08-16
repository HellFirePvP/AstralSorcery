/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.crafting.ISpecialCraftingEffects;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.useables.ItemShiftingStar;
import hellfirepvp.astralsorcery.common.item.useables.ItemUsableDust;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ReAttunementStarRecipe
 * Created by HellFirePvP
 * Date: 13.08.2018 / 20:38
 */
public class ReAttunementStarRecipe extends TraitRecipe implements ISpecialCraftingEffects {

    private final IMajorConstellation attuneCst;

    public ReAttunementStarRecipe(IMajorConstellation cst) {
        super(shapedRecipe("shiftingstar/enhanced/" + cst.getSimpleName(), ItemShiftingStar.createStack(cst))
                .addPart(cst.getConstellationSignatureItems().get(0),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemsAS.shiftingStar,
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_INGOT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .unregisteredAccessibleShapedRecipe());
        setInnerTraitItem(OreDictAlias.ITEM_STARMETAL_DUST,
                TraitRecipeSlot.values());

        addOuterTraitItem(OreDictAlias.ITEM_STARMETAL_DUST);
        addOuterTraitItem(ItemUsableDust.DustType.ILLUMINATION.asStack());
        addOuterTraitItem(cst.getConstellationSignatureItems().get(0));
        addOuterTraitItem(OreDictAlias.ITEM_STARMETAL_DUST);
        addOuterTraitItem(ItemUsableDust.DustType.ILLUMINATION.asStack());
        addOuterTraitItem(cst.getConstellationSignatureItems().get(0));

        //Better early than late.
        if (cst == null) {
            throw new IllegalArgumentException("NULL constellation passed to shifting-star enhancement recipe!");
        }

        this.attuneCst = cst;
        setRequiredConstellation(attuneCst);
    }

    @Override
    public AbstractAltarRecipe copyNewEffectInstance() {
        return new ReAttunementStarRecipe(this.attuneCst);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);


    }

}
