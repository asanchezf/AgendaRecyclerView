package com.antonioejemplos.agendarecyclerview;

/**
 * Created by Susana on 15/02/2016.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import Beans.ContactosBorrar;

//import com.videumcorp.desarrolladorandroid.navigatio.R;

/**
 * Created by Susana on 31/08/2015.
 */
public class CustomArrayAdapter_2 extends ArrayAdapter<ContactosBorrar> implements
        View.OnClickListener {


    private LayoutInflater layoutInflater;

    //private  Long[]checkmarcado;
    protected static  ArrayList checksseleccionados =new ArrayList();

    public CustomArrayAdapter_2(Context context, ArrayList<ContactosBorrar> objects) {
        super(context, 0, objects);
        layoutInflater = LayoutInflater.from(context);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // holder pattern
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();

            convertView = layoutInflater.inflate(R.layout.rows_borrar_agenda, null);

            holder.setTitulo((TextView) convertView.findViewById(R.id.text1));
            holder.setSubtitulo((TextView) convertView.findViewById(R.id.text2));
            holder.setDescripcion((TextView) convertView.findViewById(R.id.text3));
            holder.setTelefono((TextView) convertView.findViewById(R.id.text4));
            holder.setCheckBox((CheckBox) convertView.findViewById(R.id.checkBox));

            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        final ContactosBorrar contactosBorrar = getItem(position);

        holder.getTitulo().setText(contactosBorrar.getNombre());
        holder.getSubtitulo().setText(contactosBorrar.getEmail());

        //holder.getDescripcion().setText(contactosBorrar.getId_Categoria());
        holder.getTelefono().setText(contactosBorrar.getTelefono());

        //Se gestiona la pulsación en el checkBox
        holder.getCheckBox().setTag(position);
        holder.getCheckBox().setChecked(contactosBorrar.isChecked());
        holder.getCheckBox().setOnClickListener(this);

        if(contactosBorrar.getId_Categoria()==1){
            //descripcion.setText("Alcorcón");
            holder.getDescripcion().setText(R.string.categoria1);

            //categoria.setImageResource(R.drawable.furgopeque);
        }
        else if (contactosBorrar.getId_Categoria()==2){
            //descripcion.setText("Madrid capital");
            //categoria.setImageResource(R.drawable.furgonew);
            holder.getDescripcion().setText(R.string.categoria2);

        }else if (contactosBorrar.getId_Categoria()==3){
            //descripcion.setText("Madrid CC.AA.");
            //categoria.setImageResource(R.drawable.trolle);
            holder.getDescripcion().setText(R.string.categoria3);

        }else if (contactosBorrar.getId_Categoria()==4){
            //descripcion.setText("Otra CC.AA.");
            //categoria.setImageResource(R.drawable.train);
            holder.getDescripcion().setText(R.string.categoria4);
        }

        else if (contactosBorrar.getId_Categoria()==5){
            //descripcion.setText("Otro país");
            //categoria.setImageResource(R.drawable.mundo);
            holder.getDescripcion().setText(R.string.categoria5);
        }
//
//
//        else if (contactosBorrar.getId_Categoria()==6){
//            //descripcion.setText("SIN ZONA");
//            //categoria.setImageResource(R.drawable.importado);
//            holder.getDescripcion().setText("SIN ZONA");
//        }


        //CheckBox checkBox1 = (CheckBox) convertView;
        //int nItems = checkBox1.get(contactosBorrar.get_id());



        return convertView;
    }


    @Override
    public void onClick(View v) {

//////////////////////Esto es lo que había en el ejemplo...
        CheckBox checkBox = (CheckBox) v;
        //int position = (Integer) v.getTag();
        //getItem(position).setChecked(checkBox.isChecked());
        //String msg = this.getContext().getString(R.string.check_toast, position, checkBox.isChecked());//DE MOMENTO SOLO ES UN WARNING
        //Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();

/////////////////////



        int position = (Integer) v.getTag();//Lo que devuelve el check
        ContactosBorrar seleccionados=getItem(position);//Asociamos el array con la posicion que nos devuelve el check
        //position=(Integer)v.getTag((int) seleccionados.get_id());

        getItem(position).setChecked(checkBox.isChecked());//Para que mantenga los elementos chequeados al movernos por la listview al hacer scroll

        // String contacto_nombre= getItem(position).getNombre();//Recuperamos el nombre
        long contacto_id=getItem(position).get_id();//Recuperamos el id


        //Toast.makeText(this.getContext(),contacto_nombre+contacto_id, Toast.LENGTH_SHORT).show();



        //ArrayList Definido como variable miembre
        // Añadimos nodos y creamos un Iterator
        checksseleccionados.add(contacto_id);


        //Iterator  iterador2 = lista2.iterator();
        // Recorremos y mostramos la lista
        //while (iterador2.hasNext()) {

        //  String elemento = (String) iterador2.next();
        //System.out.print(elemento + " "); }
        //System.out.println("--ArrayList--");

    }



    static class Holder {
        TextView titulo;
        TextView subtitulo;
        TextView descripcion;
        //ImageView categoria = (ImageView)listItemView.findViewById(R.id.category);//Por src de su layout tien una imagen por defecto.
        TextView telefono;
        //TextView textViewTitle;
        // TextView textViewSubtitle;
        CheckBox checkBox;


        public TextView getTitulo() {
            return titulo;
        }

        public void setTitulo(TextView titulo) {
            this.titulo = titulo;
        }

        public TextView getSubtitulo() {
            return subtitulo;
        }

        public void setSubtitulo(TextView subtitulo) {
            this.subtitulo = subtitulo;
        }

        public TextView getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(TextView descripcion) {
            this.descripcion = descripcion;
        }

        public TextView getTelefono() {
            return telefono;
        }

        public void setTelefono(TextView telefono) {
            this.telefono = telefono;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }
    }
}
