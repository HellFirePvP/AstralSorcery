/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraftforge.common.MinecraftForge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonDataManager
 * Created by HellFirePvP
 * Date: 30.08.2019 / 23:23
 */
public class PatreonDataManager {

    private static final String PATREON_EFFECT_URL = "http://hellfiredev.net/patreon.json";
    private static final Gson GSON = new GsonBuilder().create();

    public static void loadPatreonEffects() {
        Thread tr = new Thread(() -> {
            URLConnection conn;
            try {
                conn = new URL(PATREON_EFFECT_URL).openConnection();
            } catch (IOException e) {
                AstralSorcery.log.error("Failed to connect to patreon fileserver! Not loading patreon files...");
                e.printStackTrace();

                PatreonEffectHelper.loadingFinished = true;
                return;
            }

            PatreonData data;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                data = GSON.fromJson(br, PatreonData.class);
            } catch (IOException e) {
                AstralSorcery.log.error("Failed to connect to patreon fileserver! Not loading patreon files...");
                e.printStackTrace();

                PatreonEffectHelper.loadingFinished = true;
                return;
            }

            int skipped = 0;
            for (PatreonData.EffectEntry entry : data.getEffectList()) {
                UUID plUuid;
                PatreonEffectType type;
                try {
                    plUuid = UUID.fromString(entry.getUuid());
                    type = PatreonEffectType.valueOf(entry.getEffectClass());
                } catch (Exception exc) {
                    skipped++;
                    continue;
                }

                try {
                    PatreonEffect pe = type.getProvider().buildEffect(plUuid, entry.getParameters());

                    pe.initialize();
                    pe.attachEventListeners(MinecraftForge.EVENT_BUS);
                    pe.attachTickListeners(AstralSorcery.getProxy().getTickManager()::register);
                    PatreonEffectHelper.playerEffectMap.computeIfAbsent(plUuid, uuid -> new ArrayList<>()).add(pe);
                    PatreonEffectHelper.effectMap.put(pe.getEffectUUID(), pe);
                } catch (Exception exc) {
                    skipped++;
                }
            }

            if (skipped > 0) {
                AstralSorcery.log.warn("Skipped " + skipped + " patreon effects during loading due to malformed data!");
            }
            AstralSorcery.log.info("Patreon effect loading finished.");

            UUID hellfire = UUID.fromString("7f6971c5-fb58-4519-a975-b1b5766e92d1");
            try {
                /*PatreonEffect effect = PatreonEffectType.CRYSTAL_FOOTPRINTS.getProvider().buildEffect(hellfire,
                        Arrays.asList("777971c5-fb58-4519-a975-b1b5766e44d1",
                                "DARK_GREEN",
                                "7865553"));
                effect.initialize();
                effect.attachEventListeners(MinecraftForge.EVENT_BUS);
                effect.attachTickListeners(AstralSorcery.getProxy().getTickManager()::register);
                PatreonEffectHelper.playerEffectMap.computeIfAbsent(hellfire, uuid -> new ArrayList<>()).add(effect);
                PatreonEffectHelper.effectMap.put(effect.getEffectUUID(), effect);*/
            } catch (Exception e) {
                e.printStackTrace();
            }

            PatreonEffectHelper.loadingFinished = true;
        });
        tr.setName("AstralSorcery Patreon Effect Loader");
        tr.start();
    }
}
