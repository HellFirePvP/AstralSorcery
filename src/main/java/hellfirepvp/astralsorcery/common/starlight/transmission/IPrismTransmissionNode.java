/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission;

import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionProvider;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IPrismTransmissionNode
 * Created by HellFirePvP
 * Date: 03.08.2016 / 10:45
 */
public interface IPrismTransmissionNode extends ILocatable {

    public static final CrystalAttributes EMPTY = CrystalAttributes.Builder.newBuilder(false).build();
    public static final Random rand = new Random();

    //Get the exact position of this Node
    public BlockPos getLocationPos();

    //Get his node's transmission properties to calculate transmission loss and so on
    //Arbitrarily this returns a max. sized Property by default...
    default public CrystalAttributes getTransmissionProperties() {
        return EMPTY;
    }

    //Used to push update from the tileentity owning this node (potentially)
    //to this network node. Return true to indicate a successful data transfer
    default public <T extends TileEntity> boolean updateFromTileEntity(T tile) {
        return true;
    }

    //Get this node's additional transmission loss multiplier.
    //Why the transmission is reduced even more is not important here.
    //0 means 100% all starlight is lost here, 1 means nothing is lost.
    //By default we don't increase additional loss.
    default public float getAdditionalTransmissionLossMultiplier() {
        return 1F;
    }

    //If this returns true, this node will be additionally cached in the transmission chain
    //Whenever starlight flows through the chain, it'll receive an update with #onTransmissionTick
    //This can not be used to manipulate starlight transmission in any way.
    default public boolean needsTransmissionUpdate() {
        return false;
    }

    //The update method of #needsTransmissionUpdate
    default public void onTransmissionTick(World world) {}

    //Fired to notify THIS that the link to "to" is no longer valid
    //The node at "to" should have THIS as a valid source.
    public boolean notifyUnlink(World world, BlockPos to);

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
    //Return true, if and only if the state of this node in regards to the network has changed at all.
    public boolean notifyBlockChange(World world, BlockPos changed);

    //Try get the next node. might not contain a valid transmission node.
    public List<NodeConnection<IPrismTransmissionNode>> queryNext(WorldNetworkHandler handler);

    //Get a list of all sources that do provide energy to this transmission node
    public List<BlockPos> getSources();

    //If this returns true, the TransmissionNode will be added to the UpdateHandler,
    //Receiving ticks each server-world-tick for the world it is in.
    default public boolean needsUpdate() {
        return false;
    }

    //If needsUpdate returns true and it is added to the UpdateHandler,
    //this method will be called each server-world-tick and may be used
    //like the TileEntity's update method.
    default public void update(World world) {}

    //Called once after reading the node from NBT
    //Use this for post-load/place logic.
    default public void postLoad(IWorld world) {}

    //Flags the world's LightNetworkBuffer as dirty,
    //which causes it to be recalculated and saved
    //whenever the world saves the next time.
    default public void markDirty(World world) {
        DataAS.DOMAIN_AS.getData(world, DataAS.KEY_STARLIGHT_NETWORK).markDirty(this.getLocationPos());
    }

    //Get the provider of the node. Used to recreate the class at NBT read.
    public TransmissionProvider getProvider();

    //Should recreate the exact state from when it was written.
    public void readFromNBT(CompoundNBT compound);

    //Should save all data that's needed to recreate the state accordingly.
    public void writeToNBT(CompoundNBT compound);

}
