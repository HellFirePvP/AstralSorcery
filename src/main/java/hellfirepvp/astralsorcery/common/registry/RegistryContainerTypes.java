/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.container.ContainerTome;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;

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
        TOME = register("tome", (id, player) -> new ContainerTome(id));
    }

    private static <C extends Container, T extends ContainerType<C>> T register(String name, PlayerFactory<C> containerFactory) {
        return register(new ResourceLocation(AstralSorcery.MODID, name), containerFactory);
    }

    private static <C extends Container, T extends ContainerType<C>> T register(ResourceLocation name, PlayerFactory<C> containerFactory) {
        ContainerType<C> type = new ContainerType<>(containerFactory);
        type.setRegistryName(name);
        AstralSorcery.getProxy().getRegistryPrimer().register(type);
        return (T) type;
    }

    //Rather dealing with a player than just its inventory.
    private static interface PlayerFactory<C extends Container> extends ContainerType.IFactory<C> {

        C create(int id, PlayerEntity player);

        @Override
        default C create(int id, PlayerInventory plInv) {
            return this.create(id, plInv.player);
        }
    }

}
