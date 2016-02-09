package hellfire.astralSorcery.common.constellation;

import hellfire.astralSorcery.api.constellation.IConstellation;
import hellfire.astralSorcery.api.constellation.IConstellationTier;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 21:36
 */
public class Tier implements IConstellationTier {

    private final RInformation renderinfo;
    private final int tierNumber;
    private final float chance;
    private LinkedList<IConstellation> constellations = new LinkedList<IConstellation>();

    public Tier(int tierNumber, RInformation renderinfo, float chance) {
        this.tierNumber = tierNumber;
        this.renderinfo = renderinfo;
        this.chance = chance;
    }

    @Override
    public int tierNumber() {
        return tierNumber;
    }

    @Override
    public void addConstellation(IConstellation constellation) {
        this.constellations.addLast(constellation);
    }

    @Override
    public List<IConstellation> getConstellations() {
        return Collections.unmodifiableList(constellations);
    }

    @Override
    public RInformation getRenderInformation() {
        return renderinfo;
    }

    @Override
    public Color calcRenderColor() {
        float perc = ((float) tierNumber) / ((float) ConstellationRegistry.getHighestTierNumber());
        return new Color(Color.HSBtoRGB((230F + (50F * perc)) / 360F, (70F + 20F * perc) / 100F, 0.8F));
    }

    @Override
    public float getShowupChance() {
        return chance;
    }
}
