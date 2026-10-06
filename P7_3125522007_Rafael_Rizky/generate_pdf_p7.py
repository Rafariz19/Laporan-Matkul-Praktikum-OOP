import os
from reportlab.lib.pagesizes import A4
from reportlab.lib.units import inch
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image as RLImage, PageBreak
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

pdf_path = r"d:\KULIAH\Semester 3\OOP\Prakatikum\P7_3125522007_Rafael_Rizky\README.pdf"
assets_dir = r"d:\KULIAH\Semester 3\OOP\Prakatikum\P7_3125522007_Rafael_Rizky\assets"

doc = SimpleDocTemplate(
    pdf_path,
    pagesize=A4,
    leftMargin=0.55 * inch,
    rightMargin=0.55 * inch,
    topMargin=0.5 * inch,
    bottomMargin=0.5 * inch
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
    spaceAfter=5
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
# HALAMAN 1: IDENTITAS, SPRINT PLANNING & AUDIT DESAIN P6 -> P7
# =========================================================================
story.append(Paragraph("Tugas Praktikum OOP - Modul 7", title_style))
story.append(Paragraph("Abstract Class, Abstract Method, dan Interface", subtitle_style))

id_table_data = [
    [Paragraph("<b>Nama</b> : Rafael Rizky", cell_style), Paragraph("<b>Dosen</b> : Nirwana Haidar Hari, S.Pd., M.Kom.", cell_style)],
    [Paragraph("<b>NRP</b>  : 3125522007", cell_style), Paragraph("<b>Kampus</b> : PENS PSDKU Sumenep (2026)", cell_style)]
]
t_id = Table(id_table_data, colWidths=[2.8 * inch, 4.4 * inch])
t_id.setStyle(TableStyle([
    ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
    ('BOTTOMPADDING', (0,0), (-1,-1), 1),
    ('TOPPADDING', (0,0), (-1,-1), 1),
    ('LEFTPADDING', (0,0), (-1,-1), 0),
    ('RIGHTPADDING', (0,0), (-1,-1), 0),
]))
story.append(t_id)
story.append(Spacer(1, 2))

story.append(Paragraph("HALAMAN 1 — ANALISIS, SPRINT PLANNING & AUDIT DESAIN P6", h1_style))
story.append(Paragraph("<b>1. Nama Project:</b> Sistem Kasir Sederhana", body_style))
story.append(Paragraph("<b>2. Sprint Goal:</b> Mengembangkan desain proyek P6 dengan menerapkan abstract class dan interface yang sesuai sehingga struktur class lebih jelas, perilaku object memiliki kontrak yang konsisten, dan program tetap dapat dijalankan.", body_style))
story.append(Spacer(1, 2))

story.append(Paragraph("<b>3. Sprint Backlog (P7)</b>", h1_style))
sb_table_data = [
    [Paragraph("ID", cell_bold), Paragraph("Sprint Backlog P7", cell_bold), Paragraph("Status", cell_bold)],
    [Paragraph("SB-01", cell_style), Paragraph("Audit hierarchy dan behavior proyek P6 (evaluasi instansiasi Superclass Produk)", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-02", cell_style), Paragraph("Menentukan kandidat abstract class (transformasi class Produk menjadi abstract)", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-03", cell_style), Paragraph("Menentukan abstract method (getKategoriInfo() dan tampilkanDetailKhusus())", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-04", cell_style), Paragraph("Menentukan kandidat interface (pembuatan kontrak interface DapatDidiskon)", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-05", cell_style), Paragraph("Memperbarui class diagram dengan notasi generalization dan realization", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-06", cell_style), Paragraph("Mengimplementasikan abstract class dan interface pada ProdukMakanan & ProdukElektronik", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-07", cell_style), Paragraph("Menguji polymorphism melalui 5 skenario komprehensif dan verifikasi program kasir", cell_style), Paragraph("DONE", cell_bold)]
]
t_sb = Table(sb_table_data, colWidths=[0.7 * inch, 5.5 * inch, 1.0 * inch])
t_sb.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 1.2),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 1.2),
]))
story.append(t_sb)
story.append(Spacer(1, 3))

story.append(Paragraph("<b>4. Tabel Audit Desain P6 ke P7:</b>", h1_style))
audit_data = [
    [Paragraph("Class / Behavior", cell_bold), Paragraph("Kondisi P6", cell_bold), Paragraph("Rencana P7", cell_bold), Paragraph("Alasan Perubahan Desain", cell_bold)],
    [Paragraph("Class Produk", cell_style), Paragraph("Class biasa", cell_style), Paragraph("Abstract class", cell_style), Paragraph("Konsep umum komoditas toko; instansiasi langsung new Produk(...) dilarang.", cell_style)],
    [Paragraph("getKategoriInfo()", cell_style), Paragraph("Method biasa", cell_style), Paragraph("Abstract method", cell_style), Paragraph("Setiap jenis barang wajib mengklasifikasikan kategorinya secara mandiri.", cell_style)],
    [Paragraph("tampilkanDetailKhusus()", cell_style), Paragraph("Belum ada", cell_style), Paragraph("Abstract method", cell_style), Paragraph("Memaksa subclass menampilkan atribut spesifiknya (kadaluarsa/garansi).", cell_style)],
    [Paragraph("DapatDidiskon", cell_style), Paragraph("Belum ada", cell_style), Paragraph("Interface", cell_style), Paragraph("Kontrak promosi diskon barang (hitungDiskon & getHargaSetelahDiskon).", cell_style)],
    [Paragraph("ProdukMakanan &\nProdukElektronik", cell_style), Paragraph("Subclass biasa", cell_style), Paragraph("Subclass konkret\n+ Implements", cell_style), Paragraph("Memenuhi kewajiban abstract method superclass sekaligus kontrak interface.", cell_style)]
]
t_audit = Table(audit_data, colWidths=[1.3 * inch, 1.1 * inch, 1.3 * inch, 3.5 * inch])
t_audit.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 1.5),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 1.5),
]))
story.append(t_audit)

# =========================================================================
# HALAMAN 2: CLASS DIAGRAM P7, ABSTRACTION, INTERFACE & ALASAN DESAIN
# =========================================================================
story.append(PageBreak())
story.append(Paragraph("HALAMAN 2 — CLASS DIAGRAM, ABSTRACTION & REALIZATION", h1_style))
story.append(Spacer(1, 1))

story.append(Paragraph("<b>5. Class Diagram Terbaru (P7)</b>", h1_style))
diagram_img_path = os.path.join(assets_dir, "class_diagram_p7.png")
if os.path.exists(diagram_img_path):
    story.append(RLImage(diagram_img_path, width=7.0 * inch, height=3.8 * inch))
    story.append(Paragraph("Gambar 1. UML Class Diagram Refactoring Abstraction dan Realization (P7)", caption_style))

story.append(Spacer(1, 1))
story.append(Paragraph("<b>6. Penjelasan Abstract Class, Abstract Method, Interface & Alasan Desain:</b>", h1_style))
story.append(Paragraph("• <b>Abstract Class (<code>Produk</code>):</b> Berperan sebagai cetak biru umum seluruh komoditas barang kasir. Memuat attribute bersama (<code>kode</code>, <code>nama</code>, <code>harga</code>, <code>stok</code>), constructor berenkapsulasi, operasi inventaris, dan template method <code>tampilkanData()</code>.<br/>"
                       "• <b>Abstract Method:</b> <code>getKategoriInfo()</code> dan <code>tampilkanDetailKhusus()</code> dideklarasikan tanpa body pada superclass untuk mewajibkan setiap subclass mendefinisikan perilakunya sendiri secara polimorfik.<br/>"
                       "• <b>Interface (<code>DapatDidiskon</code>):</b> Mendefinisikan kontrak perilaku murni (<code>hitungDiskon(double)</code> dan <code>getHargaSetelahDiskon(double)</code>) yang direalisasikan secara independen oleh subclass <code>ProdukMakanan</code> dan <code>ProdukElektronik</code>.<br/>"
                       "• <b>Alasan Perubahan Desain:</b> Mencegah instansiasi produk generik tanpa kategori nyata, memperjelas batas konsep abstrak dan turunan konkret, serta menjamin kontrak perhitungan diskon promosi yang terstandarisasi.", body_style))

# =========================================================================
# HALAMAN 3: SCREENSHOT RUNNING, TABEL PENGUJIAN SKENARIO & SPRINT REVIEW
# =========================================================================
story.append(PageBreak())
story.append(Paragraph("HALAMAN 3 — HASIL RUNNING, TABEL PENGUJIAN & SPRINT REVIEW", h1_style))
story.append(Spacer(1, 1))

story.append(Paragraph("<b>7. Screenshot Hasil Running Program (Main.java)</b>", h1_style))
path_term = os.path.join(assets_dir, "hasil_running.png")
if os.path.exists(path_term):
    story.append(RLImage(path_term, width=6.7 * inch, height=3.1 * inch))
    story.append(Paragraph("Gambar 2. Bukti Running Program Main.java pada Terminal (Eksekusi 5 Skenario P7)", caption_style))
    story.append(Spacer(1, 1))

story.append(Paragraph("<b>8. Tabel Pengujian Skenario Polymorphism (Bagian F):</b>", h1_style))
scen_data = [
    [Paragraph("No", cell_bold), Paragraph("Skenario Pengujian", cell_bold), Paragraph("Hasil yang Diharapkan", cell_bold), Paragraph("Status", cell_bold)],
    [Paragraph("1", cell_style), Paragraph("Instansiasi objek subclass konkret", cell_style), Paragraph("Objek berhasil dibuat; new Produk(...) ditolak oleh compiler Java", cell_style), Paragraph("SUKSES", cell_bold)],
    [Paragraph("2", cell_style), Paragraph("Memanggil abstract method via reference superclass", cell_style), Paragraph("Method subclass konkret yang sesuai dijalankan saat runtime", cell_style), Paragraph("SUKSES", cell_bold)],
    [Paragraph("3", cell_style), Paragraph("Memanggil method via reference interface", cell_style), Paragraph("Realisasi kontrak DapatDidiskon dieksekusi dengan kalkulasi valid", cell_style), Paragraph("SUKSES", cell_bold)],
    [Paragraph("4", cell_style), Paragraph("Iterasi array polymorphic Produk[] & DapatDidiskon[]", cell_style), Paragraph("Setiap objek menjalankan perilakunya masing-masing dalam loop", cell_style), Paragraph("SUKSES", cell_bold)],
    [Paragraph("5", cell_style), Paragraph("Integrasi transaksi kasir & pembayaran tunai", cell_style), Paragraph("Struk tercetak rapi, kembalian tunai akurat, fitur P1-P6 utuh", cell_style), Paragraph("SUKSES", cell_bold)]
]
t_scen = Table(scen_data, colWidths=[0.4 * inch, 2.3 * inch, 3.6 * inch, 0.9 * inch])
t_scen.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 1),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 1),
]))
story.append(t_scen)
story.append(Spacer(1, 2))

story.append(Paragraph("<b>9. Sprint Review & Retrospective:</b>", h1_style))
rev_table_data = [
    [Paragraph("Item Evaluasi Sprint Review", cell_bold), Paragraph("Hasil", cell_bold)],
    [Paragraph("Abstract class & abstract method tersedia", cell_style), Paragraph("Class Produk (abstract) serta method getKategoriInfo() & tampilkanDetailKhusus() siap.", cell_style)],
    [Paragraph("Subclass konkret & Interface diimplementasikan", cell_style), Paragraph("ProdukMakanan & ProdukElektronik berhasil merealisasikan superclass dan interface.", cell_style)],
    [Paragraph("Pengujian polymorphism via superclass & interface", cell_style), Paragraph("Terbukti melalui upcasting, dynamic binding, dan polymorphic array iteration.", cell_style)],
    [Paragraph("Fitur proyek sebelumnya tetap berjalan", cell_style), Paragraph("Relasi Association, Aggregation, Composition, dan Overloading berjalan bebas error.", cell_style)]
]
t_rev = Table(rev_table_data, colWidths=[2.6 * inch, 4.6 * inch])
t_rev.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 1),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 1),
]))
story.append(t_rev)
story.append(Spacer(1, 1.5))

retro_text = (
    "<b>What Went Well?</b> Transformasi superclass Produk menjadi abstract class dan implementasi interface DapatDidiskon berjalan 100% harmonis tanpa merusak relasi P4-P6.<br/>"
    "<b>What Went Wrong?</b> Perlu kehati-hatian dalam menyusun template method tampilkanData() agar tidak menduplikasi panggilan data khusus subclass.<br/>"
    "<b>Improvement:</b> Proyek P7 telah siap menjadi baseline untuk evaluasi P8 (UTS Praktikum) dan penyempurnaan implementasi UML pada P9."
)
story.append(Paragraph(retro_text, body_style))

doc.build(story)
print(f"P7 PDF successfully generated: {pdf_path}")
