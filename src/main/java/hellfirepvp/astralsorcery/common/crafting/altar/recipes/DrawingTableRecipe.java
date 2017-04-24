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
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DrawingTableRecipe
 * Created by HellFirePvP
 * Date: 24.04.2017 / 08:30
 */
public class DrawingTableRecipe extends ConstellationRecipe {

    public DrawingTableRecipe() {
        super(new ShapedRecipe(BlocksAS.drawingTable)
                .addPart(ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_RIGHT));
        setAttItem(OreDictAlias.ITEM_DYE_ALL,
                AttunementAltarSlot.UPPER_LEFT,
                AttunementAltarSlot.UPPER_RIGHT);
        setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(),
                AttunementAltarSlot.LOWER_LEFT,
                AttunementAltarSlot.LOWER_RIGHT);
        setCstItem(OreDictAlias.BLOCK_WOOD_LOGS,
                ConstellationAtlarSlot.UP_LEFT_LEFT,
                ConstellationAtlarSlot.DOWN_LEFT_LEFT,
                ConstellationAtlarSlot.UP_RIGHT_RIGHT,
                ConstellationAtlarSlot.DOWN_RIGHT_RIGHT);
    }

    @Override
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if(state == ActiveCraftingTask.CraftingState.ACTIVE) {
            Vector3 altarPos = new Vector3(altar);
            for (int i = 0; i < 2; i++) {
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                        altarPos.getX() - 3 + rand.nextFloat() * 7,
                        altarPos.getY(),
                        altarPos.getZ() - 3 + rand.nextFloat() * 7
                );
                p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setColor(new Color(Color.HSBtoRGB(rand.nextFloat() * 360, 1F, 1F)));
            }

            if(rand.nextInt(10) == 0) {
                Vector3 from = new Vector3(altar).add(0.5, 0.3, 0.5);
                MiscUtils.applyRandomOffset(from, rand, 0.4F);
                EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(4 + rand.nextInt(2)), from, 1);
                lightbeam.setMaxAge(64);
                lightbeam.setColorOverlay(new Color(Color.HSBtoRGB(rand.nextFloat() * 360, 1F, 1F)));
            }
        }
    }

}
