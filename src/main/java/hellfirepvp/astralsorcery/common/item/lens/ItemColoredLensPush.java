/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.lens;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.PartialEffectExecutor;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemColoredLensPush
 * Created by HellFirePvP
 * Date: 21.09.2019 / 19:50
 */
public class ItemColoredLensPush extends ItemColoredLens {

    private static final ColorTypePush COLOR_TYPE_PUSH = new ColorTypePush();

    public ItemColoredLensPush() {
        super(COLOR_TYPE_PUSH);
    }

    private static class ColorTypePush extends LensColorType {

        private ColorTypePush() {
            super(AstralSorcery.key("push"),
                    TargetType.ENTITY,
                    () -> new ItemStack(ItemsAS.COLORED_LENS_PUSH),
                    ColorsAS.COLORED_LENS_PUSH,
                    0.25F,
                    false);
        }

        @Override
        public void entityInBeam(World world, Vector3 origin, Vector3 target, Entity entity, PartialEffectExecutor executor) {
            if (entity instanceof PlayerEntity && !GeneralConfig.CONFIG.doColoredLensesAffectPlayers.get() && executor.canExecute()) {
                return;
            }
            Vector3 dir = target.clone().subtract(origin).normalize().multiply(0.4F);
            Vector3d eMotion = entity.getMotion();
            Vector3 motion = new Vector3(
                    Math.min(1F, eMotion.x + dir.getX()),
                    dir.getY() + 0.04F,
                    Math.min(1F, eMotion.z + dir.getZ())
            );
            entity.setMotion(motion.toVector3d());
        }

        @Override
        public void blockInBeam(World world, BlockPos pos, BlockState state, PartialEffectExecutor executor) {}
    }
}
