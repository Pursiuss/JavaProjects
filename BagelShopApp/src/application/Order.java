package application;

public class Order {
    // --- Constants for Pricing and Tax ---
    public static final double tax = 0.13; // 13% sales tax
    
    // Bagel Prices
    public static final double whiteBagel = 1.25;
    public static final double wholeWheatBagel = 1.50;
    
    // Topping Prices
    public static final double creamCheese = 0.50;
    public static final double butter = 0.25;
    public static final double blueJam = 0.75;
    public static final double raspJam = 0.75;
    public static final double jelly = 0.75;
    
    // Coffee Prices
    public static final double coffee = 1.25;
    public static final double capp = 2.00;
    public static final double cafeAuLait = 1.75;
    
    // --- Fields to hold the current order selections ---
    private double bagelCost = 0.0;
    private double toppingsCost = 0.0;
    private double coffeeCost = 0.0;

    // --- Core Calculation Methods (from the assignment requirements) ---

    public void setBagelCost(double cost) {
        this.bagelCost = cost;
    }

    public void setToppingsCost(double cost) {
        this.toppingsCost = cost;
    }

    public void setCoffeeCost(double cost) {
        this.coffeeCost = cost;
    }
    
    /**
     * Corresponds to the BagelCost(), CoffeeCost(), and ToppingCost() logic.
     * @return The total cost of the items selected (Subtotal before tax).
     */
    public double calculateSubtotal() {
        return bagelCost + toppingsCost + coffeeCost;
    }

    /**
     * Corresponds to the CalcTax() logic.
     * @param subtotal The subtotal amount.
     * @return The tax amount (13% of subtotal).
     */
    public double calculateTax(double subtotal) {
        // Only apply tax if a bagel has been selected, as coffee alone is not offered for delivery
        // Assuming the application is for orders, if a bagel is selected, the subtotal is taxed.
        // A simple check: if no bagel, no order/tax.
        if (bagelCost > 0) {
            return subtotal * tax;
        }
        return 0.0;
    }

    /**
     * Calculates the final total cost of the order.
     * @return The total of the order, including 13% sales tax.
     */
    public double calculateTotal() {
        double subtotal = calculateSubtotal();
        double tax = calculateTax(subtotal);
        return subtotal + tax;
    }
}