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
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.FlagExecutor;
import hellfirepvp.astralsorcery.common.util.event.SidedEventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkRangeAreaOfEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkRangeAreaOfEffect extends KeyPerk {

    public static final MapCodec<KeyPerkRangeAreaOfEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkRangeAreaOfEffect::new));
    public static final PerkType<KeyPerkRangeAreaOfEffect> TYPE =
            PerkType.of(KeyPerkRangeAreaOfEffect.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkRangeAreaOfEffect::new);

    private KeyPerkRangeAreaOfEffect(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkRangeAreaOfEffect(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(EventPriority.LOW, LivingDamageEvent.Pre.class, SidedEventBus.entityEvent(), this::onDamage);
    }

    private void onDamage(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        if (source.isDirect()) return;
        LivingEntity target = event.getEntity();

        if (!(source.getEntity() instanceof ServerPlayer sPlayer)) return;
        LogicalSide side = this.getSide(sPlayer);
        if (!side.isServer()) return;
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, side);
        if (!progress.getPerkData().hasPerkEffect(this)) return;
        ServerLevel sLevel = sPlayer.serverLevel();
        ItemStack attackStack = new ItemStack(Items.DIAMOND_SWORD);
        AABB swordBox = attackStack.getSweepHitBox(sPlayer, target).inflate(0.5, 0.25, 0.5);

        FlagExecutor.run(FlagExecutor.Flag.RANGED_SWEEP_ATTACK, () -> {
            sLevel.getEntitiesOfClass(LivingEntity.class, swordBox).forEach(livingTarget -> {
                if (!livingTarget.isAlive()) return;
                if (livingTarget == sPlayer) return;
                if (livingTarget == target) return;
                if (sPlayer.isAlliedTo(livingTarget)) return;
                if (livingTarget.isInvulnerable()) return;

                double dmg = 1F + sPlayer.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) * event.getNewDamage();
                dmg = EnchantmentHelper.modifyDamage(sLevel, attackStack, livingTarget, source, (float) dmg);

                livingTarget.hurt(source, (float) dmg);
            });
        });
    }
}
