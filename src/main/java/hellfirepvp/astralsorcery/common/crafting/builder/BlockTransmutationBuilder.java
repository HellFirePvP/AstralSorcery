/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeBuilder;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.util.block.BlockMatchInformation;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTransmutationBuilder
 * Created by HellFirePvP
 * Date: 07.03.2020 / 17:02
 */
public class BlockTransmutationBuilder extends CustomRecipeBuilder<BlockTransmutation> {

    private final ResourceLocation id;

    private BlockState outputState = Blocks.AIR.getDefaultState();
    private double starlight = 200.0D;
    private IWeakConstellation constellation = null;
    private ItemStack outputDisplay = ItemStack.EMPTY;
    private List<BlockMatchInformation> stateCheck = new ArrayList<>();

    private BlockTransmutationBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static BlockTransmutationBuilder builder(ForgeRegistryEntry<?> nameProvider) {
        return new BlockTransmutationBuilder(AstralSorcery.key(nameProvider.getRegistryName().getPath()));
    }

    public static BlockTransmutationBuilder builder(ResourceLocation id) {
        return new BlockTransmutationBuilder(id);
    }

    public BlockTransmutationBuilder multiplyStarlightCost(float multiply) {
        this.starlight *= multiply;
        return this;
    }

    public BlockTransmutationBuilder setStarlightCost(double starlight) {
        this.starlight = starlight;
        return this;
    }

    public BlockTransmutationBuilder setConstellation(IWeakConstellation constellation) {
        this.constellation = constellation;
        return this;
    }

    public BlockTransmutationBuilder addInputCheck(Block matchBlock) {
        return this.addInputCheck(matchBlock.getDefaultState());
    }

    public BlockTransmutationBuilder addInputCheck(BlockState matchState) {
        return this.addInputCheck(matchState, false);
    }

    public BlockTransmutationBuilder addInputCheck(BlockState matchState, boolean matchExact) {
        return this.addInputCheck(matchState, new ItemStack(matchState.getBlock()), matchExact);
    }

    public BlockTransmutationBuilder addInputCheck(BlockState matchState, IItemProvider display, boolean matchExact) {
        return this.addInputCheck(matchState, new ItemStack(display), matchExact);
    }

    public BlockTransmutationBuilder addInputCheck(BlockState matchState, ItemStack display, boolean matchExact) {
        this.stateCheck.add(new BlockMatchInformation(matchState, display, matchExact));
        return this;
    }

    public BlockTransmutationBuilder setOutput(Block output) {
        return this.setOutput(output.getDefaultState());
    }

    public BlockTransmutationBuilder setOutput(BlockState outputState) {
        this.outputState = outputState;
        this.outputDisplay = new ItemStack(outputState.getBlock());
        return this;
    }

    public BlockTransmutationBuilder setOutputDisplay(IItemProvider item) {
        return this.setOutputDisplay(new ItemStack(item));
    }

    public BlockTransmutationBuilder setOutputDisplay(ItemStack stack) {
        this.outputDisplay = stack.copy();
        return this;
    }

    @Nonnull
    @Override
    protected BlockTransmutation validateAndGet() {
        if (this.stateCheck.isEmpty()) {
            throw new IllegalArgumentException("No block input checks!");
        }
        for (BlockMatchInformation inputMatch : this.stateCheck) {
            if (inputMatch.getMatchState().getBlock() instanceof AirBlock) {
                throw new IllegalArgumentException("A block transmutation must not convert 'air' into something!");
            }
        }

        BlockTransmutation tr = new BlockTransmutation(this.id, this.outputState, this.starlight, this.constellation);
        this.stateCheck.forEach(tr::addInputOption);
        tr.setOutputDisplay(this.outputDisplay);
        return tr;
    }

    @Override
    protected CustomRecipeSerializer<BlockTransmutation> getSerializer() {
        return RecipeSerializersAS.BLOCK_TRANSMUTATION_SERIALIZER;
    }
}
