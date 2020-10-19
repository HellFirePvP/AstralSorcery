/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.login.server;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.network.base.ASLoginPacket;
import hellfirepvp.astralsorcery.common.perk.PerkLevelManager;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.perk.data.PerkTreeData;
import hellfirepvp.astralsorcery.common.perk.data.PerkTreeLoader;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktLoginSyncPerkInformation
 * Created by HellFirePvP
 * Date: 23.08.2020 / 12:54
 */
public class PktLoginSyncPerkInformation extends ASLoginPacket<PktLoginSyncPerkInformation> {

    private List<JsonObject> rawPerkTreeData = new ArrayList<>();
    private int maxLevel = 0;

    public PktLoginSyncPerkInformation() {}

    public static PktLoginSyncPerkInformation makeLogin() {
        PktLoginSyncPerkInformation pkt = new PktLoginSyncPerkInformation();
        PerkTree.PERK_TREE.getLoginPerkData().ifPresent(treeData -> {
            pkt.rawPerkTreeData.addAll(treeData);
        });
        pkt.maxLevel = PerkLevelManager.getLevelCap(LogicalSide.SERVER, null);
        return pkt;
    }

    @Nonnull
    @Override
    public Encoder<PktLoginSyncPerkInformation> encoder() {
        return (pkt, buf) -> {
            ByteBufUtils.writeCollection(buf, pkt.rawPerkTreeData, ByteBufUtils::writeJsonObject);
            buf.writeInt(pkt.maxLevel);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktLoginSyncPerkInformation> decoder() {
        return buf -> {
            PktLoginSyncPerkInformation pkt = new PktLoginSyncPerkInformation();
            pkt.rawPerkTreeData = ByteBufUtils.readList(buf, ByteBufUtils::readJsonObject);
            pkt.maxLevel = buf.readInt();
            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktLoginSyncPerkInformation> handler() {
        return new Handler<PktLoginSyncPerkInformation>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktLoginSyncPerkInformation packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    PerkTreeData treeData = PerkTreeLoader.loadPerkTree(packet.rawPerkTreeData);
                    PerkTree.PERK_TREE.receivePerkTree(treeData.prepare());
                    PerkLevelManager.receiveLevelCap(packet.maxLevel);
                    acknowledge(context);
                });
            }

            @Override
            public void handle(PktLoginSyncPerkInformation packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
