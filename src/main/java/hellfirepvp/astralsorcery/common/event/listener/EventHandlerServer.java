/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockMachine;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.event.BlockModifyEvent;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.item.tool.wand.ItemWand;
import hellfirepvp.astralsorcery.common.item.tool.wand.WandAugment;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.EnchantmentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktCraftingTableFix;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.tile.TileFakeTree;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.*;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerServer
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:09
 */
public class EventHandlerServer {

    private static final Random rand = new Random();

    public static TimeoutListContainer<PlayerWrapperContainer, Integer> perkCooldowns = new TimeoutListContainer<>(new ConstellationPerks.PerkTimeoutHandler(), TickEvent.Type.SERVER);
    public static TimeoutListContainer<PlayerWrapperContainer, Integer> perkCooldownsClient = new TimeoutListContainer<>(new ConstellationPerks.PerkTimeoutHandler(), TickEvent.Type.CLIENT);

    @SubscribeEvent
    public void attachPlague(AttachCapabilitiesEvent<Entity> event) {
        //if(event.getObject() instanceof EntityLivingBase) {
        //    event.addCapability(SpellPlague.CAPABILITY_NAME, new SpellPlague.Provider());
        //}
    }

    @SubscribeEvent
    public void onHarvestSpeedCheck(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event) {
        EntityPlayer harvester = event.getEntityPlayer();
        if (harvester != null) {
            PlayerProgress prog = ResearchManager.getProgress(harvester, harvester.getEntityWorld().isRemote ? Side.CLIENT : Side.SERVER);
            if (prog != null) {
                Map<ConstellationPerk, Integer> perks = prog.getAppliedPerks();
                for (ConstellationPerk perk : perks.keySet()) {
                    if (!prog.isPerkActive(perk)) continue;
                    if (perk.mayExecute(ConstellationPerk.Target.PLAYER_HARVEST_SPEED)) {
                        BlockPos p = event.getPos();
                        event.setNewSpeed(perk.onHarvestSpeed(harvester, event.getState(), (p == null || p.getY() < 0) ? null : p, event.getNewSpeed()));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onHarvestTypeCheck(net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck event) {
        EntityPlayer harvester = event.getEntityPlayer();
        if (harvester != null) {
            PlayerProgress prog = ResearchManager.getProgress(harvester, harvester.getEntityWorld().isRemote ? Side.CLIENT : Side.SERVER);
            if (prog != null) {
                Map<ConstellationPerk, Integer> perks = prog.getAppliedPerks();
                for (ConstellationPerk perk : perks.keySet()) {
                    if (!prog.isPerkActive(perk)) continue;
                    if (perk.mayExecute(ConstellationPerk.Target.PLAYER_HARVEST_TYPE)) {
                        if(perk.onCanHarvest(harvester, harvester.getHeldItemMainhand(), event.getTargetBlock(), event.canHarvest())) {
                            event.setCanHarvest(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onContainerOpen(PlayerContainerEvent.Open event) {
        if(event.getContainer() instanceof ContainerWorkbench && !event.getEntityPlayer().world.isRemote && event.getEntityPlayer() instanceof EntityPlayerMP) {
            PacketChannel.CHANNEL.sendTo(new PktCraftingTableFix(((ContainerWorkbench) event.getContainer()).pos), (EntityPlayerMP) event.getEntityPlayer());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDeath(LivingDeathEvent event) {
        if(event.getSource().canHarmInCreative()) {
            return;
        }
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
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        IBlockState at = event.getWorld().getBlockState(event.getPos());
        if(at.getBlock() instanceof BlockMachine) {
            if(((BlockMachine) at.getBlock()).handleSpecificActivateEvent(event)) {
                event.setCancellationResult(EnumActionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }

        ItemStack hand = event.getItemStack();
        if (event.getHand() == EnumHand.OFF_HAND) {
            hand = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
        }
        if (hand.isEmpty()) return;
        if (hand.getItem() instanceof ISpecialInteractItem) {
            ISpecialInteractItem i = (ISpecialInteractItem) hand.getItem();
            if (i.needsSpecialHandling(event.getWorld(), event.getPos(), event.getEntityPlayer(), hand)) {
                if(i.onRightClick(event.getWorld(), event.getPos(), event.getEntityPlayer(), event.getFace(), event.getHand(), hand)) {
                    event.setCanceled(true);
                }
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
        if (event.getNewBlock().equals(BlocksAS.blockAltar)) {
            if (!event.getOldBlock().equals(BlocksAS.blockAltar)) {
                WorldNetworkHandler.getNetworkHandler(event.getWorld()).informTablePlacement(at);
            }
        }
        if (event.getOldBlock().equals(BlocksAS.blockAltar)) {
            if (!event.getNewBlock().equals(BlocksAS.blockAltar)) {
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
        IBlockState broken = event.getState();

        if (broken.getBlock().equals(Blocks.CRAFTING_TABLE)) {
            WorldNetworkHandler.getNetworkHandler(event.getWorld()).informTableRemoval(at);
        }

        ItemStack active = event.getPlayer().getHeldItemMainhand();
        WandAugment found = null;
        if(!active.isEmpty() && active.getItem() instanceof ItemWand) {
            found = ItemWand.getAugment(active);
        }
        active = event.getPlayer().getHeldItemOffhand();
        if(found == null && !active.isEmpty() && active.getItem() instanceof ItemWand) {
            found = ItemWand.getAugment(active);
        }
        if(found != null && found.equals(WandAugment.EVORSIO)) {
            if(rand.nextFloat() < Config.evorsioEffectChance) {
                World w = event.getWorld();
                IBlockState stateAt = w.getBlockState(at);
                BlockArray foundBlocks = BlockDiscoverer.searchForBlocksAround(w, at, 2,
                        ((world, pos, state) -> (
                                pos.getY() >= event.getPlayer().getPosition().getY() &&
                                        state.equals(stateAt) &&
                                        state.getBlockHardness(world, pos) >= 0 &&
                                        world.getTileEntity(pos) == null &&
                                        !world.isAirBlock(pos) &&
                                        world.getBlockState(pos).getBlock().canHarvestBlock(world, pos, event.getPlayer()))));
                for (BlockPos pos : foundBlocks.getPattern().keySet()) {
                    IBlockState atState = w.getBlockState(pos);
                    w.setBlockState(pos, BlocksAS.blockFakeTree.getDefaultState());
                    TileFakeTree tt = MiscUtils.getTileAt(w, pos, TileFakeTree.class, true);
                    if(tt != null) {
                        tt.setupTile(event.getPlayer(), event.getPlayer().getHeldItemMainhand(), atState);
                    } else {
                        w.setBlockState(pos, atState);
                    }
                }
            }
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

    public static class PlayerWrapperContainer {

        @Nonnull
        public final EntityPlayer player;

        public PlayerWrapperContainer(@Nonnull EntityPlayer player) {
            this.player = player;
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj) return true;
            if(obj == null) return player == null;
            if(player == null) return false;
            if(!(obj instanceof PlayerWrapperContainer)) return false;

            return ((PlayerWrapperContainer) obj).player.getUniqueID().equals(player.getUniqueID());
        }

        @Override
        public int hashCode() {
            return player != null ? player.getUniqueID().hashCode() : 0;
        }

    }

}
