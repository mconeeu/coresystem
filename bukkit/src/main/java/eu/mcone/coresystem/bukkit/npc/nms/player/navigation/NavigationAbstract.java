package eu.mcone.coresystem.bukkit.npc.nms.player.navigation;

import eu.mcone.coresystem.bukkit.npc.nms.player.EntityHuman;
import net.minecraft.server.v1_8_R3.*;

import java.util.Iterator;
import java.util.List;

public abstract class NavigationAbstract {
    protected eu.mcone.coresystem.bukkit.npc.nms.player.EntityHuman b;
    protected World c;
    protected PathEntity d;
    protected double e;
    private final AttributeInstance a;
    private int f;
    private int g;
    private Vec3D h = new Vec3D(0.0D, 0.0D, 0.0D);
    private float i = 1.0F;
    private final Pathfinder j;

    public NavigationAbstract(EntityHuman var1, World var2) {
        this.b = var1;
        this.c = var2;
        this.a = var1.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        this.j = this.a();
    }

    protected abstract Pathfinder a();

    public void a(double var1) {
        this.e = var1;
    }

    public float i() {
        return (float)this.a.getValue();
    }

    public final PathEntity a(double var1, double var3, double var5) {
        return this.a(new BlockPosition(MathHelper.floor(var1), (int)var3, MathHelper.floor(var5)));
    }

    public PathEntity a(BlockPosition var1) {
        if (!this.b()) {
            return null;
        } else {
            float var2 = this.i();
            this.c.methodProfiler.a("pathfind");
            BlockPosition var3 = new BlockPosition(this.b);
            int var4 = (int)(var2 + 8.0F);
            ChunkCache var5 = new ChunkCache(this.c, var3.a(-var4, -var4, -var4), var3.a(var4, var4, var4), 0);
            PathEntity var6 = this.j.a(var5, this.b, var1, var2);
            this.c.methodProfiler.b();
            return var6;
        }
    }

    public boolean a(double var1, double var3, double var5, double var7) {
        PathEntity var9 = this.a((double)MathHelper.floor(var1), (double)((int)var3), (double)MathHelper.floor(var5));
        return this.a(var9, var7);
    }

    public void a(float var1) {
        this.i = var1;
    }

    public PathEntity a(Entity var1) {
        if (!this.b()) {
            return null;
        } else {
            float var2 = this.i();
            this.c.methodProfiler.a("pathfind");
            BlockPosition var3 = (new BlockPosition(this.b)).up();
            int var4 = (int)(var2 + 16.0F);
            ChunkCache var5 = new ChunkCache(this.c, var3.a(-var4, -var4, -var4), var3.a(var4, var4, var4), 0);
            PathEntity var6 = this.j.a(var5, this.b, var1, var2);
            this.c.methodProfiler.b();
            return var6;
        }
    }

    public boolean a(Entity var1, double var2) {
        PathEntity var4 = this.a(var1);
        return var4 != null ? this.a(var4, var2) : false;
    }

    public boolean a(PathEntity var1, double var2) {
        if (var1 == null) {
            this.d = null;
            return false;
        } else {
            if (!var1.a(this.d)) {
                this.d = var1;
            }

            this.d();
            if (this.d.d() == 0) {
                return false;
            } else {
                this.e = var2;
                Vec3D var4 = this.c();
                this.g = this.f;
                this.h = var4;
                return true;
            }
        }
    }

    public PathEntity j() {
        return this.d;
    }

    public void k() {
        ++this.f;
        if (!this.m()) {
            Vec3D var1;
            if (this.b()) {
                this.l();
            } else if (this.d != null && this.d.e() < this.d.d()) {
                var1 = this.c();
                Vec3D var2 = this.d.a(this.b, this.d.e());
                if (var1.b > var2.b && !this.b.onGround && MathHelper.floor(var1.a) == MathHelper.floor(var2.a) && MathHelper.floor(var1.c) == MathHelper.floor(var2.c)) {
                    this.d.c(this.d.e() + 1);
                }
            }

            if (!this.m()) {
                var1 = this.d.a(this.b);
                if (var1 != null) {
                    AxisAlignedBB var8 = (new AxisAlignedBB(var1.a, var1.b, var1.c, var1.a, var1.b, var1.c)).grow(0.5D, 0.5D, 0.5D);
                    List var3 = this.c.getCubes(this.b, var8.a(0.0D, -1.0D, 0.0D));
                    double var4 = -1.0D;
                    var8 = var8.c(0.0D, 1.0D, 0.0D);

                    AxisAlignedBB var7;
                    for(Iterator var6 = var3.iterator(); var6.hasNext(); var4 = var7.b(var8, var4)) {
                        var7 = (AxisAlignedBB)var6.next();
                    }

                    this.b.getControllerMove().a(var1.a, var1.b + var4, var1.c, this.e);
                }
            }
        }
    }

    protected void l() {
        Vec3D var1 = this.c();
        int var2 = this.d.d();

        for(int var3 = this.d.e(); var3 < this.d.d(); ++var3) {
            if (this.d.a(var3).b != (int)var1.b) {
                var2 = var3;
                break;
            }
        }

        float var8 = this.b.width * this.b.width * this.i;

        int var4;
        for(var4 = this.d.e(); var4 < var2; ++var4) {
            Vec3D var5 = this.d.a(this.b, var4);
            if (var1.distanceSquared(var5) < (double)var8) {
                this.d.c(var4 + 1);
            }
        }

        var4 = MathHelper.f(this.b.width);
        int var9 = (int)this.b.length + 1;
        int var6 = var4;

        for(int var7 = var2 - 1; var7 >= this.d.e(); --var7) {
            if (this.a(var1, this.d.a(this.b, var7), var4, var9, var6)) {
                this.d.c(var7);
                break;
            }
        }

        this.a(var1);
    }

    protected void a(Vec3D var1) {
        if (this.f - this.g > 100) {
            if (var1.distanceSquared(this.h) < 2.25D) {
                this.n();
            }

            this.g = this.f;
            this.h = var1;
        }

    }

    public boolean m() {
        return this.d == null || this.d.b();
    }

    public void n() {
        this.d = null;
    }

    protected abstract Vec3D c();

    protected abstract boolean b();

    protected boolean o() {
        return this.b.V() || this.b.ab();
    }

    protected void d() {
    }

    protected abstract boolean a(Vec3D var1, Vec3D var2, int var3, int var4, int var5);
}
