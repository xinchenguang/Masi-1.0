package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElementalBlessingMagic extends Magic{

    public ElementalBlessingMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        MagicUtil.putEffect(world,user,user,this,1000);
        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_GREEN, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_GREEN, x,y,z, 0, 0, 0.0, 0, 0.0);

            }
        }
    }

    @Override
    public void magicEffect(ItemStack staffStack, World world, LivingEntity user, Object aim,float ticks){
        if(!world.isClient()){
            if(ticks%20==0){
                if(user instanceof PlayerEntity player && (player.getAbilities().creativeMode || MagicUtil.isTrial(player))){
                    return;
                }
                if (MagicUtil.ENERGY.get(user) > 0) {
                    int energy = 0;
                    if(MagicUtil.ENERGY.get(user)>=7){
                        energy = MagicUtil.ENERGY.get(user) - 7;
                    }

                    MagicUtil.energyUpdate(user,energy,false);

                }
            }
        }

        if(aim instanceof PlayerEntity player){
            if(player.isAlive()){
                if (MagicUtil.ENERGY.get(player) > 0) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 60, 5,false,false,true));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 60, 3,false,false,true));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 60, 1,false,false,true));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 60, 1,false,false,true));
                    player.setAir(300);
                    player.setFireTicks(0);
                    player.setFrozenTicks(0);
                    player.fallDistance = 0;
                    player.removeStatusEffect(StatusEffects.SLOWNESS);
                    player.removeStatusEffect(StatusEffects.WEAKNESS);
                }else {
                    MagicUtil.putEffect(world,aim,user,this,0);
                    if(!player.getAbilities().creativeMode && !MagicUtil.isTrial(player)){
                        player.setHealth(0);
                    }
                }
            }else {
                MagicUtil.putEffect(world,aim,user,this,0);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.elemental_blessing_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
