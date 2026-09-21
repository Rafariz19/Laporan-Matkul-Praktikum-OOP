import os
from reportlab.lib.pagesizes import A4
from reportlab.lib.units import inch
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image as RLImage, PageBreak
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

pdf_path = r"d:\KULIAH\Semester 3\OOP\Prakatikum\P4_3125522007_Rafael_Rizky\README.pdf"
assets_dir = r"d:\KULIAH\Semester 3\OOP\Prakatikum\P4_3125522007_Rafael_Rizky\assets"

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
    textColor=colors.black,
    spaceAfter=2
)

subtitle_style = ParagraphStyle(
    'DocSubtitle',
    fontName='Times-Bold',
    fontSize=10.5,
    leading=13,
    alignment=1,
    textColor=colors.black,
    spaceAfter=8
)

h1_style = ParagraphStyle(
    'H1',
    fontName='Times-Bold',
    fontSize=10.5,
    leading=13,
    textColor=colors.black,
    spaceBefore=3,
    spaceAfter=2
)

body_style = ParagraphStyle(
    'Body',
    fontName='Times-Roman',
    fontSize=9,
    leading=11.5,
    textColor=colors.black,
    spaceAfter=2
)

caption_style = ParagraphStyle(
    'Caption',
    fontName='Times-Italic',
    fontSize=8,
    leading=10,
    alignment=1,
    textColor=colors.black,
    spaceAfter=3
)

cell_style = ParagraphStyle(
    'Cell',
    fontName='Times-Roman',
    fontSize=8,
    leading=10,
    textColor=colors.black
)

cell_bold = ParagraphStyle(
    'CellBold',
    fontName='Times-Bold',
    fontSize=8,
    leading=10,
    textColor=colors.black
)

story = []

# =========================================================================
# HALAMAN 1: IDENTITAS, SPRINT PLANNING & IDENTIFIKASI RELASI
# =========================================================================
story.append(Paragraph("Tugas Praktikum OOP - Modul 4", title_style))
story.append(Paragraph("Relasi Antarobject: Association, Aggregation, dan Composition", subtitle_style))

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

story.append(Paragraph("HALAMAN 1 — ANALISIS, SPRINT PLANNING & IDENTIFIKASI RELASI", h1_style))
story.append(Paragraph("<b>1. Nama Project:</b> Sistem Kasir Sederhana", body_style))
story.append(Paragraph("<b>2. Sprint Goal:</b> Menghubungkan class utama proyek (Produk, Pelanggan, ItemTransaksi, Transaksi) sehingga seluruh object dapat berinteraksi secara harmonis sebagai satu sistem kasir yang utuh melalui Association, Aggregation, dan Composition.", body_style))
story.append(Spacer(1, 2))

story.append(Paragraph("<b>3. Sprint Backlog (P4)</b>", h1_style))
sb_table_data = [
    [Paragraph("ID", cell_bold), Paragraph("Sprint Backlog", cell_bold), Paragraph("Status", cell_bold)],
    [Paragraph("SB-01", cell_style), Paragraph("Mengidentifikasi relasi antarclass (Association, Aggregation, Composition)", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-02", cell_style), Paragraph("Memperbarui UML Class Diagram proyek dengan notasi standar", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-03", cell_style), Paragraph("Mengimplementasikan relasi Association antara Pelanggan dan Transaksi", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-04", cell_style), Paragraph("Mengimplementasikan relasi Aggregation antara ItemTransaksi dan Produk", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-05", cell_style), Paragraph("Mengimplementasikan relasi Composition antara Transaksi dan ItemTransaksi", cell_style), Paragraph("DONE", cell_bold)],
    [Paragraph("SB-06", cell_style), Paragraph("Membuat pengujian interaksi multi-objek pada Main.java", cell_style), Paragraph("DONE", cell_bold)]
]
t_sb = Table(sb_table_data, colWidths=[0.7 * inch, 5.3 * inch, 1.0 * inch])
t_sb.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 2),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
]))
story.append(t_sb)
story.append(Spacer(1, 4))

story.append(Paragraph("<b>4. Tabel Identifikasi Relasi Antarclass (Bagian A)</b>", h1_style))
rel_data = [
    [Paragraph("Class A", cell_bold), Paragraph("Class B", cell_bold), Paragraph("Relasi", cell_bold), Paragraph("Alasan", cell_bold)],
    [Paragraph("Pelanggan", cell_style), Paragraph("Transaksi", cell_style), Paragraph("Association", cell_bold), Paragraph("Pelanggan melakukan transaksi (uses-a, ketergantungan lemah).", cell_style)],
    [Paragraph("ItemTransaksi", cell_style), Paragraph("Produk", cell_style), Paragraph("Aggregation", cell_bold), Paragraph("Produk tetap ada secara mandiri di luar transaksi (has-a, sedang).", cell_style)],
    [Paragraph("Transaksi", cell_style), Paragraph("ItemTransaksi", cell_style), Paragraph("Composition", cell_bold), Paragraph("Item dibuat di dalam Transaksi & bagian tak terpisahkan (part-of, kuat).", cell_style)]
]
t_rel = Table(rel_data, colWidths=[1.1 * inch, 1.2 * inch, 1.2 * inch, 3.5 * inch])
t_rel.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 2.5),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 2.5),
]))
story.append(t_rel)

# =========================================================================
# HALAMAN 2: CLASS DIAGRAM & PENJELASAN RELASI
# =========================================================================
story.append(PageBreak())
story.append(Paragraph("HALAMAN 2 — CLASS DIAGRAM & PENJELASAN RELASI", h1_style))
story.append(Spacer(1, 2))

story.append(Paragraph("<b>5. Class Diagram Terbaru (Bagian B)</b>", h1_style))
diagram_img_path = os.path.join(assets_dir, "class_diagram.png")
if os.path.exists(diagram_img_path):
    story.append(RLImage(diagram_img_path, width=6.8 * inch, height=3.8 * inch))
    story.append(Paragraph("Gambar 1. UML Class Diagram Relasi Antarobject Sistem Kasir Sederhana (P4)", caption_style))

story.append(Spacer(1, 2))
story.append(Paragraph("<b>6. Penjelasan Association, Aggregation, dan Composition:</b>", h1_style))
story.append(Paragraph("• <b>Penjelasan Association (Pelanggan - Transaksi):</b> Hubungan struktural 'uses-a' di mana objek Transaksi menyimpan referensi objek Pelanggan. Pelanggan berinteraksi dalam proses transaksi belanja untuk menentukan potongan diskon member, dan objek Pelanggan tetap hidup mandiri di luar transaksi.", body_style))
story.append(Paragraph("• <b>Penjelasan Aggregation (ItemTransaksi - Produk):</b> Hubungan 'has-a' di mana objek Produk dibuat secara independen di luar (pada master katalog) dan dioper ke dalam ItemTransaksi. Jika transaksi selesai atau dibatalkan, objek Produk tidak musnah, melainkan tetap eksis di gudang/katalog toko.", body_style))
story.append(Paragraph("• <b>Penjelasan Composition (Transaksi - ItemTransaksi):</b> Hubungan 'part-of' berketergantungan kuat di mana objek ItemTransaksi diciptakan dan dikelola di dalam method tambahItem() milik Transaksi. Siklus hidup ItemTransaksi bergantung penuh pada Transaksi; jika Transaksi dihapus, seluruh detail ItemTransaksi ikut musnah.", body_style))

# =========================================================================
# HALAMAN 3: SCREENSHOT PROGRAM, SKENARIO TEST, SPRINT REVIEW & RETROSPECTIVE
# =========================================================================
story.append(PageBreak())
story.append(Paragraph("HALAMAN 3 — HASIL RUNNING, SPRINT REVIEW & RETROSPECTIVE", h1_style))
story.append(Spacer(1, 2))

story.append(Paragraph("<b>7. Screenshot Hasil Running Program (Main.java)</b>", h1_style))
term_img_path = os.path.join(assets_dir, "hasil_running.png")
if os.path.exists(term_img_path):
    story.append(RLImage(term_img_path, width=6.5 * inch, height=3.9 * inch))
    story.append(Paragraph("Gambar 2. Bukti Running Program Main.java pada Terminal (Eksekusi 3 Skenario)", caption_style))
    story.append(Spacer(1, 2))

story.append(Paragraph("<b>8. Skenario Pengujian (Bagian F):</b>", h1_style))
story.append(Paragraph("• <b>Test 1 (Object Berhasil Dibuat):</b> Objek Produk (P001, P002, P003), Pelanggan (VIP, GOLD, REGULER), dan Transaksi (TRX-001, TRX-002) berhasil diinstansiasi secara mandiri.<br/>"
                       "• <b>Test 2 (Dua atau Lebih Object Berinteraksi):</b> Transaksi berinteraksi dengan Pelanggan dan Produk via ItemTransaksi, serta validasi batas stok berhasil menolak pembelian melebihi kuota.<br/>"
                       "• <b>Test 3 (Pemanfaatan Data Object Lain):</b> Transaksi mengekstrak harga Produk untuk subtotal, diskon Pelanggan untuk potongan, memotong stok, serta mencetak struk belanja multi-item resmi.", body_style))
story.append(Spacer(1, 2))

story.append(Paragraph("<b>9. Sprint Review</b>", h1_style))
rev_table_data = [
    [Paragraph("Item", cell_bold), Paragraph("Hasil", cell_bold)],
    [Paragraph("Class diagram diperbarui", cell_style), Paragraph("Class diagram diperbarui memuat 4 class dan simbol relasi lengkap.", cell_style)],
    [Paragraph("Association berhasil", cell_style), Paragraph("Relasi Pelanggan - Transaksi terhubung dan berfungsi dengan baik.", cell_style)],
    [Paragraph("Aggregation berhasil", cell_style), Paragraph("Relasi ItemTransaksi - Produk sukses mereferensikan master barang.", cell_style)],
    [Paragraph("Composition berhasil", cell_style), Paragraph("Relasi Transaksi - ItemTransaksi sukses mengelola multi-item belanja.", cell_style)],
    [Paragraph("Object dapat berinteraksi", cell_style), Paragraph("Seluruh objek saling bertukar data dan beroperasi secara terpadu.", cell_style)],
    [Paragraph("Program berjalan", cell_style), Paragraph("Kompilasi (javac) dan eksekusi (java) berhasil 100% tanpa error.", cell_style)],
    [Paragraph("Kendala", cell_style), Paragraph("Tidak ada kendala fatal. Implementasi multi-item berjalan lancar.", cell_style)]
]
t_rev = Table(rev_table_data, colWidths=[2.2 * inch, 4.8 * inch])
t_rev.setStyle(TableStyle([
    ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('TOPPADDING', (0, 0), (-1, -1), 1.5),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 1.5),
]))
story.append(t_rev)
story.append(Spacer(1, 3))

story.append(Paragraph("<b>10. Sprint Retrospective</b>", h1_style))
retro_text = (
    "<b>What Went Well?</b> Seluruh relasi Association, Aggregation, dan Composition berhasil dimodelkan dan dibuktikan dalam kode berjalan rapi.<br/>"
    "<b>What Went Wrong?</b> Diperlukan ketelitian dalam menentukan batas hidup objek agar notasi Aggregation dan Composition tidak tertukar.<br/>"
    "<b>Improvement:</b> Pada sprint berikutnya (P5: Inheritance), hierarki generalisasi Produk dan Pelanggan akan diterapkan untuk pewarisan sifat."
)
story.append(Paragraph(retro_text, body_style))

doc.build(story)
print(f"P4 PDF successfully generated: {pdf_path}")
