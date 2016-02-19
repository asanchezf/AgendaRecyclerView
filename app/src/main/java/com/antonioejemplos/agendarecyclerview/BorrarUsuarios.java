package com.antonioejemplos.agendarecyclerview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

import Beans.ContactosBorrar;
import controlador.SQLControlador;

//import com.videumcorp.desarrolladorandroid.navigatio.R;


public class BorrarUsuarios extends AppCompatActivity {

    private ListView lista;// OBJETO LISTVIEW
    private int index;//Para preservar el scroll de la listview
    private SQLControlador dbConnection;//CONTIENE LAS CONEXIONES A BBDD (CREADA EN DBHELPER.CLASS) Y LOS M�TODOS INSERT, UPDATE, DELETE, BUSCAR....
    private ArrayList<ContactosBorrar> contactosBorrar;//COLECCION DE TIPO CONTACTOS (BEAN CON LA MISMA ESTRUTURA DE CAMPOS QUE LA BBDD)

    //ADAPTADORES....===========================================================

    //ArrayAdapter<ContactosBorrar> adaptador;//Primer adaptador utilizado. EJEMPLO 1


    //private ContactosAdapter contactosAdapter;// Segundo adaptador utilizado.Ejemplo sin im�genes..
    //private ContactosAdapter_old contactosAdapter_Jarroba;// Ejemplo con im�genes.
    //private ContactosAdapter_Imagenes contactosAdapter_imagenes;
    private CustomArrayAdapter_2 contactosAdapter_borrar;

    //Añadimos la toolbar
    private Toolbar toolbar;
    //private ArrayAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_borrar_agenda);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        lista = (ListView) findViewById(android.R.id.list);

        lista.setDividerHeight(5);//Líneas más anchas entre los itens de la lista

        //Añadimos la toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //La acitivity debe extender de AppCompatActivity para poder hacer el seteo a ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TypedValue typedValueColorPrimaryDark = new TypedValue();
        BorrarUsuarios.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValueColorPrimaryDark, true);
        final int colorPrimaryDark = typedValueColorPrimaryDark.data;
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(colorPrimaryDark);
        }



        consultarParaBorrar();
        //lista.setTextFilterEnabled(true);Activa el filtrado por texto al salir el teclado...

        lista.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(BorrarUsuarios.this,
                //      contactosBorrar.get(position).getNombre()+contactosBorrar.get(position).get_id()
                //    , Toast.LENGTH_SHORT)
                //  .show();
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//Evitamos que funcione la tecla del menú que traen por defecto los samsung...
        switch(keyCode) {
            case KeyEvent.KEYCODE_MENU:
                // Toast.makeText(this, "Menú presionado",
                //       Toast.LENGTH_LONG);
                //toolbar.canShowOverflowMenu();
                //toolbar.setFocusable(true);
                //toolbar.collapseActionView();



                return true;
        }

        return super.onKeyUp(keyCode, event);
    }
    private void borrarUsuarios(){

        /*
		 * Borramos los registros y refrescamos la lista
		 */

        AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

        dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
        dialogEliminar.setTitle(getResources().getString(
                R.string.agenda_eliminar_muchos_titulo));
        dialogEliminar.setMessage(getResources().getString(
                R.string.agenda_eliminar_muchos_mensaje));
        dialogEliminar.setCancelable(false);

        dialogEliminar.setPositiveButton(
                getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int boton) {


                        ArrayList arrayListChequeados = new ArrayList();
                        arrayListChequeados = CustomArrayAdapter_2.checksseleccionados;//Nos devuelve un arraylist con los elementos seleccionados en los checks de la lista

                        dbConnection = new SQLControlador(getApplicationContext());
                        try {
                            dbConnection.abrirBaseDeDatos(2);//Modo escritura para el delete
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        dbConnection.borrarMuchos(arrayListChequeados);

                        Toast.makeText(BorrarUsuarios.this,
                                R.string.agenda_eliminar_muchos_confirmacion,
                                Toast.LENGTH_SHORT).show();
                        //dbConnection.cerrar();
                        //consultarParaBorrar();
                        dbConnection.cerrar();
                        setResult(RESULT_OK);
                        finish();

                      /*  finish();
                        Intent intent=new Intent(BorrarUsuarios.this, MainActivity.class);

                        startActivity(intent);*/
                    }
                });

        dialogEliminar.setNegativeButton(android.R.string.no, null);

        dialogEliminar.show();



    }


    private void consultarParaBorrar() {


        dbConnection = new SQLControlador(getApplicationContext());
        try {
            dbConnection.abrirBaseDeDatos(1);//Modo lectura
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// Lectura. Solo para ver


        contactosBorrar = dbConnection.BuscarTodosBorrar();// llamamos a BuscarTodos() que devuelve un arraylist de contactos...


        contactosAdapter_borrar = new CustomArrayAdapter_2(this, contactosBorrar);

        //adaptador=new ArrayAdapter(this,android.R.layout.simple_list_item_2,contactosBorrar);

        //int total=contactos.size();


        lista.setAdapter(contactosAdapter_borrar);

        dbConnection.cerrar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_borrar_usuarios, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_borrar_muchos) {


            this.borrarUsuarios();


            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
    		/*
    		 Se llama cuando la actividad va a comenzar a interactuar con el usuario.
    		 Es un buen lugar para lanzar las animaciones y la m�sica.
    		 *
    		 * */

        super.onResume();

        //mp.start();

        //Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();

        //Para preserver el scroll del listview
        if(lista != null) {
            if (lista.getCount() > index) lista.setSelectionFromTop(index, 0);
            else lista.setSelectionFromTop(0, 0);
        }

    }



    @Override
    protected void onPause() {
    	/*
    	 * Indica que la actividad est� a punto de ser lanzada a segundo plano, normalmente porque otra actividad es lanzada.
    	 * Es el lugar adecuado para detener animaciones, m�sica o almacenar los datos que estaban en edici�n.
    	 *
    	 * */
        //mp.stop();

        //Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        super.onPause();

        //Para preserver el scroll del listView
        index = lista.getFirstVisiblePosition(); // store index using shared preferences
    }

}
