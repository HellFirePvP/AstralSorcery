/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiPredicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResolvingRecipeType
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:52
 */
public class ResolvingRecipeType<C extends IItemHandler, T extends IHandlerRecipe<C>, R extends RecipeCraftingContext<T, C>> {

    private final ResourceLocation id;
    private final Class<T> baseClass;
    private final BiPredicate<T, R> matchFct;
    private final IRecipeType<T> type;

    public ResolvingRecipeType(String name, Class<T> baseClass, BiPredicate<T, R> matchFct) {
        this(new ResourceLocation(AstralSorcery.MODID, name), baseClass, matchFct);
    }

    public ResolvingRecipeType(ResourceLocation id, Class<T> baseClass, BiPredicate<T, R> matchFct) {
        this.id = id;
        this.baseClass = baseClass;
        this.matchFct = matchFct;
        this.type = new IRecipeType<T>() {
            @Override
            public String toString() {
                return ResolvingRecipeType.this.id.getPath();
            }
        };
        Registry.register(Registry.RECIPE_TYPE, this.id, this.type);
    }

    @Nonnull
    public Collection<T> getAllRecipes(Dist dist) {
        RecipeManager mgr = null;
        if (dist.isClient()) {
            mgr = getClientManager();
        } else {
            MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
            if (srv != null) {
                mgr = srv.getRecipeManager();
            }
        }
        if (mgr == null) {
            return Collections.emptyList();
        }
        return (Collection<T>) mgr.getRecipes(this.type).values();
    }

    public final Class<T> getBaseClass() {
        return baseClass;
    }

    @Nullable
    public T findRecipe(Dist dist, R context) {
        return MiscUtils.iterativeSearch(this.getAllRecipes(dist), (recipe) -> this.matchFct.test(recipe, context));
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    private RecipeManager getClientManager() {
        ClientPlayNetHandler conn;
        if ((conn = Minecraft.getInstance().getConnection()) != null) {
            return conn.getRecipeManager();
        }
        return null;
    }

}
