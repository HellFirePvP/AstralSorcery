function initializeCoreMod() {
    return {
        'sun_brightness_client': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.World',
                'methodName': 'getSunBrightnessBody',
                'methodDesc': '(F)F'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'sun_brightness_client\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var fReturn = ASMAPI.findFirstInstructionAfter(method, Opcodes.FRETURN, 0);

                while (fReturn !== null) {
                    method.instructions.insertBefore(fReturn, new VarInsnNode(Opcodes.ALOAD, 0));
                    method.instructions.insertBefore(fReturn, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'overrideSunBrightnessClient',
                        "(FLnet/minecraft/world/World;)F",
                        ASMAPI.MethodType.STATIC));

                    fReturn = ASMAPI.findFirstInstructionAfter(method, Opcodes.FRETURN, method.instructions.indexOf(fReturn) + 2);
                }

                ASMAPI.log('INFO', 'Added \'sun_brightness_client\' ASM patch!');
                return method;
            }
        }
    }
}