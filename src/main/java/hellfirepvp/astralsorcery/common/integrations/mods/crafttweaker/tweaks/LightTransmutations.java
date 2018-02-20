/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import crafttweaker.CraftTweakerAPI;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.LightTransmutationAdd;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.LightTransmutationRemove;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import crafttweaker.api.item.IItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightTransmutations
 * Created by HellFirePvP
 * Date: 27.02.2017 / 11:32
 */
@ZenClass("mods.astralsorcery.LightTransmutation")
public class LightTransmutations extends BaseTweaker {

    protected static final String name = "AstralSorcery Starlight Transmutation";

    @ZenMethod
    public static void addTransmutation(IItemStack stackIn, IItemStack stackOut, double cost) {
        ItemStack in = convertToItemStack(stackIn);
        ItemStack out = convertToItemStack(stackOut);
        if(in.isEmpty() || out.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe due to invalid input/output.");
            return;
        }

        IBlockState state = ItemUtils.createBlockState(in);
        if(state == null) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe - Can't create a valid BlockState from given Input");
            return;
        }
        state = ItemUtils.createBlockState(out);
        if(state == null) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe - Can't create a valid BlockState from given Output");
            return;
        }

        ModIntegrationCrafttweaker.recipeModifications.add(new LightTransmutationAdd(in, out, cost));
    }

    @ZenMethod
    public static void removeTransmutation(IItemStack stackToRemove, boolean matchMeta) {
        ItemStack removeMatch = convertToItemStack(stackToRemove);
        if(removeMatch.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-removal due to invalid output.");
            return;
        }

        ModIntegrationCrafttweaker.recipeModifications.add(new LightTransmutationRemove(removeMatch, matchMeta));
    }

}
