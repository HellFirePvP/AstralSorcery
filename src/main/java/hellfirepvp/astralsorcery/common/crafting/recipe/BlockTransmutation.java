package hellfirepvp.astralsorcery.common.crafting.recipe;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomMatcherRecipe;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTransmutation
 * Created by HellFirePvP
 * Date: 10.10.2019 / 19:27
 */
public class BlockTransmutation extends CustomMatcherRecipe {

    private final Predicate<BlockState> inStateCheck;
    private final BlockState outState;
    private final double starlight;
    private final IWeakConstellation constellation;

    public BlockTransmutation(ResourceLocation recipeId, Predicate<BlockState> inStateCheck, BlockState outState, double starlight) {
        this(recipeId, inStateCheck, outState, starlight, null);
    }

    public BlockTransmutation(ResourceLocation recipeId, Predicate<BlockState> inStateCheck, BlockState outState, double starlight, @Nullable IWeakConstellation constellation) {
        super(recipeId);
        this.inStateCheck = inStateCheck;
        this.outState = outState;
        this.starlight = starlight;
        this.constellation = constellation;
    }

    public boolean matches(@Nonnull IWorld world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IWeakConstellation constellation) {
        return inStateCheck.test(state) && (this.constellation == null || this.constellation.equals(constellation));
    }

    public BlockState getOutput() {
        return outState;
    }

    public double getStarlightRequired() {
        return starlight;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializersAS.BLOCK_TRANSMUTATION_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return null;
    }
}
