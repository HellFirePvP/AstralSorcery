/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.config.registry.TechnicalEntityRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyLightningArc
 * Created by HellFirePvP
 * Date: 31.08.2019 / 19:06
 */
public class KeyLightningArc extends KeyPerk {

    private static final float defaultArcChance = 0.6F;
    private static final float defaultArcPercent = 0.6F;
    private static final float defaultArcDistance = 7F;
    private static final int defaultArcTicks = 3;

    private static final int arcChains = 3;

    private final Config config;

    public KeyLightningArc(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.config = new Config(name.getPath());
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);

        bus.addListener(EventPriority.LOWEST, this::onAttack);
    }

    @Nullable
    @Override
    protected ConfigEntry addConfig() {
        return this.config;
    }

    private void onAttack(LivingHurtEvent event) {
        if (EventFlags.LIGHTNING_ARC.isSet()) {
            return;
        }

        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getTrueSource();
            LogicalSide side = this.getSide(player);
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            if (side.isServer() && prog.hasPerkEffect(this) && prog.doPerkAbilities()) {
                float chance = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, (float) this.applyMultiplierD(this.config.arcChance.get()));
                if (rand.nextFloat() < chance) {
                    float dmg = event.getAmount();
                    dmg *= this.applyMultiplierD(this.config.arcPercent.get());
                    new RepetitiveArcEffect(player.world,
                            player,
                            this.applyMultiplierI(this.config.arcTicks.get()),
                            event.getEntityLiving().getEntityId(),
                            dmg,
                            this.applyMultiplierD(this.config.arcDistance.get())).fire();
                }
            }
        }
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.DoubleValue arcChance;
        private ForgeConfigSpec.DoubleValue arcPercent;
        private ForgeConfigSpec.DoubleValue arcDistance;
        private ForgeConfigSpec.IntValue arcTicks;

        public Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.arcChance = cfgBuilder
                    .comment("Sets the chance to spawn a damage-arc effect when an enemy is hit (value is in percent).")
                    .translation(translationKey("arcChance"))
                    .defineInRange("arcChance", defaultArcChance, 0.1, 1.0);

            this.arcPercent = cfgBuilder
                    .comment("Defines the damage-multiplier which gets added to the damage dealt initially.")
                    .translation(translationKey("arcPercent"))
                    .defineInRange("arcPercent", defaultArcPercent, 0.1, 8.0);

            this.arcDistance = cfgBuilder
                    .comment("Defines the distance for how far a single arc can jump/search for nearby entities.")
                    .translation(translationKey("arcDistance"))
                    .defineInRange("arcDistance", defaultArcDistance, 0.2, 16.0);

            this.arcTicks = cfgBuilder
                    .defineInRange("arcTicks", defaultArcTicks, 1, 32);
        }
    }

    static class RepetitiveArcEffect {

        private World world;
        private PlayerEntity player;
        private int count;
        private int entityStartId;
        private float damage;
        private double distance;

        public RepetitiveArcEffect(World world, PlayerEntity player, int count, int entityStartId, float damage, double distance) {
            this.world = world;
            this.player = player;
            this.count = count;
            this.entityStartId = entityStartId;
            this.damage = damage;
            this.distance = distance;
        }

        void fire() {
            if (!player.isAlive()) {
                return;
            }

            int chainTimes = Math.round(PerkAttributeHelper.getOrCreateMap(player, LogicalSide.SERVER)
                    .modifyValue(player, ResearchHelper.getProgress(player, LogicalSide.SERVER), PerkAttributeTypesAS.ATTR_TYPE_ARC_CHAINS, arcChains));
            List<LivingEntity> visitedEntities = Lists.newArrayList();
            Entity start = world.getEntityByID(entityStartId);

            if (start instanceof LivingEntity && start.isAlive()) {
                AxisAlignedBB box = new AxisAlignedBB(-distance, -distance, -distance, distance, distance, distance);

                LivingEntity last = null;
                LivingEntity entity = (LivingEntity) start;
                while (entity != null && entity.isAlive() && chainTimes > 0) {
                    visitedEntities.add(entity);
                    chainTimes--;

                    if (last != null) {
                        Vector3 from = Vector3.atEntityCenter(entity);
                        Vector3 to = Vector3.atEntityCenter(last);
                        PacketDistributor.TargetPoint target = PacketChannel.pointFromPos(world, entity.getPosition(), 16);
                        PacketChannel.CHANNEL.sendToAllAround(new PktPlayEffect(PktPlayEffect.Type.LIGHTNING)
                                .addData(buf -> {
                                    ByteBufUtils.writeVector(buf, from);
                                    ByteBufUtils.writeVector(buf, to);
                                    buf.writeInt(ColorsAS.EFFECT_LIGHTNING.getRGB());
                                }), target);
                        PacketChannel.CHANNEL.sendToAllAround(new PktPlayEffect(PktPlayEffect.Type.LIGHTNING)
                                .addData(buf -> {
                                    ByteBufUtils.writeVector(buf, to);
                                    ByteBufUtils.writeVector(buf, from);
                                    buf.writeInt(ColorsAS.EFFECT_LIGHTNING.getRGB());
                                }), target);
                    }
                    List<LivingEntity> entities = entity.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, box.offset(entity.getPositionVector()), EntityUtils.selectEntities(LivingEntity.class));
                    entities.remove(entity);
                    if (last != null) {
                        entities.remove(last);
                    }
                    if (player != null) {
                        entities.remove(player);
                    }
                    entities.removeAll(visitedEntities);

                    entities.removeIf(e -> !TechnicalEntityRegistry.INSTANCE.canAffect(e));
                    entities.removeIf(e -> !MiscUtils.canPlayerAttackServer(player, e));

                    if (!entities.isEmpty()) {
                        LivingEntity tmpEntity = entity; //Final for lambda
                        LivingEntity closest = EntityUtils.selectClosest(entities, (e) -> (double) e.getDistance(tmpEntity));
                        if (closest != null && closest.isAlive()) {
                            last = entity;
                            entity = closest;
                        } else {
                            entity = null;
                        }
                    } else {
                        entity = null;
                    }
                }

                if (visitedEntities.size() > 1) {
                    visitedEntities.forEach((e) -> {
                        EventFlags.LIGHTNING_ARC.executeWithFlag(() -> {
                            DamageUtil.attackEntityFrom(e, CommonProxy.DAMAGE_SOURCE_STELLAR, damage, player);
                        });
                    });
                }
            }

            if (count > 0) {
                count--;
                AstralSorcery.getProxy().scheduleDelayed(this::fire, 12);
            }
        }
    }
}
