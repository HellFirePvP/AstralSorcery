/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.tooltip.StoredLumenDisplayTooltip;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileEntityLumenDisplay
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TileEntityLumenDisplay {

    default <T extends BlockEntity> T self() {
        return MiscUtil.cast(this);
    }

    default UUID getDisplayComponentIdentifier() {
        RandomSource src = RandomSource.create(MiscUtil.getBlockPosSeed(this.self().getBlockPos()));
        return new UUID(src.nextLong(), src.nextLong());
    }

    default Optional<StoredLumenDisplayTooltip> getDisplayTooltip() {
        BlockEntity be = this.self();
        Level level = be.getLevel();
        if (level == null) return Optional.empty();
        BlockState state = level.getBlockState(be.getBlockPos());
        ILumenHandler handler = level.getCapability(ILumenHandler.BLOCK, be.getBlockPos(), state, be, null);
        if (handler == null) return Optional.empty();

        List<StoredLumenComponent.StoredLumen> lumenList = new ArrayList<>();
        handler.getContainedLumen().forEach(stack -> {
            lumenList.add(StoredLumenComponent.StoredLumen.of(stack, handler.getCapacity(stack.getLumen())));
        });
        return Optional.of(StoredLumenDisplayTooltip.of(this.getDisplayComponentIdentifier(), new StoredLumenComponent(lumenList)));
    }
}
