/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source;

import hellfirepvp.astralsorcery.common.network.play.PktSyncModifierSource;
import hellfirepvp.astralsorcery.common.perk.PerkApplicationManager;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ModifierSourceProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ModifierSourceProvider<T extends ModifierSource> {

    // PlayerId -> { Source-Provider specific ModifierSource Identifier -> ModifierSource }
    private final Map<UUID, Map<ResourceLocation, T>> cachedSources = new HashMap<>();

    protected abstract void update(ServerPlayer playerEntity);

    protected abstract void removeModifiers(ServerPlayer playerEntity);

    public abstract StreamCodec<RegistryFriendlyByteBuf, T> getModifierSourceSyncCodec();

    @Nullable
    private T getCachedSource(ServerPlayer player, ResourceLocation identifier) {
        return this.cachedSources.computeIfAbsent(player.getUUID(), uuid -> new HashMap<>()).get(identifier);
    }

    private void setCachedSource(ServerPlayer player, ResourceLocation identifier, @Nullable T source) {
        Map<ResourceLocation, T> playerModifiers = this.cachedSources.computeIfAbsent(player.getUUID(), uuid -> new HashMap<>());
        if (source != null) {
            playerModifiers.put(identifier, source);
        } else {
            playerModifiers.remove(identifier);
        }
    }

    protected void removeSource(ServerPlayer sPlayer, ResourceLocation sourceIdentifier) {
        this.updateSource(sPlayer, sourceIdentifier, null);
    }

    protected void updateSource(ServerPlayer sPlayer, ResourceLocation sourceIdentifier, @Nullable T source) {
        boolean needsRemoval = false, needsAddition = false;

        T existing = this.getCachedSource(sPlayer, sourceIdentifier);
        if (existing != null) {
            if (!existing.isEqual(source)) {
                needsRemoval = true;
            } else {
                return; //Nothing to do
            }
        }
        if (source != null) {
            needsAddition = true;
        }

        if (needsRemoval) {
            if (needsAddition) {
                PerkApplicationManager.updateSource(sPlayer, LogicalSide.SERVER, existing, source);
                PacketDistributor.sendToPlayer(sPlayer, PktSyncModifierSource.update(existing, source));
            } else {
                PerkApplicationManager.modifySource(sPlayer, LogicalSide.SERVER, existing, PerkManager.Action.REMOVE);
                PacketDistributor.sendToPlayer(sPlayer, PktSyncModifierSource.remove(existing));
            }
        } else if (needsAddition) {
            PerkApplicationManager.modifySource(sPlayer, LogicalSide.SERVER, source, PerkManager.Action.ADD);
            PacketDistributor.sendToPlayer(sPlayer, PktSyncModifierSource.add(source));
        }
        this.setCachedSource(sPlayer, sourceIdentifier, source);
    }
}
