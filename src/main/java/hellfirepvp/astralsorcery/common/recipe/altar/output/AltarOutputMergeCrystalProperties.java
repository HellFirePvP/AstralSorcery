/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputMergeCrystalProperties
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputMergeCrystalProperties extends AltarRecipeOutputModifier {

    public static final AltarOutputMergeCrystalProperties INSTANCE = new AltarOutputMergeCrystalProperties();
    public static final MapCodec<AltarOutputMergeCrystalProperties> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputMergeCrystalProperties> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final Type<AltarOutputMergeCrystalProperties> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private AltarOutputMergeCrystalProperties() {}

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        CrystalAttributesComponent cmp = output.getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty());
        for (ItemStack stack : input.getGridInputs()) {
            CrystalAttributesComponent inputCmp = stack.getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty());
            for (CrystalAttributesComponent.TieredAttribute attr : inputCmp.getAttributes()) {
                int existing = cmp.getAttributeTier(attr);
                cmp = cmp.setAttributeTier(attr, existing + attr.getTier());
            }
        }
        output.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, cmp);
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }
}
