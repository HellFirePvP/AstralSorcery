/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.container.*;
import hellfirepvp.astralsorcery.common.container.provider.ContainerAltarProvider;
import hellfirepvp.astralsorcery.common.container.provider.ContainerTomePapersProvider;
import hellfirepvp.astralsorcery.common.util.data.MenuTypeRegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MenuTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class MenuTypesAS {

    public static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTER =
            DeferredRegister.create(Registries.MENU, AstralSorcery.MODID);

    public static final MenuTypeRegistryObject<ContainerTomePapers> TOME_PAPERS =
            register("tome_papers", ContainerTomePapersProvider::createClient);

    public static final MenuTypeRegistryObject<ContainerAltarIllumination> ALTAR_ILLUMINATION =
            register("altar_illumination", ContainerAltarProvider::createClient);
    public static final MenuTypeRegistryObject<ContainerAltarResonance> ALTAR_RESONANCE =
            register("altar_resonance", ContainerAltarProvider::createClient);
    public static final MenuTypeRegistryObject<ContainerAltarLuminance> ALTAR_LUMINANCE =
            register("altar_luminance", ContainerAltarProvider::createClient);
    public static final MenuTypeRegistryObject<ContainerAltarRadiance> ALTAR_RADIANCE =
            register("altar_radiance", ContainerAltarProvider::createClient);

    private static <T extends AbstractContainerMenu> MenuTypeRegistryObject<T> register(String name,
                                                                                        IContainerFactory<T> clientFactory) {
        var holder = MENU_TYPE_REGISTER.register(name, () -> new MenuType<>(clientFactory, FeatureFlags.VANILLA_SET));
        return new MenuTypeRegistryObject<>(holder);
    }
}
