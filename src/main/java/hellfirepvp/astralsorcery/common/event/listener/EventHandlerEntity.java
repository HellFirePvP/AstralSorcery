/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.EntityKnockbackEvent;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationDraconicEvolution;
import hellfirepvp.astralsorcery.common.item.tool.wand.ItemWand;
import hellfirepvp.astralsorcery.common.item.tool.wand.WandAugment;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.auxiliary.SwordSharpenHelper;
import hellfirepvp.astralsorcery.common.util.data.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerEntity
 * Created by HellFirePvP
 * Date: 01.08.2017 / 20:28
 */
public class EventHandlerEntity {

    private static final Random rand = new Random();
    private static final Color discidiaWandColor = new Color(0x880100);

    public static TickTokenizedMap<WorldBlockPos, TickTokenizedMap.SimpleTickToken<Double>> spawnDenyRegions = new TickTokenizedMap<>(TickEvent.Type.SERVER);
    public static TimeoutList<EntityPlayer> invulnerabilityCooldown = new TimeoutList<>(null, TickEvent.Type.SERVER);
    public static TimeoutList<EntityPlayer> ritualFlight = new TimeoutList<>(player -> {
        if(!player.isCreative()) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
        }
    }, TickEvent.Type.SERVER);
    public static Map<Integer, EntityAttackStack> attackStack = new HashMap<>();

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event) {
        EventHandlerServer.PlayerWrapperContainer ctOld = new EventHandlerServer.PlayerWrapperContainer(event.getOriginal());
        EventHandlerServer.PlayerWrapperContainer ctNew = new EventHandlerServer.PlayerWrapperContainer(event.getEntityPlayer());
        mergeTimeoutContents(EventHandlerServer.perkCooldowns, ctOld, ctNew);
        mergeTimeoutContents(EventHandlerServer.perkCooldownsClient, ctOld, ctNew);
    }

    private <V> void mergeTimeoutContents(TimeoutListContainer<EventHandlerServer.PlayerWrapperContainer, V> container,
                                          EventHandlerServer.PlayerWrapperContainer old, EventHandlerServer.PlayerWrapperContainer newPlayer) {
        if(container.hasList(old)) {
            TimeoutList<V> list = container.removeList(old);
            container.getOrCreateList(newPlayer).addAll(list);
        }
    }

    @SubscribeEvent
    public void onTarget(LivingSetAttackTargetEvent event) {
        EntityLivingBase living = event.getTarget();
        if (living != null && !living.isDead && living instanceof EntityPlayer) {
            if (invulnerabilityCooldown.contains((EntityPlayer) living)) {
                event.getEntityLiving().setRevengeTarget(null);
                if (event.getEntityLiving() instanceof EntityLiving) {
                    ((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
                }
            }
        }
    }

    @SubscribeEvent
    public void onKnockback(EntityKnockbackEvent event) {
        Entity attacker = event.getAttacker();
        if (attacker == null || attacker.getEntityWorld().isRemote) return;

        if (attacker instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) attacker;
            PlayerProgress prog = ResearchManager.getProgress(p);
            if (prog != null) {
                Map<ConstellationPerk, Integer> perks = prog.getAppliedPerks();
                for (ConstellationPerk perk : perks.keySet()) {
                    if (!prog.isPerkActive(perk)) continue;
                    if (perk.mayExecute(ConstellationPerk.Target.ENTITY_KNOCKBACK)) {
                        perk.onEntityKnockback(p, event.getEntityLiving());
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onAttack(LivingHurtEvent event) {
        if(event.getEntity().getEntityWorld().isRemote) return;

        DamageSource source = event.getSource();
        if(source.getTrueSource() != null) {
            EntityLivingBase entitySource = null;
            if(source.getTrueSource() instanceof EntityLivingBase) {
                entitySource = (EntityLivingBase) source.getTrueSource();
            } else if(source.getTrueSource() instanceof EntityArrow) {
                Entity shooter = ((EntityArrow) source.getTrueSource()).shootingEntity;
                if(shooter != null && shooter instanceof EntityLivingBase) {
                    entitySource = (EntityLivingBase) shooter;
                }
            }
            if(entitySource != null) {
                WandAugment foundAugment = null;
                ItemStack stack = entitySource.getHeldItemMainhand();
                if(!stack.isEmpty() && stack.getItem() instanceof ItemWand) {
                    foundAugment = ItemWand.getAugment(stack);
                }
                stack = entitySource.getHeldItemOffhand();
                if(foundAugment == null && !stack.isEmpty() && stack.getItem() instanceof ItemWand) {
                    foundAugment = ItemWand.getAugment(stack);
                }
                if(foundAugment != null && foundAugment.equals(WandAugment.DISCIDIA)) {
                    EntityAttackStack attack = attackStack.get(entitySource.getEntityId());
                    if(attack == null) {
                        attack = new EntityAttackStack();
                        attackStack.put(entitySource.getEntityId(), attack);
                    }
                    EntityLivingBase entity = event.getEntityLiving();
                    float multiplier = attack.getAndUpdateMultipler(entity);
                    event.setAmount(event.getAmount() * (1F + multiplier));
                    PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.DISCIDIA_ATTACK_STACK, entity.posX, entity.posY, entity.posZ);
                    ev.setAdditionalData(multiplier);
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(event.getEntity().world, event.getEntity().getPosition(), 64));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDeathInform(LivingDeathEvent event) {
        attackStack.remove(event.getEntity().getEntityId());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDamage(LivingHurtEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (living == null || living.getEntityWorld().isRemote) return;

        if (!living.isDead && living instanceof EntityPlayer) {
            if (invulnerabilityCooldown.contains((EntityPlayer) living)) {
                event.setCanceled(true);
                return;
            }
        }

        DamageSource source = event.getSource();
        if(Mods.DRACONICEVOLUTION.isPresent()) {
            ItemStack chest = living.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if(!chest.isEmpty() && chest.getItem() instanceof ItemCape && ModIntegrationDraconicEvolution.isChaosDamage(source)) {
                event.setAmount(event.getAmount() * (1F - Config.capeChaosResistance));
                if(event.getAmount() <= 1E-4) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        lblIn:
        if (source.getTrueSource() != null) {
            EntityPlayer p;
            if (source.getTrueSource() instanceof EntityPlayer) {
                p = (EntityPlayer) source.getTrueSource();
            } else if (source.getTrueSource() instanceof EntityArrow) {
                Entity shooter = ((EntityArrow) source.getTrueSource()).shootingEntity;
                if (shooter != null && shooter instanceof EntityPlayer) {
                    p = (EntityPlayer) shooter;
                } else {
                    break lblIn;
                }
            } else {
                break lblIn;
            }
            ItemStack held = p.getHeldItemMainhand();
            if(SwordSharpenHelper.isSwordSharpened(held)) {
                //YEEEAAAA i know this flat multiplies all damage.. but w/e..
                //There's no great way to test for item here.
                event.setAmount(event.getAmount() * (1 + ((float) Config.swordSharpMultiplier)));
            }

            PlayerProgress prog = ResearchManager.getProgress(p);
            if (prog != null) {
                float dmg = event.getAmount();
                Map<ConstellationPerk, Integer> perks = prog.getAppliedPerks();
                for (ConstellationPerk perk : perks.keySet()) {
                    if (!prog.isPerkActive(perk)) continue;
                    if (perk.mayExecute(ConstellationPerk.Target.ENTITY_ATTACK)) {
                        dmg = perk.onEntityAttack(p, event.getEntityLiving(), dmg);
                    }
                }
                event.setAmount(dmg);
            }
        }
        EntityLivingBase entity = event.getEntityLiving();
        if (entity != null) {
            if(entity instanceof EntityPlayer) {
                EntityPlayer hurt = (EntityPlayer) entity;
                PlayerProgress prog = ResearchManager.getProgress(hurt);
                if (prog != null) {
                    float dmg = event.getAmount();
                    Map<ConstellationPerk, Integer> perks = prog.getAppliedPerks();
                    for (ConstellationPerk perk : perks.keySet()) {
                        if (!prog.isPerkActive(perk)) continue;
                        if (perk.mayExecute(ConstellationPerk.Target.ENTITY_HURT)) {
                            dmg = perk.onEntityHurt(hurt, source, dmg);
                        }
                    }
                    event.setAmount(dmg);
                }
            }

            ItemStack active = entity.getActiveItemStack();
            if(!active.isEmpty() && active.getItem() instanceof ItemWand) {
                WandAugment wa = ItemWand.getAugment(active);
                if(wa != null && wa.equals(WandAugment.ARMARA)) {
                    PotionEffect potion = new PotionEffect(MobEffects.RESISTANCE, 100, 0);
                    if(entity.isPotionApplicable(potion)) {
                        entity.addPotionEffect(potion);
                    }
                    potion = new PotionEffect(MobEffects.ABSORPTION, 100, 1);
                    if(entity.isPotionApplicable(potion)) {
                        entity.addPotionEffect(potion);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onSpawnTest(LivingSpawnEvent.CheckSpawn event) {
        if (event.getResult() == Event.Result.DENY) return; //Already denied anyway.

        EntityLivingBase toTest = event.getEntityLiving();
        Vector3 at = Vector3.atEntityCorner(toTest);
        boolean mayDeny = Config.doesMobSpawnDenyDenyEverything || toTest.isCreatureType(EnumCreatureType.MONSTER, false);
        if (mayDeny) {
            for (Map.Entry<WorldBlockPos, TickTokenizedMap.SimpleTickToken<Double>> entry : spawnDenyRegions.entrySet()) {
                if (!entry.getKey().getWorld().equals(toTest.getEntityWorld())) continue;
                if (at.distance(entry.getKey()) <= entry.getValue().getValue()) {
                    event.setResult(Event.Result.DENY);
                    return;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playDiscidiaStackAttackEffects(PktParticleEvent pkt) {
        Vector3 at = pkt.getVec();
        World w = Minecraft.getMinecraft().world;
        EntityLivingBase found = null;
        if(w != null) {
            EntityLivingBase e = EntityUtils.selectClosest(
                    w.getEntitiesWithinAABB(EntityLivingBase.class, Block.FULL_BLOCK_AABB.offset(at.getX() - 0.5, at.getY() - 0.5, at.getZ() - 0.5)),
                    (ent) -> ent.getDistance(at.getX(), at.getY(), at.getZ()));
            if(e != null) {
                found = e;
            }
        }
        if(found != null) {
            AxisAlignedBB box = found.getEntityBoundingBox();
            for (int i = 0; i < 24; i++) {
                if(rand.nextFloat() < pkt.getAdditionalData()) {
                    Vector3 pos = new Vector3(
                            box.minX + ((box.maxX - box.minX) * rand.nextFloat()),
                            box.minY + ((box.maxY - box.minY) * rand.nextFloat()),
                            box.minZ + ((box.maxZ - box.minZ) * rand.nextFloat()));
                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
                    p.setColor(discidiaWandColor).setMaxAge(25 + rand.nextInt(10));
                    p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).setAlphaMultiplier(1F);
                    p.gravity(0.004).scale(0.15F + rand.nextFloat() * 0.1F);
                    Vector3 motion = new Vector3();
                    MiscUtils.applyRandomOffset(motion, rand, 0.03F);
                    p.motion(motion.getX(), motion.getY(), motion.getZ());
                }
            }
        }
    }

    private static class EntityAttackStack {

        private static long stackMsDuration = 5000;

        private int entityStackId = -1;
        private long lastStackMs = 0;
        private int stack = 0;

        public float getMultiplier(Entity attackedEntity) {
            return getMultiplier(attackedEntity.getEntityId());
        }

        public float getMultiplier(int attackedEntityId) {
            if(entityStackId != attackedEntityId) {
                return 0F;
            }
            return (((float) stack) / ((float) Config.discidiaStackCap)) * Config.discidiaStackMultiplier;
        }

        public float getAndUpdateMultipler(Entity attackedEntity) {
            return getAndUpdateMultipler(attackedEntity.getEntityId());
        }

        public float getAndUpdateMultipler(int attackedEntityId) {
            if(attackedEntityId != entityStackId) {
                entityStackId = attackedEntityId;
                lastStackMs = System.currentTimeMillis();
                stack = 0;
            } else {
                long current = System.currentTimeMillis();
                long diff = current - lastStackMs;
                lastStackMs = current;
                if(diff < stackMsDuration) {
                    stack = MathHelper.clamp(stack + 1, 0, Config.discidiaStackCap);
                } else {
                    stack = MathHelper.clamp(stack - ((int) (diff / stackMsDuration)), 0, Config.discidiaStackCap);
                }
            }
            return (((float) stack) / ((float) Config.discidiaStackCap)) * Config.discidiaStackMultiplier;
        }

    }

}
