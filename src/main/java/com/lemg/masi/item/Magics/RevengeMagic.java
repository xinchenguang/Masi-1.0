package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RevengeMagic extends Magic{

    public RevengeMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }

    @Override
    public boolean passive(){
        return true;
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(user.deathTime==20){
            if(!world.isClient()){
                List<StatusEffectInstance> statusEffects = user.getStatusEffects().stream().toList();
                int range = 10;
                if(MagicUtil.ENERGY.get(user)!=null){
                    range = 10 + MagicUtil.ENERGY.get(user)/10;
                }
                List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().expand(range));
                if(!list.isEmpty()){
                    for(Entity entity : list){
                        if(entity instanceof LivingEntity livingEntity) {
                            livingEntity.damage(user.getRecentDamageSource(),10);
                            for(StatusEffectInstance statusEffectInstance : statusEffects){
                                if(MagicUtil.harmful.contains(statusEffectInstance.getEffectType())){
                                    livingEntity.addStatusEffect(statusEffectInstance);
                                }
                            }
                        }
                    }
                }
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_FORWARD_BLACK, user.getX(),user.getY()+2,user.getZ(), 0, 0, 0.0, 0, 0.0);

            }
        }
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){

    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.revenge_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
