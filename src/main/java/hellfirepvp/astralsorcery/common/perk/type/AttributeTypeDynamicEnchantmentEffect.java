/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.enchantment.EnchantmentModifier;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeTypeDynamicEnchantmentEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeTypeDynamicEnchantmentEffect extends PerkAttributeType {

    public AttributeTypeDynamicEnchantmentEffect() {
        super(true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onModify);
    }

    private void onModify(DynamicEnchantmentEvent.Modify event) {
        Player player = event.getEntity();
        LogicalSide side = SidedHelper.getSide(player);
        if (!this.hasTypeApplied(player, side)) return;

        float modifier = AttributeEvent.postProcessModded(player, this, PerkManager.getOrCreateAttributes(player)
                .getModifier(player, ResearchManager.getProgress(player, side), this));
        List<EnchantmentModifier> modifiers = new ArrayList<>(event.getDynamicEnchantments().getModifiers());
        modifiers.stream()
                .filter(mod -> mod.getType() != EnchantmentModifier.Type.ADD_TO_EXISTING_ALL)
                .forEach(mod -> {
                    int addition = mod.getModifier();
                    addition = Math.round(addition * modifier);
                    event.getDynamicEnchantments().addModifier(mod.copyWithModifier(addition));
                });
    }
}
