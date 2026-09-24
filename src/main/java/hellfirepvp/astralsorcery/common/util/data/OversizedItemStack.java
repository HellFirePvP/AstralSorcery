/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: OversizedItemStack
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class OversizedItemStack {

    public static final Codec<OversizedItemStack> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ItemStack.CODEC.fieldOf("stack").forGetter(OversizedItemStack::getStack),
            Codec.INT.fieldOf("count").forGetter(OversizedItemStack::getCount)
    ).apply(inst, OversizedItemStack::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, OversizedItemStack> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            OversizedItemStack::getStack,
            ByteBufCodecs.INT,
            OversizedItemStack::getCount,
            OversizedItemStack::new);

    private final ItemStack stack;
    private int count;

    public OversizedItemStack(ItemStack stack) {
        this(stack, stack.getCount());
    }

    public OversizedItemStack(ItemStack stack, int count) {
        this.stack = stack.copyWithCount(1);
        this.count = count;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public boolean matches(ItemStack stack) {
        return ItemStack.isSameItemSameComponents(this.getStack(), stack);
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void grow(int count) {
        this.count += count;
    }

    public OversizedItemStack copyWithCount(int newCount) {
        return new OversizedItemStack(this.stack, newCount);
    }

    public MutableComponent getDisplay() {
        Component name = Component.empty().append(this.stack.getHoverName());
        return Component.literal("%sx ".formatted(this.getCount())).append(name);
    }

    public List<ItemStack> decompose() {
        List<ItemStack> stacks = new ArrayList<>();
        int remaining = this.count;
        while (remaining > 0) {
            int toTake = Math.min(remaining, this.stack.getMaxStackSize());
            stacks.add(this.stack.copyWithCount(toTake));
            remaining -= toTake;
        }
        return stacks;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OversizedItemStack that = (OversizedItemStack) o;
        return count == that.count && ItemStack.isSameItemSameComponents(stack, that.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack, count);
    }
}
