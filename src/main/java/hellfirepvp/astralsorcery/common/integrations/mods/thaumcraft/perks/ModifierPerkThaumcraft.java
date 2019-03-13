/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.thaumcraft.perks;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeModifierPerk;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModifierPerkThaumcraft
 * Created by HellFirePvP
 * Date: 18.11.2018 / 22:27
 */
public class ModifierPerkThaumcraft extends AttributeModifierPerk {

    public ModifierPerkThaumcraft(String name, int x, int y) {
        super(name, x, y);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public Collection<String> getSource() {
        return Lists.newArrayList(I18n.format("perk.astralsorcery.compat.thaumcraft"));
    }

}
