/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityFX;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiPredicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXRefreshFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface FXRefreshFunction<T extends EntityFX> {

    FXRefreshFunction<?> NO_REFRESH = fx -> false;

    static <E extends BlockEntity, T extends EntityFX> FXRefreshFunction<T> tileExists(E tile) {
        return new TileExists<>(tile);
    }

    static <E extends BlockEntity, T extends EntityFX> FXRefreshFunction<T> tileExistsAnd(E tile, BiPredicate<E, T> refreshFct) {
        TileExists<E, T> fct = new TileExists<>(tile);
        return (fx) -> Optional.ofNullable(fct.getTileIfValid()).map(t -> refreshFct.test(t, fx)).orElse(false);
    }

    boolean shouldRefresh(@Nonnull T fx);

    class TileExists<E extends BlockEntity, T extends EntityFX> implements FXRefreshFunction<T> {

        private final ResourceKey<Level> dimType;
        private final BlockPos pos;
        private final Class<E> clazzExpected;

        public TileExists(E tile) {
            this.dimType = tile.getLevel().dimension();
            this.pos = tile.getBlockPos();
            this.clazzExpected = (Class<E>) tile.getClass();
        }

        @Override
        public boolean shouldRefresh(@Nonnull T fx) {
            return getTileIfValid() != null;
        }

        @Nullable
        protected E getTileIfValid() {
            Level clWorld = Minecraft.getInstance().level;
            E tile;
            if (clWorld != null &&
                    clWorld.dimension().equals(dimType) &&
                    (tile = MiscUtil.getTileAt(clWorld, pos, clazzExpected, true).orElse(null)) != null &&
                    !tile.isRemoved()) {
                return tile;
            }
            return null;
        }
    }
}
