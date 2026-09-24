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
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputSetCrystalCount
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputSetCrystalCount extends AltarRecipeOutputModifier {

    public static final MapCodec<AltarOutputSetCrystalCount> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.fieldOf("count_per_size").forGetter(AltarOutputSetCrystalCount::getCountPerSize)
    ).apply(inst, AltarOutputSetCrystalCount::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputSetCrystalCount> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            AltarOutputSetCrystalCount::getCountPerSize,
            AltarOutputSetCrystalCount::new);
    public static final Type<AltarOutputSetCrystalCount> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final int countPerSize;

    private AltarOutputSetCrystalCount(int countPerSize) {
        this.countPerSize = countPerSize;
    }

    public static AltarOutputSetCrystalCount of(int countPerSize) {
        return new AltarOutputSetCrystalCount(countPerSize);
    }

    public int getCountPerSize() {
        return this.countPerSize;
    }

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        if (output.has(DataComponentsAS.CRYSTAL_ATTRIBUTES)) {
            CrystalAttributesComponent cmp = output.getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty());
            int size = cmp.getAttributeTier(CrystalPropertiesAS.SIZE);
            int newCount = output.getCount() * (size * this.countPerSize);
            output.setCount(Mth.clamp(newCount, 1, output.getMaxStackSize()));
        }
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }
}
