/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyCullingAttack
 * Created by HellFirePvP
 * Date: 31.08.2019 / 16:51
 */
public class KeyCullingAttack extends KeyPerk {

    private static final float defaultCullHealth = 0.15F;

    private final Config config;

    public KeyCullingAttack(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.config = new Config(name.getPath());
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);

        bus.addListener(EventPriority.LOW, this::onDamage);
    }

    @Nullable
    @Override
    protected ConfigEntry addConfig() {
        return config;
    }

    private void onDamage(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getTrueSource();
            LogicalSide side = this.getSide(player);
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                LivingEntity attacked = event.getEntityLiving();
                float actCull = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, (float) this.applyMultiplierD(this.config.cullHealth.get()));
                float lifePerc = attacked.getHealth() / attacked.getMaxHealth();
                if (lifePerc < actCull) {
                    attacked.setHealth(0); // Try faithfully...
                    attacked.getDataManager().set(LivingEntity.HEALTH, 0F); // ... then set just it forcefully.
                }
            }
        }
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.DoubleValue cullHealth;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.cullHealth = cfgBuilder
                    .comment("Defines the percentage at how low the entities' health as to be to then cull the entity.")
                    .translation(translationKey("cullHealth"))
                    .defineInRange("cullHealth", defaultCullHealth, 0.05F, 0.5F);
        }
    }
}
