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
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VeiledItemStackComponent
 * Created by HellFirePvP
 * Date: 24.10.2026 / 21:44
 */
public record VeiledItemStackComponent(ItemStack appearance, Optional<Long> expiryGameTick, boolean known) {

    public static final Codec<VeiledItemStackComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ItemStack.CODEC.fieldOf("appearance").forGetter(VeiledItemStackComponent::appearance),
            Codec.LONG.optionalFieldOf("expiryGameTick").forGetter(VeiledItemStackComponent::expiryGameTick),
            Codec.BOOL.fieldOf("known").forGetter(VeiledItemStackComponent::known)
    ).apply(inst, VeiledItemStackComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VeiledItemStackComponent> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, VeiledItemStackComponent::appearance,
            ByteBufCodecs.optional(ByteBufCodecs.VAR_LONG), VeiledItemStackComponent::expiryGameTick,
            ByteBufCodecs.BOOL, VeiledItemStackComponent::known,
            VeiledItemStackComponent::new);

    public VeiledItemStackComponent {
        if (appearance.isEmpty()) {
            AstralSorcery.LOG.error("Tried to create veiled stack component with an empty itemstack. This will fail. Changing it to stone");
            appearance = new ItemStack(Items.STONE);
        }
        appearance = appearance.copyWithCount(1);
    }

    public static VeiledItemStackComponent permanent(ItemStack appearance, boolean known) {
        return new VeiledItemStackComponent(appearance, Optional.empty(), known);
    }

    public static VeiledItemStackComponent timeout(ItemStack appearance, Level level, int durationTicks, boolean known) {
        return new VeiledItemStackComponent(appearance, Optional.of(level.getGameTime() + durationTicks), known);
    }

    public ItemStack appearance() {
        return this.appearance.copy();
    }

    public boolean isExpired(Level level) {
        return this.expiryGameTick.isPresent() && level.getGameTime() >= this.expiryGameTick.get();
    }

    public VeiledItemStackComponent withKnown(boolean known) {
        return new VeiledItemStackComponent(this.appearance, this.expiryGameTick, known);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VeiledItemStackComponent that = (VeiledItemStackComponent) o;
        return known == that.known &&
                Objects.equals(expiryGameTick, that.expiryGameTick) &&
                ItemStack.isSameItemSameComponents(appearance, that.appearance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ItemStack.hashItemAndComponents(appearance), expiryGameTick, known);
    }
}
