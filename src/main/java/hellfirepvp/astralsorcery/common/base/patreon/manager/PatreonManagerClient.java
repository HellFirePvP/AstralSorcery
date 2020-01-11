/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.manager;

import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.entity.PatreonPartialEntity;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.sync.client.ClientPatreonFlares;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collection;
import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonManagerClient
 * Created by HellFirePvP
 * Date: 31.08.2019 / 01:42
 */
public class PatreonManagerClient implements ITickHandler {

    public static PatreonManagerClient INSTANCE = new PatreonManagerClient();

    private PatreonManagerClient() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        World clWorld = Minecraft.getInstance().world;
        PlayerEntity thisPlayer = Minecraft.getInstance().player;
        if (clWorld == null || thisPlayer == null) {
            return;
        }
        int clDim = clWorld.getDimension().getType().getId();
        Vector3 thisPlayerPos = Vector3.atEntityCenter(thisPlayer);

        SyncDataHolder.executeClient(SyncDataHolder.DATA_PATREON_FLARES, ClientPatreonFlares.class, data -> {
            for (Collection<PatreonPartialEntity> playerEntities : data.getEntities()) {
                for (PatreonPartialEntity entity : playerEntities) {
                    if (entity.getLastTickedDimension() == null || entity.getLastTickedDimension() != clDim) {
                        continue;
                    }
                    if (entity.getPos().distanceSquared(thisPlayerPos) <= RenderingConfig.CONFIG.getMaxEffectRenderDistanceSq()) {
                        entity.tickClient();
                    }
                    entity.tick(clWorld);
                }
            }
        });

        SyncDataHolder.executeClient(SyncDataHolder.DATA_PATREON_FLARES, ClientPatreonFlares.class, data -> {
            for (PlayerEntity player : clWorld.getPlayers()) {
                for (PatreonEffect effect : PatreonEffectHelper.getPatreonEffects(LogicalSide.CLIENT, player.getUniqueID())) {
                    effect.doClientEffect(player);
                }
            }
        });
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Patreon Flare Manager (Client)";
    }
}
