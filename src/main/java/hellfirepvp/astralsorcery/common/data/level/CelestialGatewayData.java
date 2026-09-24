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
import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.observerlib.common.data.WorldCacheDomain;
import hellfirepvp.observerlib.common.data.base.GlobalWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialGatewayData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CelestialGatewayData extends GlobalWorldData<CelestialGatewayData> {

    public static final Codec<CelestialGatewayData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            WorldCacheDomain.SaveKey.CODEC.fieldOf("key").forGetter(CelestialGatewayData::getSaveKey),
            SetCodec.of(GatewayEntry.CODEC).fieldOf("entries").forGetter(CelestialGatewayData::getGatewayEntries)
    ).apply(inst, (key, entries) -> new CelestialGatewayData(MiscUtil.cast(key), entries)));

    private final Map<BlockPos, GatewayEntry> gatewayEntries = new HashMap<>();

    public CelestialGatewayData(WorldCacheDomain.SaveKey<CelestialGatewayData> key) {
        this(key, Collections.emptySet());
    }

    private CelestialGatewayData(WorldCacheDomain.SaveKey<CelestialGatewayData> key, Set<GatewayEntry> entries) {
        super(key);
        entries.forEach(entry -> this.gatewayEntries.put(entry.getPos(), entry));
    }

    public Set<GatewayEntry> getGatewayEntries() {
        return Set.copyOf(this.gatewayEntries.values());
    }

    public Optional<GatewayEntry> getGateway(BlockPos pos) {
        return Optional.ofNullable(this.gatewayEntries.get(pos));
    }

    public void addGateway(Level level, BlockPos pos, Component customName, DyeColor color) {
        this.removeGateway(level, pos);

        GatewayEntry entry = new GatewayEntry(pos, Optional.ofNullable(customName), color);
        this.gatewayEntries.put(pos, entry);
        this.markDirty();
        SyncDataManager.getInstance().getData(SyncDataTypesAS.CELESTIAL_GATEWAY).addEntry(level, entry);
    }

    public void removeGateway(Level level, BlockPos pos) {
        GatewayEntry entry = this.gatewayEntries.remove(pos);
        this.markDirty();
        if (entry != null) {
            SyncDataManager.getInstance().getData(SyncDataTypesAS.CELESTIAL_GATEWAY).removeEntry(level, entry);
        }
    }

    @Override
    public void writeAdditionalData(File saveDir, File backupDir) throws IOException {}

    @Override
    public void readAdditionalData(File directory, FileLoader<?> fileLoader) {}

    @Override
    public void onLoad(Level world) {
        super.onLoad(world);

        List.copyOf(this.gatewayEntries.keySet()).forEach(pos -> {
            if (!MiscUtil.getTileExists(world, pos, TileCelestialGateway.class, true)) {
                this.gatewayEntries.remove(pos);
                this.markDirty();
            }
        });
    }

    public static class GatewayEntry {

        public static final Codec<GatewayEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(GatewayEntry::getPos),
                CodecUtil.optional(ComponentSerialization.CODEC, "customName", GatewayEntry::getCustomName),
                DyeColor.CODEC.fieldOf("color").forGetter(GatewayEntry::getColor)
        ).apply(inst, GatewayEntry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GatewayEntry> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                GatewayEntry::getPos,
                ComponentSerialization.OPTIONAL_STREAM_CODEC,
                entry -> Optional.ofNullable(entry.getCustomName()),
                DyeColor.STREAM_CODEC,
                GatewayEntry::getColor,
                GatewayEntry::new
        );

        private final BlockPos pos;
        private final Component customName;
        private final DyeColor color;

        public GatewayEntry(BlockPos pos, Optional<Component> customName, DyeColor color) {
            this.pos = pos;
            this.customName = customName.orElse(null);
            this.color = color;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        @Nullable
        public Component getCustomName() {
            return this.customName;
        }

        public DyeColor getColor() {
            return ObjectUtils.firstNonNull(this.color, DyeColor.YELLOW);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            GatewayEntry that = (GatewayEntry) o;
            return Objects.equals(pos, that.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(pos);
        }
    }
}
