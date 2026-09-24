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
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputReplaceWithInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputReplaceWithInput extends AltarRecipeOutputModifier {

    public static final MapCodec<AltarOutputReplaceWithInput> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            StringRepresentable.fromEnum(SlotType::values).fieldOf("slot_type").forGetter(AltarOutputReplaceWithInput::getSlotType),
            Codec.intRange(0, 25).fieldOf("input_slot").forGetter(AltarOutputReplaceWithInput::getInputSlot)
    ).apply(inst, AltarOutputReplaceWithInput::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputReplaceWithInput> STREAM_CODEC = StreamCodec.composite(
            CodecUtil.enumStreamCodec(SlotType.class),
            AltarOutputReplaceWithInput::getSlotType,
            ByteBufCodecs.INT,
            AltarOutputReplaceWithInput::getInputSlot,
            AltarOutputReplaceWithInput::new);
    public static final Type<AltarOutputReplaceWithInput> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final SlotType slotType;
    private final int inputSlot;

    protected AltarOutputReplaceWithInput(SlotType slotType, int inputSlot) {
        this.slotType = slotType;
        this.inputSlot = inputSlot;
    }

    public static AltarOutputReplaceWithInput of(SlotType slotType, int inputSlot) {
        return new AltarOutputReplaceWithInput(slotType, inputSlot);
    }

    public AltarOutputReplaceWithInput.SlotType getSlotType() {
        return this.slotType;
    }

    public int getInputSlot() {
        return this.inputSlot;
    }

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        return (switch (this.slotType) {
            case RELAY_ITEM -> input.getRelayInputs().get(this.inputSlot);
            case ALTAR_GRID -> input.getGridInputs().get(this.inputSlot);
        }).copy();
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }

    public enum SlotType implements StringRepresentable {

        ALTAR_GRID,
        RELAY_ITEM;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
