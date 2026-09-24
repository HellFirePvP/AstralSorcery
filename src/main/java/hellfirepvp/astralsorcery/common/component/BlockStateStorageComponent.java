/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BlockStateStorageComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record BlockStateStorageComponent(List<BlockState> storedStates) implements DynamicTooltipComponent {

    public static final BlockStateStorageComponent EMPTY = new BlockStateStorageComponent(List.of());

    public static final Codec<BlockStateStorageComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockState.CODEC.listOf().optionalFieldOf("stored_states", List.of())
                    .forGetter(BlockStateStorageComponent::storedStates)
    ).apply(inst, BlockStateStorageComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockStateStorageComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistriesTrusted(BlockState.CODEC.listOf()),
            BlockStateStorageComponent::storedStates,
            BlockStateStorageComponent::new
    );

    public BlockStateStorageComponent(List<BlockState> storedStates) {
        this.storedStates = List.copyOf(storedStates);
    }

    public boolean hasStoredStates() {
        return !this.storedStates.isEmpty();
    }

    public BlockStateStorageComponent withAddedState(BlockState state) {
        return new BlockStateStorageComponent(Util.copyAndAdd(this.storedStates, state));
    }

    public BlockStateStorageComponent withClearedStates() {
        return new BlockStateStorageComponent(List.of());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BlockStateStorageComponent that = (BlockStateStorageComponent) o;
        return Objects.equals(storedStates, that.storedStates);
    }

    @Override
    public void addTooltips(ItemStack stack, Consumer<Component> tooltipAdder, AttributeTooltipContext context) {
        for (BlockState state : this.storedStates) {
            Component blockName = state.getBlock().getName();
            tooltipAdder.accept(blockName.copy().withColor(0xAAAAFF));
        }
    }
}
