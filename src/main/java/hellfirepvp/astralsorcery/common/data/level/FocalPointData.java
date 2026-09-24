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
import hellfirepvp.astralsorcery.common.focal.node.FocalPointNode;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColumnPos;
import hellfirepvp.observerlib.common.data.CachedWorldData;
import hellfirepvp.observerlib.common.data.WorldCacheDomain;
import hellfirepvp.observerlib.common.data.base.SectionWorldData;
import hellfirepvp.observerlib.common.data.base.WorldSection;
import hellfirepvp.observerlib.common.util.CodecUtil;
import net.minecraft.core.BlockPos;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalPointNode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalPointData extends SectionWorldData<FocalPointData, FocalPointData.Section> {

    public static final Codec<FocalPointData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            WorldCacheDomain.SaveKey.CODEC.fieldOf("key").forGetter(CachedWorldData::getSaveKey)
    ).apply(inst, key -> new FocalPointData(MiscUtil.cast(key))));

    public FocalPointData(WorldCacheDomain.SaveKey<FocalPointData> key) {
        super(key, Section.CODEC, PRECISION_SECTION, true);
    }

    @Override
    protected Section createNewSection(int sectionX, int sectionZ) {
        return new Section(sectionX, sectionZ);
    }

    public Optional<FocalPointNode> getNode(BlockPos pos) {
        Section section = this.getSection(pos);
        if (section == null) return Optional.empty();
        return Optional.ofNullable(section.focalPoints.get(ColumnPos.of(pos)));
    }

    public void addNode(FocalPointNode node) {
        Section section = this.getOrCreateSection(node.getPos().toBlockPos(0));
        this.write(() -> {
            section.focalPoints.put(node.getPos(), node);
            this.markDirty(section);
        });
    }

    public void removeNode(FocalPointNode node) {
        Section section = this.getSection(node.getPos().toBlockPos(0));
        if (section != null) {
            this.write(() -> {
                if (section.focalPoints.remove(node.getPos()) != null) {
                    this.markDirty(section);
                }
            });
        }
    }

    public List<FocalPointNode> getNodesNear(BlockPos pos, int searchRadius) {
        int searchRadiusSq = searchRadius * searchRadius;
        return this.getSections(pos.offset(-searchRadius, 0, -searchRadius), pos.offset(searchRadius, 0, searchRadius)).stream()
                .flatMap(section -> section.getFocalPoints().stream()
                        .filter(node -> node.getPos().distSqr(pos) <= searchRadiusSq))
                .toList();
    }

    public static class Section extends WorldSection {

        public static final Codec<Section> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("sX").forGetter(WorldSection::getSectionX),
                Codec.INT.fieldOf("sZ").forGetter(WorldSection::getSectionZ),
                FocalPointNode.CODEC.listOf().fieldOf("focalPoints").forGetter(section -> new ArrayList<>(section.focalPoints.values()))
        ).apply(inst, Section::new));

        private final Map<ColumnPos, FocalPointNode> focalPoints;

        private Section(int sX, int sZ) {
            this(sX, sZ, new ArrayList<>());
        }

        private Section(int sX, int sZ, List<FocalPointNode> focalPoints) {
            super(sX, sZ);
            this.focalPoints = focalPoints.stream().collect(Collectors.toMap(FocalPointNode::getPos, Function.identity()));
        }

        public Collection<FocalPointNode> getFocalPoints() {
            return Collections.unmodifiableCollection(this.focalPoints.values());
        }
    }
}
