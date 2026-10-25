package com.cennetmod;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Cennet başkentinin kapı muhafızı (6 blok boyunda melek asker).
 * rol 0 = lider (sahneyi yönetir), rol 1 = ikinci muhafız.
 * Poz: 0 bekleme, 1 dur/uzat, 2 inceleme, 3 karşılama.
 */
public class AngelSoldier extends Mob {
    public static final EntityDataAccessor<Integer> POZ =
            SynchedEntityData.defineId(AngelSoldier.class, EntityDataSerializers.INT);

    private static final int BITIS_INSA = 135;

    private static final BlockState SMOOTH = Blocks.SMOOTH_QUARTZ.defaultBlockState();
    private static final BlockState PILLAR = Blocks.QUARTZ_PILLAR.defaultBlockState();
    private static final BlockState GOLD = Blocks.GOLD_BLOCK.defaultBlockState();
    private static final BlockState BRICKS = Blocks.QUARTZ_BRICKS.defaultBlockState();
    private static final BlockState GLASS = Blocks.WHITE_STAINED_GLASS.defaultBlockState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    // kalıcı
    private int rol = 0;
    private int kx, ky, kz;       // kapı merkezi x, zemin y, duvar z
    private UUID esUuid;

    // geçici (sahne)
    private boolean mesgul = false;
    private boolean insaEdilecek = false;
    private int zaman = 0;
    private int plazaSatir = 20;
    private UUID oyuncu;
    private ItemStack emanet = ItemStack.EMPTY;
    private boolean geriVer = false;
    private ItemEntity ucanKart;

    public AngelSoldier(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setInvulnerable(true);
        this.setNoGravity(true);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder nitelikler() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 200.0).add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(POZ, 0);
    }

    public int getPoz() { return this.entityData.get(POZ); }
    public void setPoz(int v) { this.entityData.set(POZ, v); }
    public int getRol() { return rol; }
    public boolean mesgul() { return mesgul; }

    @Override public boolean removeWhenFarAway(double d) { return false; }
    @Override public boolean isPushable() { return false; }

    @Override
    public void addAdditionalSaveData(CompoundTag t) {
        super.addAdditionalSaveData(t);
        t.putInt("Rol", rol);
        t.putInt("Kx", kx);
        t.putInt("Ky", ky);
        t.putInt("Kz", kz);
        if (esUuid != null) t.putUUID("Es", esUuid);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag t) {
        super.readAdditionalSaveData(t);
        rol = t.getInt("Rol");
        kx = t.getInt("Kx");
        ky = t.getInt("Ky");
        kz = t.getInt("Kz");
        if (t.hasUUID("Es")) esUuid = t.getUUID("Es");
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide && mesgul) acilDurum();
        super.remove(reason);
    }

    // ------------------------------------------------------------------ kurulum

    /** İlk kullanım: kapıyı kuracak iki muhafızı oluşturur (görünmez) ve sahneyi başlatır. */
    public static void kapiKur(ServerLevel sl, ServerPlayer p) {
        int cx = Mth.floor(p.getX());
        int y0 = Mth.floor(p.getY());
        int zw = Mth.floor(p.getZ()) - 36;
        AngelSoldier a = CennetMod.MELEK.get().create(sl);
        AngelSoldier b = CennetMod.MELEK.get().create(sl);
        if (a == null || b == null) return;
        a.moveTo(cx + 0.5 - 11, y0, zw + 6.5, 0f, 0f);
        b.moveTo(cx + 0.5 + 11, y0, zw + 6.5, 0f, 0f);
        a.rol = 0;
        b.rol = 1;
        a.kx = b.kx = cx;
        a.ky = b.ky = y0;
        a.kz = b.kz = zw;
        a.esUuid = b.getUUID();
        b.esUuid = a.getUUID();
        sl.addFreshEntity(a);
        sl.addFreshEntity(b);
        a.baslat(p, true);
    }

    public void baslat(ServerPlayer p, boolean insa) {
        oyuncu = p.getUUID();
        NurSistemi.SAHNEDE.add(oyuncu);
        mesgul = true;
        insaEdilecek = insa;
        zaman = insa ? 0 : BITIS_INSA;
        plazaSatir = Math.max(8, (int) Math.ceil(p.getZ()) - (kz + 5) + 3);
        if (insa) {
            setInvisible(true);
            AngelSoldier es = esBul((ServerLevel) level());
            if (es != null) es.setInvisible(true);
        }
    }

    private AngelSoldier esBul(ServerLevel sl) {
        if (esUuid == null) return null;
        return sl.getEntity(esUuid) instanceof AngelSoldier s ? s : null;
    }

    // ------------------------------------------------------------------ tick

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (rol == 0 && mesgul) sahne();
    }

    private void sahne() {
        ServerLevel sl = (ServerLevel) level();
        ServerPlayer p = oyuncu == null ? null : sl.getServer().getPlayerList().getPlayer(oyuncu);
        if (p == null || !p.isAlive() || p.level() != sl || p.distanceToSqr(getX(), getY(), getZ()) > 100.0 * 100.0) {
            acilDurum();
            return;
        }
        AngelSoldier es = esBul(sl);
        if (!isInvisible()) {
            bak(this, p);
            if (es != null) bak(es, p);
        }
        if (insaEdilecek && zaman < BITIS_INSA) insa(sl, p, es, zaman);
        else diyalog(sl, p, es, zaman - BITIS_INSA);
        zaman++;
    }

    private static void bak(AngelSoldier a, Entity hedef) {
        double dx = hedef.getX() - a.getX();
        double dz = hedef.getZ() - a.getZ();
        double dy = (hedef.getY() + 1.6) - (a.getY() + 5.5);
        float yaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90f;
        float pitch = (float) (-(Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * (180.0 / Math.PI)));
        a.setYRot(yaw);
        a.setYBodyRot(yaw);
        a.setYHeadRot(yaw);
        a.setXRot(Mth.clamp(pitch, -30f, 30f));
    }

    // ------------------------------------------------------------------ yardımcılar

    private static void pr(ServerLevel sl, ServerPlayer p, ParticleOptions t, double x, double y, double z,
                           int n, double dx, double dy, double dz, double s) {
        sl.sendParticles(p, t, true, x, y, z, n, dx, dy, dz, s);
    }

    private static void ses(ServerLevel sl, double x, double y, double z, SoundEvent s, float vol, float pitch) {
        sl.playSound(null, BlockPos.containing(x, y, z), s, SoundSource.NEUTRAL, vol, pitch);
    }

    private void soyle(ServerLevel sl, ServerPlayer p, AngelSoldier konusan, String metin) {
        p.sendSystemMessage(Component.literal("<Melek Muhafız> ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(metin).withStyle(ChatFormatting.WHITE)));
        AngelSoldier k = konusan == null ? this : konusan;
        ses(sl, k.getX(), k.getY() + 5, k.getZ(), SoundEvents.ALLAY_AMBIENT_WITH_ITEM, 3f, 0.5f);
    }

    private Vec3 el(AngelSoldier a) {
        float yaw = a.getYRot() * Mth.DEG_TO_RAD;
        return new Vec3(a.getX() - Mth.sin(yaw) * 1.4, a.getY() + 3.3, a.getZ() + Mth.cos(yaw) * 1.4);
    }

    private Vec3 oyuncuEl(ServerPlayer p) {
        Vec3 l = p.getLookAngle();
        return p.getEyePosition().add(l.x * 0.6, -0.4, l.z * 0.6);
    }

    private void ucur(Vec3 a, Vec3 b, double f, ServerLevel sl, ServerPlayer p) {
        if (ucanKart == null) return;
        double e = f * f * (3 - 2 * f);
        double x = a.x + (b.x - a.x) * e;
        double y = a.y + (b.y - a.y) * e + Math.sin(Math.PI * e) * 0.8;
        double z = a.z + (b.z - a.z) * e;
        ucanKart.setPos(x, y, z);
        ucanKart.setDeltaMovement(Vec3.ZERO);
        pr(sl, p, ParticleTypes.END_ROD, x, y + 0.2, z, 2, 0.1, 0.1, 0.1, 0.01);
    }

    private void kartiAl(ServerLevel sl, ServerPlayer p) {
        emanet = ItemStack.EMPTY;
        geriVer = false;
        for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
            ItemStack s = p.getInventory().getItem(i);
            if (s.is(CennetMod.NUR_KARTI.get())) {
                emanet = s.copyWithCount(1);
                s.shrink(1);
                geriVer = true;
                break;
            }
        }
        if (emanet.isEmpty()) emanet = new ItemStack(CennetMod.NUR_KARTI.get());
        Vec3 h = oyuncuEl(p);
        ucanKart = new ItemEntity(sl, h.x, h.y, h.z, emanet.copy());
        ucanKart.setNoGravity(true);
        ucanKart.setPickUpDelay(32767);
        ucanKart.setUnlimitedLifetime();
        ucanKart.setInvulnerable(true);
        ucanKart.setDeltaMovement(Vec3.ZERO);
        sl.addFreshEntity(ucanKart);
    }

    private void kartiVer(ServerLevel sl, ServerPlayer p) {
        if (!emanet.isEmpty() && geriVer) {
            if (p != null && p.isAlive()) {
                if (!p.getInventory().add(emanet.copy())) p.drop(emanet.copy(), false);
            } else {
                sl.addFreshEntity(new ItemEntity(sl, getX(), getY() + 1, getZ(), emanet.copy()));
            }
        }
        emanet = ItemStack.EMPTY;
        geriVer = false;
        if (ucanKart != null) {
            ucanKart.discard();
            ucanKart = null;
        }
    }

    private void acilDurum() {
        if (level() instanceof ServerLevel sl) {
            ServerPlayer p = oyuncu == null ? null : sl.getServer().getPlayerList().getPlayer(oyuncu);
            kartiVer(sl, p);
            AngelSoldier es = esBul(sl);
            if (es != null) {
                es.setPoz(0);
                if (es.isInvisible() && !insaEdilecek) es.setInvisible(false);
            }
        }
        setPoz(0);
        if (oyuncu != null) NurSistemi.SAHNEDE.remove(oyuncu);
        mesgul = false;
        zaman = 0;
    }

    // ------------------------------------------------------------------ inşaat sahnesi

    private void insa(ServerLevel sl, ServerPlayer p, AngelSoldier es, int t) {
        double cx = kx + 0.5;
        if (t == 0) ses(sl, cx, ky, kz, SoundEvents.LIGHTNING_BOLT_THUNDER, 3f, 0.6f);
        if (t < 62 && t % 2 == 0) {
            for (int r = 0; r < 3; r++)
                pr(sl, p, ParticleTypes.CLOUD, cx, ky + 2 + r * 6, kz + 2.5, 70, 22, 2.5, 3, 0.02);
        }
        if (t >= 20 && t < 20 + plazaSatir) plazaSatiri(sl, kz + 5 + (t - 20));
        if (t >= 50 && t < 50 + 68) {
            int dy = -8 + (t - 50);
            katman(sl, dy);
            pr(sl, p, ParticleTypes.CLOUD, cx, ky + dy + 1, kz + 2.5, 60, 26, 1.5, 3, 0.03);
            if (t % 5 == 0) ses(sl, cx, ky + dy, kz, SoundEvents.STONE_PLACE, 3f, 0.5f);
            if (t % 12 == 0) ses(sl, cx, ky + dy, kz, SoundEvents.PISTON_EXTEND, 2f, 0.5f);
        }
        if (t >= 118 && t < 134)
            pr(sl, p, ParticleTypes.CLOUD, cx, ky + 25, kz + 2.5, 120, 30, 15, 3, 0.05);
        if (t == 120) {
            setInvisible(false);
            if (es != null) es.setInvisible(false);
            pr(sl, p, ParticleTypes.END_ROD, getX(), getY() + 3, getZ(), 80, 1.2, 3, 1.2, 0.15);
            if (es != null) pr(sl, p, ParticleTypes.END_ROD, es.getX(), es.getY() + 3, es.getZ(), 80, 1.2, 3, 1.2, 0.15);
            ses(sl, cx, ky + 3, kz + 6, SoundEvents.BEACON_ACTIVATE, 3f, 0.8f);
        }
    }

    private void katman(ServerLevel sl, int dy) {
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        for (int dx = -48; dx <= 48; dx++) {
            for (int dz = -1; dz <= 5; dz++) {
                BlockState s = blok(dx, dy, dz);
                if (s != null) {
                    m.set(kx + dx, ky + dy, kz + dz);
                    sl.setBlock(m, s, 18);
                }
            }
        }
    }

    /** Kapı, çerçeve, kuleler ve sur gövdesi. null = dokunma. */
    private static BlockState blok(int dx, int dy, int dz) {
        int ax = Math.abs(dx);
        if (ax <= 7 && dy >= 0 && dy <= 26 && dz >= 0 && dz <= 4) return dz == 2 ? GLASS : AIR;
        if (ax >= 8 && ax <= 9 && dy >= 0 && dy <= 28 && dz >= 0 && dz <= 4) return ax == 8 ? GOLD : PILLAR;
        if (ax <= 9 && dy >= 27 && dy <= 28 && dz >= 0 && dz <= 4) return dy == 27 ? GOLD : PILLAR;
        if (ax >= 13 && ax <= 19 && dz >= -1 && dz <= 5 && dy >= -8 && dy <= 58) {
            if (dy == 58) return GOLD;
            boolean kose = (ax == 13 || ax == 19) && (dz == -1 || dz == 5);
            return kose ? PILLAR : SMOOTH;
        }
        if (ax <= 40 && dz >= 0 && dz <= 4 && dy >= -8 && dy <= 44) {
            if (dz == 4 && (dy == 20 || dy == 40)) return GOLD;
            return SMOOTH;
        }
        if (ax <= 40 && dz >= 0 && dz <= 4 && dy == 45 && ax % 2 == 0) return SMOOTH;
        return null;
    }

    private void plazaSatiri(ServerLevel sl, int z) {
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        for (int dx = -12; dx <= 12; dx++) {
            int x = kx + dx;
            for (int y = ky; y <= ky + 14; y++) {
                m.set(x, y, z);
                if (!sl.getBlockState(m).isAir()) sl.setBlock(m, AIR, 18);
            }
            m.set(x, ky - 1, z);
            BlockState zemin = Math.abs(dx) <= 1 ? GOLD : (((dx + z) & 1) == 0 ? SMOOTH : BRICKS);
            sl.setBlock(m, zemin, 18);
            for (int y = ky - 6; y <= ky - 2; y++) {
                m.set(x, y, z);
                BlockState s = sl.getBlockState(m);
                if (s.isAir() || !s.getFluidState().isEmpty() || s.canBeReplaced()) sl.setBlock(m, SMOOTH, 18);
            }
        }
    }

    private void kapiAc(ServerLevel sl, ServerPlayer p, int adim) {
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        for (int sgn = -1; sgn <= 1; sgn += 2) {
            int dx = adim * sgn;
            if (adim == 0 && sgn == 1) continue;
            for (int dy = 0; dy <= 26; dy++) {
                m.set(kx + dx, ky + dy, kz + 2);
                if (sl.getBlockState(m).is(Blocks.WHITE_STAINED_GLASS)) {
                    sl.setBlock(m, AIR, 18);
                    if (dy % 3 == 0) pr(sl, p, ParticleTypes.END_ROD, m.getX() + 0.5, m.getY() + 0.5, m.getZ() + 0.5, 3, 0.3, 0.3, 0.3, 0.05);
                }
            }
        }
    }

    // ------------------------------------------------------------------ diyalog sahnesi

    private void diyalog(ServerLevel sl, ServerPlayer p, AngelSoldier es, int dt) {
        String ad = p.getName().getString();

        // oyuncuyu yerinde tut (karşılamaya kadar)
        if (dt < 345 && dt % 10 == 0)
            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 6, false, false, false));

        if (dt == 10) { setPoz(1); soyle(sl, p, this, "Dur, yolcu! Burası Cennet'in başkentinin kapısıdır."); }
        if (dt == 55) soyle(sl, p, this, "Giriş kimliğini göster.");
        if (dt == 90) {
            kartiAl(sl, p);
            ses(sl, getX(), getY() + 3, getZ(), SoundEvents.ALLAY_ITEM_TAKEN, 3f, 0.7f);
        }
        if (dt >= 90 && dt < 130) ucur(oyuncuEl(p), el(this), (dt - 90) / 40.0, sl, p);
        if (dt == 130) { setPoz(0); soyle(sl, p, this, "Hımm... Bir de kardeşim bakmalı."); }
        if (dt >= 150 && dt < 180 && es != null) ucur(el(this), el(es), (dt - 150) / 30.0, sl, p);
        if (dt == 180 && es != null) es.setPoz(2);
        if (dt >= 180 && dt < 270 && es != null) {
            Vec3 h = el(es).add(0, 0.3, 0);
            if (ucanKart != null) {
                ucanKart.setPos(h.x, h.y + Math.sin(dt * 0.12) * 0.06, h.z);
                ucanKart.setDeltaMovement(Vec3.ZERO);
            }
            pr(sl, p, ParticleTypes.ENCHANT, h.x, h.y, h.z, 6, 0.5, 0.5, 0.5, 0.6);
            double ty = h.y - 1.2 + (Math.sin(dt * 0.25) + 1.0) * 1.2;
            pr(sl, p, ParticleTypes.END_ROD, h.x, ty, h.z, 6, 0.6, 0.0, 0.1, 0.0);
            if (dt % 15 == 0) ses(sl, h.x, h.y, h.z, SoundEvents.AMETHYST_BLOCK_CHIME, 3f, 0.8f + (dt % 45) * 0.01f);
        }
        if (dt == 195 && es != null) soyle(sl, p, es, "Kimlik doğrulanıyor...");
        if (dt == 250 && es != null) soyle(sl, p, es, "Mühür gerçek. Her şey yolunda.");
        if (dt == 270 && es != null) es.setPoz(0);
        if (dt >= 270 && dt < 300 && es != null) ucur(el(es), el(this), (dt - 270) / 30.0, sl, p);
        if (dt == 300) setPoz(1);
        if (dt >= 305 && dt < 345) ucur(el(this), oyuncuEl(p), (dt - 305) / 40.0, sl, p);
        if (dt == 345) {
            kartiVer(sl, p);
            ses(sl, p.getX(), p.getY() + 1, p.getZ(), SoundEvents.ALLAY_ITEM_GIVEN, 3f, 0.8f);
            setPoz(3);
            soyle(sl, p, this, "Cennetin başkentine hoş geldiniz, " + ad
                    + ". Burası daha önce görmediğiniz kadar güzel bir yerdir. Mutlaka hoşunuza gider.");
        }
        if (dt == 400) {
            p.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 25));
            p.connection.send(new ClientboundSetSubtitleTextPacket(
                    Component.literal("Hoş geldin, " + ad).withStyle(ChatFormatting.YELLOW)));
            p.connection.send(new ClientboundSetTitleTextPacket(
                    Component.literal("CENNETİN BAŞKENTİ").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)));
            ses(sl, kx + 0.5, ky + 3, kz + 2, SoundEvents.BELL_RESONATE, 4f, 0.7f);
            ses(sl, kx + 0.5, ky + 3, kz + 2, SoundEvents.BEACON_POWER_SELECT, 3f, 0.8f);
        }
        if (dt >= 400 && dt < 432 && (dt - 400) % 4 == 0) kapiAc(sl, p, (dt - 400) / 4);
        if (dt == 470) {
            setPoz(0);
            if (es != null) es.setPoz(0);
            if (oyuncu != null) {
                NurSistemi.SAHNEDE.remove(oyuncu);
                NurSistemi.KARSILANDI.add(oyuncu);
            }
            mesgul = false;
            zaman = 0;
        }
    }
}
