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
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeAllElementalResist
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:02
 */
public class AttributeTypeAllElementalResist extends PerkAttributeType {

    public AttributeTypeAllElementalResist() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST, true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onDamageTaken);
    }

    private void onDamageTaken(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        LogicalSide side = this.getSide(player);
        if (!hasTypeApplied(player, side)) {
            return;
        }
        DamageSource ds = event.getSource();
        if (isMaybeElementalDamage(ds)) {
            float multiplier = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, ResearchHelper.getProgress(player, side), this, 1F);
            multiplier -= 1F;
            multiplier = AttributeEvent.postProcessModded(player, this, multiplier);
            multiplier = 1F - MathHelper.clamp(multiplier, 0F, 1F);
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    private boolean isMaybeElementalDamage(DamageSource source) {
        // "Magic" is often used for any kinds of damages... poison for example
        if (source.isFireDamage() || source.isMagicDamage()) {
            return true;
        }
        String key = source.getDamageType();
        if (key == null) {
            return false;
        }
        key = key.toLowerCase();
        return key.contains("fire") || key.contains("heat") || key.contains("lightning") ||
                key.contains("cold") || key.contains("freez") || key.contains("discharg") ||
                key.contains("electr") || key.contains("froze") || key.contains("ice");
    }

}
