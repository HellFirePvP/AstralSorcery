/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyCleanseBadPotions
 * Created by HellFirePvP
 * Date: 31.08.2019 / 16:39
 */
public class KeyCleanseBadPotions extends KeyPerk {

    public KeyCleanseBadPotions(ResourceLocation name, int x, int y) {
        super(name, x, y);
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);

        bus.addListener(EventPriority.LOW, this::onHeal);
    }

    private void onHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof PlayerEntity && !entity.world.isRemote()) {
            PlayerEntity player = (PlayerEntity) entity;
            List<EffectInstance> badEffects = player.getActivePotionEffects()
                    .stream()
                    .filter(p -> p.getPotion().getEffectType() == EffectType.HARMFUL)
                    .collect(Collectors.toList());
            if (badEffects.isEmpty()) {
                return;
            }
            EffectInstance effect = badEffects.get(rand.nextInt(badEffects.size()));
            PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
            if (prog.hasPerkEffect(this)) {
                float inclChance = 0.1F;
                inclChance = PerkAttributeHelper.getOrCreateMap(player, LogicalSide.SERVER)
                        .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, inclChance);
                float chance = getChance(event.getAmount()) * inclChance;
                if (rand.nextFloat() < chance) {
                    player.removePotionEffect(effect.getPotion());
                }
            }
        }
    }

    private float getChance(float healed) {
        if (healed <= 0) {
            return 0;
        }
        float chance = ((3F / (healed * -0.66666667F)) + 5F) / 5F;
        return MathHelper.clamp(chance, 0F, 1F);
    }
}
