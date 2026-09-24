/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.data.PerkTreeData;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSyncPerkTree
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktSyncPerkTree extends PlayPacketHandler.ToClient<PktSyncPerkTree.Request> {

    static final CustomPacketPayload.Type<PktSyncPerkTree.Request> TYPE = makeType("sync_perk_tree");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            CodecUtil.rawJsonObjectStreamCodec().apply(ByteBufCodecs.list()),
            Request::rawPerkTree,
            Request::new
    );

    public static final PktSyncPerkTree HANDLER = new PktSyncPerkTree();

    private PktSyncPerkTree() {
        super(TYPE);
    }

    public static PktSyncPerkTree.Request sync() {
        List<JsonObject> treeData = new ArrayList<>();
        PerkTree.getInstance().getLoginPerkData().ifPresent(treeData::addAll);
        return new Request(treeData);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> this.handleClient(payload));
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient(Request payload) {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener == null) return;

        PerkTreeData data = PerkTreeData.load(payload.rawPerkTree(), listener.registryAccess());
        PerkTree.getInstance().receivePerkTree(data.prepare());
    }

    public static record Request(List<JsonObject> rawPerkTree) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
