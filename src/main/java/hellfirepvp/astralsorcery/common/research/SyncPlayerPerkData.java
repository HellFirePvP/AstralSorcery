/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research;

import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.astralsorcery.common.util.data.MapStream;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SyncPlayerPerkData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SyncPlayerPerkData {

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerPerkData> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(SetCodec.streamOp()),
            SyncPlayerPerkData::getPointTokens,
            ByteBufCodecs.map(size -> new HashMap<>(),
                    ResourceLocation.STREAM_CODEC,
                    PlayerPerkData.AppliedPerkData.STREAM_CODEC),
            SyncPlayerPerkData::getPerks,
            ByteBufCodecs.DOUBLE,
            SyncPlayerPerkData::getPerkExp,
            SyncPlayerPerkData::new
    );

    private final Set<ResourceLocation> pointTokens = new HashSet<>();
    private final Map<ResourceLocation, PlayerPerkData.AppliedPerkData<?>> perks = new HashMap<>();
    private final double perkExp;

    private SyncPlayerPerkData(Set<ResourceLocation> pointTokens, Map<ResourceLocation, PlayerPerkData.AppliedPerkData<?>> perks, double perkExp) {
        this.pointTokens.addAll(pointTokens);
        this.perks.putAll(perks);
        this.perkExp = perkExp;
    }

    public static SyncPlayerPerkData sync(PlayerPerkData data) {
        Map<ResourceLocation, PlayerPerkData.AppliedPerkData<?>> perkData = new HashMap<>();
        data.getPerks().forEach((perk, appliedData) -> perkData.put(perk.getKey(), appliedData.copy()));
        return new SyncPlayerPerkData(Set.copyOf(data.getPointTokens()), perkData, data.getPerkExp());
    }

    private Set<ResourceLocation> getPointTokens() {
        return this.pointTokens;
    }

    private Map<ResourceLocation, PlayerPerkData.AppliedPerkData<?>> getPerks() {
        return this.perks;
    }

    private double getPerkExp() {
        return this.perkExp;
    }

    public PlayerPerkData load() {
        return new PlayerPerkData(this.getPointTokens(),
                MiscUtil.cast(MapStream.of(this.getPerks())
                        .mapKey(key -> PerkTree.getInstance().getPerk(LogicalSide.CLIENT, key).orElseThrow())
                        .toMap()),
                this.getPerkExp());
    }

}
