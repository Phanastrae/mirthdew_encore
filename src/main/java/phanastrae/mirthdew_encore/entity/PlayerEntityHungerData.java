package phanastrae.mirthdew_encore.entity;

import phanastrae.mirthdew_encore.duck.HungerManagerDuckInterface;

import static phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class PlayerEntityHungerData {

    private final Player player;

    private boolean isDreamyDieting = false;
    private int preDreamyDietFoodLevel = 20;
    private int dreamyDietTicks = 0;

    public PlayerEntityHungerData(Player player) {
        this.player = player;
    }

    public void tick() {
        if(!this.player.level().isClientSide()) {
            if (this.isDreamyDieting()) {
                if (!player.hasEffect(DREAMY_DIET_ENTRY)) {
                    this.onEndDreamyDieting();
                } else {
                    player.getFoodData().setFoodLevel(20);
                }

                MobEffectInstance dietEffect = this.player.getEffect(DREAMY_DIET_ENTRY);
                if (dietEffect != null) {
                    if(!player.isSpectator() && !player.getAbilities().instabuild) {
                        int amplifier = dietEffect.getAmplifier();
                        if (amplifier >= 0) {
                            this.dreamyDietTicks += (amplifier + 1);
                        }
                    }
                }
            }
            FoodData hungerManager = player.getFoodData();
            if (hungerManager.getFoodLevel() > 0 && this.getFoodLevelDebt() > 0) {
                hungerManager.setFoodLevel(hungerManager.getFoodLevel() - 1);
                this.setFoodLevelDebt(this.getFoodLevelDebt() - 1);
            }
        }
    }

    public void onStartDreamyDieting() {
        if(!this.isDreamyDieting) {
            this.isDreamyDieting = true;
            FoodData hungerManager = player.getFoodData();
            this.preDreamyDietFoodLevel = hungerManager.getFoodLevel() - this.getFoodLevelDebt();
            hungerManager.setFoodLevel(20);
            this.setFoodLevelDebt(0);
        }
    }

    public void onEndDreamyDieting() {
        if(this.isDreamyDieting) {
            this.isDreamyDieting = false;
            FoodData hungerManager = player.getFoodData();
            hungerManager.setFoodLevel(Math.clamp(this.preDreamyDietFoodLevel, 0, 20));
            this.setFoodLevelDebt(Math.clamp(-this.preDreamyDietFoodLevel, 0, 20));
            this.preDreamyDietFoodLevel = 20;
            this.reduceHungerBasedOnTime(hungerManager, this.dreamyDietTicks);
            this.dreamyDietTicks = 0;
        }
    }

    public void reduceHungerBasedOnTime(FoodData hungerManager, int dietTicks) {
        if(dietTicks < 0) return;
        if(dietTicks > 72000) dietTicks = 72000;

        float exhaustion = hungerManager.getExhaustionLevel();
        float saturationLevel = hungerManager.getSaturationLevel();
        int foodLevel = hungerManager.getFoodLevel();

        int TICKS_PER_EXHAUSTION = 100;
        float gainedExhaustion = dietTicks / (float)TICKS_PER_EXHAUSTION;

        exhaustion += gainedExhaustion;

        int foodCost = Mth.floor(exhaustion / 4);
        exhaustion -= foodCost * 4;

        int maxRemovedSaturation = Mth.ceil(saturationLevel);
        int removedSaturation = Math.min(foodCost, maxRemovedSaturation);
        saturationLevel = Math.max(saturationLevel - removedSaturation, 0);
        foodCost -= removedSaturation;

        int removedFoodLevel = Math.min(foodCost, foodLevel);
        foodLevel = Math.max(foodLevel - removedFoodLevel, 0);
        foodCost -= removedFoodLevel;

        hungerManager.setExhaustion(exhaustion);
        hungerManager.setFoodLevel(foodLevel);
        hungerManager.setSaturation(saturationLevel);

        if(foodCost > 0) {
            this.setFoodLevelDebt(Math.min(this.getFoodLevelDebt() + foodCost, 20));
        }
    }

    public void writeNbt(CompoundTag nbtCompound) {
        nbtCompound.putBoolean("IsDreamyDieting", this.isDreamyDieting);
        nbtCompound.putInt("PreDreamyDietFoodLevel", this.preDreamyDietFoodLevel);
        nbtCompound.putInt("DreamyDietTicks", this.dreamyDietTicks);
        nbtCompound.putInt("FoodLevelDebt", this.getFoodLevelDebt());
    }

    public void readNbt(CompoundTag nbtCompound) {
        if(nbtCompound.contains("IsDreamyDieting", Tag.TAG_BYTE)) {
            this.isDreamyDieting = nbtCompound.getBoolean("IsDreamyDieting");
        }
        if(nbtCompound.contains("PreDreamyDietFoodLevel", Tag.TAG_INT)) {
            this.preDreamyDietFoodLevel = nbtCompound.getInt("PreDreamyDietFoodLevel");
        }
        if(nbtCompound.contains("DreamyDietTicks", Tag.TAG_INT)) {
            this.dreamyDietTicks = nbtCompound.getInt("DreamyDietTicks");
        }
        if(nbtCompound.contains("FoodLevelDebt", Tag.TAG_INT)) {
            this.setFoodLevelDebt(nbtCompound.getInt("FoodLevelDebt"));
        }
    }

    public boolean isDreamyDieting() {
        return this.isDreamyDieting;
    }

    public int getFoodLevelDebt() {
        return ((HungerManagerDuckInterface)this.player.getFoodData()).mirthdew_encore$getFoodLevelDebt();
    }

    public void setFoodLevelDebt(int foodLevelDebt) {
        ((HungerManagerDuckInterface)this.player.getFoodData()).mirthdew_encore$setFoodLevelDebt(foodLevelDebt);
    }

    public static PlayerEntityHungerData fromPlayer(Player player) {
        return MirthdewEncorePlayerEntityAttachment.fromPlayer(player).getHungerData();
    }
}
