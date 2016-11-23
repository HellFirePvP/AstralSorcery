package hellfirepvp.astralsorcery.common.constellation.perk;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerk
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:03
 */
public class ConstellationPerk {

    private final String unlocName;
    private final String unlocInfo;
    private int id = -1;

    public ConstellationPerk(String name) {
        this.unlocName = "perk." + name;
        this.unlocInfo = unlocName + ".info";
    }

    void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUnlocalizedName() {
        return unlocName;
    }

    public String getUnlocalizedDescription() {
        return unlocInfo;
    }

    @SideOnly(Side.CLIENT)
    public void addLocalizedDescription(List<String> tooltip) {
        tooltip.add(I18n.format(getUnlocalizedDescription()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstellationPerk that = (ConstellationPerk) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
