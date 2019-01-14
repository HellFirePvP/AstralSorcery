/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.reader;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerAttributeReader
 * Created by HellFirePvP
 * Date: 05.01.2019 / 13:37
 */
public class PlayerAttributeReader {

    private PlayerAttributeMap attributeMap;
    private EntityPlayer player;
    private Side side;

    private PlayerAttributeReader(PlayerAttributeMap attributeMap, EntityPlayer player, Side side) {
        this.attributeMap = attributeMap;
        this.player = player;
        this.side = side;
    }

    public static PlayerAttributeReader defaultReader(EntityPlayer player, Side side) {
        return new PlayerAttributeReader(PerkAttributeHelper.getOrCreateMap(player, side), player, side);
    }

    public static class Builder {

        private PlayerAttributeReader reader;

        private Builder(EntityPlayer player, Side side) {
            this.reader = new PlayerAttributeReader(PerkAttributeHelper.getOrCreateMap(player, side), player, side);
        }

        public static Builder newBuilder(EntityPlayer player, Side side) {
            return new Builder(player, side);
        }

        public Builder overrideAttributeMap(PlayerAttributeMap map) {
            this.reader.attributeMap = map;
            return this;
        }

        public PlayerAttributeReader build() {
            return this.reader;
        }

    }

}
