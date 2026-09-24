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
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IdentifierComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record IdentifierComponent(UUID id) {

    public static final IdentifierComponent NONE = new IdentifierComponent(Util.NIL_UUID);

    public static final Codec<IdentifierComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            CodecUtil.uuidCodec().fieldOf("id").forGetter(IdentifierComponent::id)
    ).apply(inst, IdentifierComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, IdentifierComponent> STREAM_CODEC = StreamCodec.composite(
            CodecUtil.uuidStreamCodec(),
            IdentifierComponent::id,
            IdentifierComponent::new
    );

    public static IdentifierComponent random() {
        return new IdentifierComponent(UUID.randomUUID());
    }

    public static void createIdentifierIfNotExists(ItemStack stack) {
        if (!stack.has(DataComponentsAS.IDENTIFIER)) {
            stack.set(DataComponentsAS.IDENTIFIER, IdentifierComponent.random());
        }
    }

    public static void createOrOverwriteIdentifier(ItemStack stack) {
        stack.set(DataComponentsAS.IDENTIFIER, IdentifierComponent.random());
    }

    public static UUID getIdentifier(ItemStack stack) {
        return stack.getOrDefault(DataComponentsAS.IDENTIFIER, NONE).id();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IdentifierComponent that = (IdentifierComponent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
