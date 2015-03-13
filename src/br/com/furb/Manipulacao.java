package br.com.furb;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * Alunos:
 * 	- Bruno Henrique Freiberger
 * 	- Cleyson Gustavo Reinhold
 */
public class Manipulacao extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5783665847537775600L;

	public static final String INIT_DIR_FCHOOSER = "C:\\";
	public static final String PROG_NAME = "np-bcc-furb";
	
	JTextArea textArea;
	
	//Barra de menu
	JMenuBar menuBar;
	
	//Menu
	JMenu menuArquivo;
	JMenu menuEditar;
	JMenu menuCredits;
	
	//Itens de menu
	JMenuItem miAbrir;
	JMenuItem miSalvar;
	JMenuItem miSalvarComo;
	JMenuItem miNovo;
	JMenuItem miFechar;
	
	JMenuItem miInfo;
	
	Path path = null;

	private JFileChooser jfc;
	
	public Manipulacao() {
		this.defineLayout();
	}
	
	public static void main(String[] args) {
		new Manipulacao();
	}
	
	public void defineLayout(){
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.setLayout(new BorderLayout());
		this.setSize(800,600);
		this.setVisible(true);
		this.defineTitle("Sem título");
		
		this.textArea = new JTextArea();
		this.menuBar = new JMenuBar();
		
		this.menuArquivo = new JMenu();		
		this.menuArquivo.setText("File");
		this.menuArquivo.setMnemonic(KeyEvent.VK_F);
		
		this.menuEditar = new JMenu();
		this.menuEditar.setText("Editar");
		this.menuEditar.setMnemonic(KeyEvent.VK_F);
		
		this.menuCredits = new JMenu();
		this.menuCredits.setText("Sobre");
		this.menuCredits.setMnemonic(KeyEvent.VK_F);
		
		this.menuBar.add(this.menuArquivo);
		this.menuBar.add(this.menuEditar);
		this.menuBar.add(this.menuCredits);
		
		//Cria item de menu
		
		//Arquivo
		this.miNovo = new JMenuItem();
		this.miAbrir = new JMenuItem();
		this.miSalvar = new JMenuItem();
		this.miSalvarComo = new JMenuItem();
		this.miFechar = new JMenuItem();
		
		//Editar
		
		//About
		this.miInfo = new JMenuItem();
		
		//Define o action de cada menu
		this.miNovo.addActionListener(this);
		this.miAbrir.addActionListener(this);
		this.miSalvar.addActionListener(this);
		this.miSalvarComo.addActionListener(this);
		this.miFechar.addActionListener(this);
		
		this.miInfo.addActionListener(this);
		
		//Atribui texto dos itens de menu
		this.miNovo.setText("Novo");
		this.miAbrir.setText("Abrir...");
		this.miSalvar.setText("Salvar");
		this.miSalvarComo.setText("Salvar como...");
		this.miFechar.setText("Fechar");
		
		this.miInfo.setText("Créditos");
		
		//Adiciona itens de menu ao menu
		this.menuArquivo.add(this.miNovo);
		this.menuArquivo.add(this.miAbrir);
		this.menuArquivo.add(this.miSalvar);
		this.menuArquivo.add(this.miSalvarComo);
		this.menuArquivo.add(this.miFechar);
		
		this.menuCredits.add(this.miInfo);
		
		this.add(textArea, BorderLayout.CENTER);
		
		this.setJMenuBar(menuBar);
		this.revalidate();
	}
	
	/**
	 * Abrir arquivo
	 * @created 11/03/2015 - 17:55:15
	 * @return
	 */
	public void openFile(){
		jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File(Manipulacao.INIT_DIR_FCHOOSER)); // Definir diretório inicial que o FChooser vai abrir(C:)
		int opt = jfc.showOpenDialog(null);
		if(opt == JFileChooser.APPROVE_OPTION){
			this.path = Paths.get(jfc.getSelectedFile().toURI());
		}
		String line = null;
		try {
			File file = jfc.getSelectedFile();
			if(file != null){
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				this.textArea.setText(null);
				while((line = br.readLine()) != null){
					if(textArea.getText().isEmpty()){
						this.textArea.setText(line);
					} else {
						this.textArea.append("\n"+line);
					}
				}
				this.defineTitle(this.getNomeArquivo(file.toPath()));
				br.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void defineTitle(String fileName){
		this.setTitle(fileName + " - " + Manipulacao.PROG_NAME);
	}
	
	private void salvarAoSair(){
		if(!textArea.getText().isEmpty()){
			int accept = 0;
			accept = JOptionPane.showConfirmDialog(null, "Deseja salvar antes de sair?");
			if(accept == JOptionPane.YES_OPTION){
				this.salvarComoFile(textArea.getText());
				this.dispose();
			} else if(accept == JOptionPane.NO_OPTION) {
				this.dispose();
			}
		} else {
			this.dispose();
		}
	}

	/**
	 * Verifica se o arquivo já existe
	 * @param f
	 * @param content
	 * @return
	 */
	public boolean arquivoNExistente(File f, String content){
		int accept = 0;
		if(!Files.exists(f.toPath())){
			return true;
		} else {
			accept = JOptionPane.showConfirmDialog(null, this.getNomeArquivo(f.toPath()) + " já existe.\nDeseja substituí-lo?");
			if(accept == JOptionPane.YES_OPTION){
				this.excluirArquivo(f.toPath());
				this.path = f.toPath();
				this.salvarFile(content);
			}
			return false;
		}
	}
	
	public void excluirArquivo(Path p){
		try{
			if(p != null){
				Files.deleteIfExists(p);
			}
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	/**
	 * Novo arquivo
	 * @created 12/03/2015 - 18:13:21
	 */
	public void novoArquivo(){
		this.path = null;
		this.textArea.setText(null);
		this.defineTitle("Sem título");
	}
	
	/**
	 * Retorna o nome do arquivo selecionado
	 * @created 12/03/2015 - 17:42:21
	 * @param path
	 */
	private String getNomeArquivo(Path path){
		return String.valueOf(path.getFileName());
	}
	
	/**
	 * Fechar arquivo após escrever
	 * @created 11/03/2015 - 18:00:47
	 * @param f
	 */
	private void closeF(BufferedWriter bw){
		try {
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * adicionar extensões para salvar
	 * @created 11/03/2015 - 15:20:51
	 */
	@SuppressWarnings("unused")
	private void defineExt(){
		//TODO adicionar opções de extensão pra arquivo
	}
	
	/**
	 * Criar e Salvar arquivo
	 * @created 11/03/2015 - 17:44:21
	 * @param content
	 */
	public void salvarComoFile(String content){
		this.jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File(Manipulacao.INIT_DIR_FCHOOSER));
		this.jfc.showSaveDialog(null);
		try {
			File file = jfc.getSelectedFile();
			if(file != null){
				if(this.arquivoNExistente(file, content)){
					FileWriter fw = new FileWriter(file);
					BufferedWriter bw = new BufferedWriter(fw); // "Carregam" os dados, torna o processo mais rápido.
					if(content != null && !"".equals(content)){
						bw.write(content);
					}
					this.defineTitle(this.getNomeArquivo(file.toPath()));
					this.path = file.toPath();
					this.closeF(bw);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @created 11/03/2015 22:45:22
	 */
	private void imprimeInfo(){
		JOptionPane.showMessageDialog(null, "Alunos:\n- Bruno Henrique Freiberger\n- Cleyson Gustavo Reinhold\n\nProfessor:\n- Matheus Carvalho Viana");
	}
	
	/**
	 * Salvar file normal
	 * @param content
	 */
	public void salvarFile(String content){
		try {
			if(this.path != null){
				File file = path.toFile();
				if(file != null){
					FileWriter fw = new FileWriter(file);
					BufferedWriter bw = new BufferedWriter(fw); // "Carregam" os dados, torna o processamento mais rápido.
					if(content != null && !"".equals(content)){
						bw.write(content);
					}
					JOptionPane.showMessageDialog(null, "Arquivo alterado com sucesso!");
					this.defineTitle(this.getNomeArquivo(file.toPath()));
					this.closeF(bw);
				}
			} else {
				this.salvarComoFile(content);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == miAbrir) {
			this.openFile();
		}
		if(ae.getSource() == miSalvar) {
			this.salvarFile(textArea.getText());
		}
		if(ae.getSource() == miSalvarComo) {
			this.salvarComoFile(textArea.getText());
		}
		if(ae.getSource() == miNovo){
			this.novoArquivo();
		}
		if(ae.getSource() == miFechar){
			this.salvarAoSair();
		}
		if(ae.getSource() == miInfo){
			this.imprimeInfo();
		}
		
	}

}


