package hellfirepvp.astralsorcery.common.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LootTableUtil
 * Created by HellFirePvP
 * Date: 01.08.2016 / 19:29
 */
public class LootTableUtil {



    @SubscribeEvent
    public void onLootLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();

    }

}
