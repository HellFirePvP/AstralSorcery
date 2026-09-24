/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.patreon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.patreon.type.TypeFlare;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatreonDataManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatreonDataManager {

    private static final String PATREON_EFFECT_URL = "https://hellfiredev.net/rewards.json";
    private static final Gson GSON = new GsonBuilder().create();

    public static void loadPatreonEffects() {
        Thread tr = new Thread(() -> {
            loadData();
            PatreonEffectHelper.loadingFinished = true;
        });
        tr.setName("Astral Sorcery Patreon Data Loader");
        tr.setDaemon(true);
        tr.start();
    }

    private static void loadData() {
        URLConnection conn;
        try {
            conn = URI.create(PATREON_EFFECT_URL).toURL().openConnection();
        } catch (IOException exc) {
            AstralSorcery.LOG.error("Failed to download patreon data file.", exc);
            return;
        }

        PatreonData data;
        try (var reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            data = GSON.fromJson(reader, PatreonData.class);
        } catch (IOException exc) {
            AstralSorcery.LOG.error("Failed to download patreon data file.", exc);
            return;
        }

        int skipped = 0;
        for (PatreonData.EffectEntry effect : data.getEffectList()) {
            UUID playerUUID;
            PatreonEffectType effectType;
            try {
                playerUUID = UUID.fromString(effect.getUuid());
                effectType = PatreonEffectType.valueOf(effect.getEffectClass());
            } catch (Exception exc) {
                skipped++;
                continue;
            }

            try {
                PatreonEffect pe = effectType.getProvider().buildEffect(playerUUID, effect.getParameters());

                pe.initialize();
                pe.attachEventListeners(NeoForge.EVENT_BUS);
                PatreonEffectHelper.playerEffectMap.computeIfAbsent(playerUUID, uuid -> new ArrayList<>()).add(pe);
                PatreonEffectHelper.effectMap.put(pe.getEffectUUID(), pe);
            } catch (Exception exc) {
                skipped++;
            }
        }

        if (!FMLLoader.isProduction()) {
            //UUID hellfire = UUID.fromString("7f6971c5-fb58-4519-a975-b1b5766e92d1");
            UUID dev = UUID.fromString("380df991-f603-344c-a090-369bad2a924a");
            PatreonEffect effect = new TypeFlare(UUID.fromString("17f84a37-6d9d-4ad2-8b85-ac333390f6f2"), FlareColor.STANDARD);

            effect.attachEventListeners(NeoForge.EVENT_BUS);
            PatreonEffectHelper.playerEffectMap.computeIfAbsent(dev, uuid -> new ArrayList<>()).add(effect);
            PatreonEffectHelper.effectMap.put(effect.getEffectUUID(), effect);
        }

        if (skipped > 0) {
            AstralSorcery.LOG.warn("Skipped loading {} patreon effects due to errors.", skipped);
        }
        AstralSorcery.LOG.info("Finished loading patreon effects.");
    }
}
