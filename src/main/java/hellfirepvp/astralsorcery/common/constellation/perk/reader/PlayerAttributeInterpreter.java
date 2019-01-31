/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.reader;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerAttributeInterpreter
 * Created by HellFirePvP
 * Date: 05.01.2019 / 13:37
 */
@SideOnly(Side.CLIENT)
public class PlayerAttributeInterpreter {

    private Map<String, AttributeReader> attributeReaderOverrides = Maps.newHashMap();

    private PlayerAttributeMap attributeMap;
    private EntityPlayer player;

    private PlayerAttributeInterpreter(PlayerAttributeMap attributeMap, EntityPlayer player) {
        this.attributeMap = attributeMap;
        this.player = player;
    }

    public static PlayerAttributeInterpreter defaultInterpreter(EntityPlayer player) {
        return new Builder(player).build();
    }

    @Nullable
    public PerkStatistic getValue(PerkAttributeType type) {
        return getValue(type.getTypeString());
    }

    @Nullable
    public PerkStatistic getValue(String typeString) {
        if (attributeReaderOverrides.containsKey(typeString)) {
            return attributeReaderOverrides.get(typeString).getStatistics(attributeMap, player);
        } else {
            AttributeReader reader = AttributeReaderRegistry.getReader(typeString);
            if (reader != null) {
                return reader.getStatistics(attributeMap, player);
            }
        }
        return null;
    }

    public static class Builder {

        private PlayerAttributeInterpreter reader;

        private Builder(EntityPlayer player) {
            this.reader = new PlayerAttributeInterpreter(null, player);
        }

        public static Builder newBuilder(EntityPlayer player) {
            return new Builder(player);
        }

        public Builder overrideAttributeMap(PlayerAttributeMap map) {
            this.reader.attributeMap = map;
            return this;
        }

        public Builder overrideReader(String attributeTypeString, AttributeReader reader) {
            this.reader.attributeReaderOverrides.put(attributeTypeString, reader);
            return this;
        }

        public PlayerAttributeInterpreter build() {
            if (this.reader.attributeMap == null) {
                this.reader.attributeMap = PerkAttributeHelper.getOrCreateMap(this.reader.player, Side.CLIENT);
            }
            return this.reader;
        }

    }

}
