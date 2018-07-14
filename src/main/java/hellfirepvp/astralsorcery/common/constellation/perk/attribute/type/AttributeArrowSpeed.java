/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeArrowSpeed
 * Created by HellFirePvP
 * Date: 14.07.2018 / 13:39
 */
public class AttributeArrowSpeed extends PerkAttributeType {

    public AttributeArrowSpeed() {
        super(AttributeTypeRegistry.ATTR_TYPE_PROJ_SPEED);
    }

    @SubscribeEvent
    public void onArrowFire(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) event.getEntity();
            if (arrow.shootingEntity != null && arrow.shootingEntity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) arrow.shootingEntity;
                Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
                if (!hasTypeApplied(player, side)) {
                    return;
                }

                Vector3 motion = new Vector3(arrow.motionX, arrow.motionY, arrow.motionZ);
                float mul = 1F;
                mul = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(getTypeString(), mul);
                mul *= PerkAttributeHelper.getOrCreateMap(player, side).getModifier(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT);
                motion.multiply(mul);
                arrow.motionX = motion.getX();
                arrow.motionY = motion.getY();
                arrow.motionZ = motion.getZ();
            }
        }
    }

}
