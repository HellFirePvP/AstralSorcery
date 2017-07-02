/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.render.tile.TESRTranslucentBlock;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockMachine;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.event.BlockModifyEvent;
import hellfirepvp.astralsorcery.common.event.EntityKnockbackEvent;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.EnchantmentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktCraftingTableFix;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.data.*;
import hellfirepvp.astralsorcery.common.world.WorldProviderBrightnessInj;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerServer
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:09
 */
public class EventHandlerServer {

    private static final Random rand = new Random();

    public static TickTokenizedMap<WorldBlockPos, TickTokenizedMap.SimpleTickToken<Double>> spawnDenyRegions = new TickTokenizedMap<>(TickEvent.Type.SERVER);
    public static TimeoutListContainer<EntityPlayer, Integer> perkCooldowns = new TimeoutListContainer<EntityPlayer, Integer>(new ConstellationPerks.PerkTimeoutHandler(), TickEvent.Type.SERVER);
    public static TimeoutList<EntityPlayer> invulnerabilityCooldown = new TimeoutList<>(null, TickEvent.Type.SERVER);

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLoad(WorldEvent.Load event) {
        World w = event.getWorld();
        int id = w.provider.getDimension();
        if (!Config.weakSkyRendersWhitelist.contains(w.provider.getDimension())) {
            AstralSorcery.log.info("[AstralSorcery] Found worldProvider in Dimension " + id + " : " + w.provider.getClass().getName());
            w.provider = new WorldProviderBrightnessInj(w, w.provider);
            AstralSorcery.log.info("[AstralSorcery] Injected WorldProvider into dimension " + id + " (chaining old provider.)");
        }
    }

    @SubscribeEvent
    public void onUnload(WorldEvent.Unload event) {
        World w = event.getWorld();
        ConstellationSkyHandler.getInstance().informWorldUnload(w);
        if (w.isRemote) {
            clientUnload();
        }
    }

    @SideOnly(Side.CLIENT)
    private void clientUnload() {
        AstralSorcery.proxy.scheduleClientside(TESRTranslucentBlock::cleanUp);
    }

    @SubscribeEvent
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
        if (event.getEntityLiving() != null && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer hurt = (EntityPlayer) event.getEntityLiving();
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
    }

    @SubscribeEvent
    public void onContainerOpen(PlayerContainerEvent.Open event) {
        if(event.getContainer() instanceof ContainerWorkbench && !event.getEntityPlayer().world.isRemote && event.getEntityPlayer() instanceof EntityPlayerMP) {
            PacketChannel.CHANNEL.sendTo(new PktCraftingTableFix(((ContainerWorkbench) event.getContainer()).pos), (EntityPlayerMP) event.getEntityPlayer());
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDeath(LivingDeathEvent event) {
        if (phoenixProtect(event.getEntityLiving())) {
            event.setCanceled(true);
        } else {
            if (event.getEntityLiving() == null || event.getEntityLiving().getEntityWorld().isRemote) return;

            DamageSource source = event.getSource();
            if (source.getImmediateSource() != null && source.getImmediateSource() instanceof EntityPlayer) {
                EntityPlayer p = (EntityPlayer) source.getImmediateSource();
                PlayerProgress prog = ResearchManager.getProgress(p);
                if (prog != null) {
                    Map<ConstellationPerk, Integer> perks = prog.getAppliedPerks();
                    for (ConstellationPerk perk : perks.keySet()) {
                        if (!prog.isPerkActive(perk)) continue;
                        if (perk.mayExecute(ConstellationPerk.Target.ENTITY_KILL)) {
                            perk.onEntityKilled(p, event.getEntityLiving());
                        }
                    }
                }
            }
        }
    }

    private boolean phoenixProtect(EntityLivingBase entity) {
        PotionEffect pe = entity.getActivePotionEffect(RegistryPotions.potionCheatDeath);
        if (pe != null) {
            int level = pe.getAmplifier();
            phoenixEffects(entity, level);
            return true;
        }
        return false;
    }

    private void phoenixEffects(EntityLivingBase entity, int level) {
        entity.setHealth(6 + level * 2);
        entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 2, false, false));
        entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 500, 1, false, false));
        List<EntityLivingBase> others = entity.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox().grow(3), (e) -> !e.isDead && e != entity);
        for (EntityLivingBase lb : others) {
            lb.setFire(16);
            lb.knockBack(entity, 2F, lb.posX - entity.posX, lb.posZ - entity.posZ);
        }
        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.PHOENIX_PROC, new Vector3(entity.posX, entity.posY, entity.posZ));
        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(entity.world, entity.getPosition(), 32));

        MinecraftServer server = entity.getServer();
        if (server != null) {
            server.addScheduledTask(() -> entity.removePotionEffect(RegistryPotions.potionCheatDeath));
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

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack hand = event.getItemStack();
        if (event.getHand() == EnumHand.OFF_HAND) {
            hand = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
        }
        if (hand.isEmpty()) return;
        if (hand.getItem() instanceof ISpecialInteractItem) {
            ISpecialInteractItem i = (ISpecialInteractItem) hand.getItem();
            if (i.needsSpecialHandling(event.getWorld(), event.getPos(), event.getEntityPlayer(), hand)) {
                i.onRightClick(event.getWorld(), event.getPos(), event.getEntityPlayer(), event.getFace(), event.getHand(), hand);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRightClickLast(PlayerInteractEvent.RightClickBlock event) {
        if(!event.getWorld().isRemote) {
            IBlockState interacted = event.getWorld().getBlockState(event.getPos());
            if(interacted.getBlock() instanceof BlockWorkbench) {
                PktCraftingTableFix fix = new PktCraftingTableFix(event.getPos());
                PacketChannel.CHANNEL.sendTo(fix, (EntityPlayerMP) event.getEntityPlayer());
            }
        }
    }

    @SubscribeEvent
    public void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (event.player.getServer() != null) {
            ResearchManager.informCraftingGridCompletion(event.player, event.crafting);

            Item crafted = event.crafting.getItem();
            Block blockCrafted = Block.getBlockFromItem(crafted);
            if (blockCrafted != Blocks.AIR && blockCrafted instanceof BlockMachine) {
                if (event.crafting.getItemDamage() == BlockMachine.MachineType.TELESCOPE.getMeta()) {
                    //FIXME RE-ADD AFTER ADVANCEMENTS
                    //event.player.addStat(RegistryAchievements.achvBuildActTelescope);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (Config.giveJournalFirst) {
            EntityPlayer pl = event.player;
            if (!ResearchManager.doesPlayerFileExist(pl)) {
                pl.inventory.addItemStackToInventory(new ItemStack(ItemsAS.journal));
            }
        }
        ResearchManager.loadPlayerKnowledge(event.player);
        ResearchManager.savePlayerKnowledge(event.player);
    }

    @SubscribeEvent
    public void onSave(WorldEvent.Save event) {
        WorldCacheManager.getInstance().doSave(event.getWorld());
    }

    @SubscribeEvent
    public void onChange(BlockModifyEvent event) {
        if (event.getWorld().isRemote) return;
        if (!Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START)) {
            return; //Thanks BuildCraft.
        }
        BlockPos at = event.getPos();
        WorldNetworkHandler.getNetworkHandler(event.getWorld()).informBlockChange(at);
        if (event.getNewBlock().equals(Blocks.CRAFTING_TABLE)) {
            if (!event.getOldBlock().equals(Blocks.CRAFTING_TABLE)) {
                WorldNetworkHandler.getNetworkHandler(event.getWorld()).informTablePlacement(at);
            }
        }
        if (event.getOldBlock().equals(Blocks.CRAFTING_TABLE)) {
            if (!event.getNewBlock().equals(Blocks.CRAFTING_TABLE)) {
                WorldNetworkHandler.getNetworkHandler(event.getWorld()).informTableRemoval(at);
            }
        }
        if (event.getOldBlock().equals(BlocksAS.customOre)) {
            IBlockState oldState = event.getOldState();
            if (oldState.getValue(BlockCustomOre.ORE_TYPE).equals(BlockCustomOre.OreType.ROCK_CRYSTAL)) {
                ((RockCrystalBuffer) WorldCacheManager.getOrLoadData(event.getWorld(), WorldCacheManager.SaveKey.ROCK_CRYSTAL)).removeOre(event.getPos());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlace(BlockEvent.PlaceEvent event) {
        if (event.getWorld().isRemote) return;
        BlockPos at = event.getPos();

        if (event.getPlacedBlock().getBlock().equals(Blocks.CRAFTING_TABLE)) {
            WorldNetworkHandler.getNetworkHandler(event.getWorld()).informTablePlacement(at);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote) return;
        BlockPos at = event.getPos();

        if (event.getState().getBlock().equals(Blocks.CRAFTING_TABLE)) {
            WorldNetworkHandler.getNetworkHandler(event.getWorld()).informTableRemoval(at);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        if(event.getHarvester() != null && !event.isSilkTouching()) {
            ItemStack main = event.getHarvester().getHeldItemMainhand();
            if(!main.isEmpty()) {
                if(EnchantmentHelper.getEnchantmentLevel(EnchantmentsAS.enchantmentScorchingHeat, main) > 0) {
                    int fortuneLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, main);
                    List<ItemStack> dropsCopy = new LinkedList<>();
                    dropsCopy.addAll(event.getDrops());
                    event.getDrops().clear();
                    for (ItemStack stack : dropsCopy) {
                        ItemStack out = FurnaceRecipes.instance().getSmeltingResult(stack);
                        if(!out.isEmpty()) {
                            ItemStack furnaced = ItemUtils.copyStackWithSize(out, 1);
                            event.getDrops().add(furnaced);
                            furnaced.onCrafting(event.getWorld(), event.getHarvester(), 1);
                            FMLCommonHandler.instance().firePlayerSmeltedEvent(event.getHarvester(), furnaced);
                            if(fortuneLvl > 0 && !(out.getItem() instanceof ItemBlock)) {
                                for (int i = 0; i < fortuneLvl; i++) {
                                    if(rand.nextFloat() < 0.5F) {
                                        event.getDrops().add(ItemUtils.copyStackWithSize(out, 1));
                                    }
                                }
                            }
                        } else {
                            event.getDrops().add(stack);
                        }
                    }
                }
            }
        }
    }

    /*@SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) return;

        Entity joined = event.getEntity();
        if (joined instanceof EntityItem && !(joined instanceof EntityItemHighlighted)) {
            EntityItem ei = (EntityItem) joined;
            if (ei.getEntityItem() != null && (ei.getEntityItem().getAttItem() instanceof ItemHighlighted)) {
                ei.setDead();
                EntityItemHighlighted newItem = new EntityItemHighlighted(ei.world, ei.posX, ei.posY, ei.posZ, ei.getEntityItem());
                ItemHighlighted i = (ItemHighlighted) ei.getEntityItem().getAttItem();
                newItem.applyColor(i.getHightlightColor(ei.getEntityItem()));
                newItem.motionX = ei.motionX;
                newItem.motionY = ei.motionY;
                newItem.motionZ = ei.motionZ;
                newItem.hoverStart = ei.hoverStart;
                newItem.setPickupDelay(40);

                event.getWorld().spawnEntityInWorld(newItem);
                event.setCanceled(true);
            }
        }
    }*/

}
