/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.usage;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.types.LumenBindingUsageTypesAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBinding;
import hellfirepvp.astralsorcery.common.util.LumenUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.stream.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingUsage
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class LumenBindingUsage {

    public static final Codec<LumenBindingUsage> CODEC = RegistriesAS.REGISTRY_LUMEN_BINDING_USAGE_TYPES.byNameCodec()
            .dispatch(LumenBindingUsage::unwrapType, LumenBindingUsage.Type::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingUsage> STREAM_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_LUMEN_BINDING_USAGE_TYPES)
            .dispatch(LumenBindingUsage::unwrapType, LumenBindingUsage.Type::streamCodec);

    protected static <T extends LumenBindingUsage> Products.P2<RecordCodecBuilder.Mu<T>, Integer, Float> codecFields(RecordCodecBuilder.Instance<T> inst) {
        return inst.group(
                Codec.INT.fieldOf("lumen_cost").forGetter(LumenBindingUsage::getLumenCost),
                Codec.FLOAT.fieldOf("consumption_chance").forGetter(LumenBindingUsage::getConsumptionChance)
        );
    }

    public static final LumenBindingUsage NONE = new LumenBindingUsage(0, 0F) {
        @Override
        public DeferredType<?> getType() {
            return LumenBindingUsageTypesAS.NONE;
        }
    };
    protected final RandomSource rand = RandomSource.create();

    private final int lumenCost;
    private final float consumptionChance;

    protected LumenBindingUsage(int lumenCost, float consumptionChance) {
        this.lumenCost = lumenCost;
        this.consumptionChance = consumptionChance;
    }

    public abstract DeferredType<?> getType();

    public Type<?> unwrapType() {
        return this.getType().holder().get();
    }

    public int getLumenCost() {
        return this.lumenCost;
    }

    public float getConsumptionChance() {
        return this.consumptionChance;
    }

    private static void forEachUsedTypes(LivingEntity entity, ItemStack stack, BiConsumer<Lumen, LumenBinding> consumeFn) {
        StoredLumenComponent storedLumenCmp = stack.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY);
        if (storedLumenCmp.isEmpty()) return;
        LogicalSide side = SidedHelper.getSide(entity);

        for (Lumen bound : storedLumenCmp.getActiveBindings()) {
            storedLumenCmp.getBinding(side, bound)
                    .flatMap(bindingType -> bindingType.getActiveBinding(stack, entity))
                    .ifPresent(binding -> consumeFn.accept(bound, binding));
        }
    }

    public static <T extends LumenBindingUsage> void drainAll(LivingEntity entity, float costMultiplier, Class<T> usageType) {
        forEachUsage(entity, usageType, (stack, usage) -> {
            drainUsedLumen(entity, stack, usage, costMultiplier);
        });
    }

    protected static void drainUsedLumen(LivingEntity entity, ItemStack stack, LumenBindingUsage usage, float costMultiplier) {
        RandomSource rand = RandomSource.create();
        if (rand.nextFloat() < usage.getConsumptionChance()) {
            float drain = usage.getLumenCost() * costMultiplier;
            forEachUsedTypes(entity, stack, (lumen, binding) -> {
                if (drainLumen(stack, lumen, drain, rand, false)) {
                    drainLumen(stack, LumenAS.PRISMATIC.asLumen(), drain * 0.1F, rand, false);
                }
            });
        }
    }

    private static boolean drainLumen(ItemStack stack, Lumen lumen, float amountPart, RandomSource rand, boolean simulate) {
        return drainLumen(stack, lumen, simulate ? Mth.ceil(amountPart) : MiscUtil.roundChanced(amountPart, rand), simulate);
    }

    private static boolean drainLumen(ItemStack stack, Lumen lumen, int amount, boolean simulate) {
        return !LumenUtil.drainItem(stack, lumen.stack(amount), simulate ? ILumenHandler.Action.SIMULATE : ILumenHandler.Action.EXECUTE).isEmpty();
    }

    public static <T extends LumenBindingUsage> void forEachUsage(LivingEntity entity, Class<T> usageType, BiConsumer<ItemStack, T> consumer) {
        Streams.of(entity.getAllSlots())
                .filter(stack -> !stack.isEmpty())
                .forEach(stack -> forEachUsedTypes(entity, stack, (lumen, binding) -> {
                    List<LumenBindingUsage> usages = new ArrayList<>();
                    usages.add(binding.getLumenUsage());
                    if (binding.getLumenUsage() instanceof CombinedLumenBindingUsage combinedUsages) {
                        usages.addAll(combinedUsages.getUsages());
                    }
                    usages.forEach(usage -> {
                        if (usageType.isInstance(usage)) {
                            consumer.accept(stack, MiscUtil.cast(usage));
                        }
                    });
                }));
    }

    public record Type<T extends LumenBindingUsage>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {}

    public record DeferredType<T extends LumenBindingUsage>(DeferredHolder<Type<?>, Type<T>> holder) {}
}
