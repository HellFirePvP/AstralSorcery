/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary.gateway;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CelestialGatewayFilter
 * Created by HellFirePvP
 * Date: 23.08.2019 / 22:15
 */
public class CelestialGatewayFilter {

    private final File gatewayFilter;
    private Set<RegistryKey<World>> cache = new HashSet<>();

    CelestialGatewayFilter() {
        this.gatewayFilter = this.loadFilter();
        this.loadCache();
    }

    private File loadFilter() {
        File dataDir = AstralSorcery.getProxy().getASServerDataDirectory();
        File gatewayFilter = new File(dataDir, "gateway_filter.dat");
        if (!gatewayFilter.exists()) {
            try {
                gatewayFilter.createNewFile();
            } catch (IOException exc) {
                throw new IllegalStateException("Couldn't create plain world filter file! Are we missing file permissions?", exc);
            }
        }
        return gatewayFilter;
    }

    public boolean hasGateways(ResourceLocation worldKey) {
        return this.cache.contains(worldKey);
    }

    void addDim(RegistryKey<World> worldKey) {
        if (cache.add(worldKey)) {
            this.saveCache();
        }
    }

    void removeDim(RegistryKey<World> worldKey) {
        if (cache.remove(worldKey)) {
            this.saveCache();
        }
    }

    private void loadCache() {
        try {
            CompoundNBT tag = CompressedStreamTools.read(this.gatewayFilter);
            ListNBT list = tag.getList("list", Constants.NBT.TAG_STRING);
            this.cache = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                ResourceLocation location = new ResourceLocation(list.getString(i));
                this.cache.add(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, location));
            }
        } catch (IOException ignored) {
            this.cache = new HashSet<>();
        }
    }

    private void saveCache() {
        try {
            ListNBT list = new ListNBT();
            for (RegistryKey<World> dimType : cache) {
                list.add(StringNBT.valueOf(dimType.getLocation().toString()));
            }
            CompoundNBT cmp = new CompoundNBT();
            cmp.put("list", list);
            CompressedStreamTools.write(cmp, this.gatewayFilter);
        } catch (IOException ignored) {}
    }
}
