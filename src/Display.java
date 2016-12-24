

import javax.swing.*;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.*;

/*  Easy to use utility for displaying and changing panels in a JFrame
 *  Contains only one main panel, and overlays other panels on top of main panel.
 *  This limit allows for simpler use, and more efficient menu loading.
 */

public class Display {
	private JFrame frame;
	private JPanel mainPanel;
	private Stack<JPanel> overlayPanels;
	private JLayeredPane overlay;
	private Stack<JPanel> menuPanels;
	private boolean inMenuMode;
	private int width = 1000;
	private int height = 1000;
	private static final double REFRESH_RATE = 60;
	private static final long SLEEP_MILLIS = (long) (1000 / REFRESH_RATE);
	
	
	public Display(int width, int height, String name, JPanel mainPanel) {
		this.width = width;
		this.height = height;
		
		createFrame(name);
		createOverlay();
		
		this.mainPanel = mainPanel;
		overlay.add(mainPanel, 0);
		inMap = overlay.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		actMap = overlay.getActionMap();
		
		overlayPanels = new Stack<JPanel>();
		menuPanels = new Stack<JPanel>();
		inMenuMode = false;
		createRefresh();
	}

	private void createFrame(String name) {
		frame = new JFrame(name);
		frame.setBounds(0, 0, width + 16, height + 39);
		//frame.getContentPane().setSize(1000, 1000);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		//frame.setUndecorated(true);
		frame.setVisible(true);
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void createOverlay() {
		overlay = new JLayeredPane();
		overlay.setVisible(true);
		overlay.setLayout(null);
		overlay.setSize(width, height);
		frame.getContentPane().add(overlay);
	}
	
	private void createRefresh() {
		Thread t = new Thread() {
			@Override
			public void run() {
				while (true) {
					frame.getContentPane().revalidate();
					frame.getContentPane().repaint();
					try {
						sleep(SLEEP_MILLIS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
	}
	
	public void addOverlay(JPanel panel) {
		overlayPanels.push(panel);
		overlay.add(panel, overlayPanels.size());
		overlay.moveToFront(panel);
		System.out.println(overlay.getIndexOf(panel) + "a");
	}
	
	public void removeTopOverlay() {
		JPanel trash = overlayPanels.pop();
		overlay.remove(trash);
	}
	
	public void replaceMainPanel(JPanel panel) {
		overlay.add(panel);
		int index = overlay.getIndexOf(mainPanel);
		overlay.remove(index);
		mainPanel = panel;
		restackPanels();
	}
	
	public void switchToMainPanel() {
		if (inMenuMode) {
			inMenuMode = false;
			overlay.setVisible(true);
		}
	}
	
	public void switchToMenu(JPanel menu) {
		if (!inMenuMode) {
			inMenuMode = true;
			overlay.setVisible(false);
		} else {
			frame.getContentPane().remove(menuPanels.peek());
		}
		menuPanels.push(menu);
		frame.getContentPane().add(menu);
	}
	
	public void backMenu() {
		if (inMenuMode) {
			frame.getContentPane().remove(menuPanels.pop());
			if (menuPanels.size() != 0) {
				frame.getContentPane().add(menuPanels.peek());
			} else {
				switchToMainPanel();
			}
		} else {
			System.out.println("Not in Menu!");
		}
	}
	
	private void restackPanels() {
		for (JPanel p : overlayPanels) {
			overlay.remove(p);
		}
		overlay.remove(mainPanel);
		
		for (JPanel p : overlayPanels) {
			overlay.add(p);
			//System.out.println("index of overlay = " + overlay.getIndexOf(p));
		}
		overlay.add(mainPanel);
		//System.out.println("index of main = " + overlay.getIndexOf(mainPanel));
	}
	private InputMap inMap;
	private ActionMap actMap;
	public void addAction(Action action, int key, boolean onRelease) {
		String tag = "tag: " + key + onRelease;
		inMap.put(KeyStroke.getKeyStroke(key, 0, onRelease), tag);
		actMap.put(tag, action);
	}
}
