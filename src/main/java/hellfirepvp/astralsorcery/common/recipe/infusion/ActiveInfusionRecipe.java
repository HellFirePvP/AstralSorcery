/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.infusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.ActiveRecipe;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.tile.TileInfuser;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.astralsorcery.common.util.data.LazyRecipeHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ActiveInfusionRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ActiveInfusionRecipe extends ActiveRecipe<InfusionRecipe> {

    public static final Codec<ActiveInfusionRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            LazyRecipeHolder.typedCodec(RecipeTypesAS.INFUSION_TYPE).fieldOf("recipe").forGetter(ActiveInfusionRecipe::getRecipeReference),
            Codec.INT.fieldOf("progressTick").forGetter(ActiveInfusionRecipe::getProgressTick),
            TileChalice.LiquidDrawInstance.CODEC.fieldOf("chaliceInput").forGetter(ActiveInfusionRecipe::getDrawInstance)
    ).apply(inst, ActiveInfusionRecipe::new));

    private int progressTick;
    private final TileChalice.LiquidDrawInstance drawInstance;

    private ActiveInfusionRecipe(LazyRecipeHolder<InfusionRecipe> lazyRecipeHolder,
                                 int progressTick,
                                 TileChalice.LiquidDrawInstance drawInstance) {
        super(lazyRecipeHolder);
        this.progressTick = progressTick;
        this.drawInstance = drawInstance;
    }

    private ActiveInfusionRecipe(RecipeHolder<InfusionRecipe> recipeHolder) {
        this(LazyRecipeHolder.of(recipeHolder), 0, TileChalice.LiquidDrawInstance.newInstance());
    }

    public static ActiveInfusionRecipe of(RecipeHolder<InfusionRecipe> recipeHolder) {
        return new ActiveInfusionRecipe(recipeHolder);
    }

    public int getProgressTick() {
        return this.progressTick;
    }

    public TileChalice.LiquidDrawInstance getDrawInstance() {
        return this.drawInstance;
    }

    public boolean isFinished(Level level) {
        return this.getRecipe(level).map(InfusionRecipe::getDuration)
                        .map(duration -> this.getProgressTick() >= duration)
                        .orElse(false);
    }

    public boolean matches(Level level, TileInfuser infuser) {
        InfusionRecipeInput input = infuser.createInput(level);
        return this.getRecipe(level).map(recipe -> {
            if (!recipe.matches(input, level)) return false;
            this.drawInstance.update(level, infuser.getBlockPos(), recipe.getChaliceInputFluidStack());
            return true;
        }).orElse(false);
    }

    public void tick(Level level) {
        this.progressTick++;
    }

    public boolean consumeInputs(TileInfuser infuser, Level level) {
        InfusionRecipeInput input = infuser.createInput(level);
        return this.getRecipe(level).map(recipe -> {
            if (recipe.getFluidConsumptionChance() <= 0) return true;

            FluidStack required = recipe.getChaliceInputFluidStack();
            if (this.drawInstance.consumeLiquid(level, infuser.getBlockPos(), required, true)) {
                this.drawInstance.consumeLiquid(level, infuser.getBlockPos(), required, false);
                return true;
            }

            return recipe.consumeInputs(input, level.registryAccess());
        }).orElse(false);
    }
}
