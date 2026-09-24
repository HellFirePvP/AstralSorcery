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
import hellfirepvp.astralsorcery.common.util.data.OversizedItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StoredItemsComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record StoredItemsComponent(List<OversizedItemStack> contents) implements DynamicTooltipComponent {

    public static final StoredItemsComponent EMPTY = new StoredItemsComponent(List.of());

    public static final Codec<StoredItemsComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            OversizedItemStack.CODEC.listOf().fieldOf("contents").forGetter(StoredItemsComponent::contents)
    ).apply(inst, StoredItemsComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, StoredItemsComponent> STREAM_CODEC = StreamCodec.composite(
            OversizedItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            StoredItemsComponent::contents,
            StoredItemsComponent::new);

    public boolean isEmpty() {
        return this.contents.isEmpty() || this.contents().stream().mapToInt(OversizedItemStack::getCount).sum() <= 0;
    }

    public StoredItemsComponent accept(ItemStack stack) {
        if (stack.isEmpty()) {
            return this;
        }

        List<OversizedItemStack> newContents = new ArrayList<>(this.contents());
        for (int i = 0; i < newContents.size(); i++) {
            OversizedItemStack stored = newContents.get(i);
            if (stored.matches(stack)) {
                newContents.set(i, stored.copyWithCount(stored.getCount() + stack.getCount()));
                newContents.sort(Comparator.comparing(OversizedItemStack::getCount));
                return new StoredItemsComponent(newContents);
            }
        }
        newContents.add(new OversizedItemStack(stack, stack.getCount()));
        newContents.sort(Comparator.comparing(OversizedItemStack::getCount));
        return new StoredItemsComponent(newContents);
    }

    public List<ItemStack> decomposeAll() {
        List<ItemStack> decomposed = new ArrayList<>();
        for (OversizedItemStack stored : this.contents()) {
            decomposed.addAll(stored.decompose());
        }
        return decomposed;
    }

    public Tuple<StoredItemsComponent, List<ItemStack>> decomposeStackCount(int stacks) {
        List<ItemStack> removed = new ArrayList<>();
        List<OversizedItemStack> newContents = new ArrayList<>();
        boolean complete = stacks <= 0;

        for (OversizedItemStack stored : this.contents()) {
            if (complete) {
                newContents.add(stored);
                continue;
            }

            List<ItemStack> stacksFromEntry = stored.decompose();
            int needed = stacks - removed.size();
            if (stacksFromEntry.size() <= needed) {
                removed.addAll(stacksFromEntry);
                if (removed.size() >= stacks) {
                    complete = true;
                }
                continue;
            }

            int consumedCount = 0;
            for (int i = 0; i < needed; i++) {
                ItemStack taken = stacksFromEntry.get(i);
                removed.add(taken);
                consumedCount += taken.getCount();
            }
            int remainingCount = stored.getCount() - consumedCount;
            if (remainingCount > 0) {
                newContents.add(stored.copyWithCount(remainingCount));
            }
            complete = true;
        }

        newContents.sort(Comparator.comparing(OversizedItemStack::getCount));
        return new Tuple<>(new StoredItemsComponent(newContents), removed);
    }

    @Override
    public void addTooltips(ItemStack stack, Consumer<Component> tooltipAdder, AttributeTooltipContext context) {
        this.contents.subList(0, Math.min(12, this.contents.size()))
                .forEach(overSizedStack -> tooltipAdder.accept(overSizedStack.getDisplay().withStyle(ChatFormatting.GRAY)));
        if (this.contents.size() > 12) {
            tooltipAdder.accept(Component.literal("...").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StoredItemsComponent that = (StoredItemsComponent) o;
        return Objects.equals(contents, that.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(contents);
    }
}
