package phanastrae.mirthdew_encore.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.duck.PlayerEntityDuckInterface;
import phanastrae.mirthdew_encore.entity.MirthdewEncorePlayerEntityAttachment;

import static phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerEntityDuckInterface {

    @Unique
    private MirthdewEncorePlayerEntityAttachment mirthdew_encore$playerEntityAttachment;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mirthdew_encore$init(Level world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        this.mirthdew_encore$playerEntityAttachment = new MirthdewEncorePlayerEntityAttachment((Player)(Object)this);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void mirthdew_encore$tick(CallbackInfo ci) {
        this.mirthdew_encore$playerEntityAttachment.tick();
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void mirthdew_encore$writeNbt(CompoundTag nbt, CallbackInfo ci) {
        if(nbt.contains("MirthdewEncore", Tag.TAG_COMPOUND)) {
            CompoundTag nbtCompound = nbt.getCompound("MirthdewEncore");
            this.mirthdew_encore$playerEntityAttachment.writeNbt(nbtCompound);
        } else {
            CompoundTag nbtCompound = new CompoundTag();
            this.mirthdew_encore$playerEntityAttachment.writeNbt(nbtCompound);
            nbt.put("MirthdewEncore", nbtCompound);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void mirthdew_encore$readNbt(CompoundTag nbt, CallbackInfo ci) {
        if(nbt.contains("MirthdewEncore", Tag.TAG_COMPOUND)) {
            CompoundTag nbtCompound = nbt.getCompound("MirthdewEncore");
            this.mirthdew_encore$playerEntityAttachment.readNbt(nbtCompound);
        }
    }

    @Inject(method = "canEat", at = @At("HEAD"), cancellable = true)
    private void mirthdew_encore$canAlwaysConsumeIfDreamyDieting(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
        if (this.hasEffect(DREAMY_DIET_ENTRY)) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public MirthdewEncorePlayerEntityAttachment mirthdew_encore$getAttachment() {
        return mirthdew_encore$playerEntityAttachment;
    }
}
