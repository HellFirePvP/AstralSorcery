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
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

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

    public static final PerkTree INSTANCE = new PerkTree();

    public static final ResourceLocation REGISTRY_PERKS_NAME = new ResourceLocation(AstralSorcery.MODID, "constellation_perks");
    public static IForgeRegistry<AbstractPerk> PERK_REGISTRY = null;

    private List<PerkTreePoint> treePoints = new LinkedList<>();
    private Map<AbstractPerk, Collection<AbstractPerk>> doubleConnections = new HashMap<>();
    private List<Tuple<AbstractPerk, AbstractPerk>> connections = new LinkedList<>();

    private PerkTree() {}

    public void initializeTree() {
        if (PERK_REGISTRY != null) return;

        PERK_REGISTRY = new RegistryBuilder<AbstractPerk>()
                .disableSaving()
                .setName(REGISTRY_PERKS_NAME)
                .setType(AbstractPerk.class)
                .create();
    }

    public PointConnector registerPerk(AbstractPerk perk) {
        PERK_REGISTRY.register(perk);
        return INSTANCE.setPoint(perk);
    }

    @Nullable
    public AbstractPerk getPerk(ResourceLocation key) {
        return PERK_REGISTRY.getValue(key);
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

    public class PointConnector {

        private final AbstractPerk point;

        public PointConnector(AbstractPerk point) {
            this.point = point;
        }

        public void connect(AbstractPerk other) {
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
        }

        public void connect(PointConnector other) {
            connect(other.point);
        }

    }

}
