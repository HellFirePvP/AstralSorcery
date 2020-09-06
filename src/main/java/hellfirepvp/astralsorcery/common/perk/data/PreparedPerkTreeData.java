/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.node.RootPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PreparedPerkTreeData
 * Created by HellFirePvP
 * Date: 23.08.2020 / 12:23
 */
public class PreparedPerkTreeData {

    private final List<PerkTreePoint<AbstractPerk>> treePoints = new LinkedList<>();
    private final Map<AbstractPerk, Collection<AbstractPerk>> doubleConnections = new HashMap<>();
    private final List<Tuple<AbstractPerk, AbstractPerk>> connections = new LinkedList<>();

    private final Map<IConstellation, RootPerk> rootPerks = new HashMap<>();

    private long version = 0;

    PreparedPerkTreeData() {}

    static PreparedPerkTreeData create(Collection<LoadedPerkData> perks) {
        PreparedPerkTreeData treeData = new PreparedPerkTreeData();

        perks.stream().map(LoadedPerkData::getPerk).forEach(perk -> {
            if (perk instanceof RootPerk) {
                treeData.rootPerks.put(((RootPerk) perk).getConstellation(), (RootPerk) perk);
            }
            PerkTreePoint<? extends AbstractPerk> offsetPoint = perk.getPoint();
            if (treeData.treePoints.contains(offsetPoint)) {
                throw new IllegalArgumentException("Tried to register perk-point at already placed position: " + offsetPoint.getOffset().toString());
            }
            treeData.treePoints.add((PerkTreePoint<AbstractPerk>) offsetPoint);
        });
        perks.forEach(perkData -> {
            for (ResourceLocation connection : perkData.getConnections()) {
                AbstractPerk perkTo = treeData.getPerk(perk -> connection.equals(perk.getRegistryName()))
                        .orElseThrow(() -> new JsonParseException("Cannot connect to unknown perk: " + connection));
                treeData.getConnector(perkTo).ifPresent(connector -> {
                    connector.connect(perkData.getPerk());
                });
            }
        });

        treeData.version = treeData.computeTreeHash();
        return treeData;
    }

    public long getVersion() {
        return version;
    }

    public Optional<AbstractPerk> getPerk(Predicate<AbstractPerk> test) {
        return this.treePoints.stream().map(PerkTreePoint::getPerk).filter(test).findFirst();
    }

    public Optional<? extends AbstractPerk> getPerk(float x, float y) {
        return this.treePoints.stream()
                .filter(treePoint -> treePoint.getOffset().distance(x, y) <= 1E-4)
                .findFirst()
                .map(PerkTreePoint::getPerk);
    }

    @Nullable
    public RootPerk getRootPerk(IConstellation constellation) {
        return rootPerks.get(constellation);
    }

    public Collection<AbstractPerk> getConnectedPerks(AbstractPerk perk) {
        return doubleConnections.getOrDefault(perk, Lists.newArrayList());
    }

    public Collection<PerkTreePoint<?>> getPerkPoints() {
        return Collections.unmodifiableList(this.treePoints);
    }

    //Only for rendering purposes.
    @OnlyIn(Dist.CLIENT)
    public Collection<Tuple<AbstractPerk, AbstractPerk>> getConnections() {
        return Collections.unmodifiableList(this.connections);
    }

    private Optional<PointConnector> getConnector(AbstractPerk point) {
        if (point == null) {
            return Optional.empty();
        }
        if (this.treePoints.contains(point.getPoint())) {
            return Optional.of(new PointConnector(point));
        }
        return Optional.empty();
    }

    private long computeTreeHash() {
        long[] perkHash = new long[this.treePoints.size()];
        for (int i = 0; i < this.treePoints.size(); i++) {
            PerkTreePoint<? extends AbstractPerk> treePoint = this.treePoints.get(i);
            perkHash[i] = ((long) treePoint.getPerk().hashCode()) << 32 ^ treePoint.getOffset().hashCode();
        }
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

        private final AbstractPerk point;

        private PointConnector(AbstractPerk point) {
            this.point = point;
        }

        public PointConnector connect(AbstractPerk other) {
            if (other == null) {
                return this;
            }

            Collection<AbstractPerk> pointsTo = doubleConnections.computeIfAbsent(other, p -> new LinkedList<>());
            if (!pointsTo.contains(point)) {
                pointsTo.add(point);
            }
            pointsTo = doubleConnections.computeIfAbsent(point, p -> new LinkedList<>());
            if (!pointsTo.contains(other)) {
                pointsTo.add(other);
            }

            Tuple<AbstractPerk, AbstractPerk> connection = new Tuple<>(point, other);
            Tuple<AbstractPerk, AbstractPerk> reverse = new Tuple<>(other, point);
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
