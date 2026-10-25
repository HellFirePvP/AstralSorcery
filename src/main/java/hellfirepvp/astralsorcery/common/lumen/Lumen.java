/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import hellfirepvp.astralsorcery.common.util.RecipeUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: Lumen
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class Lumen implements LumenLike {

    public static final Codec<Lumen> NON_EMPTY_CODEC = Codec.lazyInitialized(() -> RegistriesAS.REGISTRY_LUMEN
            .byNameCodec()
            .validate((lumen) -> Objects.equals(lumen, LumenAS.NONE.get())
                    ? DataResult.error(() -> "Lumen must not be astralsorcery:none")
                    : DataResult.success(lumen)
            ));
    @SuppressWarnings("deprecation")
    public static final Codec<Holder<Lumen>> NON_EMPTY_HOLDER_CODEC = Codec.lazyInitialized(() -> RegistriesAS.REGISTRY_LUMEN
            .holderByNameCodec()
            .validate((holder) -> holder.is(LumenAS.NONE)
                    ? DataResult.error(() -> "Lumen must not be astralsorcery:none")
                    : DataResult.success(holder)
            ));
    public static final StreamCodec<RegistryFriendlyByteBuf, Lumen> STREAM_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_LUMEN);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Lumen>> HOLDER_STREAM_CODEC = ByteBufCodecs.holderRegistry(RegistriesAS.KEY_LUMEN);

    private final Holder.Reference<Lumen> builtInRegistryHolder = RegistriesAS.REGISTRY_LUMEN.createIntrusiveHolder(this);
    private final ColorWrapper color;
    private final boolean isElementary;

    private String nameId = null;

    public Lumen(ColorWrapper color) {
        this(color, false);
    }

    public Lumen(ColorWrapper color, boolean isElementary) {
        this.color = color;
        this.isElementary = isElementary;
    }

    public Holder.Reference<Lumen> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    @Override
    public Lumen asLumen() {
        return this;
    }

    public boolean isElementary() {
        return this.isElementary;
    }

    public ColorWrapper getColor(Level level, Vector3 pos) {
        long posOffset = Math.round(Math.abs(pos.getX()) * 3 + Math.abs(pos.getY()) * 3 + Math.abs(pos.getZ()) * 3);
        return this.getColor(level.getGameTime() + posOffset);
    }

    public ColorWrapper getColor(Level level, BlockPos pos) {
        long posOffset = Math.abs(pos.getX()) * 3L + Math.abs(pos.getY()) * 3L + Math.abs(pos.getZ()) * 3L;
        return this.getColor(level.getGameTime() + posOffset);
    }

    public ColorWrapper getColor(long tick) {
        return this.color;
    }

    public LumenStack stack(int amount) {
        return LumenStack.of(this, amount);
    }

    public Optional<ResourceKey<Lumen>> getRegistryKey() {
        return this.builtInRegistryHolder.unwrapKey();
    }

    public Optional<ResourceLocation> getRegistryId() {
        return getRegistryKey().map(ResourceKey::location);
    }

    public boolean maySee(Level level, PlayerProgress progress) {
        if (!progress.hasDiscoveredLumen(this) &&
                !this.isElementary() &&
                !progress.getTierReached().isThisLaterOrEqual(ResearchTier.LUMINANCE)) {
            return false;
        }
        Set<Lumen> dependents = RecipeUtil.findDirectLumenMakingUp(level, this);
        for (Lumen dependent : dependents) {
            Set<Lumen> childDependents = RecipeUtil.findAnyLumenMakingUp(level, dependent);
            if (!childDependents.isEmpty() && !progress.hasDiscoveredLumen(dependent)) {
                return false;
            }
        }
        return true;
    }

    @Nonnull
    protected String getOrCreateNameId() {
        if (this.nameId == null) {
            this.nameId = Util.makeDescriptionId("lumen", this.getRegistryId().orElse(null));
        }
        return this.nameId;
    }

    @Nonnull
    public Component getName() {
        return Component.translatable(this.getOrCreateNameId());
    }

    @Nonnull
    public Component getHoverName() {
        return Component.translatable("lumen.astralsorcery.hover.title", this.getName());
    }
}
