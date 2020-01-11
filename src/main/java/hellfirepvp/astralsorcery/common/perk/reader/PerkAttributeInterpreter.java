/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.reader;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeMap;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeInterpreter
 * Created by HellFirePvP
 * Date: 09.08.2019 / 08:01
 */
@OnlyIn(Dist.CLIENT)
public class PerkAttributeInterpreter {

    private Map<PerkAttributeType, PerkAttributeReader> attributeReaderOverrides = Maps.newHashMap();

    private PerkAttributeMap attributeMap;
    private PlayerEntity player;

    private PerkAttributeInterpreter(PerkAttributeMap attributeMap, PlayerEntity player) {
        this.attributeMap = attributeMap;
        this.player = player;
    }

    public static PerkAttributeInterpreter defaultInterpreter(PlayerEntity player) {
        return new Builder(player).build();
    }

    @Nullable
    public PerkStatistic getValue(PerkAttributeType type) {
        if (attributeReaderOverrides.containsKey(type)) {
            return attributeReaderOverrides.get(type).getStatistics(attributeMap, player);
        } else {
            PerkAttributeReader reader = type.getReader();
            if (reader != null) {
                return reader.getStatistics(attributeMap, player);
            }
        }
        return null;
    }

    public static class Builder {

        private PerkAttributeInterpreter reader;

        private Builder(PlayerEntity player) {
            this.reader = new PerkAttributeInterpreter(null, player);
        }

        public static Builder newBuilder(PlayerEntity player) {
            return new Builder(player);
        }

        public Builder overrideAttributeMap(PerkAttributeMap map) {
            this.reader.attributeMap = map;
            return this;
        }

        public Builder overrideReader(PerkAttributeType type, PerkAttributeReader reader) {
            this.reader.attributeReaderOverrides.put(type, reader);
            return this;
        }

        public PerkAttributeInterpreter build() {
            if (this.reader.attributeMap == null) {
                this.reader.attributeMap = PerkAttributeHelper.getOrCreateMap(this.reader.player, LogicalSide.CLIENT);
            }
            return this.reader;
        }

    }

}
