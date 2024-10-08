package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.entities.minions.ArcaneMinionEntity;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArcaneTorrentMagic extends Magic{

    public ArcaneTorrentMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }
    public int releaseContinueTime(){return 200;}


    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        float yaw = user.getYaw();
        float pitch = user.getPitch();
        float ff = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float gg = -MathHelper.sin(pitch * ((float)Math.PI / 180));
        float hh = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));

        Vec3d Pos1 = new Vec3d(user.getX()+ff*20,user.getY()+gg*20,user.getZ()+hh*20);
        Vec3d Pos2 = new Vec3d(user.getX()+ff*3,user.getY()+gg*3,user.getZ()+hh*3);
        Pos2 = Pos2.add(0,4,0);
        //Vec3d Pos2 = player.getPos().add(0,2,0);
        Vec3d direction = Pos1.subtract(Pos2).normalize();
        double length = Pos1.distanceTo(Pos2);


         int amount = 2;
         if(user instanceof PlayerEntity){
             if(MagicUtil.MAX_ENERGY.get(user)!=null){
                 amount = MagicUtil.MAX_ENERGY.get(user) / 30;
             }
         }

         Box box = new Box(Pos1,Pos2);
         List<Entity> list = user.getWorld().getOtherEntities(user,box);
         for(Entity entity : list){
             if(entity instanceof LivingEntity livingEntity){
                 boolean b1 = entity instanceof PlayerEntity player && user instanceof ArcaneMinionEntity arcaneMinionEntity && player==arcaneMinionEntity.getOwner();
                 if(!b1){
                     livingEntity.damage(user.getWorld().getDamageSources().magic(), amount);
                 }
             }
         }

        if(!user.getWorld().isClient()){
            for (int i = 0; i <= 10; i++) {
                double fraction = (double) i / 10;
                Vec3d particlePos = Pos2.add(direction.multiply(fraction * length));
                ((ServerWorld)user.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), particlePos.x, particlePos.y, particlePos.z, 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)user.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), particlePos.x-1, particlePos.y, particlePos.z, 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)user.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), particlePos.x+1, particlePos.y, particlePos.z, 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)user.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), particlePos.x, particlePos.y, particlePos.z-1, 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)user.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), particlePos.x, particlePos.y, particlePos.z+1, 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)user.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), particlePos.x, particlePos.y-1, particlePos.z, 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)user.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), particlePos.x, particlePos.y+1, particlePos.z, 0, 0, 0.0, 0, 0.0);
            }
        }
        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_GROUND_PURPLE, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);
            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 2;
                double z = user.getZ() + Math.sin(yawRadians) * 2;
                double y = user.getY()+4;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_FORWARD_PURPLE, x,y,z, 0, 0, 0.0, 0, 0.0);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.arcane_torrent_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
