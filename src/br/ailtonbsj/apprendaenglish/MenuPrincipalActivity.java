package br.ailtonbsj.apprendaenglish;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import br.ailtonbsj.apprendaenglish.R;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MenuPrincipalActivity extends Activity {
	ArrayList<Integer> listaOutput;
	ListView menuPrincipal;
	String opcoes[] = { "Inglês para Português", "Português para Inglês",
			"Ouvir e Digitar", "Ouvir e Traduzir", "Aleatorio" };
	Boolean jogoRandom = false;
	Random randomico = new Random();
	String escolha;
	TextToSpeech falador;
	Boolean faladorAtivo = false;
	FlowLayout areaDeBotoes;
	TextView textoUser;
	TextView textoApp;
	Button ouvirBt;
	Button nextBt;
	Vibrator vibrator;
	int botoesAmais = 10;

	ArrayList<ArrayList<String>> listFrases = new ArrayList<ArrayList<String>>(); // ok
	ArrayList<String> listButtonsEn = new ArrayList<String>(); // ok
	ArrayList<String> listButtonsPt = new ArrayList<String>(); // ok

	ArrayList<Integer> sequenciaDeFrases = new ArrayList<Integer>(); // ok
	ArrayList<String> seguenciaDeBotoes = new ArrayList<String>();
	int mundo = 1;
	int posicaoDaLista = -1;
	
	int posicaoFala = 0;

	ArrayList<String> phrase;
	String phrase_pt[];
	String phrase_en[];
	int ponteiroUser = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		// proximoMenu = (MenuItem) findViewById(R.id.proxim1);
		initFalador();
		lerArquivo();
		geraListas();
		carregaMenuPrincipal();
	}

	public void initFalador() {
		falador = new TextToSpeech(this, new OnInitListener() {
			@Override
			public void onInit(int status) {
				// TODO Auto-generated method stub
				if (status == TextToSpeech.SUCCESS) {
					int result = falador.setLanguage(Locale.US);
					if (result == TextToSpeech.LANG_MISSING_DATA) {
						Log.e("TextToSpeechDemo", "Language is not available.");
						alert("TTS não está instalado!!!");
					}
					else if(result == TextToSpeech.LANG_NOT_SUPPORTED){
						alert("TTS não está instalado!!!");
					} else {
						faladorAtivo = true;
					}
				} else {
					Log.e("TextToSpeechDemo",
							"Could not initialize TextToSpeech.");
					alert("Erro no TTS!!!");
				}
			}
		});
	}

	public void falar(String texto) {
		double[] speeds = { 0.3, 0.7, 0.8, 0.3, 0.7, 0.8 };
		if (posicaoFala<3) {
			falador.setLanguage(Locale.US);
		} else {
			falador.setLanguage(Locale.UK);
		}
		if(posicaoFala<5){
			posicaoFala++;
		}
		else{
			posicaoFala = 0;
		}
		falador.setSpeechRate((float) speeds[posicaoFala]);

		falador.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
	}

	public void carregaMenuPrincipal() {
		setContentView(R.layout.activity_menu_principal);
		setTitle("Apprenda English");
		ArrayAdapter<String> arrayAdapter1;
		arrayAdapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, opcoes);
		menuPrincipal = (ListView) findViewById(R.id.menuPrincipal1);
		menuPrincipal.setAdapter(arrayAdapter1);
		OnItemClickListener evt1 = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String opcao = ((TextView) arg1).getText().toString();
				escolha = opcao;
				if (escolha.equals("Aleatorio")) {
					jogoRandom = true;
					escolha = "Inglês para Português";
				} else {
					jogoRandom = false;
				}
				carregaBaseAplicacao();
			}
		};
		menuPrincipal.setOnItemClickListener(evt1);
	}

	public void carregaBaseAplicacao() {
		escreverSave();
		if (jogoRandom) {
			int valor = randomico.nextInt(4);
			switch (valor) {
			case 0:
				escolha = "Inglês para Português";
				break;
			case 1:
				escolha = "Português para Inglês";
				break;
			case 2:
				escolha = "Ouvir e Digitar";
				break;
			case 3:
				escolha = "Ouvir e Traduzir";
			}
		}
		setContentView(R.layout.base_aplicacao);
		setTitle("Nível: " + String.valueOf(mundo) +" ("+ String.valueOf(posicaoDaLista + 2) + "/"
				+ String.valueOf(listFrases.size()) + ") | " + escolha);

		areaDeBotoes = (FlowLayout) findViewById(R.id.area_botoes1);
		textoUser = (TextView) findViewById(R.id.textoUsuario);
		textoApp = (TextView) findViewById(R.id.textoAppli);
		ouvirBt = (Button) findViewById(R.id.btOuvir);
		nextBt = (Button) findViewById(R.id.next1);
		textoUser.setTextColor(Color.WHITE);
		textoApp.setTextColor(Color.WHITE);

		ouvirBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				falar(textoApp.getText().toString());
			}
		});

		nextBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				carregaBaseAplicacao();
			}
		});

		geraBase();

	}

	public void carregaBotoes(ArrayList<String> lst, String tp) {
		int cont = 0;
		for (String txtBt : lst) {
			Button a = new Button(this);
			a.setText(txtBt);
			a.setTextColor(Color.WHITE);
			a.setId(50 + cont);
			a.setPadding(9, 9, 9, 9);
			cont++;
			if (tp.equals("en")) {
				a.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Button a = (Button) findViewById(arg0.getId());
						if (a.getText().toString()
								.equals(phrase_en[ponteiroUser])) {
							falar(a.getText().toString());
							atualizaTextoUser(a);
							if (ponteiroUser == phrase_en.length - 1) {
								nextBt.setVisibility(Button.VISIBLE);
								textoApp.setText(phrase.get(0));
								textoUser.setText(phrase.get(1));
								textoApp.setVisibility(TextView.VISIBLE);
								textoUser.setVisibility(TextView.VISIBLE);
								ouvirBt.setVisibility(Button.VISIBLE);
								areaDeBotoes.removeAllViews();
							} else {
								ponteiroUser++;
							}
						} else {
							vibrator.vibrate(500);
						}
					}
				});
			} else {
				a.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Button a = (Button) findViewById(arg0.getId());
						if (a.getText().toString()
								.equals(phrase_pt[ponteiroUser])) {
							atualizaTextoUser(a);
							if (ponteiroUser == phrase_pt.length - 1) {
								nextBt.setVisibility(Button.VISIBLE);
								textoApp.setText(phrase.get(0));
								textoUser.setText(phrase.get(1));
								textoApp.setVisibility(TextView.VISIBLE);
								textoUser.setVisibility(TextView.VISIBLE);
								ouvirBt.setVisibility(Button.VISIBLE);
								areaDeBotoes.removeAllViews();
							} else {
								ponteiroUser++;
							}
						} else {
							vibrator.vibrate(500);
						}
					}
				});
			}
			areaDeBotoes.addView(a);
		}
	}

	public void atualizaTextoUser(Button a) {
		textoUser.append(a.getText().toString() + " ");
		a.setVisibility(Button.GONE);
	}

	public void lerArquivo() {
		AssetManager assertManager = getResources().getAssets();
		InputStream inputStream;
		try {
			listFrases = new ArrayList<ArrayList<String>>();
			inputStream = assertManager.open("apprenda_.dic");
			InputStreamReader isr = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(isr);
			String lstrlinha;
			lerSave();
			boolean captura = false;
			while ((lstrlinha = br.readLine()) != null) {
				if (lstrlinha.equals("@")) {
					String mund = br.readLine();
					int phase = Integer.valueOf(mund);
					if(phase == this.mundo){
						captura = true;
					}
					else if(phase>this.mundo){
						break;
					}
				}
				if (lstrlinha.equals("#") && captura) {
					ArrayList<String> al = new ArrayList<String>();
					lstrlinha = br.readLine();
					al.add(lstrlinha);
					lstrlinha = br.readLine();
					al.add(lstrlinha);
					listFrases.add(al);
				}
			}
			inputStream.close();
		} catch (Exception e) {
			//alert("Error :" + e.getMessage());
		}

	}

	public void escreverSave() {
		File file = new File(Environment.getExternalStorageDirectory(),
				"save.dat");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				alert("Não foi possivel criar save");
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write((String.valueOf(mundo)+"\n").getBytes());
			fos.write((String.valueOf(posicaoDaLista)+"\n").getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			alert("Falha de escrita em save");
		}

	}

	public void lerSave() {
		File file = new File(Environment.getExternalStorageDirectory(),
				"save.dat");
		if (!file.exists()) {
			escreverSave();
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String mundo = br.readLine();
			String level = br.readLine();
			if(mundo==null || level==null){
				escreverSave();
				mundo="01";
				level="01";
			}
			this.mundo = Integer.valueOf(mundo);
			this.posicaoDaLista = Integer.valueOf(level);
			br.close();
		} catch (IOException e) {
			alert("Erro ao ler arquivo");
		}
		
	}

	public void alert(String arg0) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				MenuPrincipalActivity.this);
		dialog.setTitle("Alerta");
		dialog.setMessage(arg0);
		dialog.setNeutralButton("OK", null);
		dialog.show();
	}

	public void geraBase() {
		if(posicaoDaLista<listFrases.size()-1){
			posicaoDaLista++;	
		}
		else{
			this.mundo++;
			this.posicaoDaLista = -1;
			escreverSave();
			lerArquivo();
			geraListas();
			carregaBaseAplicacao();
			return;
		}
		phrase = listFrases.get(sequenciaDeFrases.get(posicaoDaLista));

		phrase_en = phrase.get(0).split(" ");
		phrase_pt = phrase.get(1).split(" ");
		int total_en = phrase_en.length;
		int total_pt = phrase_pt.length;
		int total_palavras = 0;
		if (total_en > total_pt) {
			total_palavras = total_en;
		} else {
			total_palavras = total_pt;
		}

		seguenciaDeBotoes = new ArrayList<String>();
		ponteiroUser = 0;

		for (int i = 0; i < total_palavras + botoesAmais; i++) {

			if (escolha == "Inglês para Português") {
				seguenciaDeBotoes.add(listButtonsPt.get(randomico
						.nextInt(listButtonsPt.size())));
			} else if (escolha == "Português para Inglês") {
				seguenciaDeBotoes.add(listButtonsEn.get(randomico
						.nextInt(listButtonsEn.size())));
			} else if (escolha == "Ouvir e Digitar") {
				seguenciaDeBotoes.add(listButtonsEn.get(randomico
						.nextInt(listButtonsEn.size())));
			} else if (escolha == "Ouvir e Traduzir") {
				seguenciaDeBotoes.add(listButtonsPt.get(randomico
						.nextInt(listButtonsPt.size())));
			}

		}

		if (escolha == "Inglês para Português") {
			this.falar(phrase.get(0));
			textoApp.setText(phrase.get(0));
			gerarListaAleatoria(total_palavras + botoesAmais);
			int cont = 0;
			for (String s : phrase_pt) {
				seguenciaDeBotoes.set(listaOutput.get(cont), s);
				cont++;
			}
			carregaBotoes(seguenciaDeBotoes, "pt");
		} else if (escolha == "Português para Inglês") {
			textoApp.setText(phrase.get(1));
			gerarListaAleatoria(total_palavras + botoesAmais);
			int cont = 0;
			for (String s : phrase_en) {
				seguenciaDeBotoes.set(listaOutput.get(cont), s);
				cont++;
			}
			carregaBotoes(seguenciaDeBotoes, "en");
			ouvirBt.setVisibility(Button.GONE);
		} else if (escolha == "Ouvir e Digitar") {
			this.falar(phrase.get(0));
			textoApp.setText(phrase.get(0));
			gerarListaAleatoria(total_palavras + botoesAmais);
			int cont = 0;
			for (String s : phrase_en) {
				seguenciaDeBotoes.set(listaOutput.get(cont), s);
				cont++;
			}
			textoApp.setVisibility(TextView.GONE);
			carregaBotoes(seguenciaDeBotoes, "en");
		} else if (escolha == "Ouvir e Traduzir") {
			this.falar(phrase.get(0));
			textoApp.setText(phrase.get(0));
			gerarListaAleatoria(total_palavras + botoesAmais);
			int cont = 0;
			for (String s : phrase_pt) {
				seguenciaDeBotoes.set(listaOutput.get(cont), s);
				cont++;
			}
			carregaBotoes(seguenciaDeBotoes, "pt");
			textoApp.setVisibility(TextView.GONE);
		}
	}

	public void geraListas() {
		for (int i = 0; i < listFrases.size(); i++) {
			String l[] = listFrases.get(i).get(0).split(" ");
			String ls[] = listFrases.get(i).get(1).split(" ");
			for (String s : l) {
				listButtonsEn.add(s);
			}
			for (String s : ls) {
				listButtonsPt.add(s);
			}
		}

		//gerarListaAleatoria(listFrases.size());
		sequenciaDeFrases = new ArrayList<Integer>();
		for(int i=0;i<listFrases.size();i++){
			sequenciaDeFrases.add(i);
		}
		//sequenciaDeFrases = new ArrayList<Integer>(listaOutput);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_principal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.voltar1:
			posicaoDaLista--;
			carregaMenuPrincipal();
			break;
		case R.id.sair1:
			alert("Desenvolvido por:\nJosé Ailton B. da Silva\nemail: ailton.ifce@gmail.com");
			break;
		case R.id.proxim1:
			carregaBaseAplicacao();
			break;
		case R.id.reset1:
			this.mundo = 1;
			this.posicaoDaLista = -1;
			escreverSave();
			lerArquivo();
			geraListas();
			carregaBaseAplicacao();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void gerarListaAleatoria(int quant) {
		int quantidade = quant;
		int rand;
		ArrayList<Integer> bolas = new ArrayList<Integer>();
		for (int x = 0; x < quantidade; x++) {
			bolas.add(x);
		}
		listaOutput = new ArrayList<Integer>();
		while (quantidade > 0) {
			rand = randomico.nextInt(quantidade);
			listaOutput.add(bolas.get(rand));
			bolas.remove(rand);
			quantidade--;
		}
	}

}
