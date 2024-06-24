package phanastrae.mirthdew_encore.entity;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.MathHelper;
import phanastrae.mirthdew_encore.duck.HungerManagerDuckInterface;

import static phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY;

public class PlayerEntityHungerData {

    private final PlayerEntity player;

    private boolean isDreamyDieting = false;
    private int preDreamyDietFoodLevel = 20;
    private int dreamyDietTicks = 0;

    public PlayerEntityHungerData(PlayerEntity player) {
        this.player = player;
    }

    public void tick() {
        if(!this.player.getWorld().isClient()) {
            if (this.isDreamyDieting()) {
                if (!player.hasStatusEffect(DREAMY_DIET_ENTRY)) {
                    this.onEndDreamyDieting();
                } else {
                    player.getHungerManager().setFoodLevel(20);
                }

                StatusEffectInstance dietEffect = this.player.getStatusEffect(DREAMY_DIET_ENTRY);
                if (dietEffect != null) {
                    if(!player.isSpectator() && !player.getAbilities().creativeMode) {
                        int amplifier = dietEffect.getAmplifier();
                        if (amplifier >= 0) {
                            this.dreamyDietTicks += (amplifier + 1);
                        }
                    }
                }
            }
            HungerManager hungerManager = player.getHungerManager();
            if (hungerManager.getFoodLevel() > 0 && this.getFoodLevelDebt() > 0) {
                hungerManager.setFoodLevel(hungerManager.getFoodLevel() - 1);
                this.setFoodLevelDebt(this.getFoodLevelDebt() - 1);
            }
        }
    }

    public void onStartDreamyDieting() {
        if(!this.isDreamyDieting) {
            this.isDreamyDieting = true;
            HungerManager hungerManager = player.getHungerManager();
            this.preDreamyDietFoodLevel = hungerManager.getFoodLevel() - this.getFoodLevelDebt();
            hungerManager.setFoodLevel(20);
            this.setFoodLevelDebt(0);
        }
    }

    public void onEndDreamyDieting() {
        if(this.isDreamyDieting) {
            this.isDreamyDieting = false;
            HungerManager hungerManager = player.getHungerManager();
            hungerManager.setFoodLevel(Math.clamp(this.preDreamyDietFoodLevel, 0, 20));
            this.setFoodLevelDebt(Math.clamp(-this.preDreamyDietFoodLevel, 0, 20));
            this.preDreamyDietFoodLevel = 20;
            this.reduceHungerBasedOnTime(hungerManager, this.dreamyDietTicks);
            this.dreamyDietTicks = 0;
        }
    }

    public void reduceHungerBasedOnTime(HungerManager hungerManager, int dietTicks) {
        if(dietTicks < 0) return;
        if(dietTicks > 72000) dietTicks = 72000;

        float exhaustion = hungerManager.getExhaustion();
        float saturationLevel = hungerManager.getSaturationLevel();
        int foodLevel = hungerManager.getFoodLevel();

        int TICKS_PER_EXHAUSTION = 100;
        float gainedExhaustion = dietTicks / (float)TICKS_PER_EXHAUSTION;

        exhaustion += gainedExhaustion;

        int foodCost = MathHelper.floor(exhaustion / 4);
        exhaustion -= foodCost * 4;

        int maxRemovedSaturation = MathHelper.ceil(saturationLevel);
        int removedSaturation = Math.min(foodCost, maxRemovedSaturation);
        saturationLevel = Math.max(saturationLevel - removedSaturation, 0);
        foodCost -= removedSaturation;

        int removedFoodLevel = Math.min(foodCost, foodLevel);
        foodLevel = Math.max(foodLevel - removedFoodLevel, 0);
        foodCost -= removedFoodLevel;

        hungerManager.setExhaustion(exhaustion);
        hungerManager.setFoodLevel(foodLevel);
        hungerManager.setSaturationLevel(saturationLevel);

        if(foodCost > 0) {
            this.setFoodLevelDebt(Math.min(this.getFoodLevelDebt() + foodCost, 20));
        }
    }

    public void writeNbt(NbtCompound nbtCompound) {
        nbtCompound.putBoolean("IsDreamyDieting", this.isDreamyDieting);
        nbtCompound.putInt("PreDreamyDietFoodLevel", this.preDreamyDietFoodLevel);
        nbtCompound.putInt("DreamyDietTicks", this.dreamyDietTicks);
        nbtCompound.putInt("FoodLevelDebt", this.getFoodLevelDebt());
    }

    public void readNbt(NbtCompound nbtCompound) {
        if(nbtCompound.contains("IsDreamyDieting", NbtElement.BYTE_TYPE)) {
            this.isDreamyDieting = nbtCompound.getBoolean("IsDreamyDieting");
        }
        if(nbtCompound.contains("PreDreamyDietFoodLevel", NbtElement.INT_TYPE)) {
            this.preDreamyDietFoodLevel = nbtCompound.getInt("PreDreamyDietFoodLevel");
        }
        if(nbtCompound.contains("DreamyDietTicks", NbtElement.INT_TYPE)) {
            this.dreamyDietTicks = nbtCompound.getInt("DreamyDietTicks");
        }
        if(nbtCompound.contains("FoodLevelDebt", NbtElement.INT_TYPE)) {
            this.setFoodLevelDebt(nbtCompound.getInt("FoodLevelDebt"));
        }
    }

    public boolean isDreamyDieting() {
        return this.isDreamyDieting;
    }

    public int getFoodLevelDebt() {
        return ((HungerManagerDuckInterface)this.player.getHungerManager()).mirthdew_encore$getFoodLevelDebt();
    }

    public void setFoodLevelDebt(int foodLevelDebt) {
        ((HungerManagerDuckInterface)this.player.getHungerManager()).mirthdew_encore$setFoodLevelDebt(foodLevelDebt);
    }

    public static PlayerEntityHungerData fromPlayer(PlayerEntity player) {
        return MirthdewEncorePlayerEntityAttachment.fromPlayer(player).getHungerData();
    }
}
