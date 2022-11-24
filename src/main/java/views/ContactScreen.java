package views;

import entities.User;
import services.DBInitializer;
import services.DBService;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;



public class ContactScreen extends JPanel implements ActionListener {
    ArrayList<MemberVO>members = new ArrayList<>();
    DBService dbService;
    int userID;

    JTable table;
    DefaultTableModel model;
    JTextField tfUserid;
    public ContactScreen(int userID) throws ExecutionException, InterruptedException {
        this.userID = userID;
        this.dbService = new DBService();
        //Columns
        String[] colNames = new String[]{"User ID"};
        this.model = new DefaultTableModel(colNames, 0);

        BorderLayout borderLayout = new BorderLayout();
        this.setLayout(borderLayout);

        this.table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);


        //Input Panel at the bottom of the screen


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2,1));

        JPanel panel = new JPanel();
        this.tfUserid = new JTextField(6);

        panel.add(new JLabel("User ID"));
        panel.add(tfUserid);
        bottomPanel.add(panel);

        JPanel panel2 = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnDel = new JButton("Delete");
        panel2.add(btnAdd);
        panel2.add(btnDel);
        bottomPanel.add(panel2);

        String[] rows = new String[2];

        // Fetch list of all users from database
        User targetUser = dbService.getUserDetails(userID);
        ArrayList<Long> contacts = targetUser.getContacts();
        System.out.println(contacts);

        for (Long contact : contacts) {
            rows[0] = String.valueOf(contact);
            model.addRow(rows);
            members.add(new MemberVO(contact));
        }

        tfUserid.setText("");
        btnAdd.addActionListener(this);
        btnDel.addActionListener(this);

        this.add(bottomPanel, BorderLayout.SOUTH);

    }

    public int getSelectedRowUserID() {
        int rowIndex = this.table.getSelectedRow();
        if (rowIndex == -1) {
            return rowIndex;
        } else {
            int contactID = members.get(rowIndex).userid.intValue();
            return contactID;
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String source = e.getActionCommand();

        if (source.equals("Delete")) {
            int rowIndex = this.table.getSelectedRow();

            if (rowIndex == -1) {
                JOptionPane.showMessageDialog(this, "Select a contact.");
            }
            this.model.removeRow(rowIndex);

            Long contactID = members.get(rowIndex).userid;

            User targetUser;
            try {
                targetUser = dbService.getUserDetails(userID);
            } catch (ExecutionException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }


            try {
                dbService.deleteContact(targetUser, contactID);
            } catch (ExecutionException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            members.remove(rowIndex);

        } else if (source.equals("Add")) {
            String[] rows = new String[2];

            if (this.tfUserid.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Type in a user ID.");
            } else if (!tfUserid.getText().matches("^\\d*$")) {
                JOptionPane.showMessageDialog(this, "Type in an integer.");
            }

            rows[0] = this.tfUserid.getText();


            this.tfUserid.setText("");


            Long userid = Long.parseLong(rows[0]);


            User targetUser;
            try {

                targetUser = dbService.getUserDetails(userID);

            } catch (ExecutionException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            try {
                if (userid.intValue() == userID) {
                    JOptionPane.showMessageDialog(this, "You can't add yourself as a contact!");
                } else if (dbService.getUserDetails(userid.intValue()) == null) {
                    JOptionPane.showMessageDialog(this, "There is no user with this ID.");
                } else if (targetUser.getContacts().contains(userid)) {
                    JOptionPane.showMessageDialog(this, "You already have a contact with this ID.");
                } else {
                    dbService.addContact(targetUser, userid);
                    model.addRow(rows);
                    members.add(new MemberVO(userid));
                }

            } catch (ExecutionException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }



        }

    }

    static class MemberVO{
        private final Long userid;

        public MemberVO(Long userid){
            this.userid = userid;
        }


    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, FileNotFoundException {
        DBInitializer initializer = new DBInitializer();
        initializer.init();
        JFrame app = new JFrame("app");
        app.add(new ContactScreen(1));
        app.pack();
        app.setVisible(true);
    }
}
