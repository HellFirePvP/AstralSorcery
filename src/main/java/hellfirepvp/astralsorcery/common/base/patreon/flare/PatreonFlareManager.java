/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.flare;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.data.DataPatreonFlares;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonFlareManager
 * Created by HellFirePvP
 * Date: 23.06.2018 / 16:04
 */
public class PatreonFlareManager implements ITickHandler {

    public static PatreonFlareManager INSTANCE = new PatreonFlareManager();

    private PatreonFlareManager() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return;
        DataPatreonFlares dataFlares = SyncDataHolder.getDataServer(SyncDataHolder.DATA_PATREON_FLARES);
        for (Map.Entry<UUID, PatreonEffectHelper.PatreonEffect> effect : PatreonEffectHelper.getEntityPatrons(server.getPlayerList().getPlayers()).entrySet()) {
            EntityPlayerMP owner = server.getPlayerList().getPlayerByUUID(effect.getKey());
            PatreonPartialEntity entity = dataFlares.getEntity(Side.SERVER, effect.getKey());
            if (owner == null) {
                if (entity != null) {
                    dataFlares.destroyEntity(entity);
                }
            } else {
                if (entity == null) {
                    entity = dataFlares.createEntity(owner, effect.getValue());
                }

                World plWorld = owner.getEntityWorld();
                if (entity.getLastTickedDim() != null && plWorld.provider.getDimension() != entity.getLastTickedDim()) {
                    entity.setPositionNear(owner);
                }

                if(entity.update(plWorld)) {
                    dataFlares.updateEntity(entity);
                }
            }
        }
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
