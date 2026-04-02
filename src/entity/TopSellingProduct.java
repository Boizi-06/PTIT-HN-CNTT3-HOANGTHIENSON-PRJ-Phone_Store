package entity;

/**
 * DTO dùng để hiển thị Top sản phẩm bán chạy trong Admin Panel
 */
public class TopSellingProduct {
    private final int productId;
    private final String productName;
    private final long quantitySold;
    private final double revenue;

    public TopSellingProduct(int productId, String productName, long quantitySold, double revenue) {
        this.productId = productId;
        this.productName = productName;
        this.quantitySold = quantitySold;
        this.revenue = revenue;
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public long getQuantitySold() { return quantitySold; }
    public double getRevenue() { return revenue; }
}