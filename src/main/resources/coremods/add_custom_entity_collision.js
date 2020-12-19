function initializeCoreMod() {
    return {
        'add_custom_entity_collision': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.entity.Entity',
                'methodName': 'func_223307_a',
                'methodDesc': '(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/AxisAlignedBB;Lnet/minecraft/world/World;Lnet/minecraft/util/math/shapes/ISelectionContext;Lnet/minecraft/util/ReuseableStream;)Lnet/minecraft/util/math/vector/Vector3d;'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'add_custom_entity_collision\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var getAllowedMovementName = ASMAPI.mapMethod('func_213313_a');
                var getAllowedMovement = ASMAPI.findFirstMethodCall(method,
                    ASMAPI.MethodType.STATIC,
                    "net/minecraft/entity/Entity",
                    getAllowedMovementName,
                    "(Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/AxisAlignedBB;Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/shapes/ISelectionContext;Lnet/minecraft/util/ReuseableStream;)Lnet/minecraft/util/math/vector/Vector3d;");

                var ret = getAllowedMovement.getNext();
                method.instructions.insertBefore(ret, new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.insertBefore(ret, ASMAPI.buildMethodCall(
                    'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                    'wrapCustomEntityCollision',
                    '(Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/vector/Vector3d;',
                    ASMAPI.MethodType.STATIC));

                ASMAPI.log('INFO', 'Added \'add_custom_entity_collision\' ASM patch!');
                return method;
            }
        }
    }
}

function findFirstLabel(method) {
    var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

    for (i = 0; i < method.instructions.size(); i++) {
        node = method.instructions.get(i);
        if (node instanceof LabelNode) {
            return node;
        }
    }
    return null;
}
