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
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.observerlib.common.data.CachedWorldData;
import hellfirepvp.observerlib.common.data.WorldCacheDomain;
import hellfirepvp.observerlib.common.data.base.SectionWorldData;
import hellfirepvp.observerlib.common.data.base.WorldSection;
import hellfirepvp.observerlib.common.util.CodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RockCrystalData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RockCrystalData extends SectionWorldData<RockCrystalData, RockCrystalData.Section> {

    public static final Codec<RockCrystalData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            WorldCacheDomain.SaveKey.CODEC.fieldOf("key").forGetter(CachedWorldData::getSaveKey)
    ).apply(inst, key -> new RockCrystalData(MiscUtil.cast(key))));

    public RockCrystalData(WorldCacheDomain.SaveKey<RockCrystalData> key) {
        super(key, Section.CODEC, PRECISION_REGION, true);
    }

    @Override
    protected Section createNewSection(int sectionX, int sectionZ) {
        return new Section(sectionX, sectionZ, new HashSet<>());
    }

    public List<BlockPos> collectPositions(ChunkPos center, int chunkRadius) {
        List<BlockPos> out = new LinkedList<>();
        for (int xx = -chunkRadius; xx <= chunkRadius; xx++) {
            for (int zz = -chunkRadius; zz <= chunkRadius; zz++) {
                ChunkPos other = new ChunkPos(center.x + xx, center.z + zz);
                Section section = this.getSection(other.getWorldPosition());
                if (section != null) {
                    this.read(() -> out.addAll(section.crystalPositions));
                }
            }
        }
        return out;
    }

    public void addOre(BlockPos pos) {
        Section section = this.getOrCreateSection(pos);
        this.write(() -> {
            if (section.crystalPositions.add(pos)) {
                this.markDirty(section);
            }
        });
    }

    public void removeOre(BlockPos pos) {
        Section section = this.getSection(pos);
        if (section != null) {
            this.write(() -> {
                if (section.crystalPositions.remove(pos)) {
                    this.markDirty(section);
                }
            });
        }
    }

    public static class Section extends WorldSection {

        public static final Codec<Section> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("sX").forGetter(WorldSection::getSectionX),
                Codec.INT.fieldOf("sZ").forGetter(WorldSection::getSectionZ),
                SetCodec.of(BlockPos.CODEC).fieldOf("positions").forGetter(Section::getCrystalPositions)
        ).apply(inst, Section::new));

        private final Set<BlockPos> crystalPositions = new HashSet<>();

        private Section(int sX, int sZ, Set<BlockPos> positions) {
            super(sX, sZ);
            this.crystalPositions.addAll(positions);
        }

        private Set<BlockPos> getCrystalPositions() {
            return this.crystalPositions;
        }
    }
}
