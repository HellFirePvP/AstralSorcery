/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkEffectHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncPerkActivity
 * Created by HellFirePvP
 * Date: 09.07.2018 / 17:23
 */
public class PktSyncPerkActivity implements IMessage, IMessageHandler<PktSyncPerkActivity, IMessage> {

    private AbstractPerk perk;
    private boolean unlock;
    private boolean clearAll = false;

    public PktSyncPerkActivity() {}

    public PktSyncPerkActivity(AbstractPerk perk, boolean unlock) {
        this.perk = perk;
        this.unlock = unlock;
    }

    public PktSyncPerkActivity(boolean clearAll) {
        this.clearAll = clearAll;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.unlock = buf.readBoolean();
        this.clearAll = buf.readBoolean();
        if (buf.readBoolean()) {
            this.perk = PerkTree.PERK_TREE.getPerk(new ResourceLocation(ByteBufUtils.readString(buf)));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.unlock);
        buf.writeBoolean(this.clearAll);
        buf.writeBoolean(this.perk != null);
        if (this.perk != null) {
            ByteBufUtils.writeString(buf, this.perk.getRegistryName().toString());
        }
    }

    @Override
    public IMessage onMessage(PktSyncPerkActivity message, MessageContext ctx) {
        handleClientPerkUpdate(message);
        return null;
    }

    private void handleClientPerkUpdate(PktSyncPerkActivity pkt) {
        AstralSorcery.proxy.scheduleClientside(() -> {
            if (Minecraft.getMinecraft().player != null) {
                if (pkt.clearAll) {
                    PerkEffectHelper.EVENT_INSTANCE.clearAllPerksClient(Minecraft.getMinecraft().player);
                } else if (pkt.perk != null) {
                    PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(Minecraft.getMinecraft().player, Side.CLIENT, pkt.perk, !pkt.unlock);
                }
            }
        });
    }
}
