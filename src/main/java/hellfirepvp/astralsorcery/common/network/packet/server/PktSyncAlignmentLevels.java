/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkLevelManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncAlignmentLevels
 * Created by HellFirePvP
 * Date: 27.12.2016 / 03:28
 */
public class PktSyncAlignmentLevels implements IMessage, IMessageHandler<PktSyncAlignmentLevels, IMessage> {

    private int[] levels = null;

    public PktSyncAlignmentLevels() {}

    public PktSyncAlignmentLevels(int[] levels) {
        this.levels = levels;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        levels = new int[buf.readInt()];
        for (int i = 0; i < levels.length; i++) {
            levels[i] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(levels.length);
        for (int lvl : levels) {
            buf.writeInt(lvl);
        }
    }

    @Override
    public IMessage onMessage(PktSyncAlignmentLevels message, MessageContext ctx) {
        ConstellationPerkLevelManager.levelsRequiredClientCache = ConstellationPerkLevelManager.levelsRequired;
        ConstellationPerkLevelManager.levelsRequired = message.levels;
        return null;
    }
}
