/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.CrystalIngredient;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.FluidIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IngredientSerializersAS
 * Created by HellFirePvP
 * Date: 30.05.2019 / 18:45
 */
public class IngredientSerializersAS {

    private IngredientSerializersAS() {}

    public static IIngredientSerializer<FluidIngredient> FLUID_SERIALIZER;
    public static IIngredientSerializer<CrystalIngredient> CRYSTAL_SERIALIZER;

}
