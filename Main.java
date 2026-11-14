package com.loansystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Main {
    private final JFrame frame = new JFrame("Loan Management System");
    private String currentUser;
    private final Color navy = new Color(28, 48, 82);

    public static void main(String[] args) {
        Database.initialize();
        SwingUtilities.invokeLater(() -> new Main().showLogin());
    }
    private void setup(String title) {
        frame.setTitle(title); frame.setSize(1050, 680); frame.setMinimumSize(new Dimension(850, 560));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.setLocationRelativeTo(null);
    }
    private void showLogin() {
        setup("Loan Management System - Login");
        JPanel root = new JPanel(new GridBagLayout()); root.setBackground(new Color(239,244,250));
        JPanel card = new JPanel(new GridLayout(0,1,8,8)); card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(210,220,232)), BorderFactory.createEmptyBorder(28,34,28,34))); card.setBackground(Color.WHITE); card.setPreferredSize(new Dimension(360,330));
        JLabel heading = new JLabel("LOAN MANAGEMENT SYSTEM", SwingConstants.CENTER); heading.setFont(new Font("Arial",Font.BOLD,19)); heading.setForeground(navy);
        JTextField user = new JTextField("admin"); JPasswordField pass = new JPasswordField("admin123"); JButton login = primary("Login");
        card.add(heading); card.add(new JLabel("Username")); card.add(user); card.add(new JLabel("Password")); card.add(pass); card.add(login);
        JLabel hint = new JLabel("Demo: admin/admin123  |  officer/loan123",SwingConstants.CENTER); hint.setFont(new Font("Arial",Font.PLAIN,11)); card.add(hint);
        login.addActionListener(e -> authenticate(user.getText().trim(),new String(pass.getPassword())));
        pass.addActionListener(e -> authenticate(user.getText().trim(),new String(pass.getPassword())));
        root.add(card); frame.setContentPane(root); frame.setVisible(true);
    }
    private void authenticate(String u,String p) {
        try(Connection c=Database.connect(); PreparedStatement ps=c.prepareStatement("SELECT username FROM users WHERE username=? AND password=?")) {
            ps.setString(1,u); ps.setString(2,p); try(ResultSet rs=ps.executeQuery()) { if(rs.next()){currentUser=u;showDashboard();} else JOptionPane.showMessageDialog(frame,"Invalid username or password"); }
        } catch(SQLException ex){error(ex);}
    }
    private JButton primary(String label){JButton b=new JButton(label);b.setBackground(navy);b.setForeground(Color.WHITE);b.setFocusPainted(false);return b;}
    private void showDashboard() {
        setup("Loan Management System - Dashboard");
        JPanel root=new JPanel(new BorderLayout()); root.setBackground(new Color(245,247,251));
        JPanel top=new JPanel(new BorderLayout());top.setBackground(navy);top.setBorder(BorderFactory.createEmptyBorder(16,22,16,22));
        JLabel title=new JLabel("Loan Management System");title.setForeground(Color.WHITE);title.setFont(new Font("Arial",Font.BOLD,22));
        JButton logout=new JButton("Logout: "+currentUser);logout.addActionListener(e->showLogin());top.add(title,BorderLayout.WEST);top.add(logout,BorderLayout.EAST);root.add(top,BorderLayout.NORTH);
        JPanel nav=new JPanel(new GridLayout(0,1,6,8));nav.setBorder(BorderFactory.createEmptyBorder(18,12,18,12));nav.setBackground(new Color(232,238,247));
        String[] labels={"Dashboard","Customers","Loan Applications","EMI Calculator"}; for(String x:labels){JButton b=new JButton(x);b.setHorizontalAlignment(SwingConstants.LEFT);b.addActionListener(e->showPage(x));nav.add(b);}root.add(nav,BorderLayout.WEST);
        JPanel content=new JPanel(new BorderLayout());content.setName("content");content.setBackground(Color.WHITE);content.setBorder(BorderFactory.createEmptyBorder(24,24,24,24));root.add(content,BorderLayout.CENTER);
        frame.setContentPane(root);showPage("Dashboard");frame.setVisible(true);
    }
    private JPanel content(){return (JPanel)((BorderLayout)frame.getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);}
    private void showPage(String page){JPanel p=content();p.removeAll();p.setLayout(new BorderLayout(12,12));JLabel heading=new JLabel(page);heading.setFont(new Font("Arial",Font.BOLD,23));p.add(heading,BorderLayout.NORTH);
        switch(page){case "Customers"->customerPage(p);case "Loan Applications"->loanPage(p);case "EMI Calculator"->emiPage(p);default->dashboardPage(p);}p.revalidate();p.repaint();}
    private void dashboardPage(JPanel p){JPanel stats=new JPanel(new GridLayout(1,3,14,14));stats.setOpaque(false);try(Connection c=Database.connect();Statement s=c.createStatement()){
        stats.add(stat("Total Customers",count(s,"customers")));stats.add(stat("Total Loans",count(s,"loans")));stats.add(stat("Pending Applications",scalar(s,"SELECT COUNT(*) FROM loans WHERE status='Pending'")));
    }catch(SQLException e){error(e);}p.add(stats,BorderLayout.CENTER);JLabel note=new JLabel("Select a module from the left menu to manage customers and loan applications.");p.add(note,BorderLayout.SOUTH);}
    private JPanel stat(String label,String value){JPanel x=new JPanel(new GridLayout(2,1));x.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,226,235)),BorderFactory.createEmptyBorder(22,20,22,20)));JLabel a=new JLabel(label),b=new JLabel(value);a.setForeground(Color.GRAY);b.setFont(new Font("Arial",Font.BOLD,30));b.setForeground(navy);x.add(a);x.add(b);return x;}
    private String count(Statement s,String table)throws SQLException{return scalar(s,"SELECT COUNT(*) FROM "+table);}private String scalar(Statement s,String q)throws SQLException{try(ResultSet r=s.executeQuery(q)){return r.next()?r.getString(1):"0";}}
    private JTable table(String sql,String[] columns){DefaultTableModel m=new DefaultTableModel(columns,0);try(Connection c=Database.connect();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){while(r.next()){Object[] row=new Object[columns.length];for(int i=0;i<row.length;i++)row[i]=r.getObject(i+1);m.addRow(row);}}catch(SQLException e){error(e);}return new JTable(m);}
    private void customerPage(JPanel p){JButton add=primary("Add Customer");add.addActionListener(e->addCustomer());p.add(add,BorderLayout.SOUTH);p.add(new JScrollPane(table("SELECT id,name,phone,email,income,credit_score FROM customers ORDER BY id",new String[]{"ID","Name","Phone","Email","Monthly Income","Credit Score"})),BorderLayout.CENTER);}
    private void addCustomer(){JTextField name=new JTextField(),phone=new JTextField(),email=new JTextField(),income=new JTextField(),score=new JTextField();Object[] fields={"Full name",name,"Phone",phone,"Email",email,"Monthly income",income,"Credit score",score};if(JOptionPane.showConfirmDialog(frame,fields,"Add Customer",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)return;
        try(Connection c=Database.connect();PreparedStatement ps=c.prepareStatement("INSERT INTO customers(name,phone,email,income,credit_score) VALUES(?,?,?,?,?)")){ps.setString(1,name.getText().trim());ps.setString(2,phone.getText().trim());ps.setString(3,email.getText().trim());ps.setDouble(4,Double.parseDouble(income.getText()));ps.setInt(5,Integer.parseInt(score.getText()));if(name.getText().isBlank())throw new IllegalArgumentException("Name is required");ps.executeUpdate();showPage("Customers");}catch(Exception ex){JOptionPane.showMessageDialog(frame,"Please check the entered values. "+ex.getMessage());}}
    private void loanPage(JPanel p){JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT));JButton add=primary("Apply for Loan"),approve=new JButton("Approve Selected"),reject=new JButton("Reject Selected");actions.add(add);actions.add(approve);actions.add(reject);JTable t=table("SELECT id,customer_name,category,amount,interest_rate,months,status,created_at FROM loans ORDER BY id DESC",new String[]{"ID","Customer","Category","Amount","Rate %","Months","Status","Created"});add.addActionListener(e->addLoan());approve.addActionListener(e->updateLoan(t,"Approved"));reject.addActionListener(e->updateLoan(t,"Rejected"));p.add(actions,BorderLayout.SOUTH);p.add(new JScrollPane(t),BorderLayout.CENTER);}
    private void addLoan(){JTextField customer=new JTextField(),amount=new JTextField(),rate=new JTextField("8.5"),months=new JTextField("60");JComboBox<String> type=new JComboBox<>(new String[]{"Home","Vehicle","Personal","Education","Business"});Object[] f={"Customer name",customer,"Loan category",type,"Loan amount",amount,"Interest rate (%)",rate,"Duration (months)",months};if(JOptionPane.showConfirmDialog(frame,f,"New Loan Application",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)return;
        try(Connection c=Database.connect();PreparedStatement ps=c.prepareStatement("INSERT INTO loans(customer_name,category,amount,interest_rate,months,status) VALUES(?,?,?,?,?,'Pending')")){ps.setString(1,customer.getText().trim());ps.setString(2,(String)type.getSelectedItem());ps.setDouble(3,Double.parseDouble(amount.getText()));ps.setDouble(4,Double.parseDouble(rate.getText()));ps.setInt(5,Integer.parseInt(months.getText()));if(customer.getText().isBlank())throw new IllegalArgumentException("Customer name is required");ps.executeUpdate();showPage("Loan Applications");}catch(Exception ex){JOptionPane.showMessageDialog(frame,"Please check the entered values. "+ex.getMessage());}}
    private void updateLoan(JTable t,String status){int row=t.getSelectedRow();if(row<0){JOptionPane.showMessageDialog(frame,"Select a loan first");return;}int id=Integer.parseInt(t.getValueAt(row,0).toString());try(Connection c=Database.connect();PreparedStatement ps=c.prepareStatement("UPDATE loans SET status=? WHERE id=?")){ps.setString(1,status);ps.setInt(2,id);ps.executeUpdate();showPage("Loan Applications");}catch(SQLException e){error(e);}}
    private void emiPage(JPanel p){JPanel form=new JPanel(new GridLayout(0,2,10,12));JTextField principal=new JTextField("500000"),rate=new JTextField("8.5"),months=new JTextField("60");JButton calc=primary("Calculate EMI");JLabel result=new JLabel("Monthly EMI: —");result.setFont(new Font("Arial",Font.BOLD,18));form.add(new JLabel("Loan amount"));form.add(principal);form.add(new JLabel("Annual interest rate (%)"));form.add(rate);form.add(new JLabel("Duration (months)"));form.add(months);form.add(calc);form.add(result);calc.addActionListener(e->{try{double a=Double.parseDouble(principal.getText()),r=Double.parseDouble(rate.getText())/1200;int n=Integer.parseInt(months.getText());if(a<=0||n<=0||r<0)throw new IllegalArgumentException();double emi=r==0?a/n:a*r*Math.pow(1+r,n)/(Math.pow(1+r,n)-1);result.setText(String.format("Monthly EMI: ₹%,.2f",emi));}catch(Exception ex){JOptionPane.showMessageDialog(frame,"Enter valid positive loan amount and duration.");}});p.add(form,BorderLayout.NORTH);}
    private void error(Exception e){JOptionPane.showMessageDialog(frame,e.getMessage(),"Database error",JOptionPane.ERROR_MESSAGE);}
}
