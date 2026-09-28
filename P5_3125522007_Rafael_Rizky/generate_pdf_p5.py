import os
from reportlab.lib.pagesizes import A4
from reportlab.lib.units import inch
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image as RLImage, PageBreak
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

pdf_path = r"d:\KULIAH\Semester 3\OOP\Prakatikum\P5_3125522007_Rafael_Rizky\README.pdf"
assets_dir = r"d:\KULIAH\Semester 3\OOP\Prakatikum\P5_3125522007_Rafael_Rizky\assets"

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
    fontSize=10,
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
# HALAMAN 1: IDENTITAS, SPRINT PLANNING & AUDIT CLASS GENERALIZATION
# =========================================================================
story.append(Paragraph("Tugas Praktikum OOP - Modul 5", title_style))
story.append(Paragraph("Inheritance, Generalization, Superclass, dan Subclass", subtitle_style))

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

story.append(Paragraph("HALAMAN 1 — ANALISIS, SPRINT PLANNING & AUDIT GENERALIZATION", h1_style))
story.append(Paragraph("<b>1. Nama Project:</b> Sistem Kasir Sederhana", body_style))
story.append(Paragraph("<b>2. Sprint Goal:</b> Memperbaiki desain proyek dengan mengidentifikasi class yang memiliki karakteristik serupa, melakukan generalization untuk membentuk Superclass Produk, serta mengimplementasikan Subclass ProdukMakanan dan ProdukElektronik menggunakan klausa extends dan super, dengan tetap mempertahankan seluruh relasi P4.", body_style))
story.append(Spacer(1, 2))

story.append(Paragraph("<b>3. Sprint Backlog (P5)</b>", h1_style))
sb_table_data = [
    [Paragraph("ID", cell_bold), Paragraph("Sprint Backlog", cell_bold), Paragraph("Status", cell_bold)],
    [Paragraph("SB-01", cell_style), Paragraph("Mencari dan mengaudit class yang memiliki duplikasi atribut/behavior", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-02", cell_style), Paragraph("Menentukan dan mendefinisikan Superclass (Produk)", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-03", cell_style), Paragraph("Menentukan dan mendefinisikan Subclass (ProdukMakanan dan ProdukElektronik)", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-04", cell_style), Paragraph("Memperbarui UML Class Diagram proyek sebelum dan sesudah refactoring", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-05", cell_style), Paragraph("Implementasi pewarisan menggunakan kata kunci extends pada subclass", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-06", cell_style), Paragraph("Implementasi pemanggilan constructor superclass menggunakan super(...)", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-07", cell_style), Paragraph("Melakukan pengujian inheritance pada Main.java (transaksi multi-kategori)", cell_style), Paragraph("DONE", cell_bold)]
]
t_sb = Table(sb_table_data, colWidths=[0.7 * inch, 5.4 * inch, 1.0 * inch])
t_sb.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 1.5),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 1.5),
]))
story.append(t_sb)
story.append(Spacer(1, 4))

story.append(Paragraph("<b>4. Audit Class P4 & Kandidat Generalization (Bagian A)</b>", h1_style))
audit_data = [
    [Paragraph("Class", cell_bold), Paragraph("Attribute Umum (Kandidat Superclass)", cell_bold), Paragraph("Attribute Khusus (Subclass)", cell_bold)],
    [Paragraph("ProdukMakanan", cell_style), Paragraph("kode, nama, harga, stok", cell_style), Paragraph("tanggalKadaluarsa (String)", cell_style)],
    [Paragraph("ProdukElektronik", cell_style), Paragraph("kode, nama, harga, stok", cell_style), Paragraph("garansiBulan (int)", cell_style)]
]
t_audit = Table(audit_data, colWidths=[1.8 * inch, 3.2 * inch, 2.1 * inch])
t_audit.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 2),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
]))
story.append(t_audit)
story.append(Spacer(1, 3))

story.append(Paragraph("<b>5. Kandidat Superclass dan Subclass:</b>", h1_style))
story.append(Paragraph("• <b>Superclass:</b> <b>Produk</b> (menampung atribut bersama: kode, nama, harga, stok, serta method kurangiStok, tambahStok, hitungNilaiInventaris).<br/>"
                       "• <b>Subclass 1:</b> <b>ProdukMakanan</b> (mewarisi Produk, menambahkan atribut tanggalKadaluarsa).<br/>"
                       "• <b>Subclass 2:</b> <b>ProdukElektronik</b> (mewarisi Produk, menambahkan atribut garansiBulan).", body_style))

# =========================================================================
# HALAMAN 2: CLASS DIAGRAM SEBELUM/SESUDAH & HUBUNGAN IS-A
# =========================================================================
story.append(PageBreak())
story.append(Paragraph("HALAMAN 2 — CLASS DIAGRAM & HUBUNGAN IS-A", h1_style))
story.append(Spacer(1, 1))

story.append(Paragraph("<b>6. Class Diagram Sebelum Refactoring (P4)</b>", h1_style))
path_sebelum = os.path.join(assets_dir, "class_diagram_sebelum.png")
if os.path.exists(path_sebelum):
    story.append(RLImage(path_sebelum, width=6.9 * inch, height=2.2 * inch))
    story.append(Paragraph("Gambar 1. UML Class Diagram Sebelum Refactoring (Terjadi Duplikasi Atribut)", caption_style))

story.append(Spacer(1, 1))
story.append(Paragraph("<b>7. Class Diagram Setelah Refactoring (P5: Dengan Inheritance)</b>", h1_style))
path_sesudah = os.path.join(assets_dir, "class_diagram_sesudah.png")
if os.path.exists(path_sesudah):
    story.append(RLImage(path_sesudah, width=6.9 * inch, height=3.3 * inch))
    story.append(Paragraph("Gambar 2. UML Class Diagram Setelah Refactoring (Penerapan Inheritance & Generalization P5)", caption_style))

story.append(Spacer(1, 1))
story.append(Paragraph("<b>8. Penjelasan Hubungan is-a dan Manfaat Refactoring:</b>", h1_style))
story.append(Paragraph("• <b>Hubungan is-a:</b> <i>ProdukMakanan is-a Produk</i> dan <i>ProdukElektronik is-a Produk</i>. Keduanya merupakan jenis produk yang valid dengan atribut kode, nama, harga, dan stok yang sama.<br/>"
                       "• <b>Manfaat Refactoring:</b> (1) Menghilangkan duplikasi kode, (2) Mempermudah perawatan sistem secara modular, (3) Memungkinkan ItemTransaksi dan Transaksi dari P4 menampung seluruh turunan Produk secara polimorfik tanpa perlu perombakan kode.", body_style))

# =========================================================================
# HALAMAN 3: SCREENSHOT RUNNING, ANALISIS EXTENDS/SUPER, SPRINT REVIEW
# =========================================================================
story.append(PageBreak())
story.append(Paragraph("HALAMAN 3 — HASIL RUNNING, BUKTI EXTENDS/SUPER & SPRINT REVIEW", h1_style))
story.append(Spacer(1, 1))

story.append(Paragraph("<b>9. Screenshot Hasil Running Program (Main.java)</b>", h1_style))
path_term = os.path.join(assets_dir, "hasil_running.png")
if os.path.exists(path_term):
    story.append(RLImage(path_term, width=6.6 * inch, height=3.3 * inch))
    story.append(Paragraph("Gambar 3. Bukti Running Program Main.java pada Terminal (Eksekusi Pengujian P5)", caption_style))
    story.append(Spacer(1, 1))

story.append(Paragraph("<b>10. Bukti Penggunaan extends dan super:</b>", h1_style))
story.append(Paragraph("• <b>extends:</b> Dideklarasikan pada subclass: <code>public class ProdukMakanan extends Produk</code> dan <code>public class ProdukElektronik extends Produk</code>.<br/>"
                       "• <b>super(...):</b> Digunakan pada constructor subclass untuk meneruskan argumen dasar ke Superclass: <code>super(kode, nama, harga, stok);</code>. Urutan eksekusi: Constructor Produk dieksekusi terlebih dahulu untuk validasi awal, kemudian constructor subclass dieksekusi untuk inisialisasi atribut khusus.", body_style))
story.append(Spacer(1, 1))

story.append(Paragraph("<b>11. Sprint Review</b>", h1_style))
rev_table_data = [
    [Paragraph("Item", cell_bold), Paragraph("Hasil", cell_bold)],
    [Paragraph("Kandidat inheritance ditemukan", cell_style), Paragraph("Ditemukan entitas ProdukMakanan dan ProdukElektronik dengan atribut bersama.", cell_style)],
    [Paragraph("Superclass berhasil dibuat", cell_style), Paragraph("Superclass Produk berhasil didefinisikan dengan atribut umum dan method mutator.", cell_style)],
    [Paragraph("Minimal 2 subclass dibuat", cell_style), Paragraph("Dibuat 2 subclass: ProdukMakanan dan ProdukElektronik.", cell_style)],
    [Paragraph("extends diterapkan", cell_style), Paragraph("Klausa extends diterapkan pada kedua subclass untuk mewarisi class Produk.", cell_style)],
    [Paragraph("super diterapkan", cell_style), Paragraph("super(...) diterapkan pada constructor subclass untuk inisialisasi superclass.", cell_style)],
    [Paragraph("Class diagram diperbarui", cell_style), Paragraph("Class diagram diperbarui dengan simbol generalization dan perbandingan sebelum/sesudah.", cell_style)],
    [Paragraph("Program berhasil dijalankan", cell_style), Paragraph("Kompilasi (javac) dan eksekusi (java) berhasil 100% tanpa error.", cell_style)],
    [Paragraph("Kendala", cell_style), Paragraph("Tidak ada kendala fatal. Seluruh relasi P4 terintegrasi mulus dengan inheritance P5.", cell_style)]
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

story.append(Paragraph("<b>12. Sprint Retrospective</b>", h1_style))
retro_text = (
    "<b>What Went Well?</b> Seluruh konsep generalization dan inheritance berhasil diimplementasikan secara elegan, mengeliminasi duplikasi kode dan memperkuat modularitas kasir.<br/>"
    "<b>What Went Wrong?</b> Diperlukan kehati-hatian dalam sinkronisasi constructor agar urutan pemanggilan super(...) memenuhi kaidah Java.<br/>"
    "<b>Improvement:</b> Pada sprint berikutnya (P6: Polymorphism), akan dikembangkan dynamic method overriding untuk perhitungan diskon dan tampilan struk yang lebih variatif."
)
story.append(Paragraph(retro_text, body_style))

doc.build(story)
print(f"P5 PDF successfully generated: {pdf_path}")
