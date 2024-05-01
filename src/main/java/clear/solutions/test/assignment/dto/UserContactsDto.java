package clear.solutions.test.assignment.dto;

import clear.solutions.test.assignment.constants.ApiConstants;
import jakarta.validation.constraints.Email;

public class UserContactsDto {
    @Email(regexp = ApiConstants.EMAIL_REGEX, message = "Invalid email format")
    private String email;
    private String address;
    private String phone;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UpdateUserContactsDto{" +
                "email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
