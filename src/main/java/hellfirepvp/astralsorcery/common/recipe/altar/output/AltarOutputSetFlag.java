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
import hellfirepvp.astralsorcery.common.component.FlagsComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputSetFlag
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputSetFlag extends AltarRecipeOutputModifier {

    public static final MapCodec<AltarOutputSetFlag> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            FlagsComponent.Flag.CODEC.fieldOf("flag").forGetter(AltarOutputSetFlag::getFlag)
    ).apply(inst, AltarOutputSetFlag::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputSetFlag> STREAM_CODEC = StreamCodec.composite(
            CodecUtil.enumStreamCodec(FlagsComponent.Flag.class),
            AltarOutputSetFlag::getFlag,
            AltarOutputSetFlag::new);
    public static final AltarRecipeOutputModifier.Type<AltarOutputSetFlag> TYPE = new AltarRecipeOutputModifier.Type<>(CODEC, STREAM_CODEC);

    private final FlagsComponent.Flag flag;

    public AltarOutputSetFlag(FlagsComponent.Flag flag) {
        this.flag = flag;
    }

    public static AltarOutputSetFlag of(FlagsComponent.Flag flag) {
        return new AltarOutputSetFlag(flag);
    }

    public FlagsComponent.Flag getFlag() {
        return this.flag;
    }

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        FlagsComponent flags = output.getOrDefault(DataComponentsAS.FLAGS, FlagsComponent.EMPTY);
        flags = flags.setFlag(this.flag);
        output.set(DataComponentsAS.FLAGS, flags);
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }
}
