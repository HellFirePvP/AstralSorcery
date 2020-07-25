package hellfirepvp.astralsorcery.common.integration;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IntegrationJEI
 * Created by HellFirePvP
 * Date: 25.07.2020 / 09:23
 */
@JeiPlugin
public class IntegrationJEI implements IModPlugin {

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(
                ItemsAS.ATTUNED_ROCK_CRYSTAL,
                ItemsAS.ATTUNED_CELESTIAL_CRYSTAL,
                BlocksAS.ROCK_COLLECTOR_CRYSTAL.asItem(),
                BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL.asItem(),
                ItemsAS.MANTLE,
                ItemsAS.RESONATOR
        );
    }

    @Override
    public ResourceLocation getPluginUid() {
        return AstralSorcery.key("jei_integration");
    }
}
