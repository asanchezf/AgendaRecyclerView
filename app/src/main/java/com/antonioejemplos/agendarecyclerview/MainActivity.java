package com.antonioejemplos.agendarecyclerview;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

import Beans.Contactos;
import controlador.SQLControlador;

public class MainActivity extends AppCompatActivity implements AdaptadorRecyclerView3.OnItemClickListener {


    //CONSTANTES PARA EL MODO FORMULARIO Y PARA LOS TIPOS DE LLAMADA.============================
    public static final String C_MODO = "modo";
    public static final int C_VISUALIZAR = 551;
    public static final int C_CREAR = 552;
    public static final int C_EDITAR = 553;
    public static final int C_ELIMINAR = 554;
   // public static final int C_CALL = 555;
    //FIN CONSTANTES==============================================================================

    private RecyclerView lista;

    private AdaptadorRecyclerView3 adaptador;
    private FloatingActionButton btnFab;
    private CollapsingToolbarLayout ctlLayout;
    private SQLControlador dbConnection;//CONTIENE LAS CONEXIONES A BBDD (CREADA EN DBHELPER.CLASS) Y LOS M�TODOS INSERT, UPDATE, DELETE, BUSCAR....
    private ArrayList<Contactos> contactos;

    private static long back_pressed;//Contador para cerrar la app al pulsar dos veces seguidas el btón de cerrar. Se gestiona en el evento onBackPressed

    private static int index = -1;
    private static int top = -1;
    private LinearLayoutManager llmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //App bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Mi Aplicación");

        // Configuración del RecyclerView-----------------------------
        lista = (RecyclerView) findViewById(R.id.lstLista);

        //lista.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));//Layout para el RecyclerView

        llmanager = new LinearLayoutManager(this);
        llmanager.setOrientation(LinearLayoutManager.VERTICAL);
        lista.setLayoutManager(llmanager);





        consultar();


        //Floating Action Button
        btnFab = (FloatingActionButton) findViewById(R.id.btnFab);
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                crear();

            }
        });

        ctlLayout = (CollapsingToolbarLayout) findViewById(R.id.ctlLayout);
        ctlLayout.setTitle("Mi Agenda");

    }



    @Override
    protected void onRestart() {
    		/*
    		 *  Indica que la actividad va a volver a ser representada despu�s de haber pasado por onStop().*/

        super.onRestart();
        //Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();

        consultar();

    }


   @Override
    protected void onResume() {
        super.onResume();

       //Para preserver el scroll del listView
       //Establecer variables en onCreate (), guardar posición en onPause () y ajuste la posición de desplazamiento desplazarse en onResume ()
        if(index != -1) { llmanager.scrollToPositionWithOffset( index, top); }


    }

    @Override
    protected void onPause() {

//    	 Indica que la actividad est� a punto de ser lanzada a segundo plano, normalmente porque otra actividad es lanzada.
//    	 Es el lugar adecuado para detener animaciones, m�sica o almacenar los datos que estaban en edici�n.


        //Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        super.onPause();


        //Para preserver el scroll del Recyclerview;
        //Establecer variables en onCreate (), guardar posición en onPause () y ajuste la posición de desplazamiento desplazarse en onResume ()

        index = llmanager.findFirstVisibleItemPosition();
        View v = lista.getChildAt(0);
        //top = (v == null) ? 0 : (v.getTop() - lista.getPaddingTop());

        if(v == null){
            top=0;
        }
        else{
            top=v.getTop() - lista.getPaddingTop();
        }
    }

    private void consultar() {


        dbConnection = new SQLControlador(getApplicationContext());
        try {
            dbConnection.abrirBaseDeDatos(1);//Modo lectura
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// Lectura. Solo para ver


        contactos = dbConnection.BuscarTodos();// llamamos a BuscarTodos() que devuelve un arraylist de contactos...


        adaptador = new AdaptadorRecyclerView3(contactos, this, this);//IMplementa el adapatador: pasamos ahora tres parámetros....
        //lista.setAdapter(adaptador);

        //DECORACIÓN Y ANIMACIÓN DEL RECYCLERVIEWw. Se define en una clase aparte...

        /*lista.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        lista.setItemAnimator(new DefaultItemAnimator());*/


        //int total=contactos.size();

        //lista.setAdapter( new ContactosAdapter_Imagenes(this,contactos));

        //concatena_numero_registros=getResources().getString(R.string.concatenar_numero_registros);//DEvuelve HAY para concatenar y  mostrar el número total de registros...
        //totalRegistros=  contactosAdapter_imagenes.getCount();
        //adaptador.getItemCount();

        //totales= String.valueOf(totalRegistros);


//        if(totalRegistros>0) {
//            txtTotales.setText(concatena_numero_registros+ " " + totales + " " + getResources().getString(R.string.titulo_activity_lista));
//        }
//
//        else{
//            txtTotales.setText(getResources().getString(R.string.no_hay_registros));
//        }

        //lista.setAdapter(contactosAdapter_imagenes);
        lista.setAdapter(adaptador);
        dbConnection.cerrar();
    }


    //Método que realiza la llamada telefónica.
    public void call(long id, View view) throws SQLException {

        dbConnection = new SQLControlador(getApplicationContext());
        dbConnection.abrirBaseDeDatos(1);// Lectura. Solo para ver
        Cursor c = dbConnection.CursorBuscarUno(id);// Devuelve un Cursor


        String telefono = c.getString(c.getColumnIndex("Telefono"));

        dbConnection.cerrar();


        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telefono));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //View v = null;
            Snackbar snack = Snackbar.make(view, R.string.agenda_permiso_llamadas, Snackbar.LENGTH_LONG);
            ViewGroup group = (ViewGroup) snack.getView();
            group.setBackgroundColor(getResources().getColor(R.color.md_deep_orange_700));
            snack.show();
            return;
        }
        startActivity(intent);

    }


    @Override
    public void onClick(RecyclerView.ViewHolder holder, final int idPromocion,final View view) {//idPromocion y View se definen como final pq son llamada desde la clase interna del evento onclick() del AlertDialog
 /*
    * onClick es un método obligado a implementarse pq MainActivity implements AdaptadorRecyclerView3 que cuenta con la
    * interface OnItemClickListener
    * */
        Log.i("Demo Recycler", "Se ha pulsado en la siguiente view: " + holder);

        //Pulsando en el icono de la categoría...
        if (view.getId() == R.id.category) {
            //Toast.makeText(MainActivity.this, "Se ha pulsado en categoría: " + idPromocion + " " + holder, Toast.LENGTH_SHORT).show();
        }

        //Pulsando en en el btn de llamar se abre el dialer para llamar al contacto seleccionado
        else if (view.getId() == R.id.btncontactar) {
            AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

            dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
            dialogEliminar.setTitle(getResources().getString(
                    R.string.agenda_call_titulo));
            dialogEliminar.setMessage(getResources().getString(
                    R.string.agenda_call_mensaje));
            dialogEliminar.setCancelable(false);

            dialogEliminar.setPositiveButton(
                    getResources().getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int boton) {

            try {
                call(idPromocion,view);
            } catch (SQLException e) {
                e.printStackTrace();
            }

                        }
                    });

            dialogEliminar.setNegativeButton(android.R.string.no, null);

            dialogEliminar.show();


        }//Fin else if

        else if (view.getId() == R.id.txtubicacion) {
            //
            // Toast.makeText(MainActivity.this, "Se ha pulsado en ubicación: " + idPromocion + " " + holder, Toast.LENGTH_SHORT).show();

            //NO FUNCIONA
            /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
            startActivity(intent);*/


            //INICIA NAVEGACIÓN DESDE LA UBICACIÓN ACTUAL A LA DIRECCIÓN INTRODUCIDA....
       /*     Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=an+Mestizaje, 2+Alcorcon"));
            startActivity(intent);*/

            try {
                visitar(idPromocion);
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

        //Pulsando en otra parte de los CardView distinta se abre la Activity para editar o eliminar el contacto
        else{
            //Toast.makeText(MainActivity.this, "Se ha pulsado en la celda: " + idPromocion + " " + holder, Toast.LENGTH_SHORT).show();
            try {
                editar(idPromocion);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void visitar(int idPromocion) throws SQLException {

        dbConnection = new SQLControlador(getApplicationContext());
        dbConnection.abrirBaseDeDatos(1);// Lectura. Solo para ver

        Cursor c = dbConnection.CursorBuscarUno(idPromocion);// Devuelve un Cursor

        String direccion = c.getString(c.getColumnIndex("Direccion"));

        dbConnection.cerrar();

        if(direccion.equals("")){

            //Toast.makeText(MainActivity.this,"Este contacto no tiene ninguna dirección asignada..!",Toast.LENGTH_LONG).show();

            Snackbar snack = Snackbar.make(lista, R.string.agenda_contacto_sin_direccion, Snackbar.LENGTH_LONG);
            ViewGroup group = (ViewGroup) snack.getView();
            group.setBackgroundColor(getResources().getColor(R.color.md_deep_orange_500));
            snack.show();


        }
        else {
            //Uri.parse("google.navigation:q=an+Mestizaje, 2+Alcorcon"));
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=" + direccion));
            startActivity(intent);
        }
    }


    private void crear() {
        Intent i = new Intent(MainActivity.this, AltaUsuarios.class);
        i.putExtra(C_MODO, C_CREAR);
        startActivityForResult(i, C_CREAR);
    }

    private void editar(long id) throws SQLException {
        dbConnection = new SQLControlador(getApplicationContext());
        dbConnection.abrirBaseDeDatos(1);// Lectura. Solo para ver

        Cursor c = dbConnection.CursorBuscarUno(id);// Devuelve un Cursor

        int idenviado= c.getInt(c.getColumnIndex("_id"));
        String nombre = c.getString(c.getColumnIndex("Nombre"));
        String apellidos = c.getString(c.getColumnIndex("Apellidos"));
        String direccion = c.getString(c.getColumnIndex("Direccion"));
        String telefono = c.getString(c.getColumnIndex("Telefono"));
        String email = c.getString(c.getColumnIndex("Email"));

        int Id_Categ=c.getInt(c.getColumnIndex("Id_Categoria"));
        String observ = c.getString(c.getColumnIndex("Observaciones"));


        dbConnection.cerrar();

        // Pasamos datos al formulario en modo visualizar
        Intent i = new Intent(MainActivity.this, AltaUsuarios.class);
        i.putExtra("_id", idenviado);
        i.putExtra("Nombre", nombre);
        i.putExtra("Apellidos", apellidos);
        i.putExtra("Direccion", direccion);
        i.putExtra("Telefono", telefono);
        i.putExtra("Email", email);

        i.putExtra("Id_Categoria", Id_Categ);
        i.putExtra("Observaciones", observ);

        i.putExtra(C_MODO, C_EDITAR);
        startActivityForResult(i, C_EDITAR);

    }




    public  void borrarTodos() {
		/*
		 * Borramos todos los registros y refrescamos el recyclerView
		 */
        AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

        dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
        dialogEliminar.setTitle(getResources().getString(
                R.string.agenda_eliminar_todos_titulo));
        dialogEliminar.setMessage(getResources().getString(
                R.string.agenda_eliminar_todos_mensaje));
        dialogEliminar.setCancelable(false);

        dialogEliminar.setPositiveButton(
                getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int boton) {

                        dbConnection = new SQLControlador(
                                getApplicationContext());
                        try {
                            dbConnection.abrirBaseDeDatos(2);
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }// Escritura. Borrar

                        dbConnection.borrarTodos();

                        Toast.makeText(MainActivity.this,
                                R.string.agenda_eliminar_todos_confirmacion,
                                Toast.LENGTH_SHORT).show();
                        dbConnection.cerrar();
                        consultar();
                    }
                });

        dialogEliminar.setNegativeButton(android.R.string.no, null);

        dialogEliminar.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_traer_contactos) {
            //MIGRAR DATOS DE LA AGENDA DE ANDROID=========================
            Intent i = new Intent(this, ImportarContactos.class);
            //startActivityForResult(i,C_CREAR);
            startActivity(i);


            return true;
        }

        if (id == R.id.menu_borrar_todos) {

            borrarTodos();
            return true;
        }

        if (id == R.id.menu_borrar_algunos) {
            //finish();
            Intent intent=new Intent(MainActivity.this,BorrarUsuarios.class);
            intent.putExtra(C_MODO, C_ELIMINAR);
            //startActivity(intent);
            startActivityForResult(intent, C_ELIMINAR);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch(keyCode) {
            //Evitamos que funcione la tecla del menú que traen por defecto los samsung...
            case KeyEvent.KEYCODE_MENU:
                // Toast.makeText(this, "Menú presionado",
                //       Toast.LENGTH_LONG);
                //toolbar.canShowOverflowMenu();
                //toolbar.setFocusable(true);
                //toolbar.collapseActionView();

                return true;

            /*case KeyEvent.KEYCODE_BACK:
                //int contadorsalida=0;
                //Toast.makeText(this,"Has pulsado tecla atras",Toast.LENGTH_SHORT).show();
                contadorsalida++;
                Toast.makeText(this,"Pulsa otra vez para salir",Toast.LENGTH_SHORT).show();
                if(contadorsalida==2){
                    finish();
                }*/
        }

        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onBackPressed() {
/**
 * Cierra la app cuando se ha pulsado dos veces seguidas en un intervalo inferior a dos segundos.
 */

        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), R.string.agenda_salir, Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
       // super.onBackPressed();
    }
}
