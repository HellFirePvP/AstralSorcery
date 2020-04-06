/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.manager;

import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.entity.PatreonPartialEntity;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.sync.server.DataPatreonFlares;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonManager
 * Created by HellFirePvP
 * Date: 30.08.2019 / 23:28
 */
public class PatreonManager implements ITickHandler {

    public static PatreonManager INSTANCE = new PatreonManager();

    private PatreonManager() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        if (server == null) {
            return;
        }

        SyncDataHolder.executeServer(SyncDataHolder.DATA_PATREON_FLARES, DataPatreonFlares.class, data -> {
            Collection<UUID> owners = new ArrayList<>(data.getOwners());
            Set<UUID> foundValidOwners = new HashSet<>();
            Map<UUID, List<PatreonEffect>> playerEffects = PatreonEffectHelper.getPatreonEffects(server.getPlayerList().getPlayers());

            for (UUID playerUUID : playerEffects.keySet()) {
                ServerPlayerEntity player = server.getPlayerList().getPlayerByUUID(playerUUID);
                if (player == null) {
                    continue;
                }

                Collection<PatreonPartialEntity> knownEntities = new ArrayList<>(data.getEntities(playerUUID));
                for (PatreonEffect effect : PatreonEffectHelper.getPatreonEffects(LogicalSide.SERVER, playerUUID)) {
                    if (effect == null || !effect.hasPartialEntity()) {
                        continue;
                    }

                    foundValidOwners.add(playerUUID);
                    PatreonPartialEntity effectEntity = MiscUtils.iterativeSearch(knownEntities, e -> e.getEffectUUID().equals(effect.getEffectUUID()));
                    if (effectEntity == null) {
                        effectEntity = data.createEntity(player, effect);
                    }

                    World playerWorld = player.getServerWorld();
                    if (effectEntity.getLastTickedDimension() != null &&
                            playerWorld.getDimension().getType().getId() != effectEntity.getLastTickedDimension()) {
                        effectEntity.placeNear(player);
                    }

                    if (effectEntity.tick(playerWorld)) {
                        data.updateEntitiesOf(playerUUID);
                    }
                }
            }

            for (UUID owner : owners) {
                if (foundValidOwners.contains(owner)) {
                    continue;
                }

                data.removeEntities(owner);
            }
        });
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.SERVER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Patreon Flare Manager (Server)";
    }

}
