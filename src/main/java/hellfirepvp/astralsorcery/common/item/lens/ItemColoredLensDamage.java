/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.lens;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.PartialEffectExecutor;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemColoredLensDamage
 * Created by HellFirePvP
 * Date: 21.09.2019 / 19:31
 */
public class ItemColoredLensDamage extends ItemColoredLens {

    private static final ColorTypeDamage COLOR_TYPE_DAMAGE = new ColorTypeDamage();

    public ItemColoredLensDamage() {
        super(COLOR_TYPE_DAMAGE);
    }

    private static class ColorTypeDamage extends LensColorType {

        public ColorTypeDamage() {
            super(AstralSorcery.key("damage"),
                    TargetType.ENTITY,
                    () -> new ItemStack(ItemsAS.COLORED_LENS_DAMAGE),
                    ColorsAS.COLORED_LENS_DAMAGE,
                    0.2F,
                    false);
        }

        @Override
        public void entityInBeam(World world, Vector3 origin, Vector3 target, Entity entity, PartialEffectExecutor executor) {
            if (world.isRemote() || !(entity instanceof LivingEntity)) {
                return;
            }
            executor.executeAll(() -> {
                if (entity instanceof PlayerEntity) {
                    if (!GeneralConfig.CONFIG.doColoredLensesAffectPlayers.get() ||
                            entity.getServer() == null ||
                            !entity.getServer().isPVPEnabled()) {
                        return;
                    }
                }
                DamageUtil.attackEntityFrom(entity, CommonProxy.DAMAGE_SOURCE_STELLAR, 1.5F);
            });
        }

        @Override
        public void blockInBeam(World world, BlockPos pos, BlockState state, PartialEffectExecutor executor) {}
    }
}
