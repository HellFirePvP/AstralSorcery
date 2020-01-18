/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.AnimalHelper;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.base.ConstellationEffectEntityCollect;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectBootes
 * Created by HellFirePvP
 * Date: 23.11.2019 / 21:06
 */
public class CEffectBootes extends ConstellationEffectEntityCollect<LivingEntity> {

    public static BootesConfig CONFIG = new BootesConfig();

    public CEffectBootes(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.bootes, LivingEntity.class, entity -> AnimalHelper.getHandler(entity) != null);
    }

    @Override
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        //TODO effects
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        boolean didEffect = false;

        List<LivingEntity> entities = this.collectEntities(world, pos, properties);
        for (LivingEntity entity : entities) {
            AnimalHelper.HerdableAnimal animal = AnimalHelper.getHandler(entity);
            if (animal == null) {
                continue;
            }

            if (properties.isCorrupted()) {
                entity.hurtResistantTime = 0;
                entity.addPotionEffect(new EffectInstance(EffectsAS.EFFECT_DROP_MODIFIER, 1000, 5));
                if (DamageUtil.attackEntityFrom(entity, CommonProxy.DAMAGE_SOURCE_STELLAR, 5_000)) {
                    didEffect = true;
                }
                continue;
            }

            if (rand.nextFloat() < CONFIG.herdingChance.get()) {
                didEffect = MiscUtils.executeWithChunk(world, entity.getPosition(), didEffect, (didEffectFlag) -> {

                    List<ItemStack> drops = EntityUtils.generateLoot(entity, rand, CommonProxy.DAMAGE_SOURCE_STELLAR, null);
                    for (ItemStack drop : drops) {
                        if (rand.nextFloat() < CONFIG.herdingLootChance.get() &&
                                ItemUtils.dropItemNaturally(world, entity.posX, entity.posY, entity.posZ, drop) != null) {

                            didEffectFlag = true;
                        }
                    }
                    return didEffectFlag;
                });
            }
        }

        return didEffect;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    private static class BootesConfig extends Config {

        private final double defaultHerdingChance = 0.05;
        private final double defaultHerdingLootChance = 0.01;

        public ForgeConfigSpec.DoubleValue herdingChance;
        public ForgeConfigSpec.DoubleValue herdingLootChance;

        public BootesConfig() {
            super("bootes", 12D, 4D);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.herdingChance = cfgBuilder
                    .comment("Set the chance that an registered animal will be considered for generating loot if it is close to the ritual.")
                    .translation(translationKey("herdingChance"))
                    .defineInRange("herdingChance", this.defaultHerdingChance, 0, 1.0);
            this.herdingLootChance = cfgBuilder
                    .comment("Set the chance that a drop that has been found on the entity's loot table is actually dropped.")
                    .translation(translationKey("herdingLootChance"))
                    .defineInRange("herdingLootChance", this.defaultHerdingLootChance, 0, 1.0);
        }
    }
}
