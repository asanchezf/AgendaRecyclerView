package Beans;

public class Contactos {
	
	
	/*
	 * Id_Categoria
	 * 1-Familia
	 * 2-Amigos
	 * 3-Compa�eros
	 * 4-Otros
	 * 
	 * */
	
	private long _id;
	private String Nombre;
	private String Apellidos;
	private String Direccion;
	private String Telefono;
	private String Email;
	private int Id_Categoria;
	private String Observaciones;

	private int Importado;
	private int Sincronizado;


	public Contactos(long _id, String nombre, String apellidos, String direccion, String telefono, String email, int id_Categoria, String observaciones, int importado, int sincronizado) {
		this._id = _id;
		Nombre = nombre;
		Apellidos = apellidos;
		Direccion = direccion;
		Telefono = telefono;
		Email = email;
		Id_Categoria = id_Categoria;
		Observaciones = observaciones;
		Importado = importado;
		Sincronizado = sincronizado;
	}

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getNombre() {
		return Nombre;
	}

	public void setNombre(String nombre) {
		Nombre = nombre;
	}

	public String getApellidos() {
		return Apellidos;
	}

	public void setApellidos(String apellidos) {
		Apellidos = apellidos;
	}

	public String getDireccion() {
		return Direccion;
	}

	public void setDireccion(String direccion) {
		Direccion = direccion;
	}

	public String getTelefono() {
		return Telefono;
	}

	public void setTelefono(String telefono) {
		Telefono = telefono;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public int getId_Categoria() {
		return Id_Categoria;
	}

	public void setId_Categoria(int id_Categoria) {
		Id_Categoria = id_Categoria;
	}

	public String getObservaciones() {
		return Observaciones;
	}

	public void setObservaciones(String observaciones) {
		Observaciones = observaciones;
	}

	public int getImportado() {
		return Importado;
	}

	public void setImportado(int importado) {
		Importado = importado;
	}

	public int getSincronizado() {
		return Sincronizado;
	}

	public void setSincronizado(int sincronizado) {
		Sincronizado = sincronizado;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Contactos contactos = (Contactos) o;

		if (_id != contactos._id) return false;
		if (Id_Categoria != contactos.Id_Categoria) return false;
		if (Importado != contactos.Importado) return false;
		if (Sincronizado != contactos.Sincronizado) return false;
		if (!Nombre.equals(contactos.Nombre)) return false;
		if (!Apellidos.equals(contactos.Apellidos)) return false;
		if (!Direccion.equals(contactos.Direccion)) return false;
		if (!Telefono.equals(contactos.Telefono)) return false;
		if (!Email.equals(contactos.Email)) return false;
		return Observaciones.equals(contactos.Observaciones);

	}

	@Override
	public int hashCode() {
		int result = (int) (_id ^ (_id >>> 32));
		result = 31 * result + Nombre.hashCode();
		result = 31 * result + Apellidos.hashCode();
		result = 31 * result + Direccion.hashCode();
		result = 31 * result + Telefono.hashCode();
		result = 31 * result + Email.hashCode();
		result = 31 * result + Id_Categoria;
		result = 31 * result + Observaciones.hashCode();
		result = 31 * result + Importado;
		result = 31 * result + Sincronizado;
		return result;
	}
}
