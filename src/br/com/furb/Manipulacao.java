package br.com.furb;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Alunos:
 * 	- Bruno Henrique Freiberger
 * 	- Cleyson Gustavo Reinhold
 */
public class Manipulacao extends JFrame implements ActionListener, DocumentListener{
	/**
	 * 
	 */

	public static final String INIT_DIR_FCHOOSER = "C:\\";
	public static final String PROG_NAME = "np-bcc-furb";
	
	JTextArea textArea;
	
	//Barra de menu
	JMenuBar menuBar;
	
	//Menu
	JMenu menuArquivo;
	JMenu menuEditar;
	
	//Itens de menu
	JMenuItem miAbrir;
	JMenuItem miSalvar;
	JMenuItem miSalvarComo;
	JMenuItem miNovo;
	JMenuItem miFechar;
	
	JMenuItem miInfo;
	
	Path path = null;
	boolean modificado = false;
	String fileName = "";
	
	private JFileChooser jfc;
	
	public Manipulacao() {
		this.defineLayout();
	}
	
	public static void main(String[] args) {
		new Manipulacao();
	}
	
	public void defineLayout(){
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
		
		this.menuBar.add(this.menuArquivo);
		this.menuBar.add(this.menuEditar);
		
		//Cria item de menu
		
		//Arquivo
		this.miNovo = new JMenuItem();
		this.miAbrir = new JMenuItem();
		this.miSalvar = new JMenuItem();
		this.miSalvarComo = new JMenuItem();
		this.miFechar = new JMenuItem();
		
		//About
		this.miInfo = new JMenuItem();
		
		//Define o action de cada menu
		this.miNovo.addActionListener(this);
		this.miAbrir.addActionListener(this);
		this.miSalvar.addActionListener(this);
		this.miSalvarComo.addActionListener(this);
		this.miFechar.addActionListener(this);
		
		this.miInfo.addActionListener(this);
		
		this.textArea.getDocument().addDocumentListener(this);
		
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
		
		this.menuEditar.add(this.miInfo);
		
		WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	salvarAoSair();
            }
        };
        this.addWindowListener(exitListener);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		this.add(scrollPane, BorderLayout.CENTER);
		
		this.setJMenuBar(menuBar);
		this.revalidate();
	}
	
	/**
	 * Abrir arquivo
	 */
	public void openFile(){
		jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File(Manipulacao.INIT_DIR_FCHOOSER)); // Definir diretório inicial que o FChooser vai abrir(C:)
		int opt = jfc.showOpenDialog(null);
		Path pathTemp = null;
		if(opt == JFileChooser.APPROVE_OPTION){
			pathTemp = Paths.get(jfc.getSelectedFile().toURI());
		}
		if(modificado){
			int accept = 0;
			accept = JOptionPane.showConfirmDialog(null, "Deseja salvar antes de criar um novo arquivo?");
			if(accept == JOptionPane.YES_OPTION){
				if(path != null)
					this.salvarFile(textArea.getText());
				else
					this.salvarComoFile(textArea.getText());
			}
			if(accept != JOptionPane.CANCEL_OPTION){
				escreveTexto(pathTemp);
			}
		}
		else{
			escreveTexto(pathTemp);
		}
	}
	
	private void escreveTexto(Path pathTemp){
		this.path = pathTemp;
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
				this.modificado = false;
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
		if(path != null) //COMENTAR ESSA CONDIÇÃO PARA QUE O TÍTULO SEJA O NOME DO ARQUIVO
			this.fileName = path.toString();
		else
			this.fileName = fileName;
		defineTitle();
	}
	
	private void defineTitle(){
		String titulo = this.fileName;
		if(modificado)
			titulo += " *";
		this.setTitle(titulo + " - " + Manipulacao.PROG_NAME);
	}
	
	private void salvarAoSair(){
		if(modificado){
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
	 */
	public void novoArquivo(){
		if(modificado){
			int accept = 0;
			accept = JOptionPane.showConfirmDialog(null, "Deseja salvar antes de criar um novo arquivo?");
			if(accept == JOptionPane.YES_OPTION){
				if(path != null)
					this.salvarFile(textArea.getText());
				else
					this.salvarComoFile(textArea.getText());
			}
			if(accept != JOptionPane.CANCEL_OPTION){
				zeraTextArea();
			}
		}
		else{
			zeraTextArea();
		}
	}
	
	public void zeraTextArea(){
		this.path = null;
		this.textArea.setText(null);
		this.modificado = false;
		this.defineTitle("Sem título");
	}
	
	/**
	 * Retorna o nome do arquivo selecionado
	 */
	private String getNomeArquivo(Path path){
		return String.valueOf(path.getFileName());
	}
	
	/**
	 * Fechar arquivo após escrever
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
	 * Criar e Salvar arquivo
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
					this.modificado = false;
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
	
	private void imprimeInfo(){
		JOptionPane.showMessageDialog(null, "Alunos:\n- Bruno Henrique Freiberger\n- Cleyson Gustavo Reinhold\n\nProfessor:\n- Matheus Carvalho Viana");
	}
	
	/**
	 * Salvar file normal
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
					this.modificado = false;
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

	
	/**
	 * Eventos do DocumentEvent
	 */
	
	@Override
	public void changedUpdate(DocumentEvent de) {
		houveAlteracaoTexto();
	}

	@Override
	public void insertUpdate(DocumentEvent de) {
		houveAlteracaoTexto();
	}

	@Override
	public void removeUpdate(DocumentEvent de) {
		houveAlteracaoTexto();
	}

	public void houveAlteracaoTexto(){
		this.modificado = true;
		defineTitle();
	}

	
}