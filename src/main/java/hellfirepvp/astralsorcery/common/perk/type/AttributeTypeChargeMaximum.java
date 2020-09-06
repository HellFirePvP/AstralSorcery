/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeChargeMaximum
 * Created by HellFirePvP
 * Date: 08.03.2020 / 16:46
 */
public class AttributeTypeChargeMaximum extends PerkAttributeType {

    public AttributeTypeChargeMaximum() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onAttributePostProcess);
    }

    @Override
    public void onApply(PlayerEntity player, LogicalSide side) {
        super.onApply(player, side);
        AlignmentChargeHandler.INSTANCE.updateMaximum(player, side);
    }

    @Override
    public void onRemove(PlayerEntity player, LogicalSide side, boolean removedCompletely) {
        super.onRemove(player, side, removedCompletely);
        AlignmentChargeHandler.INSTANCE.updateMaximum(player, side);
    }

    private void onAttributePostProcess(AttributeEvent.PostProcessModded processEvent) {
        if (processEvent.getType() instanceof AttributeTypeChargeMaximum && processEvent.getValue() < 0) {
            processEvent.setValue(0);
        }
    }
}
