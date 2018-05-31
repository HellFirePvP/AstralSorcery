/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.item.tool.sextant.ItemSextant;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
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

    public PktSetSextantTarget() {}

    public PktSetSextantTarget(SextantFinder.TargetObject target) {
        this.target = target.getRegistryName();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.target = ByteBufUtils.readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, this.target);
    }

    @Override
    public IMessage onMessage(PktSetSextantTarget message, MessageContext ctx) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            SextantFinder.TargetObject target = SextantFinder.getByName(message.target);
            if(target == null) {
                return;
            }
            ItemStack held = ctx.getServerHandler().player.getHeldItem(EnumHand.MAIN_HAND);
            if(held.isEmpty() || !(held.getItem() instanceof ItemSextant)) {
                held = ctx.getServerHandler().player.getHeldItem(EnumHand.OFF_HAND);
            }
            if(held.isEmpty() || !(held.getItem() instanceof ItemSextant)) {
                return;
            }
            ItemSextant.setTarget(held, target);
        });
        return null;
    }
}
