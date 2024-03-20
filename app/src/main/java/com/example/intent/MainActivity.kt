package com.example.intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.intent.Constantes.PARAMETRO_EXTRA
import com.example.intent.Constantes.PARAMETRO_REQUEST_CODE
import com.example.intent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        amb.mainTb.apply {
            title = getString(R.string.app_name)
            subtitle = this@MainActivity.javaClass.simpleName
            setSupportActionBar(this)
        }

        amb.entrarParametroBt.setOnClickListener {
            Intent(this, ParametroActivity::class.java).also{
                it.putExtra(PARAMETRO_EXTRA, amb.parametroTv.text.toString()) // chave e valor -> envia o valor do text view pro campo de input na outra tela
                startActivityForResult(it, PARAMETRO_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PARAMETRO_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                if(data != null){
                    val parametro = data.getStringExtra(PARAMETRO_EXTRA)
                    amb.parametroTv.text = parametro
                }
            }
        }
    }
}