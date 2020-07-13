/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.effect;

import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.resource.query.SpriteQuery;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectCheatDeath
 * Created by HellFirePvP
 * Date: 26.08.2019 / 20:06
 */
public class EffectCheatDeath extends EffectCustomTexture {

    public EffectCheatDeath() {
        super(EffectType.BENEFICIAL, ColorsAS.EFFECT_CHEAT_DEATH);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>(0);
    }

    @Override
    public void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);
        bus.addListener(EventPriority.LOW, this::onDeath);
    }

    private void onDeath(LivingDeathEvent event) {
        LivingEntity le = event.getEntityLiving();
        if (!le.getEntityWorld().isRemote() && le.isPotionActive(EffectsAS.EFFECT_CHEAT_DEATH)) {
            event.setCanceled(true);

            int level = le.removeActivePotionEffect(EffectsAS.EFFECT_CHEAT_DEATH).getAmplifier();
            le.setHealth(Math.min(le.getMaxHealth(), 4 + level * 2));
            le.addPotionEffect(new EffectInstance(Effects.REGENERATION, 200, 2, false, false, true));
            le.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 500, 1, false, false, true));
            List<LivingEntity> others = le.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class,
                    le.getBoundingBox().grow(3), (e) -> e.isAlive() && e != le);
            for (LivingEntity lb : others) {
                lb.setFire(10);
                lb.knockBack(le, 2F, lb.getPosX() - le.getPosX(), lb.getPosZ() - le.getPosZ());
            }
            //TODO particles
            //PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.PHOENIX_PROC, new Vector3(le.getPosX(), le.getPosY(), le.getPosZ()));
            //PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(le.world, le.getPosition(), 32));
        }
    }

    @Override
    public SpriteQuery getSpriteQuery() {
        return new SpriteQuery(AssetLoader.TextureLocation.GUI, 1, 1, "effect", "cheat_death");
    }
}
