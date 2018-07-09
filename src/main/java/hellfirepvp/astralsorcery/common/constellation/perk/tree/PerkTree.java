/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.root.RootPerk;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.util.ResourceLocation;
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

    public static final PerkTree PERK_TREE = new PerkTree();

    private static Map<ResourceLocation, AbstractPerk> perkMap = new HashMap<>();

    private List<PerkTreePoint> treePoints = new LinkedList<>();
    private Map<AbstractPerk, Collection<AbstractPerk>> doubleConnections = new HashMap<>();
    private List<Tuple<AbstractPerk, AbstractPerk>> connections = new LinkedList<>();

    private Map<IConstellation, AbstractPerk> rootPerks = new HashMap<>();

    private PerkTree() {}

    public PointConnector registerRootPerk(RootPerk perk) {
        perkMap.put(perk.getRegistryName(), perk);
        rootPerks.put(perk.getConstellation(), perk);
        return PERK_TREE.setPoint(perk);
    }

    public PointConnector registerPerk(AbstractPerk perk) {
        perkMap.put(perk.getRegistryName(), perk);
        return PERK_TREE.setPoint(perk);
    }

    @Nullable
    public AbstractPerk getPerk(ResourceLocation key) {
        return perkMap.get(key);
    }

    @Nullable
    public AbstractPerk getRootPerk(IConstellation constellation) {
        return rootPerks.get(constellation);
    }

    @Nonnull
    private PointConnector setPoint(AbstractPerk perk) throws IllegalArgumentException {
        PerkTreePoint offsetPoint = perk.getPoint();
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

    public Collection<PerkTreePoint> getPerkPoints() {
        return ImmutableList.copyOf(this.treePoints);
    }

    //Only for rendering purposes.
    @SideOnly(Side.CLIENT)
    public Collection<Tuple<AbstractPerk, AbstractPerk>> getConnections() {
        return ImmutableList.copyOf(this.connections);
    }

    public class PointConnector {

        private final AbstractPerk point;

        public PointConnector(AbstractPerk point) {
            this.point = point;
        }

        public PointConnector connect(AbstractPerk other) {
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
