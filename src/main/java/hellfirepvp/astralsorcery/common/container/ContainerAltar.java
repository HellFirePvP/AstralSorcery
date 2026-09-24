/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.common.component.AttunedConstellationComponent;
import hellfirepvp.astralsorcery.common.container.base.ContainerTileEntity;
import hellfirepvp.astralsorcery.common.container.base.PlayerInventorySlots;
import hellfirepvp.astralsorcery.common.container.provider.ContainerAltarProvider;
import hellfirepvp.astralsorcery.common.container.slot.CustomItemSlot;
import hellfirepvp.astralsorcery.common.container.slot.TileDisplaySlot;
import hellfirepvp.astralsorcery.common.container.transfer.DefaultQuickMoveTransfer;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileFocusRelay;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.IntPoint;
import hellfirepvp.astralsorcery.common.util.data.MenuTypeRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ContainerAltar
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ContainerAltar extends ContainerTileEntity<TileAltar> implements PlayerInventorySlots, DefaultQuickMoveTransfer {

    private Slot focusSlot = null;

    protected ContainerAltar(@Nullable MenuType<?> menuType, TileAltar tile, Inventory playerInv, int containerId) {
        super(menuType, tile, playerInv, containerId);
    }

    protected void addAltarGridSlots() {
        IntPoint offset = this.getAltarGridOffset();
        IItemHandler inv = this.getTile().getTileData().getAltarInventory();
        for (int yy = 0; yy < 3; yy++) {
            for (int xx = 0; xx < 3; xx++) {
                int index = xx + yy * 3;
                this.addSlot(new SlotItemHandler(inv, index, offset.x() + xx * 18, offset.y() + yy * 18));
            }
        }
    }

    protected void addInnerAltarRelaySlots(Level level, BlockPos pos) {
        IntPoint innerOffset = this.getAltarGridOffset().add(-23, -23);

        Map<Integer, BlockPos> relayOffsets = TileAltar.getRelayGridOffsets();
        for (int xx = 0; xx < 3; xx++) {
            for (int yy = 0; yy < 3; yy++) {
                int realSlot = (xx + 1) + (yy + 1) * 5;

                BlockPos relayOffset = relayOffsets.get(realSlot);
                if (relayOffset == null) continue;

                int offsetX = innerOffset.x() + xx * 41;
                int offsetY = innerOffset.y() + yy * 41;
                this.addSlot(new TileDisplaySlot(offsetX, offsetY, () -> level, pos.offset(relayOffset), TileFocusRelay.class) {
                    @Override
                    public boolean isHighlightable() {
                        return false;
                    }
                });
            }
        }
    }

    protected void addOuterAltarRelaySlots(Level level, BlockPos pos) {
        IntPoint outerOffset = this.getAltarGridOffset().add(-43, -43);

        Map<Integer, BlockPos> relayOffsets = TileAltar.getRelayGridOffsets();
        TileAltar.getOuterRelaySlots().forEach(slot -> {
            BlockPos relayOffset = relayOffsets.get(slot);
            if (relayOffset == null) return;

            // I'm not doing any fcking fancy math for it here. hardcoded values for UI positions it is.
            int x = slot % 5;
            int y = slot / 5;
            int offsetX = switch (x) {
                case 1 -> 20;
                case 2 -> 61;
                case 3 -> 102;
                case 4 -> 122;
                default -> 0;
            } + outerOffset.x();
            int offsetY = switch (y) {
                case 1 -> 20;
                case 2 -> 61;
                case 3 -> 102;
                case 4 -> 122;
                default -> 0;
            } + outerOffset.y();

            this.addSlot(new TileDisplaySlot(offsetX, offsetY, () -> level, pos.offset(relayOffset), TileFocusRelay.class) {
                @Override
                public boolean isHighlightable() {
                    return false;
                }
            });
        });
    }

    protected void addConstellationFocusSlot(TileAltar altar, int x, int y) {
        this.addSlot(this.focusSlot = new CustomItemSlot(x, y, () -> altar.getTileData().getFocusItem(), stack -> {
            altar.getTileData().setFocusItem(stack);
            altar.getTileData().markForUpdate();
        },
                toInsert -> altar.getTileData().canSetFocusItem(toInsert),
                toTake -> true));
    }

    protected abstract IntPoint getAltarGridOffset();

    protected abstract IntPoint getPlayerInventoryOffset();

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return this.defaultQuickMoveStack(player, index);
    }

    @Override
    public Optional<ItemStack> customTransfer(ItemStack toMove, int transferIndex) {
        if (!toMove.isEmpty() && transferIndex >= 0 && transferIndex < 36) {
            if (this.focusSlot != null) {
                if (this.defaultMoveItemStackTo(toMove, this.focusSlot.index, this.focusSlot.index + 1, false)) {
                    return Optional.of(ItemStack.EMPTY);
                }
            }
        }
        return DefaultQuickMoveTransfer.super.customTransfer(toMove, transferIndex);
    }

    @Override
    public AbstractContainerMenu self() {
        return this;
    }

    public void writeClientData(RegistryFriendlyByteBuf buf) {}

    public static class Type {

        private final MenuTypeRegistryObject<? extends ContainerAltar> menuType;
        private final Provider containerProvider;
        private final ClientProvider clientContainerProvider;

        public Type(MenuTypeRegistryObject<? extends ContainerAltar> menuType, Provider containerProvider, ClientProvider clientContainerProvider) {
            this.menuType = menuType;
            this.containerProvider = containerProvider;
            this.clientContainerProvider = clientContainerProvider;
        }

        public MenuTypeRegistryObject<? extends ContainerAltar> getMenuType() {
            return this.menuType;
        }

        public ContainerAltar provideServerContainer(int containerId, Inventory playerInv, TileAltar tile) {
            return this.containerProvider.createContainer(this.menuType.type(), tile, playerInv, containerId);
        }

        public ContainerAltar provideClientContainer(TileAltar tile, Inventory playerInv, int containerId, RegistryFriendlyByteBuf byteBuf) {
            return this.clientContainerProvider.createContainer(this.menuType.type(), tile, playerInv, containerId, byteBuf);
        }
    }

    public interface Provider {

        ContainerAltar createContainer(MenuType<? extends ContainerAltar> menuType, TileAltar tile, Inventory playerInv, int containerId);

    }

    public interface ClientProvider {

        ContainerAltar createContainer(MenuType<? extends ContainerAltar> menuType, TileAltar tile, Inventory playerInv, int containerId, RegistryFriendlyByteBuf byteBuf);

    }
}
