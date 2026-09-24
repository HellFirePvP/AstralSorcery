/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputGenerateIdentifier
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputGenerateIdentifier extends AltarRecipeOutputModifier {

    public static final AltarOutputGenerateIdentifier INSTANCE = new AltarOutputGenerateIdentifier();
    public static final MapCodec<AltarOutputGenerateIdentifier> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputGenerateIdentifier> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final Type<AltarOutputGenerateIdentifier> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private AltarOutputGenerateIdentifier() {}

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        IdentifierComponent.createIdentifierIfNotExists(output);
        return output;
    }

    @Override
    public ItemStack modifyOutputForDisplay(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        output.set(DataComponentsAS.IDENTIFIER, new IdentifierComponent(new UUID(0L, 1L)));
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }
}
