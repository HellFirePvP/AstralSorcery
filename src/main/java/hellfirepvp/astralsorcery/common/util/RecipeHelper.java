/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeHelper
 * Created by HellFirePvP
 * Date: 11.10.2019 / 22:30
 */
public class RecipeHelper {

    @Nullable
    public static SimpleAltarRecipe findAltarRecipeResult(Predicate<ItemStack> match) {
        for (SimpleAltarRecipe recipe : RecipeTypesAS.TYPE_ALTAR.getAllRecipes()) {
            if (match.test(recipe.getOutputForRender(Collections.emptyList()))) {
                return recipe;
            }
        }
        return null;
    }

    @Nonnull
    public static Optional<ItemStack> findSmeltingResult(World world, BlockState input) {
        ItemStack stack = ItemUtils.createBlockStack(input);
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        return findSmeltingResult(world, stack);
    }

    @Nonnull
    public static Optional<ItemStack> findSmeltingResult(World world, ItemStack input) {
        RecipeManager mgr = world.getRecipeManager();
        IInventory inv = new Inventory(input);
        Optional<IRecipe<IInventory>> optRecipe = (Optional<IRecipe<IInventory>>) ObjectUtils.firstNonNull(
                mgr.getRecipe(IRecipeType.SMELTING, inv, world),
                mgr.getRecipe(IRecipeType.CAMPFIRE_COOKING, inv, world),
                mgr.getRecipe(IRecipeType.SMOKING, inv, world),
                Optional.empty());
        return optRecipe.map(recipe -> recipe.getCraftingResult(inv).copy());
    }

    @Nullable
    public static RecipeManager getRecipeManager() {
        if (EffectiveSide.get() == LogicalSide.CLIENT) {
            return getClientManager();
        } else {
            MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
            if (srv != null) {
                return srv.getRecipeManager();
            }
        }
        return null;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    private static RecipeManager getClientManager() {
        ClientPlayNetHandler conn;
        if ((conn = Minecraft.getInstance().getConnection()) != null) {
            return conn.getRecipeManager();
        }
        return null;
    }

}
