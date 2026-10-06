package com.safebank.banking.dto;

public class UserResponse {
	   private Long id;
	    private String fullName;
	    private String email;
	    private String mobile;
	    private String dob;
		private String aadhar;
		private String Pan;
		private String address;

	    public UserResponse() {
	    }

	    public UserResponse(Long id, String fullName, String email, String mobile
	    		,String dob,String aadhar,String Pan,String address) {
	        this.id = id;
	        this.fullName = fullName;
	        this.email = email;
	        this.mobile = mobile;
	        this.dob=dob;
	        this.aadhar=aadhar;
	        this.Pan=Pan;
	        this.address=address;
	        
	    }

	    public Long getId() {
	        return id;
	    }

	    public String getFullName() {
	        return fullName;
	    }

	    public String getEmail() {
	        return email;
	    }

	    public String getMobile() {
	        return mobile;
	    }
	
}
