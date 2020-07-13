/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.lens;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemColoredLensRegeneration
 * Created by HellFirePvP
 * Date: 21.09.2019 / 19:35
 */
public class ItemColoredLensRegeneration extends ItemColoredLens {

    private static final ColorTypeRegeneration COLOR_TYPE_REGENERATION = new ColorTypeRegeneration();

    public ItemColoredLensRegeneration() {
        super(COLOR_TYPE_REGENERATION);
    }

    private static class ColorTypeRegeneration extends LensColorType {

        private ColorTypeRegeneration() {
            super(AstralSorcery.key("regeneration"),
                    TargetType.ENTITY,
                    () -> new ItemStack(ItemsAS.COLORED_LENS_REGENERATION),
                    ColorsAS.COLORED_LENS_REGEN,
                    0.1F,
                    false);
        }

        @Override
        public void entityInBeam(Vector3 origin, Vector3 target, Entity entity, float beamStrength) {
            if (!(entity instanceof LivingEntity) || !entity.isAlive()) {
                return;
            }
            if (random.nextFloat() > beamStrength) {
                return;
            }
            LivingEntity le = (LivingEntity) entity;
            if (le.isEntityUndead()) {
                DamageUtil.attackEntityFrom(le, CommonProxy.DAMAGE_SOURCE_STELLAR, 7F * beamStrength);
            } else {
                le.heal(5F * beamStrength);
            }
        }

        @Override
        public void blockInBeam(IWorld world, BlockPos pos, BlockState state, float beamStrength) {}
    }
}
