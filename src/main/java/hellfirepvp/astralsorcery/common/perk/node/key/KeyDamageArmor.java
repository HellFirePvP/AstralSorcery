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
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDamageArmor
 * Created by HellFirePvP
 * Date: 31.08.2019 / 17:15
 */
public class KeyDamageArmor extends KeyPerk {

    private static final float defaultDamagePerArmor = 0.05F;

    private final Config config;

    public KeyDamageArmor(ResourceLocation name, int x, int y) {
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
        return this.config;
    }

    private void onDamage(LivingHurtEvent event) {
        LivingEntity attacked = event.getEntityLiving();
        if (attacked instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) attacked;
            LogicalSide side = this.getSide(player);
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                int armorPieces = 0;
                for (ItemStack armor : player.getArmorInventoryList()) {
                    if (!armor.isEmpty()) {
                        armorPieces++;
                    }
                }
                if (armorPieces == 0) {
                    return;
                }

                double dmgArmor = applyMultiplierD(this.config.damagePerArmor.get());
                float dmg = event.getAmount();
                dmg *= ((dmgArmor * armorPieces) * PerkAttributeHelper.getOrCreateMap(player, side)
                        .getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT));
                event.setAmount(Math.max(event.getAmount() - dmg, 0));

                int armorDmg = MathHelper.ceil(dmg * 1.3F);
                for (ItemStack stack : player.getArmorInventoryList()) {
                    stack.damageItem(armorDmg, player, (pl) -> pl.sendBreakAnimation(EquipmentSlotType.MAINHAND));
                }
            }
        }
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.DoubleValue damagePerArmor;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.damagePerArmor = cfgBuilder
                    .comment("Defines how much damage is dealt additionally to armor. This value gets multiplied by the amount of armorpieces the entity you're attacking wears.")
                    .translation(translationKey("damagePerArmor"))
                    .defineInRange("damagePerArmor", defaultDamagePerArmor, 0.01F, 0.2F);
        }
    }
}
