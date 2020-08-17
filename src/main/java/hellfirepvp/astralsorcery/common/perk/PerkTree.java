/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.config.entry.PerkConfig;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.data.ConnectedPerkData;
import hellfirepvp.astralsorcery.common.perk.data.PerkTreeData;
import hellfirepvp.astralsorcery.common.perk.node.RootPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTree
 * Created by HellFirePvP
 * Date: 02.06.2019 / 08:31
 */
public class PerkTree {

    public static final PerkTree PERK_TREE = new PerkTree();

    private Long treeVersion = null;

    private final List<PerkTreePoint<AbstractPerk>> treePoints = new LinkedList<>();
    private final Map<AbstractPerk, Collection<AbstractPerk>> doubleConnections = new HashMap<>();
    private final List<Tuple<AbstractPerk, AbstractPerk>> connections = new LinkedList<>();

    private final Map<IConstellation, RootPerk> rootPerks = new HashMap<>();

    private PerkTree() {}

    public Optional<AbstractPerk> getPerk(ResourceLocation key) {
        return this.getPerk(perk -> key.equals(perk.getRegistryName()));
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

    private Optional<PointConnector> getConnector(AbstractPerk point) {
        if (point == null) {
            return Optional.empty();
        }
        if (this.treePoints.contains(point.getPoint())) {
            return Optional.of(new PointConnector(point));
        }
        return Optional.empty();
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

    public Optional<Long> getVersion() {
        return Optional.ofNullable(this.treeVersion);
    }

    public void updatePerkTree(PerkTreeData newTree) {
        this.clearPerkTree();

        newTree.getLoadedPerks().stream().map(ConnectedPerkData::getPerk).forEach(perk -> {
            if (perk instanceof RootPerk) {
                this.rootPerks.put(((RootPerk) perk).getConstellation(), (RootPerk) perk);
            }
            PerkTreePoint<? extends AbstractPerk> offsetPoint = perk.getPoint();
            if (this.treePoints.contains(offsetPoint)) {
                throw new IllegalArgumentException("Tried to register perk-point at already placed position: " + offsetPoint.getOffset().toString());
            }
            this.treePoints.add((PerkTreePoint<AbstractPerk>) offsetPoint);
        });
        newTree.getLoadedPerks().forEach(perkData -> {
            for (ResourceLocation connection : perkData.getConnections()) {
                AbstractPerk perkTo = getPerk(connection).orElseThrow(() -> new JsonParseException("Cannot connect to unknown perk: " + connection));
                this.getConnector(perkTo).ifPresent(connector -> {
                    connector.connect(perkData.getPerk());
                });
            }
        });

        this.treeVersion = computeTreeHash();
    }

    private void clearPerkTree() {
        this.treePoints.clear();
        this.doubleConnections.clear();
        this.connections.clear();

        this.rootPerks.clear();

        this.treeVersion = null;
    }

    @Nonnull
    private Long computeTreeHash() {
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

    public void clearCache(LogicalSide side) {
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
