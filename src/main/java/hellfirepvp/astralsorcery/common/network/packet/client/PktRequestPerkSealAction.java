/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRequestPerkSealAction
 * Created by HellFirePvP
 * Date: 18.09.2018 / 21:09
 */
public class PktRequestPerkSealAction implements IMessage, IMessageHandler<PktRequestPerkSealAction, IMessage> {

    private ResourceLocation perkKey;
    private boolean doSealing; //Make/true or break/false the seal

    public PktRequestPerkSealAction() {}

    public PktRequestPerkSealAction(ResourceLocation perkKey, boolean seal) {
        this.perkKey = perkKey;
        this.doSealing = seal;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.perkKey = new ResourceLocation(ByteBufUtils.readString(buf));
        this.doSealing = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, this.perkKey.toString());
        buf.writeBoolean(this.doSealing);
    }

    @Override
    public IMessage onMessage(PktRequestPerkSealAction pkt, MessageContext ctx) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            AbstractPerk perk = PerkTree.PERK_TREE.getPerk(pkt.perkKey);
            if (perk != null) {
                if (pkt.doSealing) {
                    ResearchManager.applyPerkSeal(ctx.getServerHandler().player, perk);
                } else {
                    ResearchManager.breakPerkSeal(ctx.getServerHandler().player, perk);
                }
            }
        });
        return null;
    }
}
