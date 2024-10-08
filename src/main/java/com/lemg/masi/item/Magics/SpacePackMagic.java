package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.util.MagicUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SpacePackMagic extends Magic{

    public SpacePackMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }

    public static ConcurrentHashMap<String,ConcurrentHashMap<BlockPos, List<Object>>> packs = new ConcurrentHashMap<>();

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(user instanceof PlayerEntity player && !world.isClient()){
            if (packs.get(player.getUuidAsString()) == null) {
                ConcurrentHashMap<BlockPos, List<Object>> blocksAndpos = new ConcurrentHashMap<>();
                Box box = new Box(user.getBlockPos().getX()-5,user.getBlockPos().getY()-1,user.getBlockPos().getZ()-5,user.getBlockPos().getX()+5,user.getBlockPos().getY()+10,user.getBlockPos().getZ()+5);
                int x = (int) box.minX;
                int y = (int) box.minY;
                int z = (int) box.minZ;
                int rx = 0;
                int ry = 0;
                int rz = 0;
                for(;x<box.maxX;x++){
                    for(;z<box.maxZ;z++){
                        for (;y<box.maxY;y++){
                            BlockPos blockPos = new BlockPos(x,y,z);
                            BlockState blockState = world.getBlockState(blockPos);
                            List<Object> list = new ArrayList<>();
                            list.add(blockState);
                            if(blockState.hasBlockEntity()){
                                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                                list.add(blockEntity);
                                world.removeBlockEntity(blockPos);
                            }
                            BlockPos blockPos2 = new BlockPos(rx,ry,rz);
                            blocksAndpos.put(blockPos2, list);
                            world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
                            ry++;
                        }
                        y = (int) box.minY;
                        ry = 0;
                        rz++;
                    }
                    z = (int) box.minZ;
                    rz=0;
                    rx++;
                }
                packs.put(player.getUuidAsString(),blocksAndpos);
            }else {
                ConcurrentHashMap<BlockPos, List<Object>> blocksAndpos = packs.get(player.getUuidAsString());
                if(blocksAndpos!=null && !blocksAndpos.isEmpty()){
                     for(BlockPos blockPos : blocksAndpos.keySet()){
                         BlockPos blockPos1 = new BlockPos(player.getBlockPos().getX()-5+blockPos.getX(),player.getBlockPos().getY()-1+blockPos.getY(),player.getBlockPos().getZ()-5+blockPos.getZ());
                         world.setBlockState(blockPos1, (BlockState) blocksAndpos.get(blockPos).get(0));
                         if(blocksAndpos.get(blockPos).size()==2){
                             BlockEntity blockEntity = (BlockEntity) blocksAndpos.get(blockPos).get(1);
                             NbtCompound nbt = blockEntity.createNbt();
                             BlockEntity blockEntity1 = blockEntity.getType().instantiate(blockPos1,(BlockState) blocksAndpos.get(blockPos).get(0));
                             if(blockEntity1!=null){
                                 blockEntity1.readNbt(nbt);
                             }
                             world.addBlockEntity(blockEntity1);
                         }
                     }
                     packs.remove(player.getUuidAsString());
                }
            }
        }
        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_WHITE, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);
            Box box = new Box(user.getBlockPos().getX()-5,user.getBlockPos().getY()-1,user.getBlockPos().getZ()-5,user.getBlockPos().getX()+5,user.getBlockPos().getY()+10,user.getBlockPos().getZ()+5);
            int mx = (int) box.minX;
            int my = (int) box.minY;
            int mz = (int) box.minZ;
            ParticleEffect particleEffect = ParticleTypes.EFFECT;
            if(user instanceof PlayerEntity player && packs.get(player.getUuidAsString())!=null){
                particleEffect = ParticleTypes.ENCHANT;
            }
            for(;mx<=(int) box.maxX;mx++){
                ((ServerWorld)user.getWorld()).spawnParticles(particleEffect, mx,user.getY(),(int) box.minZ, 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)user.getWorld()).spawnParticles(particleEffect, mx,user.getY(),(int) box.maxZ, 0, 0, 0.0, 0, 0.0);
            }
            for(;mz<=(int) box.maxZ;mz++){
                ((ServerWorld)user.getWorld()).spawnParticles(particleEffect, (int) box.minX,user.getY()+0.2,mz, 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)user.getWorld()).spawnParticles(particleEffect, (int) box.maxX,user.getY()+0.2,mz, 0, 0, 0.0, 0, 0.0);
            }
            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_WHITE, x,y,z, 0, 0, 0.0, 0, 0.0);

            }
        }
    }

    @Override
    public void magicEffect(ItemStack staffStack, World world, LivingEntity user, Object aim,float ticks){
        if(aim instanceof Object[] bbp && ticks<=1160){

        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.space_pack_magic.tooltip"));
        tooltip.add(Text.translatable("item.masi.space_pack_magic.tooltip2"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
