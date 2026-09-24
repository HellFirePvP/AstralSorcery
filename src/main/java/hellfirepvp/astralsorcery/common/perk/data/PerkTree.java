/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.data;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.perk.tree.perk.RootPerk;
import hellfirepvp.astralsorcery.common.util.data.SidedReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkTree
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkTree {

    private static final PerkTree INSTANCE = new PerkTree();

    private PerkTreeData rawPerkTreeData = null;
    private final SidedReference<BakedPerkTreeData> treeData = new SidedReference<>();

    private PerkTree() {}

    public static PerkTree getInstance() {
        return INSTANCE;
    }

    public Optional<BakedPerkTreeData> getData(LogicalSide side) {
        return this.treeData.getData(side);
    }

    public Optional<AbstractPerk<?>> getPerk(LogicalSide side, ResourceLocation key) {
        return this.getPerk(side, perk -> key.equals(perk.getKey()));
    }

    public Optional<AbstractPerk<?>> getPerk(LogicalSide side, Predicate<AbstractPerk<?>> test) {
        return this.getData(side).flatMap(data -> data.getPerk(test));
    }

    public Optional<? extends AbstractPerk<?>> getPerk(LogicalSide side, float x, float y) {
        return this.getData(side).flatMap(data -> data.getPerk(x, y));
    }

    @Nullable
    public RootPerk<?> getRootPerk(LogicalSide side, BaseConstellation constellation) {
        return this.getData(side).map(data -> data.getRootPerk(constellation)).orElse(null);
    }

    public Collection<AbstractPerk<?>> getConnectedPerks(LogicalSide side, AbstractPerk<?> perk) {
        return this.getData(side).map(data -> data.getConnectedPerks(perk)).orElse(Collections.emptyList());
    }

    public Collection<PerkTreePoint<?>> getPerkPoints(LogicalSide side) {
        return this.getData(side).map(BakedPerkTreeData::getPerkPoints).orElse(Collections.emptyList());
    }

    //Only for rendering purposes.
    @OnlyIn(Dist.CLIENT)
    public Collection<Tuple<AbstractPerk<?>, AbstractPerk<?>>> getConnections() {
        return this.getData(LogicalSide.CLIENT).map(BakedPerkTreeData::getConnections).orElse(Collections.emptyList());
    }

    public Optional<Long> getVersion(LogicalSide side) {
        return this.getData(side).map(BakedPerkTreeData::getVersion);
    }

    // ****************************** INTERNAL, Tree updates ******************************


    public void updateOriginPerkTree(PerkTreeData perkTree) {
        this.rawPerkTreeData = perkTree;
    }

    public Optional<Collection<JsonObject>> getLoginPerkData() {
        return Optional.ofNullable(this.rawPerkTreeData).map(PerkTreeData::getRawData);
    }

    @OnlyIn(Dist.CLIENT)
    public void receivePerkTree(BakedPerkTreeData serverTreeData) {
        this.updateTreeData(LogicalSide.CLIENT, serverTreeData);
    }

    public void clearCache(LogicalSide side) {
        this.getData(side).ifPresent(data -> data.clearPerkCache(side));
        this.updateTreeData(side, null);
    }

    public void setupServerPerkTree() {
        if (this.rawPerkTreeData != null) {
            this.updateTreeData(LogicalSide.SERVER, this.rawPerkTreeData.prepare());
            AstralSorcery.LOG.info("Loaded PerkTree!");
        } else {
            AstralSorcery.LOG.info("No PerkTree data found!");
        }
    }

    private void updateTreeData(LogicalSide side, @Nullable BakedPerkTreeData newData) {
        this.treeData.getData(side).ifPresent(data -> {
            data.getPerkPoints().stream()
                    .map(PerkTreePoint::getPerk)
                    .forEach(perk -> perk.invalidate(side));
        });
        this.treeData.setData(side, newData);
        if (newData != null) {
            newData.getPerkPoints().stream()
                    .map(PerkTreePoint::getPerk)
                    .forEach(perk -> perk.validate(side));
        }
    }
}
