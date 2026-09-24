/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: JsonConfigurationManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class JsonConfigurationManager {

    private static final JsonConfigurationManager INSTANCE = new JsonConfigurationManager();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, JsonDataRegistry<?>> registries = new HashMap<>();

    private JsonConfigurationManager() {}

    public static JsonConfigurationManager getInstance() {
        return INSTANCE;
    }

    public void addRegistry(String name, JsonDataRegistry<?> registry) {
        this.registries.put(name, registry);
    }

    public void loadRegistries(MinecraftServer server) {
        Path cfgDir = FMLPaths.CONFIGDIR.get().resolve(AstralSorcery.MODID);
        if (!cfgDir.toFile().exists()) cfgDir.toFile().mkdirs();
        DynamicOps<JsonElement> jsonOps = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        this.registries.forEach((name, registry) -> {
            File cfgFile = cfgDir.resolve(name + ".json").toFile();
            if (!cfgFile.exists()) {
                this.createDefaultRegistry(cfgFile, jsonOps, registry);
            } else {
                this.loadRegistry(cfgFile, jsonOps, registry);
            }
        });
    }

    private <T> void createDefaultRegistry(File cfgFile, DynamicOps<JsonElement> jsonOps, JsonDataRegistry<T> registry) {
        List<T> defaultValues = registry.getDefaultValues();
        JsonElement json = registry.elementCodec().listOf().encodeStart(jsonOps, defaultValues).getOrThrow();

        try (FileWriter writer = new FileWriter(cfgFile)) {
            writer.write(GSON.toJson(json));
        } catch (IOException e) {
            AstralSorcery.LOG.error("Failed to write default config file: {}", cfgFile.getName(), e);
        }
        registry.setLoadedValues(defaultValues);
    }

    private <T> void loadRegistry(File cfgFile, DynamicOps<JsonElement> jsonOps, JsonDataRegistry<T> registry) {
        try (FileReader reader = new FileReader(cfgFile)) {
            JsonElement json = JsonParser.parseReader(reader);
            List<T> values = registry.elementCodec().listOf().parse(jsonOps, json).getOrThrow();
            registry.setLoadedValues(values);
            AstralSorcery.LOG.info("Loaded {} entries for JSON registry '{}'", values.size(), cfgFile.getName());
        } catch (IOException e) {
            AstralSorcery.LOG.error("Failed to read config file: {}", cfgFile.getName(), e);
        }
    }
}
