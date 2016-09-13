package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.item.ItemEntityPlacer;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryAchievements;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerAchievements
 * Created by HellFirePvP
 * Date: 13.09.2016 / 20:19
 */
public class EventHandlerAchievements {

    @SubscribeEvent
    public void onCraft(PlayerEvent.ItemCraftedEvent event) {
        ItemStack crafted = event.crafting;
        if(crafted != null && crafted.getItem() != null) {
            Item i = crafted.getItem();
            if(i instanceof ItemEntityPlacer && crafted.getItemDamage() == ItemEntityPlacer.PlacerType.TELESCOPE.getMeta()) {
                event.player.addStat(RegistryAchievements.achvBuildTelescope);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreak(BlockEvent.BreakEvent event) {
        if(event.isCanceled() || event.getState() == null)
            return;

        Block broken = event.getState().getBlock();
        if (broken != null && broken.equals(BlocksAS.customOre)) {
            BlockCustomOre.OreType ot = event.getState().getValue(BlockCustomOre.ORE_TYPE);
            if(ot != null && ot.equals(BlockCustomOre.OreType.ROCK_CRYSTAL)) {
                event.getPlayer().addStat(RegistryAchievements.achvRockCrystal);
            }
        }
    }

}
