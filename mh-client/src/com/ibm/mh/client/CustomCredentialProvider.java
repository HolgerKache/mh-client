package com.ibm.mh.client;


import com.ibm.messagehub.login.CredentialProvider;

public class CustomCredentialProvider implements CredentialProvider {

	public CustomCredentialProvider ()  {

		// This will only show a username and passsword if the
		// class has been called in the same context
		// as the calling servlet that provides these credentials
		
		System.out.println("Username: " + ClientApplication.username);
		System.out.println("Password: " + ClientApplication.password);
	}
	
	@Override
	public char[] getPassword(String clientId) {
		
		if (ClientApplication.password != null) {
		return ClientApplication.password.toCharArray();
		} else return null;
		
	}

	@Override
	public String getUserName(String clientId) {
		
		if (ClientApplication.username != null) {
		return ClientApplication.username;
		} else return null;
	}

}
