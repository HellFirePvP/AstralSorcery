/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomMatcherRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.util.block.BlockMatchInformation;
import hellfirepvp.astralsorcery.common.util.object.PredicateBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTransmutation
 * Created by HellFirePvP
 * Date: 10.10.2019 / 19:27
 */
public class BlockTransmutation extends CustomMatcherRecipe {

    private final BlockState outputState;
    private final double starlight;
    private final IWeakConstellation constellation;

    private ItemStack outputDisplay;
    private List<BlockMatchInformation> stateCheck = new ArrayList<>();
    private Predicate<BlockState> matcher = null;

    public BlockTransmutation(ResourceLocation recipeId, BlockState outState, double starlight) {
        this(recipeId, outState, starlight, null);
    }

    public BlockTransmutation(ResourceLocation recipeId, BlockState outState, double starlight, @Nullable IWeakConstellation constellation) {
        super(recipeId);
        this.outputState = outState;
        this.starlight = starlight;
        this.constellation = constellation;
        this.outputDisplay = new ItemStack(outState.getBlock());
    }

    public boolean matches(@Nonnull IWorld world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IWeakConstellation constellation) {
        if (this.matcher == null) {
            this.matcher = PredicateBuilder.joinOr(stateCheck);
        }
        return this.matcher.test(state) && (this.constellation == null || this.constellation.equals(constellation));
    }

    public void addInputOption(BlockMatchInformation test) {
        this.matcher = null;
        this.stateCheck.add(test);
    }

    public List<BlockMatchInformation> getInputOptions() {
        return stateCheck;
    }

    @Nonnull
    public BlockState getOutput() {
        return outputState;
    }

    public void setOutputDisplay(@Nonnull ItemStack outputDisplay) {
        this.outputDisplay = outputDisplay;
    }

    @Nonnull
    public ItemStack getOutputDisplay() {
        return outputDisplay.copy();
    }

    public double getStarlightRequired() {
        return starlight;
    }

    @Nullable
    public IWeakConstellation getRequiredConstellation() {
        return constellation;
    }

    @Override
    public CustomRecipeSerializer<?> getSerializer() {
        return RecipeSerializersAS.BLOCK_TRANSMUTATION_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypesAS.TYPE_BLOCK_TRANSMUTATION.getType();
    }
}
