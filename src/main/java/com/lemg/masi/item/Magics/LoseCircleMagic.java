package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
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

public class LoseCircleMagic extends Magic{
    public LoseCircleMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        MagicUtil.putEffect(world,user.getBlockPos(),user,this,200);
        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_GROUND_BLACK, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_BLACK, x,y,z, 0, 0, 0.0, 0, 0.0);

            }
        }
    }

    @Override
    public void magicEffect(ItemStack staffStack, World world, LivingEntity user, Object aim,float ticks){
        if(aim instanceof BlockPos blockPos){
            if(ticks%10==0){
                List<Entity> list = world.getOtherEntities(user,new Box(blockPos.getX()-2,blockPos.getY(),blockPos.getZ()-2,blockPos.getX()+2,blockPos.getY()+3,blockPos.getZ()+2));
                for(Entity entity : list){
                    if(entity instanceof LivingEntity livingEntity){
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 600, 1,false,true,true));
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 600, 1,false,true,true));
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 600, 0,false,true,true));
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 600, 0,false,true,true));
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 600, 1,false,true,true));
                    }
                }
            }
            if(!world.isClient()) {
                ((ServerWorld) user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_GROUND_BLACK, blockPos.getX(), blockPos.getY() + 0.2, blockPos.getZ(), 0, 0, 0.0, 0, 0.0);

            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.lose_circle_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
