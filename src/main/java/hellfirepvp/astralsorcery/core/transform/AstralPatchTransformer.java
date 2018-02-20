/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core.transform;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import hellfirepvp.astralsorcery.core.ASMTransformationException;
import hellfirepvp.astralsorcery.core.ClassPatch;
import hellfirepvp.astralsorcery.core.SubClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralPatchTransformer
 * Created by HellFirePvP
 * Date: 05.12.2016 / 16:46
 */
public class AstralPatchTransformer implements SubClassTransformer {

    private static final String PATCH_PACKAGE = "hellfirepvp.astralsorcery.core.patch";

    private static ClassPatch currentPatch = null;

    private Map<String, List<ClassPatch>> availablePatches = new HashMap<>();

    public AstralPatchTransformer() throws IOException {
        FMLLog.info("[AstralTransformer] Loading patches...");
        int loaded = loadClassPatches();
        FMLLog.info("[AstralTransformer] Initialized! Loaded " + loaded + " class patches!");
    }

    private int loadClassPatches() throws IOException {
        ImmutableSet<ClassPath.ClassInfo> classes =
                ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive(PATCH_PACKAGE);
        List<Class> patchClasses = new LinkedList<>();
        for (ClassPath.ClassInfo info : classes) {
            if(info.getName().startsWith(PATCH_PACKAGE)) {
                patchClasses.add(info.load());
            }
        }
        int load = 0;
        for (Class patchClass : patchClasses) {
            if (ClassPatch.class.isAssignableFrom(patchClass) && !Modifier.isAbstract(patchClass.getModifiers())) {
                try {
                    ClassPatch patch = (ClassPatch) patchClass.newInstance();
                    if(!availablePatches.containsKey(patch.getClassName())) {
                        availablePatches.put(patch.getClassName(), new LinkedList<>());
                    }
                    availablePatches.get(patch.getClassName()).add(patch);
                    load++;
                } catch (Exception exc) {
                    throw new IllegalStateException("Could not load ClassPatch: " + patchClass.getSimpleName(), exc);
                }
            }
        }
        if(load == 0) {
            FMLLog.info("[AstralTransformer] Found 0 Transformers! Trying to recover with direct references...");
            String[] references = new String[] {
                    "hellfirepvp.astralsorcery.core.patch.helper.PatchBlockModify",
                    "hellfirepvp.astralsorcery.core.patch.helper.PatchKnockbackEvent",
                    "hellfirepvp.astralsorcery.core.patch.helper.PatchUpdateElytra"
            };
            for (String str : references) {
                try {
                    ClassPatch c = (ClassPatch) Class.forName(str).newInstance();
                    if(!availablePatches.containsKey(c.getClassName())) {
                        availablePatches.put(c.getClassName(), new LinkedList<>());
                    }
                    availablePatches.get(c.getClassName()).add(c);
                    load++;
                } catch (Exception exc) {
                    FMLLog.warning("Could not load ClassPatch: " + str);
                    exc.printStackTrace();
                }
            }
        }

        return load;
    }

    @Override
    public void transformClassNode(ClassNode cn, String transformedClassName, String obfName) {
        if(!availablePatches.isEmpty()) {
            List<ClassPatch> patches = availablePatches.get(transformedClassName);
            if(patches != null && !patches.isEmpty()) {
                FMLLog.info("[AstralTransformer] Transforming " + obfName + " : " + transformedClassName + " with " + patches.size() + " patches!");
                try {
                    for (ClassPatch patch : patches) {
                        currentPatch = patch;
                        patch.transform(cn);
                        FMLLog.info("[AstralTransformer] Applied patch " + patch.getClass().getSimpleName().toUpperCase());
                        currentPatch = null;
                    }
                } catch (Exception exc) {
                    throw new ASMTransformationException("Applying ClassPatches failed (ClassName: " + obfName + " - " + transformedClassName + ") - Rethrowing exception!", exc);
                }
            }
            availablePatches.remove(transformedClassName);
        }
    }

    @Override
    public String getIdentifier() {
        return "Patch based transformer";
    }

    @Override
    public void addErrorInformation() {
        if(currentPatch != null) {
            FMLLog.warning("Patcher was in active patch: " + currentPatch.getClass().getSimpleName());
        }
    }

    @Override
    public boolean isTransformRequired(String transformedClassName) {
        return availablePatches.containsKey(transformedClassName);
    }

}
