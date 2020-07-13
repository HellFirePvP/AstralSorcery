/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.screen.ScreenObservatory;
import hellfirepvp.astralsorcery.client.screen.container.*;
import hellfirepvp.astralsorcery.common.container.ContainerObservatory;
import hellfirepvp.astralsorcery.common.container.factory.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.IContainerFactory;

import static hellfirepvp.astralsorcery.common.lib.ContainerTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryContainerTypes
 * Created by HellFirePvP
 * Date: 09.08.2019 / 21:15
 */
public class RegistryContainerTypes {

    private RegistryContainerTypes() {}

    public static void init() {
        TOME = register("tome", new ContainerTomeProvider.Factory());
        OBSERVATORY = register("observatory", new ContainerObservatoryProvider.Factory());

        ALTAR_DISCOVERY = register("altar_discovery", new ContainerAltarDiscoveryProvider.Factory());
        ALTAR_ATTUNEMENT = register("altar_attunement", new ContainerAltarAttunementProvider.Factory());
        ALTAR_CONSTELLATION = register("altar_constellation", new ContainerAltarConstellationProvider.Factory());
        ALTAR_RADIANCE = register("altar_radiance", new ContainerAltarRadianceProvider.Factory());
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        ScreenManager.registerFactory(TOME, ScreenContainerTome::new);
        ScreenManager.registerFactory(OBSERVATORY, new ScreenManager.IScreenFactory<ContainerObservatory, ScreenObservatory>() {
            @Override
            public ScreenObservatory create(ContainerObservatory observatory, PlayerInventory playerInventory, ITextComponent name) {
                return new ScreenObservatory(observatory);
            }
        });
        ScreenManager.registerFactory(ALTAR_DISCOVERY, ScreenContainerAltarDiscovery::new);
        ScreenManager.registerFactory(ALTAR_ATTUNEMENT, ScreenContainerAltarAttunement::new);
        ScreenManager.registerFactory(ALTAR_CONSTELLATION, ScreenContainerAltarConstellation::new);
        ScreenManager.registerFactory(ALTAR_RADIANCE, ScreenContainerAltarRadiance::new);
    }

    private static <C extends Container, T extends ContainerType<C>> T register(String name, IContainerFactory<C> containerFactory) {
        return register(AstralSorcery.key(name), containerFactory);
    }

    private static <C extends Container, T extends ContainerType<C>> T register(ResourceLocation name, IContainerFactory<C> containerFactory) {
        ContainerType<C> type = new ContainerType<>(containerFactory);
        type.setRegistryName(name);
        AstralSorcery.getProxy().getRegistryPrimer().register(type);
        return (T) type;
    }
}
