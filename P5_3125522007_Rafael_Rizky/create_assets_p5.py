import os
from PIL import Image, ImageDraw, ImageFont

base_dir = r"d:\KULIAH\Semester 3\OOP\Prakatikum\P5_3125522007_Rafael_Rizky"
assets_dir = os.path.join(base_dir, "assets")
os.makedirs(assets_dir, exist_ok=True)

try:
    font_title = ImageFont.truetype("timesbd.ttf", 16)
    font_header = ImageFont.truetype("timesbd.ttf", 14)
    font_body = ImageFont.truetype("times.ttf", 12)
    font_rel = ImageFont.truetype("timesbd.ttf", 11)
    font_rel_sub = ImageFont.truetype("times.ttf", 10)
except:
    font_title = ImageFont.load_default()
    font_header = ImageFont.load_default()
    font_body = ImageFont.load_default()
    font_rel = ImageFont.load_default()
    font_rel_sub = ImageFont.load_default()

# ---------------------------------------------------------
# 1. DIAGRAM SEBELUM REFACTORING (P4: Tanpa Inheritance)
# ---------------------------------------------------------
def create_diagram_sebelum():
    width, height = 1100, 420
    img = Image.new("RGB", (width, height), color=(255, 255, 255))
    draw = ImageDraw.Draw(img)

    draw.text((width // 2 - 250, 15), "CLASS DIAGRAM SEBELUM REFACTORING (P4)", fill=(0, 0, 0), font=font_title)

    # Class ProdukMakanan (Sebelum: berdiri sendiri dengan duplikasi)
    # x: 80, y: 60, w: 420, h: 320
    x1, y1, x2, y2 = 80, 60, 500, 380
    draw.rectangle([x1, y1, x2, y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([x1, y1, x2, y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((x1 + 130, y1 + 6), "ProdukMakanan", fill=(0, 0, 0), font=font_header)

    attrs1 = [
        "- kode : String       [Duplikasi]",
        "- nama : String       [Duplikasi]",
        "- harga : int         [Duplikasi]",
        "- stok : int          [Duplikasi]",
        "- tanggalKadaluarsa : String"
    ]
    y = y1 + 40
    for a in attrs1:
        draw.text((x1 + 15, y), a, fill=(0, 0, 0), font=font_body)
        y += 18
    draw.line([x1, y + 4, x2, y + 4], fill=(0, 0, 0), width=1)
    y += 10
    methods1 = [
        "+ getKode() : String",
        "+ getNama() : String",
        "+ getHarga() : int",
        "+ getStok() : int",
        "+ getTanggalKadaluarsa() : String",
        "+ kurangiStok(int) : boolean",
        "+ hitungNilaiInventaris() : int",
        "+ tampilkanData() : void"
    ]
    for m in methods1:
        draw.text((x1 + 15, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # Class ProdukElektronik (Sebelum: berdiri sendiri dengan duplikasi)
    x1, y1, x2, y2 = 600, 60, 1020, 380
    draw.rectangle([x1, y1, x2, y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([x1, y1, x2, y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((x1 + 120, y1 + 6), "ProdukElektronik", fill=(0, 0, 0), font=font_header)

    attrs2 = [
        "- kode : String       [Duplikasi]",
        "- nama : String       [Duplikasi]",
        "- harga : int         [Duplikasi]",
        "- stok : int          [Duplikasi]",
        "- garansiBulan : int"
    ]
    y = y1 + 40
    for a in attrs2:
        draw.text((x1 + 15, y), a, fill=(0, 0, 0), font=font_body)
        y += 18
    draw.line([x1, y + 4, x2, y + 4], fill=(0, 0, 0), width=1)
    y += 10
    methods2 = [
        "+ getKode() : String",
        "+ getNama() : String",
        "+ getHarga() : int",
        "+ getStok() : int",
        "+ getGaransiBulan() : int",
        "+ kurangiStok(int) : boolean",
        "+ hitungNilaiInventaris() : int",
        "+ tampilkanData() : void"
    ]
    for m in methods2:
        draw.text((x1 + 15, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    path = os.path.join(assets_dir, "class_diagram_sebelum.png")
    img.save(path, "PNG")
    print(f"Diagram Sebelum saved at: {path}")
    return path

# ---------------------------------------------------------
# 2. DIAGRAM SESUDAH REFACTORING (P5: Dengan Inheritance)
# ---------------------------------------------------------
def create_diagram_sesudah():
    width, height = 1380, 840
    img = Image.new("RGB", (width, height), color=(255, 255, 255))
    draw = ImageDraw.Draw(img)

    draw.text((width // 2 - 330, 15), "UML CLASS DIAGRAM SETELAH REFACTORING (INHERITANCE P5)", fill=(0, 0, 0), font=font_title)

    # 1. Superclass: Produk (Top Center)
    # x: 490, y: 60, w: 400, h: 320
    sp_x1, sp_y1, sp_x2, sp_y2 = 490, 60, 890, 360
    draw.rectangle([sp_x1, sp_y1, sp_x2, sp_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([sp_x1, sp_y1, sp_x2, sp_y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((sp_x1 + 115, sp_y1 + 6), "<<Superclass>> Produk", fill=(0, 0, 0), font=font_header)

    sp_attrs = [
        "- kode : String",
        "- nama : String",
        "- harga : int",
        "- stok : int"
    ]
    y = sp_y1 + 38
    for a in sp_attrs:
        draw.text((sp_x1 + 15, y), a, fill=(0, 0, 0), font=font_body)
        y += 18
    draw.line([sp_x1, y + 4, sp_x2, y + 4], fill=(0, 0, 0), width=1)
    y += 10
    sp_methods = [
        "+ Produk(kode, nama, harga, stok)",
        "+ getKode(), getNama(), getHarga(), getStok()",
        "+ setNama(String), setHarga(int), setStok(int)",
        "+ tambahStok(int), kurangiStok(int) : boolean",
        "+ hitungNilaiInventaris() : int",
        "+ tampilkanData() : void"
    ]
    for m in sp_methods:
        draw.text((sp_x1 + 15, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # 2. Subclass 1: ProdukMakanan (Bottom Left)
    # x: 300, y: 460, w: 360, h: 180
    sb1_x1, sb1_y1, sb1_x2, sb1_y2 = 280, 460, 640, 630
    draw.rectangle([sb1_x1, sb1_y1, sb1_x2, sb1_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([sb1_x1, sb1_y1, sb1_x2, sb1_y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((sb1_x1 + 75, sb1_y1 + 6), "<<Subclass>> ProdukMakanan", fill=(0, 0, 0), font=font_header)

    sb1_attrs = ["- tanggalKadaluarsa : String"]
    y = sb1_y1 + 38
    for a in sb1_attrs:
        draw.text((sb1_x1 + 15, y), a, fill=(0, 0, 0), font=font_body)
        y += 18
    draw.line([sb1_x1, y + 4, sb1_x2, y + 4], fill=(0, 0, 0), width=1)
    y += 10
    sb1_methods = [
        "+ ProdukMakanan(kode, nama, harga, stok, tgl)",
        "+ getTanggalKadaluarsa() : String",
        "+ setTanggalKadaluarsa(String) : void",
        "+ tampilkanData() : void  <<Override>>"
    ]
    for m in sb1_methods:
        draw.text((sb1_x1 + 15, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # 3. Subclass 2: ProdukElektronik (Bottom Right)
    # x: 740, y: 460, w: 360, h: 180
    sb2_x1, sb2_y1, sb2_x2, sb2_y2 = 740, 460, 1100, 630
    draw.rectangle([sb2_x1, sb2_y1, sb2_x2, sb2_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([sb2_x1, sb2_y1, sb2_x2, sb2_y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((sb2_x1 + 70, sb2_y1 + 6), "<<Subclass>> ProdukElektronik", fill=(0, 0, 0), font=font_header)

    sb2_attrs = ["- garansiBulan : int"]
    y = sb2_y1 + 38
    for a in sb2_attrs:
        draw.text((sb2_x1 + 15, y), a, fill=(0, 0, 0), font=font_body)
        y += 18
    draw.line([sb2_x1, y + 4, sb2_x2, y + 4], fill=(0, 0, 0), width=1)
    y += 10
    sb2_methods = [
        "+ ProdukElektronik(kode, nama, harga, stok, garansi)",
        "+ getGaransiBulan() : int",
        "+ setGaransiBulan(int) : void",
        "+ tampilkanData() : void  <<Override>>"
    ]
    for m in sb2_methods:
        draw.text((sb2_x1 + 15, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # INHERITANCE GENERALIZATION TRIANGLE (▲)
    # Triangle at (690, 360) pointing UP to Produk
    # Apex at (690, 360), base at (680, 380) to (700, 380)
    draw.polygon([(690, 360), (678, 380), (702, 380)], fill=(255, 255, 255), outline=(0, 0, 0), width=2)
    # Vertical line from triangle base (690, 380) to split line (y=415)
    draw.line([(690, 380), (690, 415)], fill=(0, 0, 0), width=2)
    # Horizontal split line: from x=460 to x=920 at y=415
    draw.line([(460, 415), (920, 415)], fill=(0, 0, 0), width=2)
    # Vertical lines down to subclasses:
    draw.line([(460, 415), (460, 460)], fill=(0, 0, 0), width=2) # to ProdukMakanan
    draw.line([(920, 415), (920, 460)], fill=(0, 0, 0), width=2) # to ProdukElektronik

    draw.text((615, 390), "Generalization / Inheritance (is-a)", fill=(0, 0, 0), font=font_rel)

    # 4. Pelanggan (Far Left)
    c_x1, c_y1, c_x2, c_y2 = 30, 100, 330, 340
    draw.rectangle([c_x1, c_y1, c_x2, c_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([c_x1, c_y1, c_x2, c_y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((c_x1 + 100, c_y1 + 6), "Pelanggan", fill=(0, 0, 0), font=font_header)
    c_attrs = ["- idPelanggan : String", "- nama : String", "- nomorHP : String", "- tipeMember : String"]
    y = c_y1 + 38
    for a in c_attrs:
        draw.text((c_x1 + 15, y), a, fill=(0, 0, 0), font=font_body)
        y += 18
    draw.line([c_x1, y + 4, c_x2, y + 4], fill=(0, 0, 0), width=1)
    y += 10
    c_methods = ["+ getNama(), getNomorHP()", "+ getTipeMember(), getDiskon()", "+ tampilkanData() : void"]
    for m in c_methods:
        draw.text((c_x1 + 15, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # 5. Transaksi (Bottom Left)
    t_x1, t_y1, t_x2, t_y2 = 30, 460, 240, 780
    draw.rectangle([t_x1, t_y1, t_x2, t_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([t_x1, t_y1, t_x2, t_y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((t_x1 + 60, t_y1 + 6), "Transaksi", fill=(0, 0, 0), font=font_header)
    t_attrs = ["- idTransaksi : String", "- tanggal : String", "- pelanggan : Pelanggan", "- daftarItem : List<Item>"]
    y = t_y1 + 38
    for a in t_attrs:
        draw.text((t_x1 + 10, y), a, fill=(0, 0, 0), font=font_rel_sub)
        y += 18
    draw.line([t_x1, y + 4, t_x2, y + 4], fill=(0, 0, 0), width=1)
    y += 10
    t_methods = ["+ tambahItem(Produk, int)", "+ hitungTotalBayar()", "+ prosesTransaksi()"]
    for m in t_methods:
        draw.text((t_x1 + 10, y), m, fill=(0, 0, 0), font=font_rel_sub)
        y += 18

    # 6. ItemTransaksi (Bottom Far Right)
    i_x1, i_y1, i_x2, i_y2 = 1140, 100, 1360, 340
    draw.rectangle([i_x1, i_y1, i_x2, i_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([i_x1, i_y1, i_x2, i_y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((i_x1 + 45, i_y1 + 6), "ItemTransaksi", fill=(0, 0, 0), font=font_header)
    i_attrs = ["- produk : Produk", "- jumlahBeli : int"]
    y = i_y1 + 38
    for a in i_attrs:
        draw.text((i_x1 + 15, y), a, fill=(0, 0, 0), font=font_body)
        y += 18
    draw.line([i_x1, y + 4, i_x2, y + 4], fill=(0, 0, 0), width=1)
    y += 10
    i_methods = ["+ hitungSubtotal() : int", "+ tampilkanItem() : void"]
    for m in i_methods:
        draw.text((i_x1 + 15, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # Relasi Association (Pelanggan - Transaksi)
    draw.line([(135, 340), (135, 460)], fill=(0, 0, 0), width=2)
    draw.text((145, 395), "Association", fill=(0, 0, 0), font=font_rel_sub)

    # Relasi Aggregation (ItemTransaksi - Produk)
    draw.polygon([(1140, 200), (1125, 193), (1110, 200), (1125, 207)], fill=(255, 255, 255), outline=(0, 0, 0), width=2)
    draw.line([(1110, 200), (890, 200)], fill=(0, 0, 0), width=2)
    draw.text((950, 182), "Aggregation (has-a Produk)", fill=(0, 0, 0), font=font_rel_sub)

    # Relasi Composition (Transaksi - ItemTransaksi)
    draw.polygon([(240, 700), (255, 693), (270, 700), (255, 707)], fill=(0, 0, 0), outline=(0, 0, 0))
    draw.line([(270, 700), (1250, 700), (1250, 340)], fill=(0, 0, 0), width=2)
    draw.text((700, 680), "Composition (part-of ItemTransaksi)", fill=(0, 0, 0), font=font_rel_sub)

    path = os.path.join(assets_dir, "class_diagram_sesudah.png")
    img.save(path, "PNG")
    print(f"Diagram Sesudah saved at: {path}")
    return path

# ---------------------------------------------------------
# 3. TERMINAL SCREENSHOT (PURE BLACK & WHITE)
# ---------------------------------------------------------
def create_terminal_screenshot_bw():
    term_text = """PS D:\\KULIAH\\Semester 3\\OOP> javac -d bin (Get-ChildItem src/*.java).FullName
PS D:\\KULIAH\\Semester 3\\OOP> java -cp bin Main
==========================================================
PRAKTIKUM P5 - PEMROGRAMAN BERORIENTASI OBYEK
Inheritance, Generalization, Superclass, dan Subclass
Nama : Rafael Rizky | NRP : 3125522007
Proyek : Sistem Kasir Sederhana
==========================================================

##########################################################
### 1. PENGUJIAN INSTANSIASI SUBCLASS DENGAN super()   ###
##########################################################
[INFO] Menginstansiasi Subclass 1: ProdukMakanan...
[INFO] Menginstansiasi Subclass 2: ProdukElektronik...

[INFO] Menampilkan informasi produk menggunakan method subclass:
=== DATA PRODUK MAKANAN (SUBCLASS) ===
Kode        : M001
Nama        : Ayam Geprek Crispy
Harga       : Rp15000
Stok        : 25 pcs
Kadaluarsa  : 25-09-2026
Inventaris  : Rp375000
--------------------------------------
=== DATA PRODUK MAKANAN (SUBCLASS) ===
Kode        : M002
Nama        : Bebek Bakar Madu
Harga       : Rp28000
Stok        : 15 pcs
Kadaluarsa  : 24-09-2026
Inventaris  : Rp420000
--------------------------------------
=== DATA PRODUK ELEKTRONIK (SUBCLASS) ===
Kode        : E001
Nama        : Kabel Data Type-C Fast
Harga       : Rp25000
Stok        : 40 pcs
Garansi     : 6 Bulan
Inventaris  : Rp1000000
-----------------------------------------
=== DATA PRODUK ELEKTRONIK (SUBCLASS) ===
Kode        : E002
Nama        : Powerbank 10000mAh
Harga       : Rp120000
Stok        : 10 pcs
Garansi     : 12 Bulan
Inventaris  : Rp1200000
-----------------------------------------

##########################################################
### 2. BUKTI AKSES MEMBER SUPERCLASS DARI SUBCLASS     ###
##########################################################
Nama Makanan 1 (via getNama() Superclass) : Ayam Geprek Crispy
Harga Makanan 1 (via getHarga() Superclass): Rp15000
Tanggal Kadaluarsa (via Subclass Khusus)  : 25-09-2026
Inventaris Makanan 1 (hitungNilaiInventaris): Rp375000

Nama Elektronik 2 (via getNama() Superclass) : Powerbank 10000mAh
Harga Elektronik 2 (via getHarga() Superclass): Rp120000
Masa Garansi (via Subclass Khusus)           : 12 Bulan
Inventaris Elektronik 2 (hitungNilaiInventaris): Rp1200000

##########################################################
### 3. INTEGRASI TRANSAKSI MULTI-KATEGORI (RELASI P4)  ###
### (Association, Aggregation, dan Composition)        ###
##########################################################

--- Transaksi TRX-001 (Pelanggan VIP: Makanan + Elektronik) ---
[INFO] Berhasil menambahkan 2 pcs Ayam Geprek Crispy ke transaksi TRX-001
[INFO] Berhasil menambahkan 1 pcs Kabel Data Type-C Fast ke transaksi TRX-001
==========================================================
                 STRUK RESMI KASIR TOKO                   
==========================================================
No. Transaksi : TRX-001
Tanggal       : 22-09-2026
Pelanggan     : Rafael Rizky (VIP)
Nomor HP      : 081234567890
----------------------------------------------------------
DAFTAR BELANJA (MAKANAN & ELEKTRONIK):
  Nama Barang      | Jml     | Harga   | Total
  --------------------------------------------------------
  Ayam Geprek Crispy |   2 pcs | Rp  15000 | Subtotal: Rp   30000
  Kabel Data Type-C Fast |   1 pcs | Rp  25000 | Subtotal: Rp   25000
----------------------------------------------------------
Total Subtotal    : Rp55000
Diskon Member     : Rp8250 (15%)
----------------------------------------------------------
TOTAL AKHIR BAYAR : Rp46750
==========================================================
[SUKSES] Transaksi TRX-001 berhasil diproses dan disimpan.

--- Transaksi TRX-002 (Pelanggan GOLD: Makanan + Elektronik) ---
[INFO] Berhasil menambahkan 1 pcs Bebek Bakar Madu ke transaksi TRX-002
[INFO] Berhasil menambahkan 1 pcs Powerbank 10000mAh ke transaksi TRX-002
==========================================================
                 STRUK RESMI KASIR TOKO                   
==========================================================
No. Transaksi : TRX-002
Tanggal       : 22-09-2026
Pelanggan     : Budi Santoso (GOLD)
Nomor HP      : 089876543210
----------------------------------------------------------
DAFTAR BELANJA (MAKANAN & ELEKTRONIK):
  Nama Barang      | Jml     | Harga   | Total
  --------------------------------------------------------
  Bebek Bakar Madu |   1 pcs | Rp  28000 | Subtotal: Rp   28000
  Powerbank 10000mAh |   1 pcs | Rp 120000 | Subtotal: Rp  120000
----------------------------------------------------------
Total Subtotal    : Rp148000
Diskon Member     : Rp14800 (10%)
----------------------------------------------------------
TOTAL AKHIR BAYAR : Rp133200
==========================================================
[SUKSES] Transaksi TRX-002 berhasil diproses dan disimpan.

--- Verifikasi Sisa Stok Setelah Transaksi Kasir ---
Sisa stok Ayam Geprek Crispy: 23 pcs
Sisa stok Bebek Bakar Madu: 14 pcs
Sisa stok Kabel Data Type-C Fast: 39 pcs
Sisa stok Powerbank 10000mAh: 9 pcs

==========================================================
         SELURUH PENGUJIAN P5 SELESAI DENGAN SUKSES       
==========================================================
"""
    lines = term_text.strip().split("\n")
    line_height = 17
    width = 960
    height = len(lines) * line_height + 55

    img = Image.new("RGB", (width, height), color=(255, 255, 255))
    draw = ImageDraw.Draw(img)

    draw.rectangle([0, 0, width, 30], fill=(245, 245, 245), outline=(0, 0, 0), width=1)
    draw.ellipse([12, 10, 20, 18], fill=(255, 255, 255), outline=(0, 0, 0), width=1)
    draw.ellipse([26, 10, 34, 18], fill=(255, 255, 255), outline=(0, 0, 0), width=1)
    draw.ellipse([40, 10, 48, 18], fill=(0, 0, 0))

    try:
        font_header = ImageFont.truetype("timesbd.ttf", 11)
        font_code = ImageFont.truetype("consola.ttf", 11)
    except:
        font_header = ImageFont.load_default()
        font_code = ImageFont.load_default()

    draw.text((60, 8), "Windows PowerShell - Command Execution (P5 Main.java)", fill=(0, 0, 0), font=font_header)

    y = 40
    for line in lines:
        draw.text((20, y), line, fill=(0, 0, 0), font=font_code)
        y += line_height

    out_path = os.path.join(assets_dir, "hasil_running.png")
    img.save(out_path, "PNG")
    print(f"B&W Terminal screenshot saved at: {out_path}")
    return out_path

create_diagram_sebelum()
create_diagram_sesudah()
create_terminal_screenshot_bw()
print("All P5 B&W assets created successfully!")
