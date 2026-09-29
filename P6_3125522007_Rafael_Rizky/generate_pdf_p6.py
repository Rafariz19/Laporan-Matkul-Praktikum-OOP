import os
from reportlab.lib.pagesizes import A4
from reportlab.lib.units import inch
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image as RLImage, PageBreak
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

pdf_path = r"d:\KULIAH\Semester 3\OOP\Prakatikum\P6_3125522007_Rafael_Rizky\README.pdf"
assets_dir = r"d:\KULIAH\Semester 3\OOP\Prakatikum\P6_3125522007_Rafael_Rizky\assets"

doc = SimpleDocTemplate(
    pdf_path,
    pagesize=A4,
    leftMargin=0.6 * inch,
    rightMargin=0.6 * inch,
    topMargin=0.55 * inch,
    bottomMargin=0.55 * inch
)

styles = getSampleStyleSheet()

title_style = ParagraphStyle(
    'DocTitle',
    fontName='Times-Bold',
    fontSize=13,
    leading=15,
    alignment=1,
    textColor=colors.black,
    spaceAfter=2
)

subtitle_style = ParagraphStyle(
    'DocSubtitle',
    fontName='Times-Bold',
    fontSize=10,
    leading=12,
    alignment=1,
    textColor=colors.black,
    spaceAfter=6
)

h1_style = ParagraphStyle(
    'H1',
    fontName='Times-Bold',
    fontSize=9.5,
    leading=12,
    textColor=colors.black,
    spaceBefore=3,
    spaceAfter=2
)

body_style = ParagraphStyle(
    'Body',
    fontName='Times-Roman',
    fontSize=8.5,
    leading=11,
    textColor=colors.black,
    spaceAfter=2
)

caption_style = ParagraphStyle(
    'Caption',
    fontName='Times-Italic',
    fontSize=7.5,
    leading=9.5,
    alignment=1,
    textColor=colors.black,
    spaceAfter=3
)

cell_style = ParagraphStyle(
    'Cell',
    fontName='Times-Roman',
    fontSize=7.5,
    leading=9.5,
    textColor=colors.black
)

cell_bold = ParagraphStyle(
    'CellBold',
    fontName='Times-Bold',
    fontSize=7.5,
    leading=9.5,
    textColor=colors.black
)

story = []

# =========================================================================
# HALAMAN 1: IDENTITAS, SPRINT PLANNING, HIERARKI & BEHAVIOR POLYMORPHIC
# =========================================================================
story.append(Paragraph("Tugas Praktikum OOP - Modul 6", title_style))
story.append(Paragraph("Polymorphism, Method Overriding, Method Overloading, dan Dynamic Binding", subtitle_style))

id_table_data = [
    [Paragraph("<b>Nama</b> : Rafael Rizky", cell_style), Paragraph("<b>Dosen</b> : Nirwana Haidar Hari, S.Pd., M.Kom.", cell_style)],
    [Paragraph("<b>NRP</b>  : 3125522007", cell_style), Paragraph("<b>Kampus</b> : PENS PSDKU Sumenep (2026)", cell_style)]
]
t_id = Table(id_table_data, colWidths=[2.8 * inch, 4.3 * inch])
t_id.setStyle(TableStyle([
    ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
    ('BOTTOMPADDING', (0,0), (-1,-1), 1),
    ('TOPPADDING', (0,0), (-1,-1), 1),
    ('LEFTPADDING', (0,0), (-1,-1), 0),
    ('RIGHTPADDING', (0,0), (-1,-1), 0),
]))
story.append(t_id)
story.append(Spacer(1, 3))

story.append(Paragraph("HALAMAN 1 — ANALISIS, SPRINT PLANNING & AUDIT BEHAVIOR P6", h1_style))
story.append(Paragraph("<b>1. Nama Project:</b> Sistem Kasir Sederhana", body_style))
story.append(Paragraph("<b>2. Sprint Goal:</b> Mengembangkan behavior object pada proyek melalui method overriding (@Override) dan polymorphism sehingga subclass dapat merespons pemanggilan method yang sama dengan perilaku berbeda, serta menyediakan fleksibilitas input pembayaran melalui method overloading.", body_style))
story.append(Spacer(1, 2))

story.append(Paragraph("<b>3. Sprint Backlog (P6)</b>", h1_style))
sb_table_data = [
    [Paragraph("ID", cell_bold), Paragraph("Sprint Backlog", cell_bold), Paragraph("Status", cell_bold)],
    [Paragraph("SB-01", cell_style), Paragraph("Audit hierarki Superclass dan Subclass hasil P5", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-02", cell_style), Paragraph("Menentukan behavior method yang dapat dioverride pada hierarki Produk", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-03", cell_style), Paragraph("Implementasi method overriding (@Override) pada ProdukMakanan dan ProdukElektronik", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-04", cell_style), Paragraph("Implementasi method overloading pada Transaksi (tambahItem dan prosesTransaksi)", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-05", cell_style), Paragraph("Implementasi upcasting dan polymorphic reference", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-06", cell_style), Paragraph("Membuat dan mengiterasi polymorphic collection (array of Superclass Produk[])", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-07", cell_style), Paragraph("Melakukan pengujian pembuktian dynamic binding saat program dijalankan", cell_style), Paragraph("DONE", cell_bold)]
]
t_sb = Table(sb_table_data, colWidths=[0.7 * inch, 5.4 * inch, 1.0 * inch])
t_sb.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 1.5),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 1.5),
]))
story.append(t_sb)
story.append(Spacer(1, 3))

story.append(Paragraph("<b>4. Hierarchy Class P5 & Behavior yang Dipilih untuk Polymorphism:</b>", h1_style))
story.append(Paragraph("Hierarki: <b>Produk</b> (Superclass) ▲ ── <b>ProdukMakanan</b> & <b>ProdukElektronik</b> (Subclass).<br/>"
                       "Behavior yang dipilih untuk overriding adalah method <code>tampilkanData()</code> dan <code>getKategoriInfo()</code>.", body_style))

audit_data = [
    [Paragraph("Superclass", cell_bold), Paragraph("Subclass", cell_bold), Paragraph("Method Dioverride", cell_bold), Paragraph("Perilaku Spesifik pada Subclass", cell_bold)],
    [Paragraph("Produk", cell_style), Paragraph("ProdukMakanan", cell_style), Paragraph("tampilkanData(), getKategoriInfo()", cell_style), Paragraph("Kategori pangan & mencetak tanggal kadaluarsa.", cell_style)],
    [Paragraph("Produk", cell_style), Paragraph("ProdukElektronik", cell_style), Paragraph("tampilkanData(), getKategoriInfo()", cell_style), Paragraph("Kategori hardware & mencetak masa garansi unit.", cell_style)]
]
t_audit = Table(audit_data, colWidths=[1.1 * inch, 1.3 * inch, 2.0 * inch, 2.7 * inch])
t_audit.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 2),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
]))
story.append(t_audit)

# =========================================================================
# HALAMAN 2: CLASS DIAGRAM P6, CONTOH OVERRIDING, OVERLOADING & UPCASTING
# =========================================================================
story.append(PageBreak())
story.append(Paragraph("HALAMAN 2 — CLASS DIAGRAM, OVERRIDING, OVERLOADING & UPCASTING", h1_style))
story.append(Spacer(1, 1))

story.append(Paragraph("<b>5. Class Diagram Terbaru (P6)</b>", h1_style))
diagram_img_path = os.path.join(assets_dir, "class_diagram_p6.png")
if os.path.exists(diagram_img_path):
    story.append(RLImage(diagram_img_path, width=6.9 * inch, height=3.8 * inch))
    story.append(Paragraph("Gambar 1. UML Class Diagram Polymorphism, Overriding, dan Overloading (P6)", caption_style))

story.append(Spacer(1, 1))
story.append(Paragraph("<b>6. Contoh Overriding, Overloading, dan Upcasting:</b>", h1_style))
story.append(Paragraph("• <b>Contoh Overriding:</b> Subclass mendefinisikan ulang method superclass menggunakan <code>@Override</code>:<br/>"
                       "  - <code>ProdukMakanan.tampilkanData()</code> mencetak tanggal kadaluarsa.<br/>"
                       "  - <code>ProdukElektronik.tampilkanData()</code> mencetak masa garansi bulan.<br/>"
                       "• <b>Contoh Overloading:</b> Pada class <code>Transaksi</code>:<br/>"
                       "  - <code>tambahItem(Produk, int)</code> (reguler) vs <code>tambahItem(Produk, int, double disc)</code> (promo item).<br/>"
                       "  - <code>prosesTransaksi()</code> (standar) vs <code>prosesTransaksi(double uang)</code> (tunai & hitung kembalian).<br/>"
                       "• <b>Penjelasan Upcasting:</b> Objek subclass dirujuk menggunakan tipe referensi superclass (<code>Produk p = new ProdukMakanan(...)</code>). Memungkinkan polymorphic collection dan runtime dispatch.", body_style))

# =========================================================================
# HALAMAN 3: SCREENSHOT RUNNING, TABEL DYNAMIC BINDING, SPRINT REVIEW
# =========================================================================
story.append(PageBreak())
story.append(Paragraph("HALAMAN 3 — HASIL RUNNING, TABEL DYNAMIC BINDING & SPRINT REVIEW", h1_style))
story.append(Spacer(1, 1))

story.append(Paragraph("<b>7. Screenshot Hasil Running Program (Main.java)</b>", h1_style))
path_term = os.path.join(assets_dir, "hasil_running.png")
if os.path.exists(path_term):
    story.append(RLImage(path_term, width=6.6 * inch, height=3.1 * inch))
    story.append(Paragraph("Gambar 2. Bukti Running Program Main.java pada Terminal (Eksekusi Pengujian P6)", caption_style))
    story.append(Spacer(1, 1))

story.append(Paragraph("<b>8. Tabel Pengujian Dynamic Binding:</b>", h1_style))
db_data = [
    [Paragraph("Reference Type", cell_bold), Paragraph("Actual Object", cell_bold), Paragraph("Method Dipanggil", cell_bold), Paragraph("Perilaku / Output yang Dihasilkan", cell_bold)],
    [Paragraph("Produk (Superclass)", cell_style), Paragraph("ProdukMakanan", cell_style), Paragraph("tampilkanData()", cell_style), Paragraph("Mencetak format makanan & Kadaluarsa: 25-10-2026", cell_style)],
    [Paragraph("Produk (Superclass)", cell_style), Paragraph("ProdukElektronik", cell_style), Paragraph("tampilkanData()", cell_style), Paragraph("Mencetak format elektronik & Garansi: 6 Bulan", cell_style)],
    [Paragraph("Produk (Superclass)", cell_style), Paragraph("ProdukMakanan", cell_style), Paragraph("getKategoriInfo()", cell_style), Paragraph("Output: 'Makanan & Minuman Segar (Konsumsi)'", cell_style)],
    [Paragraph("Produk (Superclass)", cell_style), Paragraph("ProdukElektronik", cell_style), Paragraph("getKategoriInfo()", cell_style), Paragraph("Output: 'Elektronik & Aksesoris Gadget (Hardware)'", cell_style)]
]
t_db = Table(db_data, colWidths=[1.5 * inch, 1.3 * inch, 1.4 * inch, 2.9 * inch])
t_db.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 1),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 1),
]))
story.append(t_db)
story.append(Spacer(1, 2))

story.append(Paragraph("<b>9. Sprint Review & Retrospective:</b>", h1_style))
rev_table_data = [
    [Paragraph("Item", cell_bold), Paragraph("Hasil", cell_bold)],
    [Paragraph("Superclass & subclass tersedia", cell_style), Paragraph("Superclass Produk serta Subclass ProdukMakanan dan ProdukElektronik tersedia.", cell_style)],
    [Paragraph("Method overriding berhasil", cell_style), Paragraph("Method tampilkanData() dan getKategoriInfo() berhasil dioverride dengan @Override.", cell_style)],
    [Paragraph("Behavior berbeda terbukti", cell_style), Paragraph("ProdukMakanan dan ProdukElektronik memiliki output spesifik sesuai domainnya.", cell_style)],
    [Paragraph("Method overloading tersedia", cell_style), Paragraph("tambahItem dan prosesTransaksi berhasil dioverload dengan parameter berbeda.", cell_style)],
    [Paragraph("Upcasting & Dynamic binding", cell_style), Paragraph("Terbukti pemanggilan method via referensi Produk mengeksekusi objek aktual di runtime.", cell_style)],
    [Paragraph("Program berjalan", cell_style), Paragraph("Kompilasi (javac) dan eksekusi (java) berhasil 100% bebas error.", cell_style)]
]
t_rev = Table(rev_table_data, colWidths=[2.2 * inch, 4.9 * inch])
t_rev.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 1),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 1),
]))
story.append(t_rev)
story.append(Spacer(1, 2))

retro_text = (
    "<b>What Went Well?</b> Polymorphism dan dynamic binding berjalan sempurna; penambahan overloading pembayaran tunai dan diskon item memberi nilai guna nyata.<br/>"
    "<b>What Went Wrong?</b> Perlu ketelitian dalam membedakan overloading (compile-time) dan overriding (runtime).<br/>"
    "<b>Improvement:</b> Pada sprint berikutnya (P7: Abstraction & Interface), Superclass Produk dapat ditransformasikan menjadi Abstract Class atau mengimplementasikan Interface kontrak bisnis."
)
story.append(Paragraph(retro_text, body_style))

doc.build(story)
print(f"P6 PDF successfully generated: {pdf_path}")
