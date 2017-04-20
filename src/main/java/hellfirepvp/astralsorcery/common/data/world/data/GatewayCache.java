/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world.data;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.CelestialGatewaySystem;
import hellfirepvp.astralsorcery.common.data.world.CachedWorldData;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GatewayCache
 * Created by HellFirePvP
 * Date: 16.04.2017 / 18:05
 */
public class GatewayCache extends CachedWorldData {

    private List<BlockPos> gatewayPositions = new LinkedList<>();

    public GatewayCache() {
        super(WorldCacheManager.SaveKey.GATEWAY_DATA);
    }

    public List<BlockPos> getGatewayPositions() {
        return new ArrayList<>(gatewayPositions);
    }

    public void offerPosition(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if(te == null || !(te instanceof TileCelestialGateway)) {
            return;
        }
        if(gatewayPositions.contains(pos)) {
            return;
        }
        gatewayPositions.add(pos);
        markDirty();
        CelestialGatewaySystem.instance.addPosition(world, pos);
        AstralSorcery.log.info("Added new gateway node at: dim=" + world.provider.getDimension() + ", " + pos.toString());
    }

    public void removePosition(World world, BlockPos pos) {
        if(gatewayPositions.remove(pos)) {
            markDirty();
            CelestialGatewaySystem.instance.removePosition(world, pos);
            AstralSorcery.log.info("Removed gateway node at: dim=" + world.provider.getDimension() + ", " + pos.toString());
        }
    }

    @Override
    public void onLoad(World world) {
        AstralSorcery.log.info("Checking GatewayCache integrity for dimension " + world.provider.getDimension());
        long msStart = System.currentTimeMillis();

        Iterator<BlockPos> iterator = gatewayPositions.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            TileEntity te = world.getTileEntity(pos); //Loads the chunk... uh...
            if (te == null || !(te instanceof TileCelestialGateway)) {
                iterator.remove();
                AstralSorcery.log.info("Invalid entry: " + pos + " - no gateway tileentity found there!");
            }
        }

        AstralSorcery.log.info("GatewayCache checked and fully loaded in " + (System.currentTimeMillis() - msStart) + "ms! Collected and checked " + gatewayPositions.size() + " gateway nodes!");
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("posList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            gatewayPositions.add(NBTUtils.readBlockPosFromNBT(list.getCompoundTagAt(i)));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (BlockPos pos : gatewayPositions) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(pos, tag);
            list.appendTag(tag);
        }
        compound.setTag("posList", list);
    }

    @Override
    public void updateTick(World world) {}

}
