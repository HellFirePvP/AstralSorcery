/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source;

import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ModifierSource
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
//Each ModifierSource should be a AttributeModifierProvider in some way or subclass.
public interface ModifierSource {

    StreamCodec<RegistryFriendlyByteBuf, ModifierSource> STREAM_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_PERK_MODIFIER_SOURCES)
                    .dispatch(ModifierSource::getSourceProvider, ModifierSourceProvider::getModifierSourceSyncCodec);

    boolean canApplySource(Player player, LogicalSide dist);

    void onRemove(Player player, LogicalSide dist);

    void onApply(Player player, LogicalSide dist);

    default boolean isEqual(ModifierSource other) {
        return this.equals(other);
    }

    ModifierSourceProvider<?> getSourceProvider();

}
