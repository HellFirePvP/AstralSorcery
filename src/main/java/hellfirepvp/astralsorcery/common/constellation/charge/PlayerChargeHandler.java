/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.charge;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncCharge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerChargeHandler
 * Created by HellFirePvP
 * Date: 05.04.2017 / 16:50
 */
public class PlayerChargeHandler implements ITickHandler {

    public static PlayerChargeHandler instance = new PlayerChargeHandler();

    private Map<EntityPlayer, Float> chargeMap = new HashMap<>();
    public float clientCharge = 0F;

    private PlayerChargeHandler() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if(context[1] == Side.SERVER) {
            EntityPlayer pl = (EntityPlayer) context[0];
            float charge = getCharge(pl);
            if(charge < 1F) {
                if(pl.isCreative()) {
                    charge = 1F;
                } else {
                    float chargeGain = 0.003F;
                    float dayMult = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(pl.getEntityWorld());
                    chargeGain *= (0.5F + dayMult * 0.5F);
                    if(pl.getEntityWorld().canSeeSky(pl.getPosition().up())) {
                        chargeGain *= 3F;
                    }
                    charge = MathHelper.clamp(charge + chargeGain, 0F, 1F);
                }
                setCharge(pl, charge);

                PktSyncCharge ch = new PktSyncCharge(charge);
                PacketChannel.CHANNEL.sendTo(ch, (EntityPlayerMP) pl);
            }
        }
    }

    public void informDisconnect(EntityPlayer player) {
        chargeMap.remove(player);
    }

    public float getCharge(EntityPlayer player) {
        Float ret = chargeMap.get(player);
        return ret == null ? 0F : ret;
    }

    public void setCharge(EntityPlayer player, float charge) {
        chargeMap.put(player, MathHelper.clamp(charge, 0F, 1F));
    }

    public boolean hasAtLeast(EntityPlayer player, float toTest) {
        return getCharge(player) >= toTest;
    }

    public void drainCharge(EntityPlayer player, float toDrain) {
        float charge = getCharge(player);
        charge = Math.max(0, charge - toDrain);
        setCharge(player, charge);
    }

    public void setClientCharge(float pktIncCharge) {
        this.clientCharge = pktIncCharge;
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "PlayerCharge Handler";
    }
}
