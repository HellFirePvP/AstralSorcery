package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.entities.EntityItemHighlighted;
import hellfirepvp.astralsorcery.common.event.BlockModifyEvent;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.world.WorldProviderBrightnessInj;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerServer
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:09
 */
public class EventHandlerServer {

    @SubscribeEvent
    public void onLoad(WorldEvent.Load event) {
        World w = event.getWorld();
        if(w.provider.getDimension() == 0) {
            if(!(w.provider.getClass().equals(WorldProviderSurface.class)) && !(w.provider instanceof WorldProviderBrightnessInj)) {
                FMLLog.bigWarning("AstralSorcery: Could not overwrite WorldProvider for dimension 0 - expect issues.");
            } else {
                WorldProviderBrightnessInj inj = new WorldProviderBrightnessInj();
                inj.registerWorld(w);
                inj.setDimension(0);
                w.provider = inj;
                AstralSorcery.log.info("Injected WorldProvider into dimension 0");
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
