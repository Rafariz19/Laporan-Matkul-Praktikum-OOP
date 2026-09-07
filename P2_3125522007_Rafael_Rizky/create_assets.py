import os
from PIL import Image, ImageDraw, ImageFont
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

base_dir = r"d:\KULIAH\Semester 3\OOP\P2_3125522007_Rafael_Rizky"
assets_dir = os.path.join(base_dir, "assets")
os.makedirs(assets_dir, exist_ok=True)

# ---------------------------------------------------------
# 1. GENERATE UML CLASS DIAGRAM IMAGE
# ---------------------------------------------------------
def create_class_diagram():
    width, height = 1350, 750
    img = Image.new("RGB", (width, height), color=(255, 255, 255))
    draw = ImageDraw.Draw(img)

    # Use default or system font
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

    # Draw diagram title
    draw.text((width // 2 - 180, 20), "CLASS DIAGRAM - SISTEM KASIR SEDERHANA", fill=(0, 0, 0), font=font_title)

    # Class 1: Produk (Left top)
    # x: 60, y: 70, w: 340, h: 260
    p_x1, p_y1, p_x2, p_y2 = 60, 80, 420, 370
    draw.rectangle([p_x1, p_y1, p_x2, p_y2], outline=(0, 0, 0), width=2, fill=(248, 249, 250))
    draw.rectangle([p_x1, p_y1, p_x2, p_y1 + 35], fill=(230, 235, 245), outline=(0, 0, 0), width=2)
    draw.text((p_x1 + 130, p_y1 + 8), "Produk", fill=(0, 0, 0), font=font_header)

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

    draw.line([p_x1, y_curr + 5, p_x2, y_curr + 5], fill=(0, 0, 0), width=1)
    y_curr += 15

    p_methods = [
        "+ Produk(kode, nama, harga, stok)",
        "+ tampilkanData() : void",
        "+ ubahHarga(hargaBaru : int) : void",
        "+ tambahStok(jumlah : int) : void",
        "+ kurangiStok(jumlah : int) : void",
        "+ hitungNilaiInventaris() : int",
        "+ getNama() : String"
    ]
    for m in p_methods:
        draw.text((p_x1 + 15, y_curr), m, fill=(0, 0, 0), font=font_body)
        y_curr += 20

    # Class 2: Pelanggan (Right top)
    # x: 920, y: 80, w: 360, h: 290
    c_x1, c_y1, c_x2, c_y2 = 920, 80, 1290, 370
    draw.rectangle([c_x1, c_y1, c_x2, c_y2], outline=(0, 0, 0), width=2, fill=(248, 249, 250))
    draw.rectangle([c_x1, c_y1, c_x2, c_y1 + 35], fill=(230, 235, 245), outline=(0, 0, 0), width=2)
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

    draw.line([c_x1, y_curr + 5, c_x2, y_curr + 5], fill=(0, 0, 0), width=1)
    y_curr += 15

    c_methods = [
        "+ Pelanggan(id, nama, noHP, tipe)",
        "+ tampilkanData() : void",
        "+ ubahNomorHP(nomorBaru : String) : void",
        "+ ubahTipeMember(tipeBaru : String) : void",
        "+ getDiskon() : double",
        "+ getNama() : String"
    ]
    for m in c_methods:
        draw.text((c_x1 + 15, y_curr), m, fill=(0, 0, 0), font=font_body)
        y_curr += 20

    # Class 3: Transaksi (Bottom Center)
    t_x1, t_y1, t_x2, t_y2 = 470, 430, 890, 710
    draw.rectangle([t_x1, t_y1, t_x2, t_y2], outline=(0, 0, 0), width=2, fill=(248, 249, 250))
    draw.rectangle([t_x1, t_y1, t_x2, t_y1 + 35], fill=(230, 235, 245), outline=(0, 0, 0), width=2)
    draw.text((t_x1 + 170, t_y1 + 8), "Transaksi", fill=(0, 0, 0), font=font_header)

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
        y_curr += 20

    draw.line([t_x1, y_curr + 5, t_x2, y_curr + 5], fill=(0, 0, 0), width=1)
    y_curr += 15

    t_methods = [
        "+ Transaksi(id, tgl, pelanggan, produk, qty)",
        "+ hitungSubtotal() : int",
        "+ hitungDiskonNominal() : double",
        "+ hitungTotalBayar() : double",
        "+ ubahJumlahBeli(jumlahBaru : int) : void",
        "+ prosesTransaksi() : void"
    ]
    for m in t_methods:
        draw.text((t_x1 + 15, y_curr), m, fill=(0, 0, 0), font=font_body)
        y_curr += 20

    # Draw Association Lines
    # Line from Transaksi to Produk: from t_x1 (470) at y=500 to p_x2 (420) at y=300
    draw.line([(470, 500), (300, 500), (300, 370)], fill=(50, 50, 50), width=2)
    # Arrowhead or diamond towards Transaksi or simple line
    draw.polygon([(300, 370), (295, 385), (305, 385)], fill=(50, 50, 50))
    draw.text((310, 390), "1", fill=(0, 0, 0), font=font_rel)
    draw.text((450, 480), "0..*", fill=(0, 0, 0), font=font_rel)
    draw.text((320, 510), "mengandung produk", fill=(70, 70, 70), font=font_rel)

    # Line from Transaksi to Pelanggan: from t_x2 (890) at y=500 to c_x1 (920) at y=370
    draw.line([(890, 500), (1050, 500), (1050, 370)], fill=(50, 50, 50), width=2)
    draw.polygon([(1050, 370), (1045, 385), (1055, 385)], fill=(50, 50, 50))
    draw.text((1060, 390), "1", fill=(0, 0, 0), font=font_rel)
    draw.text((870, 480), "0..*", fill=(0, 0, 0), font=font_rel)
    draw.text((910, 510), "dilakukan oleh pelanggan", fill=(70, 70, 70), font=font_rel)

    diagram_path = os.path.join(assets_dir, "class_diagram.png")
    img.save(diagram_path, "PNG")
    print(f"Class diagram saved at: {diagram_path}")
    return diagram_path

# ---------------------------------------------------------
# 2. GENERATE TERMINAL OUTPUT SCREENSHOT IMAGE
# ---------------------------------------------------------
def create_terminal_screenshot():
    term_text = """PS D:\\KULIAH\\Semester 3\\OOP> javac -d bin (Get-ChildItem src/*.java).FullName
PS D:\\KULIAH\\Semester 3\\OOP> java -cp bin Main
=================================================
PRAKTIKUM P2 - PEMROGRAMAN BERORIENTASI OBYEK
Sistem Kasir Sederhana
Nama : Rafael Rizky | NRP : 3125522007
=================================================

>>> 1. INISIALISASI OBJECT MELALUI CONSTRUCTOR <<<
=== DATA PRODUK ===
Kode        : P001
Nama        : Ayam Geprek
Harga       : Rp15000
Stok        : 20
-------------------------
=== DATA PRODUK ===
Kode        : P002
Nama        : Es Teh Manis
Harga       : Rp5000
Stok        : 50
-------------------------
=== DATA PELANGGAN ===
ID Pelanggan : C001
Nama         : Rafael Rizky
Nomor HP     : 081234567890
Tipe Member  : VIP
Diskon       : 15%
-------------------------
=== DATA PELANGGAN ===
ID Pelanggan : C002
Nama         : Budi Santoso
Nomor HP     : 089876543210
Tipe Member  : Reguler
Diskon       : 0%
-------------------------
>>> 2. PENGUJIAN METHOD DENGAN RETURN VALUE <<<
Total nilai inventaris produk Ayam Geprek: Rp300000
Total nilai inventaris produk Es Teh Manis: Rp250000
Diskon pelanggan Rafael Rizky: 15%
Diskon pelanggan Budi Santoso: 0%

>>> 3. PENGUJIAN METHOD DENGAN PARAMETER (UBAH DATA) <<<
[INFO] Harga produk Ayam Geprek berhasil diubah menjadi Rp16000
[INFO] Stok produk Ayam Geprek bertambah 10. Total stok saat ini: 30
[INFO] Nomor HP pelanggan Budi Santoso berhasil diperbarui menjadi: 081122334455
[INFO] Status member pelanggan Budi Santoso berhasil diubah menjadi: Gold

Data setelah perubahan:
=== DATA PRODUK ===
Kode        : P001
Nama        : Ayam Geprek
Harga       : Rp16000
Stok        : 30
-------------------------
=== DATA PELANGGAN ===
ID Pelanggan : C002
Nama         : Budi Santoso
Nomor HP     : 081122334455
Tipe Member  : Gold
Diskon       : 10%
-------------------------
>>> 4. INISIALISASI DAN PENGUJIAN OBJECT TRANSAKSI <<<
[INFO] Jumlah beli pada transaksi TRX-001 berhasil diubah menjadi: 4 pcs
Estimasi total bayar TRX-001 (via return value): Rp54400
Estimasi total bayar TRX-002 (via return value): Rp22500

>>> 5. MEMPROSES TRANSAKSI (STRUK & UPDATE STOK) <<<
========================================
         STRUK TRANSAKSI KASIR          
========================================
ID Transaksi  : TRX-001
Tanggal       : 07-09-2026
Pelanggan     : Rafael Rizky (VIP)
Produk        : Ayam Geprek
Harga Satuan  : Rp16000
Jumlah Beli   : 4 pcs
Subtotal      : Rp64000
Diskon Member : Rp9600 (15%)
----------------------------------------
TOTAL BAYAR   : Rp54400
========================================
[SUKSES] Transaksi TRX-001 berhasil diselesaikan.

========================================
         STRUK TRANSAKSI KASIR          
========================================
ID Transaksi  : TRX-002
Tanggal       : 07-09-2026
Pelanggan     : Budi Santoso (Gold)
Produk        : Es Teh Manis
Harga Satuan  : Rp5000
Jumlah Beli   : 5 pcs
Subtotal      : Rp25000
Diskon Member : Rp2500 (10%)
----------------------------------------
TOTAL BAYAR   : Rp22500
========================================
[SUKSES] Transaksi TRX-002 berhasil diselesaikan.

>>> 6. STATUS STOK AKHIR SETELAH TRANSAKSI <<<
=== DATA PRODUK ===
Kode        : P001
Nama        : Ayam Geprek
Harga       : Rp16000
Stok        : 26
-------------------------
=== DATA PRODUK ===
Kode        : P002
Nama        : Es Teh Manis
Harga       : Rp5000
Stok        : 45
-------------------------
=================================================
         PENGUJIAN SELESAI DENGAN SUKSES         
=================================================
"""
    lines = term_text.strip().split("\n")
    line_height = 18
    width = 900
    height = len(lines) * line_height + 60

    img = Image.new("RGB", (width, height), color=(30, 30, 30))
    draw = ImageDraw.Draw(img)

    # Window header bar
    draw.rectangle([0, 0, width, 32], fill=(45, 45, 45))
    # Window buttons
    draw.ellipse([12, 10, 22, 20], fill=(255, 95, 86))
    draw.ellipse([30, 10, 40, 20], fill=(255, 189, 46))
    draw.ellipse([48, 10, 58, 20], fill=(39, 201, 63))

    try:
        font_header = ImageFont.truetype("arialbd.ttf", 12)
        font_code = ImageFont.truetype("consola.ttf", 12)
    except:
        font_header = ImageFont.load_default()
        font_code = ImageFont.load_default()

    draw.text((80, 8), "Windows PowerShell - Running Main.java", fill=(200, 200, 200), font=font_header)

    y = 45
    for line in lines:
        if line.startswith("PS "):
            color = (255, 255, 255)
        elif line.startswith("===") or line.startswith("---") or line.startswith(">>>"):
            color = (78, 201, 176)
        elif "[SUKSES]" in line or "SELESAI DENGAN SUKSES" in line:
            color = (106, 153, 85)
        elif "[INFO]" in line:
            color = (86, 156, 214)
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

diagram_img = create_class_diagram()
term_img = create_terminal_screenshot()
print("Images created successfully!")
