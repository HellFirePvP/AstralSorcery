/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary.link;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LinkableTileEntity
 * Created by HellFirePvP
 * Date: 30.06.2019 / 20:57
 */
//Interface for linking a TileEntity, which should implement this interface, to any other block for whatever reason.
public interface LinkableTileEntity {

    /**
     * This tile's world.
     * Links can only be created in the same world as this tile is in.
     */
    default public World getLinkWorld() {
        if (this instanceof TileEntity) {
            return ((TileEntity) this).getWorld();
        }
        throw new IllegalStateException("LinkableTileEntity not implemented on TileEntity: " + this.getClass());
    }

    /**
     * This tile's position
     */
    default public BlockPos getLinkPos() {
        if (this instanceof TileEntity) {
            return ((TileEntity) this).getPos();
        }
        throw new IllegalStateException("LinkableTileEntity not implemented on TileEntity: " + this.getClass());
    }

    /**
     * The unLocalized displayname for this tile.
     * Can be null if no message should be displayed.
     */
    @Nullable
    default public String getUnLocalizedDisplayName() {
        if (this instanceof TileEntity) {
            BlockState state = ((TileEntity) this).getBlockState();
            return state.getBlock().getTranslationKey();
        }
        throw new IllegalStateException("LinkableTileEntity not implemented on TileEntity: " + this.getClass());
    }

    /**
     * Defines if this Tile does accept other tiles linking to it.
     *
     * True to allow other tiles to create links to this tile
     * False to deny any tile to link to this tile.
     *
     * Returns true by default.
     */
    default public boolean doesAcceptLinks() {
        return true;
    }

    /**
     * Informs of a successful link creation, however it is handled.
     * Can only happen after tryLink() returned true to mark a successful link creation.
     *
     * @param player the player that created the link.
     */
    public void onLinkCreate(PlayerEntity player, BlockPos other);

    /**
     * Informs that a player right-clicked the tile to start the linking process.
     *
     * @param player the player starting to create a link
     *
     * @return boolean true if the select actually selected it, false for any other selection modification
     */
    default public boolean onSelect(PlayerEntity player) {
        if (player.isSneaking()) {
            for (BlockPos linkTo : Lists.newArrayList(getLinkedPositions())) {
                tryUnlink(player, linkTo);
            }
            player.sendMessage(new TranslationTextComponent("astralsorcery.misc.link.unlink.all").setStyle(new Style().setColor(TextFormatting.GREEN)));
            return false;
        }
        return true;
    }

    /**
     * Called when a player right-clicks any other block while in link mode
     * and this tile's onSelect()
     *
     * @param player the player trying to create the link.
     * @param other the other block this tile is supposed to link to.
     * @return true, if and only if a allowed/correct link can be created, false otherwise
     */
    public boolean tryLink(PlayerEntity player, BlockPos other);

    /**
     * Called when a player shift-right-clicks a block that is linked to this tile.
     *
     * @param player the player trying to undo the link.
     * @param other the other block this tile has a link to.
     * @return true, if the link got removed, which, in case this is actually linked to the given block, should always happen
     */
    public boolean tryUnlink(PlayerEntity player, BlockPos other);

    /**
     * Get the block positions this tile is currently linked to.
     *
     * @return the block positions
     */
    public List<BlockPos> getLinkedPositions();

}
