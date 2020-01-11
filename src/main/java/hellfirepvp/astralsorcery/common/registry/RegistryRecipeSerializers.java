/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.custom.RecipeChangeWandColor;
import hellfirepvp.astralsorcery.common.crafting.serializer.BlockTransmutationSerializer;
import hellfirepvp.astralsorcery.common.crafting.serializer.LiquidInfusionSerializer;
import hellfirepvp.astralsorcery.common.crafting.serializer.SimpleAltarRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.serializer.WellRecipeSerializer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;

import static hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryRecipeSerializers
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:32
 */
public class RegistryRecipeSerializers {

    private RegistryRecipeSerializers() {}

    public static void init() {
        WELL_LIQUEFACTION_SERIALIZER = register(new WellRecipeSerializer());
        LIQUID_INFUSION_SERIALIZER = register(new LiquidInfusionSerializer());
        BLOCK_TRANSMUTATION_SERIALIZER = register(new BlockTransmutationSerializer());
        ALTAR_RECIPE_SERIALIZER = register(new SimpleAltarRecipeSerializer());

        CUSTOM_CHANGE_WAND_COLOR_SERIALIZER = register(new RecipeChangeWandColor.Serializer());
    }

    private static <C extends IInventory, R extends IRecipe<C>, T extends IRecipeSerializer<R>> T register(T serializer) {
        AstralSorcery.getProxy().getRegistryPrimer().register(serializer);
        return serializer;
    }

}
