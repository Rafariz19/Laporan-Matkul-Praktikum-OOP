import os
from reportlab.lib.pagesizes import letter, A4
from reportlab.lib.units import inch
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image as RLImage, PageBreak, KeepTogether
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

pdf_path = r"d:\KULIAH\Semester 3\OOP\P2_3125522007_Rafael_Rizky\README.pdf"
assets_dir = r"d:\KULIAH\Semester 3\OOP\P2_3125522007_Rafael_Rizky\assets"

doc = SimpleDocTemplate(
    pdf_path,
    pagesize=A4,
    leftMargin=0.65 * inch,
    rightMargin=0.65 * inch,
    topMargin=0.6 * inch,
    bottomMargin=0.6 * inch
)

styles = getSampleStyleSheet()

# Custom styles with Times-Roman
title_style = ParagraphStyle(
    'DocTitle',
    fontName='Times-Bold',
    fontSize=14,
    leading=17,
    alignment=1, # Center
    spaceAfter=2
)

subtitle_style = ParagraphStyle(
    'DocSubtitle',
    fontName='Times-Bold',
    fontSize=11,
    leading=14,
    alignment=1,
    spaceAfter=10
)

h1_style = ParagraphStyle(
    'H1',
    fontName='Times-Bold',
    fontSize=11,
    leading=14,
    spaceBefore=4,
    spaceAfter=3
)

body_style = ParagraphStyle(
    'Body',
    fontName='Times-Roman',
    fontSize=9.5,
    leading=12.5,
    spaceAfter=3
)

caption_style = ParagraphStyle(
    'Caption',
    fontName='Times-Italic',
    fontSize=8.5,
    leading=11,
    alignment=1,
    spaceAfter=4
)

cell_style = ParagraphStyle(
    'Cell',
    fontName='Times-Roman',
    fontSize=8.5,
    leading=10.5
)

cell_bold = ParagraphStyle(
    'CellBold',
    fontName='Times-Bold',
    fontSize=8.5,
    leading=10.5
)

story = []

# =========================================================================
# HALAMAN 1
# =========================================================================
story.append(Paragraph("Tugas Praktikum OOP - Modul 2", title_style))
story.append(Paragraph("Implementasi Class, Object, Attribute, Method, dan Constructor", subtitle_style))

id_table_data = [
    [Paragraph("<b>Nama</b> : Rafael Rizky", cell_style), Paragraph("<b>Dosen</b> : Nirwana Haidar Hari, S.Pd., M.Kom.", cell_style)],
    [Paragraph("<b>NRP</b>  : 3125522007", cell_style), Paragraph("<b>Kampus</b> : PENS PSDKU Sumenep (2026)", cell_style)]
]
t_id = Table(id_table_data, colWidths=[2.8 * inch, 4.2 * inch])
t_id.setStyle(TableStyle([
    ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
    ('BOTTOMPADDING', (0,0), (-1,-1), 1),
    ('TOPPADDING', (0,0), (-1,-1), 1),
    ('LEFTPADDING', (0,0), (-1,-1), 0),
    ('RIGHTPADDING', (0,0), (-1,-1), 0),
]))
story.append(t_id)
story.append(Spacer(1, 8))

story.append(Paragraph("HALAMAN 1 — ANALISIS & SPRINT PLANNING", h1_style))
story.append(Spacer(1, 2))

story.append(Paragraph("<b>1. Nama Proyek</b>", h1_style))
story.append(Paragraph("Sistem Kasir Sederhana", body_style))
story.append(Spacer(1, 3))

story.append(Paragraph("<b>2. Product Goal</b>", h1_style))
story.append(Paragraph("Membuat aplikasi kasir sederhana untuk membantu mengelola data produk, memelihara informasi pelanggan, serta mendukung proses pencatatan dan perhitungan transaksi penjualan secara akurat, mudah, dan teratur.", body_style))
story.append(Spacer(1, 3))

story.append(Paragraph("<b>3. Aktor & User Story (Review P1)</b>", h1_style))
story.append(Paragraph("• <b>Kasir</b>: Menggunakan data produk dalam transaksi penjualan, melayani pembelian, dan mencetak nota.<br/>• <b>Pemilik/Admin</b>: Menambah dan mengelola stok produk, harga, serta informasi pelanggan.", body_style))

pb_table_data = [
    [Paragraph("ID", cell_bold), Paragraph("User Story", cell_bold), Paragraph("Prioritas", cell_bold)],
    [Paragraph("US-01", cell_style), Paragraph("Sebagai admin, saya ingin menambahkan data produk, sehingga data produk dapat dikelola.", cell_style), Paragraph("High", cell_style)],
    [Paragraph("US-02", cell_style), Paragraph("Sebagai kasir, saya ingin melihat daftar produk, sehingga saya dapat mengetahui produk yang tersedia.", cell_style), Paragraph("High", cell_style)],
    [Paragraph("US-03", cell_style), Paragraph("Sebagai admin, saya ingin mengubah data produk, sehingga informasi produk tetap sesuai.", cell_style), Paragraph("Medium", cell_style)],
    [Paragraph("US-04", cell_style), Paragraph("Sebagai kasir, saya ingin memproses transaksi penjualan, sehingga transaksi dapat dicatat.", cell_style), Paragraph("High", cell_style)],
    [Paragraph("US-05", cell_style), Paragraph("Sebagai admin, saya ingin melihat data stok produk, sehingga dapat mengetahui ketersediaan produk.", cell_style), Paragraph("Medium", cell_style)]
]
t_pb = Table(pb_table_data, colWidths=[0.7 * inch, 5.4 * inch, 0.9 * inch])
t_pb.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#EAEAEA")),
    ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CCCCCC")),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 2),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
]))
story.append(t_pb)
story.append(Spacer(1, 5))

story.append(Paragraph("<b>4. Sprint Goal (P2)</b>", h1_style))
story.append(Paragraph("Mengimplementasikan class utama proyek (Produk, Pelanggan, Transaksi) ke dalam bahasa Java sehingga object dapat diinstansiasi, diisi datanya melalui constructor, serta menjalankan operasi method berparameter dan return value.", body_style))
story.append(Spacer(1, 3))

story.append(Paragraph("<b>5. Sprint Backlog (P2)</b>", h1_style))
sb_table_data = [
    [Paragraph("ID", cell_bold), Paragraph("Sprint Backlog", cell_bold), Paragraph("Status", cell_bold)],
    [Paragraph("SB-01", cell_style), Paragraph("Membuat class Produk (atribut kode, nama, harga, stok)", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-02", cell_style), Paragraph("Membuat class Pelanggan (atribut idPelanggan, nama, nomorHP, tipeMember)", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-03", cell_style), Paragraph("Membuat class Transaksi (atribut idTransaksi, tanggal, pelanggan, produk, jumlahBeli)", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-04", cell_style), Paragraph("Membuat constructor untuk inisialisasi object pada ketiga class", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-05", cell_style), Paragraph("Membuat method tanpa parameter, dengan parameter, dan return value", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-06", cell_style), Paragraph("Membuat class Main untuk instansiasi minimal 2 object per class dan pengujian", cell_style), Paragraph("DONE", cell_bold)]
]
t_sb = Table(sb_table_data, colWidths=[0.8 * inch, 5.2 * inch, 1.0 * inch])
t_sb.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#EAEAEA")),
    ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CCCCCC")),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 2),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
]))
story.append(t_sb)

# =========================================================================
# HALAMAN 2
# =========================================================================
story.append(PageBreak())
story.append(Paragraph("HALAMAN 2 — REFINEMENT OBJECT & CLASS DIAGRAM", h1_style))
story.append(Spacer(1, 3))

story.append(Paragraph("<b>6. Tabel Refinement Object</b>", h1_style))
ref_data = [
    [Paragraph("Class", cell_bold), Paragraph("Attribute", cell_bold), Paragraph("Method", cell_bold), Paragraph("Constructor", cell_bold)],
    [
        Paragraph("<b>Produk</b>", cell_style),
        Paragraph("• kode (String)<br/>• nama (String)<br/>• harga (int)<br/>• stok (int)", cell_style),
        Paragraph("• tampilkanData()<br/>• ubahHarga(int)<br/>• tambahStok(int)<br/>• kurangiStok(int)<br/>• hitungNilaiInventaris()<br/>• getNama()", cell_style),
        Paragraph("Produk(kode, nama, harga, stok)", cell_style)
    ],
    [
        Paragraph("<b>Pelanggan</b>", cell_style),
        Paragraph("• idPelanggan (String)<br/>• nama (String)<br/>• nomorHP (String)<br/>• tipeMember (String)", cell_style),
        Paragraph("• tampilkanData()<br/>• ubahNomorHP(String)<br/>• ubahTipeMember(String)<br/>• getDiskon()<br/>• getNama()", cell_style),
        Paragraph("Pelanggan(id, nama, nomorHP, tipeMember)", cell_style)
    ],
    [
        Paragraph("<b>Transaksi</b>", cell_style),
        Paragraph("• idTransaksi (String)<br/>• tanggal (String)<br/>• pelanggan (Pelanggan)<br/>• produk (Produk)<br/>• jumlahBeli (int)", cell_style),
        Paragraph("• hitungSubtotal()<br/>• hitungDiskonNominal()<br/>• hitungTotalBayar()<br/>• ubahJumlahBeli(int)<br/>• prosesTransaksi()", cell_style),
        Paragraph("Transaksi(id, tgl, pelanggan, produk, qty)", cell_style)
    ]
]
t_ref = Table(ref_data, colWidths=[1.1 * inch, 1.7 * inch, 2.3 * inch, 1.9 * inch])
t_ref.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#EAEAEA")),
    ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CCCCCC")),
    ('VALIGN', (0, 0), (-1, -1), 'TOP'),
    ('TOPPADDING', (0, 0), (-1, -1), 3),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
]))
story.append(t_ref)
story.append(Spacer(1, 8))

story.append(Paragraph("<b>7. Diagram Sederhana Class yang Dibuat</b>", h1_style))
diagram_img_path = os.path.join(assets_dir, "class_diagram.png")
if os.path.exists(diagram_img_path):
    story.append(RLImage(diagram_img_path, width=6.8 * inch, height=3.7 * inch))
    story.append(Paragraph("Gambar 1. UML Class Diagram Relasi Objek Kasir", caption_style))

# =========================================================================
# HALAMAN 3
# =========================================================================
story.append(PageBreak())
story.append(Paragraph("HALAMAN 3 — HASIL RUNNING, SPRINT REVIEW & RETROSPECTIVE", h1_style))
story.append(Spacer(1, 3))

story.append(Paragraph("<b>8. Screenshot Hasil Running Program (Main.java)</b>", h1_style))
term_img_path = os.path.join(assets_dir, "hasil_running.png")
if os.path.exists(term_img_path):
    story.append(RLImage(term_img_path, width=6.5 * inch, height=4.2 * inch))
    story.append(Paragraph("Gambar 2. Bukti Running Program Main.java di Terminal", caption_style))
    story.append(Spacer(1, 4))

story.append(Paragraph("<b>9. Sprint Review</b>", h1_style))
rev_table_data = [
    [Paragraph("Item", cell_bold), Paragraph("Hasil", cell_bold)],
    [Paragraph("Class berhasil dibuat", cell_style), Paragraph("Class Produk, Pelanggan, Transaksi, dan Main berhasil dibuat.", cell_style)],
    [Paragraph("Object berhasil dibuat", cell_style), Paragraph("Minimal 2 object per class berhasil diinstansiasi di Main.", cell_style)],
    [Paragraph("Constructor berjalan", cell_style), Paragraph("Constructor berjalan dengan baik untuk inisialisasi data.", cell_style)],
    [Paragraph("Method berjalan", cell_style), Paragraph("Method tanpa parameter, berparameter, dan return value berfungsi normal.", cell_style)],
    [Paragraph("Program dapat dijalankan", cell_style), Paragraph("Berhasil dikompilasi (javac) dan dijalankan (java) tanpa error.", cell_style)],
    [Paragraph("Kendala", cell_style), Paragraph("Tidak ada kendala berarti. Kompilasi wildcard pada shell telah disesuaikan.", cell_style)]
]
t_rev = Table(rev_table_data, colWidths=[2.2 * inch, 4.8 * inch])
t_rev.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#EAEAEA")),
    ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CCCCCC")),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 2),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
]))
story.append(t_rev)
story.append(Spacer(1, 5))

story.append(Paragraph("<b>10. Kendala yang Ditemukan & Sprint Retrospective</b>", h1_style))
retro_text = (
    "<b>What Went Well?</b> Implementasi seluruh class, constructor berparameter, serta pemanggilan method berjalan lancar dan terstruktur dengan rapi.<br/>"
    "<b>What Went Wrong?</b> Seluruh atribut masih belum dienkapsulasi (akses default), sehingga nilai atribut belum terlindungi oleh validasi data.<br/>"
    "<b>Improvement:</b> Pada sprint berikutnya (P3), class akan disempurnakan dengan enkapsulasi (access modifier private, getter-setter, dan validasi nilai)."
)
story.append(Paragraph(retro_text, body_style))

doc.build(story)
print(f"PDF successfully generated: {pdf_path}")
