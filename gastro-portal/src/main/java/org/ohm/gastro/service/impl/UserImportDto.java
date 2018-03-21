package org.ohm.gastro.service.impl;

/**
 * Created by ezhulkov on 02.07.15.
 */
public class UserImportDto {

    private String email;
    private String password;
    private String catalog;
    private String source;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "UserImportDto{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", catalog='" + catalog + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
