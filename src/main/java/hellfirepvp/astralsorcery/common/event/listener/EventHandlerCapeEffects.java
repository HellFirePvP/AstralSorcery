/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.base.Plants;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectAevitas;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectDiscidia;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectFornax;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.CropHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

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

    //Prevent damage overflow for discidia
    private static boolean chainingAttack = false;

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if(event.getEntityLiving() != null && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer pl = (EntityPlayer) event.getEntityLiving();
            CapeEffectDiscidia cd = ItemCape.getCapeEffect(pl, Constellations.discidia);
            if(cd != null) {
                cd.writeLastAttackDamage(event.getAmount());
            }
            if(event.getSource().isFireDamage()) {
                CapeEffectFornax cf = ItemCape.getCapeEffect(pl, Constellations.fornax);
                if(cf != null) {
                    cf.healFor(pl, event.getAmount());
                    float mul = cf.getDamageMultiplier();
                    if(mul <= 0) {
                        event.setCanceled(true);
                    } else {
                        event.setAmount(event.getAmount() * mul);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if(chainingAttack) return;

        DamageSource ds = event.getSource();
        if(ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) ds.getTrueSource();
            CapeEffectDiscidia cd = ItemCape.getCapeEffect(attacker, Constellations.discidia);
            if(cd != null) {
                double added = cd.getLastAttackDamage();

                chainingAttack = true;
                attacker.attackEntityFrom(DamageSource.causePlayerDamage(attacker), (float) added);
                attacker.attackEntityFrom(CommonProxy.dmgSourceStellar, (float) (added / 2));
                chainingAttack = false;
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
                bb.offset(pl.posX, pl.posY, pl.posZ);
                Predicate<Entity> pr = EntitySelectors.NOT_SPECTATING.and(EntitySelectors.IS_ALIVE);
                List<EntityPlayer> players = w.getEntitiesWithinAABB(EntityPlayer.class, bb, pr::test);
                for (EntityPlayer player : players) {
                    player.heal(0.8F);
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

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        switch (type) {
            case WORLD:
                break;
            case PLAYER:
                EntityPlayer pl = (EntityPlayer) context[0];
                Side side = (Side) context[1];
                if(side == Side.SERVER) {
                    tickAevitasEffect(pl);
                    tickFornaxMelting(pl);
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
