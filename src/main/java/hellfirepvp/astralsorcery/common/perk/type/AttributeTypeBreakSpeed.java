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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeBreakSpeed
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:15
 */
public class AttributeTypeBreakSpeed extends PerkAttributeType {

    public static boolean evaluateBreakSpeedWithoutPerks = false;

    public AttributeTypeBreakSpeed() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_INC_HARVEST_SPEED);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(EventPriority.LOW, this::onBreakSpeed);
    }

    private void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (evaluateBreakSpeedWithoutPerks) {
            return;
        }

        PlayerEntity player = event.getPlayer();
        LogicalSide side = this.getSide(player);
        if (!hasTypeApplied(player, side)) {
            return;
        }
        float speed = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, ResearchHelper.getProgress(player, side), this, event.getNewSpeed());
        speed = AttributeEvent.postProcessModded(player, this, speed);
        event.setNewSpeed(speed);
    }
}
