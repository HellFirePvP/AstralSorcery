package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.event.BlockModifyEvent;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.world.WorldProviderBrightnessInj;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerServer
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:09
 */
public class EventHandlerServer {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLoad(WorldEvent.Load event) {
        World w = event.getWorld();
        if(w.provider.getDimension() == 0) {
            AstralSorcery.log.info("[AstralSorcery] Found worldProvider in Dimension 0: " + w.provider.getClass().getName());
            if(Config.overwriteWorldProviderAggressively) {
                WorldProviderBrightnessInj inj = new WorldProviderBrightnessInj();
                inj.registerWorld(w);
                inj.setDimension(0);
                w.provider = inj;
                AstralSorcery.log.info("[AstralSorcery] Injected WorldProvider into dimension 0 (aggressively, overriding old providers.)");
            } else {
                if(!(w.provider.getClass().equals(WorldProviderSurface.class)) && !(w.provider instanceof WorldProviderBrightnessInj)) {
                    FMLLog.bigWarning("[AstralSorcery] Could not overwrite WorldProvider for dimension 0 - expect visual issues. - Current provider class found: " + w.provider.getClass().getName());
                } else {
                    WorldProviderBrightnessInj inj = new WorldProviderBrightnessInj();
                    inj.registerWorld(w);
                    inj.setDimension(0);
                    w.provider = inj;
                    AstralSorcery.log.info("[AstralSorcery] Injected WorldProvider into dimension 0");
                }
            }
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack hand = event.getItemStack();
        if(event.getHand() == EnumHand.OFF_HAND) {
            hand = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
        }
        if(hand == null || hand.getItem() == null) return;
        if(hand.getItem() instanceof ISpecialInteractItem) {
            ISpecialInteractItem i = (ISpecialInteractItem) hand.getItem();
            if(i.needsSpecialHandling(event.getWorld(), event.getPos(), event.getEntityPlayer(), hand)) {
                i.onRightClick(event.getWorld(), event.getPos(), event.getEntityPlayer(), event.getFace(), event.getHand(), hand);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if(event.player.getServer() != null) {
            ResearchManager.informCraftingGridCompletion(event.player, event.crafting);
        }
    }

    @SubscribeEvent
    public void onFirst(PlayerEvent.PlayerLoggedInEvent event) {
        if(Config.giveJournalFirst) {
            EntityPlayer pl = event.player;
            NBTTagCompound cmp = NBTHelper.getPersistentData(pl);
            if(!cmp.hasKey("joined") || !cmp.getBoolean("joined")) {
                cmp.setBoolean("joined", true);
                pl.inventory.addItemStackToInventory(new ItemStack(ItemsAS.journal));
            }
        }
    }

    @SubscribeEvent
    public void onSave(WorldEvent.Save event) {
        WorldCacheManager.getInstance().doSave(event.getWorld());
    }

    @SubscribeEvent
    public void onChange(BlockModifyEvent event) {
        if(event.getWorld().isRemote) return;
        BlockPos at = event.getPos();
        WorldNetworkHandler.getNetworkHandler(event.getWorld()).informBlockChange(at);
        if(event.getNewBlock().equals(Blocks.CRAFTING_TABLE)) {
            if(!event.getOldBlock().equals(Blocks.CRAFTING_TABLE)) {
                WorldNetworkHandler.getNetworkHandler(event.getWorld()).informTablePlacement(at);
            }
        }
        if(event.getOldBlock().equals(Blocks.CRAFTING_TABLE)) {
            if(!event.getNewBlock().equals(Blocks.CRAFTING_TABLE)) {
                WorldNetworkHandler.getNetworkHandler(event.getWorld()).informTableRemoval(at);
            }
        }
        if(event.getOldBlock().equals(BlocksAS.customOre)) {
            IBlockState oldState = event.getOldState();
            if(oldState.getValue(BlockCustomOre.ORE_TYPE).equals(BlockCustomOre.OreType.ROCK_CRYSTAL)) {
                ((RockCrystalBuffer) WorldCacheManager.getOrLoadData(event.getWorld(), WorldCacheManager.SaveKey.ROCK_CRYSTAL)).removeOre(event.getPos());
            }
        }
    }

    /*@SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) return;

        Entity joined = event.getEntity();
        if (joined instanceof EntityItem && !(joined instanceof EntityItemHighlighted)) {
            EntityItem ei = (EntityItem) joined;
            if (ei.getEntityItem() != null && (ei.getEntityItem().getItem() instanceof ItemHighlighted)) {
                ei.setDead();
                EntityItemHighlighted newItem = new EntityItemHighlighted(ei.worldObj, ei.posX, ei.posY, ei.posZ, ei.getEntityItem());
                ItemHighlighted i = (ItemHighlighted) ei.getEntityItem().getItem();
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
