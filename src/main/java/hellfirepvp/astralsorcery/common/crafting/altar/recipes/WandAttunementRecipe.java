/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WandAttunementRecipe
 * Created by HellFirePvP
 * Date: 04.08.2017 / 18:39
 */
public class WandAttunementRecipe extends TraitRecipe {

    private final IMajorConstellation cst;

    public WandAttunementRecipe(IMajorConstellation cst) {
        super(ShapedRecipe.Builder.newShapedRecipe("internal/altar/wand_att", ItemsAS.wand)
                .addPart(ItemsAS.wand,
                        ShapedRecipeSlot.CENTER)
        .unregisteredAccessibleShapedRecipe());
        List<ItemHandle> signature = cst.getConstellationSignatureItems();
        if(!signature.isEmpty()) {
            ItemHandle first = Iterables.getFirst(signature, null); //Never null.
            setInnerTraitItem(first, TraitRecipe.TraitRecipeSlot.values());
            for (ItemHandle s : signature) {
                addOuterTraitItem(s);
            }
        }
        this.cst = cst;
    }

}
