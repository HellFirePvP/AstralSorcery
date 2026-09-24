/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.transfer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.RayTraceUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.astralsorcery.common.util.data.MapStream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenNode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenNode {

    public static final Codec<LumenNode> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(LumenNode::getPos),
            Vec3.CODEC.fieldOf("los_pos").forGetter(LumenNode::getLineOfSightPos),
            CodecUtil.enumCodec(ConnectionType.class).fieldOf("connection_type").forGetter(LumenNode::getConnectionType),
            SetCodec.of(BlockPos.CODEC).fieldOf("linked_positions").forGetter(LumenNode::getLinkedPositions),
            SetCodec.of(RegistriesAS.REGISTRY_LUMEN.byNameCodec()).fieldOf("provided_lumen_types").forGetter(LumenNode::getMutableProvidedLumenTypes),
            Codec.unboundedMap(RegistriesAS.REGISTRY_LUMEN.byNameCodec(), SetCodec.of(RelayDistance.CODEC))
                    .fieldOf("relayed_lumen_types").forGetter(LumenNode::getRelayedLumenTypes)
    ).apply(inst, LumenNode::new));

    private final BlockPos pos;
    private final Vec3 losPos;
    private final ConnectionType connectionType;
    private final Set<BlockPos> linkedPositions = new HashSet<>();

    private final Set<Lumen> providedLumenTypes = new HashSet<>();
    private final Map<Lumen, Set<RelayDistance>> relayedLumenTypes = new HashMap<>();

    public LumenNode(BlockPos pos, Vec3 losPos, ConnectionType connectionType) {
        this.pos = pos;
        this.losPos = losPos;
        this.connectionType = connectionType;
    }

    private LumenNode(BlockPos pos, Vec3 losPos, ConnectionType connectionType, Set<BlockPos> linkedPositions, Set<Lumen> providedLumenTypes, Map<Lumen, Set<RelayDistance>> relayedLumenTypes) {
        this.pos = pos;
        this.losPos = losPos;
        this.connectionType = connectionType;
        this.linkedPositions.addAll(linkedPositions);
        this.providedLumenTypes.addAll(providedLumenTypes);
        this.relayedLumenTypes.putAll(relayedLumenTypes);
    }

    public final BlockPos getPos() {
        return this.pos;
    }

    public final Vec3 getLineOfSightPos() {
        return this.losPos;
    }

    public final ConnectionType getConnectionType() {
        return this.connectionType;
    }

    public boolean canConnect(Level level, LumenNode other) {
        ClipContext ctx = new ClipContext(this.getLineOfSightPos(), other.getLineOfSightPos(),
                ClipContext.Block.VISUAL, ClipContext.Fluid.WATER,
                CollisionContext.empty());
        return RayTraceUtil.clip(level, ctx, Set.of(this.getPos(), other.getPos())).getType() == HitResult.Type.MISS;
    }

    public <T extends IBlockEntityExtension> Optional<T> getTile(Level level, Class<T> clazz) {
        return this.getTile(level, clazz, true);
    }

    public <T extends IBlockEntityExtension> Optional<T> getTile(Level level, Class<T> clazz, boolean forceLoad) {
        return MiscUtil.getTileAt(level, this.getPos(), clazz, forceLoad);
    }

    Set<BlockPos> getLinkedPositions() {
        return this.linkedPositions;
    }

    Set<Lumen> getMutableProvidedLumenTypes() {
        return this.providedLumenTypes;
    }

    public Set<Lumen> getProvidedLumenTypes() {
        return Collections.unmodifiableSet(this.providedLumenTypes);
    }

    void removeRelay(BlockPos providerSrc) {
        this.relayedLumenTypes.values().forEach(relays ->
                relays.removeIf(relay -> relay.srcPos().equals(providerSrc)));

        List<Lumen> lumenTypes = MapStream.of(this.relayedLumenTypes).filterValue(Set::isEmpty).map(Tuple::getA).toList();
        lumenTypes.forEach(this.relayedLumenTypes::remove);
    }

    void updateRelayType(Lumen lumen, BlockPos providerSrc, int distance) {
        this.relayedLumenTypes.computeIfAbsent(lumen, l -> new HashSet<>())
                .removeIf(relay -> relay.srcPos().equals(providerSrc));
        this.addRelayedLumenType(lumen, providerSrc, distance);
    }

    void addRelayedLumenType(Lumen lumen, BlockPos providerSrc, int distance) {
        this.relayedLumenTypes.computeIfAbsent(lumen, l -> new HashSet<>()).add(new RelayDistance(providerSrc, distance));
    }

    int getRelayDistance(Lumen type, BlockPos src) {
        Set<RelayDistance> distances = this.relayedLumenTypes.get(type);
        if (distances == null || distances.isEmpty()) return -1;
        return distances.stream()
                .filter(relay -> relay.srcPos().equals(src))
                .map(RelayDistance::hopDistance)
                .findFirst()
                .orElse(-1);
    }

    public boolean doesRelayLumenType(Lumen type) {
        return this.relayedLumenTypes.containsKey(type) && !this.relayedLumenTypes.get(type).isEmpty();
    }

    public Optional<Integer> getMinRelayDistance(Lumen type) {
        Set<RelayDistance> distances = this.relayedLumenTypes.get(type);
        if (distances == null || distances.isEmpty()) return Optional.empty();
        return distances.stream()
                .map(RelayDistance::hopDistance)
                .min(Integer::compareTo);
    }

    public Map<Lumen, Set<RelayDistance>> getRelayedLumenTypes() {
        return Collections.unmodifiableMap(this.relayedLumenTypes);
    }

    public enum ConnectionType {

        SOURCE,
        TRANSMISSION,
        RECEIVER

    }

    public record RelayDistance(BlockPos srcPos, int hopDistance) {

        static final Codec<RelayDistance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BlockPos.CODEC.fieldOf("src").forGetter(RelayDistance::srcPos),
                Codec.INT.fieldOf("dst").forGetter(RelayDistance::hopDistance)
        ).apply(inst, RelayDistance::new));

    }
}
