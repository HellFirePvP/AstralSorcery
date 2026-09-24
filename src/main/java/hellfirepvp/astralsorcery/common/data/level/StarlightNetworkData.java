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
import hellfirepvp.astralsorcery.common.starlight.StarlightNetworkTickHelper;
import hellfirepvp.astralsorcery.common.starlight.api.ITransmissionTickable;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionSourceNode;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.observerlib.common.data.CachedWorldData;
import hellfirepvp.observerlib.common.data.WorldCacheDomain;
import hellfirepvp.observerlib.common.data.base.SectionWorldData;
import hellfirepvp.observerlib.common.data.base.WorldSection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarlightNetworkData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StarlightNetworkData extends SectionWorldData<StarlightNetworkData, StarlightNetworkData.ChunkNetworkData> {

    public static final Codec<StarlightNetworkData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            WorldCacheDomain.SaveKey.CODEC.fieldOf("key").forGetter(CachedWorldData::getSaveKey)
    ).apply(inst, key -> new StarlightNetworkData(MiscUtil.cast(key))));

    private final Map<BlockPos, TransmissionSourceNode> sources = new HashMap<>();

    public StarlightNetworkData(WorldCacheDomain.SaveKey<StarlightNetworkData> key) {
        super(key, ChunkNetworkData.CODEC, PRECISION_CHUNK);
    }

    @Override
    protected ChunkNetworkData createNewSection(int sX, int sZ) {
        return new ChunkNetworkData(sX, sZ, new HashMap<>());
    }

    private Map<BlockPos, TransmissionSourceNode> getSources() {
        return Collections.unmodifiableMap(this.sources);
    }

    public Collection<TransmissionSourceNode> getSourceNodes() {
        return Collections.unmodifiableCollection(this.sources.values());
    }

    @Override
    public void onLoad(Level world) {
        super.onLoad(world);

        this.getSections().forEach(section -> {
            section.getNodes().forEach(node -> {
                if (node instanceof ITransmissionTickable tickableNode) {
                    StarlightNetworkTickHelper.getInstance().addNodeUpdate(world, tickableNode);
                }
                if (node instanceof TransmissionSourceNode sourceNode) {
                    this.sources.put(sourceNode.getNodePos(), sourceNode);
                }
            });
        });
    }

    public void addTransmissionNode(TransmissionNode node) {
        BlockPos nodePos = node.getNodePos();
        this.getOrCreateSection(nodePos).addNode(nodePos, node);
        if (node instanceof TransmissionSourceNode sourceNode) {
            this.sources.put(nodePos, sourceNode);
        }
        this.markDirty(nodePos);
    }

    public boolean removeTransmissionNode(BlockPos pos) {
        ChunkNetworkData section = this.getSection(pos);
        if (section == null || section.isEmpty()) return false;
        if (!section.removeNode(pos)) return false;
        this.sources.remove(pos);
        this.markDirty(pos);
        return true;
    }

    public static class ChunkNetworkData extends WorldSection {

        public static final Codec<ChunkNetworkData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("sX").forGetter(WorldSection::getSectionX),
                Codec.INT.fieldOf("sZ").forGetter(WorldSection::getSectionZ),
                Codec.unboundedMap(CodecUtil.stringBlockPos(), TransmissionNode.CODEC).fieldOf("nodes").forGetter(data -> data.nodes)
        ).apply(inst, ChunkNetworkData::new));

        private final Map<BlockPos, TransmissionNode> nodes = new HashMap<>();

        protected ChunkNetworkData(int sX, int sZ, Map<BlockPos, TransmissionNode> nodes) {
            super(sX, sZ);
            this.nodes.putAll(nodes);
        }

        public boolean isEmpty() {
            return this.nodes.isEmpty();
        }

        public Iterable<TransmissionNode> getNodes() {
            return Collections.unmodifiableCollection(this.nodes.values());
        }

        public Optional<TransmissionNode> getNode(BlockPos pos) {
            return Optional.ofNullable(this.nodes.get(pos));
        }

        protected boolean removeNode(BlockPos pos) {
            return this.nodes.remove(pos) != null;
        }

        protected void addNode(BlockPos pos, TransmissionNode node) {
            this.nodes.put(pos, node);
        }
    }
}
