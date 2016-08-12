package hellfirepvp.astralsorcery.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
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
 * Class: AstralTransformer
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:56
 */
public class AstralTransformer extends AccessTransformer {

    private static final String PATCH_PACKAGE = "hellfirepvp.astralsorcery.core.patch";

    private Map<String, List<ClassPatch>> availablePatches = new HashMap<>();

    public AstralTransformer() throws IOException {
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
        return load;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(!availablePatches.isEmpty()) {
            List<ClassPatch> patches = availablePatches.get(transformedName);
            if(patches != null && !patches.isEmpty()) {
                FMLLog.info("[AstralTransformer] Transforming " + name + " : " + transformedName + " with " + patches.size() + " patches!");
                try {
                    for (ClassPatch patch : patches) {
                        bytes = patch.transform(bytes);
                        FMLLog.info("[AstralTransformer] Applied patch " + patch.getClass().getSimpleName().toUpperCase());
                    }
                } catch (Exception exc) {
                    throw new ASMTransformationException("Applying ClassPatches failed (ClassName: " + name + " - " + transformedName + ") - Rethrowing exception!");
                }
            }
            availablePatches.remove(transformedName);
        }
        return bytes;
    }

}
