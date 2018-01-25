/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.models.obj;

import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OBJModelLibrary
 * Created by HellFirePvP
 * Date: 16.09.2016 / 15:36
 */
public class OBJModelLibrary {

    public static final WavefrontObject bigCrystal = load("crystal_big");

    public static final WavefrontObject crystalsStage0 = load("c_crystal0");
    public static final WavefrontObject crystalsStage1 = load("c_crystal1");
    public static final WavefrontObject crystalsStage2 = load("c_crystal2");
    public static final WavefrontObject crystalsStage3 = load("c_crystal3");
    public static final WavefrontObject crystalsStage4 = load("c_crystal4");

    private static WavefrontObject load(String name) {
        return AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, name);
    }

    public static void init() {} //To invoke static initializer for fields

}
