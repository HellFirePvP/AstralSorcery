/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world.data;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.CelestialGatewaySystem;
import hellfirepvp.astralsorcery.common.data.world.CachedWorldData;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
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
 * Date: 16.04.2017 / 18:05
 */
public class GatewayCache extends CachedWorldData {

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
        AstralSorcery.log.info("[AstralSorcery] Added new gateway node at: dim=" + world.provider.getDimension() + ", " + pos.toString());
    }

    public void removePosition(World world, BlockPos pos) {
        if(gatewayPositions.remove(pos)) {
            markDirty();
            CelestialGatewaySystem.instance.removePosition(world, pos);
            AstralSorcery.log.info("[AstralSorcery] Removed gateway node at: dim=" + world.provider.getDimension() + ", " + pos.toString());
        }
    }

    @Override
    public void onLoad(World world) {
        AstralSorcery.log.info("[AstralSorcery] Checking GatewayCache integrity for dimension " + world.provider.getDimension());
        long msStart = System.currentTimeMillis();

        Iterator<GatewayNode> iterator = gatewayPositions.iterator();
        while (iterator.hasNext()) {
            GatewayNode node = iterator.next();
            TileCelestialGateway gateway = MiscUtils.getTileAt(world, node, TileCelestialGateway.class, true);
            if (gateway == null) {
                iterator.remove();
                AstralSorcery.log.info("[AstralSorcery] Invalid entry: " + node + " - no gateway tileentity found there!");
            }
        }

        AstralSorcery.log.info("[AstralSorcery] GatewayCache checked and fully loaded in " + (System.currentTimeMillis() - msStart) + "ms! Collected and checked " + gatewayPositions.size() + " gateway nodes!");
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("posList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            BlockPos pos = NBTUtils.readBlockPosFromNBT(tag);
            String display = tag.getString("display");
            GatewayNode node = new GatewayNode(pos, display);
            gatewayPositions.add(node);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (GatewayNode node : gatewayPositions) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(node, tag);
            tag.setString("display", node.display);
            list.appendTag(tag);
        }
        compound.setTag("posList", list);
    }

    @Override
    public void updateTick(World world) {}

    public static class GatewayNode extends BlockPos {

        public final String display;

        public GatewayNode(BlockPos pos, String display) {
            super(pos.getX(), pos.getY(), pos.getZ());
            this.display = display;
        }

        @Override
        public boolean equals(Object pos) {
            return super.equals(pos);
        }
    }

}
