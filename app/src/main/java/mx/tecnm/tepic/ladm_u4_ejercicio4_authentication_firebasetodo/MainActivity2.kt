package mx.tecnm.tepic.ladm_u4_ejercicio4_authentication_firebasetodo

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import mx.tecnm.tepic.ladm_u4_ejercicio4_authentication_firebasetodo.databinding.ActivityMain2Binding
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MainActivity2 : AppCompatActivity() {
    lateinit var binding : ActivityMain2Binding
    lateinit var imagen : Uri
    var listaNombres = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        cargarLista()
        binding.insertar.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()

            val datos = hashMapOf(
                "descripcion" to binding.descripcion.text.toString(),
                "direccion" to binding.direccion.text.toString()
            )
            baseRemota.collection("almacen")
                .add(datos)
                .addOnSuccessListener {
                    binding.descripcion.text.clear()
                    binding.direccion.text.clear()
                    Toast.makeText(this,"EXITO SE INSERTO",Toast.LENGTH_LONG)
                }
                .addOnFailureListener{
                    AlertDialog.Builder(this).setMessage(it.message).show()
                }
        }
        binding.elegir.setOnClickListener {
            val galeria = Intent(Intent.ACTION_GET_CONTENT)

            galeria.type = "image/*"
            startActivityForResult(galeria,202)
        }
        binding.subir.setOnClickListener {
            var nombreArchivo = ""
            val cal = GregorianCalendar.getInstance()
            val dialogo = ProgressDialog(this)

            dialogo.setMessage("SUBIENDO ARCHIVO...")
            dialogo.setCancelable(false)
            dialogo.show()

            nombreArchivo = cal.get(Calendar.YEAR).toString()+
                    cal.get(Calendar.MONTH).toString()+
                    cal.get(Calendar.DAY_OF_MONTH).toString()+
                    cal.get(Calendar.HOUR).toString()+
                    cal.get(Calendar.MINUTE).toString()+
                    cal.get(Calendar.SECOND).toString()+
                    cal.get(Calendar.MILLISECOND).toString()
            val storageRef = FirebaseStorage.getInstance()
                .reference.child("imagenes/${nombreArchivo}")

            storageRef.putFile(imagen)
                .addOnSuccessListener {
                    Toast.makeText(this,"EXITO! SE SUBIO",Toast.LENGTH_LONG)
                        .show()
                    binding.imagen.setImageBitmap(null)
                    dialogo.dismiss()
                    cargarLista()
                }
                .addOnFailureListener{
                    Toast.makeText(this,"ERROR",Toast.LENGTH_LONG)
                        .show()
                    dialogo.dismiss()
                }
        }
    }

    private fun cargarLista(){
        val storageRef = FirebaseStorage.getInstance().reference.child("imagenes")

        storageRef.listAll()
            .addOnSuccessListener {
                listaNombres.clear()
                it.items.forEach{
                    listaNombres.add(it.name)
                }

                binding.lista.adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,listaNombres)

                binding.lista.setOnItemClickListener { adapterView, view, i, l ->
                    cargarImagenRemota(listaNombres.get(i))
                }
            }
            .addOnFailureListener{

            }
    }

    private fun cargarImagenRemota(nombreArchivoRemoto: String){
        val storageRef = FirebaseStorage.getInstance()
            .reference.child("imagenes/${nombreArchivoRemoto}")
        val archivoTemporal = File.createTempFile("imagenTemp","jpg")

        storageRef.getFile(archivoTemporal)
            .addOnSuccessListener {
                val mapadeBits = BitmapFactory.decodeFile(archivoTemporal.absolutePath)
                binding.imagen.setImageBitmap(mapadeBits)
            }
            .addOnFailureListener {
                AlertDialog.Builder(this).setMessage(it.message).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==202){
            imagen = data!!.data!!
            binding.imagen.setImageURI(imagen)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuoculto,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.salir->{

            }
            R.id.acerca->{

            }
            R.id.sesion->{
                FirebaseAuth.getInstance().signOut() //Cierra sesion
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
        }
        return true
    }
}