function initializeCoreMod() {
    return {
        'elytra_start_client_flight': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.entity.player.ClientPlayerEntity',
                'methodName': 'func_70636_d',
                'methodDesc': '()V'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'elytra_start_client_flight\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var isElytraUsableName = ASMAPI.mapMethod('func_185069_d');
                var isElytraUsable = ASMAPI.findFirstMethodCall(method,
                        ASMAPI.MethodType.STATIC,
                        'net/minecraft/item/ElytraItem',
                        isElytraUsableName,
                        '(Lnet/minecraft/item/ItemStack;)Z');

                if (isElytraUsable === null) {
                    ASMAPI.log('INFO', 'Failed applying \'elytra_start_client_flight\' ASM patch. Resolving isElytraUsable failed!');
                    return method;
                }

                var getItemStackFromSlotName = ASMAPI.mapMethod('func_184582_a');
                var getItemStackFromSlot = findFirstMethodCallBefore(method,
                        ASMAPI.MethodType.VIRTUAL,
                        'net/minecraft/client/entity/player/ClientPlayerEntity',
                        getItemStackFromSlotName,
                        '(Lnet/minecraft/inventory/EquipmentSlotType;)Lnet/minecraft/item/ItemStack;',
                        method.instructions.indexOf(isElytraUsable));

                if (getItemStackFromSlot === null) {
                    ASMAPI.log('INFO', 'Failed applying \'elytra_start_client_flight\' ASM patch. Resolving previous getItemStackFromSlot failed!');
                    return method;
                }

                method.instructions.insert(getItemStackFromSlot, ASMAPI.buildMethodCall(
                    'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                    'transformElytraItem',
                    '(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;',
                    ASMAPI.MethodType.STATIC));
                method.instructions.insert(getItemStackFromSlot, new VarInsnNode(Opcodes.ALOAD, 0));

                ASMAPI.log('INFO', 'Added \'elytra_start_client_flight\' ASM patch!');
                return method;
            }
        }
    }
}

function findFirstMethodCallBefore(method, methodType, owner, name, descriptor, startIndex) {
    var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');

    for (i = 0; i < Math.min(method.instructions.size() - 1, startIndex); i++) {
        node = method.instructions.get(i);
        if (node instanceof MethodInsnNode && node.getOpcode() == methodType.toOpcode()) {
            if (node.owner === owner &&
                    node.name === name &&
                    node.desc === descriptor) {
                return node;
            }
        }
    }
    return null;
}