import os
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

def set_cell_background(cell, fill_hex):
    shading_elm = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
    cell._tc.get_or_add_tcPr().append(shading_elm)

def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = OxmlElement('w:tcMar')
    for m, val in [('top', top), ('bottom', bottom), ('left', left), ('right', right)]:
        node = OxmlElement(f'w:{m}')
        node.set(qn('w:w'), str(val))
        node.set(qn('w:type'), 'dxa')
        tcMar.append(node)
    tcPr.append(tcMar)

def set_table_borders(table):
    tblPr = table._tbl.tblPr
    borders = parse_xml(
        f'<w:tblBorders {nsdecls("w")}>\n'
        f'  <w:top w:val="single" w:sz="6" w:space="0" w:color="CCCCCC"/>\n'
        f'  <w:bottom w:val="single" w:sz="6" w:space="0" w:color="CCCCCC"/>\n'
        f'  <w:insideH w:val="single" w:sz="4" w:space="0" w:color="E0E0E0"/>\n'
        f'  <w:insideV w:val="none"/>\n'
        f'  <w:left w:val="none"/>\n'
        f'  <w:right w:val="none"/>\n'
        f'</w:tblBorders>'
    )
    tblPr.append(borders)

def add_styled_heading(doc, text, level=1):
    p = doc.add_paragraph()
    if level == 1:
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after = Pt(4)
        run = p.add_run(text)
        run.font.name = "Times New Roman"
        run.font.size = Pt(13)
        run.font.bold = True
        run.font.color.rgb = RGBColor(0, 0, 0)
    elif level == 2:
        p.paragraph_format.space_before = Pt(10)
        p.paragraph_format.space_after = Pt(3)
        run = p.add_run(text)
        run.font.name = "Times New Roman"
        run.font.size = Pt(12)
        run.font.bold = True
        run.font.color.rgb = RGBColor(30, 30, 30)
    elif level == 3:
        p.paragraph_format.space_before = Pt(6)
        p.paragraph_format.space_after = Pt(2)
        run = p.add_run(text)
        run.font.name = "Times New Roman"
        run.font.size = Pt(11.5)
        run.font.bold = True
        run.font.italic = True
        run.font.color.rgb = RGBColor(50, 50, 50)
    return p

def add_body_p(doc, text, bold_prefix="", italic=False, space_after=4):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(space_after)
    p.paragraph_format.line_spacing = 1.15
    if bold_prefix:
        r_pre = p.add_run(bold_prefix)
        r_pre.font.name = "Times New Roman"
        r_pre.font.size = Pt(11.5)
        r_pre.font.bold = True
        r_pre.font.color.rgb = RGBColor(0, 0, 0)
    r_body = p.add_run(text)
    r_body.font.name = "Times New Roman"
    r_body.font.size = Pt(11.5)
    r_body.font.italic = italic
    r_body.font.color.rgb = RGBColor(20, 20, 20)
    return p

def add_bullet_p(doc, text, bold_prefix=""):
    p = doc.add_paragraph(style='List Bullet')
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.line_spacing = 1.15
    if bold_prefix:
        r_pre = p.add_run(bold_prefix)
        r_pre.font.name = "Times New Roman"
        r_pre.font.size = Pt(11)
        r_pre.font.bold = True
        r_pre.font.color.rgb = RGBColor(0, 0, 0)
    r_body = p.add_run(text)
    r_body.font.name = "Times New Roman"
    r_body.font.size = Pt(11)
    r_body.font.color.rgb = RGBColor(20, 20, 20)
    return p

def build_laporan_p3(output_filename):
    doc = docx.Document()

    # Margin 1 inch (2.54 cm)
    for section in doc.sections:
        section.top_margin = Inches(1.0)
        section.bottom_margin = Inches(1.0)
        section.left_margin = Inches(1.0)
        section.right_margin = Inches(1.0)

    normal_style = doc.styles['Normal']
    normal_style.font.name = 'Times New Roman'
    normal_style.font.size = Pt(11.5)
    normal_style.font.color.rgb = RGBColor(0, 0, 0)

    # ---------------------------------------------------------------------------
    # HEADER / TITLE
    # ---------------------------------------------------------------------------
    p_title = doc.add_paragraph()
    p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_title.paragraph_format.space_before = Pt(0)
    p_title.paragraph_format.space_after = Pt(2)
    r = p_title.add_run("LAPORAN PRAKTIKUM PEMROGRAMAN BERORIENTASI OBYEK")
    r.font.name = "Times New Roman"
    r.font.size = Pt(14)
    r.font.bold = True

    p_sub = doc.add_paragraph()
    p_sub.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_sub.paragraph_format.space_before = Pt(0)
    p_sub.paragraph_format.space_after = Pt(14)
    r = p_sub.add_run("MODUL 3: ENCAPSULATION, ACCESS MODIFIER, GETTER–SETTER, DAN VALIDASI DATA")
    r.font.name = "Times New Roman"
    r.font.size = Pt(12)
    r.font.bold = True

    # Identity Table
    table_id = doc.add_table(rows=4, cols=2)
    table_id.alignment = WD_TABLE_ALIGNMENT.CENTER
    table_id.autofit = False
    
    id_data = [
        ("Nama Mahasiswa", ": Rafael Rizky"),
        ("NRP", ": 3125522007"),
        ("Dosen Pengampu", ": Nirwana Haidar Hari, S.Pd., M.Kom."),
        ("Program Studi / Kampus", ": D3 Teknik Informatika - PENS PSDKU Sumenep (2026)")
    ]
    for idx, (label, val) in enumerate(id_data):
        row = table_id.rows[idx]
        cell_lbl, cell_val = row.cells[0], row.cells[1]
        cell_lbl.width = Inches(2.2)
        cell_val.width = Inches(4.3)
        
        p0 = cell_lbl.paragraphs[0]
        p0.paragraph_format.space_after = Pt(1)
        r0 = p0.add_run(label)
        r0.font.name = "Times New Roman"
        r0.font.size = Pt(11)
        r0.font.bold = True
        
        p1 = cell_val.paragraphs[0]
        p1.paragraph_format.space_after = Pt(1)
        r1 = p1.add_run(val)
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(11)

    doc.add_paragraph().paragraph_format.space_after = Pt(6)

    # ---------------------------------------------------------------------------
    # HALAMAN 1 / BAGIAN 1: REVIEW P2, SPRINT PLANNING & AUDIT CLASS P2
    # ---------------------------------------------------------------------------
    add_styled_heading(doc, "1. Deskripsi Proyek dan Review Hasil P2", level=1)
    add_body_p(doc, "Sistem Kasir Sederhana", bold_prefix="1.1 Nama Proyek: ")
    add_body_p(doc, "Pada praktikum P2, struktur class Produk, Pelanggan, dan Transaksi telah berhasil diimplementasikan. Namun, seluruh atribut pada class tersebut dideklarasikan dengan access modifier default (package-private). Kondisi ini menimbulkan kerentanan keamanan data (information exposure), di mana pihak luar dapat memodifikasi isi atribut secara langsung tanpa melalui mekanisme kontrol atau validasi (misalnya: menetapkan harga negatif atau jumlah stok yang tidak realistis). Oleh karena itu, pada praktikum P3 ini dilakukan refactoring menyeluruh menggunakan prinsip Encapsulation.", bold_prefix="1.2 Latar Belakang Masalah: ")
    
    add_styled_heading(doc, "2. Perencanaan Agile Sprint (Sprint P3)", level=1)
    add_body_p(doc, "Memperbaiki struktur class proyek (Produk, Pelanggan, dan Transaksi) dengan menerapkan encapsulation sehingga seluruh atribut bersifat private, menyediakan akses terkontrol via Getter dan Setter, serta menerapkan validasi data ketat pada method mutator dan constructor.", bold_prefix="2.1 Sprint Goal: ")

    add_body_p(doc, "Sprint Backlog P3", bold_prefix="2.2 ")
    t_sb = doc.add_table(rows=7, cols=3)
    t_sb.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_sb)
    sb_data = [
        ("ID", "Sprint Backlog", "Status"),
        ("SB-01", "Mengubah seluruh atribut pada class Produk, Pelanggan, dan Transaksi menjadi private.", "DONE"),
        ("SB-02", "Membuat method getter untuk atribut yang perlu dibaca dari luar class.", "DONE"),
        ("SB-03", "Membuat method setter selektif hanya untuk atribut yang memang diizinkan diubah.", "DONE"),
        ("SB-04", "Menambahkan aturan validasi data pada setter (harga > 0, stok >= 0, format nomor HP, tipe member, kuantitas transaksi).", "DONE"),
        ("SB-05", "Memperbaiki constructor agar memanggil setter dan mematuhi seluruh aturan validasi.", "DONE"),
        ("SB-06", "Melakukan pengujian komprehensif pada Main.java mencakup skenario Test Valid dan Test Invalid.", "DONE")
    ]
    for r_idx, row_content in enumerate(sb_data):
        for c_idx, val in enumerate(row_content):
            cell = t_sb.rows[r_idx].cells[c_idx]
            if c_idx == 0:
                cell.width = Inches(1.0)
            elif c_idx == 1:
                cell.width = Inches(4.5)
            else:
                cell.width = Inches(1.0)
            set_cell_margins(cell, 80, 80, 100, 100)
            p = cell.paragraphs[0]
            p.paragraph_format.space_after = Pt(0)
            p.paragraph_format.line_spacing = 1.1
            run = p.add_run(val)
            run.font.name = "Times New Roman"
            run.font.size = Pt(10.5)
            if r_idx == 0:
                run.font.bold = True
                set_cell_background(cell, "EAEAEA")
            elif c_idx == 2:
                run.font.bold = True

    doc.add_paragraph().paragraph_format.space_after = Pt(6)

    add_styled_heading(doc, "3. Audit Class P2 (Bagian A)", level=1)
    add_body_p(doc, "Audit dilakukan terhadap 3 class hasil pengerjaan P2 untuk mengidentifikasi status akses atribut dan perbaikan yang harus diimplementasikan pada P3:")
    
    t_audit = doc.add_table(rows=12, cols=4)
    t_audit.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_audit)
    audit_data = [
        ("Class", "Attribute", "Kondisi P2", "Perbaikan P3"),
        ("Produk", "kode", "default (bisa diakses langsung)", "private + getter only (read-only)"),
        ("Produk", "nama", "default (bisa diakses langsung)", "private + getter + setter (validasi non-empty)"),
        ("Produk", "harga", "default (tanpa validasi nilai)", "private + getter + setter (validasi > 0)"),
        ("Produk", "stok", "default (tanpa batas negatif)", "private + getter + setter (validasi >= 0)"),
        ("Pelanggan", "idPelanggan", "default (bisa diakses langsung)", "private + getter only (read-only)"),
        ("Pelanggan", "nama", "default (bisa diakses langsung)", "private + getter + setter (validasi non-empty)"),
        ("Pelanggan", "nomorHP", "default (tanpa validasi format)", "private + getter + setter (validasi regex 10-13 digit)"),
        ("Pelanggan", "tipeMember", "default (bisa diisi sembarang)", "private + getter + setter (validasi VIP/GOLD/REGULER)"),
        ("Transaksi", "idTransaksi", "default (bisa diakses langsung)", "private + getter only (read-only)"),
        ("Transaksi", "tanggal, produk, pelanggan", "default (bisa diakses langsung)", "private + getter only (diatur via constructor)"),
        ("Transaksi", "jumlahBeli", "default (tanpa cek stok)", "private + getter + setter (validasi > 0 dan <= stok)")
    ]
    for r_idx, row_content in enumerate(audit_data):
        for c_idx, val in enumerate(row_content):
            cell = t_audit.rows[r_idx].cells[c_idx]
            if c_idx == 0:
                cell.width = Inches(1.1)
            elif c_idx == 1:
                cell.width = Inches(1.8)
            elif c_idx == 2:
                cell.width = Inches(1.8)
            else:
                cell.width = Inches(1.8)
            set_cell_margins(cell, 60, 60, 80, 80)
            p = cell.paragraphs[0]
            p.paragraph_format.space_after = Pt(0)
            p.paragraph_format.line_spacing = 1.1
            run = p.add_run(val)
            run.font.name = "Times New Roman"
            run.font.size = Pt(9.5)
            if r_idx == 0:
                run.font.bold = True
                set_cell_background(cell, "EAEAEA")

    doc.add_page_break()

    # ---------------------------------------------------------------------------
    # HALAMAN 2 / BAGIAN 2: REFACTORING ENCAPSULATION & CLASS DIAGRAM
    # ---------------------------------------------------------------------------
    add_styled_heading(doc, "4. Implementasi Encapsulation, Getter-Setter, dan Validasi (Bagian B–E)", level=1)
    add_body_p(doc, "Rincian spesifikasi enkapsulasi, metode akses, dan aturan validasi data pada seluruh class dirangkum dalam tabel berikut:")

    t_spec = doc.add_table(rows=4, cols=5)
    t_spec.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_spec)
    spec_data = [
        ("Class", "Private Attribute", "Getter", "Setter", "Validation Rule"),
        ("Produk", 
         "• kode\n• nama\n• harga\n• stok",
         "getKode()\ngetNama()\ngetHarga()\ngetStok()",
         "setNama()\nsetHarga()\nsetStok()",
         "• nama: tidak null & !isBlank()\n• harga: > 0\n• stok: >= 0"),
        ("Pelanggan",
         "• idPelanggan\n• nama\n• nomorHP\n• tipeMember",
         "getIdPelanggan()\ngetNama()\ngetNomorHP()\ngetTipeMember()\ngetDiskon()",
         "setNama()\nsetNomorHP()\nsetTipeMember()",
         "• nama: tidak null & !isBlank()\n• nomorHP: 10-13 digit angka\n• tipe: VIP, GOLD, atau REGULER"),
        ("Transaksi",
         "• idTransaksi\n• tanggal\n• pelanggan\n• produk\n• jumlahBeli",
         "getIdTransaksi()\ngetTanggal()\ngetPelanggan()\ngetProduk()\ngetJumlahBeli()",
         "setJumlahBeli()",
         "• jumlahBeli: > 0\n• jumlahBeli <= produk.getStok()")
    ]
    for r_idx, row_content in enumerate(spec_data):
        for c_idx, val in enumerate(row_content):
            cell = t_spec.rows[r_idx].cells[c_idx]
            if c_idx == 0:
                cell.width = Inches(1.0)
            elif c_idx == 1:
                cell.width = Inches(1.3)
            elif c_idx == 2:
                cell.width = Inches(1.4)
            elif c_idx == 3:
                cell.width = Inches(1.2)
            else:
                cell.width = Inches(1.6)
            set_cell_margins(cell, 80, 80, 100, 100)
            p = cell.paragraphs[0]
            p.paragraph_format.space_after = Pt(0)
            p.paragraph_format.line_spacing = 1.1
            run = p.add_run(val)
            run.font.name = "Times New Roman"
            run.font.size = Pt(9.5)
            if r_idx == 0:
                run.font.bold = True
                set_cell_background(cell, "EAEAEA")

    doc.add_paragraph().paragraph_format.space_after = Pt(8)

    add_body_p(doc, "Diagram Kelas UML Hasil Refactoring (P3)", bold_prefix="4.2 ")
    add_body_p(doc, "Class diagram di bawah ini memperlihatkan visibilitas private (-) pada seluruh atribut dan visibilitas public (+) pada constructor, getter, setter, dan metode operasional.")

    diagram_path = os.path.join(r"d:\KULIAH\Semester 3\OOP\P3_3125522007_Rafael_Rizky\assets", "class_diagram.png")
    if os.path.exists(diagram_path):
        p_img = doc.add_paragraph()
        p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img.paragraph_format.space_before = Pt(4)
        p_img.paragraph_format.space_after = Pt(2)
        run_img = p_img.add_run()
        run_img.add_picture(diagram_path, width=Inches(6.2))

        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_before = Pt(0)
        p_cap.paragraph_format.space_after = Pt(8)
        r_cap = p_cap.add_run("Gambar 1. UML Class Diagram Sistem Kasir Sederhana (Terenkapsulasi P3)")
        r_cap.font.name = "Times New Roman"
        r_cap.font.size = Pt(10)
        r_cap.font.italic = True

    add_body_p(doc, "Prinsip Information Hiding & Validasi Constructor (Bagian E):", bold_prefix="4.3 ")
    add_bullet_p(doc, "Seluruh field data tidak dapat lagi diakses langsung seperti 'produk.harga = -5000;'. Pengubahan nilai wajib memanggil setter.", bold_prefix="1. Pembatasan Akses: ")
    add_bullet_p(doc, "Atribut kode, idPelanggan, idTransaksi, dan tanggal hanya memiliki getter (read-only), mencegah manipulasi identitas data setelah objek dibuat.", bold_prefix="2. Immutability Selektif: ")
    add_bullet_p(doc, "Constructor pada class Produk, Pelanggan, dan Transaksi dimodifikasi agar memanggil method setter internal. Hal ini menjamin bahwa pembentukan objek baru tidak dapat membypass aturan validasi bisnis.", bold_prefix="3. Integrasi Validasi Constructor: ")

    doc.add_page_break()

    # ---------------------------------------------------------------------------
    # HALAMAN 3 / BAGIAN 3: PENGUJIAN (TEST VALID & INVALID), SPRINT REVIEW & RETROSPECTIVE
    # ---------------------------------------------------------------------------
    add_styled_heading(doc, "5. Pengujian Program (Bagian F) dan Hasil Eksekusi", level=1)
    add_body_p(doc, "Pengujian pada class Main.java dibagi ke dalam dua kelompok skenario pengujian:")
    add_bullet_p(doc, "Menguji instansiasi data legal via constructor, pembacaan nilai melalui getter, pengubahan harga/nomor HP/tipe member secara terkontrol melalui setter, serta eksekusi transaksi yang berhasil memotong stok dan mencetak nota kasir resmi.", bold_prefix="a. Test Valid: ")
    add_bullet_p(doc, "Menguji ketahanan sistem terhadap input data salah: memasukkan harga negatif (-10000), stok negatif (-15), nama kosong (\"\"), format nomor HP non-angka / tidak valid, tipe member tidak terdaftar, serta transaksi dengan jumlah beli melebihi ketersediaan stok. Terbukti seluruh input salah ditolak dengan pesan kesalahan yang informatif dan integritas data objek tetap terjaga utuh.", bold_prefix="b. Test Invalid: ")

    add_body_p(doc, "Tangkapan Layar Hasil Running Terminal PowerShell", bold_prefix="5.1 ")
    term_img_path = os.path.join(r"d:\KULIAH\Semester 3\OOP\P3_3125522007_Rafael_Rizky\assets", "hasil_running.png")
    if os.path.exists(term_img_path):
        p_img2 = doc.add_paragraph()
        p_img2.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img2.paragraph_format.space_before = Pt(4)
        p_img2.paragraph_format.space_after = Pt(2)
        run_img2 = p_img2.add_run()
        run_img2.add_picture(term_img_path, width=Inches(6.0))

        p_cap2 = doc.add_paragraph()
        p_cap2.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap2.paragraph_format.space_before = Pt(0)
        p_cap2.paragraph_format.space_after = Pt(8)
        r_cap2 = p_cap2.add_run("Gambar 2. Bukti Running Program Main.java (Uji Valid & Invalid) di Terminal")
        r_cap2.font.name = "Times New Roman"
        r_cap2.font.size = Pt(10)
        r_cap2.font.italic = True

    add_styled_heading(doc, "6. Sprint Review (Evaluasi Sprint P3)", level=1)
    t_rev = doc.add_table(rows=8, cols=2)
    t_rev.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_rev)
    rev_data = [
        ("Item Evaluasi", "Hasil / Keterangan"),
        ("Attribute berhasil di-private", "Seluruh atribut utama pada 3 class (Produk, Pelanggan, Transaksi) telah diubah menjadi private."),
        ("Getter berjalan", "Method getter berhasil membaca seluruh nilai atribut tanpa memaparkan referensi internal."),
        ("Setter berjalan", "Method setter berjalan lancar dan mengontrol mutasi nilai data dengan baik."),
        ("Validasi berhasil", "Minimal 3 aturan validasi berhasil diterapkan dan terbukti menolak masukan yang salah."),
        ("Constructor diperbaiki", "Constructor pada seluruh class telah mematuhi aturan validasi."),
        ("Test invalid berhasil", "Pengujian dengan data tidak valid berhasil ditolak dan data objek tidak berubah."),
        ("Program dapat dijalankan", "Program sukses dikompilasi bebas error dan berjalan lancar di terminal PowerShell.")
    ]
    for r_idx, row_content in enumerate(rev_data):
        for c_idx, val in enumerate(row_content):
            cell = t_rev.rows[r_idx].cells[c_idx]
            if c_idx == 0:
                cell.width = Inches(2.2)
            else:
                cell.width = Inches(4.3)
            set_cell_margins(cell, 80, 80, 100, 100)
            p = cell.paragraphs[0]
            p.paragraph_format.space_after = Pt(0)
            p.paragraph_format.line_spacing = 1.1
            run = p.add_run(val)
            run.font.name = "Times New Roman"
            run.font.size = Pt(10.5)
            if r_idx == 0:
                run.font.bold = True
                set_cell_background(cell, "EAEAEA")

    doc.add_paragraph().paragraph_format.space_after = Pt(8)

    add_styled_heading(doc, "7. Sprint Retrospective", level=1)
    add_body_p(doc, "Refactoring prinsip encapsulation berjalan mulus. Seluruh atribut terproteksi dengan baik dan mekanisme validasi berhasil mengamankan nilai objek dari kerusakan akibat input ilegal.", bold_prefix="What Went Well? ")
    add_body_p(doc, "Penyesuaian constructor dan setter memerlukan ketelitian agar nilai default tidak menyebabkan inkonsistensi saat validasi pertama kali dijalankan.", bold_prefix="What Went Wrong? ")
    add_body_p(doc, "Pada sprint berikutnya (P4: Relasi Antarobject), struktur class akan dikembangkan lebih lanjut dengan pemodelan relasi agregasi, asosiasi, dan komposisi yang lebih kompleks dan dinamis.", bold_prefix="Improvement: ")

    doc.add_paragraph().paragraph_format.space_after = Pt(8)

    add_styled_heading(doc, "8. Definition of Done (DoD) Checklist Modul 3", level=1)
    dod_items = [
        ("Menggunakan project P2 (Sistem Kasir Sederhana)", True),
        ("Minimal 3 class direfactor (Produk, Pelanggan, Transaksi)", True),
        ("Attribute utama menggunakan private (information hiding)", True),
        ("Getter dibuat sesuai kebutuhan", True),
        ("Setter hanya dibuat bila diperlukan (controlled mutator)", True),
        ("Minimal 3 validasi data diterapkan (6 aturan validasi terpasang)", True),
        ("Constructor mengikuti aturan validasi", True),
        ("Tidak ada perubahan attribute secara langsung dari Main", True),
        ("Terdapat pengujian data valid (Test Valid)", True),
        ("Terdapat pengujian data tidak valid (Test Invalid)", True),
        ("Program berhasil dikompilasi bebas error", True),
        ("Program berhasil dijalankan dengan output yang sesuai", True),
        ("Sprint Backlog diperbarui dengan status DONE", True),
        ("Sprint Review dan Sprint Retrospective tersedia lengkap", True)
    ]
    t_dod = doc.add_table(rows=len(dod_items)+1, cols=2)
    t_dod.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_dod)
    
    hdr0, hdr1 = t_dod.rows[0].cells[0], t_dod.rows[0].cells[1]
    hdr0.width, hdr1.width = Inches(5.3), Inches(1.2)
    set_cell_margins(hdr0, 80, 80, 100, 100)
    set_cell_margins(hdr1, 80, 80, 100, 100)
    set_cell_background(hdr0, "EAEAEA")
    set_cell_background(hdr1, "EAEAEA")
    p = hdr0.paragraphs[0]; p.paragraph_format.space_after = Pt(0); r = p.add_run("Kriteria Definition of Done (Modul 3)"); r.font.name = "Times New Roman"; r.font.bold = True; r.font.size = Pt(10.5)
    p = hdr1.paragraphs[0]; p.paragraph_format.space_after = Pt(0); r = p.add_run("Status"); r.font.name = "Times New Roman"; r.font.bold = True; r.font.size = Pt(10.5)

    for idx, (item, fulfilled) in enumerate(dod_items, start=1):
        c0, c1 = t_dod.rows[idx].cells[0], t_dod.rows[idx].cells[1]
        c0.width, c1.width = Inches(5.3), Inches(1.2)
        set_cell_margins(c0, 60, 60, 100, 100)
        set_cell_margins(c1, 60, 60, 100, 100)
        
        p0 = c0.paragraphs[0]; p0.paragraph_format.space_after = Pt(0); p0.paragraph_format.line_spacing = 1.1
        r0 = p0.add_run(item); r0.font.name = "Times New Roman"; r0.font.size = Pt(10)
        
        p1 = c1.paragraphs[0]; p1.paragraph_format.space_after = Pt(0); p1.paragraph_format.line_spacing = 1.1
        r1 = p1.add_run("[ v ] Terpenuhi" if fulfilled else "[   ] Belum"); r1.font.name = "Times New Roman"; r1.font.size = Pt(10); r1.font.bold = True

    doc.add_page_break()

    # ---------------------------------------------------------------------------
    # LAMPIRAN: SOURCE CODE JAVA LENGKAP P3
    # ---------------------------------------------------------------------------
    add_styled_heading(doc, "Lampiran: Source Code Java (Terenkapsulasi)", level=1)
    
    code_files = [
        ("Produk.java", r"d:\KULIAH\Semester 3\OOP\P3_3125522007_Rafael_Rizky\src\Produk.java"),
        ("Pelanggan.java", r"d:\KULIAH\Semester 3\OOP\P3_3125522007_Rafael_Rizky\src\Pelanggan.java"),
        ("Transaksi.java", r"d:\KULIAH\Semester 3\OOP\P3_3125522007_Rafael_Rizky\src\Transaksi.java"),
        ("Main.java", r"d:\KULIAH\Semester 3\OOP\P3_3125522007_Rafael_Rizky\src\Main.java")
    ]

    for fname, fpath in code_files:
        add_styled_heading(doc, f"Berkas: {fname}", level=2)
        if os.path.exists(fpath):
            with open(fpath, "r", encoding="utf-8") as f:
                code_content = f.read()
            
            tbl = doc.add_table(rows=1, cols=1)
            tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
            cell = tbl.rows[0].cells[0]
            cell.width = Inches(6.5)
            set_cell_background(cell, "F9F9F9")
            set_cell_margins(cell, 120, 120, 150, 150)
            set_table_borders(tbl)
            
            p = cell.paragraphs[0]
            p.paragraph_format.space_after = Pt(0)
            p.paragraph_format.line_spacing = 1.0
            r = p.add_run(code_content)
            r.font.name = "Courier New"
            r.font.size = Pt(9)
            r.font.color.rgb = RGBColor(30, 30, 30)

        doc.add_paragraph().paragraph_format.space_after = Pt(6)

    doc.save(output_filename)
    print(f"Document saved successfully: {output_filename}")

out_docx = r"d:\KULIAH\Semester 3\OOP\P3_3125522007_Rafael_Rizky\Laporan_P3_3125522007_Rafael_Rizky.docx"
build_laporan_p3(out_docx)

readme_docx = r"d:\KULIAH\Semester 3\OOP\P3_3125522007_Rafael_Rizky\README.docx"
build_laporan_p3(readme_docx)
