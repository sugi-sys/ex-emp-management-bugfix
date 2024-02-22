package com.example.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 従業員登録時に使用するフォーム.
 * 
 * @author sugimoto
 * 
 */
public class InsertEmployeeForm {
    /** 名前 */
    @NotBlank()
    private String name;

    /** 画像（名前） */
    private MultipartFile image;

    /** 性別 */
    @NotBlank()
    private String gender;

    /** 入社日 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull()
    private Date hireDate;

    /** メールアドレス */
    @NotBlank()
    private String mailAddress;

    /** 郵便番号 */
    @Pattern(regexp = "^[0-9]{7}$")
    @NotBlank()
    private String zipCode;

    /** 住所 */
    @NotBlank()
    private String address;

    /** 電話番号 */
    @NotBlank()
    private String telephone;

    /** 給料 */
    @NotBlank()
    private String salary;

    /** 特性 */
    @NotBlank()
    private String characteristics;
    
    /** 扶養人数 */
    @Pattern(regexp = "^[0-9]+$", message = "扶養人数は数値で入力してください")
    private String dependentsCount;

    /**
	 * 給料を数値として返します.
	 * 
	 * @return 数値の給料
	 */
	public Integer getIntSalary() {
		return Integer.parseInt(salary);
	}

    /**
	 * 扶養人数を数値として返します.
	 * 
	 * @return 数値の扶養人数
	 */
	public Integer getIntDependentsCount() {
		return Integer.parseInt(dependentsCount);
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public String getDependentsCount() {
        return dependentsCount;
    }

    public void setDependentsCount(String dependentsCount) {
        this.dependentsCount = dependentsCount;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "InsertEmployeeForm [name=" + name + ", image=" + image + ", gender=" + gender + ", hireDate=" + hireDate
                + ", mailAddress=" + mailAddress + ", zipCode=" + zipCode + ", address=" + address + ", telephone="
                + telephone + ", salary=" + salary + ", characteristics=" + characteristics + ", dependentsCount="
                + dependentsCount + "]";
    }

    
}
