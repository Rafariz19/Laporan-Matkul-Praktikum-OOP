public class ItemTransaksi {
    // Relasi Aggregation: ItemTransaksi merujuk pada objek Produk yang dibuat di luar.
    // Objek Produk tetap dapat eksis secara mandiri meskipun objek ItemTransaksi dihapus.
    private Produk produk;
    private int jumlahBeli;

    // Constructor Aggregation
    public ItemTransaksi(Produk produk, int jumlahBeli) {
        this.produk = produk;
        setJumlahBeli(jumlahBeli);
    }

    // --- GETTER & SETTER ---
    public Produk getProduk() {
        return produk;
    }

    public void setProduk(Produk produk) {
        if (produk != null) {
            this.produk = produk;
        } else {
            System.out.println("[ERROR VALIDASI] Objek produk tidak boleh null!");
        }
    }

    public int getJumlahBeli() {
        return jumlahBeli;
    }

    public void setJumlahBeli(int jumlahBeli) {
        if (jumlahBeli > 0) {
            this.jumlahBeli = jumlahBeli;
        } else {
            System.out.println("[ERROR VALIDASI] Jumlah beli harus lebih dari 0! (Ditolak: " + jumlahBeli + ")");
        }
    }

    // --- METODE KALKULASI & DISPLAY ---
    public int hitungSubtotal() {
        if (produk != null) {
            return produk.getHarga() * jumlahBeli;
        }
        return 0;
    }

    public void tampilkanItem() {
        if (produk != null) {
            System.out.printf("  %-16s | %3d pcs | Rp%7d | Subtotal: Rp%8d\n",
                    produk.getNama(), jumlahBeli, produk.getHarga(), hitungSubtotal());
        }
    }
}
