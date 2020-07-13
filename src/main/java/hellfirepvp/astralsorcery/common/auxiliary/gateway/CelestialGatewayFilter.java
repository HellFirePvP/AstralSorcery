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
import net.minecraft.util.ResourceLocation;
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

    private File gatewayFilter;
    private Set<ResourceLocation> cache = new HashSet<>();

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

    public boolean hasGateways(ResourceLocation dimType) {
        return this.cache.contains(dimType);
    }

    void addDim(ResourceLocation type) {
        if (cache.add(type)) {
            this.saveCache();
        }
    }

    void removeDim(ResourceLocation type) {
        if (cache.remove(type)) {
            this.saveCache();
        }
    }

    private void loadCache() {
        try {
            CompoundNBT tag = CompressedStreamTools.read(this.gatewayFilter);
            ListNBT list = tag.getList("list", Constants.NBT.TAG_STRING);
            this.cache = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                String str = list.getString(i);
                this.cache.add(new ResourceLocation(str));
            }
        } catch (IOException ignored) {
            this.cache = new HashSet<>();
        }
    }

    private void saveCache() {
        try {
            ListNBT list = new ListNBT();
            for (ResourceLocation dimType : cache) {
                list.add(StringNBT.valueOf(dimType.toString()));
            }
            CompoundNBT cmp = new CompoundNBT();
            cmp.put("list", list);
            CompressedStreamTools.write(cmp, this.gatewayFilter);
        } catch (IOException ignored) {}
    }
}
