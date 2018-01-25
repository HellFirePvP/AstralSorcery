/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MappingMigrationHandler
 * Created by HellFirePvP
 * Date: 03.07.2017 / 12:50
 */
public class MappingMigrationHandler {

    //this is not generified/abstracted yet due to lazyness and it's just 1 thing to migrate so........

    private static final ResourceLocation ILLUMINATION_POWDER_KEY = new ResourceLocation(AstralSorcery.MODID, "itemilluminationpowder");

    @SubscribeEvent
    public void onMissingMapping(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings()) {
            if(mapping.key.equals(ILLUMINATION_POWDER_KEY)) {
                mapping.remap(ItemsAS.useableDust);
            }
        }
    }

}
