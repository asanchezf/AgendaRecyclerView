package com.antonioejemplos.agendarecyclerview;


import android.app.AlertDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.sql.SQLException;

import controlador.SQLControlador;

//import com.videumcorp.desarrolladorandroid.navigatio.R;

//import antonio.ejemplos.agendacomercial.R;

public class AltaUsuarios extends AppCompatActivity {

    private EditText nombre;
    private EditText apellidos;
    private EditText direc;
    private EditText telefono;
    private EditText email;

    private RadioButton radio1, radio2, radio3, radio4;
    private EditText observaciones;

    //private Button cancelar;
    //private Button guardar;

    private SQLControlador Connection;


    private boolean validar = true;

    private Toolbar toolbar;

    // Modo del formulario
    private int modo;
    private int id_recogido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_usuarios_material);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        nombre = (EditText) findViewById(R.id.nombre);
        apellidos = (EditText) findViewById(R.id.apellidos);
        direc = (EditText) findViewById(R.id.direc);
        telefono = (EditText) findViewById(R.id.telefono);
        email = (EditText) findViewById(R.id.email);


        radio1 = (RadioButton) findViewById(R.id.radio1);
        radio2 = (RadioButton) findViewById(R.id.radio2);
        radio3 = (RadioButton) findViewById(R.id.radio3);
        radio4 = (RadioButton) findViewById(R.id.radio4);



        observaciones = (EditText) findViewById(R.id.observaciones);

        //Botones que se han ocultado
       // cancelar = (Button) findViewById(R.id.boton_cancelar);
       // guardar = (Button) findViewById(R.id.boton_guardar);

        //Añadimos la toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //toolbar.getMenu().setGroupVisible(R.menu.alta_usuarios,true);
//toolbar.setMenu(R.menu.alta_usuarios,new ActionMenuPresenter(CONTEXT_IGNORE_SECURITY));


        //La acitivity debe extender de AppCompatActivity para poder hacer el seteo a ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TypedValue typedValueColorPrimaryDark = new TypedValue();
        AltaUsuarios.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValueColorPrimaryDark, true);
        final int colorPrimaryDark = typedValueColorPrimaryDark.data;
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(colorPrimaryDark);
        }

        Bundle bundle = getIntent().getExtras();
        establecerModo(bundle.getInt(MainActivity.C_MODO));

        if (modo == MainActivity.C_EDITAR) {
//Mostramos los datos recogidos del formulario si el modo invocado es editar...

            id_recogido = bundle.getInt("_id");

            nombre.setText(bundle.getString("Nombre"));
            apellidos.setText(bundle.getString("Apellidos"));
            direc.setText(bundle.getString("Direccion"));
            telefono.setText(bundle.getString("Telefono"));
            email.setText(bundle.getString("Email"));


            if (bundle.getInt("Id_Categoria") == 1) {
                //radio1.isChecked();
                radio1.setChecked(true);
            }
            if (bundle.getInt("Id_Categoria") == 2) {
                //radio2.isChecked();
                radio2.setChecked(true);
            }
            if (bundle.getInt("Id_Categoria") == 3) {
                //radio3.isChecked();
                radio3.setChecked(true);
            }
            if (bundle.getInt("Id_Categoria") == 4) {
                //radio4.isChecked();
                radio4.setChecked(true);
            }

            observaciones.setText(bundle.getString("Observaciones"));

        }

        /*guardar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                guardar();

            }
        });*/

       /* cancelar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //Devolvemos el control y cerramos la Activity
                setResult(RESULT_CANCELED);
                finish();

            }
        });*/


    }


    private void establecerModo(int m) {
        this.modo = m;

        if (modo == MainActivity.C_VISUALIZAR) {
            this.setTitle(nombre.getText().toString());
            this.setEdicion(false);
        } else if (modo == MainActivity.C_CREAR) {
            // this.setTitle(R.string.hipoteca_crear_titulo);
            this.setEdicion(true);
        } else if (modo == MainActivity.C_EDITAR) {
            this.setTitle(R.string.agenda_editar_titulo);
            this.setEdicion(true);
        }
    }

    private void setEdicion(boolean opcion) {
//        nombre.setEnabled(opcion);
//        apellidos.setEnabled(opcion);
//        direc.setEnabled(opcion);
//        telefono.setEnabled(opcion);
//        email.setEnabled(opcion);
//
//        radio1.setEnabled(opcion);
//        radio2.setEnabled(opcion);
//        radio3.setEnabled(opcion);
//        radio4.setEnabled(opcion);
//
//
//        observaciones.setEnabled(opcion);

        // Controlamos visibilidad de botonera
       // LinearLayout v = (LinearLayout) findViewById(R.id.botonera);


       /* if (opcion)
            v.setVisibility(View.VISIBLE);

        else
            v.setVisibility(View.GONE);
*/

       /* if(modo==MainActivity.C_EDITAR){
            //m.setGroupVisible(R.id.modificar_usuario,true);



        }*/


        // Lineas para ocultar el teclado virtual (Hide keyboard)
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nombre.getWindowToken(), 0);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//Evitamos que funcione la tecla del menú que traen por defecto los samsung...
        switch (keyCode) {
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

    public void guardar() {

        String nom = nombre.getText().toString();
        String apell = apellidos.getText().toString();
        String direccion = direc.getText().toString();
        String tele = telefono.getText().toString();
        String correo = email.getText().toString();
        long Id_Categ = 0;

        if (radio1.isChecked()) {
            Id_Categ = 1;

        } else if (radio2.isChecked()) {
            Id_Categ = 2;
        } else if (radio3.isChecked()) {
            Id_Categ = 3;
        } else if (radio4.isChecked()) {
            Id_Categ = 4;
        }


        String observa = observaciones.getText().toString();

        if (modo == MainActivity.C_CREAR) {

            if (validar(validar)) {

                try {
                    Connection = new SQLControlador(getApplicationContext());//Objeto SQLControlador
                    Connection.abrirBaseDeDatos(2);
                    Connection.InsertarUsuario(nom, apell, direccion, tele, correo, Id_Categ, observa);
                    Toast.makeText(getApplicationContext(), "Se ha incluido en la agenda a "+ nom, Toast.LENGTH_SHORT).show();
                    Connection.cerrar();
                    setResult(RESULT_OK);
                    finish();
                    //Para actualizar datos en MainActivity Se va a llamar a Consultar() desde Onrestart() del com.agendacomercial.navigatio.

                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }


            }//Fin validar em modo C_CREAR

        }

        if (modo == MainActivity.C_EDITAR) {
            if (validar(validar)) {

                try {
                    Connection = new SQLControlador(getApplicationContext());//Objeto SQLControlador
                    Connection.abrirBaseDeDatos(2);

                    Connection.ModificarContacto(id_recogido, nom, apell, direccion, tele, correo, (int) Id_Categ, observa);
                    Toast.makeText(getApplicationContext(), "Se ha modificado en la agenda a " + nom, Toast.LENGTH_SHORT).show();
                    Connection.cerrar();
                    setResult(RESULT_OK);
                    finish();
                    //Para actualizar datos en MainActivity Se va a llamar a Consultar() desde Onrestart() del com.agendacomercial.navigatio.

                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }


            }//Fin validar em modo C_EDITAR

        }

    }


    private void borrar(final long id) {
		/*
		 * Borramos el registro y refrescamos la lista
		 */
        AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

        dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
        dialogEliminar.setTitle(getResources().getString(
                R.string.agenda_eliminar_titulo));
        dialogEliminar.setMessage(getResources().getString(
                R.string.agenda_eliminar_mensaje));
        dialogEliminar.setCancelable(false);

        dialogEliminar.setPositiveButton(
                getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int boton) {


                        Connection = new SQLControlador(
                                getApplicationContext());
                        try {
                            Connection.abrirBaseDeDatos(2);
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }// Escritura. Borrar

                        Connection.delete(id);

                        Toast.makeText(AltaUsuarios.this,
                                R.string.agenda_eliminar_confirmacion,
                                Toast.LENGTH_SHORT).show();
                        Connection.cerrar();
                        setResult(RESULT_OK);
                        finish();


                    }
                });

        dialogEliminar.setNegativeButton(android.R.string.no, null);

        dialogEliminar.show();
    }


    //Validaci�n para que el nombre y el teléfono no se dejen vac�os
    private boolean validar(boolean validar) {
        if ((nombre.getText().toString().equals("")) || (telefono.getText().toString().equals(""))) {
            //if (nombre.getText().toString().length() == 0){

            //Toast.makeText(getApplicationContext(), "Es obligatorio rellenar el nombre" , Toast.LENGTH_LONG).show();

            //Se prepara la alerta creando nueva instancia
            AlertDialog.Builder dialogValidar = new AlertDialog.Builder(this);
            dialogValidar.setIcon(android.R.drawable.ic_dialog_alert);//icono
            dialogValidar.setTitle(getResources().getString(R.string.agenda_crear_titulo));//T�tulo
            dialogValidar.setMessage(getResources().getString(R.string.agenda_texto_vacio));
            //Se a�ade un solo bot�n para que el usuario confirme...
            dialogValidar.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            dialogValidar.create().show();


            return false;
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (modo == MainActivity.C_EDITAR) {
            getMenuInflater().inflate(R.menu.edit, menu);
        }

        else {
            getMenuInflater().inflate(R.menu.alta, menu);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.nuevo_usuario) {

            guardar();

            return true;
        }


        if (id == R.id.modificar_usuario) {

            guardar();

            return true;
        }

        if (id == R.id.eliminar_usuario) {

            borrar(id_recogido);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
