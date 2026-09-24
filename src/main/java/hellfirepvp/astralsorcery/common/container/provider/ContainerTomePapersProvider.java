/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.provider;

import hellfirepvp.astralsorcery.common.container.ContainerTomePapers;
import hellfirepvp.astralsorcery.common.container.base.ContainerProviderCustom;
import hellfirepvp.astralsorcery.common.lib.MenuTypesAS;
import hellfirepvp.astralsorcery.common.util.data.MenuTypeRegistryObject;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ContainerTomePapersProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ContainerTomePapersProvider extends ContainerProviderCustom<ContainerTomePapers> {

    private final int tomeSlotId;

    protected ContainerTomePapersProvider(int tomeSlotId) {
        super(MenuTypesAS.TOME_PAPERS);
        this.tomeSlotId = tomeSlotId;
    }

    public static ContainerTomePapersProvider openTome(int tomeSlotId) {
        return new ContainerTomePapersProvider(tomeSlotId);
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.tomeSlotId);
    }

    @Nullable
    @Override
    public ContainerTomePapers createMenu(int containerId, Inventory playerInventory, Player player) {
        return createServer(containerId, playerInventory, this.tomeSlotId);
    }

    public static ContainerTomePapers createServer(int containerId, Inventory playerInventory, int tomeSlotId) {
        return new ContainerTomePapers(MenuTypesAS.TOME_PAPERS.type(), playerInventory, tomeSlotId, containerId);
    }

    public static ContainerTomePapers createClient(int windowId, Inventory inv, RegistryFriendlyByteBuf data) {
        return new ContainerTomePapers(MenuTypesAS.TOME_PAPERS.type(), inv, data.readInt(), windowId);
    }
}
