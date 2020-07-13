/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeArrowSpeed
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:08
 */
public class AttributeTypeArrowSpeed extends PerkAttributeType {

    public AttributeTypeArrowSpeed() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_PROJ_SPEED, true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onArrowFire);
    }

    private void onArrowFire(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ArrowEntity) {
            ArrowEntity arrow = (ArrowEntity) event.getEntity();
            if (arrow.shootingEntity != null) {
                PlayerEntity player = event.getWorld().getPlayerByUuid(arrow.shootingEntity);
                if (player != null) {
                    LogicalSide side = this.getSide(player);
                    if (!hasTypeApplied(player, side)) {
                        return;
                    }

                    Vector3 motion = new Vector3(arrow.getMotion());
                    float mul = PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, ResearchHelper.getProgress(player, side), this, 1F);
                    mul = AttributeEvent.postProcessModded(player, this, mul);
                    motion.multiply(mul);
                    arrow.setMotion(motion.toVec3d());
                }
            }
        }
    }
}
