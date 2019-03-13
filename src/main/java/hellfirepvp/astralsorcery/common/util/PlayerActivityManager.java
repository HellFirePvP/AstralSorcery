/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktPlayerStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerActivityManager
 * Created by HellFirePvP
 * Date: 23.11.2018 / 17:58
 */
public class PlayerActivityManager implements ITickHandler {

    private static final long INACTIVITY_MS = 5 * 60 * 1000; //5 minutes.
    public static final PlayerActivityManager INSTANCE = new PlayerActivityManager();
    private Map<UUID, Boolean> serverActivityMap = Maps.newHashMap();

    private long clientInteractMs = -1;
    private boolean clientActive = true;
    private boolean up = false, down = false, left = false, right = false;

    private PlayerActivityManager() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        handleClientTick();
    }

    @SideOnly(Side.CLIENT)
    private void handleClientTick() {
        if (clientInteractMs == -1) {
            clientInteractMs = System.currentTimeMillis();
        }
        if (Minecraft.getMinecraft().player == null) {
            return;
        }
        checkMs();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onKeyIn(InputEvent.KeyInputEvent event) {
        if (Minecraft.getMinecraft().player == null) {
            return;
        }

        // If you find yourself at this line of code, trying to find a way around the anti-afk mechanic,
        // you should be ashamed of yourself. How about actually putting effort into playing the game
        // instead of cheating. Play creative mode or just give yourself the progress you want. Cause you're
        // not far away from that when you're just trying to cheat your way through mechanics.
        // ~HellFirePvP
        GameSettings gs = Minecraft.getMinecraft().gameSettings;

        if (up && gs.keyBindForward.isKeyDown()) {
            return;
        }
        up = gs.keyBindForward.isKeyDown();
        if (down && gs.keyBindBack.isKeyDown()) {
            return;
        }
        down = gs.keyBindBack.isKeyDown();
        if (left && gs.keyBindLeft.isKeyDown()) {
            return;
        }
        left = gs.keyBindLeft.isKeyDown();
        if (right && gs.keyBindRight.isKeyDown()) {
            return;
        }
        right = gs.keyBindRight.isKeyDown();

        if (up || down || left || right) {
            clientInteractMs = System.currentTimeMillis();
            if (!clientActive) {
                clientActive = true;
                PktPlayerStatus pkt = new PktPlayerStatus(Minecraft.getMinecraft().player.getUniqueID(), true);
                PacketChannel.CHANNEL.sendToServer(pkt);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void checkMs() {
        if (System.currentTimeMillis() - clientInteractMs > INACTIVITY_MS) {

            clientInteractMs = System.currentTimeMillis();
            clientActive = false;
            PktPlayerStatus pkt = new PktPlayerStatus(Minecraft.getMinecraft().player.getUniqueID(), false);
            PacketChannel.CHANNEL.sendToServer(pkt);
        }
    }

    public void setStatusServer(UUID playerUUID, boolean active) {
        this.serverActivityMap.put(playerUUID, active);
    }

    public boolean isPlayerActiveServer(EntityPlayer player) {
        return isPlayerActiveServer(player.getUniqueID());
    }

    public boolean isPlayerActiveServer(UUID playerUUID) {
        return this.serverActivityMap.getOrDefault(playerUUID, true);
    }

    @SideOnly(Side.CLIENT)
    public boolean isPlayerActiveClient() {
        return this.clientActive;
    }

    public void clearCache(Side side) {
        if (side == Side.CLIENT) {
            clientInteractMs = -1;
            clientActive = true;
            up = false;
            down = false;
            left = false;
            right = false;
        } else {
            this.serverActivityMap.clear();
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
        return "Player Activity Manager";
    }
}
