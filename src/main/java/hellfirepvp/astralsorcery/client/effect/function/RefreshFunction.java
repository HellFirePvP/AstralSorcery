/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiPredicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RefreshFunction
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:43
 */
public interface RefreshFunction<T extends EntityComplexFX> {

    RefreshFunction<?> DESPAWN = fx -> false;

    public static <E extends TileEntity, T extends EntityComplexFX> RefreshFunction<T> tileExists(E tile) {
        return new TileExists<>(tile);
    }

    public static <E extends TileEntity, T extends EntityComplexFX> RefreshFunction<T> tileExistsAnd(E tile, BiPredicate<E, T> refreshFct) {
        TileExists<E, T> fct = new TileExists<>(tile);
        return (fx) -> Optional.ofNullable(fct.getTileIfValid()).map(t -> refreshFct.test(t, fx)).orElse(false);
    }

    public boolean shouldRefresh(@Nonnull T fx);

    public static class TileExists<E extends TileEntity, T extends EntityComplexFX> implements RefreshFunction<T> {

        private final DimensionType dimType;
        private final BlockPos pos;
        private final Class<E> clazzExpected;

        public TileExists(E tile) {
            this.dimType = tile.getWorld().getDimension().getType();
            this.pos = tile.getPos();
            this.clazzExpected = (Class<E>) tile.getClass();
        }

        @Override
        public boolean shouldRefresh(@Nonnull T fx) {
            return getTileIfValid() != null;
        }

        @Nullable
        protected E getTileIfValid() {
            World clWorld = Minecraft.getInstance().world;
            E tile;
            if (clWorld != null &&
                    clWorld.getDimension().getType().equals(dimType) &&
                    (tile = MiscUtils.getTileAt(clWorld, pos, clazzExpected, true)) != null &&
                    !tile.isRemoved()) {
                return tile;
            }
            return null;
        }
    }

}
