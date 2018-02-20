/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.base.Plants;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.*;
import hellfirepvp.astralsorcery.common.entities.EntitySpectralTool;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktElytraCapeState;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.CropHelper;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerCapeEffects
 * Created by HellFirePvP
 * Date: 10.10.2017 / 00:34
 */
public class EventHandlerCapeEffects implements ITickHandler {

    private static final Random rand = new Random();
    public static EventHandlerCapeEffects INSTANCE = new EventHandlerCapeEffects();

    private EventHandlerCapeEffects() {}

    //Propagate player in tick for octans anti-knockback effect.
    public static EntityPlayer currentPlayerInTick = null;

    //Prevent event overflow
    private static boolean discidiaChainingAttack = false;
    private static boolean evorsioChainingBreak = false;

    //To propagate elytra states
    private static boolean updateElytraBuffer = false;
    public static boolean inElytraCheck = false;

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event) {
        if(event.getWorld().isRemote) return;
        if(evorsioChainingBreak) return;

        EntityPlayer pl = event.getPlayer();
        if(pl == null || !(pl instanceof EntityPlayerMP)) return;
        if(MiscUtils.isPlayerFakeMP((EntityPlayerMP) pl)) return;

        IBlockState state = event.getState();
        ItemStack held = pl.getHeldItemMainhand();


        CapeEffectPelotrio pel = ItemCape.getCapeEffect(pl, Constellations.pelotrio);
        if(pel != null) {
            if(("pickaxe".equalsIgnoreCase(state.getBlock().getHarvestTool(state)) ||
                    (!state.getMaterial().isToolNotRequired() && Items.DIAMOND_PICKAXE.canHarvestBlock(state))) &&
                    !pl.getHeldItemMainhand().isEmpty() && pl.getHeldItemMainhand().getItem().getToolClasses(held).contains("pickaxe")) {
                if (rand.nextFloat() < pel.getChanceSpawnPick()) {
                    BlockPos at = pl.getPosition().up();
                    EntitySpectralTool esp = new EntitySpectralTool(
                            event.getWorld(), at, new ItemStack(Items.DIAMOND_PICKAXE),
                            EntitySpectralTool.ToolTask.createPickaxeTask());
                    event.getWorld().spawnEntity(esp);
                    return;
                }
            }
            if((state.getBlock().isWood(event.getWorld(), event.getPos()) ||
                    state.getBlock().isLeaves(state, event.getWorld(), event.getPos())) &&
                    !pl.getHeldItemMainhand().isEmpty() && pl.getHeldItemMainhand().getItem().getToolClasses(held).contains("axe")) {
                if (rand.nextFloat() < pel.getChanceSpawnAxe()) {
                    BlockPos at = pl.getPosition().up();
                    EntitySpectralTool esp = new EntitySpectralTool(
                            event.getWorld(), at, new ItemStack(Items.DIAMOND_AXE),
                            EntitySpectralTool.ToolTask.createLogTask());
                    event.getWorld().spawnEntity(esp);
                }
            }
        }
        CapeEffectEvorsio ev =  ItemCape.getCapeEffect(pl, Constellations.evorsio);
        if(ev != null &&
                !pl.getHeldItemMainhand().isEmpty() &&
                !pl.getHeldItemMainhand().getItem().getToolClasses(pl.getHeldItemMainhand()).isEmpty()) {
            evorsioChainingBreak = true;
            try {
                RayTraceResult rtr = MiscUtils.rayTraceLook(pl);
                if(rtr != null) {
                    EnumFacing faceHit = rtr.sideHit;
                    if(faceHit != null) {
                        ev.breakBlocksPlane((EntityPlayerMP) pl, faceHit, event.getWorld(), event.getPos());
                    }
                }
            } finally {
                evorsioChainingBreak = false;
            }
        }
    }

    @SubscribeEvent
    public void playerUpdatePre(TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.START) {
            currentPlayerInTick = event.player;
        } else {
            currentPlayerInTick = null;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onHurt(LivingHurtEvent event) {
        if(event.getEntityLiving().world.isRemote) return;

        if(event.getEntityLiving() != null && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer pl = (EntityPlayer) event.getEntityLiving();
            CapeEffectDiscidia cd = ItemCape.getCapeEffect(pl, Constellations.discidia);
            if(cd != null) {
                cd.writeLastAttackDamage(event.getAmount());
            }
            CapeEffectArmara ca = ItemCape.getCapeEffect(pl, Constellations.armara);
            if(ca != null) {
                if(ca.shouldPreventDamage(event.getSource(), false)) {
                    event.setCanceled(true);
                    return;
                }
            }
            CapeEffectBootes bo = ItemCape.getCapeEffect(pl, Constellations.bootes);
            if(bo != null && event.getSource().getTrueSource() != null) {
                Entity source = event.getSource().getTrueSource();
                if(source instanceof EntityLivingBase) {
                    bo.onPlayerDamagedByEntity(pl, (EntityLivingBase) source);
                }
            }
            if(event.getSource().isFireDamage()) {
                CapeEffectFornax cf = ItemCape.getCapeEffect(pl, Constellations.fornax);
                if(cf != null) {
                    cf.healFor(pl, event.getAmount());
                    float mul = cf.getDamageMultiplier();
                    if(mul <= 0) {
                        event.setCanceled(true);
                        return;
                    } else {
                        event.setAmount(event.getAmount() * mul);
                    }
                }
            } else {
                CapeEffectHorologium horo = ItemCape.getCapeEffect(pl, Constellations.horologium);
                if(horo != null) {
                    horo.onHurt(pl);
                }
            }
        }
    }

    @SubscribeEvent
    public void onKill(LivingDeathEvent event) {
        if(event.getEntity().getEntityWorld().isRemote) return;

        DamageSource ds = event.getSource();
        if(ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer pl = (EntityPlayer) ds.getTrueSource();
            if(!(pl instanceof EntityPlayerMP)) return;
            if(MiscUtils.isPlayerFakeMP((EntityPlayerMP) pl)) return;

            CapeEffectEvorsio ev = ItemCape.getCapeEffect(pl, Constellations.evorsio);
            if(ev != null) {
                ev.deathAreaDamage(ds, event.getEntityLiving());
            }
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if(discidiaChainingAttack) return;
        if(event.getEntityLiving().world.isRemote) return;

        DamageSource ds = event.getSource();
        if(ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) ds.getTrueSource();
            if(!(attacker instanceof EntityPlayerMP)) return;
            if(MiscUtils.isPlayerFakeMP((EntityPlayerMP) attacker)) return;

            CapeEffectDiscidia cd = ItemCape.getCapeEffect(attacker, Constellations.discidia);
            if(cd != null) {
                double added = cd.getLastAttackDamage();

                discidiaChainingAttack = true;
                try {
                    attacker.attackEntityFrom(DamageSource.causePlayerDamage(attacker), (float) added);
                    attacker.attackEntityFrom(CommonProxy.dmgSourceStellar, (float) (added / 2));
                } finally {
                    discidiaChainingAttack = false;
                }
            }
            CapeEffectPelotrio pel = ItemCape.getCapeEffect(attacker, Constellations.pelotrio);
            if (pel != null && !attacker.getHeldItemMainhand().isEmpty() && rand.nextFloat() < pel.getChanceSpawnSword()) {
                BlockPos at = attacker.getPosition().up();
                EntitySpectralTool esp = new EntitySpectralTool(
                        attacker.getEntityWorld(), at, new ItemStack(Items.DIAMOND_SWORD),
                        EntitySpectralTool.ToolTask.createAttackTask());
                attacker.getEntityWorld().spawnEntity(esp);
            }
        }
    }

    /**
     * {@link EntityPlayer#getDigSpeed(IBlockState, BlockPos)}
     */
    @SubscribeEvent
    public void onWaterBreak(PlayerEvent.BreakSpeed event) {
        EntityPlayer pl = event.getEntityPlayer();
        if(pl.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(pl)) {
            //Normally the break speed would be divided by 5 here in the actual logic. See link above
            CapeEffectOctans ceo = ItemCape.getCapeEffect(pl, Constellations.octans);
            if(ceo != null) {
                //Revert speed back to what we think is original.
                // Might stack with others that implement it the same way.
                event.setNewSpeed(event.getOriginalSpeed() * 5);
            }
        }
    }

    private void tickFornaxMelting(EntityPlayer pl) {
        if(pl.isBurning()) {
            CapeEffectFornax cf = ItemCape.getCapeEffect(pl, Constellations.fornax);
            if(cf != null) {
                cf.attemptMelt(pl);
            }
        }
    }

    private void tickAevitasEffect(EntityPlayer pl) {
        CapeEffectAevitas cd = ItemCape.getCapeEffect(pl, Constellations.aevitas);
        if(cd != null) {
            float potency = cd.getPotency();
            float range = cd.getRange();
            if(rand.nextFloat() < potency) {
                World w = pl.getEntityWorld();
                AxisAlignedBB bb = new AxisAlignedBB(-range, -range, -range, range, range, range);
                bb = bb.offset(pl.posX, pl.posY, pl.posZ);
                Predicate<Entity> pr = EntitySelectors.NOT_SPECTATING.and(EntitySelectors.IS_ALIVE);
                List<EntityPlayer> players = w.getEntitiesWithinAABB(EntityPlayer.class, bb, pr::test);
                for (EntityPlayer player : players) {
                    player.heal(0.3F);
                    player.getFoodStats().addStats(2, 0.4F);
                }
            }
            if(rand.nextFloat() < cd.getTurnChance()) {
                int x = Math.round(-range + 1 + (2 * range * rand.nextFloat()));
                int y = Math.round(-range + 1 + (2 * range * rand.nextFloat()));
                int z = Math.round(-range + 1 + (2 * range * rand.nextFloat()));
                BlockPos at = pl.getPosition().add(x, y, z);
                IBlockState state = pl.getEntityWorld().getBlockState(at);
                if(Plants.matchesAny(state)) {
                    state = Plants.getAnyRandomState();
                    pl.getEntityWorld().setBlockState(at, state);
                    PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_CROP_INTERACT, at);
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(pl.getEntityWorld(), at, 16));
                } else {
                    CropHelper.GrowablePlant growable = CropHelper.wrapPlant(pl.getEntityWorld(), at);
                    if(growable != null) {
                        growable.tryGrow(pl.getEntityWorld(), rand);
                        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_CROP_INTERACT, at);
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(pl.getEntityWorld(), at, 16));
                    }
                }
            }
        }
    }

    private void tickArmaraWornEffect(EntityPlayer pl) {
        CapeEffectArmara ca = ItemCape.getCapeEffect(pl, Constellations.armara);
        if(ca != null) {
            ca.wornTick();
        }
    }

    @SideOnly(Side.CLIENT)
    private void tickVicioClientEffect(EntityPlayer player) {
        if(player instanceof EntityPlayerSP) {
            EntityPlayerSP spl = (EntityPlayerSP) player;
            if(spl.movementInput.jump && !spl.onGround && spl.motionY < -0.5 && !spl.capabilities.isFlying && !spl.isInWater() && !spl.isInLava()) {
                PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.resetFallDistance());
                if(!spl.isElytraFlying()) {
                    PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.setFlying());
                }
            } else if(spl.isElytraFlying()) {
                PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.resetFallDistance());
                if(spl.capabilities.isFlying || spl.onGround || spl.isInWater() || spl.isInLava()) {
                    PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.resetFlying());
                } else {
                    Vector3 mov = new Vector3(((EntityPlayerSP) player).motionX, 0, ((EntityPlayerSP) player).motionZ);
                    if(mov.length() <= 0.4F && ((EntityPlayerSP) player).motionY > 0.4F) {
                        PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.resetFlying());
                    }
                }
            }
        }
    }

    private void tickOctansEffect(EntityPlayer pl) {
        CapeEffectOctans ceo = ItemCape.getCapeEffect(pl, Constellations.octans);
        if(ceo != null && pl.isInsideOfMaterial(Material.WATER)) {
            if(pl.getAir() < 300) {
                pl.setAir(300);
            }
            ceo.onWaterHealTick(pl);
        }
    }

    private void tickBootesEffect(EntityPlayer pl) {
        CapeEffectBootes ceo = ItemCape.getCapeEffect(pl, Constellations.bootes);
        if(ceo != null) {
            ceo.onPlayerTick(pl);
        }
    }

    public static void updateElytraEventPre(EntityLivingBase entity) {
        if(entity instanceof EntityPlayer) {
            CapeEffectVicio vic = ItemCape.getCapeEffect((EntityPlayer) entity, Constellations.vicio);
            if(vic != null) {
                updateElytraBuffer = entity.getFlag(7);
                inElytraCheck = true;
            }
        }
    }

    public static void updateElytraEventPost(EntityLivingBase entity) {
        inElytraCheck = false;
        if(entity instanceof EntityPlayer && updateElytraBuffer) {
            CapeEffectVicio vic = ItemCape.getCapeEffect((EntityPlayer) entity, Constellations.vicio);
            if(vic != null) {
                boolean current = entity.getFlag(7);
                // So the state from true before has now changed to false.
                // We need to check if the item not being an elytra is responsible for that.
                if(!current) {
                    if(!((EntityPlayer) entity).onGround && !entity.isRiding()) {
                        entity.setFlag(7, true);
                    }
                }

                //TODO find a better solution.
                //Vector3 mV = new Vector3(entity.motionX, entity.motionY, entity.motionZ).normalize().multiply(0.65F);
                //entity.motionX += mV.getX() * 0.1D + (mV.getX() * 1.5D - entity.motionX) * 0.5D;
                //entity.motionY += mV.getY() * 0.1D + (mV.getY() * 1.5D - entity.motionY) * 0.5D;
                //entity.motionZ += mV.getZ() * 0.1D + (mV.getZ() * 1.5D - entity.motionZ) * 0.5D;
                entity.motionX *= 1.005F;
                entity.motionY *= 1.005F;
                entity.motionZ *= 1.005F;
            }
        }
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        switch (type) {
            case WORLD:
                break;
            case PLAYER:
                EntityPlayer pl = (EntityPlayer) context[0];
                Side side = (Side) context[1];
                if(side == Side.SERVER) {
                    if(!(pl instanceof EntityPlayerMP)) return;
                    if(MiscUtils.isPlayerFakeMP((EntityPlayerMP) pl)) return;

                    tickAevitasEffect(pl);
                    tickFornaxMelting(pl);
                    tickArmaraWornEffect(pl);
                    tickOctansEffect(pl);
                    tickBootesEffect(pl);
                } else if(side == Side.CLIENT) {
                    CapeArmorEffect cae = ItemCape.getCapeEffect(pl);
                    if(cae != null) {
                        cae.playActiveParticleTick(pl);
                    }
                    CapeEffectVicio vic = ItemCape.getCapeEffect(pl, Constellations.vicio);
                    if(vic != null) {
                        tickVicioClientEffect(pl);
                    }
                    CapeEffectLucerna luc = ItemCape.getCapeEffect(pl, Constellations.lucerna);
                    if(luc != null) {
                        luc.playClientHighlightTick(pl);
                    }
                    CapeEffectMineralis min = ItemCape.getCapeEffect(pl, Constellations.mineralis);
                    if(min != null) {
                        min.playClientHighlightTick(pl);
                    }
                }
                break;
            case CLIENT:
                break;
            case SERVER:
                break;
            case RENDER:
                break;
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Cape-EventHandler";
    }

}
