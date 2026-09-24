/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.patreon;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatreonData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatreonData {

    private final List<EffectEntry> effectList = new ArrayList<>();

    public List<EffectEntry> getEffectList() {
        return this.effectList;
    }

    public static class EffectEntry {

        private String uuid;
        private String effectClass;
        private final List<String> parameters = new ArrayList<>();

        public String getUuid() {
            return this.uuid;
        }

        public String getEffectClass() {
            return this.effectClass;
        }

        public List<String> getParameters() {
            return this.parameters;
        }
    }
}
