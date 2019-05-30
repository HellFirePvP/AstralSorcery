/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world.data;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.base.GlobalWorldData;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import hellfirepvp.astralsorcery.common.util.log.LogUtil;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GatewayCache
 * Created by HellFirePvP
 * Date: 30.05.2019 / 14:35
 */
public class GatewayCache extends GlobalWorldData {

    private List<GatewayNode> gatewayPositions = new LinkedList<>();

    public GatewayCache() {
        super(WorldCacheManager.SaveKey.GATEWAY_DATA);
    }

    public List<GatewayNode> getGatewayPositions() {
        return new ArrayList<>(gatewayPositions);
    }

    public void offerPosition(World world, BlockPos pos, @Nonnull String display) {
        TileEntity te = world.getTileEntity(pos);
        if(te == null || !(te instanceof TileCelestialGateway)) {
            return;
        }
        GatewayNode node = new GatewayNode(pos, display);
        if(gatewayPositions.contains(node)) {
            return;
        }
        gatewayPositions.add(node);
        markDirty();
        CelestialGatewaySystem.instance.addPosition(world, node);
        LogUtil.info(LogCategory.GATEWAY_CACHE, () -> "Added new gateway node at: dim=" + world.getDimension().getType().getId() + ", " + pos.toString());
    }

    public void removePosition(World world, BlockPos pos) {
        if(gatewayPositions.remove(pos)) {
            markDirty();
            CelestialGatewaySystem.instance.removePosition(world, pos);
            LogUtil.info(LogCategory.GATEWAY_CACHE, () -> "Removed gateway node at: dim=" + world.getDimension().getType().getId() + ", " + pos.toString());
        }
    }

    @Override
    public void updateTick(World world) {}

    @Override
    public void onLoad(World world) {
        super.onLoad(world);

        LogUtil.info(LogCategory.GATEWAY_CACHE, () -> "Checking GatewayCache integrity for dimension " + world.getDimension().getType().getId());
        long msStart = System.currentTimeMillis();

        Iterator<GatewayNode> iterator = gatewayPositions.iterator();
        while (iterator.hasNext()) {
            GatewayNode node = iterator.next();
            TileCelestialGateway gateway;
            try {
                gateway = MiscUtils.getTileAt(world, node, TileCelestialGateway.class, true);
            } catch (Exception loadEx) {
                LogUtil.info(LogCategory.GATEWAY_CACHE, () -> "Failed to check gateway for " + node + " skipping");
                continue;
            }
            if (gateway == null) {
                iterator.remove();
                LogUtil.info(LogCategory.GATEWAY_CACHE, () -> "Invalid entry: " + node + " - no gateway tileentity found there!");
            }
        }

        LogUtil.info(LogCategory.GATEWAY_CACHE, () ->
                "GatewayCache checked and fully loaded in " + (System.currentTimeMillis() - msStart) +
                        "ms! Collected and checked " + gatewayPositions.size() + " gateway nodes!");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (GatewayNode node : gatewayPositions) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(node, tag);
            tag.setString("display", node.display);
            list.add(tag);
        }
        compound.setTag("posList", list);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getList("posList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            NBTTagCompound tag = list.getCompound(i);
            BlockPos pos = NBTHelper.readBlockPosFromNBT(tag);
            String display = tag.getString("display");
            GatewayNode node = new GatewayNode(pos, display);
            gatewayPositions.add(node);
        }
    }

    public static class GatewayNode extends BlockPos {

        private final String display;

        public GatewayNode(BlockPos pos, String display) {
            super(pos.getX(), pos.getY(), pos.getZ());
            this.display = display;
        }

        public String getDisplayName() {
            return display;
        }

        @Override
        public boolean equals(Object pos) {
            return super.equals(pos);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

    }
}
