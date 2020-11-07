/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ICameraTransformer
 * Created by HellFirePvP
 * Date: 02.12.2019 / 19:50
 */
public interface ICameraTransformer {

    public int getPriority();

    public void onClientTick();

    public ICameraPersistencyFunction getPersistencyFunction();

    public void onStartTransforming(float pTicks);

    public void onStopTransforming(float pTicks);

    public void transformRenderView(float pTicks);

}
