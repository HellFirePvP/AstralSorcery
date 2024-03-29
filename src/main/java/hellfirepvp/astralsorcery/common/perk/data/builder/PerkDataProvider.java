/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.data.builder;

import com.google.gson.*;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDataWriter
 * Created by HellFirePvP
 * Date: 14.08.2020 / 19:09
 */
public abstract class PerkDataProvider implements IDataProvider {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final DataGenerator generator;

    public PerkDataProvider(DataGenerator generator) {
        this.generator = generator;
    }

    public abstract void registerPerks(Consumer<FinishedPerk> registrar);

    @Override
    public void act(DirectoryCache cache) throws IOException {
        Path path = this.generator.getOutputFolder();

        List<FinishedPerk> builtPerks = new ArrayList<>();
        this.registerPerks(finishedPerk -> {
            ResourceLocation perkName = finishedPerk.perk.getRegistryName();
            Point.Float offset = finishedPerk.perk.getOffset();
            if (builtPerks.stream().anyMatch(knownPerk -> knownPerk.perk.getOffset().equals(offset))) {
                throw new IllegalArgumentException("Duplicate perk registration at " + offset + " for " + perkName);
            }
            if (builtPerks.contains(finishedPerk)) {
                throw new IllegalArgumentException("Duplicate perk registry name: " + perkName);
            }
            builtPerks.add(finishedPerk);
            this.savePerkFile(cache, finishedPerk.serialize(), path.resolve(String.format("data/%s/perks/%s.json", perkName.getNamespace(), perkName.getPath())));
        });

        JsonObject allPerks = new JsonObject();
        builtPerks.sort(Comparator.naturalOrder());
        builtPerks.forEach(perk -> allPerks.add(perk.perk.getRegistryName().toString(), perk.serialize()));
        this.savePerkFile(cache, allPerks, path.resolve("data/astralsorcery/perks/_full_tree.json"));
    }

    private void savePerkFile(DirectoryCache cache, JsonElement perk, Path filePath) {
        try {
            String perkJson = GSON.toJson(perk);
            String perkHash = HASH_FUNCTION.hashUnencodedChars(perkJson).toString();
            if (!Objects.equals(cache.getPreviousHash(filePath), perkHash) || !Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());

                try (BufferedWriter bufferedwriter = Files.newBufferedWriter(filePath)) {
                    bufferedwriter.write(perkJson);
                }
            }

            cache.recordHash(filePath, perkHash);
        } catch (IOException exc) {
            AstralSorcery.log.error("Couldn't save perk {}", filePath, exc);
        }
    }

    @Override
    public String getName() {
        return "Perks";
    }

    public static class FinishedPerk implements Comparable<FinishedPerk> {

        private final AbstractPerk perk;
        private final List<ResourceLocation> connections;

        public FinishedPerk(AbstractPerk perk, List<ResourceLocation> connections) {
            this.perk = perk;
            this.connections = connections;
        }

        private JsonObject serialize() {
            JsonObject object = this.perk.serializePerk();
            JsonArray array = new JsonArray();
            for (ResourceLocation connection : this.connections) {
                array.add(connection.toString());
            }
            object.add("connection", array);
            return object;
        }

        @Override
        public int compareTo(FinishedPerk that) {
            return this.perk.getRegistryName().compareTo(that.perk.getRegistryName());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FinishedPerk that = (FinishedPerk) o;
            return Objects.equals(perk.getRegistryName(), that.perk.getRegistryName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(perk.getRegistryName());
        }
    }
}
