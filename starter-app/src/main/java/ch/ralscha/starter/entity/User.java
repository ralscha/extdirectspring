package ch.ralscha.starter.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "`User`")
public class User extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(max = 100)
	@Column(unique = true)
	private String userName;

	@Size(max = 254)
	private String name;

	@Size(max = 254)
	private String firstName;

	@Email
	@Size(max = 254)
	@NotNull
	private String email;

	@Size(max = 80)
	private String passwordHash;

	@Size(max = 8)
	private String locale;

	private boolean enabled;

	@Temporal(TemporalType.DATE)
	@JsonIgnore
	private Date createDate;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "UserRoles", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "roleId"))
	private Set<Role> roles;

	public void update(User modifiedUser) {
		this.userName = modifiedUser.getUserName();
		this.name = modifiedUser.getName();
		this.firstName = modifiedUser.getFirstName();
		this.email = modifiedUser.getEmail();
		this.enabled = modifiedUser.isEnabled();
		this.locale = modifiedUser.getLocale();

		if (StringUtils.hasText(modifiedUser.getPasswordHash())) {
			this.passwordHash = modifiedUser.getPasswordHash();
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JsonIgnore
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
