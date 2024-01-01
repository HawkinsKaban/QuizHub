package quizhub.controller;

import quizhub.Koneksi;
import quizhub.Model.User;
import quizhub.view.AdminView;
import quizhub.view.DosenView;
import quizhub.view.MahasiswaView;
import quizhub.view.Login;

import java.sql.ResultSet;
import javax.swing.*;
import java.sql.SQLException;



public class LoginController {
    private final Login login;
    private final Koneksi koneksi;
    String sql;

    public LoginController(Login login, Koneksi koneksi) {
        this.login = login;
        this.koneksi = koneksi;

        login.setLoginListener(e -> performLogin());
    }

    public void performLogin() {
        try {
            User user = login.getUserInput();
            String getRole = user.getRole();;

            switch (getRole) {
                case "Admin":
                    sql = "SELECT * FROM admin WHERE username='" + user.getUsername() + "' AND password='" + user.getPassword() + "'";
                    break;
                case "Dosen":
                    sql = "SELECT * FROM dosen WHERE username='" + user.getUsername() + "' AND password='" + user.getPassword() + "'";
                    break;
                default:
                    sql = "SELECT * FROM mahasiswa WHERE username='" + user.getUsername() + "' AND password='" + user.getPassword() + "'";
                    break;
            }

            ResultSet rs = koneksi.stm.executeQuery(sql);

            if (rs.next()) {
                // Login successful
                openRoleSpecificView(getRole);
            } else {
                JOptionPane.showMessageDialog(login, "Username or password is incorrect");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(login, e.getMessage());
        }
    }

    private void openRoleSpecificView(String role) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            switch (role) {
                case "Admin":
                    new AdminView().setVisible(true);
                    break;
                case "Dosen":
                    new DosenView().setVisible(true);
                    break;
                default:
                    ResultSet rs = koneksi.stm.executeQuery(sql);

                    if (rs.next()) {
                        String nama = rs.getString("nama");
                        String nim = rs.getString("nim");

                        new MahasiswaView(nama, nim).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(login, "Failed to retrieve user information");
                    }
                    break;
            }

            login.dispose(); // Close the login window
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(login, ex.getMessage());
        }
    }

}