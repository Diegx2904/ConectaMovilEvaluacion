package com.example.conectamovilevaluacion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    private EditText txtid, txtnom, txtape, txttel;
    private Button btnbus, btnmod, btnreg, btneli;
    private ImageView volver,perfil,mensajes;
    private ListView lvDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        txtid   = (EditText) findViewById(R.id.txtid);
        txtnom  = (EditText) findViewById(R.id.txtnom);
        txtape = (EditText) findViewById(R.id.txtape);
        txttel = (EditText) findViewById(R.id.txttel);
        volver = (ImageView) findViewById(R.id.imgVolver);
        perfil = (ImageView) findViewById(R.id.imgPerfil);
        mensajes = (ImageView) findViewById(R.id.imgMensajes);
        btnbus  = (Button)   findViewById(R.id.btnbus);
        btnmod  = (Button)   findViewById(R.id.btnmod);
        btnreg  = (Button)   findViewById(R.id.btnreg);
        btneli  = (Button)   findViewById(R.id.btneli);
        lvDatos = (ListView) findViewById(R.id.lvDatos);

        botonBuscar();
        botonModificar();
        botonRegistrar();
        botonEliminar();
        listarContactos();
        volver();
        perfil();
        mensajes();

    }
    private void botonBuscar(){
        btnbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtid.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(Menu.this, "Ingrese el ID que desea buscar", Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtid.getText().toString());

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Contacto.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String aux = Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()){
                                if (aux.equalsIgnoreCase(x.child("id").getValue().toString())){
                                    res = true;
                                    ocultarTeclado();
                                    txtnom.setText(x.child("nombre").getValue().toString());
                                    txtape.setText(x.child("apellido").getValue().toString());
                                    txttel.setText(x.child("telefono").getValue().toString());
                                    break;
                                }
                            }
                            if (res == false){
                                ocultarTeclado();
                                Toast.makeText(Menu.this, "ID ("+aux+") no se ha encontrado", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });
    }

    private void botonModificar(){
        btnmod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtid.getText().toString().trim().isEmpty() || txtnom.getText().toString().trim().isEmpty() || txtape.getText().toString().trim().isEmpty() || txttel.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(Menu.this,"Complete los campos faltantes para actualizar",Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtid.getText().toString());
                    String nom = txtnom.getText().toString();
                    String ape = txtape.getText().toString();
                    String tel = txttel.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Contacto.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            boolean res2 = false;
                            for (DataSnapshot x : snapshot.getChildren()){
                                if (x.child("nombre").getValue().toString().equalsIgnoreCase(nom)){
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(Menu.this, "El Nombre ("+nom+") ya existe, no es posible modificar", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            boolean res3 = false;
                            for (DataSnapshot x : snapshot.getChildren()){
                                if (x.child("apellido").getValue().toString().equalsIgnoreCase(ape)){
                                    res3 = true;
                                    ocultarTeclado();
                                    Toast.makeText(Menu.this, "El Apellido ("+ape+") ya existe, no es posible modificar", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            boolean res4 = false;
                            for (DataSnapshot x : snapshot.getChildren()){
                                if (x.child("telefono").getValue().toString().equalsIgnoreCase(tel)){
                                    res4 = true;
                                    ocultarTeclado();
                                    Toast.makeText(Menu.this, "El Telefono ("+tel+") ya existe, no es posible modificar", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            if (res2 == false && res3 == false && res4 == false){

                                String aux = Integer.toString(id);
                                boolean res = false;
                                for (DataSnapshot x : snapshot.getChildren()){
                                    if (x.child("id").getValue().toString().equalsIgnoreCase(aux)){
                                        res = true;
                                        ocultarTeclado();
                                        x.getRef().child("nombre").setValue(nom);
                                        x.getRef().child("apellido").setValue(ape);
                                        x.getRef().child("telefono").setValue(tel);
                                        txtid.setText("");
                                        txtnom.setText("");
                                        txtape.setText("");
                                        txttel.setText("");
                                        listarContactos();
                                        break;
                                    }
                                }

                                if (res == false){
                                    ocultarTeclado();
                                    Toast.makeText(Menu.this,"ID ("+aux+") no encontrado, no es posible modificar",Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtnom.setText("");
                                    txtape.setText("");
                                    txttel.setText("");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Menu.this,"Contacto no se ha registrado",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void botonRegistrar(){
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtid.getText().toString().trim().isEmpty() || txtnom.getText().toString().trim().isEmpty() || txtape.getText().toString().trim().isEmpty() || txttel.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(Menu.this,"Complete los campos faltantes",Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtid.getText().toString());
                    String nom = txtnom.getText().toString();
                    String ape = txtape.getText().toString();
                    String tel = txttel.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Contacto.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String aux = Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()){
                                if (x.child("id").getValue().toString().equalsIgnoreCase(aux)){
                                    res = true;
                                    ocultarTeclado();
                                    Toast.makeText(Menu.this, "Error el id ("+aux+") ya existe", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            boolean res2 = false;
                            for (DataSnapshot x : snapshot.getChildren()){
                                if (x.child("nombre").getValue().toString().equalsIgnoreCase(nom)){
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(Menu.this, "Error el nombre ("+nom+") ya existe", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            boolean res3 = false;
                            for (DataSnapshot x : snapshot.getChildren()){
                                if (x.child("apellido").getValue().toString().equalsIgnoreCase(ape)){
                                    res3 = true;
                                    ocultarTeclado();
                                    Toast.makeText(Menu.this, "Error el apellido ("+ape+") ya existe", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            boolean res4 = false;
                            for (DataSnapshot x : snapshot.getChildren()){
                                if (x.child("telefono").getValue().toString().equalsIgnoreCase(tel)){
                                    res4 = true;
                                    ocultarTeclado();
                                    Toast.makeText(Menu.this, "Error el telefono ("+tel+") ya existe", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            if (res == false && res2 == false && res3 == false && res4 == false){
                                Contacto con = new Contacto(id,nom,ape,tel);
                                dbref.push().setValue(con);
                                ocultarTeclado();
                                Toast.makeText(Menu.this,"Contacto registrado correctamente",Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtnom.setText("");
                                txtape.setText("");
                                txttel.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Menu.this,"Contacto no se ha registrado",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private void listarContactos(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbref = db.getReference(Contacto.class.getSimpleName());

        ArrayList<Contacto> liscon = new ArrayList<Contacto>();
        ArrayAdapter<Contacto> ada = new ArrayAdapter<Contacto>(Menu.this, android.R.layout.simple_list_item_1, liscon);
        lvDatos.setAdapter(ada);

        dbref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Contacto con = snapshot.getValue(Contacto.class);
                liscon.add(con);
                ada.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ada.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        lvDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contacto con = liscon.get(position);
                AlertDialog.Builder a = new AlertDialog.Builder(Menu.this);
                a.setCancelable(true);
                a.setTitle("Contacto Seleccionado");

                String msg = "ID : " + con.getId() + "\n\n";
                msg += "NOMBRE : " + con.getNombre() + "\n\n";
                msg += "APELLIDO : " + con.getApellido() + "\n\n";
                msg += "TELEFONO : " + con.getTelefono() + "\n\n";

                a.setMessage(msg);
                a.show();
            }
        });
    }
    private void botonEliminar(){
        btneli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtid.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(Menu.this, "Ingrese el ID que desea eliminar", Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtid.getText().toString());

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Contacto.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String aux = Integer.toString(id);
                            final boolean[] res = {false};
                            for (DataSnapshot x : snapshot.getChildren()){
                                if (aux.equalsIgnoreCase(x.child("id").getValue().toString())){

                                    AlertDialog.Builder a = new AlertDialog.Builder(Menu.this);
                                    a.setCancelable(false);
                                    a.setTitle("Pregunta");
                                    a.setMessage("¿Estas seguro de querer eliminar esta registro?");

                                    a.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            res[0] = true;
                                            ocultarTeclado();
                                            x.getRef().removeValue();
                                            listarContactos();
                                        }
                                    });
                                    a.show();
                                    break;
                                }
                            }
                            if (res[0] == false){
                                ocultarTeclado();
                                Toast.makeText(Menu.this, "ID ("+aux+") no se ha encontrado, no fue posible eliminar", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
    private void ocultarTeclado(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void perfil(){
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this,Perfil.class);
                startActivity(intent);
            }
        });
    }
    private void volver(){
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void mensajes(){
        mensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, SendMessageActivity.class);
                startActivity(intent);
            }
        });
    }
}