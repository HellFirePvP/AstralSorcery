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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyNoArmor
 * Created by HellFirePvP
 * Date: 31.08.2019 / 21:42
 */
public class KeyNoArmor extends KeyPerk {

    private static final float defaultDamageTakenMultiplier = 0.7F;

    private final Config config;

    public KeyNoArmor(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.config = new Config(name.getPath());
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);

        bus.addListener(this::onLivingHurt);
    }

    @Nullable
    @Override
    protected ConfigEntry addConfig() {
        return this.config;
    }

    private void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        LogicalSide side = this.getSide(player);
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (prog.hasPerkEffect(this)) {
            int eq = 0;
            for (ItemStack stack : player.getArmorInventoryList()) {
                if (!stack.isEmpty()) {
                    eq++;
                }
            }
            if (eq < 2) {
                float multiplier = (float) this.applyMultiplierD(this.config.damageTakenMultiplier.get());
                float effMulti = PerkAttributeHelper.getOrCreateMap(player, side).getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);
                event.setAmount(event.getAmount() * (multiplier * (1F / effMulti)));
            }
        }
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.DoubleValue damageTakenMultiplier;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.damageTakenMultiplier = cfgBuilder
                    .comment("The multiplier that is applied to damage the player receives. The lower the more damage is negated.")
                    .translation(translationKey("damageTakenMultiplier"))
                    .defineInRange("damageTakenMultiplier", defaultDamageTakenMultiplier, 0.1F, 1F);
        }
    }
}
