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
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenLike;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.lumen.binding.data.LumenBindingTypeLoader;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenHandlerItemFactory;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StoredLumenDisplayTooltip
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record StoredLumenComponent(List<StoredLumen> storedLumen, List<StoredLumenDisplay> lumenDisplay, Properties properties, Map<Lumen, ResourceLocation> boundLumen) {

    public static final int DEFAULT_CAPACITY = 2000;
    public static final StoredLumenComponent EMPTY = new StoredLumenComponent(List.of(), List.of(), Properties.DEFAULT, Map.of());

    public static final Codec<StoredLumenComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            StoredLumen.CODEC.listOf().fieldOf("storedLumen").forGetter(StoredLumenComponent::storedLumen),
            StoredLumenDisplay.CODEC.listOf().fieldOf("lumenDisplay").forGetter(StoredLumenComponent::lumenDisplay),
            Properties.CODEC.fieldOf("properties").forGetter(StoredLumenComponent::properties),
            Codec.unboundedMap(RegistriesAS.REGISTRY_LUMEN.byNameCodec(), ResourceLocation.CODEC).fieldOf("lumenBindings").forGetter(StoredLumenComponent::boundLumen)
    ).apply(inst, StoredLumenComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StoredLumenComponent> STREAM_CODEC = StreamCodec.composite(
            StoredLumen.STREAM_CODEC.apply(ByteBufCodecs.list()),
            StoredLumenComponent::storedLumen,
            StoredLumenDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
            StoredLumenComponent::lumenDisplay,
            Properties.STREAM_CODEC,
            StoredLumenComponent::properties,
            ByteBufCodecs.map(size -> new HashMap<>(), ByteBufCodecs.registry(RegistriesAS.KEY_LUMEN), ResourceLocation.STREAM_CODEC),
            StoredLumenComponent::boundLumen,
            StoredLumenComponent::new);

    public StoredLumenComponent(List<StoredLumen> storedLumen, List<StoredLumenDisplay> lumenDisplay) {
        this(storedLumen, lumenDisplay, Properties.DEFAULT, Map.of());
    }

    public StoredLumenComponent(List<StoredLumen> storedLumen) {
        this(storedLumen, List.of());
    }

    public StoredLumenComponent updateLumenStack(Lumen lumen, int newAmount, int maxAmount) {
        List<StoredLumen> newList = new ArrayList<>(this.storedLumen);
        for (int i = 0; i < newList.size(); i++) {
            if (newList.get(i).lumen().equals(lumen)) {
                newList.set(i, new StoredLumen(lumen, newAmount, maxAmount));
                return new StoredLumenComponent(List.copyOf(newList), this.lumenDisplay, this.properties, this.boundLumen);
            }
        }
        newList.add(new StoredLumen(lumen, newAmount, maxAmount));
        return new StoredLumenComponent(List.copyOf(newList), this.lumenDisplay, this.properties, this.boundLumen);
    }

    public StoredLumenComponent updateLumenDisplay(ResourceKey<Lumen> lumen, Component message) {
        List<StoredLumenDisplay> newList = new ArrayList<>(this.lumenDisplay);
        for (int i = 0; i < newList.size(); i++) {
            StoredLumenDisplay existing = newList.get(i);
            if (existing.lumen().equals(lumen)) {
                newList.set(i, new StoredLumenDisplay(lumen, existing.alwaysShow, message));
                return new StoredLumenComponent(this.storedLumen, newList, this.properties, this.boundLumen);
            }
        }
        newList.add(new StoredLumenDisplay(lumen, false, message));
        return new StoredLumenComponent(this.storedLumen, newList, this.properties, this.boundLumen);
    }

    public StoredLumenComponent updateLumenAlwaysShow(ResourceKey<Lumen> lumen, boolean alwaysShow) {
        List<StoredLumenDisplay> newList = new ArrayList<>(this.lumenDisplay);
        for (int i = 0; i < newList.size(); i++) {
            StoredLumenDisplay existing = newList.get(i);
            if (existing.lumen().equals(lumen)) {
                newList.set(i, new StoredLumenDisplay(lumen, alwaysShow, existing.message));
                return new StoredLumenComponent(this.storedLumen, newList, this.properties, this.boundLumen);
            }
        }
        newList.add(new StoredLumenDisplay(lumen, alwaysShow, Component.empty()));
        return new StoredLumenComponent(this.storedLumen, newList, this.properties, this.boundLumen);
    }

    public StoredLumenComponent updateProperties(Properties properties) {
        return new StoredLumenComponent(this.storedLumen, this.lumenDisplay, properties, this.boundLumen);
    }

    public StoredLumenComponent updateProperties(UnaryOperator<Properties> propertiesFn) {
        return new StoredLumenComponent(this.storedLumen, this.lumenDisplay, propertiesFn.apply(this.properties), this.boundLumen);
    }

    public StoredLumenComponent applyBinding(Lumen lumen, ResourceLocation bindingTypeId) {
        if (this.boundLumen.containsKey(lumen)) return this;
        return new StoredLumenComponent(this.storedLumen, this.lumenDisplay, this.properties, Util.copyAndPut(this.boundLumen, lumen, bindingTypeId));
    }

    public StoredLumenComponent removeBinding(Lumen lumen) {
        if (!this.boundLumen.containsKey(lumen)) return this;
        Map<Lumen, ResourceLocation> newBound = new HashMap<>(this.boundLumen);
        newBound.remove(lumen);
        return new StoredLumenComponent(this.storedLumen, this.lumenDisplay, this.properties, Map.copyOf(newBound));
    }

    public List<Lumen> getActiveBindings() {
        List<Lumen> active = new ArrayList<>();
        for (Lumen bound : this.boundLumen.keySet()) {
            if (this.getStoredLumen(bound).map(StoredLumen::amount).filter(i -> i > 0).isEmpty()) continue;
            active.add(bound);
        }
        return active;
    }

    public ILumenHandler asHandlerAccess(ItemStack stack) {
        return LumenHandlerItemFactory.builder()
                .tankCapacity(lumen -> this.properties().capacity())
                .inputFilter((toAdd, existing) -> this.canStoreFromExistingLumen(toAdd.getLumen()))
                .extractFilter((amount, existing) -> this.properties().canDrain())
                .createHandler(stack);
    }

    @Nullable
    public static ILumenHandler getAsHandlerAccess(ItemStack stack) {
        StoredLumenComponent storedLumen = stack.get(DataComponentsAS.STORED_LUMEN);
        if (storedLumen == null) return null;
        return storedLumen.asHandlerAccess(stack);
    }

    public boolean isEmpty() {
        return this.storedLumen.stream().mapToInt(StoredLumen::amount).sum() <= 0 &&
                this.boundLumen().isEmpty();
    }

    public boolean canStoreFromExistingLumen(Lumen lumen) {
        if (!this.properties().canFill()) return false;
        if (!this.properties().accepted().isEmpty() && !this.properties().accepted().contains(lumen.getRegistryKey().orElse(null))) {
            return false;
        }
        if (!this.boundLumen().isEmpty()) {
            StoredLumen stored = this.getStoredLumen(lumen).orElse(null);
            if (stored == null) return false;
            if (stored.amount() <= 0 && !this.boundLumen().containsKey(lumen)) return false;
            return stored.amount() < stored.maxAmount();
        }
        return true;
    }

    public Optional<LumenBindingType> getBinding(LogicalSide side, LumenLike lumen) {
        return Optional.ofNullable(this.boundLumen.get(lumen.asLumen()))
                .flatMap(bindingTypeId -> LumenBindingTypeLoader.getInstance().getBindingType(side, bindingTypeId));
    }

    public boolean hasBinding(LumenLike lumen) {
        return this.boundLumen.containsKey(lumen.asLumen());
    }

    public Optional<StoredLumen> getStoredLumen(LumenLike lumen) {
        return this.storedLumen.stream()
                .filter(s -> s.lumen().equals(lumen.asLumen()))
                .findFirst();
    }

    public List<StoredLumen> getLumenDisplay() {
        List<StoredLumen> displayed = new ArrayList<>();
        for (StoredLumen stored : this.storedLumen()) {
            if (stored.amount() <= 0) {
                boolean shouldDisplay = this.lumenDisplay().stream()
                        .filter(d -> d.lumen().equals(stored.lumen().getRegistryKey().orElse(null)))
                        .findFirst()
                        .map(StoredLumenDisplay::alwaysShow)
                        .orElse(false);
                if (!this.boundLumen().containsKey(stored.lumen()) && !shouldDisplay) {
                    continue;
                }
            }
            displayed.add(stored);
        }
        return displayed;
    }

    public List<Component> getLumenDisplayTexts(LogicalSide side, ItemStack stack, Lumen lumen) {
        List<Component> display = new ArrayList<>();
        this.getBinding(side, lumen)
                .map(bindingType -> bindingType.getBinding(stack))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .ifPresent(b -> {
                    display.addAll(b.getDisplayText());
                    display.addAll(b.getEffect().getDisplayText(side, stack));
                });
        RegistriesAS.REGISTRY_LUMEN.getResourceKey(lumen).ifPresent(lumenKey -> {
            this.lumenDisplay.stream()
                    .filter(s -> s.lumen().equals(lumenKey))
                    .forEach(s -> display.add(s.message()));
        });
        return display;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StoredLumenComponent that = (StoredLumenComponent) o;
        return Objects.equals(properties, that.properties) &&
                Objects.equals(storedLumen, that.storedLumen) &&
                Objects.equals(lumenDisplay, that.lumenDisplay) &&
                Objects.equals(boundLumen, that.boundLumen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storedLumen, lumenDisplay, properties, boundLumen);
    }

    public record StoredLumen(Lumen lumen, int amount, int maxAmount) {

        public static final Codec<StoredLumen> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                RegistriesAS.REGISTRY_LUMEN.byNameCodec().fieldOf("lumen").forGetter(StoredLumen::lumen),
                Codec.INT.fieldOf("amount").forGetter(StoredLumen::amount),
                Codec.INT.fieldOf("maxAmount").forGetter(StoredLumen::maxAmount)
        ).apply(inst, StoredLumen::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, StoredLumen> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(RegistriesAS.KEY_LUMEN),
                StoredLumen::lumen,
                ByteBufCodecs.INT,
                StoredLumen::amount,
                ByteBufCodecs.INT,
                StoredLumen::maxAmount,
                StoredLumen::new
        );

        public static StoredLumen of(LumenStack stack, int maxAmount) {
            return new StoredLumen(stack.getLumen(), stack.getAmount(), maxAmount);
        }

        public LumenStack lumenStack() {
            return LumenStack.of(this.lumen(), this.amount());
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            StoredLumen that = (StoredLumen) o;
            return amount == that.amount && maxAmount == that.maxAmount && Objects.equals(lumen, that.lumen);
        }

        @Override
        public int hashCode() {
            return Objects.hash(lumen, amount, maxAmount);
        }
    }

    public record StoredLumenDisplay(ResourceKey<Lumen> lumen, boolean alwaysShow, Component message) {

        public static final Codec<StoredLumenDisplay> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResourceKey.codec(RegistriesAS.KEY_LUMEN).fieldOf("lumen").forGetter(StoredLumenDisplay::lumen),
                Codec.BOOL.fieldOf("alwaysShow").forGetter(StoredLumenDisplay::alwaysShow),
                ComponentSerialization.CODEC.fieldOf("message").forGetter(StoredLumenDisplay::message)
        ).apply(inst, StoredLumenDisplay::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, StoredLumenDisplay> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(RegistriesAS.KEY_LUMEN),
                StoredLumenDisplay::lumen,
                ByteBufCodecs.BOOL,
                StoredLumenDisplay::alwaysShow,
                ComponentSerialization.STREAM_CODEC,
                StoredLumenDisplay::message,
                StoredLumenDisplay::new
        );

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            StoredLumenDisplay that = (StoredLumenDisplay) o;
            return Objects.equals(lumen, that.lumen) && alwaysShow == that.alwaysShow && Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(lumen, alwaysShow, message);
        }
    }

    public record Properties(int capacity, boolean canDrain, boolean canFill, List<ResourceKey<Lumen>> accepted) {

        public static final Properties DEFAULT = new Properties(DEFAULT_CAPACITY, false, false, List.of());

        public static Codec<Properties> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("capacity").forGetter(Properties::capacity),
                Codec.BOOL.fieldOf("canDrain").forGetter(Properties::canDrain),
                Codec.BOOL.fieldOf("canFill").forGetter(Properties::canFill),
                ResourceKey.codec(RegistriesAS.KEY_LUMEN).listOf().fieldOf("accepted").forGetter(Properties::accepted)
        ).apply(inst, Properties::new));
        public static StreamCodec<RegistryFriendlyByteBuf, Properties> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                Properties::capacity,
                ByteBufCodecs.BOOL,
                Properties::canDrain,
                ByteBufCodecs.BOOL,
                Properties::canFill,
                ResourceKey.streamCodec(RegistriesAS.KEY_LUMEN).apply(ByteBufCodecs.list()),
                Properties::accepted,
                Properties::new
        );

        public Properties withCapacity(int capacity) {
            return new Properties(capacity, this.canDrain, this.canFill, this.accepted);
        }

        public Properties allowDrain() {
            return new Properties(this.capacity, true, this.canFill, this.accepted);
        }

        public Properties denyDrain() {
            return new Properties(this.capacity, false, this.canFill, this.accepted);
        }

        public Properties allowFill() {
            return new Properties(this.capacity, this.canDrain, true, this.accepted);
        }

        public Properties denyFill() {
            return new Properties(this.capacity, this.canDrain, false, this.accepted);
        }

        public Properties allowAccept(DeferredHolder<Lumen, ? extends Lumen> lumenHolder) {
            return this.allowAccept(lumenHolder.getKey());
        }

        public Properties allowAccept(ResourceKey<Lumen> lumen) {
            if (this.accepted.contains(lumen)) return this;
            List<ResourceKey<Lumen>> newAccepted = new ArrayList<>(this.accepted);
            newAccepted.add(lumen);
            return new Properties(this.capacity, this.canDrain, this.canFill, List.copyOf(newAccepted));
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Properties that = (Properties) o;
            return capacity == that.capacity && canFill == that.canFill && canDrain == that.canDrain && Objects.equals(accepted, that.accepted);
        }

        @Override
        public int hashCode() {
            return Objects.hash(capacity, canDrain, canFill, accepted);
        }
    }
}
