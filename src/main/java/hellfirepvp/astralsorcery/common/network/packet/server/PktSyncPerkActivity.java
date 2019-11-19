/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
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
        this.type = ByteBufUtils.readOptional(buf, (byteBuf) -> ByteBufUtils.readEnumValue(byteBuf, Type.class));
        ResourceLocation key = ByteBufUtils.readOptional(buf, ByteBufUtils::readResourceLocation);
        if (key != null) {
            this.perk = PerkTree.PERK_TREE.getPerk(key);
        }
        this.newData = ByteBufUtils.readOptional(buf, ByteBufUtils::readNBTTag);
        this.oldData = ByteBufUtils.readOptional(buf, ByteBufUtils::readNBTTag);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.unlock);
        ByteBufUtils.writeOptional(buf, this.type, ByteBufUtils::writeEnumValue);
        ByteBufUtils.writeOptional(buf, this.perk,
                ((byteBuf, perk) -> ByteBufUtils.writeResourceLocation(byteBuf, perk.getRegistryName())));
        ByteBufUtils.writeOptional(buf, this.newData, ByteBufUtils::writeNBTTag);
        ByteBufUtils.writeOptional(buf, this.oldData, ByteBufUtils::writeNBTTag);
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
                    LogCategory.PERKS.info(() -> "Received perk activity packet on clientside: " + pkt.type);
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
                    LogCategory.PERKS.info(() -> "Received perk modification packet on clientside: " + pkt.perk.getRegistryName() + " " + (pkt.unlock ? "Application" : "Removal"));
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
