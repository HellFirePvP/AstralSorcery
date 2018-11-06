/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.tool.sextant.ItemSextant;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSetSextantTarget
 * Created by HellFirePvP
 * Date: 31.05.2018 / 10:14
 */
public class PktSetSextantTarget implements IMessage, IMessageHandler<PktSetSextantTarget, IMessage> {

    private String target;
    private EnumHand hand;

    public PktSetSextantTarget() {}

    public PktSetSextantTarget(SextantFinder.TargetObject target, EnumHand hand) {
        this.target = target.getRegistryName();
        this.hand = hand;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.target = ByteBufUtils.readString(buf);
        this.hand = EnumHand.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, this.target);
        buf.writeInt(hand.ordinal());
    }

    @Override
    public IMessage onMessage(PktSetSextantTarget message, MessageContext ctx) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            SextantFinder.TargetObject target = SextantFinder.getByName(message.target);
            if(target == null) {
                return;
            }
            ItemStack held = ctx.getServerHandler().player.getHeldItem(message.hand);
            if(held.isEmpty() || !(held.getItem() instanceof ItemSextant)) {
                return;
            }
            EntityPlayer player = ctx.getServerHandler().player;
            Thread tr = new Thread(() -> {
                //May be null; In that case, tell that to the client as well so it won't ask the server any longer.
                BlockPos result = target.searchFor((WorldServer) player.world, player.getPosition());
                if (result != null) {
                    FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                        if (ResearchManager.useSextantTarget(target, player)) {
                            ItemSextant.setTarget(held, target);
                            ItemSextant.setCurrentTargetInformation(held, result, player.world.provider.getDimension());
                        } else {
                            AstralSorcery.log.warn("Could not set used sextant target for player " + player.getDisplayName() + " - missing progress!");
                        }
                    });
                }
            });
            tr.setName("SextantTargetFinder-Applying ThreadId=" + tr.getId());
            tr.start();
        });
        return null;
    }
}
