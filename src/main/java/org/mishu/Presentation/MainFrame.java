package org.mishu.Presentation;

import org.mishu.Business.ClientBLL;
import org.mishu.Business.OrdersBLL;
import org.mishu.Business.ProductBLL;
import org.mishu.Model.Bill;
import org.mishu.Model.Client;
import org.mishu.Model.Orders;
import org.mishu.Model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class MainFrame extends JFrame {

	private final OrdersBLL ordersBLL = new OrdersBLL();
    private final ClientBLL clientBLL = new ClientBLL(ordersBLL);
    private final ProductBLL productBLL = new ProductBLL(ordersBLL);
	private final JTextField clientIdField = new JTextField(5);
	private final JTextField clientNameField = new JTextField(14);
	private final JTextField clientEmailField = new JTextField(14);
	private final JTextField clientAddressField = new JTextField(14);
	private final JTextField productIdField = new JTextField(5);
	private final JTextField productNameField = new JTextField(14);
	private final JTextField productPriceField = new JTextField(8);
	private final JTextField productQuantityField = new JTextField(8);
	private JTable clientTable, productTable, orderTable;
	private DefaultTableModel clientModel, productModel, orderModel;
	private Integer selectedClientId = null;
	private Integer selectedProductId = null;

	/**
	 * main view constructor
	 */
	public MainFrame() {
		setTitle("Order Management System");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initUI();
		loadClients();
		loadProducts();
	}

	/**
	 * initializes the ui
	 */
	private void initUI() {
		JPanel allPanel = new JPanel(new BorderLayout());

		JPanel topBar = new JPanel(new BorderLayout());
		topBar.add(new JLabel("  Order Management"), BorderLayout.WEST);

		JButton refreshButton = new JButton("Refresh All");
		refreshButton.addActionListener(_ -> {
			loadClients();
			loadProducts();
			reloadOrders();
		});
		JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRight.add(refreshButton);
		topBar.add(topRight, BorderLayout.EAST);

		allPanel.add(topBar, BorderLayout.NORTH);

		JSplitPane leftRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildClientPanel(),
				new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildProductPanel(), buildOrderPanel()));
		leftRight.setResizeWeight(0.33);

		JSplitPane inner = (JSplitPane) leftRight.getRightComponent();
		inner.setResizeWeight(0.5);

		allPanel.add(leftRight, BorderLayout.CENTER);
		setContentPane(allPanel);
	}

	/**
	 * builds the panel with the clients
	 * 
	 * @return JPanel with all clients
	 */
	private JPanel buildClientPanel() {
		clientModel = new DefaultTableModel(new String[]{"id", "name", "email", "address"}, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		clientTable = new JTable(clientModel);

		clientTable.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting())
				return;
			int row = clientTable.getSelectedRow();
			if (row < 0)
				return;
			fillFields(clientModel, row, clientIdField, clientNameField, clientEmailField, clientAddressField);
			selectedClientId = parseId(clientModel, row);
			reloadOrders();
		});

		JPanel fields = fieldsRow("Id", clientIdField, "Name", clientNameField, "Email", clientEmailField, "Address",
				clientAddressField);

		JButton addButton = new JButton("Add");
		JButton updateButton = new JButton("Update");
		JButton deleteButton = new JButton("Delete");

		addButton.addActionListener(_ -> {
			try {
				clientBLL.insertClient(new Client(clientNameField.getText(), clientEmailField.getText(),
						clientAddressField.getText()));
				loadClients();
			} catch (Exception ex) {
				showError(ex);
			}
		});

		updateButton.addActionListener(_ -> {
			try {
				int id = Integer.parseInt(clientIdField.getText());
				clientBLL.updateClient(id, clientNameField.getText(), clientEmailField.getText(),
						clientAddressField.getText());
				loadClients();
			} catch (Exception ex) {
				showError(ex);
			}
		});

		deleteButton.addActionListener(_ -> {
			try {
				int id = Integer.parseInt(clientIdField.getText());
				clientBLL.deleteClient(id);
				selectedClientId = null;
				loadClients();
				orderModel.setRowCount(0);
			} catch (Exception ex) {
				showError(ex);
			}
		});

		return buildSectionPanel("Clients", clientTable, fields, buttonRow(addButton, updateButton, deleteButton));
	}

	/**
	 * builds the panel with the products
	 *
	 * @return JPanel with all products
	 */
	private JPanel buildProductPanel() {
		productModel = new DefaultTableModel(new String[]{"id", "name", "price", "quantity"}, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		productTable = new JTable(productModel);

		productTable.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting())
				return;
			int row = productTable.getSelectedRow();
			if (row < 0)
				return;
			fillFields(productModel, row, productIdField, productNameField, productPriceField, productQuantityField);
			selectedProductId = parseId(productModel, row);
		});

		JPanel fields = fieldsRow("Id", productIdField, "Name", productNameField, "Price", productPriceField, "Qty",
				productQuantityField);

		JButton addButton = new JButton("Add");
		JButton updateButton = new JButton("Update");
		JButton deleteButton = new JButton("Delete");

		addButton.addActionListener(_ -> {
			try {
				productBLL.insertProduct(
						new Product(productNameField.getText(), Double.parseDouble(productPriceField.getText()),
								Integer.parseInt(productQuantityField.getText())));
				loadProducts();
			} catch (Exception ex) {
				showError(ex);
			}
		});

		updateButton.addActionListener(_ -> {
			try {
				int id = Integer.parseInt(productIdField.getText());
				productBLL.updateProduct(id, productNameField.getText(),
						Double.parseDouble(productPriceField.getText()),
						Integer.parseInt(productQuantityField.getText()));
				loadProducts();
			} catch (Exception ex) {
				showError(ex);
			}
		});

		deleteButton.addActionListener(_ -> {
			try {
				int id = Integer.parseInt(productIdField.getText());
				productBLL.deleteProduct(id);
				selectedProductId = null;
				loadProducts();
			} catch (Exception ex) {
				showError(ex);
			}
		});

		return buildSectionPanel("Products", productTable, fields, buttonRow(addButton, updateButton, deleteButton));
	}

	/**
	 *
	 * builds the panel with the orders
	 *
	 * @return JPanel with all orders for a client
	 */
	private JPanel buildOrderPanel() {
		orderModel = new DefaultTableModel(new String[]{"id", "clientId", "productId", "quantity"}, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		orderTable = new JTable(orderModel);

		JButton newOrderButton = new JButton("New Order");
		JButton getBillButton = new JButton("Get Bill");
		JButton deleteOrderButton = new JButton("Delete");

		newOrderButton.addActionListener(_ -> openNewOrderFrame());

		getBillButton.addActionListener(_ -> {
			int row = orderTable.getSelectedRow();
			if (row < 0) {
				showMessage("Select an order first.");
				return;
			}
			int orderId = (Integer) orderModel.getValueAt(row, 0);
			int productId = (Integer) orderModel.getValueAt(row, 2);
			int qty = (Integer) orderModel.getValueAt(row, 3);
			showBillDialog(orderId, productId, qty);
		});

		deleteOrderButton.addActionListener(_ -> {
			int row = orderTable.getSelectedRow();
			if (row < 0) {
				showMessage("Select an order to delete.");
				return;
			}
			try {
				int id = (Integer) orderModel.getValueAt(row, 0);
				ordersBLL.deleteOrder(id);
				reloadOrders();
			} catch (Exception ex) {
				showError(ex);
			}
		});

		JLabel orderMessage = new JLabel("Select a client to view orders");
		clientTable.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting())
				return;
			int row = clientTable.getSelectedRow();
			if (row >= 0)
				orderMessage.setText("Orders for: " + clientModel.getValueAt(row, 1));
		});

		JPanel topRow = new JPanel(new BorderLayout());
		topRow.add(orderMessage, BorderLayout.WEST);

		return buildSectionPanel("Orders", orderTable, topRow,
				buttonRow(newOrderButton, getBillButton, deleteOrderButton));
	}

	/**
	 * <p>
	 * method for opening a new JFrame that will create and insert an order into the
	 * table for a customer and a product that were selected in advance
	 * </p>
	 */
	private void openNewOrderFrame() {
		if (selectedClientId == null) {
			showMessage("Please select a client.");
			return;
		}

		if (selectedProductId == null) {
			showMessage("Please select a product.");
			return;
		}

		JFrame frame = new JFrame("Create New Order");
		frame.setSize(300, 200);
		frame.setLocationRelativeTo(this);
		frame.setResizable(false);

		JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

		JTextField qtyField = new JTextField("1");

		panel.add(new JLabel("Client ID:"));
		panel.add(new JLabel(String.valueOf(selectedClientId)));

		panel.add(new JLabel("Product ID:"));
		panel.add(new JLabel(String.valueOf(selectedProductId)));

		panel.add(new JLabel("Quantity:"));
		panel.add(qtyField);

		JButton confirm = new JButton("Create Order");

		confirm.addActionListener(_ -> {
			try {
				int qty = Integer.parseInt(qtyField.getText());

				ordersBLL.insertOrder(new Orders(selectedClientId, selectedProductId, qty));

				showMessage("Order created successfully!");

				frame.dispose();

				reloadOrders();

			} catch (Exception ex) {
				showError(ex);
			}
		});

		panel.add(new JLabel());
		panel.add(confirm);

		frame.setContentPane(panel);
		frame.setVisible(true);
	}

	/**
	 * shows the bill
	 *
	 * @param orderId
	 *            of the order
	 * @param productId
	 *            that was bought
	 * @param qty
	 *            quantity bought
	 */
	private void showBillDialog(int orderId, int productId, int qty) {
		try {
			Product product = productBLL.findProductById(productId);
			double unitPrice = product.getPrice();
			double total = unitPrice * qty;

			String billText = String.format("""
					Order ID   : %d
					Product    : %s
					Unit price : $%.2f
					Quantity   : %d
					----------------------------
					TOTAL      : $%.2f""", orderId, product.getName(), unitPrice, qty, total);

			JTextArea area = new JTextArea(billText);
			area.setFont(new Font("Monospaced", Font.PLAIN, 13));
			area.setEditable(false);

			JDialog dlg = new JDialog(this, "Bill - Order #" + orderId, true);
			dlg.add(new JScrollPane(area));
			dlg.pack();
			dlg.setLocationRelativeTo(this);
			dlg.setVisible(true);

		} catch (Exception ex) {
			showError(ex);
		}
	}

	/**
	 * load clients into the clients table
	 */
	private void loadClients() {
		populateTable(clientModel, clientBLL.findAll());
	}

	/**
	 * load products into the products table
	 */
	private void loadProducts() {
		populateTable(productModel, productBLL.findAll());
	}

	/**
	 * refresh button in case something does not update also loads/reloads the
	 * orders of a selected client
	 */
	private void reloadOrders() {
		if (selectedClientId == null)
			return;
		orderModel.setRowCount(0);
		ordersBLL.findAll().stream().filter(o -> o.getClientId() == selectedClientId)
				.forEach(o -> orderModel.addRow(reflectRow(o, "id", "clientId", "productId", "quantity")));
	}

	/**
	 * Populates a table model with the values of the given items using reflection
	 *
	 * @param model
	 *            the table model to be filled
	 * @param items
	 *            the list of items to be reflected and added to the table
	 */
	private <T> void populateTable(DefaultTableModel model, List<T> items) {
		if (items.isEmpty()) {
			model.setRowCount(0);
			return;
		}

		Field[] fields = items.getFirst().getClass().getDeclaredFields();

		String[] headers = Arrays.stream(fields).map(Field::getName).toArray(String[]::new);

		model.setColumnIdentifiers(headers);
		model.setRowCount(0);

		for (T item : items) {
			Object[] row = new Object[fields.length];
			for (int i = 0; i < fields.length; i++) {
				try {
					fields[i].setAccessible(true);
					row[i] = fields[i].get(item);
				} catch (IllegalAccessException ex) {
					row[i] = "?";
				}
			}
			model.addRow(row);
		}
	}

	/**
	 * Extracts the values of the specified fields from an object using reflection
	 * and stores them in an array representing a table row.
	 *
	 * @param obj
	 *            the object from which the field values are extracted
	 * @param fieldNames
	 *            the names of the fields to be reflected
	 * @return an array containing the values of the specified fields
	 */
	private Object[] reflectRow(Object obj, String... fieldNames) {
		Object[] row = new Object[fieldNames.length];

		for (int i = 0; i < fieldNames.length; i++) {
			try {
				Field f = findField(obj.getClass(), fieldNames[i]);
				f.setAccessible(true);
				row[i] = f.get(obj);
			} catch (Exception ex) {
				row[i] = "?";
			}
		}

		return row;
	}

	/**
	 * Searches for a field with the given name in the specified class or in one of
	 * its superclasses.
	 *
	 * @param clazz
	 *            the class where the search starts
	 * @param name
	 *            the name of the field to find
	 * @return the field with the given name
	 * @throws NoSuchFieldException
	 *             if the field cannot be found in the class hierarchy
	 */
	private Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
		while (clazz != null) {
			try {
				return clazz.getDeclaredField(name);
			} catch (NoSuchFieldException ignored) {
				clazz = clazz.getSuperclass();
			}
		}
		throw new NoSuchFieldException(name);
	}

	/**
	 * Fills the given text fields with values from a selected table row.
	 *
	 * @param model
	 *            the table model from which the values are read
	 * @param row
	 *            the selected row index
	 * @param fields
	 *            the text fields to be filled
	 */
	private void fillFields(DefaultTableModel model, int row, JTextField... fields) {
		for (int i = 0; i < fields.length && i < model.getColumnCount(); i++) {
			Object val = model.getValueAt(row, i);
			fields[i].setText(val != null ? val.toString() : "");
		}
	}

	/**
	 * Parses the id value from the first column of the selected table row.
	 *
	 * @param model
	 *            the table model from which the id is read
	 * @param row
	 *            the selected row index
	 * @return the parsed id value
	 */
	private int parseId(DefaultTableModel model, int row) {
		return Integer.parseInt(model.getValueAt(row, 0).toString());
	}

	/**
	 * Builds a panel containing a titled table section and its control panels.
	 *
	 * @param title
	 *            the title displayed around the section
	 * @param table
	 *            the table displayed in the center of the section
	 * @param middle
	 *            the panel placed above the bottom panel
	 * @param bottom
	 *            the panel placed at the bottom of the section
	 * @return the constructed section panel
	 */
	private JPanel buildSectionPanel(String title, JTable table, JPanel middle, JPanel bottom) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder(title));

		JPanel south = new JPanel(new BorderLayout());
		south.add(middle, BorderLayout.NORTH);
		south.add(bottom, BorderLayout.SOUTH);

		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		panel.add(south, BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Builds a row panel containing labels and text fields.
	 *
	 * @param args
	 *            pairs of label names and text fields
	 * @return the constructed fields row panel
	 */
	private JPanel fieldsRow(Object... args) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
		for (int i = 0; i < args.length - 1; i += 2) {
			p.add(new JLabel(args[i].toString() + ":"));
			p.add((JTextField) args[i + 1]);
		}
		return p;
	}

	/**
	 * Builds a row panel containing the given buttons.
	 *
	 * @param buttons
	 *            the buttons to be added to the row
	 * @return the constructed button row panel
	 */
	private JPanel buttonRow(JButton... buttons) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
		for (JButton b : buttons) {
			p.add(b);
		}
		return p;
	}

	/**
	 * Displays an error dialog containing the exception message.
	 *
	 * @param e
	 *            the exception whose message is displayed
	 */
	private void showError(Exception e) {
		JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Displays a message dialog containing the given message.
	 *
	 * @param message
	 *            the message to be displayed
	 */
	private void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}
}