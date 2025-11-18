package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class Main extends Application {


    private final Order currentOrder = new Order();
    private final DecimalFormat currency = new DecimalFormat("0.00");

    private RadioButton radWhiteBagel, radWheatBagel;
    private TextField qtyWhite, qtyWheat;
    private ToggleGroup bagelGroup;

    private CheckBox chkCreamCheese, chkButter, chkBlueberry, chkRaspberry, chkPeach;

    private RadioButton radNoCoffee, radRegCoffee, radCappuccino, radCafeAuLait;
    private TextField qtyCoffee, qtyCapp, qtyCAL;
    private ToggleGroup coffeeGroup;

    private Label lblSubtotalValue, lblTaxValue, lblTotalValue;

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        //Title
        Label title = new Label("Sheridan’s Bagel House");
        title.setFont(new Font("Calibri", 24));
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10, 0, 20, 0));
        root.setTop(titleBox);
        
        // menu
        VBox topContainer = new VBox(0);
        topContainer.setAlignment(Pos.TOP_LEFT);
        topContainer.getChildren().addAll(createMenuBar());
        root.setTop(topContainer);

        //Bagels, Toppings, Coffee
        GridPane optionsGrid = new GridPane();
        optionsGrid.setHgap(20);
        //optionsGrid.setVgap(10);
        optionsGrid.setAlignment(Pos.CENTER);

 
        VBox bagelPane = createBagelPane();
        optionsGrid.add(bagelPane, 0, 0);


        VBox toppingsPane = createToppingsPane();
        optionsGrid.add(toppingsPane, 1, 0);


        VBox coffeePane = createCoffeePane();
        optionsGrid.add(coffeePane, 2, 0);
        
        root.setCenter(optionsGrid);

        // Price
        VBox pricePane = createPricePane();
        optionsGrid.add(pricePane, 3, 0);

        //buttons
        VBox bottomContainer = new VBox(10);
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.getChildren().addAll(createButtonBox());
        root.setBottom(bottomContainer);

        Scene scene = new Scene(root, 1000, 500);
        primaryStage.setTitle("Sheridan Bagel and Coffee Price Calculator");
        primaryStage.setScene(scene);
        primaryStage.show();

        // force reset on start to guarantee nothing is previously filled in
        handleReset();
    }

    private MenuBar createMenuBar() {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit System");
        exitItem.setOnAction(e -> handleExit());
        fileMenu.getItems().add(exitItem);

        Menu orderMenu = new Menu("Order");
        MenuItem calculateItem = new MenuItem("Calculate Total");
        calculateItem.setOnAction(e -> handleCalculate());
        MenuItem resetItem = new MenuItem("Reset Form");
        resetItem.setOnAction(e -> handleReset());
        orderMenu.getItems().addAll(calculateItem, resetItem);
        
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, orderMenu);
        return menuBar;
    }
    

    private VBox createBagelPane() {
    	bagelGroup = new ToggleGroup();
        
        radWhiteBagel = new RadioButton("White Bagel ($" + currency.format(Order.whiteBagel) + ")");
        radWheatBagel = new RadioButton("Whole Wheat Bagel ($" + currency.format(Order.wholeWheatBagel) + ")");
        
        radWhiteBagel.setToggleGroup(bagelGroup);
        radWheatBagel.setToggleGroup(bagelGroup);

        // Create TextFields for Quantity
        qtyWhite = createQuantityTextField();
        qtyWheat = createQuantityTextField();
        
        // Set up layout for each bagel option
        HBox whiteBagelRow = new HBox(10, radWhiteBagel, new Label("Qty:"), qtyWhite);
        HBox wheatBagelRow = new HBox(10, radWheatBagel, new Label("Qty:"), qtyWheat);

        // Add listeners to ensure only the selected bagel's quantity is considered
        radWhiteBagel.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
        	qtyWhite.setDisable(!isSelected);
            if (isSelected) {
                // When White is selected, disable and reset the other
            	qtyWheat.setDisable(true);
                qtyWheat.setText("1");
            }
        });

        radWheatBagel.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
        	qtyWheat.setDisable(!isSelected);
            if (isSelected) {
                // When Wheat is selected, disable and reset the other
            	qtyWhite.setDisable(true);
            	qtyWhite.setText("1");
            }
        });

        // Default selection and initial state
        radWhiteBagel.setSelected(true); // Default to White Bagel, Qty: 1
        qtyWheat.setDisable(true);
        
        VBox bagelVBox = new VBox(5, whiteBagelRow, wheatBagelRow);
        TitledPane titledPane = new TitledPane("Pick a Bagel & Quantity", bagelVBox);
        titledPane.setCollapsible(false);
        VBox.setMargin(titledPane.getContent(), new Insets(5));
        
        return new VBox(titledPane);
    }    


    private VBox createToppingsPane() {
        chkCreamCheese = new CheckBox("Cream Cheese ($" + currency.format(Order.creamCheese) + ")");
        chkButter = new CheckBox("Butter ($" + currency.format(Order.butter) + ")");
        chkBlueberry = new CheckBox("Blueberry Jam ($" + currency.format(Order.blueJam) + ")");
        chkRaspberry = new CheckBox("Raspberry Jam ($" + currency.format(Order.raspJam) + ")");
        chkPeach = new CheckBox("Peach Jelly ($" + currency.format(Order.jelly) + ")");

        VBox toppingsVBox = new VBox(5, chkCreamCheese, chkButter, chkBlueberry, chkRaspberry, chkPeach);
        TitledPane titledPane = new TitledPane("Pick Your Toppings", toppingsVBox);
        titledPane.setCollapsible(false);
        VBox.setMargin(titledPane.getContent(), new Insets(5));

        return new VBox(titledPane);
    }


    private VBox createCoffeePane() {
        coffeeGroup = new ToggleGroup();

        radNoCoffee = new RadioButton("None");
        radRegCoffee = new RadioButton("Regular Coffee ($" + currency.format(Order.coffee) + ")");
        radCappuccino = new RadioButton("Cappuccino ($" + currency.format(Order.capp) + ")");
        radCafeAuLait = new RadioButton("Café au lait ($" + currency.format(Order.cafeAuLait) + ")");

        radNoCoffee.setToggleGroup(coffeeGroup);
        radRegCoffee.setToggleGroup(coffeeGroup);
        radCappuccino.setToggleGroup(coffeeGroup);
        radCafeAuLait.setToggleGroup(coffeeGroup);
        
        // Create TextFields for Quantity
        qtyCoffee = createQuantityTextField();
        qtyCapp = createQuantityTextField();
        qtyCAL = createQuantityTextField();

        // The 'No Coffee' option is special: it disables all coffee quantity fields
        radNoCoffee.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            boolean enable = !isSelected;
            qtyCoffee.setDisable(!enable);
            qtyCapp.setDisable(!enable);
            qtyCAL.setDisable(!enable);
            
            // Reset quantities when 'None' is selected
            if (isSelected) {
            	qtyCoffee.setText("0");
                qtyCapp.setText("0");
                qtyCAL.setText("0");
            }
        });
        
        HBox noCoffeeRow = new HBox(radNoCoffee); // No quantity needed for 'None'
        HBox regCoffeeRow = new HBox(10, radRegCoffee, new Label("Qty:"), qtyCoffee);
        HBox cappuccinoRow = new HBox(10, radCappuccino, new Label("Qty:"), qtyCapp);
        HBox cafeAuLaitRow = new HBox(10, radCafeAuLait, new Label("Qty:"), qtyCAL);
        
        // Default selection and initial state
        radNoCoffee.setSelected(true); // Default to No Coffee
        qtyCoffee.setDisable(true);
        qtyCapp.setDisable(true);
        qtyCAL.setDisable(true);


        VBox coffeeVBox = new VBox(5, noCoffeeRow, regCoffeeRow, cappuccinoRow, cafeAuLaitRow);
        TitledPane titledPane = new TitledPane("Coffee", coffeeVBox);
        titledPane.setCollapsible(false);
        VBox.setMargin(titledPane.getContent(), new Insets(5));

        return new VBox(titledPane);
    }

    private TextField createQuantityTextField() {
        TextField tf = new TextField("1"); 
        tf.setPrefWidth(50);
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tf.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        return tf;
    }
    

    private VBox createPricePane() {

        lblSubtotalValue = new Label("$0.00");
        lblTaxValue = new Label("$0.00");
        lblTotalValue = new Label("$0.00");

        // Styling the result labels
        String borderStyle = "-fx-border-color: darkgray; -fx-border-insets: 2; -fx-border-width: 1; -fx-background-color: white;";
        lblSubtotalValue.setStyle(borderStyle);
        lblTaxValue.setStyle(borderStyle);
        lblTotalValue.setStyle(borderStyle);
        
        lblSubtotalValue.setPrefWidth(100);
        lblTaxValue.setPrefWidth(100);
        lblTotalValue.setPrefWidth(100);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        grid.add(new Label("Subtotal:"), 0, 0);
        grid.add(new Label("Tax (13%):"), 0, 1);
        grid.add(new Label("Total:"), 0, 2);

        grid.add(lblSubtotalValue, 1, 0);
        grid.add(lblTaxValue, 1, 1);
        grid.add(lblTotalValue, 1, 2);

        TitledPane titledPane = new TitledPane("Price", grid);
        titledPane.setCollapsible(false);
        VBox.setMargin(titledPane.getContent(), new Insets(5));
        
        return new VBox(titledPane);
    }

    private HBox createButtonBox() {
        Button btnCalculate = new Button("Calculate Total");
        btnCalculate.setOnAction(e -> handleCalculate()); 

        Button btnReset = new Button("Reset Form");
        btnReset.setOnAction(e -> handleReset());

        Button btnExit = new Button("Exit");
        btnExit.setOnAction(e -> handleExit()); 

        Button btnPrintFile = new Button("Print Receipt to File");
        btnPrintFile.setOnAction(e -> handlePrintReceiptFile());

        Button btnPrintPrinter = new Button("Print Receipt to Printer");
        btnPrintPrinter.setOnAction(e -> handlePrintReceiptPrinter());


        HBox buttonBox = new HBox(15, btnCalculate, btnReset, btnExit, btnPrintFile, btnPrintPrinter);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        return buttonBox;
    }

    private void handleCalculate() {

        java.util.function.Function<TextField, Integer> getQty = (tf) -> {
            try {
                return tf.isDisable() ? 0 : Integer.parseInt(tf.getText());
            } catch (NumberFormatException e) {
                return 0; 
            }
        };
        
        currentOrder.setBagelCost(0.0);
        currentOrder.setToppingsCost(0.0);
        currentOrder.setCoffeeCost(0.0);

        double totalBagelCost = 0.0;
        int totalBagels = 0;

        if (radWhiteBagel.isSelected()) {
            int qty = getQty.apply(qtyWhite);
            totalBagelCost = qty * Order.whiteBagel;
            totalBagels = qty;
        } else if (radWheatBagel.isSelected()) {
            int qty = getQty.apply(qtyWheat);
            totalBagelCost = qty * Order.wholeWheatBagel;
            totalBagels = qty;
        }

        if (totalBagels == 0) {
             showAlert(Alert.AlertType.WARNING, "Missing Selection", "Please select a bagel and enter a quantity greater than zero.");
             return; 
        }
        currentOrder.setBagelCost(totalBagelCost);

        double totalCoffeeCost = 0.0;

        if (radRegCoffee.isSelected()) {
            totalCoffeeCost += getQty.apply(qtyCoffee) * Order.coffee;
        } else if (radCappuccino.isSelected()) { // Using else if for exclusivity
            totalCoffeeCost += getQty.apply(qtyCapp) * Order.capp;
        } else if (radCafeAuLait.isSelected()) {
            totalCoffeeCost += getQty.apply(qtyCAL) * Order.cafeAuLait;
        }

        currentOrder.setCoffeeCost(totalCoffeeCost);
        
        double toppingsTotal = 0.0;

        if (chkCreamCheese.isSelected()) toppingsTotal += Order.creamCheese;
        if (chkButter.isSelected()) toppingsTotal += Order.butter;
        if (chkBlueberry.isSelected()) toppingsTotal += Order.blueJam;
        if (chkRaspberry.isSelected()) toppingsTotal += Order.raspJam;
        if (chkPeach.isSelected()) toppingsTotal += Order.jelly;
        currentOrder.setToppingsCost(toppingsTotal);
        

        double subtotal = currentOrder.calculateSubtotal();
        double tax = currentOrder.calculateTax(subtotal);
        double total = currentOrder.calculateTotal();

        lblSubtotalValue.setText("$" + currency.format(subtotal));
        lblTaxValue.setText("$" + currency.format(tax));
        lblTotalValue.setText("$" + currency.format(total));
    }


    private void handleReset() {
        // ResetBagels()
        radWhiteBagel.setSelected(true); 
        radWheatBagel.setSelected(false);
        qtyWhite.setText("1");
        qtyWhite.setDisable(false); 
        qtyWheat.setText("1");
        qtyWheat.setDisable(true); 
        

        chkCreamCheese.setSelected(false);
        chkButter.setSelected(false);
        chkBlueberry.setSelected(false);
        chkRaspberry.setSelected(false);
        chkPeach.setSelected(false);
        
        // ResetCoffee()
        radNoCoffee.setSelected(true); 
        radRegCoffee.setSelected(false);
        radCappuccino.setSelected(false);
        radCafeAuLait.setSelected(false);
        qtyCoffee.setText("1");
        qtyCoffee.setDisable(true); 
        qtyCapp.setText("1");
        qtyCapp.setDisable(true);
        qtyCAL.setText("1");
        qtyCAL.setDisable(true);


        currentOrder.setBagelCost(0.0);
        currentOrder.setToppingsCost(0.0);
        currentOrder.setCoffeeCost(0.0);
        lblSubtotalValue.setText("$0.00");
        lblTaxValue.setText("$0.00");
        lblTotalValue.setText("$0.00");
    }


    private void handleExit() {
        System.exit(0);
    }
    

    private void handlePrintReceiptFile() {
        try {
            handleCalculate(); 
            
            double subtotal = currentOrder.calculateSubtotal();
            double tax = currentOrder.calculateTax(subtotal);
            double total = currentOrder.calculateTotal();

            // Create a receipt string
            String receipt = "============== SHERIDAN'S BAGEL HOUSE ==============\n";
            receipt += "Bagel: " + getSelectedBagel() + "\n";
            receipt += "Toppings: " + getSelectedToppings() + "\n";
            receipt += "Coffee: " + getSelectedCoffee() + "\n";
            receipt += "----------------------------------------------------\n";
            receipt += String.format("Subtotal:\t$%s\n", currency.format(subtotal));
            receipt += String.format("Tax (13%%):\t$%s\n", currency.format(tax));
            receipt += String.format("Total:\t\t$%s\n", currency.format(total));
            receipt += "====================================================\n";

            // Write to file
            PrintWriter writer = new PrintWriter(new FileWriter("receipt.txt"));
            writer.println(receipt);
            writer.close();
            
            showAlert(Alert.AlertType.INFORMATION, "Receipt Printed", "The order receipt has been successfully written to 'receipt.txt'.");

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Could not write to receipt file: " + e.getMessage());
        }
    }


    private void handlePrintReceiptPrinter() { //This entire method created to "send to printer"

        handleCalculate();

        VBox printNode = createReceiptNodeForPrinting();
        
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) { // Show the print dialog

            boolean success = job.printPage(printNode);
            if (success) {
                job.endJob();
            }
        }
    }
    
    private VBox createReceiptNodeForPrinting() {
        
        double subtotal = currentOrder.calculateSubtotal();
        double tax = currentOrder.calculateTax(subtotal);
        double total = currentOrder.calculateTotal();
        
        Label title = new Label("SHERIDAN'S BAGEL HOUSE");
        title.setFont(new Font("Monospaced", 16));
        
        Label items = new Label(
            "Bagel: " + getSelectedBagel() + "\n" +
            "Toppings: " + getSelectedToppings() + "\n" +
            "Coffee: " + getSelectedCoffee()
        );
        items.setFont(new Font("Monospaced", 12));
        
        Label totals = new Label(
            String.format("Subtotal:\t$%s\n", currency.format(subtotal)) +
            String.format("Tax (13%%):\t$%s\n", currency.format(tax)) +
            String.format("Total:\t\t$%s\n", currency.format(total))
        );
        totals.setFont(new Font("Monospaced", 12));
        
        VBox receipt = new VBox(5, title, new Separator(), items, new Separator(), totals);
        receipt.setPadding(new Insets(20));
        receipt.setStyle("-fx-border-color: black; -fx-border-width: 1;"); 
        return receipt;
    }
    
    private String getSelectedBagel() {
        if (radWhiteBagel.isSelected()) return "White Bagel";
        if (radWheatBagel.isSelected()) return "Whole Wheat Bagel";
        return "None";
    }
    
    private String getSelectedToppings() {
        StringBuilder toppings = new StringBuilder();
        if (chkCreamCheese.isSelected()) toppings.append("Cream Cheese, ");
        if (chkButter.isSelected()) toppings.append("Butter, ");
        if (chkBlueberry.isSelected()) toppings.append("Blueberry Jam, ");
        if (chkRaspberry.isSelected()) toppings.append("Raspberry Jam, ");
        if (chkPeach.isSelected()) toppings.append("Peach Jelly, ");
        
        String result = toppings.toString();

        return result.isEmpty() ? "None" : result.substring(0, result.length() - 2);
    }
    
    private String getSelectedCoffee() {
        if (radRegCoffee.isSelected()) return "Regular Coffee";
        if (radCappuccino.isSelected()) return "Cappuccino";
        if (radCafeAuLait.isSelected()) return "Café au lait";
        return "None";
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}