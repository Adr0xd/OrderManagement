package org.mishu.Model;

public class Client {

	private int id;
	private String name;
	private String address;
	private String email;

	public Client() {

	}

	/**
	 * creates a new client
	 *
	 * @param id
	 *            id of the client
	 * @param name
	 *            name of client
	 * @param email
	 *            email address of client
	 * @param address
	 *            location address of client
	 */
	public Client(int id, String name, String email, String address) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.email = email;
	}

	/**
	 *
	 * creates a new client
	 *
	 * @param name
	 *            name of client
	 * @param email
	 *            email address of client
	 * @param address
	 *            location address of client
	 */
	public Client(String name, String email, String address) {
		super();
		this.name = name;
		this.address = address;
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Client [id=" + id + ", name=" + name + ", address=" + address + ", " + "email=" + email + "]";
	}

}
