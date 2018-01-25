/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkCreationReach;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktUpdateReach
 * Created by HellFirePvP
 * Date: 23.12.2016 / 11:26
 */
public class PktUpdateReach implements IMessage, IMessageHandler<PktUpdateReach, IMessage> {

    public boolean apply = false;

    public PktUpdateReach() {}

    public PktUpdateReach(boolean apply) {
        this.apply = apply;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.apply = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(apply);
    }

    @Override
    public IMessage onMessage(PktUpdateReach message, MessageContext ctx) {
        updateReachClient(message);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void updateReachClient(PktUpdateReach message) {
        AstralSorcery.proxy.scheduleClientside(() -> PerkCreationReach.updateReach(message.apply));
    }
}
