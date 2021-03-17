/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.impl.commands.CTCommandCollectionEvent;
import com.blamejared.crafttweaker.impl.commands.script_examples.ExampleCollectionEvent;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.util.text.*;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IntegrationCraftTweaker
 * Created by Jaredlll08
 * Date: 03.17.2021 / 15:36
 */
public class IntegrationCraftTweaker {
    
    public static void attachListeners(IEventBus eventBus) {
        eventBus.addListener(IntegrationCraftTweaker::onCommandCollection);
        eventBus.addListener(IntegrationCraftTweaker::onExampleCollection);
    }
    
    public static void onExampleCollection(ExampleCollectionEvent event) {
        event.addResource(AstralSorcery.key(AstralSorcery.MODID + "/altar"));
        event.addResource(AstralSorcery.key(AstralSorcery.MODID + "/block_transmutation"));
        event.addResource(AstralSorcery.key(AstralSorcery.MODID + "/infusion"));
        event.addResource(AstralSorcery.key(AstralSorcery.MODID + "/liquid_interaction"));
        event.addResource(AstralSorcery.key(AstralSorcery.MODID + "/well"));
    }
    
    public static void onCommandCollection(CTCommandCollectionEvent event) {
        
        event.registerDump("astralConstellations", "Lists the different Astral Sorcery Constellations", commandContext -> {
            
            CraftTweakerAPI.logDump("List of all known Astral Sorcery Constellations: ");
            RegistriesAS.REGISTRY_CONSTELLATIONS.getKeys().forEach(resourceLocation -> {
                IConstellation constellation = RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(resourceLocation);
                CraftTweakerAPI.logDump("%s\tis weak: %s, is major: %s", resourceLocation.toString(), constellation instanceof IWeakConstellation, constellation instanceof IMajorConstellation);
            });
            
            final StringTextComponent message = new StringTextComponent(TextFormatting.GREEN + "Constellations written to the log" + TextFormatting.RESET);
            commandContext.getSource().sendFeedback(message, true);
            return 0;
        });
        event.registerDump("astralAltarTypes", "Lists the different Astral Sorcery Altar Types", commandContext -> {
            
            CraftTweakerAPI.logDump("List of all known Astral Sorcery Altar Types: ");
            for(AltarType value : AltarType.values()) {
                CraftTweakerAPI.logDump(value.name());
            }
            final StringTextComponent message = new StringTextComponent(TextFormatting.GREEN + "Altar Types written to the log" + TextFormatting.RESET);
            commandContext.getSource().sendFeedback(message, true);
            return 0;
        });
    }
}
