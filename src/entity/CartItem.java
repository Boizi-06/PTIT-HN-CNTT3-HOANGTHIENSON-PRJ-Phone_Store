package entity;

public class CartItem {
    private int cartItemId;
    private int productId;
    private String productName;
    private String categoryName;
    private String storage;
    private String color;
    private double price;
    private int quantity;

    public CartItem(int cartItemId, int productId, String productName, String categoryName,
                    String storage, String color, double price, int quantity) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.categoryName = categoryName;
        this.storage = storage;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters
    public int getCartItemId() { return cartItemId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getCategoryName() { return categoryName; }
    public String getStorage() { return storage; }
    public String getColor() { return color; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public double getSubtotal() {
        return price * quantity;
    }
}