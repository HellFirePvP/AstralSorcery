/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.data;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonEffectData
 * Created by HellFirePvP
 * Date: 16.02.2019 / 17:28
 */
public class PatreonEffectData {

    private List<EffectEntry> effectList = Lists.newArrayList();

    public List<EffectEntry> getEffectList() {
        return Collections.unmodifiableList(effectList);
    }

    public static class EffectEntry {

        private String uuid;
        private String effectClass;
        private List<String> parameters = Lists.newArrayList();

        public String getUuid() {
            return uuid;
        }

        public String getEffectClass() {
            return effectClass;
        }

        public List<String> getParameters() {
            return parameters;
        }

    }

}
