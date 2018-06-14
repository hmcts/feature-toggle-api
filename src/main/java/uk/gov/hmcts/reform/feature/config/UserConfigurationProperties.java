package uk.gov.hmcts.reform.feature.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("users")
public class UserConfigurationProperties {

    private List<UserDetails> admins;
    private List<UserDetails> editors;
    private List<UserDetails> readers;

    public List<UserDetails> getAdmins() {
        return admins;
    }

    public void setAdmins(List<UserDetails> admins) {
        this.admins = admins;
    }

    public List<UserDetails> getEditors() {
        return editors;
    }

    public void setEditors(List<UserDetails> editors) {
        this.editors = editors;
    }

    public List<UserDetails> getReaders() {
        return readers;
    }

    public void setReaders(List<UserDetails> readers) {
        this.readers = readers;
    }

    public static class UserDetails {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
