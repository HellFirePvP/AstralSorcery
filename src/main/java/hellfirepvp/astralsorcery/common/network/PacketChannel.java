package hellfirepvp.astralsorcery.common.network;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.network.packet.client.PktAttuneConstellation;
import hellfirepvp.astralsorcery.common.network.packet.client.PktDiscoverConstellation;
import hellfirepvp.astralsorcery.common.network.packet.client.PktRequestSeed;
import hellfirepvp.astralsorcery.common.network.packet.client.PktUnlockPerk;
import hellfirepvp.astralsorcery.common.network.packet.server.PktAttunementAltarState;
import hellfirepvp.astralsorcery.common.network.packet.server.PktCraftingTableFix;
import hellfirepvp.astralsorcery.common.network.packet.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.network.packet.server.PktProgressionUpdate;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncAlignmentLevels;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncConfig;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncData;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncKnowledge;
import hellfirepvp.astralsorcery.common.network.packet.server.PktUpdateReach;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PacketChannel
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:11
 */
public class PacketChannel {

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(AstralSorcery.NAME);

    public static void init() {
        int id = 0;

        //Synchronizing data (server -> client)
        CHANNEL.registerMessage(PktSyncConfig.class, PktSyncConfig.class, id++, Side.CLIENT); //General config sync
        CHANNEL.registerMessage(PktSyncKnowledge.class, PktSyncKnowledge.class, id++, Side.CLIENT); //Knowledge data sync
        CHANNEL.registerMessage(PktSyncData.class, PktSyncData.class, id++, Side.CLIENT); //General dataholder sync
        CHANNEL.registerMessage(PktParticleEvent.class, PktParticleEvent.class, id++, Side.CLIENT); //Generic particle event
        CHANNEL.registerMessage(PktCraftingTableFix.class, PktCraftingTableFix.class, id++, Side.CLIENT); //Sends a crafting table GUI-open packet that has a gui with proper data.
        CHANNEL.registerMessage(PktProgressionUpdate.class, PktProgressionUpdate.class, id++, Side.CLIENT); //Sends visual packet that player has discovered research 'X'
        //CHANNEL.registerMessage(PktSpawnWorldParticles.class, PktSpawnWorldParticles.class, id++, Side.CLIENT); //General std world-particles
        CHANNEL.registerMessage(PktPlayEffect.class, PktPlayEffect.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktRequestSeed.class, PktRequestSeed.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktUpdateReach.class, PktUpdateReach.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktUnlockPerk.class, PktUnlockPerk.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktSyncAlignmentLevels.class, PktSyncAlignmentLevels.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktAttunementAltarState.class, PktAttunementAltarState.class, id++, Side.CLIENT);

        //Constellation discovery (client -> server)
        CHANNEL.registerMessage(PktDiscoverConstellation.class, PktDiscoverConstellation.class, id++, Side.SERVER); //discover a constellation in gui and inform server.
        CHANNEL.registerMessage(PktRequestSeed.class, PktRequestSeed.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktUnlockPerk.class, PktUnlockPerk.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktAttunementAltarState.class, PktAttunementAltarState.class, id++, Side.SERVER);
        CHANNEL.registerMessage(PktAttuneConstellation.class, PktAttuneConstellation.class, id++, Side.SERVER);
    }

    public static NetworkRegistry.TargetPoint pointFromPos(World world, BlockPos pos, double range) {
        return new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), range);
    }

}
