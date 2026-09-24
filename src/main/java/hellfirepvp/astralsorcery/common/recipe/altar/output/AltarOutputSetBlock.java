/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputSetBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputSetBlock extends AltarRecipeOutputModifier {

    public static final MapCodec<AltarOutputSetBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            WeightedEntry.Wrapper.codec(BlockState.CODEC).listOf().fieldOf("block_states").forGetter(AltarOutputSetBlock::getBlockStates)
    ).apply(inst, AltarOutputSetBlock::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputSetBlock> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(WeightedEntry.Wrapper.codec(BlockState.CODEC)).apply(ByteBufCodecs.list()),
            AltarOutputSetBlock::getBlockStates,
            AltarOutputSetBlock::new);
    public static final Type<AltarOutputSetBlock> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final WeightedRandomList<WeightedEntry.Wrapper<BlockState>> blockStates;
    private final RandomSource rand = RandomSource.create();

    private AltarOutputSetBlock(List<WeightedEntry.Wrapper<BlockState>> blockStates) {
        this.blockStates = WeightedRandomList.create(blockStates);
    }

    public List<WeightedEntry.Wrapper<BlockState>> getBlockStates() {
        return Collections.unmodifiableList(this.blockStates.unwrap());
    }

    public static AltarOutputSetBlock.Builder builder() {
        return new AltarOutputSetBlock.Builder();
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        this.blockStates.getRandom(this.rand).map(WeightedEntry.Wrapper::data).ifPresent(state -> {
            if (input.getAltar().getLevel() instanceof ServerLevel sLevel) {
                sLevel.setBlock(input.getAltar().getBlockPos(), state, Block.UPDATE_ALL);
            }
        });
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack modifyOutputForDisplay(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        return this.blockStates.getRandom(this.rand).map(WeightedEntry.Wrapper::data).map(ItemUtil::createBlockStack).orElse(output);
    }

    public static class Builder {

        private final List<WeightedEntry.Wrapper<BlockState>> blockStates = new ArrayList<>();

        public Builder add(Holder<? extends Block> block) {
            return this.add(block.value());
        }

        public Builder add(Block block) {
            return this.add(block.defaultBlockState(), 1);
        }

        public Builder add(BlockState state) {
            return this.add(state, 1);
        }

        public Builder add(Block block, int weight) {
            return this.add(block.defaultBlockState(), weight);
        }

        public Builder add(BlockState state, int weight) {
            this.blockStates.add(WeightedEntry.wrap(state, weight));
            return this;
        }

        public AltarOutputSetBlock build() {
            return new AltarOutputSetBlock(this.blockStates);
        }
    }
}
