/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.root.RootPerk;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTree
 * Created by HellFirePvP
 * Date: 17.06.2018 / 09:28
 */
public class PerkTree {

    public static final int PERK_TREE_VERSION = 1;
    public static final PerkTree PERK_TREE = new PerkTree();

    private static Map<ResourceLocation, AbstractPerk> perkMap = new HashMap<>();
    private boolean frozen = false;

    private List<PerkTreePoint<?>> treePoints = new LinkedList<>();
    private Map<AbstractPerk, Collection<AbstractPerk>> doubleConnections = new HashMap<>();
    private List<Tuple<AbstractPerk, AbstractPerk>> connections = new LinkedList<>();

    private Map<IConstellation, AbstractPerk> rootPerks = new HashMap<>();

    private PerkTree() {}

    public PointConnector registerRootPerk(RootPerk perk) {
        if (frozen) {
            throw new IllegalStateException("Cannot register perk: PerkTree-State already frozen!");
        }
        perkMap.put(perk.getRegistryName(), perk);
        rootPerks.put(perk.getConstellation(), perk);
        MinecraftForge.EVENT_BUS.register(perk);
        return PERK_TREE.setPoint(perk);
    }

    public PointConnector registerPerk(AbstractPerk perk) {
        if (frozen) {
            throw new IllegalStateException("Cannot register perk: PerkTree-State already frozen!");
        }
        perkMap.put(perk.getRegistryName(), perk);
        MinecraftForge.EVENT_BUS.register(perk);
        return PERK_TREE.setPoint(perk);
    }

    @Nullable
    public AbstractPerk getPerk(ResourceLocation key) {
        return perkMap.get(key);
    }

    @Nullable
    public AbstractPerk getAstralSorceryPerk(String keyName) {
        return getPerk(new ResourceLocation(AstralSorcery.MODID, keyName));
    }

    @Nullable
    public AbstractPerk getRootPerk(IConstellation constellation) {
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
    public PointConnector tryGetConnector(AbstractPerk point) {
        if (point == null) return null;
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
    @SideOnly(Side.CLIENT)
    public Collection<Tuple<AbstractPerk, AbstractPerk>> getConnections() {
        return ImmutableList.copyOf(this.connections);
    }

    public void clearCache(Side side) {
        this.treePoints.stream().map(PerkTreePoint::getPerk).forEach(p -> p.clearCaches(side));
    }

    public void removePerk(AbstractPerk perk) {
        if (frozen) {
            throw new IllegalStateException("Cannot remove perk: PerkTree-State already frozen!");
        }
        if (perk instanceof RootPerk) {
            rootPerks.remove(((RootPerk) perk).getConstellation());
        }
        perkMap.remove(perk.getRegistryName());
        MinecraftForge.EVENT_BUS.unregister(perk);
        PerkTreePoint<?> point = perk.getPoint();
        this.treePoints.remove(point);
        new PointConnector(perk).disconnectAll();
    }

    public void freeze() {
        this.frozen = true;
    }

    public class PointConnector {

        private final AbstractPerk point;

        public PointConnector(AbstractPerk point) {
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
            return connections.removeIf(t -> (t.getKey().equals(other) && t.getValue().equals(point)) ||
                    (t.getKey().equals(point) && t.getValue().equals(other)));
        }

        public PointConnector connect(AbstractPerk other) {
            if (other ==  null) {
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

    }

}
