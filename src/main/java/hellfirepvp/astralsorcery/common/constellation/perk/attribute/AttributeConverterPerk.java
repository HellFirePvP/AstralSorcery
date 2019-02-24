/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.constellation.perk.ProgressGatedPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IConverterProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeConverterPerk
 * Created by HellFirePvP
 * Date: 03.08.2018 / 07:35
 */
public abstract class AttributeConverterPerk extends ProgressGatedPerk implements IConverterProvider {

    private List<PerkConverter> converters = Lists.newArrayList();

    public AttributeConverterPerk(String name, int x, int y) {
        super(name, x, y);
    }

    public AttributeConverterPerk(ResourceLocation name, int x, int y) {
        super(name, x, y);
    }

    public <T> T addConverter(PerkConverter converter) {
        this.converters.add(converter);
        return (T) this;
    }

    public <T> T addRangedConverter(double radius, PerkConverter converter) {
        this.converters.add(converter.asRangedConverter(new Point.Double(this.getOffset().getX(), this.getOffset().getY()), radius));
        return (T) this;
    }

    @Override
    public List<PerkConverter> provideConverters(EntityPlayer player, Side side) {
        if (modifiersDisabled(player, side)) {
            return Collections.emptyList();
        }

        return ImmutableList.copyOf(converters);
    }

    public List<PerkConverter> getConverters() {
        return Collections.unmodifiableList(this.converters);
    }

    @Override
    public void applyPerkLogic(EntityPlayer player, Side side) {}

    @Override
    public void removePerkLogic(EntityPlayer player, Side side) {}
}
