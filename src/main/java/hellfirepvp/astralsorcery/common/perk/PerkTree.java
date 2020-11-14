/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.perk.data.PerkTreeData;
import hellfirepvp.astralsorcery.common.perk.data.PreparedPerkTreeData;
import hellfirepvp.astralsorcery.common.perk.node.RootPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.util.SidedReference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
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

    //The original tree, loaded from JSON on the server
    private PerkTreeData loadedPerkTree = null;

    private final SidedReference<PreparedPerkTreeData> treedata = new SidedReference<>();

    private PerkTree() {}

    public Optional<PreparedPerkTreeData> getData(LogicalSide side) {
        return this.treedata.getData(side);
    }

    public Optional<AbstractPerk> getPerk(LogicalSide side, ResourceLocation key) {
        return this.getPerk(side, perk -> key.equals(perk.getRegistryName()));
    }

    public Optional<AbstractPerk> getPerk(LogicalSide side, Predicate<AbstractPerk> test) {
        return this.getData(side).flatMap(data -> data.getPerk(test));
    }

    public Optional<? extends AbstractPerk> getPerk(LogicalSide side, float x, float y) {
        return this.getData(side).flatMap(data -> data.getPerk(x, y));
    }

    @Nullable
    public RootPerk getRootPerk(LogicalSide side, IConstellation constellation) {
        return this.getData(side).map(data -> data.getRootPerk(constellation)).orElse(null);
    }

    public Collection<AbstractPerk> getConnectedPerks(LogicalSide side, AbstractPerk perk) {
        return this.getData(side).map(data -> data.getConnectedPerks(perk)).orElse(Collections.emptyList());
    }

    public Collection<PerkTreePoint<?>> getPerkPoints(LogicalSide side) {
        return this.getData(side).map(PreparedPerkTreeData::getPerkPoints).orElse(Collections.emptyList());
    }

    //Only for rendering purposes.
    @OnlyIn(Dist.CLIENT)
    public Collection<Tuple<AbstractPerk, AbstractPerk>> getConnections() {
        return this.getData(LogicalSide.CLIENT).map(PreparedPerkTreeData::getConnections).orElse(Collections.emptyList());
    }

    public Optional<Long> getVersion(LogicalSide side) {
        return this.getData(side).map(PreparedPerkTreeData::getVersion);
    }

    public void updateOriginPerkTree(PerkTreeData perkTree) {
        this.loadedPerkTree = perkTree;
    }

    public Optional<Collection<JsonObject>> getLoginPerkData() {
        return Optional.ofNullable(this.loadedPerkTree).map(PerkTreeData::getAsDataTree);
    }

    @OnlyIn(Dist.CLIENT)
    public void receivePerkTree(PreparedPerkTreeData serverTreeData) {
        this.updateTreeData(LogicalSide.CLIENT, serverTreeData);
    }

    public void clearCache(LogicalSide side) {
        this.getData(side).ifPresent(data -> data.clearPerkCache(side));
        this.updateTreeData(side, null);
    }

    public void setupServerPerkTree() {
        if (this.loadedPerkTree != null) {
            this.updateTreeData(LogicalSide.SERVER, this.loadedPerkTree.prepare());
            AstralSorcery.log.info("Loaded PerkTree!");
        } else {
            AstralSorcery.log.info("No PerkTree data found!");
        }
    }

    private void updateTreeData(LogicalSide side, @Nullable PreparedPerkTreeData newData) {
        this.treedata.getData(side).ifPresent(data -> {
            data.getPerkPoints().stream()
                    .map(PerkTreePoint::getPerk)
                    .forEach(perk -> perk.invalidate(side));
        });
        this.treedata.setData(side, newData);
        if (newData != null) {
            newData.getPerkPoints().stream()
                    .map(PerkTreePoint::getPerk)
                    .forEach(perk -> perk.validate(side));
        }
    }
}
