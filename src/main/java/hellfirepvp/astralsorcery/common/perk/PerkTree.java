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
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.config.entry.PerkConfig;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.node.RootPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
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

    public static final int PERK_TREE_VERSION = 4;
    public static final PerkTree PERK_TREE = new PerkTree();

    private List<PerkTreePoint<?>> treePoints = new LinkedList<>();
    private Map<AbstractPerk, Collection<AbstractPerk>> doubleConnections = new HashMap<>();
    private List<Tuple<AbstractPerk, AbstractPerk>> connections = new LinkedList<>();

    private Map<IConstellation, RootPerk> rootPerks = new HashMap<>();

    private PerkTree() {}

    public void addPerk(AbstractPerk perk) {
        if (perk instanceof RootPerk) {
            rootPerks.put(((RootPerk) perk).getConstellation(), (RootPerk) perk);
        }
        ConfigEntry entry = perk.addConfig();
        if (entry != null) {
            PerkConfig.CONFIG.newSubSection(entry);
        }
        this.setPoint(perk);
    }

    @Nullable
    public AbstractPerk getPerk(ResourceLocation key) {
        AbstractPerk perk = RegistriesAS.REGISTRY_PERKS.getValue(key);
        return perk != null ? perk : AstralSorcery.getProxy().getRegistryPrimer().getCached(RegistriesAS.REGISTRY_PERKS, key);
    }

    @Nullable
    public AbstractPerk getPerk(Predicate<AbstractPerk> test) {
        return RegistriesAS.REGISTRY_PERKS.getValues().stream()
                .filter(test)
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public AbstractPerk getPerk(float x, float y) {
        return this.treePoints.stream()
                .filter(treePoint -> treePoint.getOffset().distance(x, y) <= 1E-4)
                .findFirst()
                .map(PerkTreePoint::getPerk)
                .orElse(null);
    }

    @Nullable
    public RootPerk getRootPerk(IConstellation constellation) {
        return rootPerks.get(constellation);
    }

    @Nonnull
    private PointConnector setPoint(AbstractPerk perk) throws IllegalArgumentException {
        PerkTreePoint<?> offsetPoint = perk.getPoint();
        if (this.treePoints.contains(offsetPoint)) {
            throw new IllegalArgumentException("Tried to register perk-point at already placed position: " + offsetPoint.getOffset().toString());
        }
        this.treePoints.add(offsetPoint);
        return new PointConnector(perk);
    }

    @Nullable
    public PointConnector getConnector(AbstractPerk point) {
        if (point == null) {
            return null;
        }
        if (this.treePoints.contains(point.getPoint())) {
            return new PointConnector(point);
        }
        return null;
    }

    public Collection<AbstractPerk> getConnectedPerks(AbstractPerk perk) {
        return doubleConnections.getOrDefault(perk, Lists.newArrayList());
    }

    public Collection<PerkTreePoint<?>> getPerkPoints() {
        return ImmutableList.copyOf(this.treePoints);
    }

    //Only for rendering purposes.
    @OnlyIn(Dist.CLIENT)
    public Collection<Tuple<AbstractPerk, AbstractPerk>> getConnections() {
        return ImmutableList.copyOf(this.connections);
    }

    public void clearCache(LogicalSide side) {
        this.treePoints.stream().map(PerkTreePoint::getPerk).forEach(p -> p.clearCaches(side));
    }

    public void removePerk(AbstractPerk perk) {
        if (perk instanceof RootPerk) {
            rootPerks.remove(((RootPerk) perk).getConstellation());
        }
        RegistriesAS.REGISTRY_PERKS.remove(perk.getRegistryName());
        MinecraftForge.EVENT_BUS.unregister(perk);
        PerkTreePoint<?> point = perk.getPoint();
        this.treePoints.remove(point);
        new PointConnector(perk).disconnectAll();
    }

    public class PointConnector {

        private final AbstractPerk point;

        private PointConnector(AbstractPerk point) {
            this.point = point;
        }

        public boolean disconnectAll() {
            boolean removedAll = true;
            Collection<AbstractPerk> otherLinked = new LinkedList<>(doubleConnections.get(this.point));
            for (AbstractPerk other : otherLinked) {
                if (!disconnect(other)) {
                    removedAll = false;
                }
            }
            return removedAll;
        }

        public boolean disconnect(AbstractPerk other) {
            if (other ==  null) {
                return false;
            }

            Collection<AbstractPerk> others = doubleConnections.get(this.point);
            if (others == null) {
                return false;
            }
            if (!others.remove(other)) {
                return false;
            }
            return connections.removeIf(t -> (t.getA().equals(other) && t.getB().equals(point)) ||
                    (t.getA().equals(point) && t.getB().equals(other)));
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
