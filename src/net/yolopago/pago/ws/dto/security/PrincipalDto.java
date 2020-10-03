package net.yolopago.pago.ws.dto.security;

import java.util.List;

public class PrincipalDto  {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private Integer expires_in;
    private String username;
    private List<Authority> authorities;
    private Long id;
    private String email;
    private String name;
    private String lastname;
    private String lastname2;
    private Long idMerchant;
    private String taxIdMerchant;
    private Long idTerminal;
    private Long idSeller;
    private boolean sellerEnable;
    private boolean terminalActiva;
    private boolean sellerAcces;

    public boolean isSellerAcces() {
        return sellerAcces;
    }
    public void setSellerAcces(boolean sellerAcces) {
        this.sellerAcces = sellerAcces;
    }

    public String getTaxIdMerchant() {
        return taxIdMerchant;
    }

    public void setTaxIdMerchant(String taxIdMerchant) {
        this.taxIdMerchant = taxIdMerchant;
    }

    public Long getIdSeller() {
        return idSeller;
    }
    public void setIdSeller(Long idSeller) {
        this.idSeller = idSeller;
    }

    public boolean isTerminalActiva() {
        return terminalActiva;
    }
    public void setTerminalActiva(boolean terminalActiva) {
        this.terminalActiva = terminalActiva;
    }
    public boolean isSellerEnable() {
        return sellerEnable;
    }
    public void setSellerEnable(boolean sellerEnable) {
        this.sellerEnable = sellerEnable;
    }
    public Long getIdTerminal() {
        return idTerminal;
    }
    public void setIdTerminal(Long idTerminal) {
        this.idTerminal = idTerminal;
    }
    public Long getIdMerchant() {
        return idMerchant;
    }
    public void setIdMerchant(Long idMerchant) {
        this.idMerchant = idMerchant;
    }



    public String getAccess_token() {
        return access_token;
    }
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    public String getToken_type() {
        return token_type;
    }
    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
    public String getRefresh_token() {
        return refresh_token;
    }
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
    public Integer getExpires_in() {
        return expires_in;
    }
    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public List<Authority> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getLastname2() {
        return lastname2;
    }
    public void setLastname2(String lastname2) {
        this.lastname2 = lastname2;
    }
}
