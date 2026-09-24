/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile.base;

import hellfirepvp.astralsorcery.common.component.ConstellationPaperComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.tile.base.*;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BaseTileBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class BaseTileBlock<T extends BlockEntity> extends BaseEntityBlock {

    private final TileRegistryObject<T> tileType;

    protected BaseTileBlock(Properties properties, TileRegistryObject<T> tileType) {
        super(properties);
        this.tileType = tileType;
    }

    public final TileRegistryObject<T> getTileType() {
        return this.tileType;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.getTileType().newTile(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.isClientSide()) {
            super.onRemove(state, level, pos, newState, movedByPiston);
            return;
        }
        this.dropTileContents(state, level, pos);

        MiscUtil.getTileAt(level, pos, BlockEntity.class, true).ifPresent(tile -> {
            //Remove observer from structure observing tiles
            if (tile instanceof TileEntityTick<?> tickTile && tickTile.shouldRemoveStructureObserver(state, newState)) {
                tickTile.removeObserver(level);
            }
        });

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    protected void dropTileContents(BlockState state, Level level, BlockPos pos) {
        MiscUtil.getTileAt(level, pos, BlockEntity.class, true).ifPresent(tile -> {
            //Drop items from inventory/inventories
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, state, tile, null);
            if (handler != null) {
                Containers.dropContents(level, pos, ItemUtil.listContents(handler));
            }
        });
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        MiscUtil.getTileAt(level, pos, TileEntitySynchronized.class, true).ifPresent(tile -> {
            if (tile.getTileData() instanceof TileDataCrystalAttributeContainer attributeContainer) {
                if (stack.has(DataComponentsAS.CRYSTAL_ATTRIBUTES)) {
                    stack.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, attributeContainer.getCrystalAttributes());
                }
            }
        });
        MiscUtil.getTileAt(level, pos, TileEntitySynchronized.class, true).ifPresent(tile -> {
            if (tile.getTileData() instanceof TileDataConstellationContainer constellationContainer) {
                constellationContainer.getConstellation().ifPresent(cst -> {
                    constellationContainer.setAsComponent(stack);
                });
            }
        });
        return stack;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.has(DataComponentsAS.CRYSTAL_ATTRIBUTES)) {
            MiscUtil.getTileAt(level, pos, TileEntitySynchronized.class, true).ifPresent(tile -> {
                if (tile.getTileData() instanceof TileDataCrystalAttributeContainer attributeContainer) {
                    attributeContainer.setCrystalAttributes(stack.getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, attributeContainer.getEmptyCrystalAttributes()));
                    attributeContainer.markForUpdate();
                }
            });
        }
        MiscUtil.getTileAt(level, pos, TileEntitySynchronized.class, true).ifPresent(tile -> {
            if (tile.getTileData() instanceof TileDataConstellationContainer constellationContainer) {
                if (stack.has(constellationContainer.getComponent())) {
                    ConstellationPaperComponent cmp = stack.get(constellationContainer.getComponent());
                    if (cmp != null) {
                        constellationContainer.setConstellation(cmp.getConstellation().orElse(null));
                        constellationContainer.markForUpdate();
                    }
                }
            }
        });
        if (placer instanceof ServerPlayer sPlayer) {
            MiscUtil.getTileAt(level, pos, TileEntitySynchronized.class, true).ifPresent(tile -> {
                if (tile.getTileData() instanceof TileDataOwned ownedData) {
                    ownedData.setOwnerId(sPlayer.getUUID());
                    ownedData.markForUpdate();
                }
            });
        }
    }
}
