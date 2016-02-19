package controlador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import Beans.Contactos;
import Beans.ContactosBorrar;
import modelo.DBhelper;


public class SQLControlador {

	private DBhelper dbhelper;
	private Context ourcontext;
	private SQLiteDatabase db;
	// Definimos constante con el nombre de la tabla
	//
	public static final String C_TABLA = "Contactos";
	public static final String C_COLUMNA_ID = "_id";
	public static final String C_COLUMNA_NOMBRE = "Nombre";
	public static final String C_COLUMNA_APELLIDOS = "Apellidos";
	public static final String C_COLUMNA_DIRECCION = "Direccion";
	public static final String C_COLUMNA_TELEFONO = "Telefono";
	public static final String C_COLUMNA_EMAIL = "Email";

	public static final String C_COLUMNA_CATEGORIA = "Id_Categoria";
	public static final String C_COLUMNA_OBSERVACIONES = "Observaciones";


	//private Resources resources;//para traer directamente los recursos al código java...



    public static final String IMPORTADO_OBSERVACIONES = "Contacto importado desde la agenda de Android el día: ";
    //public static final String IMPORTADO_OBSERVACIONES =(getResources().getString(R.string.agenda_texto_vacio));
    //public static final String IMPORTADO_OBSERVACIONES ="@style/observaciones_importados";

   // public static final String IMPORTADO_OBSERVACIONES ="";

    private String importado_observaciones;
    public static final String EMAIL_POR_DEFECTO = "email@gmail.com";
	
	public SQLControlador(Context c) {
		super();
		this.ourcontext = c;

        //resources=Resources.getSystem().getResourceName()

       //Resources resources = resources.getString(R.string.observaciones_importados);
        //Resources resources =Resources.getSystem();
        //importado_observaciones = resources.getString(R.string.observaciones_importados);

        //importado_observaciones = resources.getString(values.R.string.observaciones_importados);
	}



	public SQLControlador abrirBaseDeDatos(int opcion) throws SQLException {

		dbhelper = new DBhelper(ourcontext);

		if (opcion == 1) {
			db = dbhelper.getReadableDatabase();// Abrimos en modo lectura
		} else if (opcion == 2) {
			db = dbhelper.getWritableDatabase();// Abrimos en modo escritura
		}
		return this;
	}

	public void cerrar() {
		dbhelper.close();
	}

	// Ya existe un objeto db en la clase... por eso no se pasa por par�metro...
	public void InsertarUsuario(String Nombre, String Apellidos,
			String Direccion, String Telefono, String Email, long Id_Categ,
			String observa) {


		if(Email.toString().equals("")){
			Email="Email no disponible";
		}


		//Los campos Importado y Sincronizado no se muestran por pantalla y admiten nulos, por eso no se incluuyen en el insert
		SQLiteStatement pst = db
				.compileStatement("INSERT INTO Contactos (Nombre, Apellidos, Direccion, Telefono, Email, Id_Categoria, Observaciones) VALUES (?,?,?,?,?,?,?)");
		pst.bindString(1, Nombre);
		pst.bindString(2, Apellidos);
		pst.bindString(3, Direccion);
		pst.bindString(4, Telefono);
		pst.bindString(5, Email);

		pst.bindLong(6, Id_Categ);
		pst.bindString(7, observa);
		pst.execute();

	}

	// Utilizando ContentValus
	public void InsertContentAgenda(String Nombre, String Telefono, String Email) {

		ContentValues valores = new ContentValues();

		valores.put(C_COLUMNA_NOMBRE, Nombre);
		// valores.put(C_COLUMNA_APELLIDOS, Apellidos);
		// valores.put(C_COLUMNA_DIRECCION, Direccion);
		valores.put(C_COLUMNA_TELEFONO, Telefono);
		valores.put(C_COLUMNA_EMAIL, Email);
		valores.put(C_COLUMNA_CATEGORIA, 4);
		// valores.put(C_COLUMNA_OBSERVACIONES, observa);
		db.insert(C_TABLA, null, valores);

	}

	public void ImportaCursorContent(Cursor nombres, Cursor telefonos,
			Cursor emails) {
		
		
		ContentValues valores = new ContentValues();
		db.beginTransaction();
		
		// NOMBRES
		while (nombres.moveToNext()) {
			valores.put(C_COLUMNA_NOMBRE, nombres.getInt(0));

			// TEL�FONOS
			while (telefonos.moveToNext()) {
				valores.put(C_COLUMNA_TELEFONO, telefonos.getInt(0));

			}
			telefonos.close();

			// EMAILS
			while (emails.moveToNext()) {
				
				valores.put(C_COLUMNA_EMAIL, emails.getInt(0));

			}
			emails.close();

		}
		
		db.insert(C_TABLA, null, valores);
		
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	//Se utiliza para importar los contactos de android
	public void ImportCollectionContactsContent(ArrayList<Contactos> ArrayListcontactos, String fecha){
		/*
	    * PARA IMPORTAR LOS CONTACTOS DE ANDROID Y COMPARAR CON LO QUE HAYA EN LA BB.DD. DE LA APP...
		* Tenemos toda la Agenda de Android en ArrayListcontactos
		* Borramos de la BB.DD. de la app los que ya hayan sido importados antes (categoría 5)
		* Buscamos en la BB.DD. los contactos por nombre y los comparemos que con que tenemos en ArrayListcontactos
		* que hemos traido de la agenda de Android.
		* Insertamos los contacots de Android que tenemos en ArrayListcontactos y que no se hallen en la BB.DD. de la app.
		* */



		//Fecha de la actualizaci�n
		Contactos contactos;
		ArrayList<Contactos> arraListPorNombre = new ArrayList<Contactos>();//para comparar datos traidos con otros antes de hacer el insert
		
		
		ContentValues valores=new ContentValues();
		
		
		
		db.beginTransaction();

		//borrarTodos();
//		borrarImportados();
		
		
		//borrarImportados(nombre);

		//Borramos los que ya han sido previamente importados para no tener duplicados
		borrarImportados();
		
		//Busco por nombre y por categor�a 
//		for (int i = 0; i < arraListTodos.size(); i++) {
//			
//			arraListPorNombre=BuscarTodos();
//			
//			String nombre=ArrayListcontactos.get(i).getNombre();
//			
//		}
		
		
		for (int i = 0; i < ArrayListcontactos.size(); i++) {
			
			String nombre=ArrayListcontactos.get(i).getNombre();
			String telefono=ArrayListcontactos.get(i).getTelefono();
			String email=ArrayListcontactos.get(i).getEmail();
			int categoria=ArrayListcontactos.get(i).getId_Categoria();

			String direccion=ArrayListcontactos.get(i).getDireccion();
			
			/*
			* Una vez borrados los importados comparamos los nombres que tengamos en el ArrayListcontactos
			* con los que haya en la BB.DD. de la app. Si no coinciden es que es un contacto nuevo y tenemos
			* que ingresarlo en la BB.DD. de la app con la categoría de importado (6).
			* */
			String nombrequeexiste=BuscarNombre(nombre);
			
			if(!nombrequeexiste.equals(nombre)){
			
			valores.put(C_COLUMNA_NOMBRE, nombre);
			valores.put(C_COLUMNA_TELEFONO, telefono);
			valores.put(C_COLUMNA_EMAIL, email);
			valores.put(C_COLUMNA_DIRECCION, direccion);
			valores.put(C_COLUMNA_CATEGORIA, categoria);
			valores.put(C_COLUMNA_OBSERVACIONES, IMPORTADO_OBSERVACIONES+fecha);
			db.insert(C_TABLA, null, valores);
			}
			
		}
		
		db.setTransactionSuccessful();
		db.endTransaction();
		
	}
	
	// Utilizando ContentValus
	public void ImportaColeccionContent(ArrayList<String> nombres,
			ArrayList<String> telefonos, ArrayList<String> emails) {
		// public void ImportaColeccionContent(ArrayList<Contactos> datos) {

		// db.beginTransaction();
		// for (entry : listOfEntries)
		// { db.insert(entry); }
		// db.setTransactionSuccessful();
		// db.endTransaction();

		// private void insertTestData() { String sql =
		// "insert into producttable (name, description, price, stock_available) values (?, ?, ?, ?);";
		// dbHandler.getWritableDatabase(); database.beginTransaction();
		// SQLiteStatement stmt = database.compileStatement(sql); for (int i =
		// 0; i < NUMBER_OF_ROWS; i++) { //generate some values
		// stmt.bindString(1, randomName); stmt.bindString(2,
		// randomDescription); stmt.bindDouble(3, randomPrice); stmt.bindLong(4,
		// randomNumber); long entryID = stmt.executeInsert();
		// stmt.clearBindings(); } database.setTransactionSuccessful();
		// database.endTransaction(); dbHandler.close(); }

		
	

	ContentValues valores=new ContentValues();
	ContentValues valoresnombres = new ContentValues();
	ContentValues valorestelefonos = new ContentValues();
	ContentValues valoresemails = new ContentValues();
	
		Iterator iteradorNombres = nombres.iterator();
		Iterator iteradorTelefonos = telefonos.iterator();
		Iterator iteradorEmails = emails.iterator();

		db.beginTransaction();
		String telefono="";
		int contadornombres=0;
		int contadortelefonos=0;
		int contadoremails=0;
		
		
		while (iteradorNombres.hasNext()) {
			String nombre = (String) iteradorNombres.next();
			//valores.put(C_COLUMNA_NOMBRE, nombres.get(i));
			valores.put(C_COLUMNA_NOMBRE, nombre);
			//valores.put(C_COLUMNA_TELEFONO, telefono);
			 //iteradorNombres.next();
			 //db.insert(C_TABLA, null, valoresnombres);
			 contadornombres++;
			 
			 
			// TEL�FONOS
				while (iteradorTelefonos.hasNext() && (contadortelefonos== contadornombres-1)) {
					//Integer j = (Integer) iteradorTelefonos.next();
					 telefono = (String) iteradorTelefonos.next();
					//valores.put(C_COLUMNA_NOMBRE, nombre);
					//valores.put(C_COLUMNA_TELEFONO, telefono);
					//iteradorTelefonos.next();
					  
					//db.insert(C_TABLA, null, valorestelefonos);
					 contadortelefonos++;
				}//Fin tel�fonos
				

				
//				while (iteradorEmails.hasNext() && (contadoremails== contadortelefonos-1)) {
//					//Integer z = (Integer) iteradorEmails.next();
//					String email=(String) iteradorEmails.next();
//					valores.put(C_COLUMNA_EMAIL, email);
//					//iteradorEmails.next();
//					//db.insert(C_TABLA, null, valoresemails);
////					db.insert(C_TABLA, null, valores);//MAL trae emails
//					contadoremails++;
//				}
				
				valores.put(C_COLUMNA_NOMBRE, nombre);
				valores.put(C_COLUMNA_TELEFONO, telefono);
				valores.put(C_COLUMNA_EMAIL, EMAIL_POR_DEFECTO);
				//valores.put(C_COLUMNA_CATEGORIA, 4);
				valores.put(C_COLUMNA_OBSERVACIONES, IMPORTADO_OBSERVACIONES);
				db.insert(C_TABLA, null, valores);
				
			 }//fin todos
		
		//==============
		//Solo me trae  el �ltimo contacto y el �ltimo correo y el tel�fono mal...!!!
		//db.insert(C_TABLA, null, valores);NONONONONONO
		//==============
		
		
		
		db.setTransactionSuccessful();
		db.endTransaction();
	

	}
	
	
	
	

	// Ya existe un objeto db en la clase... por eso no se pasa por par�metro...
	public void ModificarContacto(int id, String Nombre, String Apellidos,
			String Direccion, String Telefono, String Email, int Id_Categ,
			String Observaciones) {

		// db.execSQL("UPDATE Contactos SET Nombre=Nombre,nombre='Nombre' WHERE codigo=_id ");

		ContentValues values = new ContentValues();
		// values.put(C_COLUMNA_ID,_id);//Es la Primary key...
		values.put(C_COLUMNA_NOMBRE, Nombre);
		values.put(C_COLUMNA_APELLIDOS, Apellidos);
		values.put(C_COLUMNA_DIRECCION, Direccion);
		values.put(C_COLUMNA_TELEFONO, Telefono);
		values.put(C_COLUMNA_EMAIL, Email);

		values.put(C_COLUMNA_CATEGORIA, Id_Categ);
		values.put(C_COLUMNA_OBSERVACIONES, Observaciones);

		// String where = "_id=?";//Cla�sula where
		String where = C_COLUMNA_ID + " = " + id;

		// String[] args = {"_id" };//Valores adicionales a la cla�sula where...

		Log.i(this.getClass().toString(), "_id" + "UPDATE_2" + id + "where "
				+ where);
		// db.update(C_TABLA, values, "_id =" + id, null);
		db.update(C_TABLA, values, where, null);
		// db.update("Contactos", values, "_id=_id", null);
	}

	// Devuelve un ArrayList de Contactos con los datos que hay en BBDD
	public ArrayList BuscarTodos() {
		Contactos contactos;
		ArrayList<Contactos> arraList = new ArrayList<Contactos>();

		Cursor rs = db
				.rawQuery("Select * from Contactos order by nombre", null);

		if (rs.moveToFirst()) {
			do {

				contactos = new Contactos(rs.getInt(0), rs.getString(1),
						rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getInt(6), rs.getString(7), rs.getInt(8), rs.getInt(9));

				arraList.add(contactos);

			} while (rs.moveToNext());

		}
		return arraList;

	}


	public ArrayList BuscarTodosBorrar() {
		ContactosBorrar contactosBorrar;
		ArrayList<ContactosBorrar> arraList = new ArrayList<ContactosBorrar>();

		Cursor rs = db
				.rawQuery("Select _id, Nombre, Telefono,Email,Id_Categoria from Contactos order by nombre", null);

		 boolean chequedado=false;

		if (rs.moveToFirst()) {
			do {

				contactosBorrar = new ContactosBorrar(rs.getInt(0), rs.getString(1),
						rs.getString(2), rs.getString(3), rs.getInt(4),
						chequedado);

				arraList.add(contactosBorrar);

			} while (rs.moveToNext());

		}
		return arraList;

	}




	public ArrayList BuscarUno(long id) {
		Contactos contactos;
		ArrayList<Contactos> arraList = new ArrayList<Contactos>();
		// Nombre, Apellidos, Direccion, Telefono, Email
		Cursor rs = db
				.rawQuery(
						"Select _id, Nombre, Apellidos, Direccion, Telefono, Email, Id_Categoria, Observaciones, Importado, Sincronizado from Contactos where _id="
								+ id, null);

		// //Nos movemos al primer registro de la consulta
		// if (rs != null) {
		// rs.moveToFirst();
		// }

		// Preguntamos si el Cursor contiene alg�n dato
		if (rs.moveToFirst())
			// Si se puede mover a la primera posici�n ser� porque
			// contenga informaci�n.

			do {

				// Recogemos toda la informaci�n

				contactos = new Contactos(rs.getInt(0), rs.getString(1),
						rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getInt(6), rs.getString(7), rs.getInt(8), rs.getInt(9));
				arraList.add(contactos);

				// int _id= rs.getInt(rs.getColumnIndex("_id"));
				// String nombre = rs.getString(rs.getColumnIndex("Nombre"));
				// String apellidos =
				// rs.getString(rs.getColumnIndex("Apellidos"));
				// String direccion =
				// rs.getString(rs.getColumnIndex("Direccion"));
				// String telefono =
				// rs.getString(rs.getColumnIndex("Telefono"));
				// String email = rs.getString(rs.getColumnIndex("Email"));
				//
				// arraList.add(new Contactos(_id,nombre,
				// apellidos,direccion,telefono,email));

			} while (rs.moveToNext());

		return arraList;
	}

	// Devuelve un cursor
	public Cursor CursorBuscarUno(long id) {

		Cursor rs = db
				.rawQuery(
						"Select _id, Nombre, Apellidos, Direccion, Telefono, Email, Id_Categoria, Observaciones from Contactos where _id="
								+ id, null);
		// Cursor c = db.query( true, C_TABLA, columnas, C_COLUMNA_ID + "=" +
		// id, null, null, null, null, null);

		// Nos movemos al primer registro de la consulta
		if (rs != null) {
			rs.moveToFirst();
		}

		// rs.close();

		return rs;

	}

	public ArrayList BuscarPorNombre(String nombre) {
		Contactos contactos;
		ArrayList<Contactos> arraList = new ArrayList<Contactos>();
		// SQLiteDatabase db = this.getReadableDatabase();// Abrimos en modo
		// lectura.
		// Nombre, Apellidos, Direccion, Telefono, Email
		Cursor rs = db
				.rawQuery(
						"Select Nombre, Apellidos, Direccion, Telefono, Email from Contactos where nombre="
								+ nombre, null);

		// Cursor c = db.query( true, C_TABLA, columnas, C_COLUMNA_ID + "=" +
		// id, null, null, null, null, null);
		// Cursor rs = db.query(true, C_TABLA, arraList, C_COLUMNA_ID + "=" +
		// id, null, null, null, null, null);

		// Nos movemos al primer registro de la consulta
		if (rs != null) {
			rs.moveToFirst();
		}

		return arraList;

	}

	
	
	public String BuscarNombre(String nombre) {
		//Contactos contactos;
		//ArrayList<Contactos> arraList = new ArrayList<Contactos>();
		// SQLiteDatabase db = this.getReadableDatabase();// Abrimos en modo
		// lectura.
		// Nombre, Apellidos, Direccion, Telefono, Email new String[] { nombre });
		
		String query="Select * from Contactos where Nombre= ?";
		String[] args=new String[] {nombre};
		Cursor rs = db.rawQuery(query, args);
		
//		Cursor rs = db
//				.rawQuery("Select Nombre from Contactos where Nombre= ?", new String[] {nombre});

		// Cursor c = db.query( true, C_TABLA, columnas, C_COLUMNA_ID + "=" +
		// id, null, null, null, null, null);
		// Cursor rs = db.query(true, C_TABLA, arraList, C_COLUMNA_ID + "=" +
		// id, null, null, null, null, null);

		// Nos movemos al primer registro de la consulta
		if (rs != null) {
			//rs.moveToFirst();
			rs.moveToFirst();
		}

		
		if(rs.getCount()>0){
		return rs.getString(1);
		}
		else{
			return "";
		}
		
	}
	
	
	
	
	/**
	 * Eliminar el registro con el identificador indicado
	 */
	public long delete(long id) {
		// if (db == null)
		// abrir();

		return db.delete(C_TABLA, "_id=" + id, null);
		// return id;
	}

	//Para borrar más de un registro...Viene del blog CodigoAlonso...Vídeo sqllite....
	public void borrarMuchos(ArrayList arrayListChequeados){



		if(arrayListChequeados != null && arrayListChequeados.size() > 0){
			//SQLiteDatabase db = getWritableDatabase();
			if(db!=null){
				db.beginTransaction();
				for(int i = 0; i < arrayListChequeados.size();i++){
					//db.execSQL(arrayListChequeados.get(i));

					db.delete(C_TABLA, "_id=" + arrayListChequeados.get(i), null);
					//db.delete(C_TABLA, C_COLUMNA_ID + "IN()", new String[]{nombre1, nombre2});
				}
				db.setTransactionSuccessful();
				db.endTransaction();
				db.close();
			}
			arrayListChequeados.clear();



		}

		//db.delete(C_TABLA,C_COLUMNA_ID +"IN(?,?)", new String[]{nombre1,nombre2});

	}


	public void borrarTodos() {
	    //SQLiteDatabase db = getWritableDatabase();
	    db.delete(C_TABLA, null, null);
	    //db.close();  
	}
	
	public void borrarImportados(String nombreantiguo) {
	    //SQLiteDatabase db = getWritableDatabase();
		 String where= "Id_Categoria  = ? OR Nombre= ?" ;
	    String[] argumentos={"5",nombreantiguo};
	    db.delete(C_TABLA,where, argumentos);
	   
	    //db.close();  
	}

	
	public void borrarImportados() {
	    //SQLiteDatabase db = getWritableDatabase();
		 String where= "Id_Categoria  = ?" ;
	    String[] argumentos={"5"};
	    db.delete(C_TABLA,where, argumentos);
	   
	    //db.close();  
	}



	
}
