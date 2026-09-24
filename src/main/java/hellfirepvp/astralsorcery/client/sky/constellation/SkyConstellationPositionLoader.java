/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky.constellation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.datagen.ConstellationPositionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SkyConstellationPositionLoader
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SkyConstellationPositionLoader implements ResourceManagerReloadListener {

    private static final Gson GSON = new Gson();
    private static final SkyConstellationPositionLoader INSTANCE = new SkyConstellationPositionLoader();

    private final List<SkyConstellationPosition> positions = new ArrayList<>();
    private boolean renderDebug = false;

    private SkyConstellationPositionLoader() {}

    public static SkyConstellationPositionLoader getInstance() {
        return INSTANCE;
    }

    public List<SkyConstellationPosition> getPositions() {
        return Collections.unmodifiableList(this.positions);
    }

    public boolean renderDebug() {
        return this.renderDebug;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.positions.clear();

        for (String namespace : resourceManager.getNamespaces()) {
            try {
                Resource positionFile = resourceManager
                        .getResource(ResourceLocation.fromNamespaceAndPath(namespace, "constellation_positions.json"))
                        .orElse(null);
                if (positionFile != null) {
                    try (InputStream stream = positionFile.open()) {
                        String posJson = IOUtils.toString(stream, StandardCharsets.UTF_8);
                        JsonObject obj = GSON.fromJson(posJson, JsonObject.class);
                        ConstellationPositionProvider.PositionFile file = ConstellationPositionProvider.PositionFile.CODEC.parse(JsonOps.INSTANCE, obj)
                                .getOrThrow(str -> new JsonParseException("Invalid Constellation position file: " + str));
                        if (file.replace()) {
                            this.renderDebug = false;
                            this.positions.clear();
                        }
                        file.positions().forEach(SkyConstellationPosition::validatePosition);
                        this.positions.addAll(file.positions());
                        this.renderDebug |= file.renderDebug();
                    }
                }
            } catch (IOException exc) {
                AstralSorcery.LOG.warn(exc);
            }
        }
        this.positions.sort(Comparator.comparingDouble(SkyConstellationPosition::pitch));
    }
}
