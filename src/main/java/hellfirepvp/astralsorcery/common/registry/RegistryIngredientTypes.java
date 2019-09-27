/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.FluidIngredientSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import static hellfirepvp.astralsorcery.common.lib.IngredientSerializersAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryIngredientTypes
 * Created by HellFirePvP
 * Date: 30.05.2019 / 17:48
 */
public class RegistryIngredientTypes {

    private RegistryIngredientTypes() {}

    public static void init() {
        FLUID_SERIALIZER = new FluidIngredientSerializer();

        CraftingHelper.register(AstralSorcery.key("fluid"), FLUID_SERIALIZER);
    }

}
