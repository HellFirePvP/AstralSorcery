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
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectFixedSprite;
import hellfirepvp.astralsorcery.common.data.DataPatreonFlares;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonFlareManagerClient
 * Created by HellFirePvP
 * Date: 23.06.2018 / 17:44
 */
public class PatreonFlareManagerClient implements ITickHandler {

    public static PatreonFlareManagerClient INSTANCE = new PatreonFlareManagerClient();

    private PatreonFlareManagerClient() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        World clWorld = Minecraft.getMinecraft().world;
        EntityPlayer thisPlayer = Minecraft.getMinecraft().player;
        if (clWorld == null || thisPlayer == null) return;
        int clDim = clWorld.provider.getDimension();
        Vector3 thisPlayerPos = Vector3.atEntityCenter(thisPlayer);

        DataPatreonFlares dataFlares = SyncDataHolder.getDataClient(SyncDataHolder.DATA_PATREON_FLARES);
        for (PatreonPartialEntity flare : dataFlares.getEntities(Side.CLIENT)) {
            if (flare.getLastTickedDim() == null || clDim != flare.getLastTickedDim()) continue;
            if (flare.getPos().distanceSquared(thisPlayerPos) <= Config.maxEffectRenderDistanceSq) {
                flare.tickInRenderDistance();
            }
            flare.update(clWorld);
        }

        for (EntityPlayer pl : clWorld.playerEntities) {
            PatreonEffectHelper.PatreonEffect eff = PatreonEffectHelper.getEffect(Side.CLIENT, pl.getUniqueID());
            if (eff != null && eff instanceof PtEffectFixedSprite) {
                ((PtEffectFixedSprite) eff).doEffect(pl);
            }
        }
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
