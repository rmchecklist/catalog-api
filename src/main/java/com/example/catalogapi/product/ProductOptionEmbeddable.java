package com.example.catalogapi.product;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;

@Embeddable
public class ProductOptionEmbeddable {
    private String label;
    private String weight;
    private Integer minQty;
    private boolean available;
    private String sku;
    private java.math.BigDecimal purchasePrice;
    private java.math.BigDecimal sellingPrice;
    private java.math.BigDecimal marketPrice;
    @Column(name = "stock_qty")
    private Integer stock;
    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Integer getMinQty() {
        return minQty;
    }

    public void setMinQty(Integer minQty) {
        this.minQty = minQty;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public java.math.BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(java.math.BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public java.math.BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(java.math.BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public java.math.BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(java.math.BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }
}
