/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.focal.observer;

import hellfirepvp.astralsorcery.common.focal.FocusCrystalPlacementHelper;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.observerlib.api.ChangeObserver;
import hellfirepvp.observerlib.api.ObservableArea;
import hellfirepvp.observerlib.api.ObservableAreaBoundingBox;
import hellfirepvp.observerlib.api.block.BlockChangeSet;
import hellfirepvp.observerlib.common.change.BlockStateChangeSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocusCrystalFilamentObserver
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocusCrystalFilamentObserver extends ChangeObserver<FocusCrystalFilamentObserver> {

    public static final int OBSERVED_AREA_RADIUS = 3;
    private final FocusCrystalFilamentProvider provider;
    private final AABB observerBox;
    private final ObservableArea observedArea;
    private final Set<BlockPos> positions = new HashSet<>();

    public FocusCrystalFilamentObserver(FocusCrystalFilamentProvider provider) {
        this.provider = provider;
        this.observerBox = AABB.ofSize(Vec3.ZERO, OBSERVED_AREA_RADIUS * 2, OBSERVED_AREA_RADIUS * 2, OBSERVED_AREA_RADIUS * 2);
        this.observedArea = new ObservableAreaBoundingBox(this.observerBox);
    }

    FocusCrystalFilamentObserver addPositions(Set<BlockPos> positions) {
        this.positions.addAll(positions);
        return this;
    }

    public Set<BlockPos> getPositions() {
        return Collections.unmodifiableSet(this.positions);
    }

    @Override
    public FocusCrystalFilamentProvider getProvider() {
        return this.provider;
    }

    @Override
    public void initialize(LevelAccessor world, BlockPos center) {
        this.positions.addAll(FocusCrystalPlacementHelper.collectFocusCrystalFilaments(world, center, this.observerBox.move(center)));
    }

    @Nonnull
    @Override
    public ObservableArea getObservableArea() {
        return this.observedArea;
    }

    @Override
    public boolean notifyChange(Level world, BlockPos center, BlockChangeSet changeSet) {
        for (BlockStateChangeSet.StateChange change : changeSet.getChanges()) {
            if (change.getNewState().is(BlocksAS.STELLAR_FILAMENT)) {
                this.positions.add(change.getAbsolutePosition());
            } else {
                this.positions.remove(change.getAbsolutePosition());
            }
        }
        return true;
    }
}
