package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkLevelManager;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkMap;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkMapRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktUnlockPerk
 * Created by HellFirePvP
 * Date: 12.12.2016 / 13:07
 */
public class PktUnlockPerk implements IMessage, IMessageHandler<PktUnlockPerk, IMessage> {

    private ConstellationPerks perk;
    private IMajorConstellation owningConstellation;

    public PktUnlockPerk() {}

    public PktUnlockPerk(ConstellationPerks perk, IMajorConstellation owningConstellation) {
        this.perk = perk;
        this.owningConstellation = owningConstellation;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ConstellationPerks perk = ConstellationPerks.getById(buf.readInt());
        if(perk != null) {
            this.perk = perk;
        }
        IMajorConstellation cst = ConstellationRegistry.getMajorConstellationByName(ByteBufUtils.readString(buf));
        if(cst != null) {
            this.owningConstellation = cst;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(perk.getSingleInstance().getId());
        ByteBufUtils.writeString(buf, owningConstellation.getUnlocalizedName());
    }

    @Override
    public IMessage onMessage(PktUnlockPerk message, MessageContext ctx) {
        if(ctx.side == Side.SERVER) {
            EntityPlayer pl = ctx.getServerHandler().playerEntity;
            if(pl != null) {
                if(perk != null && owningConstellation != null) {
                    ConstellationPerkMap map = ConstellationPerkMapRegistry.getPerkMap(owningConstellation);
                    if(map != null) {
                        PlayerProgress prog = ResearchManager.getProgress(pl, ctx.side);
                        if(prog != null) {
                            List<ConstellationPerk> appliedPerks = prog.getAppliedPerks();
                            if(!appliedPerks.contains(message.perk.getSingleInstance())) {
                                boolean canUnlock = ConstellationPerkLevelManager.canUnlockNextPerk(prog);
                                for (ConstellationPerkMap.Dependency d : map.getPerkDependencies()) {
                                    if(d.to.equals(message.perk)) {
                                        if(!appliedPerks.contains(d.from.getSingleInstance())) {
                                            canUnlock = false;
                                        }
                                    }
                                }
                                if(canUnlock) {
                                    ResearchManager.applyPerk(pl, message.perk);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
