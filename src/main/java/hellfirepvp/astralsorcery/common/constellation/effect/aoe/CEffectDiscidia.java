/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.base.ConstellationEffectEntityCollect;
import hellfirepvp.astralsorcery.common.data.config.registry.TechnicalEntityRegistry;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.DamageSourceUtil;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
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
 * Class: CEffectDiscidia
 * Created by HellFirePvP
 * Date: 23.11.2019 / 21:35
 */
public class CEffectDiscidia extends ConstellationEffectEntityCollect<LivingEntity> {

    public static DiscidiaConfig CONFIG = new DiscidiaConfig();

    public CEffectDiscidia(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.discidia, LivingEntity.class,
                entity -> entity.isAlive() && !TechnicalEntityRegistry.INSTANCE.canAffect(entity));
    }

    @Override
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        //TODO effects
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        if ((world.getGameTime() % 20) != 0) { // Run once per second.
            return false;
        }

        boolean didEffect = false;

        float damage = CONFIG.damage.get().floatValue(); //Randomize?..
        PlayerEntity owner = this.getOwningPlayerInWorld(world, pos);
        DamageSource src = owner == null ? CommonProxy.DAMAGE_SOURCE_STELLAR :
                DamageSourceUtil.withEntityDirect(CommonProxy.DAMAGE_SOURCE_STELLAR, owner);
        List<LivingEntity> entities = this.collectEntities(world, pos, properties);
        for (LivingEntity entity : entities) {
            if (properties.isCorrupted()) {
                entity.heal(damage);
                entity.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 30, 1));
                didEffect = true;

            } else {
                if (entity instanceof PlayerEntity &&
                        !MiscUtils.canPlayerAttackServer(null, entity)) {
                    continue;
                }

                int hrTime = entity.hurtResistantTime;
                entity.hurtResistantTime = 0;
                try {
                    if (DamageUtil.attackEntityFrom(entity, src, damage)) {
                        didEffect = true;
                    }
                } finally {
                    entity.hurtResistantTime = hrTime;
                }
            }

        }

        return didEffect;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    private static class DiscidiaConfig extends Config {

        private final double defaultDamage = 5D;

        public ForgeConfigSpec.DoubleValue damage;

        public DiscidiaConfig() {
            super("discidia", 10D, 2D);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.damage = cfgBuilder
                    .comment("Defines the max. possible damage dealt per damage tick.")
                    .translation(translationKey("damage"))
                    .defineInRange("damage", this.defaultDamage, 0.1, 128.0D);
        }
    }
}
