package mx.tecnm.tepic.ladm_u4_ejercicio4_authentication_firebasetodo

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import mx.tecnm.tepic.ladm_u4_ejercicio4_authentication_firebasetodo.databinding.ActivityMain2Binding
import mx.tecnm.tepic.ladm_u4_ejercicio4_authentication_firebasetodo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(FirebaseAuth.getInstance().currentUser!=null){
            invocarOtraVentana()
        }

        binding.incribir.setOnClickListener{
            val autentication = FirebaseAuth.getInstance()
            val dialogo = ProgressDialog(this)
            dialogo.setMessage("CREANDO USUARIO")
            dialogo.setCancelable(false)
            dialogo.show()

            autentication.createUserWithEmailAndPassword(
                binding.correo.text.toString(),
            binding.contrasena.text.toString()
            ).addOnCompleteListener{
                dialogo.dismiss()
                if(it.isSuccessful){
                    Toast.makeText(this,"SE INCRIBIO CORRECTAMENTE", Toast.LENGTH_LONG)
                    binding.correo.text.clear()
                    binding.contrasena.text.clear()
                }else{
                    AlertDialog.Builder(this).setTitle("ATENCIÓN")
                        .setMessage("ERROR! NO SE PUDO CREAR USUARIO")
                        .show()
                }
            }
        }
        binding.autenticar.setOnClickListener{
            val autentication = FirebaseAuth.getInstance()
            val dialogo = ProgressDialog(this)
            dialogo.setMessage("CREANDO USUARIO")
            dialogo.setCancelable(false)
            dialogo.show()

            autentication.signInWithEmailAndPassword(
                binding.correo.text.toString(),
                binding.contrasena.text.toString()
            ).addOnCompleteListener {
                dialogo.dismiss()
                if(it.isSuccessful){
                    invocarOtraVentana()
                    return@addOnCompleteListener
                }
                AlertDialog.Builder(this)
                    .setMessage("ERROR! correo/contraseña no validos")
                    .show()
            }
        }
        binding.recuperar.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(
                binding.correo.text.toString()
            ).addOnSuccessListener {
                AlertDialog.Builder(this)
                    .setMessage("SE EIVNO CORREO A TU BANDEJA ENTRADA")
                    .show()
            }
        }
    }

    private fun invocarOtraVentana() {
        startActivity(Intent(this,MainActivity2::class.java))
        finish()
    }
}