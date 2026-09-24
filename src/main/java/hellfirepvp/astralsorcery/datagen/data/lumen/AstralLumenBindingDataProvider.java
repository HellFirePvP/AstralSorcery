/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.lumen;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.MobEffectsAS;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBinding;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.lumen.binding.data.LumenBindingDataProvider;
import hellfirepvp.astralsorcery.common.lumen.binding.effect.*;
import hellfirepvp.astralsorcery.common.lumen.binding.usage.*;
import hellfirepvp.astralsorcery.common.util.RandomMobEffectInstance;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffects;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static hellfirepvp.astralsorcery.common.lib.PerksAS.AttributeTypes.*;
import static hellfirepvp.astralsorcery.common.perk.type.base.ModifierType.*;
import static hellfirepvp.astralsorcery.common.util.TimeUtil.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralLumenBindingDataProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralLumenBindingDataProvider extends LumenBindingDataProvider {

    public AstralLumenBindingDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(AstralSorcery.MODID, output, registries);
    }

    @Override
    public void registerBindingTypes() {
        this.registerPrimitiveTypeBindings();
        this.registerCombinedTypeBindings();
        this.registerComplexTypeBindings();

        this.newBindingType(LumenAS.PRISMATIC)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        LumenBindingUsage.NONE,
                        List.of(),
                        LumenBindingEffectEffectiveness.of(0.5F))
                )
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        LumenBindingUsage.NONE,
                        List.of(),
                        LumenBindingEffectEffectiveness.of(0.5F))
                )
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        LumenBindingUsage.NONE,
                        List.of(),
                        LumenBindingEffectEffectiveness.of(0.5F))
                )
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        LumenBindingUsage.NONE,
                        List.of(),
                        LumenBindingEffectEffectiveness.of(0.5F))
                )
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        LumenBindingUsage.NONE,
                        List.of(),
                        LumenBindingEffectEffectiveness.of(0.5F))
                )
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        LumenBindingUsage.NONE,
                        List.of(),
                        LumenBindingEffectEffectiveness.of(0.5F))
                )
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        LumenBindingUsage.NONE,
                        List.of(),
                        LumenBindingEffectEffectiveness.of(0.5F))
                )
                .build()
                .registerBinding(LumenAS.PRISMATIC);
    }

    private void registerComplexTypeBindings() {
        this.newBindingType(LumenAS.HYLE)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.10F),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(DAMAGE_REDUCTION, ADDED_MULTIPLY, 0.05F)
                                        .addModifier(ARMOR_TOUGHNESS, ADDITION, 2F)
                                        .build(),
                                LumenBindingAbsorbDamageEffect.of(10F, 80, 1, 100)
                        )))
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.15F),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(DAMAGE_REDUCTION, ADDED_MULTIPLY, 0.08F)
                                        .addModifier(ARMOR_TOUGHNESS, ADDITION, 4F)
                                        .build(),
                                LumenBindingAbsorbDamageEffect.of(10F, 100, 2, 100)
                        )))
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.10F),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(DAMAGE_REDUCTION, ADDED_MULTIPLY, 0.05F)
                                        .addModifier(ARMOR_TOUGHNESS, ADDITION, 3F)
                                        .build(),
                                LumenBindingAbsorbDamageEffect.of(10F, 80, 1, 100)
                        )))
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.10F),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(SAFE_FALL_DISTANCE, ADDITION, 10F)
                                        .addModifier(DAMAGE_REDUCTION, ADDED_MULTIPLY, 0.04F)
                                        .build(),
                                LumenBindingAbsorbDamageEffect.of(10F, 80, 1, 100)
                        )))
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.30F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_DAMAGE, ADDED_MULTIPLY, 0.05F)
                                .addModifier(LIFE_LEECH, ADDED_MULTIPLY, 0.04F)
                                .build()))
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.30F, false),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(PROJECTILE_DAMAGE, ADDED_MULTIPLY, 0.05F)
                                .addModifier(LIFE_LEECH, ADDED_MULTIPLY, 0.03F)
                                .build()))
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        LumenBindingUsageBlockBreak.of(1, 0.20F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MINING_SIZE, ADDITION, 1F)
                                .addModifier(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.10F)
                                .build()))
                .potionEffect(MobEffects.DAMAGE_RESISTANCE, IntRange.of(seconds(15), seconds(30)), IntRange.of(2, 3))
                .build()
                .registerBinding(LumenAS.HYLE);

        this.newBindingType(LumenAS.DYNAMIS)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.25F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_SPEED, ADDED_MULTIPLY, 0.07F)
                                .addModifier(CRITICAL_HIT_CHANCE, ADDED_MULTIPLY, 0.05F)
                                .build()))
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.30F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_SPEED, ADDED_MULTIPLY, 0.07F)
                                .addModifier(CRITICAL_HIT_DAMAGE, ADDED_MULTIPLY, 0.1F)
                                .build()))
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.25F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_SPEED, ADDED_MULTIPLY, 0.07F)
                                .addModifier(CRITICAL_HIT_CHANCE, ADDED_MULTIPLY, 0.07F)
                                .build()))
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageDealt.of(1, 0.25F, true),
                                LumenBindingUsageMovement.of(1, 0.1F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_SPEED, ADDED_MULTIPLY, 0.05F)
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.04F)
                                .build()))
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.35F, true),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(CRITICAL_HIT_CHANCE, ADDED_MULTIPLY, 0.08F)
                                        .addModifier(CRITICAL_HIT_DAMAGE, ADDED_MULTIPLY, 0.10F)
                                        .build(),
                                LumenBindingDamageBurstEffect.of(4F, 1.5F)
                        )))
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.35F, false),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(PROJECTILE_SPEED, ADDED_MULTIPLY, 0.10F)
                                .addModifier(CRITICAL_HIT_CHANCE, ADDED_MULTIPLY, 0.06F)
                                .build()))
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        LumenBindingUsageBlockBreak.of(1, 0.20F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.15F)
                                .build()))
                .potionEffect(MobEffectsAS.RAMPAGE, IntRange.of(minutes(1), minutes(3)), IntRange.of(2, 3))
                .build()
                .registerBinding(LumenAS.DYNAMIS);

        this.newBindingType(LumenAS.AION)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageTaken.of(1, 0.08F),
                                LumenBindingUsageMovement.of(1, 0.3F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(COOLDOWN_REDUCTION, ADDED_MULTIPLY, -0.06F)
                                        //.addModifier(POTION_DURATION, ADDED_MULTIPLY, 0.1F)
                                        .build(),
                                LumenBindingExtendMobEffectsEffect.of(40)
                        )))
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageTaken.of(1, 0.12F),
                                LumenBindingUsageMovement.of(1, 0.35F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(COOLDOWN_REDUCTION, ADDED_MULTIPLY, -0.1F)
                                        //.addModifier(POTION_DURATION, ADDED_MULTIPLY, 0.14F)
                                        .build(),
                                LumenBindingExtendMobEffectsEffect.of(60)
                        )))
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageTaken.of(1, 0.08F),
                                LumenBindingUsageMovement.of(1, 0.3F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(COOLDOWN_REDUCTION, ADDED_MULTIPLY, -0.08F)
                                        //.addModifier(POTION_DURATION, ADDED_MULTIPLY, 0.1F)
                                        .build(),
                                LumenBindingExtendMobEffectsEffect.of(40)
                        )))
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        LumenBindingUsageMovement.of(1, 0.3F, 0.03F,
                                Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.05F)
                                .addModifier(COOLDOWN_REDUCTION, ADDED_MULTIPLY, -0.04F)
                                .build()))
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.30F, true),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(ATTACK_SPEED, ADDED_MULTIPLY, 0.1F)
                                        .addModifier(COOLDOWN_REDUCTION, ADDED_MULTIPLY, -0.06F)
                                        .build(),
                                LumenBindingHitAddEffectEffect.of(MobEffects.MOVEMENT_SLOWDOWN,
                                        IntRange.of(seconds(8), seconds(15)), IntRange.of(0, 1), 1F)
                        )))
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.30F, false),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(PROJECTILE_SPEED, ADDED_MULTIPLY, 0.06F)
                                .addModifier(COOLDOWN_REDUCTION, ADDED_MULTIPLY, -0.06F)
                                .build()))
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageBlockBreak.of(1, 0.4F),
                                LumenBindingUsageMovement.of(1, 0.25F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.1F)
                                        .addModifier(COOLDOWN_REDUCTION, ADDED_MULTIPLY, -0.06F)
                                        .build(),
                                LumenBindingAoeCropGrowthEffect.INSTANCE
                        )))
                .potionEffect(MobEffectsAS.PHOENIX_BLESSING, IntRange.of(minutes(5), minutes(10)), IntRange.of(0))
                .build()
                .registerBinding(LumenAS.AION);

        this.newBindingType(LumenAS.AKASHA)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        LumenBindingUsageMovement.of(1, 0.3F, 0.02F,
                                Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(BLOCK_REACH, ADDED_MULTIPLY, 0.05F)
                                .addModifier(STEP_HEIGHT, ADDITION, 0.5F)
                                .build()))
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        LumenBindingUsageMovement.of(1, 0.4F, 0.02F,
                                Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_REACH, ADDED_MULTIPLY, 0.08F)
                                .addModifier(BLOCK_REACH, ADDED_MULTIPLY, 0.08F)
                                .build()))
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        LumenBindingUsageMovement.of(1, 0.3F, 0.02F,
                                Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(STEP_HEIGHT, ADDITION, 0.5F)
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.03F)
                                .build()))
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        LumenBindingUsageMovement.of(1, 0.3F, 0.03F,
                                Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(STEP_HEIGHT, ADDITION, 1F)
                                .addModifier(SAFE_FALL_DISTANCE, ADDITION, 2.5F)
                                .build()))
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.30F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_REACH, ADDED_MULTIPLY, 0.08F)
                                .addModifier(PIERCE_ARMOR, ADDED_MULTIPLY, 0.1F)
                                .build()))
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.30F, false),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(PROJECTILE_SPEED, ADDED_MULTIPLY, 0.08F)
                                        .build(),
                                LumenBindingProjectileAccuracyEffect.INSTANCE
                        )))
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        LumenBindingUsage.NONE,
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(MINING_SIZE, ADDITION, 1F)
                                        .build(),
                                LumenBindingCollectDropsEffect.INSTANCE
                        )))
                .potionEffect(MobEffects.NIGHT_VISION, IntRange.of(seconds(30), minutes(1)), IntRange.of(0),
                        RandomMobEffectInstance.of(MobEffects.LUCK, IntRange.of(minutes(5), minutes(10)), IntRange.of(0)))
                .build()
                .registerBinding(LumenAS.AKASHA);
    }

    private void registerCombinedTypeBindings() {
        this.newBindingType(LumenAS.VIREL)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageTaken.of(1, 0.06F),
                                LumenBindingUsageMovement.of(1, 0.2F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MAX_HEALTH, ADDED_MULTIPLY, 0.04F)
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.02F)
                                .build())
                )
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageTaken.of(1, 0.08F),
                                LumenBindingUsageMovement.of(1, 0.25F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MAX_HEALTH, ADDED_MULTIPLY, 0.05F)
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.04F)
                                .build())
                )
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageTaken.of(1, 0.06F),
                                LumenBindingUsageMovement.of(1, 0.2F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MAX_HEALTH, ADDED_MULTIPLY, 0.04F)
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.02F)
                                .build())
                )
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageTaken.of(1, 0.06F),
                                LumenBindingUsageMovement.of(1, 0.2F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MAX_HEALTH, ADDED_MULTIPLY, 0.04F)
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.02F)
                                .build())
                )
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageMovement.of(1, 0.14F, 0.03F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                                LumenBindingUsageDamageTaken.of(1, 0.12F)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.05F)
                                .addModifier(MAX_HEALTH, ADDED_MULTIPLY, 0.02F)
                                .build())
                )
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageMovement.of(1, 0.14F, 0.04F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                                LumenBindingUsageDamageTaken.of(1, 0.12F)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.08F)
                                .addModifier(MAX_HEALTH, ADDED_MULTIPLY, 0.02F)
                                .build())
                )
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageMovement.of(1, 0.14F, 0.04F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                                LumenBindingUsageDamageTaken.of(1, 0.2F)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.08F)
                                .addModifier(MAX_HEALTH, ADDED_MULTIPLY, 0.02F)
                                .build())
                )
                .potionEffect(MobEffects.REGENERATION, IntRange.of(minutes(1), minutes(2)), IntRange.of(1, 2))
                .build()
                .registerBinding(LumenAS.VIREL);

        this.newBindingType(LumenAS.SOLYN)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.1F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MAX_HEALTH, ADDITION, 1F)
                                .addModifier(ARMOR, ADDITION, 2F)
                                .build())
                )
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.13F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MAX_HEALTH, ADDED_MULTIPLY, 0.05F)
                                .addModifier(ARMOR, ADDITION, 2F)
                                .build())
                )
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.1F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MAX_HEALTH, ADDITION, 1F)
                                .addModifier(ARMOR, ADDITION, 2F)
                                .build())
                )
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.1F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(MAX_HEALTH, ADDITION, 1F)
                                .addModifier(ARMOR, ADDITION, 2F)
                                .build())
                )
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.2F),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(BLOCK_CHANCE, ADDED_MULTIPLY, 0.15F)
                                        .addModifier(MAX_HEALTH, ADDITION, 1F)
                                        .build(),
                                LumenBindingHitAddEffectEffect.of(MobEffects.GLOWING, IntRange.of(seconds(45)), IntRange.of(0), 1F)
                        ))
                )
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.2F),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(BLOCK_CHANCE, ADDED_MULTIPLY, 0.1F)
                                        .addModifier(MAX_HEALTH, ADDITION, 1F)
                                        .build(),
                                LumenBindingHitAddEffectEffect.of(MobEffects.GLOWING, IntRange.of(seconds(45)), IntRange.of(0), 1F)
                        ))
                )
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.2F),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(BLOCK_CHANCE, ADDED_MULTIPLY, 0.1F)
                                        .addModifier(MAX_HEALTH, ADDITION, 2F)
                                        .build(),
                                LumenBindingPlaceLightEffect.INSTANCE
                        ))
                )
                .potionEffect(MobEffects.DAMAGE_RESISTANCE, IntRange.of(minutes(1), minutes(2)), IntRange.of(1, 1))
                .build()
                .registerBinding(LumenAS.SOLYN);

        this.newBindingType(LumenAS.CALDOR)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageTaken.of(1, 0.1F),
                                LumenBindingUsageDamageDealt.of(1, 0.25F, false)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_DAMAGE, ADDED_MULTIPLY, 0.05F)
                                .addModifier(PROJECTILE_DAMAGE, ADDED_MULTIPLY, 0.06F)
                                .build())
                )
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageTaken.of(1, 0.15F),
                                LumenBindingUsageDamageDealt.of(1, 0.3F, false)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_DAMAGE, ADDED_MULTIPLY, 0.08F)
                                .addModifier(PROJECTILE_DAMAGE, ADDED_MULTIPLY, 0.08F)
                                .build())
                )
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageTaken.of(1, 0.1F),
                                LumenBindingUsageDamageDealt.of(1, 0.25F, false)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_DAMAGE, ADDED_MULTIPLY, 0.05F)
                                .addModifier(PROJECTILE_DAMAGE, ADDED_MULTIPLY, 0.06F)
                                .build())
                )
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageTaken.of(1, 0.1F),
                                LumenBindingUsageDamageDealt.of(1, 0.25F, false)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_DAMAGE, ADDED_MULTIPLY, 0.05F)
                                .addModifier(PROJECTILE_DAMAGE, ADDED_MULTIPLY, 0.06F)
                                .build())
                )
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.4F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_DAMAGE, ADDED_MULTIPLY, 0.08F)
                                .addModifier(ATTACK_REACH, ADDED_MULTIPLY, 0.08F)
                                .addModifier(PIERCE_ARMOR, ADDED_MULTIPLY, 0.05F)
                                .build())
                )
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.4F, false),
                        List.of(),
                        CombinedLumenBindingEffect.of(
                                LumenBindingDynamicModifierEffect.builder()
                                        .addModifier(PROJECTILE_DAMAGE, ADDED_MULTIPLY, 0.15F)
                                        .addModifier(PROJECTILE_SPEED, ADDED_MULTIPLY, 0.05F)
                                        .build(),
                                LumenBindingProjectileAccuracyEffect.INSTANCE
                        ))
                )
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        LumenBindingUsageBlockBreak.of(1, 0.25F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.15F)
                                .addModifier(BLOCK_REACH, ADDED_MULTIPLY, 0.08F)
                                .build())
                )
                .potionEffect(MobEffects.INVISIBILITY, IntRange.of(minutes(3), minutes(5)), IntRange.of(0))
                .build()
                .registerBinding(LumenAS.CALDOR);

        this.newBindingType(LumenAS.NULLAE)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageBlockBreak.of(1, 0.15F),
                                LumenBindingUsageMovement.of(1, 0.35F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.05F)
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.04F)
                                .build()
                ))
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageBlockBreak.of(1, 0.18F),
                                LumenBindingUsageMovement.of(1, 0.4F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.08F)
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.08F)
                                .build()
                ))
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageBlockBreak.of(1, 0.15F),
                                LumenBindingUsageMovement.of(1, 0.35F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.05F)
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.04F)
                                .build()
                ))
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageBlockBreak.of(1, 0.15F),
                                LumenBindingUsageMovement.of(1, 0.35F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.05F)
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.04F)
                                .build()
                ))
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageDealt.of(1, 0.2F, true),
                                LumenBindingUsageMovement.of(1, 0.2F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(ATTACK_REACH, ADDED_MULTIPLY, 0.1F)
                                .addModifier(PIERCE_ARMOR, ADDED_MULTIPLY, 0.06F)
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.04F)
                                .build()
                ))
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageDamageDealt.of(1, 0.25F, false),
                                LumenBindingUsageMovement.of(1, 0.2F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(PROJECTILE_SPEED, ADDED_MULTIPLY, 0.12F)
                                .addModifier(PIERCE_ARMOR, ADDED_MULTIPLY, 0.08F)
                                .build()
                ))
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        CombinedLumenBindingUsage.of(
                                LumenBindingUsageBlockBreak.of(1, 0.18F),
                                LumenBindingUsageMovement.of(1, 0.3F, 0.02F,
                                        Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM)
                        ),
                        List.of(),
                        LumenBindingDynamicModifierEffect.builder()
                                .addModifier(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.1F)
                                .addModifier(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.1F)
                                .build()
                ))
                .potionEffect(MobEffects.BLINDNESS, IntRange.of(seconds(20), seconds(35)), IntRange.of(0),
                        RandomMobEffectInstance.of(MobEffects.DIG_SPEED, IntRange.of(minutes(1) + seconds(30), minutes(3)), IntRange.of(1, 2)))
                .build()
                .registerBinding(LumenAS.NULLAE);
    }

    private void registerPrimitiveTypeBindings() {
        this.newBindingType(LumenAS.AEVITAS)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.1F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MAX_HEALTH, ADDED_MULTIPLY, 0.05F))
                )
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.15F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MAX_HEALTH, ADDED_MULTIPLY, 0.08F))
                )
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.1F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MAX_HEALTH, ADDED_MULTIPLY, 0.05F))
                )
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.1F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MAX_HEALTH, ADDED_MULTIPLY, 0.05F))
                )
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.3F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MAX_HEALTH, ADDED_MULTIPLY, 0.02F))
                )
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.3F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MAX_HEALTH, ADDED_MULTIPLY, 0.02F))
                )
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.3F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MAX_HEALTH, ADDED_MULTIPLY, 0.02F))
                )
                .potionEffect(MobEffects.REGENERATION, IntRange.of(minutes(4), minutes(8)), IntRange.of(0, 0))
                .build()
                .registerBinding(LumenAS.AEVITAS);

        this.newBindingType(LumenAS.ARMARA)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.1F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(ARMOR, ADDITION, 3F))
                )
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.15F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(DAMAGE_REDUCTION, ADDED_MULTIPLY, 0.1F))
                )
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.1F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(ARMOR, ADDITION, 3F))
                )
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.1F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(ARMOR, ADDITION, 3F))
                )
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.3F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(BLOCK_CHANCE, ADDED_MULTIPLY, 0.1F))
                )
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.3F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(BLOCK_CHANCE, ADDED_MULTIPLY, 0.1F))
                )
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        LumenBindingUsageDamageTaken.of(1, 0.3F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(BLOCK_CHANCE, ADDED_MULTIPLY, 0.1F))
                )
                .potionEffect(MobEffects.DAMAGE_RESISTANCE, IntRange.of(minutes(2), minutes(3)), IntRange.of(0))
                .build()
                .registerBinding(LumenAS.ARMARA);

        this.newBindingType(LumenAS.DISCIDIA)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.3F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(ATTACK_DAMAGE, ADDED_MULTIPLY, 0.06F))
                )
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.4F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(ATTACK_DAMAGE, ADDED_MULTIPLY, 0.1F))
                )
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.3F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(ATTACK_DAMAGE, ADDED_MULTIPLY, 0.06F))
                )
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.3F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(ATTACK_DAMAGE, ADDED_MULTIPLY, 0.06F))
                )
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.3F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(ATTACK_DAMAGE, ADDITION, 3F))
                )
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.3F, false),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(PROJECTILE_DAMAGE, ADDITION, 2F))
                )
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.3F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(ATTACK_DAMAGE, ADDITION, 4F))
                )
                .potionEffect(MobEffects.DAMAGE_BOOST, IntRange.of(minutes(3), minutes(7)), IntRange.of(0, 2))
                .build()
                .registerBinding(LumenAS.DISCIDIA);

        this.newBindingType(LumenAS.EVORSIO)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        LumenBindingUsageBlockBreak.of(1, 0.1F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.06F))
                )
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        LumenBindingUsageBlockBreak.of(1, 0.15F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.08F))
                )
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        LumenBindingUsageBlockBreak.of(1, 0.1F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.06F))
                )
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        LumenBindingUsageBlockBreak.of(1, 0.1F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.06F))
                )
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.2F, true),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(PIERCE_ARMOR, ADDED_MULTIPLY, 0.15F))
                )
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.2F, false),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(PIERCE_ARMOR, ADDED_MULTIPLY, 0.2F))
                )
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        LumenBindingUsageBlockBreak.of(1, 0.15F),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(BLOCK_BREAK_SPEED, ADDED_MULTIPLY, 0.1F))
                )
                .potionEffect(MobEffects.DIG_SPEED, IntRange.of(minutes(4), minutes(6)), IntRange.of(0, 1))
                .build()
                .registerBinding(LumenAS.EVORSIO);

        this.newBindingType(LumenAS.VICIO)
                .put(LumenBindingType.SlotType.HELMET, LumenBinding.of(
                        LumenBindingUsageMovement.of(1, 0.3F, 0.02F,
                                Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.04F))
                )
                .put(LumenBindingType.SlotType.CHESTPLATE, LumenBinding.of(
                        LumenBindingUsageMovement.of(1, 0.4F, 0.02F,
                                Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.06F))
                )
                .put(LumenBindingType.SlotType.LEGGINGS, LumenBinding.of(
                        LumenBindingUsageMovement.of(1, 0.3F, 0.02F,
                                Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.04F))
                )
                .put(LumenBindingType.SlotType.BOOTS, LumenBinding.of(
                        LumenBindingUsageMovement.of(1, 0.3F, 0.03F,
                                Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MOVEMENT_SPEED, STACKING_MULTIPLY, 1.1F))
                )
                .put(LumenBindingType.SlotType.MELEE_WEAPON, LumenBinding.of(
                        LumenBindingUsageMovement.of(1, 0.3F, 0.02F,
                                Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.08F))
                )
                .put(LumenBindingType.SlotType.RANGED_WEAPON, LumenBinding.of(
                        LumenBindingUsageDamageDealt.of(1, 0.4F, false),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(PROJECTILE_SPEED, ADDED_MULTIPLY, 0.1F))
                )
                .put(LumenBindingType.SlotType.TOOL, LumenBinding.of(
                        LumenBindingUsageMovement.of(1, 0.3F, 0.04F,
                                Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.WALK_ON_WATER_ONE_CM),
                        List.of(),
                        LumenBindingDynamicModifierEffect.of(MOVEMENT_SPEED, ADDED_MULTIPLY, 0.1F))
                )
                .potionEffect(MobEffects.MOVEMENT_SPEED, IntRange.of(minutes(3), minutes(6)), IntRange.of(0, 1))
                .build()
                .registerBinding(LumenAS.VICIO);
    }
}
