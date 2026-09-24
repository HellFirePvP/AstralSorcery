/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.provider;

import hellfirepvp.astralsorcery.common.container.ContainerAltar;
import hellfirepvp.astralsorcery.common.container.base.ContainerProviderCustom;
import hellfirepvp.astralsorcery.common.lib.MenuTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.MenuTypeRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ContainerAltarProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ContainerAltarProvider<T extends ContainerAltar> extends ContainerProviderCustom<T> {

    private final BlockPos pos;
    private final TileAltar.AltarType altarType;

    protected ContainerAltarProvider(BlockPos pos, TileAltar.AltarType altarType) {
        super(MiscUtil.cast(altarType.getContainerType().getMenuType()));
        this.pos = pos;
        this.altarType = altarType;
    }

    public static ContainerAltarProvider<?> openAltar(TileAltar altar) {
        return new ContainerAltarProvider<>(altar.getBlockPos(), altar.getTileData().getAltarType());
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public TileAltar.AltarType getAltarType() {
        return this.altarType;
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.getPos());
        buffer.writeEnum(this.getAltarType());
        if (menu instanceof ContainerAltar altarMenu) {
            altarMenu.writeClientData(buffer);
        }
    }

    @Nullable
    @Override
    public T createMenu(int containerId, Inventory playerInventory, Player player) {
        return createServer(containerId, playerInventory, player.level(), this.pos, this.altarType);
    }

    @Nullable
    public static <T extends ContainerAltar> T  createServer(int containerId, Inventory playerInventory, Level level, BlockPos pos, @Nullable TileAltar.AltarType altarType) {
        return MiscUtil.getTileAt(level, pos, TileAltar.class, false).map(tile -> {
            if (altarType != null && tile.getTileData().getAltarType() != altarType) return null;
            return (T) tile.getTileData().getAltarType().getContainerType().provideServerContainer(containerId, playerInventory, tile);
        }).orElse(null);
    }

    public static <T extends ContainerAltar> T createClient(int windowId, Inventory inv, RegistryFriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        TileAltar.AltarType altarType = data.readEnum(TileAltar.AltarType.class);
        return MiscUtil.getTileAt(inv.player.level(), pos, TileAltar.class, false).map(tile -> {
            if (tile.getTileData().getAltarType() != altarType) return null;
            return (T) tile.getTileData().getAltarType().getContainerType().provideClientContainer(tile, inv, windowId, data);
        }).orElseThrow();
    }
}
