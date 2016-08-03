package hellfirepvp.astralsorcery.common.starlight.transmission;

import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IPrismTransmissionNode
 * Created by HellFirePvP
 * Date: 03.08.2016 / 10:45
 */
public interface IPrismTransmissionNode {

    //Get the exact position of this Node
    public BlockPos getPos();

    //Fired to notify THIS that the link to "to" is no longer valid
    //The node at "to" should have THIS as a valid source.
    public void notifyUnlink(World world, BlockPos to);

    //Fired to notify THIS to add a link to "to"
    //The node at "to" should have THIS as a valid source.
    public void notifyLink(World world, BlockPos to);

    //Fired to notify THIS that the given "source" is a valid energy transmission node
    //The node at "source" should have THIS as its "next" or one of his "next"
    public void notifySourceLink(World world, BlockPos source);

    //Fired to notify THIS that the given "source" is no longer a valid energy transmission node
    //The node at "source" should have THIS as its "next" or one of his "next"
    public void notifySourceUnlink(World world, BlockPos source);

    //Fired to check if a line from THIS to a NEXT is still valid after blockchanges
    public void notifyBlockChange(World world, BlockPos changed);

    //Try get the next node. might not contain a valid transmission node.
    public List<NodeConnection<IPrismTransmissionNode>> queryNext(WorldNetworkHandler handler);

    //Get a list of all sources that do provide energy to this transmission node
    public List<BlockPos> getSources();

    //Get an empty instance for a transmission node here.
    //No operation will be performed, readFromNBT will be called instantly
    public IPrismTransmissionNode provideEmptyNBTReadInstance();

    //Should recreate the exact state from when it was written.
    public void readFromNBT(World world, NBTTagCompound compound);

    //Should save all data that's needed to recreate the state accordingly.
    public void writeToNBT(World world, NBTTagCompound compound);

}
