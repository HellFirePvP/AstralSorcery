/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration;

import hellfirepvp.astralsorcery.common.base.Mods;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IntegrationCurios
 * Created by HellFirePvP
 * Date: 12.08.2019 / 06:50
 */
public class IntegrationCurios {

    public static void initIMC() {
        InterModComms.sendTo(Mods.CURIOS.getModId(), CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("necklace"));
    }

    public static Optional<ImmutableTriple<String, Integer, ItemStack>> getCurio(PlayerEntity player, Predicate<ItemStack> match) {
        return CuriosAPI.getCurioEquipped(match, player);
    }

}
