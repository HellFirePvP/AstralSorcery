/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.base;

import hellfirepvp.astralsorcery.common.util.data.MenuTypeRegistryObject;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ContainerProviderCustom
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ContainerProviderCustom<T extends AbstractContainerMenu> implements MenuProvider {

    private final MenuTypeRegistryObject<T> menuType;

    protected ContainerProviderCustom(MenuTypeRegistryObject<T> menuType) {
        this.menuType = menuType;
    }

    @Override
    public Component getDisplayName() {
        return this.menuType.getDisplayName();
    }

    @Override
    public abstract void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer);

    @Nullable
    @Override
    public abstract T createMenu(int containerId, Inventory playerInventory, Player player);

    public void open(ServerPlayer sPlayer) {
        sPlayer.openMenu(this);
    }
}
