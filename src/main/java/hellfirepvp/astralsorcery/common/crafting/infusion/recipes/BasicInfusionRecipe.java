/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.infusion.recipes;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BasicInfusionRecipe
 * Created by HellFirePvP
 * Date: 11.12.2016 / 18:11
 */
public class BasicInfusionRecipe extends AbstractInfusionRecipe {

    public BasicInfusionRecipe(ItemStack output, String oreDictInput) {
        this(output, new ItemHandle(oreDictInput));
    }

    public BasicInfusionRecipe(ItemStack output, ItemStack input) {
        this(output, new ItemHandle(input));
    }

    public BasicInfusionRecipe(ItemStack output, ItemHandle input) {
        super(output, input);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileStarlightInfuser infuser, int tick, Random rand) {
        super.onCraftClientTick(infuser, tick, rand);

        if(rand.nextInt(13) == 0) {
            Vector3 from = new Vector3(infuser).add(0.5, 0.3, 0.5);
            MiscUtils.applyRandomOffset(from, rand, 0.4F);
            EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(4 + rand.nextInt(2)), from, 1);
            lightbeam.setMaxAge(64);
        }
    }
}
