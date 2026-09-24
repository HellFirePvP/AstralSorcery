/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.perk.tree.perk.RootPerk;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BakedPerkTreeData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BakedPerkTreeData {

    private final List<PerkTreePoint<AbstractPerk<?>>> treePoints = new ArrayList<>();
    private final Map<AbstractPerk<?>, Collection<AbstractPerk<?>>> doubleConnections = new HashMap<>();
    private final List<Tuple<AbstractPerk<?>, AbstractPerk<?>>> connections = new ArrayList<>();

    private final Map<BaseConstellation, RootPerk<?>> rootPerks = new HashMap<>();

    private long version = 0;

    BakedPerkTreeData() {}

    static BakedPerkTreeData create(Collection<RawPerkData> perks) {
        BakedPerkTreeData treeData = new BakedPerkTreeData();

        perks.stream().map(RawPerkData::perk).forEach(perk -> {
            if (perk instanceof RootPerk<?> rootPerk) {
                treeData.rootPerks.put(rootPerk.getConstellation(), rootPerk);
            }
            PerkTreePoint<? extends AbstractPerk<?>> offsetPoint = perk.getPerkTreePoint();
            if (treeData.treePoints.contains(offsetPoint)) {
                throw new IllegalArgumentException("Tried to register perk-point at already placed position: " + offsetPoint.getOffset().toString());
            }
            treeData.treePoints.add(MiscUtil.cast(offsetPoint));
        });
        perks.forEach(perkData -> {
            for (ResourceLocation connection : perkData.connections()) {
                AbstractPerk<?> perkTo = treeData.getPerk(perk -> connection.equals(perk.getKey()))
                        .orElseThrow(() -> new JsonParseException("Cannot connect to unknown perk: " + connection));
                treeData.getConnector(perkTo).ifPresent(connector -> {
                    connector.connect(perkData.perk());
                });
            }
        });

        treeData.version = treeData.computeTreeHash();
        return treeData;
    }

    public long getVersion() {
        return this.version;
    }

    public Optional<? extends AbstractPerk<?>> getPerk(Predicate<AbstractPerk<?>> test) {
        return this.treePoints.stream().map(PerkTreePoint::getPerk).filter(test).findFirst();
    }

    public Optional<? extends AbstractPerk<?>> getPerk(float x, float y) {
        return this.treePoints.stream()
                .filter(treePoint -> treePoint.getOffset().distance(x, y) <= 1E-4)
                .findFirst()
                .map(PerkTreePoint::getPerk);
    }

    @Nullable
    public RootPerk<?> getRootPerk(BaseConstellation constellation) {
        return this.rootPerks.get(constellation);
    }

    public Collection<AbstractPerk<?>> getConnectedPerks(AbstractPerk<?> perk) {
        return this.doubleConnections.getOrDefault(perk, Lists.newArrayList());
    }

    public Collection<PerkTreePoint<?>> getPerkPoints() {
        return Collections.unmodifiableList(this.treePoints);
    }

    //Only for rendering purposes.
    @OnlyIn(Dist.CLIENT)
    public Collection<Tuple<AbstractPerk<?>, AbstractPerk<?>>> getConnections() {
        return Collections.unmodifiableList(this.connections);
    }

    private Optional<PointConnector> getConnector(AbstractPerk<?> point) {
        if (point == null) {
            return Optional.empty();
        }
        if (this.treePoints.contains(point.getPerkTreePoint())) {
            return Optional.of(new PointConnector(point));
        }
        return Optional.empty();
    }

    private long computeTreeHash() {
        long[] perkHash = new long[this.treePoints.size()];
        for (int i = 0; i < this.treePoints.size(); i++) {
            PerkTreePoint<? extends AbstractPerk<?>> treePoint = this.treePoints.get(i);
            perkHash[i] = ((long) treePoint.getPerk().getKey().hashCode()) << 32 ^ (treePoint.getOffset().hashCode() & 0xFFFFFFFFL);
        }
        Arrays.sort(perkHash);

        long hash = 1L;
        for (long element : perkHash) {
            long elementHash = element ^ (element >>> 32);
            hash = 31 * hash + elementHash;
        }
        return hash;
    }

    public void clearPerkCache(LogicalSide side) {
        this.treePoints.stream().map(PerkTreePoint::getPerk).forEach(p -> p.clearCaches(side));
    }

    public class PointConnector {

        private final AbstractPerk<?> point;

        private PointConnector(AbstractPerk<?> point) {
            this.point = point;
        }

        public PointConnector connect(AbstractPerk<?> other) {
            if (other == null) {
                return this;
            }

            Collection<AbstractPerk<?>> pointsTo = doubleConnections.computeIfAbsent(other, p -> new LinkedList<>());
            if (!pointsTo.contains(this.point)) {
                pointsTo.add(this.point);
            }
            pointsTo = doubleConnections.computeIfAbsent(this.point, p -> new LinkedList<>());
            if (!pointsTo.contains(other)) {
                pointsTo.add(other);
            }

            Tuple<AbstractPerk<?>, AbstractPerk<?>> connection = new Tuple<>(this.point, other);
            Tuple<AbstractPerk<?>, AbstractPerk<?>> reverse = new Tuple<>(other, this.point);
            if (!connections.contains(connection) && !connections.contains(reverse)) {
                connections.add(connection);
            }
            return this;
        }

        public PointConnector connect(PointConnector other) {
            return connect(other.point);
        }

        public PointConnector chain(PointConnector other) {
            connect(other.point);
            return other;
        }
    }
}
