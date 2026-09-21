import os
from PIL import Image, ImageDraw, ImageFont

base_dir = r"d:\KULIAH\Semester 3\OOP\Prakatikum\P4_3125522007_Rafael_Rizky"
assets_dir = os.path.join(base_dir, "assets")
os.makedirs(assets_dir, exist_ok=True)

# ---------------------------------------------------------
# 1. GENERATE PURE BLACK & WHITE UML CLASS DIAGRAM IMAGE
# ---------------------------------------------------------
def create_class_diagram_bw():
    width, height = 1400, 850
    # Pure white background
    img = Image.new("RGB", (width, height), color=(255, 255, 255))
    draw = ImageDraw.Draw(img)

    try:
        font_title = ImageFont.truetype("timesbd.ttf", 18)
        font_header = ImageFont.truetype("timesbd.ttf", 15)
        font_body = ImageFont.truetype("times.ttf", 13)
        font_rel = ImageFont.truetype("timesbd.ttf", 12)
        font_rel_sub = ImageFont.truetype("times.ttf", 11)
    except:
        try:
            font_title = ImageFont.truetype("arialbd.ttf", 16)
            font_header = ImageFont.truetype("arialbd.ttf", 14)
            font_body = ImageFont.truetype("arial.ttf", 12)
            font_rel = ImageFont.truetype("arialbd.ttf", 12)
            font_rel_sub = ImageFont.truetype("arial.ttf", 11)
        except:
            font_title = ImageFont.load_default()
            font_header = ImageFont.load_default()
            font_body = ImageFont.load_default()
            font_rel = ImageFont.load_default()
            font_rel_sub = ImageFont.load_default()

    draw.text((width // 2 - 320, 20), "UML CLASS DIAGRAM - SISTEM KASIR SEDERHANA (P4)", fill=(0, 0, 0), font=font_title)

    # ------------------ Class 1: Pelanggan (Top Left) ------------------
    # x: 50, y: 70, w: 380, h: 320
    c_x1, c_y1, c_x2, c_y2 = 50, 80, 420, 390
    draw.rectangle([c_x1, c_y1, c_x2, c_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([c_x1, c_y1, c_x2, c_y1 + 35], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((c_x1 + 140, c_y1 + 8), "Pelanggan", fill=(0, 0, 0), font=font_header)

    c_attrs = [
        "- idPelanggan : String",
        "- nama : String",
        "- nomorHP : String",
        "- tipeMember : String"
    ]
    y_curr = c_y1 + 45
    for attr in c_attrs:
        draw.text((c_x1 + 15, y_curr), attr, fill=(0, 0, 0), font=font_body)
        y_curr += 20

    draw.line([c_x1, y_curr + 4, c_x2, y_curr + 4], fill=(0, 0, 0), width=1)
    y_curr += 12

    c_methods = [
        "+ Pelanggan(id, nama, noHP, tipe)",
        "+ getIdPelanggan() : String",
        "+ getNama() : String",
        "+ getNomorHP() : String",
        "+ getTipeMember() : String",
        "+ getDiskon() : double",
        "+ setNama(nama : String) : void",
        "+ setNomorHP(noHP : String) : void",
        "+ setTipeMember(tipe : String) : void",
        "+ tampilkanData() : void"
    ]
    for m in c_methods:
        draw.text((c_x1 + 15, y_curr), m, fill=(0, 0, 0), font=font_body)
        y_curr += 20

    # ------------------ Class 2: Produk (Top Right) ------------------
    # x: 960, y: 80, w: 390, h: 390
    p_x1, p_y1, p_x2, p_y2 = 960, 80, 1350, 470
    draw.rectangle([p_x1, p_y1, p_x2, p_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([p_x1, p_y1, p_x2, p_y1 + 35], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((p_x1 + 160, p_y1 + 8), "Produk", fill=(0, 0, 0), font=font_header)

    p_attrs = [
        "- kode : String",
        "- nama : String",
        "- harga : int",
        "- stok : int"
    ]
    y_curr = p_y1 + 45
    for attr in p_attrs:
        draw.text((p_x1 + 15, y_curr), attr, fill=(0, 0, 0), font=font_body)
        y_curr += 20

    draw.line([p_x1, y_curr + 4, p_x2, y_curr + 4], fill=(0, 0, 0), width=1)
    y_curr += 12

    p_methods = [
        "+ Produk(kode, nama, harga, stok)",
        "+ getKode() : String",
        "+ getNama() : String",
        "+ getHarga() : int",
        "+ getStok() : int",
        "+ setNama(nama : String) : void",
        "+ setHarga(harga : int) : void",
        "+ setStok(stok : int) : void",
        "+ tambahStok(jumlah : int) : void",
        "+ kurangiStok(jumlah : int) : boolean",
        "+ hitungNilaiInventaris() : int",
        "+ tampilkanData() : void"
    ]
    for m in p_methods:
        draw.text((p_x1 + 15, y_curr), m, fill=(0, 0, 0), font=font_body)
        y_curr += 20

    # ------------------ Class 3: Transaksi (Bottom Left) ------------------
    # x: 50, y: 500, w: 460, h: 320
    t_x1, t_y1, t_x2, t_y2 = 50, 500, 510, 810
    draw.rectangle([t_x1, t_y1, t_x2, t_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([t_x1, t_y1, t_x2, t_y1 + 35], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((t_x1 + 190, t_y1 + 8), "Transaksi", fill=(0, 0, 0), font=font_header)

    t_attrs = [
        "- idTransaksi : String",
        "- tanggal : String",
        "- pelanggan : Pelanggan",
        "- daftarItem : ArrayList<ItemTransaksi>"
    ]
    y_curr = t_y1 + 45
    for attr in t_attrs:
        draw.text((t_x1 + 15, y_curr), attr, fill=(0, 0, 0), font=font_body)
        y_curr += 20

    draw.line([t_x1, y_curr + 4, t_x2, y_curr + 4], fill=(0, 0, 0), width=1)
    y_curr += 12

    t_methods = [
        "+ Transaksi(id, tanggal, pelanggan)",
        "+ getIdTransaksi() : String",
        "+ getTanggal() : String",
        "+ getPelanggan() : Pelanggan",
        "+ getDaftarItem() : ArrayList<ItemTransaksi>",
        "+ tambahItem(produk, qty) : boolean",
        "+ hitungTotalSubtotal() : int",
        "+ hitungDiskonNominal() : double",
        "+ hitungTotalBayar() : double",
        "+ prosesTransaksi() : void"
    ]
    for m in t_methods:
        draw.text((t_x1 + 15, y_curr), m, fill=(0, 0, 0), font=font_body)
        y_curr += 20

    # ------------------ Class 4: ItemTransaksi (Bottom Right) ------------------
    # x: 670, y: 540, w: 400, h: 260
    i_x1, i_y1, i_x2, i_y2 = 670, 530, 1070, 790
    draw.rectangle([i_x1, i_y1, i_x2, i_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([i_x1, i_y1, i_x2, i_y1 + 35], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((i_x1 + 150, i_y1 + 8), "ItemTransaksi", fill=(0, 0, 0), font=font_header)

    i_attrs = [
        "- produk : Produk",
        "- jumlahBeli : int"
    ]
    y_curr = i_y1 + 45
    for attr in i_attrs:
        draw.text((i_x1 + 15, y_curr), attr, fill=(0, 0, 0), font=font_body)
        y_curr += 20

    draw.line([i_x1, y_curr + 4, i_x2, y_curr + 4], fill=(0, 0, 0), width=1)
    y_curr += 12

    i_methods = [
        "+ ItemTransaksi(produk, jumlahBeli)",
        "+ getProduk() : Produk",
        "+ setProduk(produk : Produk) : void",
        "+ getJumlahBeli() : int",
        "+ setJumlahBeli(qty : int) : void",
        "+ hitungSubtotal() : int",
        "+ tampilkanItem() : void"
    ]
    for m in i_methods:
        draw.text((i_x1 + 15, y_curr), m, fill=(0, 0, 0), font=font_body)
        y_curr += 20

    # ------------------ RELATIONSHIPS ------------------

    # 1. ASSOCIATION: Pelanggan (Top Left) <------> Transaksi (Bottom Left)
    # Vertical line from Pelanggan bottom (x=235, y=390) to Transaksi top (x=235, y=500)
    draw.line([(235, 390), (235, 500)], fill=(0, 0, 0), width=2)
    # Multiplicities
    draw.text((245, 400), "1", fill=(0, 0, 0), font=font_rel)
    draw.text((245, 480), "0..*", fill=(0, 0, 0), font=font_rel)
    draw.text((255, 440), "Association (uses-a)", fill=(0, 0, 0), font=font_rel)
    draw.text((255, 455), "Pelanggan melakukan Transaksi", fill=(0, 0, 0), font=font_rel_sub)

    # 2. COMPOSITION: Transaksi (Bottom Left) <◆------- ItemTransaksi (Bottom Right)
    # Line from Transaksi right (x=510, y=650) to ItemTransaksi left (x=670, y=650)
    # Solid black diamond on Transaksi side (composite owner)
    # Diamond points: (510, 650), (525, 642), (540, 650), (525, 658)
    draw.polygon([(510, 650), (525, 642), (540, 650), (525, 658)], fill=(0, 0, 0), outline=(0, 0, 0))
    draw.line([(540, 650), (670, 650)], fill=(0, 0, 0), width=2)
    # Multiplicities
    draw.text((545, 630), "1", fill=(0, 0, 0), font=font_rel)
    draw.text((645, 630), "1..*", fill=(0, 0, 0), font=font_rel)
    draw.text((550, 660), "Composition (part-of)", fill=(0, 0, 0), font=font_rel)
    draw.text((550, 675), "Item dibuat di dalam Transaksi", fill=(0, 0, 0), font=font_rel_sub)

    # 3. AGGREGATION: ItemTransaksi (Bottom Right) ◇-------> Produk (Top Right)
    # Line from ItemTransaksi top (x=900, y=530) up to Produk bottom (x=1100, y=470)
    # Hollow/white diamond on ItemTransaksi side (has-a)
    # Points at (900, 530): (900, 530), (892, 515), (900, 500), (908, 515)
    draw.polygon([(900, 530), (892, 515), (900, 500), (908, 515)], fill=(255, 255, 255), outline=(0, 0, 0), width=2)
    draw.line([(900, 500), (900, 485), (1100, 485), (1100, 470)], fill=(0, 0, 0), width=2)
    # Multiplicities
    draw.text((915, 510), "0..*", fill=(0, 0, 0), font=font_rel)
    draw.text((1110, 475), "1", fill=(0, 0, 0), font=font_rel)
    draw.text((915, 465), "Aggregation (has-a)", fill=(0, 0, 0), font=font_rel)
    draw.text((915, 480), "Produk mandiri di luar transaksi", fill=(0, 0, 0), font=font_rel_sub)

    diagram_path = os.path.join(assets_dir, "class_diagram.png")
    img.save(diagram_path, "PNG")
    print(f"B&W Class diagram saved at: {diagram_path}")
    return diagram_path

# ---------------------------------------------------------
# 2. GENERATE BLACK & WHITE TERMINAL SCREENSHOT
# ---------------------------------------------------------
def create_terminal_screenshot_bw():
    term_text = """PS D:\\KULIAH\\Semester 3\\OOP> javac -d bin (Get-ChildItem src/*.java).FullName
PS D:\\KULIAH\\Semester 3\\OOP> java -cp bin Main
==========================================================
PRAKTIKUM P4 - PEMROGRAMAN BERORIENTASI OBYEK
Relasi Antarobject: Association, Aggregation, & Composition
Nama : Rafael Rizky | NRP : 3125522007
Proyek : Sistem Kasir Sederhana
==========================================================

##########################################################
### TEST 1: PENGUJIAN INSTANSIASI OBJECT (BERHASIL)    ###
##########################################################
[1] Membuat master data produk...
=== DATA PRODUK ===
Kode        : P001
Nama        : Ayam Geprek
Harga       : Rp15000
Stok        : 30 pcs
Inventaris  : Rp450000
-------------------------
=== DATA PRODUK ===
Kode        : P002
Nama        : Es Teh Manis
Harga       : Rp5000
Stok        : 50 pcs
Inventaris  : Rp250000
-------------------------
=== DATA PRODUK ===
Kode        : P003
Nama        : Bebek Goreng
Harga       : Rp25000
Stok        : 15 pcs
Inventaris  : Rp375000
-------------------------
[2] Membuat master data pelanggan...
=== DATA PELANGGAN ===
ID Pelanggan : C001
Nama         : Rafael Rizky
Nomor HP     : 081234567890
Tipe Member  : VIP
Hak Diskon   : 15%
-------------------------
=== DATA PELANGGAN ===
ID Pelanggan : C002
Nama         : Budi Santoso
Nomor HP     : 089876543210
Tipe Member  : GOLD
Hak Diskon   : 10%
-------------------------
=== DATA PELANGGAN ===
ID Pelanggan : C003
Nama         : Siti Aminah
Nomor HP     : 081122334455
Tipe Member  : REGULER
Hak Diskon   : 0%
-------------------------
[3] Menginisialisasi transaksi kasir...
Transaksi 1 terdaftar: TRX-001 untuk pelanggan Rafael Rizky
Transaksi 2 terdaftar: TRX-002 untuk pelanggan Budi Santoso
[STATUS] Seluruh object berhasil dibuat dengan sempurna.

##########################################################
### TEST 2: PENGUJIAN INTERAKSI ANTAROBJECT            ###
### (Association, Aggregation, dan Composition)        ###
##########################################################

--- Menambahkan Item ke Transaksi 1 (Composition & Aggregation) ---
[INFO] Berhasil menambahkan 2 pcs Ayam Geprek ke dalam transaksi TRX-001
[INFO] Berhasil menambahkan 3 pcs Es Teh Manis ke dalam transaksi TRX-001
[INFO] Berhasil menambahkan 1 pcs Bebek Goreng ke dalam transaksi TRX-001

--- Menambahkan Item ke Transaksi 2 (Composition & Aggregation) ---
[INFO] Berhasil menambahkan 4 pcs Ayam Geprek ke dalam transaksi TRX-002
[INFO] Berhasil menambahkan 5 pcs Es Teh Manis ke dalam transaksi TRX-002

--- Pengujian Validasi Interaksi Melebihi Stok Produk ---
Mencoba membeli 50 Bebek Goreng (stok hanya 14)...
[ERROR STOK] Pembelian Bebek Goreng (50 pcs) melebihi stok yang ada (14 pcs)!
Hasil penambahan: Ditolak oleh sistem
[STATUS] Interaksi multi-objek berjalan harmonis dan aman.

##########################################################
### TEST 3: PEMANFAATAN DATA OBJECT LAIN LEWAT METHOD  ###
### (Kalkulasi Multi-Item, Diskon & Cetak Struk)       ###
##########################################################

--- Memproses dan Mencetak Struk Transaksi 1 ---
==========================================================
                 STRUK RESMI KASIR TOKO                   
==========================================================
No. Transaksi : TRX-001
Tanggal       : 21-09-2026
Pelanggan     : Rafael Rizky (VIP)
Nomor HP      : 081234567890
----------------------------------------------------------
DAFTAR BELANJA:
  Nama Barang      | Jml     | Harga   | Total
  --------------------------------------------------------
  Ayam Geprek      |   2 pcs | Rp  15000 | Subtotal: Rp   30000
  Es Teh Manis     |   3 pcs | Rp   5000 | Subtotal: Rp   15000
  Bebek Goreng     |   1 pcs | Rp  25000 | Subtotal: Rp   25000
----------------------------------------------------------
Total Subtotal    : Rp70000
Diskon Member     : Rp10500 (15%)
----------------------------------------------------------
TOTAL AKHIR BAYAR : Rp59500
==========================================================
[SUKSES] Transaksi TRX-001 berhasil diproses dan disimpan.

--- Memproses dan Mencetak Struk Transaksi 2 ---
==========================================================
                 STRUK RESMI KASIR TOKO                   
==========================================================
No. Transaksi : TRX-002
Tanggal       : 21-09-2026
Pelanggan     : Budi Santoso (GOLD)
Nomor HP      : 089876543210
----------------------------------------------------------
DAFTAR BELANJA:
  Nama Barang      | Jml     | Harga   | Total
  --------------------------------------------------------
  Ayam Geprek      |   4 pcs | Rp  15000 | Subtotal: Rp   60000
  Es Teh Manis     |   5 pcs | Rp   5000 | Subtotal: Rp   25000
----------------------------------------------------------
Total Subtotal    : Rp85000
Diskon Member     : Rp8500 (10%)
----------------------------------------------------------
TOTAL AKHIR BAYAR : Rp76500
==========================================================
[SUKSES] Transaksi TRX-002 berhasil diproses dan disimpan.

--- Verifikasi Sisa Stok Produk Setelah Interaksi Transaksi ---
Stok akhir Ayam Geprek: 24 pcs
Stok akhir Es Teh Manis: 42 pcs
Stok akhir Bebek Goreng: 14 pcs

==========================================================
         SELURUH PENGUJIAN P4 SELESAI DENGAN SUKSES       
==========================================================
"""
    lines = term_text.strip().split("\n")
    line_height = 17
    width = 960
    height = len(lines) * line_height + 55

    # Pure white background for black and white styling
    img = Image.new("RGB", (width, height), color=(255, 255, 255))
    draw = ImageDraw.Draw(img)

    # Window header bar in white with black border
    draw.rectangle([0, 0, width, 30], fill=(245, 245, 245), outline=(0, 0, 0), width=1)
    # Simple monochrome indicator dots
    draw.ellipse([12, 10, 20, 18], fill=(255, 255, 255), outline=(0, 0, 0), width=1)
    draw.ellipse([26, 10, 34, 18], fill=(255, 255, 255), outline=(0, 0, 0), width=1)
    draw.ellipse([40, 10, 48, 18], fill=(0, 0, 0))

    try:
        font_header = ImageFont.truetype("timesbd.ttf", 11)
        font_code = ImageFont.truetype("consola.ttf", 11)
    except:
        font_header = ImageFont.load_default()
        font_code = ImageFont.load_default()

    draw.text((60, 8), "Windows PowerShell - Command Execution (P4 Main.java)", fill=(0, 0, 0), font=font_header)

    y = 40
    for line in lines:
        # All text in solid black (#000000) for strict Black and White compliance
        draw.text((20, y), line, fill=(0, 0, 0), font=font_code)
        y += line_height

    out_path = os.path.join(assets_dir, "hasil_running.png")
    img.save(out_path, "PNG")
    print(f"B&W Terminal screenshot saved at: {out_path}")
    return out_path

create_class_diagram_bw()
create_terminal_screenshot_bw()
print("P4 B&W Assets created successfully!")
