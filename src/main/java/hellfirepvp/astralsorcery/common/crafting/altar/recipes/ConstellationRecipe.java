package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationRecipe
 * Created by HellFirePvP
 * Date: 17.10.2016 / 22:22
 */
public class ConstellationRecipe extends AttenuationRecipe {

    private Constellation skyConstellationNeeded = null;

    protected ConstellationRecipe(TileAltar.AltarLevel neededLevel, IAccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    protected ConstellationRecipe(TileAltar.AltarLevel neededLevel, AbstractCacheableRecipe recipe) {
        super(neededLevel, recipe);
    }

    public ConstellationRecipe(AbstractCacheableRecipe recipe) {
        this(recipe.make());
    }

    public ConstellationRecipe(IAccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.CONSTELLATION_CRAFT, recipe);
        setPassiveStarlightRequirement(3700);
    }

    @Override
    public int craftingTickTime() {
        return 2000;
    }

    public void setSkyConstellation(Constellation constellation) {
        this.skyConstellationNeeded = constellation;
    }

    @Override
    public boolean matches(TileAltar altar) {
        if(skyConstellationNeeded != null) {
            DataActiveCelestials cel = SyncDataHolder.getDataServer(SyncDataHolder.DATA_CONSTELLATIONS);
            if(!cel.getActiveConstellations().contains(skyConstellationNeeded)) return false;
        }
        return super.matches(altar);
    }
}
