package com.lemg.masi.entity.entities;

import com.lemg.masi.item.Magics.DeathDeclarationMagic;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;



public class MagicBulletEntity
        extends ThrownItemEntity {
    public MagicBulletEntity(EntityType<? extends net.minecraft.entity.projectile.thrown.SnowballEntity> entityType, World world) {
        super((EntityType<? extends ThrownItemEntity>)entityType, world);
    }

    public MagicBulletEntity(World world, LivingEntity owner) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.SNOWBALL, owner, world);
    }

    public MagicBulletEntity(World world, double x, double y, double z) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.SNOWBALL, x, y, z, world);
    }

    public Magic magic = null;
    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        LivingEntity livingEntity = (LivingEntity) this.getOwner();
        magic.BulletEffect(hitResult,livingEntity,this);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.age>=60){
            this.discard();
        }
        if(this.magic instanceof DeathDeclarationMagic){
            ((ServerWorld)this.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x000000).toVector3f(), 1.0f), this.getX(), this.getY(), this.getZ(), 0, 0, 0.0, 0, 0.0);

        }
    }
}

