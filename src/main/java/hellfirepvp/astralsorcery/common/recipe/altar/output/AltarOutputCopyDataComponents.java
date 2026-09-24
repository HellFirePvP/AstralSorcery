/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputCopyDataComponents
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputCopyDataComponents extends AltarOutputReplaceWithInput {

    public static final MapCodec<AltarOutputCopyDataComponents> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            StringRepresentable.fromEnum(AltarOutputReplaceWithInput.SlotType::values).fieldOf("slot_type").forGetter(AltarOutputCopyDataComponents::getSlotType),
            Codec.intRange(0, 25).fieldOf("input_slot").forGetter(AltarOutputCopyDataComponents::getInputSlot),
            Codec.BOOL.fieldOf("copy_all").forGetter(AltarOutputCopyDataComponents::isCopyAll),
            BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().listOf().fieldOf("components_to_copy").forGetter(AltarOutputCopyDataComponents::getComponentsToCopy)
    ).apply(inst, AltarOutputCopyDataComponents::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputCopyDataComponents> STREAM_CODEC = StreamCodec.composite(
            CodecUtil.enumStreamCodec(AltarOutputReplaceWithInput.SlotType.class),
            AltarOutputCopyDataComponents::getSlotType,
            ByteBufCodecs.INT,
            AltarOutputCopyDataComponents::getInputSlot,
            ByteBufCodecs.BOOL,
            AltarOutputCopyDataComponents::isCopyAll,
            ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE).apply(ByteBufCodecs.list()),
            AltarOutputCopyDataComponents::getComponentsToCopy,
            AltarOutputCopyDataComponents::new);
    public static final Type<AltarOutputCopyDataComponents> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final boolean copyAll;
    private final List<DataComponentType<?>> componentsToCopy;

    private AltarOutputCopyDataComponents(AltarOutputReplaceWithInput.SlotType slotType, int inputSlot, boolean copyAll, List<DataComponentType<?>> componentsToCopy) {
        super(slotType, inputSlot);
        this.copyAll = copyAll;
        this.componentsToCopy = componentsToCopy;
    }

    public static AltarOutputCopyDataComponents of(SlotType slotType, int inputSlot, Supplier<? extends DataComponentType<?>>... types) {
        return of(slotType, inputSlot, Stream.of(types).map(Supplier::get).toArray(DataComponentType[]::new));
    }

    public static AltarOutputCopyDataComponents of(SlotType slotType, int inputSlot, DataComponentType<?>... types) {
        return new AltarOutputCopyDataComponents(slotType, inputSlot, false, List.of(types));
    }

    public static AltarOutputCopyDataComponents ofAll(SlotType slotType, int inputSlot) {
        return new AltarOutputCopyDataComponents(slotType, inputSlot, true, List.of());
    }

    public boolean isCopyAll() {
        return this.copyAll;
    }

    public List<DataComponentType<?>> getComponentsToCopy() {
        return this.componentsToCopy;
    }

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        ItemStack source = super.modifyOutput(output, input, registries);
        if (this.isCopyAll()) {
            source.getComponents().forEach(typedComponent -> {
                output.set(typedComponent.type(), MiscUtil.cast(typedComponent.value()));
            });
        } else {
            this.getComponentsToCopy().forEach(type -> {
                if (source.has(type)) {
                    output.set(type, MiscUtil.cast(source.get(type)));
                }
            });
        }
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }
}
