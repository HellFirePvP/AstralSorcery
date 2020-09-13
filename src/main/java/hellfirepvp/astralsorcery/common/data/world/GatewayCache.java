/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world;

import hellfirepvp.astralsorcery.common.auxiliary.gateway.CelestialGatewayHandler;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import hellfirepvp.astralsorcery.common.util.log.LogUtil;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.observerlib.common.data.WorldCacheDomain;
import hellfirepvp.observerlib.common.data.base.GlobalWorldData;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GatewayCache
 * Created by HellFirePvP
 * Date: 30.05.2019 / 14:35
 */
public class GatewayCache extends GlobalWorldData {

    private final List<GatewayNode> gatewayPositions = new LinkedList<>();

    public GatewayCache(WorldCacheDomain.SaveKey<?> key) {
        super(key);
    }

    public List<GatewayNode> getGatewayPositions() {
        return new ArrayList<>(gatewayPositions);
    }

    public boolean hasGateway(BlockPos pos) {
        return this.gatewayPositions.stream().anyMatch(gateway -> gateway.getPos().equals(pos));
    }

    public void offerPosition(World world, BlockPos pos, @Nullable ITextComponent display, @Nullable DyeColor color) {
        TileCelestialGateway te = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, false);
        if (te == null) {
            return;
        }
        GatewayNode node = new GatewayNode(pos, display, color);
        if (gatewayPositions.contains(node)) {
            return;
        }
        gatewayPositions.add(node);
        markDirty();
        CelestialGatewayHandler.INSTANCE.addPosition(world, node);
        LogUtil.info(LogCategory.GATEWAY_CACHE, () -> "Added new gateway node at: dim=" + world.getDimension().getType().getId() + ", " + pos.toString());
    }

    public void removePosition(World world, BlockPos pos) {
        if (gatewayPositions.removeIf(node -> node.getPos().equals(pos))) {
            markDirty();
            CelestialGatewayHandler.INSTANCE.removePosition(world, pos);
            LogUtil.info(LogCategory.GATEWAY_CACHE, () -> "Removed gateway node at: dim=" + world.getDimension().getType().getId() + ", " + pos.toString());
        }
    }

    @Override
    public void updateTick(World world) {}

    @Override
    public void onLoad(IWorld world) {
        super.onLoad(world);

        LogUtil.info(LogCategory.GATEWAY_CACHE, () -> "Checking GatewayCache integrity for dimension " + world.getDimension().getType().getId());
        long msStart = System.currentTimeMillis();

        Iterator<GatewayNode> iterator = gatewayPositions.iterator();
        while (iterator.hasNext()) {
            GatewayNode node = iterator.next();
            TileCelestialGateway gateway;
            try {
                gateway = MiscUtils.getTileAt(world, node.getPos(), TileCelestialGateway.class, true);
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
    public void writeToNBT(CompoundNBT compound) {
        ListNBT list = new ListNBT();
        for (GatewayNode node : gatewayPositions) {
            CompoundNBT tag = new CompoundNBT();
            node.write(tag);
            list.add(tag);
        }
        compound.put("posList", list);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        ListNBT list = compound.getList("posList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT tag = list.getCompound(i);
            gatewayPositions.add(GatewayNode.read(tag));
        }
    }

    public static class GatewayNode {

        private final BlockPos pos;
        private final ITextComponent display;
        private final DyeColor color;

        public GatewayNode(BlockPos pos, @Nullable ITextComponent display, @Nullable DyeColor color) {
            this.pos = pos;
            this.display = display;
            this.color = color;
        }

        @Nullable
        public ITextComponent getDisplayName() {
            return display;
        }

        @Nullable
        public DyeColor getColor() {
            return color;
        }

        public BlockPos getPos() {
            return pos;
        }

        public void write(CompoundNBT tag) {
            NBTHelper.writeBlockPosToNBT(this.pos, tag);
            if (this.display != null) {
                tag.putString("display", ITextComponent.Serializer.toJson(this.display));
            }
            if (this.color != null) {
                NBTHelper.writeEnum(tag, "color", this.color);
            }
        }

        public static GatewayNode read(CompoundNBT tag) {
            BlockPos pos = NBTHelper.readBlockPosFromNBT(tag);
            ITextComponent display = null;
            if (tag.contains("display")) {
                display = ITextComponent.Serializer.fromJson(tag.getString("display"));
            }
            DyeColor color = null;
            if (tag.contains("color")) {
                color = NBTHelper.readEnum(tag, "color", DyeColor.class);
            }
            return new GatewayNode(pos, display, color);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GatewayNode that = (GatewayNode) o;
            return Objects.equals(pos, that.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos);
        }
    }
}
