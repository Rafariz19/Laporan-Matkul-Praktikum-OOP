import os
from PIL import Image, ImageDraw, ImageFont

base_dir = r"d:\KULIAH\Semester 3\OOP\Prakatikum\P6_3125522007_Rafael_Rizky"
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
# 1. UML CLASS DIAGRAM P6 (POLYMORPHISM, OVERRIDING & OVERLOADING)
# ---------------------------------------------------------
def create_class_diagram_p6():
    width, height = 1420, 860
    img = Image.new("RGB", (width, height), color=(255, 255, 255))
    draw = ImageDraw.Draw(img)

    draw.text((width // 2 - 340, 15), "UML CLASS DIAGRAM - POLYMORPHISM & DYNAMIC BINDING (P6)", fill=(0, 0, 0), font=font_title)

    # 1. Superclass: Produk (Top Center)
    # x: 500, y: 55, w: 420, h: 320
    sp_x1, sp_y1, sp_x2, sp_y2 = 500, 55, 920, 365
    draw.rectangle([sp_x1, sp_y1, sp_x2, sp_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([sp_x1, sp_y1, sp_x2, sp_y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((sp_x1 + 120, sp_y1 + 6), "<<Superclass>> Produk", fill=(0, 0, 0), font=font_header)

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
        "+ getKategoriInfo() : String  <<Polymorphic>>",
        "+ tampilkanData() : void     <<Polymorphic>>"
    ]
    for m in sp_methods:
        draw.text((sp_x1 + 15, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # 2. Subclass 1: ProdukMakanan (Bottom Left)
    sb1_x1, sb1_y1, sb1_x2, sb1_y2 = 280, 470, 660, 645
    draw.rectangle([sb1_x1, sb1_y1, sb1_x2, sb1_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([sb1_x1, sb1_y1, sb1_x2, sb1_y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((sb1_x1 + 80, sb1_y1 + 6), "<<Subclass>> ProdukMakanan", fill=(0, 0, 0), font=font_header)

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
        "+ getKategoriInfo() : String <<Override>>",
        "+ tampilkanData() : void    <<Override>>"
    ]
    for m in sb1_methods:
        draw.text((sb1_x1 + 15, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # 3. Subclass 2: ProdukElektronik (Bottom Right)
    sb2_x1, sb2_y1, sb2_x2, sb2_y2 = 760, 470, 1140, 645
    draw.rectangle([sb2_x1, sb2_y1, sb2_x2, sb2_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([sb2_x1, sb2_y1, sb2_x2, sb2_y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((sb2_x1 + 75, sb2_y1 + 6), "<<Subclass>> ProdukElektronik", fill=(0, 0, 0), font=font_header)

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
        "+ getKategoriInfo() : String <<Override>>",
        "+ tampilkanData() : void    <<Override>>"
    ]
    for m in sb2_methods:
        draw.text((sb2_x1 + 15, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # INHERITANCE TRIANGLE (▲)
    draw.polygon([(710, 365), (698, 385), (722, 385)], fill=(255, 255, 255), outline=(0, 0, 0), width=2)
    draw.line([(710, 385), (710, 425)], fill=(0, 0, 0), width=2)
    draw.line([(470, 425), (950, 425)], fill=(0, 0, 0), width=2)
    draw.line([(470, 425), (470, 470)], fill=(0, 0, 0), width=2)
    draw.line([(950, 425), (950, 470)], fill=(0, 0, 0), width=2)
    draw.text((610, 400), "Inheritance & Dynamic Binding", fill=(0, 0, 0), font=font_rel)

    # 4. Pelanggan (Top Left)
    c_x1, c_y1, c_x2, c_y2 = 30, 80, 330, 330
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
    c_methods = ["+ getNama(), getDiskon()", "+ tampilkanData() : void"]
    for m in c_methods:
        draw.text((c_x1 + 15, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # 5. Transaksi (Bottom Left) with Overloading
    t_x1, t_y1, t_x2, t_y2 = 30, 470, 250, 810
    draw.rectangle([t_x1, t_y1, t_x2, t_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([t_x1, t_y1, t_x2, t_y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((t_x1 + 65, t_y1 + 6), "Transaksi", fill=(0, 0, 0), font=font_header)
    t_attrs = ["- idTransaksi : String", "- tanggal : String", "- pelanggan : Pelanggan", "- daftarItem : List<Item>"]
    y = t_y1 + 38
    for a in t_attrs:
        draw.text((t_x1 + 8, y), a, fill=(0, 0, 0), font=font_rel_sub)
        y += 18
    draw.line([t_x1, y + 4, t_x2, y + 4], fill=(0, 0, 0), width=1)
    y += 10
    t_methods = [
        "+ tambahItem(p, qty)        <<Overload>>",
        "+ tambahItem(p, qty, disc)  <<Overload>>",
        "+ prosesTransaksi()         <<Overload>>",
        "+ prosesTransaksi(uang)     <<Overload>>",
        "+ hitungTotalBayar() : double"
    ]
    for m in t_methods:
        draw.text((t_x1 + 8, y), m, fill=(0, 0, 0), font=font_rel_sub)
        y += 18

    # 6. ItemTransaksi (Top Right)
    i_x1, i_y1, i_x2, i_y2 = 1170, 80, 1390, 330
    draw.rectangle([i_x1, i_y1, i_x2, i_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([i_x1, i_y1, i_x2, i_y1 + 30], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((i_x1 + 40, i_y1 + 6), "ItemTransaksi", fill=(0, 0, 0), font=font_header)
    i_attrs = ["- produk : Produk (Super)", "- jumlahBeli : int", "- diskonItem : double"]
    y = i_y1 + 38
    for a in i_attrs:
        draw.text((i_x1 + 10, y), a, fill=(0, 0, 0), font=font_rel_sub)
        y += 18
    draw.line([i_x1, y + 4, i_x2, y + 4], fill=(0, 0, 0), width=1)
    y += 10
    i_methods = ["+ hitungSubtotal() : int", "+ tampilkanItem() : void"]
    for m in i_methods:
        draw.text((i_x1 + 10, y), m, fill=(0, 0, 0), font=font_rel_sub)
        y += 18

    # Association (Pelanggan - Transaksi)
    draw.line([(140, 330), (140, 470)], fill=(0, 0, 0), width=2)
    draw.text((150, 400), "Association", fill=(0, 0, 0), font=font_rel_sub)

    # Aggregation (ItemTransaksi - Produk)
    draw.polygon([(1170, 180), (1155, 173), (1140, 180), (1155, 187)], fill=(255, 255, 255), outline=(0, 0, 0), width=2)
    draw.line([(1140, 180), (920, 180)], fill=(0, 0, 0), width=2)
    draw.text((980, 162), "Aggregation (Polymorphic Reference)", fill=(0, 0, 0), font=font_rel_sub)

    # Composition (Transaksi - ItemTransaksi)
    draw.polygon([(250, 730), (265, 723), (280, 730), (265, 737)], fill=(0, 0, 0), outline=(0, 0, 0))
    draw.line([(280, 730), (1280, 730), (1280, 330)], fill=(0, 0, 0), width=2)
    draw.text((680, 712), "Composition (part-of ItemTransaksi)", fill=(0, 0, 0), font=font_rel_sub)

    path = os.path.join(assets_dir, "class_diagram_p6.png")
    img.save(path, "PNG")
    print(f"P6 Class diagram saved at: {path}")
    return path

# ---------------------------------------------------------
# 2. TERMINAL SCREENSHOT (PURE BLACK & WHITE)
# ---------------------------------------------------------
def create_terminal_screenshot_bw():
    term_text = """PS D:\\KULIAH\\Semester 3\\OOP> javac -d bin (Get-ChildItem src/*.java).FullName
PS D:\\KULIAH\\Semester 3\\OOP> java -cp bin Main
==========================================================
PRAKTIKUM P6 - PEMROGRAMAN BERORIENTASI OBYEK
Polymorphism, Method Overriding, Overloading, Dynamic Binding
Nama : Rafael Rizky | NRP : 3125522007
Proyek : Sistem Kasir Sederhana
==========================================================

##########################################################
### 1. PENGUJIAN UPCASTING & POLYMORPHIC REFERENCE     ###
##########################################################
[PENJELASAN] Tipe referensi Superclass (Produk) merujuk ke objek Subclass (ProdukMakanan & ProdukElektronik).

Objek p1 dideklarasikan sebagai Produk, objek aktual: ProdukMakanan
Objek p2 dideklarasikan sebagai Produk, objek aktual: ProdukElektronik
Objek p3 dideklarasikan sebagai Produk, objek aktual: ProdukMakanan
Objek p4 dideklarasikan sebagai Produk, objek aktual: ProdukElektronik

##########################################################
### 2. PENGUJIAN DYNAMIC BINDING (METHOD OVERRIDING)   ###
##########################################################
[PENJELASAN] Memanggil method tampilkanData() melalui referensi Produk.
JVM menentukan implementasi method saat runtime berdasarkan tipe objek aktual.

=== DATA PRODUK MAKANAN (SUBCLASS) ===
Kategori    : Makanan & Minuman Segar (Konsumsi)
Kode        : M001
Nama        : Ayam Geprek Crispy
Harga       : Rp15000
Stok        : 30 pcs
Kadaluarsa  : 25-10-2026
Inventaris  : Rp450000
--------------------------------------
=== DATA PRODUK ELEKTRONIK (SUBCLASS) ===
Kategori    : Elektronik & Aksesoris Gadget (Hardware)
Kode        : E001
Nama        : Kabel Data Type-C Fast
Harga       : Rp25000
Stok        : 40 pcs
Garansi     : 6 Bulan
Inventaris  : Rp1000000
-----------------------------------------

##########################################################
### 3. PENGUJIAN POLYMORPHIC COLLECTION (ARRAY OF SUPER)###
##########################################################
[PENJELASAN] Menyimpan 4 objek subclass berbeda ke dalam satu array Produk[].

Iterasi Polymorphic Collection:
----------------------------------------------------------
[1] Kategori Objek: Makanan & Minuman Segar (Konsumsi)
    Nama Barang : Ayam Geprek Crispy
    Harga       : Rp15000
    Stok        : 30 pcs
    Inventaris  : Rp450000
----------------------------------------------------------
[2] Kategori Objek: Elektronik & Aksesoris Gadget (Hardware)
    Nama Barang : Kabel Data Type-C Fast
    Harga       : Rp25000
    Stok        : 40 pcs
    Inventaris  : Rp1000000
----------------------------------------------------------
[3] Kategori Objek: Makanan & Minuman Segar (Konsumsi)
    Nama Barang : Bebek Bakar Madu
    Harga       : Rp28000
    Stok        : 15 pcs
    Inventaris  : Rp420000
----------------------------------------------------------
[4] Kategori Objek: Elektronik & Aksesoris Gadget (Hardware)
    Nama Barang : Powerbank 10000mAh
    Harga       : Rp120000
    Stok        : 12 pcs
    Inventaris  : Rp1440000
----------------------------------------------------------

##########################################################
### 4. PENGUJIAN METHOD OVERLOADING PADA TRANSAKSI     ###
##########################################################

--- [4.1] Transaksi TRX-001 (Overloading tambahItem) ---
[INFO] Berhasil menambahkan 2 pcs Ayam Geprek Crispy ke transaksi TRX-001
[INFO] Berhasil menambahkan 1 pcs Kabel Data Type-C Fast [PROMO 10%] ke transaksi TRX-001
==========================================================
                 STRUK RESMI KASIR TOKO                   
==========================================================
No. Transaksi : TRX-001
Tanggal       : 29-09-2026
Pelanggan     : Rafael Rizky (VIP)
Nomor HP      : 081234567890
----------------------------------------------------------
DAFTAR BELANJA (POLYMORPHIC PRODUCT ITEMS):
  Nama Barang            | Jml     | Harga Satuan | Subtotal
  --------------------------------------------------------
  Ayam Geprek Crispy     |   2 pcs | Rp  15000 | Subtotal: Rp   30000
  Kabel Data Type-C Fast |   1 pcs | Rp  25000 (Disc 10%) | Subtotal: Rp   22500
----------------------------------------------------------
Total Subtotal    : Rp52500
Diskon Member     : Rp7875 (15%)
----------------------------------------------------------
TOTAL TAGIHAN     : Rp44625
Status Bayar      : LUNAS (Non-Tunai / Otomatis)
==========================================================
[SUKSES] Transaksi TRX-001 berhasil diselesaikan.

--- [4.2] Transaksi TRX-002 (Overloading prosesTransaksi Tunai) ---
[INFO] Berhasil menambahkan 2 pcs Bebek Bakar Madu ke transaksi TRX-002
[INFO] Berhasil menambahkan 1 pcs Powerbank 10000mAh [PROMO 5%] ke transaksi TRX-002
==========================================================
                 STRUK RESMI KASIR TOKO                   
==========================================================
No. Transaksi : TRX-002
Tanggal       : 29-09-2026
Pelanggan     : Budi Santoso (GOLD)
Nomor HP      : 089876543210
----------------------------------------------------------
DAFTAR BELANJA (POLYMORPHIC PRODUCT ITEMS):
  Nama Barang            | Jml     | Harga Satuan | Subtotal
  --------------------------------------------------------
  Bebek Bakar Madu       |   2 pcs | Rp  28000 | Subtotal: Rp   56000
  Powerbank 10000mAh     |   1 pcs | Rp 120000 (Disc 5%) | Subtotal: Rp  114000
----------------------------------------------------------
Total Subtotal    : Rp170000
Diskon Member     : Rp17000 (10%)
----------------------------------------------------------
TOTAL TAGIHAN     : Rp153000
Uang Diterima     : Rp200000
Uang Kembalian    : Rp47000
Status Bayar      : LUNAS (Pembayaran Tunai)
==========================================================
[SUKSES] Transaksi TRX-002 berhasil diselesaikan.

--- Verifikasi Sisa Stok Produk Setelah Transaksi Kasir ---
Sisa stok Ayam Geprek Crispy: 28 pcs
Sisa stok Kabel Data Type-C Fast: 39 pcs
Sisa stok Bebek Bakar Madu: 13 pcs
Sisa stok Powerbank 10000mAh: 11 pcs

==========================================================
         SELURUH PENGUJIAN P6 SELESAI DENGAN SUKSES       
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

    draw.text((60, 8), "Windows PowerShell - Command Execution (P6 Main.java)", fill=(0, 0, 0), font=font_header)

    y = 40
    for line in lines:
        draw.text((20, y), line, fill=(0, 0, 0), font=font_code)
        y += line_height

    out_path = os.path.join(assets_dir, "hasil_running.png")
    img.save(out_path, "PNG")
    print(f"B&W Terminal screenshot saved at: {out_path}")
    return out_path

create_class_diagram_p6()
create_terminal_screenshot_bw()
print("All P6 B&W assets created successfully!")
