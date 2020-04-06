/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ObjModelRender
 * Created by HellFirePvP
 * Date: 05.04.2020 / 10:59
 */
public class ObjModelRender {

    private static WavefrontObject crystalModel;
    private static int crystalDList = -1;

    private static WavefrontObject celestialWingsModel;
    private static int celestialWingsDList = -1;

    private static WavefrontObject wraithWingsModel;
    private static int wraithWingsDList = -1;

    public static void renderCrystal() {
        if (crystalModel == null) {
            crystalModel = AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, "crystal");
        }
        if (crystalDList == -1) {
            crystalDList = GLAllocation.generateDisplayLists(1);
            GlStateManager.newList(crystalDList, GL11.GL_COMPILE);
            crystalModel.renderAll(true);
            GlStateManager.endList();
        }
        GlStateManager.callList(crystalDList);
    }

    public static void renderCelestialWings() {
        if (celestialWingsModel == null) {
            celestialWingsModel = AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, "celestial_wings");
        }
        if (celestialWingsDList == -1) {
            celestialWingsDList = GLAllocation.generateDisplayLists(1);
            GlStateManager.newList(celestialWingsDList, GL11.GL_COMPILE);
            celestialWingsModel.renderAll(true);
            GlStateManager.endList();
        }
        GlStateManager.callList(celestialWingsDList);
    }

    public static void renderWraithWings() {
        if (wraithWingsModel == null) {
            wraithWingsModel = AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, "wraith_wings");
        }

        if (wraithWingsDList == -1) {
            wraithWingsDList = GLAllocation.generateDisplayLists(2);
            GlStateManager.newList(wraithWingsDList, GL11.GL_COMPILE);
            wraithWingsModel.renderOnly(true, "Bones");
            GlStateManager.endList();
            GlStateManager.newList(wraithWingsDList + 1, GL11.GL_COMPILE);
            wraithWingsModel.renderOnly(true, "Wing");
            GlStateManager.endList();
        }
        GlStateManager.color4f(0.3F, 0.3F, 0.3F, 1F);
        GlStateManager.callList(wraithWingsDList);
        GlStateManager.color4f(0F, 0F, 0F, 1F);
        GlStateManager.callList(wraithWingsDList + 1);
        GlStateManager.color4f(1F, 1F, 1F, 1F);
    }
}
