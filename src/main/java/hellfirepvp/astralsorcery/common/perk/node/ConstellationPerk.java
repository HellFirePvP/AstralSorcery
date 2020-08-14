/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.data.PerkTypeHandler;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreeConstellation;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerk
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:31
 */
public class ConstellationPerk extends AttributeModifierPerk {

    private IConstellation constellation;

    public ConstellationPerk(ResourceLocation name, IConstellation cst, float x, float y) {
        super(name, x, y);
        this.setConstellation(cst);
    }

    public static ConstellationPerk convertToThis(ResourceLocation perkKey, float x, float y) {
        return new ConstellationPerk(perkKey, null, x, y);
    }

    @Override
    protected PerkTreePoint<? extends ConstellationPerk> initPerkTreePoint() {
        Validate.notNull(this.constellation);
        return new PerkTreeConstellation<>(this, getOffset(),
                this.constellation, PerkTreeConstellation.MINOR_SPRITE_SIZE);
    }

    public void setConstellation(IConstellation constellation) {
        Validate.isTrue(this.constellation == null);
        Validate.notNull(constellation);
        this.constellation = constellation;
    }

    public IConstellation getConstellation() {
        Validate.notNull(this.constellation);
        return this.constellation;
    }

    @Override
    public void deserializeData(JsonObject perkData) {
        super.deserializeData(perkData);

        this.constellation = null;

        if (perkData.has("constellation")) {
            String cstKey = JSONUtils.getString(perkData, "constellation");
            IConstellation cst = ConstellationRegistry.getConstellation(new ResourceLocation(cstKey));
            if (cst == null) {
                throw new JsonParseException("Unknown constellation: " + cstKey);
            }
            this.setConstellation(cst);
        }
    }

    @Override
    public void serializeData(JsonObject perkData) {
        super.serializeData(perkData);

        if (this.constellation != null) {
            perkData.addProperty("constellation", this.constellation.getRegistryName().toString());
        }
    }
}
