/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk.key;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.event.SidedEventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkDisarm
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkDisarm extends KeyPerk {

    public static final MapCodec<KeyPerkDisarm> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkDisarm::new));
    public static final PerkType<KeyPerkDisarm> TYPE =
            PerkType.of(KeyPerkDisarm.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkDisarm::new);
    public static final Config CONFIG = new Config();

    private KeyPerkDisarm(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkDisarm(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(LivingDamageEvent.Post.class, SidedEventBus.entityEvent(), this::onAttacked);
    }

    private void onAttacked(LivingDamageEvent.Post event) {
        Entity source = event.getSource().getEntity();
        if (!(source instanceof ServerPlayer sPlayer)) return;
        LogicalSide side = this.getSide(sPlayer);
        if (!side.isServer()) return;
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, side);
        if (!progress.getPerkData().hasPerkEffect(this)) return;
        LivingEntity target = event.getEntity();

        int foundArmorPieces = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!target.getItemBySlot(slot).isEmpty()) foundArmorPieces++;
        }
        if (foundArmorPieces <= 0) return;

        float disarmChance = PerkManager.getOrCreateAttributes(sPlayer)
                .modifyValue(sPlayer, progress, PerksAS.AttributeTypes.PERK_EFFECT, CONFIG.disarmChance.get().floatValue());
        disarmChance = Mth.clamp(disarmChance / foundArmorPieces, 0F, 1F);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = target.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            if (this.rand.nextFloat() < disarmChance) {
                ItemUtil.dropItemNaturally(target.level(), target.getX(), target.getY(), target.getZ(), stack).ifPresent(dropped -> {
                    target.setItemSlot(slot, ItemStack.EMPTY);
                });
            }
        }
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }

    public static class Config extends ConfigEntry {

        public ModConfigSpec.DoubleValue disarmChance;

        private Config() {
            super("key_disarm");
        }

        @Override
        public void createEntries(ModConfigSpec.Builder cfgBuilder) {
            this.disarmChance = cfgBuilder
                    .comment("Defines the chance per hit to make the target drop a piece of armor.")
                    .translation(translationKey("disarmChance"))
                    .defineInRange("disarmChance", 0.1F, 0F, 1F);
        }
    }
}
