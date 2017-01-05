/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemWellCatalyst
 * Created by HellFirePvP
 * Date: 18.10.2016 / 13:06
 */
public interface ItemWellCatalyst {

    public boolean isCatalyst(@Nonnull ItemStack stack);

    public double collectionMultiplier(@Nonnull ItemStack stack);

    public Color getCatalystColor(@Nonnull ItemStack stack);

    public double getShatterChanceMultiplier(@Nonnull ItemStack stack);

}
