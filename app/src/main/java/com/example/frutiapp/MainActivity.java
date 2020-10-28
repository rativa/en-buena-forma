package com.example.frutiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Nombres para los objetos de los componentes
    private EditText et_nombre;
    private EditText et_altura;
    private EditText et_peso;
    private ImageView iv_personaje;
    private TextView tv_bestScore;
    private MediaPlayer mp;

    //Casting para retornar número aleatorio y así poder cambiar las imagenes aleatorias
    int num_aleatorio = (int) (Math.random() * 2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Relación entre la parte lógica y la parte gráfica
        et_nombre = (EditText)findViewById(R.id.txt_nombre);
        et_altura = (EditText)findViewById(R.id.txt_altura);
        et_peso = (EditText)findViewById(R.id.txt_peso);
        iv_personaje = (ImageView)findViewById(R.id.imageview_Personaje);
        tv_bestScore = (TextView)findViewById(R.id.textView_BestScore);

        //Icono de la aplicación en actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //Qué personaje tiene que mostrar

        int id;

        if(num_aleatorio == 0 ){
            //GETIDENTIFIER nos permite optener la ruta de la imagen a cambiar
            id = getResources().getIdentifier("pesa", "drawable" , getPackageName());
            iv_personaje.setImageResource(id);
        } else if(num_aleatorio == 1) {
            //GETIDENTIFIER nos permite optener la ruta de la imagen a cambiar
            id = getResources().getIdentifier("abdominales", "drawable", getPackageName());
            iv_personaje.setImageResource(id);
        }

        //Conexión a base de datos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"BD", null,1);
        SQLiteDatabase BD = admin.getWritableDatabase();

        //Consulta a la base de datos
        Cursor consulta = BD.rawQuery(
          "select * from puntaje where score = (select max(score) from puntaje)", null);
        //Si encontro algo
        if(consulta.moveToFirst()){
            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);
            tv_bestScore.setText("Record: " + temp_score + " de " + temp_nombre);
            BD.close();
        } else {
            BD.close();
        }

        //Sonido del juego
        mp = MediaPlayer.create(this, R.raw.alphabet_song);
        mp.start();
        mp.setLooping(true);

    }

    //Método para el bóton jugar
    public void Jugar(View view){
        //Validar que escribio el nombre
        String nombre = et_nombre.getText().toString();
        String altura = et_altura.getText().toString();
        String peso = et_peso.getText().toString();

        if(!nombre.equals("") && !altura.equals("") && !peso.equals("")){
            mp.stop();//Detener la canción
            mp.release();//Destruir objeto de audio

            //Pasar al siguiente activity
            Intent intent = new Intent(this, Main2Activity_Nivel1.class);

            //Pasar el nombre del jugador
            intent.putExtra("jugador", nombre);
            intent.putExtra("jugadorAltura", altura);
            intent.putExtra("jugadorPeso", peso);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this,"Debes ingresar los datos", Toast.LENGTH_SHORT).show();

            //Abrir el teclado para que ingrese el nombre
            et_nombre.requestFocus();
            InputMethodManager imn = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imn.showSoftInput(et_nombre, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    //Método para que no se pueda regresar el jugador
    @Override
    public void onBackPressed(){
        //Dejo vacio para que no haga nada
    }

}
