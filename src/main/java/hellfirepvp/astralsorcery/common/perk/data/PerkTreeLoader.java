package hellfirepvp.astralsorcery.common.perk.data;

import com.google.gson.*;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeLoader
 * Created by HellFirePvP
 * Date: 12.08.2020 / 22:15
 */
public class PerkTreeLoader extends JsonReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final PerkTreeLoader INSTANCE = new PerkTreeLoader();

    private PerkTreeLoader() {
        super(GSON, "perks");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> object, IResourceManager resourceManager, IProfiler profiler) {
        PerkTreeData newTree = new PerkTreeData();

        for(Map.Entry<ResourceLocation, JsonObject> entry : object.entrySet()) {
            ResourceLocation perkKey = entry.getKey();
            if (perkKey.getPath().startsWith("_")) {
                continue;
            }

            JsonObject serializedPerkData = entry.getValue();

            ResourceLocation customClass = PerkTypeHandler.DEFAULT.getKey();
            if (serializedPerkData.has("perk_class")) {
                customClass = new ResourceLocation(JSONUtils.getString(serializedPerkData, "perk_class"));
                if (!PerkTypeHandler.hasCustomType(customClass)) {
                    throw new JsonParseException("Unknown perk_class: " + customClass.toString());
                }
            }

            float posX = JSONUtils.getFloat(serializedPerkData, "x");
            float posY = JSONUtils.getFloat(serializedPerkData, "y");

            AbstractPerk perk = PerkTypeHandler.convert(perkKey, posX, posY, customClass);
            if (serializedPerkData.has("name")) {
                String name = JSONUtils.getString(serializedPerkData, "name");
                perk.setName(name);
            }

            if (serializedPerkData.has("data")) {
                JsonObject perkData = JSONUtils.getJsonObject(serializedPerkData, "data");
                perk.deserializeData(perkData);
            }

            ConnectedPerkData connector = newTree.addPerk(perk);
            if (serializedPerkData.has("connection")) {
                JsonArray connectionArray = JSONUtils.getJsonArray(serializedPerkData, "connection");
                for (int i = 0; i < connectionArray.size(); i++) {
                    JsonElement connection = connectionArray.get(i);
                    String connectedPerkKey = JSONUtils.getString(connection, String.format("connection[%s]", i));
                    connector.addConnection(new ResourceLocation(connectedPerkKey));
                }
            }
        }

        AstralSorcery.log.info("Loaded {} perks", newTree.getLoadedPerks().size());

        PerkTree.PERK_TREE.updatePerkTree(newTree);
    }
}
