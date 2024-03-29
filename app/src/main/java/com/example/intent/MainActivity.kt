package com.example.intent

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_CHOOSER
import android.content.Intent.ACTION_DIAL
import android.content.Intent.ACTION_PICK
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_INTENT
import android.content.Intent.EXTRA_TITLE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.intent.Constantes.PARAMETRO_EXTRA
import com.example.intent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var parl: ActivityResultLauncher<Intent> // parametro activity result launcher (parl)
    private lateinit var permissaoChamada: ActivityResultLauncher<String>
    private lateinit var pegarImagemArl : ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        amb.mainTb.apply {
            title = getString(R.string.app_name)
            subtitle = this@MainActivity.javaClass.simpleName
            setSupportActionBar(this)
        }

        amb.entrarParametroBt.setOnClickListener {
            // INTENT EXPLÍCITA
            Intent(this, ParametroActivity::class.java).also{
                it.putExtra(PARAMETRO_EXTRA, amb.parametroTv.text.toString()) // chave e valor -> envia o valor do text view pro campo de input na outra tela
                parl.launch(it)
            }
        }

        parl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object: ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    if(result.resultCode == RESULT_OK){
                        result.data?.getStringExtra(PARAMETRO_EXTRA)?.let{
                            amb.parametroTv.text = it
                        }
                    }
                }
            })

        //TEM UM DESSE PRA CADA TELA
//        parl = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()) // contrato
//        { result -> // lambda
//            if (result.resultCode == RESULT_OK) {
//                result.data?.getStringExtra(PARAMETRO_EXTRA)?.let {
//                    amb.parametroTv.text = it
//                }
//            }
//        }

        permissaoChamada = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            permissaoConcedida ->
            if(permissaoConcedida){
                // realizar chamada
                chamarNumero(chamar = true)

            }else{
                Toast.makeText(this,"Permissao necessaria para continuar", Toast.LENGTH_LONG).show()
            }
        }

        pegarImagemArl= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            resultado ->
            if(resultado.resultCode == RESULT_OK){
                resultado.data?.data?.let { imagemUri ->amb.parametroTv.text = imagemUri.toString()
                Intent(ACTION_VIEW, imagemUri).also { startActivity(it) }}
            }
        }


    }
    override fun onCreateOptionsMenu(menu: Menu?):Boolean{
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        return when(item.itemId){
            R.id.viewMi -> {
                val url: Uri = Uri.parse(amb.parametroTv.text.toString())
                val navegadorIntent: Intent = Intent(ACTION_VIEW, url)
                startActivity(navegadorIntent)
                true }
            R.id.callMi -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                        chamarNumero(chamar = true)
                    } else {
                        permissaoChamada.launch(CALL_PHONE)
                    }
                }
                else{
                    chamarNumero(chamar = true)
                }
                true}
            R.id.dialMi -> {
                chamarNumero(chamar = false)
                true}
            R.id.pickMi -> {
                val pegarImagemIntent = Intent(ACTION_PICK)
                val diretorioImagens = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                pegarImagemIntent.setDataAndType(Uri.parse(diretorioImagens),"image/*")
                pegarImagemArl.launch(pegarImagemIntent)
                true}
            R.id.chooserMi -> {
                Uri.parse(amb.parametroTv.text.toString()).let{uri ->
                    Intent(ACTION_VIEW, uri).also {navegadorIntent ->
                        val escolherAppIntent = Intent(ACTION_CHOOSER)
                        escolherAppIntent.putExtra(EXTRA_TITLE, "Escolha seu navegador favorito")
                        escolherAppIntent.putExtra(EXTRA_INTENT, navegadorIntent)
                        startActivity(escolherAppIntent)
                    }
                }
                true
            }
            else -> {false}
        }
    }

    private fun chamarNumero(chamar:Boolean){
        val numeroUri:Uri = Uri.parse("tel:${amb.parametroTv.text}")
        val chamarIntent:Intent = Intent(if(chamar) ACTION_CALL else ACTION_DIAL)
        chamarIntent.data = numeroUri
        startActivity(chamarIntent)
    }
}
