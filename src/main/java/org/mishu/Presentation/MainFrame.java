package org.mishu.Presentation;

import org.mishu.Business.ClientBLL;
import org.mishu.Business.OrdersBLL;
import org.mishu.Business.ProductBLL;
import org.mishu.Model.Client;
import org.mishu.Model.Orders;
import org.mishu.Model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class MainFrame extends JFrame {

	private static final Color BG = new Color(0x1e, 0x1e, 0x2e);
	private static final Color SURFACE = new Color(0x28, 0x28, 0x3c);
	private static final Color HEADER_BG = new Color(0x18, 0x18, 0x28);
	private static final Color ACCENT = new Color(0x89, 0xb4, 0xfa);
	private static final Color GREEN = new Color(0x40, 0xa0, 0x70);
	private static final Color ORANGE = new Color(0xe0, 0x9a, 0x3e);
	private static final Color RED = new Color(0xc0, 0x50, 0x50);
	private static final Color TEXT = new Color(0xcd, 0xd6, 0xf4);
	private static final Color MUTED = new Color(0x7f, 0x84, 0x9c);
	private static final Color TABLE_HEADER_BG = new Color(0x31, 0x32, 0x44);
	private static final Color TABLE_ALT = new Color(0x24, 0x24, 0x38);
	private static final Color BORDER = new Color(0x45, 0x47, 0x5a);

	private final OrdersBLL ordersBLL = new OrdersBLL();
	private final ClientBLL clientBLL = new ClientBLL(ordersBLL);
	private final ProductBLL productBLL = new ProductBLL(ordersBLL);

	private final JTextField clientIdField = styledField(5);
	private final JTextField clientNameField = styledField(15);
	private final JTextField clientEmailField = styledField(15);
	private final JTextField clientAddressField = styledField(15);
	private final JTextField productIdField = styledField(5);
	private final JTextField productNameField = styledField(15);
	private final JTextField productPriceField = styledField(10);
	private final JTextField productQuantityField = styledField(10);

	private JTable clientTable, productTable, orderTable;
	private DefaultTableModel clientModel, productModel, orderModel;
	private Integer selectedClientId = null;
	private Integer selectedProductId = null;
	private JLabel statusLabel;

	public MainFrame() {
		setTitle("Order Management System");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(BG);
		initUI();
		loadClients();
		loadProducts();
	}

	private void initUI() {
		setLayout(new BorderLayout());
		add(buildHeader(), BorderLayout.NORTH);

		JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				buildClientPanel(), buildProductPanel());
		topSplit.setResizeWeight(0.5);
		topSplit.setBorder(null);
		topSplit.setBackground(BG);

		JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				topSplit, buildOrderPanel());
		mainSplit.setResizeWeight(0.6);
		mainSplit.setBorder(null);
		mainSplit.setBackground(BG);

		add(mainSplit, BorderLayout.CENTER);
		add(buildStatusBar(), BorderLayout.SOUTH);
	}

	private JPanel buildHeader() {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(HEADER_BG);
		header.setBorder(new EmptyBorder(14, 20, 14, 20));

		JLabel title = new JLabel("Order Management System");
		title.setFont(new Font("Segoe UI", Font.BOLD, 22));
		title.setForeground(TEXT);

		JButton refresh = accentButton("  Refresh All", ACCENT);
		refresh.addActionListener(_ -> {
			loadClients();
			loadProducts();
			reloadOrders();
			setStatus("Refreshed", false);
		});

		header.add(title, BorderLayout.WEST);
		header.add(refresh, BorderLayout.EAST);
		return header;
	}

	private JPanel buildStatusBar() {
		JPanel bar = new JPanel(new BorderLayout());
		bar.setBackground(HEADER_BG);
		bar.setBorder(new EmptyBorder(6, 20, 6, 20));
		statusLabel = new JLabel("Ready");
		statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		statusLabel.setForeground(MUTED);
		bar.add(statusLabel, BorderLayout.WEST);
		return bar;
	}

	private JPanel buildClientPanel() {
		clientModel = nonEditableModel();
		clientTable = styledTable(clientModel);

		clientTable.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) return;
			int row = clientTable.getSelectedRow();
			if (row < 0) return;
			fillFields(clientModel, row, clientIdField, clientNameField, clientEmailField, clientAddressField);
			selectedClientId = parseId(clientModel, row);
			reloadOrders();
		});

		JButton add = accentButton("+ Add", GREEN);
		JButton update = accentButton("Edit", ORANGE);
		JButton delete = accentButton("Delete", RED);

		add.addActionListener(_ -> {
			try {
				clientBLL.insertClient(new Client(clientNameField.getText(),
						clientEmailField.getText(), clientAddressField.getText()));
				loadClients();
				setStatus("Client added", false);
			} catch (Exception ex) {
				showError(ex);
			}
		});
		update.addActionListener(_ -> {
			try {
				int id = Integer.parseInt(clientIdField.getText());
				clientBLL.updateClient(id, clientNameField.getText(),
						clientEmailField.getText(), clientAddressField.getText());
				loadClients();
				setStatus("Client updated", false);
			} catch (Exception ex) {
				showError(ex);
			}
		});
		delete.addActionListener(_ -> {
			try {
				int id = Integer.parseInt(clientIdField.getText());
				clientBLL.deleteClient(id);
				selectedClientId = null;
				loadClients();
				orderModel.setRowCount(0);
				setStatus("Client deleted", false);
			} catch (Exception ex) {
				showError(ex);
			}
		});

		return buildSection("Clients",
				clientTable,
				fieldsRow("ID", clientIdField, "Name", clientNameField,
						"Email", clientEmailField, "Address", clientAddressField),
				buttonRow(add, update, delete));
	}

	private JPanel buildProductPanel() {
		productModel = nonEditableModel();
		productTable = styledTable(productModel);

		productTable.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) return;
			int row = productTable.getSelectedRow();
			if (row < 0) return;
			fillFields(productModel, row, productIdField, productNameField,
					productPriceField, productQuantityField);
			selectedProductId = parseId(productModel, row);
		});

		JButton add = accentButton("+ Add", GREEN);
		JButton update = accentButton("Edit", ORANGE);
		JButton delete = accentButton("Delete", RED);

		add.addActionListener(_ -> {
			try {
				productBLL.insertProduct(new Product(productNameField.getText(),
						Double.parseDouble(productPriceField.getText()),
						Integer.parseInt(productQuantityField.getText())));
				loadProducts();
				setStatus("Product added", false);
			} catch (Exception ex) {
				showError(ex);
			}
		});
		update.addActionListener(_ -> {
			try {
				int id = Integer.parseInt(productIdField.getText());
				productBLL.updateProduct(id, productNameField.getText(),
						Double.parseDouble(productPriceField.getText()),
						Integer.parseInt(productQuantityField.getText()));
				loadProducts();
				setStatus("Product updated", false);
			} catch (Exception ex) {
				showError(ex);
			}
		});
		delete.addActionListener(_ -> {
			try {
				int id = Integer.parseInt(productIdField.getText());
				productBLL.deleteProduct(id);
				selectedProductId = null;
				loadProducts();
				setStatus("Product deleted", false);
			} catch (Exception ex) {
				showError(ex);
			}
		});

		return buildSection("Products",
				productTable,
				fieldsRow("ID", productIdField, "Name", productNameField,
						"Price", productPriceField, "Qty", productQuantityField),
				buttonRow(add, update, delete));
	}

	private JPanel buildOrderPanel() {
		orderModel = nonEditableModel();
		orderTable = styledTable(orderModel);

		JButton newOrder = accentButton("+ New Order", GREEN);
		JButton getBill = accentButton("$ Bill", ACCENT);
		JButton deleteOrder = accentButton("Delete", RED);

		newOrder.addActionListener(_ -> openNewOrderFrame());

		getBill.addActionListener(_ -> {
			int row = orderTable.getSelectedRow();
			if (row < 0) {
				showMessage("Select an order first.");
				return;
			}
			showBillDialog(
					(Integer) orderModel.getValueAt(row, 0),
					(Integer) orderModel.getValueAt(row, 2),
					(Integer) orderModel.getValueAt(row, 3));
		});

		deleteOrder.addActionListener(_ -> {
			int row = orderTable.getSelectedRow();
			if (row < 0) {
				showMessage("Select an order to delete.");
				return;
			}
			try {
				ordersBLL.deleteOrder((Integer) orderModel.getValueAt(row, 0));
				reloadOrders();
				setStatus("Order deleted", false);
			} catch (Exception ex) {
				showError(ex);
			}
		});

		JLabel hint = new JLabel("  Select a client to view their orders");
		hint.setForeground(MUTED);
		hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		clientTable.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) return;
			int row = clientTable.getSelectedRow();
			if (row >= 0)
				hint.setText("  Orders for:  " + clientModel.getValueAt(row, 1));
		});

		JPanel hintPanel = new JPanel(new BorderLayout());
		hintPanel.setOpaque(false);
		hintPanel.add(hint, BorderLayout.WEST);

		return buildSection("Orders", orderTable, hintPanel, buttonRow(newOrder, getBill, deleteOrder));
	}

	// ── component builders ──────────────────────────────────────────────────

	private JPanel buildSection(String title, JTable table, JPanel middle, JPanel bottom) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(BG);
		panel.setBorder(BorderFactory.createLineBorder(BORDER));

		JLabel lbl = new JLabel("  " + title.toUpperCase());
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lbl.setForeground(ACCENT);
		lbl.setBorder(new EmptyBorder(10, 10, 10, 10));
		lbl.setOpaque(true);
		lbl.setBackground(TABLE_HEADER_BG);

		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER));
		scroll.getViewport().setBackground(BG);

		JPanel south = new JPanel(new BorderLayout());
		south.setBackground(SURFACE);
		middle.setBackground(SURFACE);
		bottom.setBackground(SURFACE);
		south.add(middle, BorderLayout.CENTER);
		south.add(bottom, BorderLayout.SOUTH);

		panel.add(lbl, BorderLayout.NORTH);
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(south, BorderLayout.SOUTH);
		return panel;
	}

	private DefaultTableModel nonEditableModel() {
		return new DefaultTableModel(new String[0], 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
	}

	private JTable styledTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setBackground(BG);
		table.setForeground(TEXT);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setRowHeight(30);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setSelectionBackground(new Color(0x45, 0x60, 0x90));
		table.setSelectionForeground(Color.WHITE);
		table.setFillsViewportHeight(true);

		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(
					JTable t, Object value, boolean sel, boolean focus, int row, int col) {
				super.getTableCellRendererComponent(t, value, sel, focus, row, col);
				setBackground(sel ? new Color(0x45, 0x60, 0x90) : (row % 2 == 0 ? BG : TABLE_ALT));
				setForeground(sel ? Color.WHITE : TEXT);
				setBorder(new EmptyBorder(0, 10, 0, 10));
				return this;
			}
		});

		JTableHeader header = table.getTableHeader();
		header.setBackground(TABLE_HEADER_BG);
		header.setForeground(MUTED);
		header.setFont(new Font("Segoe UI", Font.BOLD, 12));
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
		header.setReorderingAllowed(false);

		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(
					JTable t, Object value, boolean sel, boolean focus, int row, int col) {
				super.getTableCellRendererComponent(t, value, sel, focus, row, col);
				setBackground(TABLE_HEADER_BG);
				setForeground(MUTED);
				setBorder(new EmptyBorder(6, 10, 6, 10));
				setFont(new Font("Segoe UI", Font.BOLD, 12));
				return this;
			}
		};
		table.getTableHeader().setDefaultRenderer(headerRenderer);

		return table;
	}

	private JTextField styledField(int cols) {
		JTextField f = new JTextField(cols);
		f.setBackground(new Color(0x31, 0x32, 0x44));
		f.setForeground(TEXT);
		f.setCaretColor(TEXT);
		f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		f.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER),
				new EmptyBorder(5, 8, 5, 8)));
		return f;
	}

	private JButton accentButton(String text, Color color) {
		JButton btn = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				Color base = getModel().isPressed() ? color.darker().darker()
						: getModel().isRollover() ? color.brighter() : color;
				g2.setColor(base);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btn.setForeground(Color.WHITE);
		btn.setOpaque(false);
		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setBorder(new EmptyBorder(8, 18, 8, 18));
		return btn;
	}

	private JPanel fieldsRow(Object... args) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
		p.setOpaque(false);
		for (int i = 0; i < args.length - 1; i += 2) {
			JLabel lbl = new JLabel(args[i].toString() + ":");
			lbl.setForeground(MUTED);
			lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			p.add(lbl);
			p.add((JTextField) args[i + 1]);
		}
		return p;
	}

	private JPanel buttonRow(JButton... buttons) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
		p.setOpaque(false);
		for (JButton b : buttons) p.add(b);
		return p;
	}

	private void setStatus(String msg, boolean error) {
		statusLabel.setText(msg);
		statusLabel.setForeground(error ? RED : GREEN);
	}

	// ── data ────────────────────────────────────────────────────────────────

	private void loadClients() {
		populateTable(clientModel, clientBLL.findAll());
	}

	private void loadProducts() {
		populateTable(productModel, productBLL.findAll());
	}

	private void reloadOrders() {
		if (selectedClientId == null) return;
		orderModel.setRowCount(0);
		ordersBLL.findAll().stream()
				.filter(o -> o.getClientId() == selectedClientId)
				.forEach(o -> orderModel.addRow(reflectRow(o, "id", "clientId", "productId", "quantity")));
	}

	private <T> void populateTable(DefaultTableModel model, List<T> items) {
		if (items.isEmpty()) {
			model.setRowCount(0);
			return;
		}
		Field[] fields = items.getFirst().getClass().getDeclaredFields();
		model.setColumnIdentifiers(Arrays.stream(fields).map(Field::getName).toArray(String[]::new));
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

	private void fillFields(DefaultTableModel model, int row, JTextField... fields) {
		for (int i = 0; i < fields.length && i < model.getColumnCount(); i++) {
			Object val = model.getValueAt(row, i);
			fields[i].setText(val != null ? val.toString() : "");
		}
	}

	private int parseId(DefaultTableModel model, int row) {
		return Integer.parseInt(model.getValueAt(row, 0).toString());
	}

	// ── dialogs ─────────────────────────────────────────────────────────────

	private void openNewOrderFrame() {
		if (selectedClientId == null) {
			showMessage("Please select a client.");
			return;
		}
		if (selectedProductId == null) {
			showMessage("Please select a product.");
			return;
		}

		JDialog dlg = new JDialog(this, "New Order", true);
		dlg.setSize(340, 240);
		dlg.setLocationRelativeTo(this);
		dlg.setResizable(false);

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(SURFACE);
		panel.setBorder(new EmptyBorder(20, 28, 20, 28));
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(7, 6, 7, 6);
		c.anchor = GridBagConstraints.WEST;

		JTextField qtyField = styledField(10);
		qtyField.setText("1");

		addDialogRow(panel, c, 0, "Client ID:", selectedClientId.toString());
		addDialogRow(panel, c, 1, "Product ID:", selectedProductId.toString());

		JLabel qtyLbl = new JLabel("Quantity:");
		qtyLbl.setForeground(MUTED);
		qtyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		c.gridx = 0; c.gridy = 2; c.gridwidth = 1;
		panel.add(qtyLbl, c);
		c.gridx = 1;
		panel.add(qtyField, c);

		JButton confirm = accentButton("Create Order", GREEN);
		confirm.addActionListener(_ -> {
			try {
				ordersBLL.insertOrder(new Orders(selectedClientId, selectedProductId,
						Integer.parseInt(qtyField.getText())));
				dlg.dispose();
				reloadOrders();
				setStatus("Order created", false);
			} catch (Exception ex) {
				showError(ex);
			}
		});

		c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(16, 6, 0, 6);
		panel.add(confirm, c);

		dlg.setContentPane(panel);
		dlg.setVisible(true);
	}

	private void addDialogRow(JPanel panel, GridBagConstraints c, int row, String label, String value) {
		JLabel lbl = new JLabel(label);
		lbl.setForeground(MUTED);
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		JLabel val = new JLabel(value);
		val.setForeground(TEXT);
		val.setFont(new Font("Segoe UI", Font.BOLD, 13));

		c.gridx = 0; c.gridy = row; c.gridwidth = 1;
		panel.add(lbl, c);
		c.gridx = 1;
		panel.add(val, c);
	}

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
			area.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
			area.setBackground(TABLE_ALT);
			area.setForeground(TEXT);
			area.setEditable(false);
			area.setBorder(new EmptyBorder(16, 20, 16, 20));

			JDialog dlg = new JDialog(this, "Bill — Order #" + orderId, true);
			dlg.add(new JScrollPane(area));
			dlg.pack();
			dlg.setLocationRelativeTo(this);
			dlg.setVisible(true);
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private void showError(Exception e) {
		JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		setStatus("Error: " + e.getMessage(), true);
	}

	private void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}
}
