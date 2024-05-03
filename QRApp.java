import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.*;

public class QRApp extends JFrame implements ActionListener, KeyListener {

    static String url;
    JLabel label, message1, message2, message3;
    ImageIcon iconImg;
    JTextArea text;
    JButton getQrButton, againButton, DownloadButton, OpenButton;
    JPanel qrPanel;

    List<JLabel> qrImageLabels;
    JFileChooser fileChooser;

    public QRApp() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setBounds(500, 100, 650, 700);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setTitle("QR generator");

        iconImg = new ImageIcon("Lalit.png");
        this.setIconImage(iconImg.getImage());

        label = new JLabel();
        label.setText("Enter Your Link ");
        label.setFont(new Font("Courier New", Font.BOLD, 16));
        label.setBounds(100, 105, 250, 100);


        text = new JTextArea();
        text.setFont(new Font("Courier New", Font.BOLD, 16));
        text.setBounds(260, 130, 250, 53);
        Border border =BorderFactory.createLineBorder(Color.BLACK);
        Border emptyBorder =BorderFactory.createEmptyBorder(5,5,5,5);
        text.setBorder(BorderFactory.createCompoundBorder(border, emptyBorder));
        Color backgroundColor = new Color(240, 240, 240);
        text.setBackground(backgroundColor);
        text.setEditable(false);

        text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                text.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        text.setBackground(Color.WHITE);
                        text.setEditable(true);
                    }
                });
            }

            @Override
            public void focusLost(FocusEvent e) {
                text.setBackground(backgroundColor);
                text.setEditable(false);
            }
        });

        text.setLineWrap(true);
        text.setWrapStyleWord(true);


        getQrButton = new JButton("Get QR");
        getQrButton.setBounds(280, 200, 100, 35);
        getQrButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        getQrButton.addActionListener(this);
        text.addKeyListener(this);

        againButton = new JButton("Again");
        againButton.setBounds(400, 200, 100, 35);
        againButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        againButton.addActionListener(this);

        qrPanel = new JPanel();
        qrPanel.setBounds(200, 300, 212, 212);
        qrPanel.setLayout(new FlowLayout());
        qrImageLabels = new ArrayList<>();

        DownloadButton = new JButton("Download");
        DownloadButton.setBounds(200, 550, 100, 35);
        DownloadButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        DownloadButton.addActionListener(this);

        OpenButton = new JButton("Open");
        OpenButton.setBounds(310, 550, 100, 35);
        OpenButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        OpenButton.addActionListener(this);

        message1 = new JLabel("QR Download");
        message1.setBounds(250, 600, 250, 30);
        message1.setFont(new Font("Courier New", Font.BOLD, 16));
        message1.setForeground(Color.GREEN);

        message2 = new JLabel("QR not Download");
        message2.setBounds(230, 600, 250, 30);
        message2.setFont(new Font("Courier New", Font.BOLD, 16));
        message2.setForeground(Color.RED);

        message3 = new JLabel("Please enter your link or text");
        message3.setBounds(260, 175, 250, 30);
        message3.setFont(new Font("Courier New", Font.BOLD, 13));
        message3.setForeground(Color.RED);

        qrImageLabels.add(new JLabel()); // Add the first QR code image label to the list


        Color buttonColor = new Color(76, 175, 80);
        // Update button styles

        getQrButton.setBackground(buttonColor);
        getQrButton.setForeground(buttonColor);
        againButton.setBackground(buttonColor);
        againButton.setForeground(buttonColor);
        DownloadButton.setBackground(buttonColor);
        DownloadButton.setForeground(buttonColor);
        OpenButton.setBackground(buttonColor);
        OpenButton.setForeground(buttonColor);


        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.setBorder(BorderFactory.createEmptyBorder());


        // Create a menu named "File"
        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        // Create a menu item named "About"
        JMenuItem aboutMenuItem = new JMenuItem("About");
        menu.add(aboutMenuItem);

        // Add an action listener to the "About" menu item
        aboutMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(QRApp.this, """
                This is a QR Code Generator Application.
                Created by Lalit Kumar.
                Version 1.0
                Developed in Java using the ZXing library.
                © 2023 Lalit. All rights reserved.""", "About", JOptionPane.INFORMATION_MESSAGE)
        );


        this.add(label);
        this.add(text);
        this.add(getQrButton);
        this.add(againButton);
        this.add(qrPanel);

        againButton.setEnabled(false);

        this.setVisible(true);
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new QRApp();
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == getQrButton) {
            if (Objects.equals(text.getText(), "")) {
                add(message3);
                message3.setVisible(true);
            } else {
                message3.setVisible(false);
                url = text.getText();
                //System.out.println(url);

                try {
                    createQR();
                } catch (WriterException | IOException e1) {
                    e1.printStackTrace();
                }

                text.setEnabled(false);
                getQrButton.setEnabled(false);
                againButton.setEnabled(true);
                this.add(DownloadButton);
                this.add(OpenButton);
                DownloadButton.setVisible(true);
                OpenButton.setVisible(true);
                OpenButton.setEnabled(false);

            }


            this.revalidate();
            this.repaint();

        }

        if (e.getSource() == againButton) {
            text.setEnabled(true);
            text.setText("");
            getQrButton.setEnabled(true);
            againButton.setEnabled(false);
            DownloadButton.setVisible(false);
            OpenButton.setVisible(false);
            qrPanel.removeAll();
            message1.setVisible(false);
            message2.setVisible(false);

            // Clear all QR code image labels from the list
            qrImageLabels.clear();
            Color backgroundColor = new Color(240, 240, 240);
            qrPanel.setBackground(backgroundColor);
            qrPanel.revalidate();
            qrPanel.repaint();
            this.repaint();
        }

        if (e.getSource() == DownloadButton) {
            try {
                downloadQR();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        if (e.getSource() == OpenButton) {
            try {
                openQR();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    private void createQR() throws WriterException, IOException {

        String str = url;
        String path = "qr.png";
        String charset = "UTF-8";
        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<>();
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        // Generate the QR code as a BufferedImage
        BitMatrix matrix = new MultiFormatWriter().encode(new String(str.getBytes(charset), charset), BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);

        // Save the BufferedImage as a PNG image file
        File qrFile = new File(path);
        MatrixToImageWriter.writeToPath(matrix, "png", qrFile.toPath());

        // Create a new QR code image label and add it to the list
        JLabel newQrImageLabel = new JLabel(new ImageIcon(qrImage));
        qrImageLabels.add(newQrImageLabel);

        Color backgroundColor = new Color(76, 175, 80);
        qrPanel.setBackground(backgroundColor);

        // Remove the old QR code image label from the qrPanel and add the new one
        qrPanel.removeAll();
        qrPanel.add(newQrImageLabel);

        qrPanel.revalidate();
        qrPanel.repaint();
        //System.out.println("QR Code created successfully.");
    }

    private void downloadQR() throws IOException {
        fileChooser = new JFileChooser("C:\\Users\\a3lal\\Downloads");
        int response = fileChooser.showSaveDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            String sourceFilePath = "qr.png";
            File destinationFilePath = new File(fileChooser.getSelectedFile().getAbsoluteFile() + ".png");
            File sourceFile = new File(sourceFilePath);
            File destinationFile = new File(destinationFilePath.toURI());
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            this.add(message1);
            message1.setVisible(true);
            message2.setVisible(false);
            OpenButton.setEnabled(true);
            this.repaint();
            //System.out.println("QR Code downloaded");
        } else if (response == JFileChooser.CANCEL_OPTION) {
            this.add(message2);
            message1.setVisible(false);
            message2.setVisible(true);
            this.repaint();
            //System.out.println("QR Code not downloaded");
        }
    }

    private void openQR() throws IOException {
        File destinationFilePath = new File(fileChooser.getSelectedFile().getAbsoluteFile() + ".png");
        File destinationFile = new File(destinationFilePath.toURI());

        if (destinationFile.exists()) {
            try {
                Desktop.getDesktop().open(destinationFile);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error opening the QR code image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "The QR code image does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // Respond to the Enter key press
            getQrButton.doClick(); // Simulate a click on the "Get QR" button
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
