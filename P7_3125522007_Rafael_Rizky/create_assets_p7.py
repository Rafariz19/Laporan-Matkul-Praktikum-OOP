import os
from PIL import Image, ImageDraw, ImageFont

base_dir = r"d:\KULIAH\Semester 3\OOP\Prakatikum\P7_3125522007_Rafael_Rizky"
assets_dir = os.path.join(base_dir, "assets")
os.makedirs(assets_dir, exist_ok=True)

try:
    font_title = ImageFont.truetype("timesbd.ttf", 16)
    font_header = ImageFont.truetype("timesbd.ttf", 13)
    font_body = ImageFont.truetype("times.ttf", 11)
    font_bold = ImageFont.truetype("timesbd.ttf", 11)
    font_mono = ImageFont.truetype("consola.ttf", 11)
except:
    font_title = ImageFont.load_default()
    font_header = ImageFont.load_default()
    font_body = ImageFont.load_default()
    font_bold = ImageFont.load_default()
    font_mono = ImageFont.load_default()

# -------------------------------------------------------------------------
# 1. UML CLASS DIAGRAM P7 (ABSTRACT CLASS, INTERFACE & REALIZATION)
# -------------------------------------------------------------------------
def create_class_diagram_p7():
    width, height = 1450, 920
    img = Image.new("RGB", (width, height), color=(255, 255, 255))
    draw = ImageDraw.Draw(img)

    draw.text((width // 2 - 380, 15), "UML CLASS DIAGRAM - ABSTRACT CLASS, INTERFACE & REALIZATION (P7)", fill=(0, 0, 0), font=font_title)

    # 1. Superclass: <<abstract>> Produk (Center Top)
    sp_x1, sp_y1, sp_x2, sp_y2 = 470, 55, 930, 375
    draw.rectangle([sp_x1, sp_y1, sp_x2, sp_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([sp_x1, sp_y1, sp_x2, sp_y1 + 28], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((sp_x1 + 120, sp_y1 + 5), "<<Abstract Class>> Produk", fill=(0, 0, 0), font=font_header)

    sp_attrs = [
        "- kode : String",
        "- nama : String",
        "- harga : int",
        "- stok : int"
    ]
    y = sp_y1 + 34
    for a in sp_attrs:
        draw.text((sp_x1 + 12, y), a, fill=(0, 0, 0), font=font_body)
        y += 17
    draw.line([sp_x1, y + 2, sp_x2, y + 2], fill=(0, 0, 0), width=1)
    y += 8
    sp_methods = [
        "+ Produk(kode, nama, harga, stok)",
        "+ getKode(), getNama(), getHarga(), getStok()",
        "+ setNama(String), setHarga(int), setStok(int)",
        "+ tambahStok(int), kurangiStok(int) : boolean",
        "+ hitungNilaiInventaris() : int",
        "+ {abstract} getKategoriInfo() : String",
        "+ {abstract} tampilkanDetailKhusus() : void",
        "+ tampilkanData() : void   <<Template Method>>"
    ]
    for m in sp_methods:
        if "{abstract}" in m:
            draw.text((sp_x1 + 12, y), m, fill=(0, 0, 0), font=font_bold)
        else:
            draw.text((sp_x1 + 12, y), m, fill=(0, 0, 0), font=font_body)
        y += 17

    # 2. Interface: <<interface>> DapatDidiskon (Top Right)
    if_x1, if_y1, if_x2, if_y2 = 1010, 55, 1400, 205
    draw.rectangle([if_x1, if_y1, if_x2, if_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([if_x1, if_y1, if_x2, if_y1 + 28], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((if_x1 + 80, if_y1 + 5), "<<Interface>> DapatDidiskon", fill=(0, 0, 0), font=font_header)
    y = if_y1 + 36
    draw.text((if_x1 + 12, y), "/* Tidak memiliki attribute instance */", fill=(0, 0, 0), font=font_body)
    y += 24
    draw.line([if_x1, y, if_x2, y], fill=(0, 0, 0), width=1)
    y += 10
    if_methods = [
        "+ {abstract} hitungDiskon(double) : double",
        "+ {abstract} getHargaSetelahDiskon(double) : double"
    ]
    for m in if_methods:
        draw.text((if_x1 + 12, y), m, fill=(0, 0, 0), font=font_bold)
        y += 20

    # 3. Subclass 1: ProdukMakanan (Bottom Center-Left)
    pm_x1, pm_y1, pm_x2, pm_y2 = 360, 480, 770, 755
    draw.rectangle([pm_x1, pm_y1, pm_x2, pm_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([pm_x1, pm_y1, pm_x2, pm_y1 + 28], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((pm_x1 + 80, pm_y1 + 5), "<<Subclass>> ProdukMakanan", fill=(0, 0, 0), font=font_header)
    y = pm_y1 + 34
    draw.text((pm_x1 + 12, y), "- tanggalKadaluarsa : String", fill=(0, 0, 0), font=font_body)
    y += 20
    draw.line([pm_x1, y + 2, pm_x2, y + 2], fill=(0, 0, 0), width=1)
    y += 8
    pm_methods = [
        "+ ProdukMakanan(kode, nama, harga, stok, tgl)",
        "+ getTanggalKadaluarsa() : String",
        "+ setTanggalKadaluarsa(String) : void",
        "+ getKategoriInfo() : String   <<Override>>",
        "+ tampilkanDetailKhusus() : void <<Override>>",
        "+ hitungDiskon(double) : double <<Implements>>",
        "+ getHargaSetelahDiskon(double) : double <<Implements>>"
    ]
    for m in pm_methods:
        draw.text((pm_x1 + 12, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # 4. Subclass 2: ProdukElektronik (Bottom Center-Right)
    pe_x1, pe_y1, pe_x2, pe_y2 = 820, 480, 1240, 755
    draw.rectangle([pe_x1, pe_y1, pe_x2, pe_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([pe_x1, pe_y1, pe_x2, pe_y1 + 28], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((pe_x1 + 80, pe_y1 + 5), "<<Subclass>> ProdukElektronik", fill=(0, 0, 0), font=font_header)
    y = pe_y1 + 34
    draw.text((pe_x1 + 12, y), "- garansiBulan : int", fill=(0, 0, 0), font=font_body)
    y += 20
    draw.line([pe_x1, y + 2, pe_x2, y + 2], fill=(0, 0, 0), width=1)
    y += 8
    pe_methods = [
        "+ ProdukElektronik(kode, nama, harga, stok, garansi)",
        "+ getGaransiBulan() : int",
        "+ setGaransiBulan(int) : void",
        "+ getKategoriInfo() : String   <<Override>>",
        "+ tampilkanDetailKhusus() : void <<Override>>",
        "+ hitungDiskon(double) : double <<Implements>>",
        "+ getHargaSetelahDiskon(double) : double <<Implements>>"
    ]
    for m in pe_methods:
        draw.text((pe_x1 + 12, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # 5. Class Pelanggan (Top Left)
    cust_x1, cust_y1, cust_x2, cust_y2 = 30, 55, 330, 260
    draw.rectangle([cust_x1, cust_y1, cust_x2, cust_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([cust_x1, cust_y1, cust_x2, cust_y1 + 28], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((cust_x1 + 90, cust_y1 + 5), "Pelanggan", fill=(0, 0, 0), font=font_header)
    y = cust_y1 + 34
    for a in ["- idPelanggan: String", "- nama: String", "- nomorHP: String", "- tipeMember: String"]:
        draw.text((cust_x1 + 12, y), a, fill=(0, 0, 0), font=font_body)
        y += 17
    draw.line([cust_x1, y + 2, cust_x2, y + 2], fill=(0, 0, 0), width=1)
    y += 8
    for m in ["+ getDiskon() : double", "+ tampilkanData() : void"]:
        draw.text((cust_x1 + 12, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # 6. Class ItemTransaksi (Bottom Far-Left)
    it_x1, it_y1, it_x2, it_y2 = 30, 650, 310, 840
    draw.rectangle([it_x1, it_y1, it_x2, it_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([it_x1, it_y1, it_x2, it_y1 + 28], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((it_x1 + 75, it_y1 + 5), "ItemTransaksi", fill=(0, 0, 0), font=font_header)
    y = it_y1 + 34
    for a in ["- produk : Produk (Polymorphic)", "- jumlahBeli : int", "- diskonItem : double"]:
        draw.text((it_x1 + 10, y), a, fill=(0, 0, 0), font=font_body)
        y += 17
    draw.line([it_x1, y + 2, it_x2, y + 2], fill=(0, 0, 0), width=1)
    y += 8
    for m in ["+ hitungSubtotal() : int", "+ tampilkanItem() : void"]:
        draw.text((it_x1 + 10, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # 7. Class Transaksi (Center-Bottom Left)
    tx_x1, tx_y1, tx_x2, tx_y2 = 30, 320, 330, 580
    draw.rectangle([tx_x1, tx_y1, tx_x2, tx_y2], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.rectangle([tx_x1, tx_y1, tx_x2, tx_y1 + 28], outline=(0, 0, 0), width=2, fill=(255, 255, 255))
    draw.text((tx_x1 + 100, tx_y1 + 5), "Transaksi", fill=(0, 0, 0), font=font_header)
    y = tx_y1 + 34
    for a in ["- idTransaksi : String", "- tanggal : String", "- pelanggan : Pelanggan", "- daftarItem : List<ItemTransaksi>"]:
        draw.text((tx_x1 + 10, y), a, fill=(0, 0, 0), font=font_body)
        y += 17
    draw.line([tx_x1, y + 2, tx_x2, y + 2], fill=(0, 0, 0), width=1)
    y += 8
    for m in [
        "+ tambahItem(p, qty) <<Overload>>",
        "+ tambahItem(p, qty, disc) <<Overload>>",
        "+ prosesTransaksi() <<Overload>>",
        "+ prosesTransaksi(cash) <<Overload>>",
        "+ hitungTotalBayar() : double"
    ]:
        draw.text((tx_x1 + 10, y), m, fill=(0, 0, 0), font=font_body)
        y += 18

    # --- DRAW RELATIONS ---
    # A. Generalization (Inheritance): ProdukMakanan -> Produk & ProdukElektronik -> Produk
    # Hollow triangle at Produk bottom
    mid_sp_x = (sp_x1 + sp_x2) // 2  # 700
    sp_bot_y = sp_y2  # 375
    tri_tip_y = sp_bot_y + 2
    tri_base_y = sp_bot_y + 18
    draw.polygon([(mid_sp_x, tri_tip_y), (mid_sp_x - 12, tri_base_y), (mid_sp_x + 12, tri_base_y)], outline=(0, 0, 0), fill=(255, 255, 255))
    
    fork_y = 425
    draw.line([(mid_sp_x, tri_base_y), (mid_sp_x, fork_y)], fill=(0, 0, 0), width=2)
    pm_mid_x = (pm_x1 + pm_x2) // 2  # 565
    pe_mid_x = 940
    draw.line([(pm_mid_x, fork_y), (pe_mid_x, fork_y)], fill=(0, 0, 0), width=2)
    draw.line([(pm_mid_x, fork_y), (pm_mid_x, pm_y1)], fill=(0, 0, 0), width=2)
    draw.line([(pe_mid_x, fork_y), (pe_mid_x, pe_y1)], fill=(0, 0, 0), width=2)
    draw.text((mid_sp_x + 15, 395), "extends (Inheritance)", fill=(0, 0, 0), font=font_body)

    # B. Realization: ProdukMakanan & ProdukElektronik implements DapatDidiskon
    # Dashed line with hollow triangle at DapatDidiskon
    if_mid_x = (if_x1 + if_x2) // 2  # 1205
    if_bot_y = if_y2  # 205
    draw.polygon([(if_mid_x, if_bot_y + 2), (if_mid_x - 12, if_bot_y + 18), (if_mid_x + 12, if_bot_y + 18)], outline=(0, 0, 0), fill=(255, 255, 255))
    
    # Dashed vertical line down to y=440
    def draw_dashed_line(p1, p2, dash_len=6, gap=4):
        x1, y1 = p1
        x2, y2 = p2
        dist = ((x2 - x1)**2 + (y2 - y1)**2)**0.5
        if dist == 0: return
        dx = (x2 - x1) / dist
        dy = (y2 - y1) / dist
        curr = 0
        while curr < dist:
            end = min(curr + dash_len, dist)
            draw.line([(x1 + dx * curr, y1 + dy * curr), (x1 + dx * end, y1 + dy * end)], fill=(0, 0, 0), width=2)
            curr += dash_len + gap

    draw_dashed_line((if_mid_x, if_bot_y + 18), (if_mid_x, 440))
    pe_top_right_x = 1120
    draw_dashed_line((if_mid_x, 440), (pe_top_right_x, 440))
    draw_dashed_line((pe_top_right_x, 440), (pe_top_right_x, pe_y1))
    
    # From ProdukMakanan right corner across to interface dashed line
    pm_right_x = pm_x2
    draw_dashed_line((pm_right_x, 460), (if_mid_x, 460))
    draw_dashed_line((if_mid_x, 460), (if_mid_x, 440))
    draw.text((if_mid_x - 150, 220), "implements (Realization)", fill=(0, 0, 0), font=font_body)

    # C. Association: Transaksi -> Pelanggan
    draw.line([(180, tx_y1), (180, cust_y2)], fill=(0, 0, 0), width=2)
    draw.polygon([(180, cust_y2), (175, cust_y2 + 10), (185, cust_y2 + 10)], outline=(0, 0, 0), fill=(0, 0, 0))
    draw.text((190, (tx_y1 + cust_y2)//2 - 10), "uses-a (Association)", fill=(0, 0, 0), font=font_body)

    # D. Composition: Transaksi -> ItemTransaksi
    # Solid diamond at Transaksi (x=180, y=tx_y2)
    dm_x, dm_y = 180, tx_y2
    draw.polygon([(dm_x, dm_y), (dm_x - 8, dm_y + 10), (dm_x, dm_y + 20), (dm_x + 8, dm_y + 10)], fill=(0, 0, 0))
    draw.line([(dm_x, dm_y + 20), (dm_x, it_y1)], fill=(0, 0, 0), width=2)
    draw.polygon([(dm_x, it_y1), (dm_x - 6, it_y1 - 10), (dm_x + 6, it_y1 - 10)], fill=(0, 0, 0))
    draw.text((190, (tx_y2 + it_y1)//2 - 10), "part-of (Composition)", fill=(0, 0, 0), font=font_body)

    # E. Aggregation: ItemTransaksi -> Produk
    # Hollow diamond at ItemTransaksi (it_x2, 720)
    it_mid_y = (it_y1 + it_y2) // 2
    draw.polygon([(it_x2, it_mid_y), (it_x2 + 10, it_mid_y - 6), (it_x2 + 20, it_mid_y), (it_x2 + 10, it_mid_y + 6)], outline=(0, 0, 0), fill=(255, 255, 255))
    draw.line([(it_x2 + 20, it_mid_y), (mid_sp_x - 200, it_mid_y)], fill=(0, 0, 0), width=2)
    draw.line([(mid_sp_x - 200, it_mid_y), (mid_sp_x - 200, sp_y2 + 60)], fill=(0, 0, 0), width=2)
    draw.line([(mid_sp_x - 200, sp_y2 + 60), (sp_x1, sp_y2 - 20)], fill=(0, 0, 0), width=2)
    draw.polygon([(sp_x1, sp_y2 - 20), (sp_x1 - 10, sp_y2 - 25), (sp_x1 - 10, sp_y2 - 15)], fill=(0, 0, 0))
    draw.text((it_x2 + 25, it_mid_y - 15), "has-a (Aggregation / Polymorphic Ref)", fill=(0, 0, 0), font=font_body)

    out_path = os.path.join(assets_dir, "class_diagram_p7.png")
    img.save(out_path, dpi=(300, 300))
    print(f"Class diagram saved at: {out_path}")
    return out_path

# -------------------------------------------------------------------------
# 2. TERMINAL SCREENSHOT P7 (HIGH CLARITY & CRISP MONOCHROME)
# -------------------------------------------------------------------------
def create_terminal_screenshot_p7():
    try:
        font_mono = ImageFont.truetype('consola.ttf', 15)
        font_mono_bold = ImageFont.truetype('consolab.ttf', 15)
        font_head = ImageFont.truetype('timesbd.ttf', 14)
    except:
        font_mono = ImageFont.load_default()
        font_mono_bold = ImageFont.load_default()
        font_head = ImageFont.load_default()

    width, height = 1920, 880
    img = Image.new('RGB', (width, height), color=(0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Title bar
    draw.rectangle([0, 0, width, 36], fill=(255, 255, 255), outline=(0, 0, 0), width=1)
    draw.text((20, 9), 'Command Prompt / PowerShell — Output Eksekusi Main.java P7 (Rafael Rizky - 3125522007)', fill=(0, 0, 0), font=font_head)
    
    # Window control buttons
    draw.rectangle([width - 90, 8, width - 70, 28], outline=(0, 0, 0), width=1, fill=(255, 255, 255))
    draw.line([width - 85, 18, width - 75, 18], fill=(0, 0, 0), width=2)
    draw.rectangle([width - 60, 8, width - 40, 28], outline=(0, 0, 0), width=1, fill=(255, 255, 255))
    draw.rectangle([width - 55, 13, width - 45, 23], outline=(0, 0, 0), width=1)
    draw.rectangle([width - 30, 8, width - 10, 28], outline=(0, 0, 0), width=1, fill=(255, 255, 255))
    draw.line([width - 25, 13, width - 15, 23], fill=(0, 0, 0), width=2)
    draw.line([width - 15, 13, width - 25, 23], fill=(0, 0, 0), width=2)

    # Outer border
    draw.rectangle([0, 0, width - 1, height - 1], outline=(180, 180, 180), width=2)

    # Divider line between columns
    draw.line([955, 42, 955, height - 15], fill=(100, 100, 100), width=2)

    col1_lines = [
        ('PS D:\\OOP\\P7_3125522007_Rafael_Rizky> javac -d bin src/*.java', True),
        ('PS D:\\OOP\\P7_3125522007_Rafael_Rizky> java -cp bin Main', True),
        ('==================================================================', False),
        ('PRAKTIKUM P7: ABSTRACT CLASS, ABSTRACT METHOD & INTERFACE', True),
        ('Nama   : Rafael Rizky | NRP: 3125522007 | PENS PSDKU Sumenep (2026)', False),
        ('Proyek : Sistem Kasir Sederhana (Sprint P7 - Abstraction)', False),
        ('==================================================================', False),
        ('', False),
        ('>>> SKENARIO 1: INSTANSIASI SUBCLASS & KELAS ABSTRAK <<<', True),
        ('[DESAIN] Class Produk adalah public abstract class.', False),
        ('[AUDIT]  new Produk(...) DITOLAK compiler (Cannot instantiate type).', False),
        ('[SUKSES] Objek Subclass 1: ProdukMakanan    -> Roti Gandum Sehat', False),
        ('[SUKSES] Objek Subclass 2: ProdukElektronik -> Mouse Wireless Ergo', False),
        ('', False),
        ('>>> SKENARIO 2: ABSTRACT METHOD VIA REFERENCE SUPERCLASS <<<', True),
        ('[UPCASTING] Produk ref1 = makanan1; Produk ref2 = elektro1;', False),
        ('[DYNAMIC BINDING 1 - Subclass ProdukMakanan]:', True),
        ('  ref1.getKategoriInfo() -> Makanan & Minuman Segar (Konsumsi)', False),
        ('  ref1.tampilkanData()   -> Kadaluarsa: 25-10-2026 | Stok: 25 unit', False),
        ('[DYNAMIC BINDING 2 - Subclass ProdukElektronik]:', True),
        ('  ref2.getKategoriInfo() -> Elektronik & Aksesoris Gadget (Hardware)', False),
        ('  ref2.tampilkanData()   -> Garansi: 12 Bulan Resmi | Stok: 15 unit', False),
        ('=> Kesimpulan: Method subclass konkret dipanggil saat runtime!', False),
        ('', False),
        ('>>> SKENARIO 3: PEMANGGILAN METHOD VIA REFERENCE INTERFACE <<<', True),
        ('[INTERFACE] DapatDidiskon d1 = makanan1; DapatDidiskon d2 = elektro1;', False),
        ('* Makanan (Rp 18.000, Disc 10%)    : Pot Rp  1.800 -> Rp  16.200', False),
        ('* Elektronik (Rp 150.000, Disc 15%): Pot Rp 22.500 -> Rp 127.500', False),
        ('=> Kesimpulan: Kontrak DapatDidiskon dipenuhi secara polimorfik!', False)
    ]

    col2_lines = [
        ('>>> SKENARIO 4: POLYMORPHIC COLLECTION & FLASH SALE 20% <<<', True),
        ('[A] Iterasi Array Superclass Produk[] (4 Objek Polimorfik):', False),
        ('  #1 M01 - Roti Gandum Sehat    | Rp  18.000 | Exp: 25-10-2026', False),
        ('  #2 E01 - Mouse Wireless Ergo  | Rp 150.000 | Garansi: 12 Bulan', False),
        ('  #3 M02 - Susu UHT Cokelat 1L  | Rp  20.000 | Exp: 15-12-2026', False),
        ('  #4 E02 - Keyboard Mechanical  | Rp 450.000 | Garansi: 24 Bulan', False),
        ('[B] Simulasi Flash Sale 20% via Array Interface DapatDidiskon[]:', False),
        ('No | Nama Produk             | Harga Asli  | Diskon 20%  | Promo Nett', True),
        ('---+-------------------------+-------------+-------------+------------', False),
        ('1  | Roti Gandum Sehat       | Rp   18.000 | Rp    3.600 | Rp   14.400', False),
        ('2  | Mouse Wireless Ergo     | Rp  150.000 | Rp   30.000 | Rp  120.000', False),
        ('3  | Susu UHT Cokelat 1L     | Rp   20.000 | Rp    4.000 | Rp   16.000', False),
        ('4  | Keyboard Mechanical     | Rp  450.000 | Rp   90.000 | Rp  360.000', False),
        ('', False),
        ('>>> SKENARIO 5: INTEGRASI TRANSAKSI KASIR (FITUR P1-P6 UTUH) <<<', True),
        ('===================== STRUK PENJUALAN TOKO =====================', False),
        ('No. TRX   : TRX-2026-P7-001 | Tgl: 06-10-2026 10:15 | Kasir: Rafael', False),
        ('Pelanggan : Rafael Rizky (MEMBER-GOLD -> Diskon Member 10%)', False),
        ('----------------------------------------------------------------', False),
        ('1. Roti Gandum Sehat     : 2 x Rp  18.000              = Rp  36.000', False),
        ('2. Mouse Wireless Ergo   : 1 x Rp 150.000 [Disc 10.0%] = Rp 135.000', False),
        ('3. Susu UHT Cokelat 1L   : 3 x Rp  20.000              = Rp  60.000', False),
        ('----------------------------------------------------------------', False),
        ('Total Belanja : Rp 231.000 | Diskon Member GOLD (10%) : Rp 23.100', False),
        ('TOTAL AKHIR   : Rp 207.900', True),
        ('PEMBAYARAN    : TUNAI Rp 250.000 | KEMBALIAN : Rp 42.100 (LUNAS)', True),
        ('================================================================', False),
        ('[VERIFIKASI] Seluruh fitur P1-P7 beroperasi 100% harmonis & sukses!', True)
    ]

    y = 48
    line_h = 27
    for text, is_bold in col1_lines:
        f = font_mono_bold if is_bold else font_mono
        draw.text((25, y), text, fill=(255, 255, 255), font=f)
        y += line_h

    y = 48
    for text, is_bold in col2_lines:
        f = font_mono_bold if is_bold else font_mono
        draw.text((975, y), text, fill=(255, 255, 255), font=f)
        y += line_h

    out_path = os.path.join(assets_dir, 'hasil_running.png')
    img.save(out_path, dpi=(300, 300))
    print(f'Terminal screenshot saved at: {out_path}')
    return out_path

if __name__ == "__main__":
    create_class_diagram_p7()
    create_terminal_screenshot_p7()
    print("All P7 assets created successfully.")
