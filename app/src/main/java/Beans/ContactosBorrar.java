package Beans;

/**
 * Created by Susana on 31/08/2015.
 */
public class ContactosBorrar {

    private long _id;
    private String Nombre;
    private String Telefono;
    private String Email;

    public ContactosBorrar(long _id, String nombre, String telefono, String email, int id_Categoria, boolean checked) {
        this._id = _id;
        Nombre = nombre;
        Telefono = telefono;
        Email = email;
        Id_Categoria = id_Categoria;
        this.checked = checked;
    }

    private int Id_Categoria;
    private boolean checked;




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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
