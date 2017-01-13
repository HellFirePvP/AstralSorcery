/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.INighttimeRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.init.Items;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttunementAltarRecipe
 * Created by HellFirePvP
 * Date: 07.12.2016 / 13:47
 */
public class AttunementAltarRecipe extends AttunementRecipe implements INighttimeRecipe {

    private static Vector3[] offsetPillars = new Vector3[] {
        new Vector3( 3, 2,  3),
        new Vector3(-3, 2,  3),
        new Vector3( 3, 2, -3),
        new Vector3(-3, 2, -3)
    };

    public AttunementAltarRecipe() {
        super(new ShapedRecipe(BlocksAS.attunementAltar)
                .addPart(ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(BlocksAS.attunementRelay,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT));
        setAttItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                AltarSlot.UPPER_LEFT,
                AltarSlot.UPPER_RIGHT);
        setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(),
                AltarSlot.LOWER_LEFT,
                AltarSlot.LOWER_RIGHT);
    }

    @Override
    public void onCraftClientTick(TileAltar altar, long tick, Random rand) {
        super.onCraftClientTick(altar, tick, rand);

        Vector3 randomPos = new Vector3(altar);
        randomPos.add(rand.nextFloat() * 7 - 3, 0.1, rand.nextFloat() * 7 - 3);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(randomPos.getX(), randomPos.getY(), randomPos.getZ());
        p.scale(0.7F).gravity(0.02);
        if(tick % 50 == 0) {
            Vector3 vec = new Vector3(altar).add(0.5, 0.5, 0.5);
            for (Vector3 offset : offsetPillars) {
                EffectHandler.getInstance().lightbeam(offset.clone().add(altar.getPos()).add(0.5, 0.5, 0.5), vec, 1.2F);
                        //.setColorOverlay(127F / 255F, 127F / 255F, 1F, 1F);
            }
        }

        if(rand.nextInt(10) == 0) {
            Vector3 from = new Vector3(altar).add(0.5, -0.6, 0.5);
            MiscUtils.applyRandomOffset(from, rand, 1.8F);
            from.setY(altar.getPos().getY() - 0.6 + 1 * rand.nextFloat() * (rand.nextBoolean() ? 1 : -1));
            EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(5 + rand.nextInt(3)), from, 1);
            lightbeam.setMaxAge(64);
        }
    }
}
