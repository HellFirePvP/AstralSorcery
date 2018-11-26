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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    private NBTTagCompound newData, oldData;
    private Type type = null;

    public PktSyncPerkActivity() {}

    public PktSyncPerkActivity(AbstractPerk perk, boolean unlock) {
        this.perk = perk;
        this.unlock = unlock;
    }

    public PktSyncPerkActivity(Type type) {
        this.type = type;
    }

    public PktSyncPerkActivity(AbstractPerk perk, NBTTagCompound oldData, NBTTagCompound newData) {
        this.type = Type.DATACHANGE;
        this.perk = perk;
        this.oldData = oldData;
        this.newData = newData;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.unlock = buf.readBoolean();
        int tt = buf.readInt();
        if (tt == -1) {
            this.type = null;
        } else {
            this.type = Type.values()[MathHelper.clamp(tt, 0, Type.values().length - 1)];
        }
        if (buf.readBoolean()) {
            this.perk = PerkTree.PERK_TREE.getPerk(new ResourceLocation(ByteBufUtils.readString(buf)));
        }
        if (buf.readBoolean()) {
            this.newData = ByteBufUtils.readNBTTag(buf);
        }
        if (buf.readBoolean()) {
            this.oldData = ByteBufUtils.readNBTTag(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.unlock);
        if (this.type == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(this.type.ordinal());
        }
        buf.writeBoolean(this.perk != null);
        if (this.perk != null) {
            ByteBufUtils.writeString(buf, this.perk.getRegistryName().toString());
        }
        buf.writeBoolean(this.newData != null);
        if (newData != null) {
            ByteBufUtils.writeNBTTag(buf, newData);
        }
        buf.writeBoolean(this.oldData != null);
        if (oldData != null) {
            ByteBufUtils.writeNBTTag(buf, oldData);
        }
    }

    @Override
    public IMessage onMessage(PktSyncPerkActivity message, MessageContext ctx) {
        handleClientPerkUpdate(message);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void handleClientPerkUpdate(PktSyncPerkActivity pkt) {
        AstralSorcery.proxy.scheduleClientside(() -> {
            if (Minecraft.getMinecraft().player != null) {
                if (pkt.type != null) {
                    switch (pkt.type) {
                        case CLEARALL:
                            PerkEffectHelper.EVENT_INSTANCE.clearAllPerksClient(Minecraft.getMinecraft().player);
                            break;
                        case UNLOCKALL:
                            PerkEffectHelper.EVENT_INSTANCE.reapplyAllPerksClient(Minecraft.getMinecraft().player);
                            break;
                        case DATACHANGE:
                            PerkEffectHelper.EVENT_INSTANCE.notifyPerkDataChangeClient(Minecraft.getMinecraft().player, pkt.perk, pkt.oldData, pkt.newData);
                            break;
                        default:
                            break;
                    }
                } else if (pkt.perk != null) {
                    PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(Minecraft.getMinecraft().player, Side.CLIENT, pkt.perk, !pkt.unlock);
                }
            }
        });
    }

    public static enum Type {

        CLEARALL,
        UNLOCKALL,
        DATACHANGE

    }
}
