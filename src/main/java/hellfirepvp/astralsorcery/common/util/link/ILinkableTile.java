package hellfirepvp.astralsorcery.common.util.link;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ILinkable
 * Created by HellFirePvP
 * Date: 03.08.2016 / 17:18
 */
//Interface for linking a TileEntity, which should implement this interface, to any other block for whatever reason.
public interface ILinkableTile {

    /**
     * This tile's world.
     * Links can only be created in the same world as this tile is in.
     */
    public World getWorld();

    /**
     * This tile's position
     */
    public BlockPos getPos();

    /**
     * The unlocalized displayname for this tile.
     * Can be null if no message should be displayed.
     */
    @Nullable
    public String getUnlocalizedDisplayName();

    /**
     * Informs of a successful link creation, however it is handled.
     * Can only happen after tryLink() returned true to mark a successful link creation.
     *
     * @param player the player that created the link.
     */
    public void onLinkCreate(EntityPlayer player, BlockPos other);

    /**
     * Informs that a player right-clicked the tile to start the linking process.
     *
     * @param player the player starting to create a link
     */
    default public void onSelect(EntityPlayer player) {}

    /**
     * Called when a player right-clicks any other block while in link mode
     * and this tile's onSelect()
     *
     * @param player the player trying to create the link.
     * @param other the other block this tile is supposed to link to.
     * @return true, if and only if a allowed/correct link can be created, false otherwise
     */
    public boolean tryLink(EntityPlayer player, BlockPos other);

    /**
     * Called when a player shift-right-clicks a block that is linked to this tile.
     *
     * @param player the player trying to undo the link.
     * @param other the other block this tile has a link to.
     * @return true, if the link got removed, which, in case this is actually linked to the given block, should always happen
     */
    public boolean tryUnlink(EntityPlayer player, BlockPos other);

    /**
     * Get the block positions this tile is currently linked to.
     *
     * @return the block positions
     */
    public List<BlockPos> getLinkedPositions();

}
