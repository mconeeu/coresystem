package eu.mcone.coresystem.bukkit.npc.nms.player.controller;

import eu.mcone.coresystem.bukkit.npc.nms.player.EntityHuman;
import net.minecraft.server.v1_8_R3.*;

public class ControllerLook {

    private EntityHuman a;
    private float b;
    private float c;
    private boolean d;
    private double e;
    private double f;
    private double g;

    public ControllerLook(eu.mcone.coresystem.bukkit.npc.nms.player.EntityHuman var1) {
        this.a = var1;
    }

    public void a(Entity var1, float var2, float var3) {
        this.e = var1.locX;
        if (var1 instanceof EntityLiving) {
            this.f = var1.locY + (double)var1.getHeadHeight();
        } else {
            this.f = (var1.getBoundingBox().b + var1.getBoundingBox().e) / 2.0D;
        }

        this.g = var1.locZ;
        this.b = var2;
        this.c = var3;
        this.d = true;
    }

    public void a(double var1, double var3, double var5, float var7, float var8) {
        this.e = var1;
        this.f = var3;
        this.g = var5;
        this.b = var7;
        this.c = var8;
        this.d = true;
    }

    public void a() {
        this.a.pitch = 0.0F;
        if (this.d) {
            this.d = false;
            double var1 = this.e - this.a.locX;
            double var3 = this.f - (this.a.locY + (double)this.a.getHeadHeight());
            double var5 = this.g - this.a.locZ;
            double var7 = (double) MathHelper.sqrt(var1 * var1 + var5 * var5);
            float var9 = (float)(MathHelper.b(var5, var1) * 180.0D / 3.1415927410125732D) - 90.0F;
            float var10 = (float)(-(MathHelper.b(var3, var7) * 180.0D / 3.1415927410125732D));
            this.a.pitch = this.a(this.a.pitch, var10, this.c);
            this.a.aK = this.a(this.a.aK, var9, this.b);
        } else {
            this.a.aK = this.a(this.a.aK, this.a.aI, 10.0F);
        }

        float var11 = MathHelper.g(this.a.aK - this.a.aI);
        if (!this.a.getNavigation().m()) {
            if (var11 < -75.0F) {
                this.a.aK = this.a.aI - 75.0F;
            }

            if (var11 > 75.0F) {
                this.a.aK = this.a.aI + 75.0F;
            }
        }

    }

    private float a(float var1, float var2, float var3) {
        float var4 = MathHelper.g(var2 - var1);
        if (var4 > var3) {
            var4 = var3;
        }

        if (var4 < -var3) {
            var4 = -var3;
        }

        return var1 + var4;
    }

    public boolean b() {
        return this.d;
    }

    public double e() {
        return this.e;
    }

    public double f() {
        return this.f;
    }

    public double g() {
        return this.g;
    }
}
