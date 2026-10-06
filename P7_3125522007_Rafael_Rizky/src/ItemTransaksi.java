public class ItemTransaksi {
    // Relasi Aggregation: ItemTransaksi merujuk pada objek Superclass Abstract Produk.
    // Menunjukkan Polymorphic Reference karena dapat menampung objek turunan apa pun.
    private Produk produk;
    private int jumlahBeli;
    private double diskonItem; // Diskon item khusus promo (0.0 s/d 1.0)

    // Constructor 1: Reguler tanpa diskon item khusus
    public ItemTransaksi(Produk produk, int jumlahBeli) {
        this(produk, jumlahBeli, 0.0);
    }

    // Constructor 2 (Overloaded Constructor): Dengan diskon item promosi khusus
    public ItemTransaksi(Produk produk, int jumlahBeli, double diskonItem) {
        this.produk = produk;
        setJumlahBeli(jumlahBeli);
        this.diskonItem = (diskonItem >= 0.0 && diskonItem <= 1.0) ? diskonItem : 0.0;
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

    public double getDiskonItem() {
        return diskonItem;
    }

    // --- METODE KALKULASI & DISPLAY DENGAN DYNAMIC BINDING ---
    public int hitungSubtotal() {
        if (produk != null) {
            int gross = produk.getHarga() * jumlahBeli;
            return (int)(gross * (1.0 - diskonItem));
        }
        return 0;
    }

    public void tampilkanItem() {
        if (produk != null) {
            // Memanfaatkan dynamic binding getKategoriInfo() dari abstract class Produk
            String diskonStr = (diskonItem > 0.0) ? String.format(" [Disc %.1f%%]", diskonItem * 100) : "";
            System.out.printf("- %s (%s) x %d @ Rp %,d%s = Rp %,d\n",
                    produk.getNama(),
                    produk.getKategoriInfo(),
                    jumlahBeli,
                    produk.getHarga(),
                    diskonStr,
                    hitungSubtotal());
        }
    }
}
