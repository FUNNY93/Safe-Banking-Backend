package com.safebank.banking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // APPLICATION DETAILS
    // =========================

    @Column(unique = true)
    private String applicationNumber;

    // =========================
    // PERSONAL DETAILS
    // =========================

    @Column(nullable = false)
    private String fullName;

    private String fatherName;

    private String dob;

    private String gender;

    private String address;

    // =========================
    // CONTACT DETAILS
    // =========================

    @Column(nullable = false, unique = true)
    private String mobile;

    @Column(nullable = false, unique = true)
    private String email;

    // =========================
    // KYC DETAILS
    // =========================

    @Column(nullable = false, unique = true)
    private String aadhar;

    @Column(nullable = false, unique = true)
    private String pan;

    // =========================
    // ACCOUNT DETAILS
    // =========================

    private String accountType;

    private String nominee;

    @Column(nullable = false)
    private Double deposit = 0.0;

    private String status = "Pending";

    @Column(unique = true)
    private String accountNumber;
    
    @Column(name = "ifsc_code")
    private String ifscCode;

    // =========================
    // SECURITY
    // =========================

    private String pin;

 

    // =========================
    // CONSTRUCTORS
    // =========================

    public User() {
    }

    public User(
            Long id,
            String fullName,
            String email,
            String mobile) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        
    }

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getNominee() {
        return nominee;
    }

    public void setNominee(String nominee) {
        this.nominee = nominee;
    }

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

}