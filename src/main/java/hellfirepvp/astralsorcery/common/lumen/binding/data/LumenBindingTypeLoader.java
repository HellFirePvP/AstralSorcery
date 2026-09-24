/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.util.data.MapStream;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.LogicalSide;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingTypeLoader
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingTypeLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final LumenBindingTypeLoader INSTANCE = new LumenBindingTypeLoader();

    private final Map<ResourceLocation, LumenBindingType> bindings = new HashMap<>();
    private final Map<ResourceKey<Lumen>, ResourceLocation> lumenApplicationMapping = new HashMap<>();

    private final Map<ResourceLocation, LumenBindingType> clientBindings = new HashMap<>();
    private final Map<ResourceKey<Lumen>, ResourceLocation> clientLumenApplicationMapping = new HashMap<>();

    private LumenBindingTypeLoader() {
        super(GSON, "lumen_binding");
    }

    public static LumenBindingTypeLoader getInstance() {
        return INSTANCE;
    }

    public Optional<ResourceLocation> getLumenBindingType(LogicalSide side, ResourceLocation lumenKey) {
        return this.getLumenBindingType(side, ResourceKey.create(RegistriesAS.KEY_LUMEN, lumenKey));
    }

    public Optional<ResourceLocation> getLumenBindingType(LogicalSide side, ResourceKey<Lumen> lumenKey) {
        return Optional.ofNullable(this.getLumenApplicationMapping(side).get(lumenKey));
    }

    public Optional<LumenBindingType> getBindingType(LogicalSide side, ResourceLocation bindingKey) {
        return Optional.ofNullable(this.getBindings(side).get(bindingKey));
    }

    public Map<ResourceLocation, LumenBindingType> getBindings(LogicalSide side) {
        return switch (side) {
            case CLIENT -> Map.copyOf(this.clientBindings);
            case SERVER -> Map.copyOf(this.bindings);
        };
    }

    public Map<ResourceKey<Lumen>, ResourceLocation> getLumenApplicationMapping(LogicalSide side) {
        return switch (side) {
            case CLIENT -> Map.copyOf(this.clientLumenApplicationMapping);
            case SERVER -> Map.copyOf(this.lumenApplicationMapping);
        };
    }

    public void updateClientBindings(Map<ResourceLocation, LumenBindingType> bindings, Map<ResourceKey<Lumen>, ResourceLocation> lumenApplicationMapping) {
        this.clientBindings.clear();
        this.clientBindings.putAll(bindings);
        this.clientLumenApplicationMapping.clear();
        this.clientLumenApplicationMapping.putAll(lumenApplicationMapping);
    }

    public void clearClientBindings() {
        this.clientBindings.clear();
        this.clientLumenApplicationMapping.clear();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonObject> loadableFiles = MapStream.of(dataMap)
                .filterValue(JsonElement::isJsonObject)
                .mapValue(JsonElement::getAsJsonObject)
                .toMap();
        Map<ResourceLocation, JsonObject> mappings = MapStream.of(loadableFiles)
                .filterKey(key -> key.getPath().startsWith("_"))
                .toMap();
        Map<ResourceLocation, JsonObject> bindingTypes = MapStream.of(loadableFiles)
                .filterKey(key -> !key.getPath().startsWith("_"))
                .toMap();

        AstralSorcery.LOG.info("Loading {} lumen binding types and {} mapping files.", bindingTypes.size(), mappings.size());

        this.bindings.clear();
        this.lumenApplicationMapping.clear();
        RegistryOps<JsonElement> ops = this.getRegistryLookup().createSerializationContext(JsonOps.INSTANCE);
        mappings.forEach((key, obj) -> {
            LumenBindingTypeMapping.CODEC.parse(ops, obj)
                    .ifError(error -> AstralSorcery.LOG.warn("Failed to load lumen binding type mapping {}: {}", key, error.message()))
                    .ifSuccess(mapping -> this.lumenApplicationMapping.putAll(mapping.getMapping()));
        });

        bindingTypes.forEach((key, obj) -> {
            LumenBindingType.CODEC.parse(ops, obj)
                    .ifError(error -> AstralSorcery.LOG.warn("Failed to load lumen binding type {}: {}", key, error.message()))
                    .ifSuccess(bindingType -> this.bindings.put(key, bindingType));
        });
    }
}
