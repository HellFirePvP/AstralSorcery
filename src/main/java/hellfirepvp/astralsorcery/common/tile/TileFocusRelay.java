/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryStackList;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryView;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryViewFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileFocusRelay
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileFocusRelay extends TileEntitySynchronized<TileFocusRelay.Data> {

    public TileFocusRelay(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.FOCUS_RELAY, pos, blockState);
    }

    protected TileFocusRelay(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    public static class Data extends TileEntitySynchronized.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                CodecUtil.defaulted(InventoryStackList.CODEC, "inventoryContents", InventoryStackList::create, Data::getInventoryContents)
        ).apply(inst, Data::new));

        protected final InventoryStackList inventoryContents;

        protected Data(InventoryStackList inventoryContents) {
            this.inventoryContents = inventoryContents;
        }

        protected InventoryStackList getInventoryContents() {
            return inventoryContents;
        }

        protected InventoryViewFactory newRelayInventory() {
            return InventoryViewFactory.builder(1)
                    .accessibleSides(Direction.DOWN)
                    .stackSizeLimiter((slot, stack) -> 1);
        }

        public InventoryView getInventory() {
            return this.newRelayInventory().createTileView(this, this.getInventoryContents());
        }
    }
}
