/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.common.base.RockCrystalHandler;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockMachine;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.BlockModifyEvent;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.item.knowledge.ItemFragmentCapsule;
import hellfirepvp.astralsorcery.common.item.knowledge.ItemKnowledgeFragment;
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
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.world.util.WorldEventNotifier;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Iterator;
import java.util.List;
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


    @SubscribeEvent
    public void attachPlague(AttachCapabilitiesEvent<Entity> event) {
        //if(event.getObject() instanceof EntityLivingBase) {
        //    event.addCapability(SpellPlague.CAPABILITY_NAME, new SpellPlague.Provider());
        //}
    }

    /*@SubscribeEvent
    public void onHarvestSpeedCheck(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event) {
        EntityPlayer harvester = event.getEntityPlayer();
        if (harvester != null) {
            PlayerProgress prog = ResearchManager.getProgress(harvester, harvester.getEntityWorld().isRemote ? Side.CLIENT : Side.SERVER);
            if (prog != null) {
                Map<ConstellationPerk, Integer> perks = prog.getAppliedPerks();
                for (ConstellationPerk perk : perks.keySet()) {
                    if (!prog.isPerkActive(perk)) continue;
                    if (perk.mayExecute(ConstellationPerk.Target.PLAYER_HARVEST_SPEED)) {
                        BlockPos p = event.getLocationPos();
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
    }*/

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent event) {
        EntityItem ei = event.getItem();
        if (ei.getItem().getItem() instanceof ItemFragmentCapsule ||
                ei.getItem().getItem() instanceof ItemKnowledgeFragment) {
            EntityPlayer pickingUp = event.getEntityPlayer();
            if (!pickingUp.getEntityWorld().isRemote) {
                String playerName = ei.getOwner();
                if (playerName == null) {
                    playerName = ei.getThrower();
                }
                if (playerName != null && !playerName.equals(pickingUp.getName())) {
                    event.setCanceled(true);
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
        entity.setHealth(Math.min(entity.getMaxHealth(), 6 + level * 2));
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
        if (hand.isEmpty()) return;
        if (hand.getItem() instanceof ISpecialInteractItem) {
            ISpecialInteractItem i = (ISpecialInteractItem) hand.getItem();
            if (i.needsSpecialHandling(event.getWorld(), event.getPos(), event.getEntityPlayer(), hand)) {
                if (i.onRightClick(event.getWorld(), event.getPos(), event.getEntityPlayer(), event.getFace(), event.getHand(), hand)) {
                    event.setCanceled(true);
                    event.setCancellationResult(EnumActionResult.SUCCESS);
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
        }
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.player instanceof EntityPlayerMP) {
            ResearchManager.loadPlayerKnowledge((EntityPlayerMP) event.player);
            ResearchManager.savePlayerKnowledge((EntityPlayerMP) event.player);
        }
        if (Config.giveJournalFirst) {
            EntityPlayer pl = event.player;
            if (!ResearchManager.getProgress(pl).didReceiveTome() &&
                    pl.inventory.addItemStackToInventory(new ItemStack(ItemsAS.journal))) {
                ResearchManager.setTomeReceived(pl);
            }
        }
    }

    @SubscribeEvent
    public void onLoad(WorldEvent.Load event) {
        event.getWorld().addEventListener(new WorldEventNotifier());

        GameRules rules = event.getWorld().getGameRules();
        if (!rules.hasRule(MiscUtils.GAMERULE_SKIP_SKYLIGHT_CHECK)) {
            rules.addGameRule(MiscUtils.GAMERULE_SKIP_SKYLIGHT_CHECK, "false", GameRules.ValueType.BOOLEAN_VALUE);
        }
    }

    @SubscribeEvent
    public void onChange(BlockModifyEvent event) {
        if (event.getWorld().isRemote ||
                !event.getChunk().isTerrainPopulated()) return;
        if (!Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START)) {
            return; //Thanks BuildCraft.
        }
        BlockPos at = event.getPos();
        WorldNetworkHandler.getNetworkHandler(event.getWorld()).informBlockChange(at);
        if (event.getNewBlock().equals(Blocks.CRAFTING_TABLE)) {
            if (!event.getOldBlock().equals(Blocks.CRAFTING_TABLE)) {
                WorldNetworkHandler.getNetworkHandler(event.getWorld()).attemptAutoLinkTo(at);
            }
        }
        if (event.getOldBlock().equals(Blocks.CRAFTING_TABLE)) {
            if (!event.getNewBlock().equals(Blocks.CRAFTING_TABLE)) {
                WorldNetworkHandler.getNetworkHandler(event.getWorld()).removeAutoLinkTo(at);
            }
        }
        if (event.getNewBlock().equals(BlocksAS.blockAltar)) {
            if (!event.getOldBlock().equals(BlocksAS.blockAltar)) {
                WorldNetworkHandler.getNetworkHandler(event.getWorld()).attemptAutoLinkTo(at);
            }
        }
        if (event.getOldBlock().equals(BlocksAS.blockAltar)) {
            if (!event.getNewBlock().equals(BlocksAS.blockAltar)) {
                WorldNetworkHandler.getNetworkHandler(event.getWorld()).removeAutoLinkTo(at);
            }
        }
        if (event.getOldBlock().equals(BlocksAS.customOre)) {
            IBlockState oldState = event.getOldState();
            if (oldState.getValue(BlockCustomOre.ORE_TYPE).equals(BlockCustomOre.OreType.ROCK_CRYSTAL)) {
                RockCrystalHandler.INSTANCE.removeOre(event.getWorld(), event.getPos(), true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote) return;
        BlockPos at = event.getPos();

        Tuple<EnumHand, ItemStack> heldStack =
                MiscUtils.getMainOrOffHand(event.getPlayer(), ItemsAS.wand,stack -> ItemWand.getAugment(stack) != null);
        if(heldStack != null && ItemWand.getAugment(heldStack.value) == WandAugment.EVORSIO) {
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
                    if (w.setBlockState(pos, BlocksAS.blockFakeTree.getDefaultState())) {
                        TileFakeTree tt = MiscUtils.getTileAt(w, pos, TileFakeTree.class, true);
                        if(tt != null) {
                            tt.setupTile(event.getPlayer(), event.getPlayer().getHeldItemMainhand(), atState);
                        } else {
                            w.setBlockState(pos, atState);
                        }
                    }
                }
                if (foundBlocks.getPattern().containsKey(at)) {
                    event.setCanceled(true);
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
                    NonNullList<ItemStack> newStacks = NonNullList.create();
                    Iterator<ItemStack> iterator = event.getDrops().iterator();
                    while (iterator.hasNext()) {
                        ItemStack stack = iterator.next();
                        ItemStack out = FurnaceRecipes.instance().getSmeltingResult(stack);
                        if (!out.isEmpty()) {
                            ItemStack furnaced = ItemUtils.copyStackWithSize(out, 1);
                            iterator.remove();
                            newStacks.add(furnaced);
                            furnaced.onCrafting(event.getWorld(), event.getHarvester(), 1);
                            FMLCommonHandler.instance().firePlayerSmeltedEvent(event.getHarvester(), furnaced);
                            if (fortuneLvl > 0 && !(out.getItem() instanceof ItemBlock)) {
                                for (int i = 0; i < fortuneLvl; i++) {
                                    if (rand.nextFloat() < 0.5F) {
                                        newStacks.add(ItemUtils.copyStackWithSize(out, 1));
                                    }
                                }
                            }
                        }
                    }
                    event.getDrops().addAll(newStacks);
                }
            }
        }
    }

}
