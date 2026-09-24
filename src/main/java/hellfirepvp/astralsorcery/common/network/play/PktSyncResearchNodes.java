/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.data.ResearchNodeLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSyncResearchNodes
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktSyncResearchNodes extends PlayPacketHandler.ToClient<PktSyncResearchNodes.Request> {

    static final CustomPacketPayload.Type<PktSyncResearchNodes.Request> TYPE = makeType("sync_research_nodes");
    static final StreamCodec<RegistryFriendlyByteBuf, PktSyncResearchNodes.Request> CODEC =
            ResearchNode.SYNC_CODEC.apply(ByteBufCodecs.list()).map(Request::new, Request::nodes);

    public static final PktSyncResearchNodes HANDLER = new PktSyncResearchNodes();

    private PktSyncResearchNodes() {
        super(TYPE);
    }

    public static Request newRequest() {
        return new Request(ResearchNodeLoader.getInstance().getNodes());
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> ResearchNodeLoader.getInstance().updateServerNodes(payload.nodes()));
    }

    public static record Request(List<ResearchNode> nodes) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
