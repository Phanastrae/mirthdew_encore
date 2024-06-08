package phanastrae.mirthlight_encore.render.shader;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthlight_encore.MirthlightEncore;

import java.io.IOException;

public class MirthlightEncoreShaders {
    public static final String dreamtwirlBarrierID = "rendertype_dreamtwirl_barrier";

    @Nullable
    private static ShaderProgram dreamtwirlBarrierShader;

    @Nullable
    public static ShaderProgram getDreamtwirlBarrierShader() {
        return dreamtwirlBarrierShader;
    }

    public static void registerShaders(CoreShaderRegistrationCallback.RegistrationContext registrationCallback) throws IOException {
        registrationCallback.register(MirthlightEncore.id(dreamtwirlBarrierID), VertexFormats.POSITION, shaderProgram -> MirthlightEncoreShaders.dreamtwirlBarrierShader = shaderProgram);
    }
}
