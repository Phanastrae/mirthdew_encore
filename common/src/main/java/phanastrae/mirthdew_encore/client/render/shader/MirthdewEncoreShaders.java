package phanastrae.mirthdew_encore.client.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;

import java.io.IOException;
import java.util.function.Consumer;

public class MirthdewEncoreShaders {
    public static final String dreamtwirlBarrierID = "rendertype_dreamtwirl_barrier";

    @Nullable
    private static ShaderInstance dreamtwirlBarrierShader;

    @Nullable
    public static ShaderInstance getDreamtwirlBarrierShader() {
        return dreamtwirlBarrierShader;
    }

    public static void registerShaders(RegistrationContext registrationCallback) throws IOException {
        registrationCallback.register(MirthdewEncore.id(dreamtwirlBarrierID), DefaultVertexFormat.POSITION, shaderProgram -> MirthdewEncoreShaders.dreamtwirlBarrierShader = shaderProgram);
    }

    @FunctionalInterface
    public interface RegistrationContext {
        void register(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback) throws IOException;
    }
}
