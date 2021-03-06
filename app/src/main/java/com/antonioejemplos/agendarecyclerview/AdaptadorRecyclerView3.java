package com.antonioejemplos.agendarecyclerview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import Beans.Contactos;

/**
 * Created by Susana on 04/02/2016.
 */
public class AdaptadorRecyclerView3 extends RecyclerView.Adapter<AdaptadorRecyclerView3.ContactosViewHolder> implements Filterable {

    private ArrayList<Contactos> items;//ArrayList de contactos
    private OnItemClickListener escucha;
    private final Context contexto;

    Contactos contactos;
    private FriendFilter friendFilter;//Clase para gestionar el filtrado
    private ArrayList<Contactos> friendList;//Contactos  sin filtrar
    private ArrayList<Contactos> filteredList;//Contactos filtrados

    private String filter = "";//Caracteres introducidos para el filtrado
    private String itemValue = "";//Nombre completo que aparece en el textview del nombre

    /**
     * <p>Returns a filter that can be used to constrain data with a filtering
     * pattern.</p>
     * <p/>
     * <p>This method is usually implemented by {@link Adapter}
     * classes.</p>
     *
     * @return a filter used to constrain data
     */
    @Override
    public Filter getFilter() {
        if (friendFilter == null) {
            friendFilter = new FriendFilter();
        }

        return friendFilter;
    }




    interface OnItemClickListener {
        public void onClick(RecyclerView.ViewHolder holder, int idPromocion, View v);
    }

    //CLASE INTERNA CON VIEWHOLDER. CONTIENE EL MANEJADOR DE EVENTOS
    public class ContactosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //Campos a mostrar en la celda
        TextView titulo;
        TextView subtitulo;
        TextView descripcion;
        TextView telefono;
        ImageView categoria;
        TextView txtObservaciones;
        Button contactar;

        ImageView ubicacion;
        TextView txtUbicacion;
        //Button ruta;
        TextView txtRuta;
        ImageView ruta;

        public ContactosViewHolder(View v) {
            super(v);

            titulo = (TextView) v.findViewById(R.id.text1);
            subtitulo = (TextView) v.findViewById(R.id.text2);
            descripcion = (TextView) v.findViewById(R.id.text3);
            categoria = (ImageView) v.findViewById(R.id.category);
            telefono = (TextView) v.findViewById(R.id.text4);
            txtObservaciones = (TextView) v.findViewById(R.id.txtobservaciones);
            contactar=(Button)v.findViewById(R.id.btncontactar);

            ubicacion= (ImageView) v.findViewById(R.id.posicionamiento);
            txtUbicacion= (TextView) v.findViewById(R.id.txtubicacion);
            ruta= (ImageView) v.findViewById(R.id.imgruta);
            txtRuta= (TextView) v.findViewById(R.id.txtruta);
            v.setOnClickListener(this);
            categoria.setOnClickListener(this);
            contactar.setOnClickListener(this);

            txtUbicacion.setOnClickListener(this);
            txtRuta.setOnClickListener(this);
            //ruta.setOnClickListener(this);


        }


        @Override
        public void onClick(View v) {

            escucha.onClick(this, obtenerIdContacto(getAdapterPosition()),v);



        }

        private int obtenerIdContacto(int posicion) {

            return (int)items.get(posicion).get_id();

           //return (int)contactos.get_id();
            //return items.getInt(contactos.get_id());

        /*    if (items != null) {



                if (items.moveToPosition(posicion)) {
                    return items.getInt(ConsultaAlquileres.ID_ALQUILER);
                } else {
                    return -1;
                }
            } else {
                return -1;
            }*/

        }


    }

    //CONSTRUCTOR DEL ADAPTADOR
    public AdaptadorRecyclerView3(ArrayList<Contactos> datos, OnItemClickListener escucha, Context contexto) {

        this.items = datos;
        this.escucha = escucha;
        this.contexto = contexto;

       // getFilter();



    }



    @Override
    public AdaptadorRecyclerView3.ContactosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fila_recyclerview2, parent,false);


        return new ContactosViewHolder(v);
    }


    @Override
    public void onBindViewHolder(AdaptadorRecyclerView3.ContactosViewHolder holder, int position) {

          contactos=items.get(position);

        /*COMIENZA A PINTAR LAS VIEWS. EL CONTROL TITULO (CONTIENE EL NOMBRE) Y CUANDO SE HA REALIZADO UN FILTRADO EN EL SEARCHRVIEW SE MODIFICA
        PONIENDO LOS CARACTERES QUE COINCIDAN CON LA BÚSQUEDA DE OTRO COLOR...*/

        if (filter.toString().equals("")) {//Antes de haber realizado alguna filtración se pintan los controles asociados al listview sin modificaciones.
            holder.titulo.setText(contactos.getNombre());//Pinta el textview normal

        }else{//Ha habido filtrado: pinta los caracteres del textview que correspondan en otro color y el resto permanece igual

            //AQUI
            itemValue = contactos.getNombre();

            int startPos = itemValue.toLowerCase(Locale.US).indexOf(filter.toLowerCase(Locale.US));
            int endPos = startPos + filter.length();

            if (startPos != -1) // This should always be true, just a sanity check
            {
                Spannable spannable = new SpannableString(itemValue);
                ColorStateList color = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});//No ponen bien los colores traidos desde res?
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, color, null);

                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.titulo.setText(spannable);//Pinta los cambios
            } else
                holder.titulo.setText(itemValue);//El resto permanece igual

        }


        holder.titulo.setText(items.get(position).getNombre());

        holder.subtitulo.setText(items.get(position).getEmail());
        holder.telefono.setText(items.get(position).getTelefono());
        holder.txtObservaciones.setText(items.get(position).getObservaciones());

        //Evento scroll en una textView
       // holder.txtObservaciones.setMovementMethod(new ScrollingMovementMethod());

        holder.categoria.setImageResource(R.drawable.imgcontacto3);
        holder.ubicacion.setImageResource(R.drawable.icono_ubicacion);

        //holder.descripcion.setText("CATEGORIA");

        if (contactos.getId_Categoria() == 1) {
            holder.descripcion.setText("Familia");
            //holder.categoria.setImageResource(R.drawable.furgopeque);
        }else if(contactos.getId_Categoria() == 2){
            holder.descripcion.setText("Amigos");
        }else if(contactos.getId_Categoria() == 3){
            holder.descripcion.setText("Compañeros");
        }else if(contactos.getId_Categoria() == 4){
            holder.descripcion.setText("Otros");
        }else if(contactos.getId_Categoria() == 5){
            holder.descripcion.setText("Importado");
        }






        //holder.categoria.setOnClickListener(holder);

    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    /**
     * Contenido en la lista según el texto de búsqueda
     */
    private class FriendFilter extends android.widget.Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            //String filter = "";
            //String itemValue = "";

            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Contactos> tempList = new ArrayList<Contactos>();

                // search content in friend list
                for (Contactos user : friendList) {//AQUI
                    if (user.getNombre().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(user);

                        filter = constraint.toString();
                        //itemValue=user.getNombre();


                    }
                }


                filterResults.count = tempList.size();
                filterResults.values = tempList;


            } else {
                filterResults.count = friendList.size();
                filterResults.values = friendList;
            }

            return filterResults;
        }

        /**
         * <p>Invoked in the UI thread to publish the filtering results in the
         * user interface. Subclasses must implement this method to display the
         * results computed in {@link #performFiltering}.</p>
         *
         * @param constraint the constraint used to filter the data
         * @param results    the results of the filtering operation
         * @see #filter(CharSequence, FilterListener)
         * @see #performFiltering(CharSequence)
         * @see FilterResults
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredList = (ArrayList<Contactos>) results.values;

            notifyDataSetChanged();



        }
    }

    }
