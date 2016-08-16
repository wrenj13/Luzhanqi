package display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import network.WaitNotifyChat;

/**
 * A class to represent a chat box and input line.
 * Much help from http://docs.oracle.com/javase/tutorial/uiswing/components/textarea.html.
 * 	
 * @author Ren-Jay
 *
 */
public class ChatComponent extends JScrollPane {
	
	private JTextPane textArea;
	private JTextField input;
	private String username;
	SimpleAttributeSet set;
	WaitNotifyChat syncChat;
	
	public ChatComponent(WaitNotifyChat newChat) {
		this("Anon", newChat);
	}
	
	public ChatComponent(String user, WaitNotifyChat newChat) {
		username = user;
		syncChat = newChat;
		set = new SimpleAttributeSet();
		StyleConstants.setFontSize(set, 16);
		// Set up chat box
		textArea = new JTextPane();
		textArea.setText("");
		textArea.setEditable(false);
		// Set up input line
		input = new JTextField(20);
		class InputListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
		        String text = input.getText();
		        addString(text, username);
		        input.setText("");
		        // note we dont add a new line because the client thread will do that
		        syncChat.setMessage(username + ": " + text);
		        syncChat.doNotify();
			}
		}
		input.addActionListener(new InputListener());
		// put them together
		JPanel chatPanel = new JPanel(new BorderLayout());
		chatPanel.add(textArea, BorderLayout.CENTER);
		chatPanel.add(input, BorderLayout.PAGE_END);
		
		setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setViewportView(chatPanel);
		setPreferredSize(new Dimension(300, 0));
	}

	public void addString(String newString, String username) {
	    StyleConstants.setBold(set, true);
	    Document doc = textArea.getStyledDocument();
	    try {
			doc.insertString(doc.getLength(), username + ": ", set);
			 StyleConstants.setBold(set, false);
			doc.insertString(doc.getLength(), newString + "\n", set);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}
