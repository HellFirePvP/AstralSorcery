/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.tome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPage;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageRecipe;
import hellfirepvp.astralsorcery.common.lib.types.TomePageTypesAS;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.RecipeFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomePageRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record TomePageRecipe(ResourceKey<RecipeType<?>> type, ResourceLocation recipeId) implements TomePage {

    //TODO modify to allow for dynamic resolving by output or id
    public static final MapCodec<TomePageRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceKey.codec(Registries.RECIPE_TYPE).fieldOf("recipe_type").forGetter(TomePageRecipe::type),
            ResourceLocation.CODEC.fieldOf("recipeId").forGetter(TomePageRecipe::recipeId)
    ).apply(inst, TomePageRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TomePageRecipe> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.RECIPE_TYPE),
            TomePageRecipe::type,
            ResourceLocation.STREAM_CODEC,
            TomePageRecipe::recipeId,
            TomePageRecipe::new);

    private static final TomePageText NOT_FOUND_PAGE = new TomePageText("tome.research.info.recipe_error");

    public static TomePageText getNotFoundPage() {
        return NOT_FOUND_PAGE;
    }

    public static TomePageRecipe of(DeferredHolder<RecipeType<?>, ? extends RecipeType<?>> type, ResourceLocation recipeId) {
        return of(type.getKey(), recipeId);
    }

    public static TomePageRecipe of(ResourceKey<RecipeType<?>> type, ResourceLocation recipeId) {
        return new TomePageRecipe(type, recipeId);
    }

    @Override
    public TomePageType<?> getType() {
        return TomePageTypesAS.RECIPE_PAGE.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderPage createPage(@Nullable ResearchNode node, int page) {
        return Optional.ofNullable(Minecraft.getInstance().getConnection())
                .map(ClientPacketListener::getRecipeManager)
                .flatMap(mgr -> {
                    RecipeType<?> type = BuiltInRegistries.RECIPE_TYPE.get(this.type());
                    if (type == null) return Optional.empty();
                    return RecipeFinder.of(mgr).findRecipe(MiscUtil.cast(type), this.recipeId());
                })
                .flatMap(recipe -> {
                    return RenderPageRecipe.getFactoryForType(this.type())
                            .map(factory -> (RenderPage) factory.create(node, page, this.recipeId()));
                }).orElseGet(() -> getNotFoundPage().createPage(node, page));
    }
}
