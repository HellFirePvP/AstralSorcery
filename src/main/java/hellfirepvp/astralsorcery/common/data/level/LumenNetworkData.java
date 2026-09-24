/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenNode;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.observerlib.common.data.CachedWorldData;
import hellfirepvp.observerlib.common.data.WorldCacheDomain;
import hellfirepvp.observerlib.common.data.base.SectionWorldData;
import hellfirepvp.observerlib.common.data.base.WorldSection;
import net.minecraft.core.BlockPos;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenNetworkData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenNetworkData extends SectionWorldData<LumenNetworkData, LumenNetworkData.LumenNodeSection> {

    public static final Codec<LumenNetworkData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            WorldCacheDomain.SaveKey.CODEC.fieldOf("key").forGetter(CachedWorldData::getSaveKey)
    ).apply(inst, key -> new LumenNetworkData(MiscUtil.cast(key))));

    public LumenNetworkData(WorldCacheDomain.SaveKey<LumenNetworkData> key) {
        super(key, LumenNodeSection.CODEC, PRECISION_CHUNK, true);
    }

    @Override
    protected LumenNodeSection createNewSection(int sectionX, int sectionZ) {
        return new LumenNodeSection(sectionX, sectionZ, new HashMap<>());
    }

    public List<LumenNode> collectNearbyNodes(BlockPos pos, int distance) {
        if (distance <= 0) return Collections.emptyList();
        int distanceSq = distance * distance;
        return this.getSections(pos.offset(-distance, 0, -distance), pos.offset(distance, 0, distance)).stream()
                .flatMap(section -> section.getNodes().stream())
                .filter(node -> node.getPos().distSqr(pos) <= distanceSq)
                .toList();
    }

    public Optional<LumenNode> getLumenNode(BlockPos pos) {
        LumenNodeSection section = this.getSection(pos);
        if (section == null || section.isEmpty()) return Optional.empty();
        return section.getNode(pos);
    }

    public void addLumenNode(BlockPos pos, LumenNode node) {
        this.getOrCreateSection(pos).addNode(pos, node);
        this.markDirty(pos);
    }

    public boolean removeLumenNode(BlockPos pos) {
        LumenNodeSection section = this.getSection(pos);
        if (section == null || section.isEmpty()) return false;
        if (!section.removeNode(pos)) return false;
        this.markDirty(pos);
        return true;
    }

    public static class LumenNodeSection extends WorldSection {

        public static final Codec<LumenNodeSection> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("sX").forGetter(WorldSection::getSectionX),
                Codec.INT.fieldOf("sZ").forGetter(WorldSection::getSectionZ),
                Codec.unboundedMap(CodecUtil.stringBlockPos(), LumenNode.CODEC).fieldOf("nodes").forGetter(data -> data.nodes)
        ).apply(inst, LumenNodeSection::new));

        private final Map<BlockPos, LumenNode> nodes = new HashMap<>();

        protected LumenNodeSection(int sX, int sZ, Map<BlockPos, LumenNode> nodes) {
            super(sX, sZ);
            this.nodes.putAll(nodes);
        }

        public boolean isEmpty() {
            return this.nodes.isEmpty();
        }

        public Collection<LumenNode> getNodes() {
            return Collections.unmodifiableCollection(this.nodes.values());
        }

        public Optional<LumenNode> getNode(BlockPos pos) {
            return Optional.ofNullable(this.nodes.get(pos));
        }

        protected boolean removeNode(BlockPos pos) {
            return this.nodes.remove(pos) != null;
        }

        protected void addNode(BlockPos pos, LumenNode node) {
            this.nodes.put(pos, node);
        }
    }
}
