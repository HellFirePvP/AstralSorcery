/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.lumen.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.datagen.LumenDisplayPositionProvider;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenDisplayPositionLoader
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenDisplayPositionLoader implements ResourceManagerReloadListener {

    private static final Gson GSON = new Gson();
    private static final LumenDisplayPositionLoader INSTANCE = new LumenDisplayPositionLoader();

    private final Map<Lumen, LumenDisplayPosition> positions = new HashMap<>();

    private LumenDisplayPositionLoader() {}

    public static LumenDisplayPositionLoader getInstance() {
        return INSTANCE;
    }

    public Map<Lumen, LumenDisplayPosition> getPositions() {
        return Collections.unmodifiableMap(this.positions);
    }

    @Nullable
    public LumenDisplayPosition getPosition(Lumen lumen) {
        return this.positions.get(lumen);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.positions.clear();

        for (String namespace : resourceManager.getNamespaces()) {

            try {
                Resource positionFile = resourceManager
                        .getResource(ResourceLocation.fromNamespaceAndPath(namespace, "lumen_display_positions.json"))
                        .orElse(null);
                if (positionFile != null) {
                    try (InputStream stream = positionFile.open()) {
                        String posJson = IOUtils.toString(stream, StandardCharsets.UTF_8);
                        JsonObject obj = GSON.fromJson(posJson, JsonObject.class);
                        LumenDisplayPositionProvider.PositionFile file = LumenDisplayPositionProvider.PositionFile.CODEC.parse(JsonOps.INSTANCE, obj)
                                .getOrThrow(str -> new JsonParseException("Invalid tome lumen display position file: " + str));
                        if (file.replace()) {
                            this.positions.clear();
                        }
                        this.positions.putAll(file.positions());
                    }
                }
            } catch (IOException exc) {
                AstralSorcery.LOG.warn(exc);
            }
        }
    }
}
