import os
from reportlab.lib.pagesizes import A4
from reportlab.lib.units import inch
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image as RLImage, PageBreak
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

pdf_path = r"d:\KULIAH\Semester 3\OOP\P3_3125522007_Rafael_Rizky\README.pdf"
assets_dir = r"d:\KULIAH\Semester 3\OOP\P3_3125522007_Rafael_Rizky\assets"

doc = SimpleDocTemplate(
    pdf_path,
    pagesize=A4,
    leftMargin=0.65 * inch,
    rightMargin=0.65 * inch,
    topMargin=0.6 * inch,
    bottomMargin=0.6 * inch
)

styles = getSampleStyleSheet()

title_style = ParagraphStyle(
    'DocTitle',
    fontName='Times-Bold',
    fontSize=13,
    leading=16,
    alignment=1,
    spaceAfter=2
)

subtitle_style = ParagraphStyle(
    'DocSubtitle',
    fontName='Times-Bold',
    fontSize=10.5,
    leading=13,
    alignment=1,
    spaceAfter=8
)

h1_style = ParagraphStyle(
    'H1',
    fontName='Times-Bold',
    fontSize=10.5,
    leading=13,
    spaceBefore=3,
    spaceAfter=2
)

body_style = ParagraphStyle(
    'Body',
    fontName='Times-Roman',
    fontSize=9,
    leading=11.5,
    spaceAfter=2
)

caption_style = ParagraphStyle(
    'Caption',
    fontName='Times-Italic',
    fontSize=8,
    leading=10,
    alignment=1,
    spaceAfter=3
)

cell_style = ParagraphStyle(
    'Cell',
    fontName='Times-Roman',
    fontSize=8,
    leading=10
)

cell_bold = ParagraphStyle(
    'CellBold',
    fontName='Times-Bold',
    fontSize=8,
    leading=10
)

story = []

# =========================================================================
# HALAMAN 1: ANALISIS, SPRINT PLANNING & AUDIT CLASS P2
# =========================================================================
story.append(Paragraph("Tugas Praktikum OOP - Modul 3", title_style))
story.append(Paragraph("Encapsulation, Access Modifier, Getter–Setter, dan Validasi Data", subtitle_style))

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
story.append(Spacer(1, 4))

story.append(Paragraph("HALAMAN 1 — ANALISIS, SPRINT PLANNING & AUDIT P2", h1_style))
story.append(Paragraph("<b>1. Nama Project:</b> Sistem Kasir Sederhana", body_style))
story.append(Paragraph("<b>2. Sprint Goal:</b> Memperbaiki struktur class proyek dengan menerapkan encapsulation sehingga seluruh data object bersifat private dan hanya dapat diakses/diubah melalui mekanisme yang terkontrol (Getter, Setter, dan Validasi).", body_style))
story.append(Spacer(1, 2))

story.append(Paragraph("<b>3. Sprint Backlog (P3)</b>", h1_style))
sb_table_data = [
    [Paragraph("ID", cell_bold), Paragraph("Sprint Backlog", cell_bold), Paragraph("Status", cell_bold)],
    [Paragraph("SB-01", cell_style), Paragraph("Mengubah seluruh attribute menjadi private pada semua class", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-02", cell_style), Paragraph("Membuat method getter untuk pembacaan data secara aman", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-03", cell_style), Paragraph("Membuat method setter selektif hanya untuk data yang dapat diubah", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-04", cell_style), Paragraph("Menambahkan validasi data (harga > 0, stok >= 0, format no HP, tipe member, stok transaksi)", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-05", cell_style), Paragraph("Memperbaiki constructor agar mematuhi aturan validasi", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-06", cell_style), Paragraph("Melakukan pengujian komprehensif (Test Valid dan Test Invalid)", cell_style), Paragraph("DONE", cell_bold)]
]
t_sb = Table(sb_table_data, colWidths=[0.7 * inch, 5.3 * inch, 1.0 * inch])
t_sb.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#EAEAEA")),
    ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CCCCCC")),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 2),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
]))
story.append(t_sb)
story.append(Spacer(1, 4))

story.append(Paragraph("<b>4. Tabel Audit Class P2</b>", h1_style))
audit_data = [
    [Paragraph("Class", cell_bold), Paragraph("Attribute", cell_bold), Paragraph("Kondisi P2", cell_bold), Paragraph("Perbaikan P3", cell_bold)],
    [Paragraph("Produk", cell_style), Paragraph("kode", cell_style), Paragraph("default", cell_style), Paragraph("private + getter only", cell_style)],
    [Paragraph("Produk", cell_style), Paragraph("nama", cell_style), Paragraph("default", cell_style), Paragraph("private + getter + setter (non-empty)", cell_style)],
    [Paragraph("Produk", cell_style), Paragraph("harga", cell_style), Paragraph("default", cell_style), Paragraph("private + getter + setter (> 0)", cell_style)],
    [Paragraph("Produk", cell_style), Paragraph("stok", cell_style), Paragraph("default", cell_style), Paragraph("private + getter + setter (>= 0)", cell_style)],
    [Paragraph("Pelanggan", cell_style), Paragraph("idPelanggan", cell_style), Paragraph("default", cell_style), Paragraph("private + getter only", cell_style)],
    [Paragraph("Pelanggan", cell_style), Paragraph("nama", cell_style), Paragraph("default", cell_style), Paragraph("private + getter + setter (non-empty)", cell_style)],
    [Paragraph("Pelanggan", cell_style), Paragraph("nomorHP", cell_style), Paragraph("default", cell_style), Paragraph("private + getter + setter (regex 10-13 digit)", cell_style)],
    [Paragraph("Pelanggan", cell_style), Paragraph("tipeMember", cell_style), Paragraph("default", cell_style), Paragraph("private + getter + setter (VIP/GOLD/REG)", cell_style)],
    [Paragraph("Transaksi", cell_style), Paragraph("idTransaksi, tgl", cell_style), Paragraph("default", cell_style), Paragraph("private + getter only", cell_style)],
    [Paragraph("Transaksi", cell_style), Paragraph("jumlahBeli", cell_style), Paragraph("default", cell_style), Paragraph("private + getter + setter (>0 & <= stok)", cell_style)]
]
t_audit = Table(audit_data, colWidths=[1.1 * inch, 1.5 * inch, 1.5 * inch, 2.9 * inch])
t_audit.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#EAEAEA")),
    ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CCCCCC")),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 1.5),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 1.5),
]))
story.append(t_audit)

# =========================================================================
# HALAMAN 2: ENCAPSULATION SPECIFICATION & CLASS DIAGRAM
# =========================================================================
story.append(PageBreak())
story.append(Paragraph("HALAMAN 2 — ENCAPSULATION & CLASS DIAGRAM", h1_style))
story.append(Spacer(1, 2))

story.append(Paragraph("<b>5. Tabel Spesifikasi Encapsulation, Getter, Setter, dan Validasi</b>", h1_style))
spec_data = [
    [Paragraph("Class", cell_bold), Paragraph("Private Attribute", cell_bold), Paragraph("Getter", cell_bold), Paragraph("Setter", cell_bold), Paragraph("Validation", cell_bold)],
    [
        Paragraph("<b>Produk</b>", cell_style),
        Paragraph("• kode<br/>• nama<br/>• harga<br/>• stok", cell_style),
        Paragraph("getKode()<br/>getNama()<br/>getHarga()<br/>getStok()", cell_style),
        Paragraph("setNama()<br/>setHarga()<br/>setStok()", cell_style),
        Paragraph("• harga > 0<br/>• stok >= 0<br/>• nama != null & non-blank", cell_style)
    ],
    [
        Paragraph("<b>Pelanggan</b>", cell_style),
        Paragraph("• idPelanggan<br/>• nama<br/>• nomorHP<br/>• tipeMember", cell_style),
        Paragraph("getIdPelanggan()<br/>getNama()<br/>getNomorHP()<br/>getTipeMember()<br/>getDiskon()", cell_style),
        Paragraph("setNama()<br/>setNomorHP()<br/>setTipeMember()", cell_style),
        Paragraph("• nama != null & non-blank<br/>• noHP: regex 10-13 digit<br/>• tipe: VIP, GOLD, REGULER", cell_style)
    ],
    [
        Paragraph("<b>Transaksi</b>", cell_style),
        Paragraph("• idTransaksi<br/>• tanggal<br/>• pelanggan<br/>• produk<br/>• jumlahBeli", cell_style),
        Paragraph("getIdTransaksi()<br/>getTanggal()<br/>getPelanggan()<br/>getProduk()<br/>getJumlahBeli()", cell_style),
        Paragraph("setJumlahBeli()", cell_style),
        Paragraph("• jumlahBeli > 0<br/>• jumlahBeli <= produk.getStok()", cell_style)
    ]
]
t_spec = Table(spec_data, colWidths=[1.0 * inch, 1.2 * inch, 1.4 * inch, 1.2 * inch, 2.2 * inch])
t_spec.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#EAEAEA")),
    ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CCCCCC")),
    ('VALIGN', (0, 0), (-1, -1), 'TOP'),
    ('TOPPADDING', (0, 0), (-1, -1), 3),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
]))
story.append(t_spec)
story.append(Spacer(1, 6))

story.append(Paragraph("<b>6. Diagram Kelas Hasil Refactoring (UML Class Diagram)</b>", h1_style))
diagram_img_path = os.path.join(assets_dir, "class_diagram.png")
if os.path.exists(diagram_img_path):
    story.append(RLImage(diagram_img_path, width=6.8 * inch, height=3.8 * inch))
    story.append(Paragraph("Gambar 1. UML Class Diagram Terenkapsulasi (Notasi - untuk Private dan + untuk Public)", caption_style))

# =========================================================================
# HALAMAN 3: SCREENSHOT PROGRAM, TEST VALID & INVALID, SPRINT REVIEW & RETROSPECTIVE
# =========================================================================
story.append(PageBreak())
story.append(Paragraph("HALAMAN 3 — HASIL PENGUJIAN, SPRINT REVIEW & RETROSPECTIVE", h1_style))
story.append(Spacer(1, 2))

story.append(Paragraph("<b>7. Screenshot Hasil Running Program (Main.java)</b>", h1_style))
term_img_path = os.path.join(assets_dir, "hasil_running.png")
if os.path.exists(term_img_path):
    story.append(RLImage(term_img_path, width=6.5 * inch, height=4.1 * inch))
    story.append(Paragraph("Gambar 2. Bukti Running Program Main.java (Eksekusi Test Valid & Test Invalid)", caption_style))
    story.append(Spacer(1, 3))

story.append(Paragraph("<b>8. Rincian Pengujian Valid dan Invalid:</b>", h1_style))
story.append(Paragraph("• <b>Test Valid:</b> Instansiasi constructor valid, pembacaan via getter, pembaruan data setter (harga Rp17000, tipe member GOLD), serta checkout transaksi TRX-001 sukses mencetak struk dan memotong stok.<br/>• <b>Test Invalid:</b> Uji harga negatif (-10000), stok negatif (-15), nama kosong (\"\"), format no HP salah, tipe member salah, serta kuantitas melebihi stok berhasil ditolak dan data objek terlindungi utuh.", body_style))
story.append(Spacer(1, 2))

story.append(Paragraph("<b>9. Sprint Review</b>", h1_style))
rev_table_data = [
    [Paragraph("Item", cell_bold), Paragraph("Hasil", cell_bold)],
    [Paragraph("Attribute berhasil di-private", cell_style), Paragraph("Seluruh atribut class Produk, Pelanggan, dan Transaksi di-private.", cell_style)],
    [Paragraph("Getter berjalan", cell_style), Paragraph("Seluruh getter dapat membaca data tanpa membocorkan akses langsung.", cell_style)],
    [Paragraph("Setter berjalan", cell_style), Paragraph("Setter berjalan aman mengontrol nilai atribut yang diizinkan.", cell_style)],
    [Paragraph("Validasi berhasil", cell_style), Paragraph("Seluruh 6 aturan validasi berhasil menolak data yang tidak valid.", cell_style)],
    [Paragraph("Constructor diperbaiki", cell_style), Paragraph("Constructor memanggil setter sehingga validasi ditegakkan sejak inisialisasi.", cell_style)],
    [Paragraph("Test invalid berhasil", cell_style), Paragraph("Data tidak valid ditolak dan nilai atribut dalam objek tetap terjaga.", cell_style)],
    [Paragraph("Program dapat dijalankan", cell_style), Paragraph("Kompilasi (javac) dan eksekusi (java) berhasil 100% tanpa error.", cell_style)]
]
t_rev = Table(rev_table_data, colWidths=[2.2 * inch, 4.8 * inch])
t_rev.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#EAEAEA")),
    ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CCCCCC")),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 1.5),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 1.5),
]))
story.append(t_rev)
story.append(Spacer(1, 4))

story.append(Paragraph("<b>10. Sprint Retrospective</b>", h1_style))
retro_text = (
    "<b>What Went Well?</b> Seluruh class berhasil dienkapsulasi dengan sempurna, information hiding terlindungi, dan validasi data terbukti kokoh menangkal input ilegal.<br/>"
    "<b>What Went Wrong?</b> Penyesuaian method yang sebelumnya mengakses atribut langsung membutuhkan refactoring menyeluruh pada method pemanggil.<br/>"
    "<b>Improvement:</b> Pada sprint berikutnya (P4), fokus dialihkan pada pemodelan relasi antarobject (asosiasi, agregasi, komposisi) yang lebih kaya dan dinamis."
)
story.append(Paragraph(retro_text, body_style))

doc.build(story)
print(f"P3 PDF successfully generated: {pdf_path}")
