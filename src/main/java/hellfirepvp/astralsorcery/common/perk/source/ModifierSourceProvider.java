/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source;

import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncModifierSource;
import hellfirepvp.astralsorcery.common.perk.PerkEffectHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModifierSourceProvider
 * Created by HellFirePvP
 * Date: 01.04.2020 / 18:20
 */
public abstract class ModifierSourceProvider<T extends ModifierSource> {

    private final ResourceLocation key;

    private final Map<UUID, Map<ResourceLocation, T>> cachedSources = new HashMap<>();

    protected ModifierSourceProvider(ResourceLocation key) {
        this.key = key;
    }

    protected abstract void update(ServerPlayerEntity playerEntity);

    public abstract void serialize(T source, PacketBuffer buf);

    public abstract T deserialize(PacketBuffer buf);

    @Nullable
    private T getModifier(ServerPlayerEntity player, ResourceLocation identifier) {
        Map<ResourceLocation, T> playerModifiers = cachedSources.computeIfAbsent(player.getUniqueID(), uuid -> new HashMap<>());
        return playerModifiers.get(identifier);
    }

    private void setModifier(ServerPlayerEntity player, ResourceLocation identifier, @Nullable T source) {
        Map<ResourceLocation, T> playerModifiers = cachedSources.computeIfAbsent(player.getUniqueID(), uuid -> new HashMap<>());
        if (source != null) {
            playerModifiers.put(identifier, source);
        } else {
            playerModifiers.remove(identifier);
        }
    }

    protected void updateSource(ServerPlayerEntity player, ResourceLocation identifier, @Nullable T source) {
        T existing = this.getModifier(player, identifier);
        if (existing != null) {
            if (!existing.isEqual(source)) {
                PerkEffectHelper.modifySource(player, LogicalSide.SERVER, existing, PerkEffectHelper.Action.REMOVE);
                PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncModifierSource(existing, PerkEffectHelper.Action.REMOVE));
            } else {
                return; //Nothing to do
            }
        }

        if (source != null) {
            PerkEffectHelper.modifySource(player, LogicalSide.SERVER, source, PerkEffectHelper.Action.ADD);
            PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncModifierSource(source, PerkEffectHelper.Action.ADD));
        }
        this.setModifier(player, identifier, source);
    }

    public final ResourceLocation getKey() {
        return key;
    }
}
