/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.altar;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarCollectionCategory
 * Created by HellFirePvP
 * Date: 08.09.2020 / 21:39
 */
public class AltarCollectionCategory {

    public static final AltarCollectionCategory HEIGHT          = new AltarCollectionCategory(AstralSorcery.key("height"));
    public static final AltarCollectionCategory FOSIC_FIELD     = new AltarCollectionCategory(AstralSorcery.key("fosic_field"));
    public static final AltarCollectionCategory RELAY           = new AltarCollectionCategory(AstralSorcery.key("relay"));
    public static final AltarCollectionCategory FOCUSED_NETWORK = new AltarCollectionCategory(AstralSorcery.key("focused_network"));

    private final ResourceLocation key;

    public AltarCollectionCategory(ResourceLocation key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AltarCollectionCategory that = (AltarCollectionCategory) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
