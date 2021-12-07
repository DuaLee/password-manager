package ui;

import encryptor.MasterEncryptor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.security.GeneralSecurityException;

/**
 * User screen after user logs in successfully
 * Displays login information entries and options to add, modify, and delete entries
 */
public class UserScreen {

    private JTable entryTable;
    private JPanel userPanel = new JPanel();
    private JTextField entryName;
    private JTextField username;
    private JTextField password;
    private JButton confirmButton;
    private JRadioButton addModifyRadioButton;
    private JRadioButton deleteRadioButton;
    private JLabel statusLabel;
    private DefaultTableModel tableData = new DefaultTableModel();

    private static String title;
    private static String key;

    // determining mouse position for right click menu
    private static int rowIndex;
    private static int columnIndex;

    private final ImageIcon UNLOCK_ICON = new ImageIcon("./img/unlock.png");

    /**
     * Create an instance of UserScreen with passed-in username and password
     *
     * @param title username
     * @param key password
     */
    public UserScreen(String title, String key) {
        this.title = title;
        this.key = key;

        // UI theme to match system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        $$$setupUI$$$();

        // attempt to parse decrypted file
        try {
            populateTable();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        ///// right click menu /////

        JPopupMenu popup = new JPopupMenu();
        JMenuItem copy = new JMenuItem("Copy");
        popup.add(copy);

        copy.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(String.valueOf(entryTable
                    .getModel().getValueAt(rowIndex, columnIndex)));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });

        entryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 3) {
                    rowIndex = entryTable.rowAtPoint(e.getPoint());
                    columnIndex = entryTable.columnAtPoint(e.getPoint());

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });

        //////////

        // change mode to add/modify entry
        addModifyRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username.setEnabled(true);
                username.setText("");
                password.setEnabled(true);
                password.setText("");
            }
        });

        // change mode to delete entry
        deleteRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username.setEnabled(false);
                username.setText("");
                password.setEnabled(false);
                password.setText("");
            }
        });

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (deleteRadioButton.isSelected()) {
                        if (!entryName.getText().isEmpty()) {
                            if (removeEntry(entryName.getText())) {
                                statusLabel.setForeground(Color.black);
                                statusLabel.setText(entryName.getText() + " removed.");
                            } else {
                                statusLabel.setForeground(Color.red);
                                statusLabel.setText(entryName.getText() + " not found.");
                            }
                        } else {
                            statusLabel.setForeground(Color.red);
                            statusLabel.setText("Fields cannot be blank.");
                        }
                    } else if (addModifyRadioButton.isSelected()) {
                        if (
                                !entryName.getText().isEmpty()
                                        && !username.getText().isEmpty()
                                        && !password.getText().isEmpty()
                        ) {
                            if (addEntry(entryName.getText(), username.getText(), password.getText())) {
                                statusLabel.setText(entryName.getText() + " modified.");
                            } else {
                                statusLabel.setText(entryName.getText() + " inserted.");
                            }
                        } else {
                            statusLabel.setForeground(Color.red);
                            statusLabel.setText("Fields cannot be blank.");
                        }
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                try {
                    populateTable();
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }
        });

    }

    /**
     * Initialization for UserScreen JFrame and its parameters
     *
     * @throws FileNotFoundException
     */
    public void create() throws FileNotFoundException {
        JFrame frame = new JFrame(title);
        frame.setContentPane(new UserScreen(frame.getTitle(), key).userPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to log out?", "Logout?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    try {
                        exitAction();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.setIconImage(UNLOCK_ICON.getImage());
    }

    public static void main(String[] args) {
        //FOR TESTING ONLY

        JFrame frame = new JFrame();
        frame.setContentPane(new UserScreen(frame.getTitle(), key).userPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Determines what to do if the user confirms logout dialog
     *
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void exitAction() throws IOException, GeneralSecurityException {
        MasterEncryptor encryptor = new MasterEncryptor();
        encryptor.encryptFile(title, key);

        System.exit(0);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        userPanel = new JPanel();
        userPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        userPanel.setBorder(BorderFactory.createTitledBorder(null, "User", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(30);
        scrollPane1.setVerticalScrollBarPolicy(20);
        userPanel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        entryTable.setAutoCreateRowSorter(false);
        entryTable.setDoubleBuffered(false);
        entryTable.setFillsViewportHeight(false);
        entryTable.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
        entryTable.putClientProperty("Table.isFileList", Boolean.FALSE);
        scrollPane1.setViewportView(entryTable);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        userPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(null, "Query", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        confirmButton = new JButton();
        confirmButton.setText("Confirm");
        panel1.add(confirmButton, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(null, "Entry Name", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        entryName = new JTextField();
        panel2.add(entryName, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(null, "Username", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        username = new JTextField();
        panel3.add(username, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(null, "Password", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        password = new JTextField();
        panel4.add(password, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        deleteRadioButton = new JRadioButton();
        deleteRadioButton.setText("Delete");
        panel1.add(deleteRadioButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addModifyRadioButton = new JRadioButton();
        addModifyRadioButton.setSelected(true);
        addModifyRadioButton.setText("Add/Modify");
        panel1.add(addModifyRadioButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(361, 20), null, 0, false));
        statusLabel = new JLabel();
        statusLabel.setText("Please enter a query.");
        userPanel.add(statusLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(addModifyRadioButton);
        buttonGroup.add(deleteRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return userPanel;
    }

    /**
     * Initialize the empty table with column headers
     */
    private void createUIComponents() {
        entryTable = new JTable(tableData);
        tableData.setColumnIdentifiers(new String[]{"Entry Name", "Username", "Password"});

//        tableData.addRow(new String[]{"Google.com", "alice", "ilovebob"});
//        tableData.addRow(new String[]{"Yahoo.com", "alice2", "ilovebob2"});
//        tableData.addRow(new String[]{"Chase.com", "alice3", "ilovebob3"});
//        tableData.addRow(new String[]{"Facebook.com", "alice4", "ilovebob4"});
    }

    /**
     * Search for given entry name and remove all matching entries if found
     *
     * @param entryName
     * @return true if entry exists
     * @throws IOException
     */
    private Boolean removeEntry(String entryName) throws IOException {
        Boolean found = false;

        File inputFile = new File(title);
        File tempFile = new File("temp");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
            String line = currentLine;
            String[] lineTemp = line.split(",");

            if (lineTemp[0].equals(entryName)) {
                found = true;
                continue;
            }

            writer.write(currentLine + System.getProperty("line.separator"));
        }

        writer.close();
        reader.close();

        // if entry is not found, it is not necessary to overwrite the original
        if (found) {
            if (inputFile.exists()) {
                inputFile.delete();
            }

            tempFile.renameTo(inputFile);
        }

        if (tempFile.exists()) {
            tempFile.delete();
        }

        return found;
    }

    /**
     * Add an entry if given entry name does not already exist, modify existing entry if found
     *
     * @param entryName
     * @param username
     * @param password
     * @return true if entry exists
     * @throws IOException
     */
    private Boolean addEntry(String entryName, String username, String password) throws IOException {
        Boolean found = false;

        if (removeEntry(entryName)) {
            found = true;
        }

        FileWriter writer = new FileWriter(title, true);
        writer.write(entryName + "," + username + "," + password + System.getProperty("line.separator"));

        writer.close();

        return found;
    }

    /**
     * Populates the JTable with parsed entries from the decrypted file
     *
     * @throws FileNotFoundException
     */
    private void populateTable() throws FileNotFoundException {
        DefaultTableModel model = (DefaultTableModel) entryTable.getModel();
        model.setRowCount(0);

        FileReader file = new FileReader(title);
        try (BufferedReader br = new BufferedReader(file)) {
            for (String line; (line = br.readLine()) != null; ) {
                String[] lineTemp = line.split(",");
                if (lineTemp.length == 3) {
                    tableData.addRow(lineTemp);
                } else {
                    // only occurs if the user file is corrupted or modified
                    tableData.addRow(new String[]{"Error", "Error", "Error"});
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}