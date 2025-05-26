
package com.mycompany.part2;

/**
 *
 * @author RC_Student_lab
 */
class User {
    private static User instance;
     String Username;
     String Password;
     String FirstName;
     String LastName;
    
    
    public User() {
        
}
    public static User getInstance()  {
        if (instance ==null) {
            instance = new User();
        }
        return instance;
    }
    //Set username and password for authentication
    public void setCredentials(String Username, String Password) {
        this.Username = Username;
        this.Password = Password;
    }
    //Check if provided credentials are valid
    public boolean checkCredentials(String username, String password){
        return this.Username != null && this.Username.equals(Username) && this.Password != null && this.Password.equals(Password);
        
    }
    //Return username
    public String  getUsername(){
        return Username;
    }
    //Return password 
    public String getPassword(){
        return Password;
    }
    //Return first name 
    public String getFirstName(){
        String Firstname = null;
        return Firstname;
    }
    
    public void setFirstName(String FName){
        this.FirstName = FName;
    }
    //Returns lastname
    public String getLastName(){
        String LastName = null;
        return LastName;
    }
    
    public void setLastName(String lname){
        this.LastName = lname;
    }

    void setfirstName(String text) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

   // String getLastName() {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

