package hellfirepvp.astralsorcery.common.integration;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.impl.commands.CTCommandCollectionEvent;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.IEventBus;

public class IntegrationCraftTweaker {

    public static void attachListeners(IEventBus eventBus) {
        eventBus.addListener(IntegrationCraftTweaker::onCommandCollection);
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
            for (AltarType value : AltarType.values()) {
                CraftTweakerAPI.logDump(value.name());
            }
            final StringTextComponent message = new StringTextComponent(TextFormatting.GREEN + "Altar Types written to the log" + TextFormatting.RESET);
            commandContext.getSource().sendFeedback(message, true);
            return 0;
        });
    }
}
