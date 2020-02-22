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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyLastBreath
 * Created by HellFirePvP
 * Date: 31.08.2019 / 18:54
 */
public class KeyLastBreath extends KeyPerk {

    private static final float defaultDigSpeedMultiplier = 1.5F;
    private static final float defaultDamageMultiplier = 3F;

    private final Config config;

    public KeyLastBreath(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.config = new Config(name.getPath());
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);

        bus.addListener(this::onAttack);
        bus.addListener(this::onBreakSpeed);
    }

    @Nullable
    @Override
    protected ConfigEntry addConfig() {
        return this.config;
    }

    private void onAttack(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getTrueSource();
            LogicalSide side = this.getSide(player);
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                float actIncrease = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, (float) this.applyMultiplierD(this.config.damageMultiplier.get()));
                float healthPerc = 1F - (player.getHealth() / player.getMaxHealth());
                event.setAmount(event.getAmount() * (1F + (healthPerc * actIncrease)));
            }
        }
    }

    private void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        PlayerEntity player = event.getPlayer();
        LogicalSide side = this.getSide(player);
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (prog.hasPerkEffect(this)) {
            float actIncrease = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, (float) this.applyMultiplierD(this.config.digSpeedMultiplier.get()));
            float healthPerc = 1F - (player.getHealth() / player.getMaxHealth());
            event.setNewSpeed(event.getNewSpeed() * (1F + (healthPerc * actIncrease)));
        }
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.DoubleValue digSpeedMultiplier;
        private ForgeConfigSpec.DoubleValue damageMultiplier;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.digSpeedMultiplier = cfgBuilder
                    .comment("Defines the dig speed multiplier you get additionally to your normal dig speed when being low on health (25% health = 75% of this additional multiplier)")
                    .translation(translationKey("digSpeedMultiplier"))
                    .defineInRange("digSpeedMultiplier", defaultDigSpeedMultiplier, 0.1, 10);

            this.damageMultiplier = cfgBuilder
                    .comment("Defines the damage multiplier you get additionally to your normal damage when being low on health (25% health = 75% of this additional multiplier)")
                    .translation(translationKey("damageMultiplier"))
                    .defineInRange("damageMultiplier", defaultDamageMultiplier, 0.1, 10);
        }
    }
}
