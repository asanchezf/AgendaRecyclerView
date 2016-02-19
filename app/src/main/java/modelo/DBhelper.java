package modelo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBhelper extends SQLiteOpenHelper {
	
	private static int version = 1;
	private static String name = "Agenda.db";
	private static CursorFactory factory = null;

	private String sql = "CREATE TABLE Contactos (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "Nombre TEXT NOT NULL, "
			+ "Apellidos TEXT, "
			+ "Direccion TEXT, "
			+ "Telefono TEXT, "
			+ "Email TEXT,"
			+ "Id_Categoria INTEGER,"
			+ "Observaciones TEXT,"
			+ "Importado INTEGER,"//Campo nuevo para sincronizar con agenda android
			+ "Sincronizado INTEGER)";//Campo nuevo para sincronizar con WS REST
/*
 *Id_Categoria:1:Familia
 * 			2:Amigos	
 * 			3:Compa�eros
 * 			4:Otros
 * valorar posibilidad de crear otra tabla...
 * */
	

	public DBhelper(Context context) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Creamos la BB.DD. Agenda.db
		db.execSQL(sql);
		Log.i(this.getClass().toString(), "Tabla Contactos creada");
		
		// Insertamos en primer registro....
		//db.execSQL("INSERT INTO Contactos(Nombre) VALUES('Susana')");
		db.execSQL("INSERT INTO Contactos(Nombre,Apellidos,Direccion,Telefono,Email,Id_Categoria,Observaciones,Importado,Sincronizado) VALUES('Familia','Apellidos','Rubens, 12 M�stoles, MADRID', '676048719','susimail62@gmail.com',1,'Observaciones incluidas por defecto.Observaciones incluidas por defecto.Observaciones incluidas por defecto.',null,null)");
		db.execSQL("INSERT INTO Contactos(Nombre,Apellidos,Direccion,Telefono,Email,Id_Categoria,Observaciones,Importado,Sincronizado) VALUES('Amigo','Apellidos','Rubens, 12 M�stoles, MADRID', '659355808','antoniom.sanchezf@gmail.com',2,'Observaciones incluidas por defecto.Observaciones incluidas por defecto.',null,null)");
		db.execSQL("INSERT INTO Contactos(Nombre,Apellidos,Direccion,Telefono,Email,Id_Categoria,Observaciones,Importado,Sincronizado) VALUES('Companero','Apellidos','Rubens, 12 M�stoles, MADRID', '659355808','antoniom.sanchezf@gmail.com',3,'Observaciones incluidas por defecto.Observaciones incluidas por defecto.Observaciones incluidas por defecto.',null,null)");
		db.execSQL("INSERT INTO Contactos(Nombre,Apellidos,Direccion,Telefono,Email,Id_Categoria,Observaciones,Importado,Sincronizado) VALUES('Otros','Apellidos','Rubens, 12 M�stoles, MADRID', '659355808','antoniom.sanchezf@gmail.com',4,'Observaciones incluidas por defecto.Observaciones incluidas por defecto.',null,null)");
		db.execSQL("INSERT INTO Contactos(Nombre,Apellidos,Direccion,Telefono,Email,Id_Categoria,Observaciones,Importado,Sincronizado) VALUES('Importado Android','Apellidos','Rubens, 12 M�stoles, MADRID', '659355808','antoniom.sanchezf@gmail.com',5,'Importado Android.Observaciones incluidas por defecto.Observaciones incluidas por defecto.',1,null)");
		db.execSQL("INSERT INTO Contactos(Nombre,Apellidos,Direccion,Telefono,Email,Id_Categoria,Observaciones,Importado,Sincronizado) VALUES('Sincronizado WS','Apellidos','Rubens, 12 M�stoles, MADRID', '659355808','antoniom.sanchezf@gmail.com',5,'Importado Android.Observaciones incluidas por defecto.',null,1)");



		Log.i(this.getClass().toString(), "Datos iniciales incluyendo campos nuevos... insertados. BB.DD. creada");
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
