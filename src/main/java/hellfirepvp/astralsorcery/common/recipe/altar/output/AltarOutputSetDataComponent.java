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
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputSetDataComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputSetDataComponent<T> extends AltarRecipeOutputModifier {

    public static final MapCodec<AltarOutputSetDataComponent<?>> CODEC =
            DataComponentType.CODEC.dispatchMap("componentType", AltarOutputSetDataComponent::getComponentType, AltarOutputSetDataComponent::makeCodec);
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputSetDataComponent<?>> STREAM_CODEC =
            DataComponentType.STREAM_CODEC.dispatch(AltarOutputSetDataComponent::getComponentType, AltarOutputSetDataComponent::makeStreamCodec);
    public static final Type<AltarOutputSetDataComponent<?>> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private static MapCodec<AltarOutputSetDataComponent<?>> makeCodec(DataComponentType<?> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                type.codecOrThrow().fieldOf("value").forGetter(cmp -> MiscUtil.cast(cmp.getValue()))
        ).apply(instance, value -> new AltarOutputSetDataComponent<>(type, MiscUtil.cast(value))));
    }

    private static StreamCodec<RegistryFriendlyByteBuf, AltarOutputSetDataComponent<?>> makeStreamCodec(DataComponentType<?> type) {
        return StreamCodec.composite(
                DataComponentType.STREAM_CODEC,
                AltarOutputSetDataComponent::getComponentType,
                ByteBufCodecs.fromCodecWithRegistriesTrusted(type.codecOrThrow()),
                cmp -> MiscUtil.cast(cmp.getValue()),
                (cmp, val) -> new AltarOutputSetDataComponent<>(type, MiscUtil.cast(val)));
    }

    private final DataComponentType<T> componentType;
    private final T value;

    private AltarOutputSetDataComponent(DataComponentType<T> componentType, T value) {
        this.componentType = componentType;
        this.value = value;
    }

    public static <T> AltarOutputSetDataComponent<T> of(DataComponentType<T> componentType, T value) {
        return new AltarOutputSetDataComponent<>(componentType, value);
    }

    public DataComponentType<T> getComponentType() {
        return this.componentType;
    }

    public T getValue() {
        return this.value;
    }

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        output.set(this.componentType, this.value);
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }
}
