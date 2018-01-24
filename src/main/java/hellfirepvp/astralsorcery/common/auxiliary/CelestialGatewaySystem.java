/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.GatewayCache;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktUpdateGateways;
import hellfirepvp.astralsorcery.common.util.FileStorageUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.*;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CelestialGatewaySystem
 * Created by HellFirePvP
 * Date: 16.04.2017 / 18:50
 */
public class CelestialGatewaySystem {

    private boolean startup = false;

    public static CelestialGatewaySystem instance = new CelestialGatewaySystem();
    private Map<Integer, List<GatewayCache.GatewayNode>> serverCache = new HashMap<>();
    private Map<Integer, List<GatewayCache.GatewayNode>> clientCache = new HashMap<>();

    private CelestialGatewaySystem() {}

    public GatewayWorldFilter getFilter() {
        File f = FileStorageUtil.getGeneralSubDirectory("gatewayFilter");
        File worldFilter = new File(f, "worldFilter.dat");
        if(!worldFilter.exists()) {
            try {
                worldFilter.createNewFile();
            } catch (IOException exc) {
                throw new IllegalStateException("Couldn't create plain world filter file! Are we missing file permissions?", exc);
            }
        }
        return new GatewayWorldFilter(worldFilter);
    }

    public void onServerStart() {
        startup = true;
        Integer[] worlds = DimensionManager.getStaticDimensionIDs(); //Should be loaded during startup = we should grab those.
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        List<Integer> involved = getFilter().getInvolvedWorlds();
        for (Integer id : worlds) {
            if(id == null || !involved.contains(id)) continue;
            WorldServer world = server.getWorld(id);
            loadWorldCache(world);

            if(world.getChunkProvider().getLoadedChunkCount() <= 0 && ForgeChunkManager.getPersistentChunksFor(world).size() == 0 && !world.provider.getDimensionType().shouldLoadSpawn()){
                DimensionManager.unloadWorld(world.provider.getDimension());
            }
        }
        startup = false;
        syncToAll();
    }

    @SubscribeEvent
    public void onWorldInit(WorldEvent.Load event) {
        if(startup) return; //We're already loading up there.

        World world = event.getWorld();
        if(world.isRemote) return;

        loadWorldCache(world);
        syncToAll();
    }

    public void syncTo(EntityPlayer pl) {
        PktUpdateGateways pkt = new PktUpdateGateways(serverCache);
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) pl);
    }

    public void syncToAll() {
        PktUpdateGateways pkt = new PktUpdateGateways(serverCache);
        PacketChannel.CHANNEL.sendToAll(pkt);
    }

    public List<GatewayCache.GatewayNode> getGatewaysForWorld(World world, Side side) {
        return (side == Side.SERVER ? serverCache : clientCache).get(world.provider.getDimension());
    }

    public Map<Integer, List<GatewayCache.GatewayNode>> getGatewayCache(Side side) {
        return Collections.unmodifiableMap(side == Side.SERVER ? serverCache : clientCache);
    }

    public void addPosition(World world, GatewayCache.GatewayNode pos) {
        if(world.isRemote) return;

        int dim = world.provider.getDimension();
        if(!serverCache.containsKey(dim)) {
            forceLoad(dim);
        }
        if(!serverCache.containsKey(dim)) {
            AstralSorcery.log.info("[AstralSorcery] Couldn't add position for world " + dim + "! - Force loading the world resulted in... nothing.");
            return;
        }

        getFilter().appendAndSave(dim);
        List<GatewayCache.GatewayNode> cache = serverCache.get(dim);
        if(!cache.contains(pos)) {
            cache.add(pos);
            syncToAll();
        }
    }

    public void removePosition(World world, BlockPos pos) {
        if(world.isRemote) return;

        int dim = world.provider.getDimension();
        if(!serverCache.containsKey(dim)) {
            return;
        }
        if(serverCache.get(dim).remove(pos)) {
            if(serverCache.get(dim).isEmpty()) {
                getFilter().removeAndSave(dim);
            }
            syncToAll();
        }
    }

    private void forceLoad(int dim) {
        WorldServer serv = DimensionManager.getWorld(dim);
        if(serv == null) {
            DimensionManager.initDimension(dim);
        }
    }

    public void updateClientCache(Map<Integer, List<GatewayCache.GatewayNode>> positions) {
        this.clientCache = positions;
    }

    private void loadWorldCache(World world) {
        GatewayCache cache = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.GATEWAY_DATA);
        serverCache.put(world.provider.getDimension(), cache.getGatewayPositions());
    }

    public static class GatewayWorldFilter {

        private final File gatewayCacheFile;
        private List<Integer> cache = null;

        private GatewayWorldFilter(File gatewayCacheFile) {
            this.gatewayCacheFile = gatewayCacheFile;
        }

        public List<Integer> getInvolvedWorlds() {
            if(cache == null) {
                loadCache();
            }
            return cache;
        }

        private void loadCache() {
            try {
                NBTTagCompound tag = CompressedStreamTools.read(this.gatewayCacheFile);
                NBTTagList list = tag.getTagList("list", Constants.NBT.TAG_INT);
                cache = Lists.newArrayList(0);
                for (int i = 0; i < list.tagCount(); i++) {
                    int id = list.getIntAt(i);
                    if(!cache.contains(id)) {
                        cache.add(id);
                    }
                }
            } catch (IOException ignored) {
                cache = Lists.newArrayList(0);
            }
        }

        private void appendAndSave(int id) {
            if(cache == null) {
                loadCache();
            }
            if(!cache.contains(id)) {
                cache.add(id);
                try {
                    NBTTagList list = new NBTTagList();
                    for (int dimId : cache) {
                        list.appendTag(new NBTTagInt(dimId));
                    }
                    NBTTagCompound cmp = new NBTTagCompound();
                    cmp.setTag("list", list);
                    CompressedStreamTools.write(cmp, this.gatewayCacheFile);
                } catch (IOException ignored) {}
            }
        }

        private void removeAndSave(int dim) {
            if(cache == null) {
                loadCache();
            }
            if(cache.contains(dim)) {
                cache.remove(dim);
                try {
                    NBTTagList list = new NBTTagList();
                    for (int dimId : cache) {
                        list.appendTag(new NBTTagInt(dimId));
                    }
                    NBTTagCompound cmp = new NBTTagCompound();
                    cmp.setTag("list", list);
                    CompressedStreamTools.write(cmp, this.gatewayCacheFile);
                } catch (IOException ignored) {}
            }
        }

    }

}
