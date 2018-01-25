/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkMap;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkMap;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktUnlockPerk
 * Created by HellFirePvP
 * Date: 12.12.2016 / 13:07
 */
public class PktUnlockPerk implements IMessage, IMessageHandler<PktUnlockPerk, PktUnlockPerk> {

    private ConstellationPerks perk;
    private IMajorConstellation owningConstellation;

    private boolean serverAccept = false;

    public PktUnlockPerk() {}

    public PktUnlockPerk(boolean serverAccept, ConstellationPerks perk) {
        this.serverAccept = serverAccept;
        this.perk = perk;
    }

    public PktUnlockPerk(ConstellationPerks perk, IMajorConstellation owningConstellation) {
        this.perk = perk;
        this.owningConstellation = owningConstellation;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.serverAccept = buf.readBoolean();
        ConstellationPerks perk = ConstellationPerks.getById(buf.readInt());
        if(perk != null) {
            this.perk = perk;
        }
        if(!serverAccept) {
            IMajorConstellation cst = ConstellationRegistry.getMajorConstellationByName(ByteBufUtils.readString(buf));
            if(cst != null) {
                this.owningConstellation = cst;
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(serverAccept);
        buf.writeInt(perk.getSingleInstance().getId());
        if(!serverAccept) {
            ByteBufUtils.writeString(buf, owningConstellation.getUnlocalizedName());
        }
    }

    @Override
    public PktUnlockPerk onMessage(PktUnlockPerk message, MessageContext ctx) {
        if(ctx.side == Side.SERVER) {
            EntityPlayer pl = ctx.getServerHandler().player;
            if(pl != null) {
                if(message.perk != null && message.owningConstellation != null) {
                    ConstellationPerkMap map = message.owningConstellation.getPerkMap();
                    if(map != null) {
                        PlayerProgress prog = ResearchManager.getProgress(pl, ctx.side);
                        if(prog != null) {
                            Map<ConstellationPerk, Integer> appliedPerks = prog.getAppliedPerks();
                            if(!appliedPerks.containsKey(message.perk.getSingleInstance())) {
                                boolean canUnlock = prog.hasFreeAlignmentLevel();
                                for (ConstellationPerkMap.Dependency d : map.getPerkDependencies()) {
                                    if(d.to.equals(message.perk)) {
                                        if(!appliedPerks.containsKey(d.from.getSingleInstance())) {
                                            canUnlock = false;
                                        }
                                    }
                                }
                                if(canUnlock && ResearchManager.applyPerk(pl, message.perk)) {
                                    return new PktUnlockPerk(true, message.perk);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            recUnlockResultClient(message);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void recUnlockResultClient(PktUnlockPerk message) {
        if(message.serverAccept) {
            ConstellationPerks perk = message.perk;
            GuiScreen current = Minecraft.getMinecraft().currentScreen;
            if(current != null && current instanceof GuiJournalPerkMap) {
                ((GuiJournalPerkMap) current).playUnlockAnimation(perk);
            }
        }
    }

}
