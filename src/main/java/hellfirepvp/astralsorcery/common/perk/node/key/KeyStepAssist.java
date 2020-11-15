/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncStepAssist;
import hellfirepvp.astralsorcery.common.perk.CooldownPerk;
import hellfirepvp.astralsorcery.common.perk.PerkCooldownHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tick.PlayerTickPerk;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyStepAssist
 * Created by HellFirePvP
 * Date: 31.08.2019 / 22:53
 */
public class KeyStepAssist extends KeyPerk implements PlayerTickPerk, CooldownPerk {

    public KeyStepAssist(ResourceLocation name, float x, float y) {
        super(name, x, y);
    }

    @Override
    public void attachListeners(LogicalSide side, IEventBus bus) {
        super.attachListeners(side, bus);
        bus.addListener(EventPriority.LOWEST, this::onTeleport);
    }

    @Override
    public void onPlayerTick(PlayerEntity player, LogicalSide side) {
        if (side.isServer()) {
            float currentHeight = player.stepHeight;
            if (!PerkCooldownHelper.isCooldownActiveForPlayer(player, this)) {
                player.stepHeight += 0.5F;
            } else {
                if (player.stepHeight < 1.1F) {
                    player.stepHeight = 1.1F;
                }
            }
            PerkCooldownHelper.forceSetCooldownForPlayer(player, this, 20);
            if (currentHeight != player.stepHeight && player instanceof ServerPlayerEntity) {
                if (MiscUtils.isConnectionEstablished((ServerPlayerEntity) player)) {
                    PktSyncStepAssist sync = new PktSyncStepAssist(player.stepHeight);
                    PacketChannel.CHANNEL.sendToPlayer(player, sync);
                }
            }
        }
    }

    @Override
    public void onCooldownTimeout(PlayerEntity player) {
        player.stepHeight -= 0.5F;
        if (player.stepHeight < 0.6F) {
            player.stepHeight = 0.6F;
        }

        if (player instanceof ServerPlayerEntity && MiscUtils.isConnectionEstablished((ServerPlayerEntity) player)) {
            PktSyncStepAssist sync = new PktSyncStepAssist(player.stepHeight);
            PacketChannel.CHANNEL.sendToPlayer(player, sync);
        }
    }

    private void onTeleport(EntityTravelToDimensionEvent event) {
        if (!event.getEntity().getEntityWorld().isRemote() && event.getEntity() instanceof PlayerEntity) {
            PerkCooldownHelper.removeAllCooldowns((PlayerEntity) event.getEntity(), LogicalSide.SERVER);
        }
    }
}
