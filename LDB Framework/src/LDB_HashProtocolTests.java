import java.util.Base64;
import java.util.Scanner;
import java.io.*;

public class LDB_HashProtocolTests {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		/** HASHING VS HASHING+SALT Password Database Storing  **/
		String password = "1234Hello";
		String userPassword;
		String encPassword = Base64.getEncoder().encodeToString(password.getBytes());
		encPassword = Integer.toString(encPassword.hashCode());
		
		Scanner in = new Scanner(System.in);
		
		System.out.println("Password to Hash (Password with only hashing): ");
		System.out.println("EX: If password is '1234Hello' then the hash is: "+ encPassword);
		userPassword = in.nextLine();
		
		if (Integer.toString(Base64.getEncoder().encodeToString(userPassword.getBytes()).hashCode()).equals(encPassword)) {
			System.out.println("YOUR PASSWORD WAS A MATCH! Your hashed password: "+ Integer.toString(Base64.getEncoder().encodeToString(userPassword.getBytes()).hashCode()));
		} else {
			System.out.println("YOU ENTERED A DIFFERENT PASSWORD! Your hashed password: "+ Integer.toString(Base64.getEncoder().encodeToString(userPassword.getBytes()).hashCode()));
		}
		
		String salt0 = "WGinl5cZ8c"; /* String password offsets for further encoding!*/
		String userPasswordSALT;
		
		System.out.println("\nPassword to Hash (Password & Salt hashing): ");
		encPassword = Integer.toString(Base64.getEncoder().encodeToString((password+salt0).getBytes()).hashCode());
		System.out.println("EX: If password is '1234Hello' then the hash with salt is: "+ encPassword);
		userPasswordSALT = in.nextLine();
		
		if (Integer.toString(Base64.getEncoder().encodeToString((userPasswordSALT+salt0).getBytes()).hashCode()).equals(encPassword)){
			System.out.println("YOUR PASSWORD WAS A MATCH! Your hashed with salt password is: "+ Integer.toString(Base64.getEncoder().encodeToString((userPasswordSALT+salt0).getBytes()).hashCode()));
			in.close();
		}else {
			System.out.println("YOU ENTERED A DIFFERENT PASSWORD! Your hashed with salt password is: "+ Integer.toString(Base64.getEncoder().encodeToString((userPasswordSALT+salt0).getBytes()).hashCode()));
			in.close();
		}
	}
}