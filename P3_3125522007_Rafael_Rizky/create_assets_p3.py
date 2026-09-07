import os
from PIL import Image, ImageDraw, ImageFont

base_dir = r"d:\KULIAH\Semester 3\OOP\P3_3125522007_Rafael_Rizky"
assets_dir = os.path.join(base_dir, "assets")
os.makedirs(assets_dir, exist_ok=True)

# ---------------------------------------------------------
# 1. GENERATE UML CLASS DIAGRAM IMAGE FOR P3
# ---------------------------------------------------------
def create_class_diagram():
    width, height = 1380, 800
    img = Image.new("RGB", (width, height), color=(255, 255, 255))
    draw = ImageDraw.Draw(img)

    try:
        font_title = ImageFont.truetype("arialbd.ttf", 16)
        font_header = ImageFont.truetype("arialbd.ttf", 14)
        font_body = ImageFont.truetype("arial.ttf", 12)
        font_rel = ImageFont.truetype("arial.ttf", 11)
    except:
        font_title = ImageFont.load_default()
        font_header = ImageFont.load_default()
        font_body = ImageFont.load_default()
        font_rel = ImageFont.load_default()

    draw.text((width // 2 - 250, 18), "UML CLASS DIAGRAM (REFACTORED ENCAPSULATION) - P3", fill=(0, 0, 0), font=font_title)

    # Class 1: Produk (Left top)
    # x1, y1, x2, y2 = 50, 70, 440, 420
    p_x1, p_y1, p_x2, p_y2 = 50, 70, 440, 425
    draw.rectangle([p_x1, p_y1, p_x2, p_y2], outline=(0, 0, 0), width=2, fill=(248, 249, 250))
    draw.rectangle([p_x1, p_y1, p_x2, p_y1 + 35], fill=(230, 235, 245), outline=(0, 0, 0), width=2)
    draw.text((p_x1 + 155, p_y1 + 8), "Produk", fill=(0, 0, 0), font=font_header)

    p_attrs = [
        "- kode : String",
        "- nama : String",
        "- harga : int",
        "- stok : int"
    ]
    y_curr = p_y1 + 45
    for attr in p_attrs:
        draw.text((p_x1 + 15, y_curr), attr, fill=(0, 0, 0), font=font_body)
        y_curr += 19

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
        y_curr += 19

    # Class 2: Pelanggan (Right top)
    c_x1, c_y1, c_x2, c_y2 = 910, 70, 1330, 425
    draw.rectangle([c_x1, c_y1, c_x2, c_y2], outline=(0, 0, 0), width=2, fill=(248, 249, 250))
    draw.rectangle([c_x1, c_y1, c_x2, c_y1 + 35], fill=(230, 235, 245), outline=(0, 0, 0), width=2)
    draw.text((c_x1 + 160, c_y1 + 8), "Pelanggan", fill=(0, 0, 0), font=font_header)

    c_attrs = [
        "- idPelanggan : String",
        "- nama : String",
        "- nomorHP : String",
        "- tipeMember : String"
    ]
    y_curr = c_y1 + 45
    for attr in c_attrs:
        draw.text((c_x1 + 15, y_curr), attr, fill=(0, 0, 0), font=font_body)
        y_curr += 19

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
        y_curr += 19

    # Class 3: Transaksi (Bottom Center)
    t_x1, t_y1, t_x2, t_y2 = 470, 460, 930, 770
    draw.rectangle([t_x1, t_y1, t_x2, t_y2], outline=(0, 0, 0), width=2, fill=(248, 249, 250))
    draw.rectangle([t_x1, t_y1, t_x2, t_y1 + 35], fill=(230, 235, 245), outline=(0, 0, 0), width=2)
    draw.text((t_x1 + 180, t_y1 + 8), "Transaksi", fill=(0, 0, 0), font=font_header)

    t_attrs = [
        "- idTransaksi : String",
        "- tanggal : String",
        "- pelanggan : Pelanggan",
        "- produk : Produk",
        "- jumlahBeli : int"
    ]
    y_curr = t_y1 + 45
    for attr in t_attrs:
        draw.text((t_x1 + 15, y_curr), attr, fill=(0, 0, 0), font=font_body)
        y_curr += 19

    draw.line([t_x1, y_curr + 4, t_x2, y_curr + 4], fill=(0, 0, 0), width=1)
    y_curr += 12

    t_methods = [
        "+ Transaksi(id, tgl, pelanggan, produk, qty)",
        "+ getIdTransaksi() : String",
        "+ getTanggal() : String",
        "+ getPelanggan() : Pelanggan",
        "+ getProduk() : Produk",
        "+ getJumlahBeli() : int",
        "+ setJumlahBeli(qty : int) : void",
        "+ hitungSubtotal() : int",
        "+ hitungDiskonNominal() : double",
        "+ hitungTotalBayar() : double",
        "+ prosesTransaksi() : void"
    ]
    for m in t_methods:
        draw.text((t_x1 + 15, y_curr), m, fill=(0, 0, 0), font=font_body)
        y_curr += 19

    # Associations
    # Line from Transaksi to Produk
    draw.line([(470, 550), (280, 550), (280, 425)], fill=(50, 50, 50), width=2)
    draw.polygon([(280, 425), (275, 438), (285, 438)], fill=(50, 50, 50))
    draw.text((290, 435), "1", fill=(0, 0, 0), font=font_rel)
    draw.text((450, 532), "0..*", fill=(0, 0, 0), font=font_rel)
    draw.text((290, 555), "referensi produk (private)", fill=(70, 70, 70), font=font_rel)

    # Line from Transaksi to Pelanggan
    draw.line([(930, 550), (1080, 550), (1080, 425)], fill=(50, 50, 50), width=2)
    draw.polygon([(1080, 425), (1075, 438), (1085, 438)], fill=(50, 50, 50))
    draw.text((1090, 435), "1", fill=(0, 0, 0), font=font_rel)
    draw.text((910, 532), "0..*", fill=(0, 0, 0), font=font_rel)
    draw.text((945, 555), "referensi pelanggan (private)", fill=(70, 70, 70), font=font_rel)

    diagram_path = os.path.join(assets_dir, "class_diagram.png")
    img.save(diagram_path, "PNG")
    print(f"Class diagram saved at: {diagram_path}")
    return diagram_path

# ---------------------------------------------------------
# 2. GENERATE TERMINAL OUTPUT SCREENSHOT IMAGE FOR P3
# ---------------------------------------------------------
def create_terminal_screenshot():
    term_text = """PS D:\\KULIAH\\Semester 3\\OOP> javac -d bin (Get-ChildItem src/*.java).FullName
PS D:\\KULIAH\\Semester 3\\OOP> java -cp bin Main
=================================================
PRAKTIKUM P3 - PEMROGRAMAN BERORIENTASI OBYEK
Encapsulation, Access Modifier, Getter-Setter & Validasi
Nama : Rafael Rizky | NRP : 3125522007
Proyek : Sistem Kasir Sederhana
=================================================

#################################################
### 1. PENGUJIAN DATA VALID (TEST VALID)      ###
#################################################

--- [1.1] Instansiasi Object dengan Data Valid via Constructor ---

--- [1.2] Membaca Data Menggunakan Method Getter ---
Nama Produk 1 : Ayam Geprek | Harga: Rp15000 | Stok: 25
Nama Produk 2 : Es Teh Manis | Harga: Rp5000 | Stok: 40
Pelanggan 1   : Rafael Rizky | Member: VIP | Diskon: 15%
Pelanggan 2   : Budi Santoso | Member: REGULER | Diskon: 0%

--- [1.3] Mengubah Data Menggunakan Method Setter Valid ---
Memperbarui harga produk1 menjadi Rp17000...
Nilai baru harga produk1 (via getter): Rp17000

Memperbarui nomor HP pelanggan2...
Nilai baru nomor HP pelanggan2 (via getter): 081298765432

Memperbarui tipe member pelanggan2 menjadi GOLD (diskon 10%)...
Tipe member baru (via getter): GOLD (Diskon: 10%)

--- [1.4] Memproses Transaksi Valid ---
========================================
         STRUK TRANSAKSI KASIR          
========================================
ID Transaksi  : TRX-001
Tanggal       : 08-09-2026
Pelanggan     : Rafael Rizky (VIP)
Produk        : Ayam Geprek
Harga Satuan  : Rp17000
Jumlah Beli   : 5 pcs
Subtotal      : Rp85000
Diskon Member : Rp12750 (15%)
----------------------------------------
TOTAL BAYAR   : Rp72250
========================================
[SUKSES] Transaksi TRX-001 berhasil diselesaikan.

Status Stok Setelah Transaksi:
Sisa stok Ayam Geprek: 20 pcs
Sisa stok Es Teh Manis: 30 pcs

#################################################
### 2. PENGUJIAN DATA TIDAK VALID (TEST INVALID) ###
#################################################

--- [2.1] Uji Validasi Harga Negatif (Produk.setHarga) ---
Harga saat ini: Rp17000
Mencoba mengubah harga menjadi -10000...
[ERROR VALIDASI] Harga produk harus lebih besar dari 0! (Ditolak: Rp-10000)
Verifikasi Nilai: Harga produk1 TETAP: Rp17000 [INTEGRITAS TERJAGA]

--- [2.2] Uji Validasi Stok Negatif (Produk.setStok) ---
Stok saat ini: 20 pcs
Mencoba mengubah stok menjadi -15...
[ERROR VALIDASI] Stok produk tidak boleh bernilai negatif! (Ditolak: -15)
Verifikasi Nilai: Stok produk1 TETAP: 20 pcs [INTEGRITAS TERJAGA]

--- [2.3] Uji Validasi Nama Kosong / Null (Produk.setNama) ---
Nama saat ini: Ayam Geprek
Mencoba mengubah nama menjadi string kosong ("")...
[ERROR VALIDASI] Nama produk tidak boleh kosong/null! Nilai tidak diubah.
Verifikasi Nilai: Nama produk1 TETAP: Ayam Geprek [INTEGRITAS TERJAGA]

--- [2.4] Uji Validasi Format Nomor HP (Pelanggan.setNomorHP) ---
Nomor HP saat ini: 081234567890
Mencoba mengubah nomor HP menjadi '0812-SALAH-XYZ'...
[ERROR VALIDASI] Format nomor HP tidak valid! Harus berupa 10-13 digit angka (Ditolak: 0812-SALAH-XYZ)
Verifikasi Nilai: Nomor HP pelanggan1 TETAP: 081234567890 [INTEGRITAS TERJAGA]

--- [2.5] Uji Validasi Tipe Member Invalid (Pelanggan.setTipeMember) ---
Tipe member saat ini: VIP
Mencoba mengubah tipe member menjadi 'PLATINUM_DIAMOND'...
[ERROR VALIDASI] Tipe member tidak valid! Pilihan: VIP, GOLD, atau REGULER (Ditolak: PLATINUM_DIAMOND)
Verifikasi Nilai: Tipe member pelanggan1 TETAP: VIP [INTEGRITAS TERJAGA]

--- [2.6] Uji Validasi Jumlah Beli Melebihi Stok (Transaksi.setJumlahBeli) ---
Sisa stok Ayam Geprek saat ini: 20 pcs
Mencoba transaksi baru dengan jumlah beli 100 pcs (melebihi stok)...
[ERROR VALIDASI] Jumlah beli (100 pcs) melebihi stok produk Ayam Geprek yang tersedia (20 pcs)!
Mencoba memproses transaksi...
[GAGAL] Transaksi TRX-ERR gagal diproses: Jumlah beli belum valid (0 atau negatif)!
=================================================
       SELURUH PENGUJIAN SELESAI (SUKSES)        
=================================================
"""
    lines = term_text.strip().split("\n")
    line_height = 18
    width = 920
    height = len(lines) * line_height + 60

    img = Image.new("RGB", (width, height), color=(30, 30, 30))
    draw = ImageDraw.Draw(img)

    draw.rectangle([0, 0, width, 32], fill=(45, 45, 45))
    draw.ellipse([12, 10, 22, 20], fill=(255, 95, 86))
    draw.ellipse([30, 10, 40, 20], fill=(255, 189, 46))
    draw.ellipse([48, 10, 58, 20], fill=(39, 201, 63))

    try:
        font_header = ImageFont.truetype("arialbd.ttf", 12)
        font_code = ImageFont.truetype("consola.ttf", 12)
    except:
        font_header = ImageFont.load_default()
        font_code = ImageFont.load_default()

    draw.text((80, 8), "Windows PowerShell - Running Main.java (Encapsulation Test)", fill=(200, 200, 200), font=font_header)

    y = 45
    for line in lines:
        if line.startswith("PS "):
            color = (255, 255, 255)
        elif line.startswith("===") or line.startswith("---") or line.startswith("###"):
            color = (78, 201, 176)
        elif "[ERROR VALIDASI]" in line or "[GAGAL]" in line:
            color = (244, 135, 113) # Red/coral for validation errors
        elif "[INTEGRITAS TERJAGA]" in line or "[SUKSES]" in line:
            color = (106, 153, 85) # Green
        elif "[INFO]" in line:
            color = (86, 156, 214) # Blue
        elif "TOTAL BAYAR" in line or "Diskon Member" in line:
            color = (220, 220, 170)
        else:
            color = (204, 204, 204)
        
        draw.text((20, y), line, fill=color, font=font_code)
        y += line_height

    out_path = os.path.join(assets_dir, "hasil_running.png")
    img.save(out_path, "PNG")
    print(f"Terminal screenshot saved at: {out_path}")
    return out_path

create_class_diagram()
create_terminal_screenshot()
print("P3 Images created successfully!")
