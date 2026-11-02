package org.mishu;

import com.formdev.flatlaf.FlatDarkLaf;
import org.mishu.Presentation.MainFrame;

import javax.swing.*;

public class OrdersManagement {
	static void main(String[] args) {
//		FlatDarkLaf.setup();
//		SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));

		Dog dog = new Dog("Rex");

		dog.speak();
		dog.bark();

	}
}


