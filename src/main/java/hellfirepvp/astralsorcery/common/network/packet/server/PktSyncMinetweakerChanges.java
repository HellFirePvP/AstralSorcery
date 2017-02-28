/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.SerializeableRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncMinetweakerChanges
 * Created by HellFirePvP
 * Date: 27.02.2017 / 01:51
 */
public class PktSyncMinetweakerChanges implements IMessage, IMessageHandler<PktSyncMinetweakerChanges, IMessage> {

    public SerializeableRecipe recipe;

    public PktSyncMinetweakerChanges() {}

    public PktSyncMinetweakerChanges(SerializeableRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int ordType = buf.readInt();
        SerializeableRecipe.CraftingType type = SerializeableRecipe.CraftingType.values()[ordType];
        recipe = type.newInstance();
        recipe.read(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(recipe.getType().ordinal());
        recipe.write(buf);
    }

    @Override
    public IMessage onMessage(PktSyncMinetweakerChanges p, MessageContext ctx) {
        p.recipe.applyClient();
        return null;
    }

    public static class Compound implements IMessage, IMessageHandler<Compound, IMessage> {

        public List<PktSyncMinetweakerChanges> parts = Lists.newArrayList();

        public Compound() {}

        @Override
        public void fromBytes(ByteBuf buf) {
            int count = buf.readInt();
            for (int i = 0; i < count; i++) {
                PktSyncMinetweakerChanges pkt = new PktSyncMinetweakerChanges();
                pkt.fromBytes(buf);
                parts.add(pkt);
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(parts.size());
            for (PktSyncMinetweakerChanges change : parts) {
                change.toBytes(buf);
            }
        }

        @Override
        public IMessage onMessage(Compound p, MessageContext ctx) {
            CraftingAccessManager.clearModifications();
            for (PktSyncMinetweakerChanges change : p.parts) {
                change.onMessage(change, ctx);
            }
            CraftingAccessManager.compile();
            return null;
        }

    }

}
